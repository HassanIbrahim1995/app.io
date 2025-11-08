# Deployment Guide

This guide provides step-by-step instructions for deploying the Hotel Management System.

## Table of Contents

1. [Local Development](#local-development)
2. [Docker Deployment](#docker-deployment)
3. [Kubernetes Deployment](#kubernetes-deployment)
4. [Production Considerations](#production-considerations)
5. [Monitoring Setup](#monitoring-setup)
6. [Backup & Recovery](#backup-recovery)
7. [Troubleshooting](#troubleshooting)

## Local Development

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 16
- Redis 7
- Maven 3.9+

### Backend Setup

```bash
cd backend

# Create database
createdb hotel_management

# Update application-dev.yml with your database credentials

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create .env file
echo "REACT_APP_API_URL=http://localhost:8080/api/v1" > .env

# Start development server
npm start
```

## Docker Deployment

### Single Machine Deployment

```bash
# Build images
docker-compose build

# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Remove volumes (careful!)
docker-compose down -v
```

### Docker Swarm Deployment

```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml hotel

# Check services
docker service ls

# Remove stack
docker stack rm hotel
```

## Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (v1.24+)
- kubectl configured
- NGINX Ingress Controller
- Cert-Manager (optional, for TLS)

### Step 1: Create Namespace

```bash
kubectl create namespace hotel-management
```

### Step 2: Configure Secrets

```bash
# Create secrets
kubectl create secret generic hotel-secrets \
  --from-literal=database-name=hotel_management \
  --from-literal=database-user=hoteluser \
  --from-literal=database-password=CHANGE_ME \
  --from-literal=jwt-secret=CHANGE_ME_TO_LONG_RANDOM_STRING \
  --from-literal=stripe-api-key=CHANGE_ME \
  -n hotel-management

# Verify
kubectl get secrets -n hotel-management
```

### Step 3: Deploy Application

#### Development Environment

```bash
# Apply development configuration
kubectl apply -k k8s/overlays/dev

# Wait for deployments
kubectl wait --for=condition=available --timeout=300s \
  deployment --all -n hotel-management-dev

# Check status
kubectl get pods -n hotel-management-dev
kubectl get svc -n hotel-management-dev

# Port forward for local access
kubectl port-forward -n hotel-management-dev \
  svc/dev-hotel-backend 8080:8080
kubectl port-forward -n hotel-management-dev \
  svc/dev-hotel-frontend 3000:80
```

#### Production Environment

```bash
# Build and push images
docker build -t your-registry/hotel-backend:v1.0.0 ./backend
docker push your-registry/hotel-backend:v1.0.0

docker build -t your-registry/hotel-frontend:v1.0.0 ./frontend
docker push your-registry/hotel-frontend:v1.0.0

# Update image tags in k8s/overlays/prod/kustomization.yaml

# Apply production configuration
kubectl apply -k k8s/overlays/prod

# Monitor rollout
kubectl rollout status deployment/hotel-backend -n hotel-management
kubectl rollout status deployment/hotel-frontend -n hotel-management

# Check health
kubectl get pods -n hotel-management
kubectl get ingress -n hotel-management
```

### Step 4: Configure Ingress

```bash
# Install NGINX Ingress Controller (if not installed)
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml

# Install Cert-Manager for TLS (optional)
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Create ClusterIssuer for Let's Encrypt
cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: your-email@example.com
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
    - http01:
        ingress:
          class: nginx
EOF

# Update DNS records to point to Ingress IP
kubectl get ingress -n hotel-management
```

## Production Considerations

### 1. Security Hardening

```bash
# Create network policies
kubectl apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: backend-network-policy
  namespace: hotel-management
spec:
  podSelector:
    matchLabels:
      app: hotel-backend
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: hotel-frontend
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: postgres
    ports:
    - protocol: TCP
      port: 5432
EOF

# Enable Pod Security Standards
kubectl label namespace hotel-management \
  pod-security.kubernetes.io/enforce=restricted \
  pod-security.kubernetes.io/audit=restricted \
  pod-security.kubernetes.io/warn=restricted
```

### 2. Resource Limits

Ensure all pods have resource requests and limits defined:

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

### 3. Database Backup

```bash
# Create CronJob for daily backups
cat <<EOF | kubectl apply -f -
apiVersion: batch/v1
kind: CronJob
metadata:
  name: postgres-backup
  namespace: hotel-management
spec:
  schedule: "0 2 * * *"
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: backup
            image: postgres:16-alpine
            command:
            - /bin/sh
            - -c
            - |
              pg_dump -h postgres -U postgres hotel_management | \
              gzip > /backup/hotel_backup_\$(date +%Y%m%d_%H%M%S).sql.gz
            env:
            - name: PGPASSWORD
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: database-password
            volumeMounts:
            - name: backup
              mountPath: /backup
          volumes:
          - name: backup
            persistentVolumeClaim:
              claimName: backup-pvc
          restartPolicy: OnFailure
EOF
```

### 4. Monitoring Setup

```bash
# Install Prometheus Operator
kubectl create namespace monitoring
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/kube-prometheus-stack \
  -n monitoring

# Create ServiceMonitor for backend
cat <<EOF | kubectl apply -f -
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: hotel-backend
  namespace: hotel-management
spec:
  selector:
    matchLabels:
      app: hotel-backend
  endpoints:
  - port: http
    path: /actuator/prometheus
EOF
```

### 5. Logging Setup

```bash
# Install ELK Stack
kubectl create namespace logging

# Install Elasticsearch
helm repo add elastic https://helm.elastic.co
helm install elasticsearch elastic/elasticsearch \
  -n logging \
  --set replicas=3

# Install Kibana
helm install kibana elastic/kibana -n logging

# Install Filebeat
helm install filebeat elastic/filebeat -n logging
```

## Backup & Recovery

### Database Backup

```bash
# Manual backup
kubectl exec -it deployment/postgres -n hotel-management -- \
  pg_dump -U postgres hotel_management > backup.sql

# Restore
kubectl exec -i deployment/postgres -n hotel-management -- \
  psql -U postgres hotel_management < backup.sql
```

### Persistent Volume Backup

```bash
# Create VolumeSnapshot (if supported by storage class)
cat <<EOF | kubectl apply -f -
apiVersion: snapshot.storage.k8s.io/v1
kind: VolumeSnapshot
metadata:
  name: postgres-snapshot
  namespace: hotel-management
spec:
  volumeSnapshotClassName: csi-snapclass
  source:
    persistentVolumeClaimName: postgres-pvc
EOF
```

## Monitoring Setup

### Health Checks

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend health
curl http://localhost:3000/health
```

### Metrics

```bash
# Backend metrics
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

## Troubleshooting

### Pod Not Starting

```bash
# Check pod status
kubectl describe pod <pod-name> -n hotel-management

# Check logs
kubectl logs <pod-name> -n hotel-management

# Check events
kubectl get events -n hotel-management --sort-by='.lastTimestamp'
```

### Database Connection Issues

```bash
# Test database connection
kubectl exec -it deployment/hotel-backend -n hotel-management -- \
  nc -zv postgres 5432

# Check database logs
kubectl logs deployment/postgres -n hotel-management
```

### Image Pull Errors

```bash
# Check image pull secret
kubectl get secrets -n hotel-management

# Manually pull image
docker pull your-registry/hotel-backend:v1.0.0

# Check pod events
kubectl describe pod <pod-name> -n hotel-management
```

### Ingress Not Working

```bash
# Check ingress controller
kubectl get pods -n ingress-nginx

# Check ingress resource
kubectl describe ingress hotel-ingress -n hotel-management

# Check ingress controller logs
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller
```

### Performance Issues

```bash
# Check resource usage
kubectl top pods -n hotel-management
kubectl top nodes

# Check HPA status
kubectl get hpa -n hotel-management

# Scale manually if needed
kubectl scale deployment hotel-backend --replicas=5 -n hotel-management
```

## Rollback Procedure

```bash
# View rollout history
kubectl rollout history deployment/hotel-backend -n hotel-management

# Rollback to previous version
kubectl rollout undo deployment/hotel-backend -n hotel-management

# Rollback to specific revision
kubectl rollout undo deployment/hotel-backend --to-revision=2 -n hotel-management

# Verify rollback
kubectl rollout status deployment/hotel-backend -n hotel-management
```

## Useful Commands

```bash
# View all resources
kubectl get all -n hotel-management

# Get detailed pod information
kubectl get pods -n hotel-management -o wide

# Execute command in pod
kubectl exec -it <pod-name> -n hotel-management -- /bin/sh

# Copy files from pod
kubectl cp <pod-name>:/path/to/file ./local-file -n hotel-management

# View pod logs (last hour)
kubectl logs --since=1h <pod-name> -n hotel-management

# Follow logs
kubectl logs -f <pod-name> -n hotel-management

# View logs from multiple pods
kubectl logs -l app=hotel-backend -n hotel-management

# Port forward
kubectl port-forward <pod-name> 8080:8080 -n hotel-management

# Get resource usage
kubectl top pods -n hotel-management
kubectl top nodes
```

---

For more information, see the main [README.md](README.md) and [ARCHITECTURE.md](ARCHITECTURE.md).
