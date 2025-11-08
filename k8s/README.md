# Kubernetes Deployment Guide

This directory contains Kubernetes manifests for deploying the Hotel Management System.

## Architecture

The application is deployed with the following components:
- **PostgreSQL**: Database (StatefulSet with persistent volume)
- **Redis**: Cache layer
- **Backend**: Spring Boot API (3-10 pods with HPA)
- **Frontend**: React application (2-5 pods with HPA)
- **Ingress**: NGINX Ingress Controller with TLS

## Prerequisites

1. Kubernetes cluster (v1.24+)
2. kubectl configured
3. Kustomize installed
4. NGINX Ingress Controller installed
5. Cert-Manager for TLS certificates (optional)

## Quick Start

### Development Environment

```bash
# Apply development configuration
kubectl apply -k k8s/overlays/dev

# Check deployment status
kubectl get pods -n hotel-management-dev

# Port forward to access locally
kubectl port-forward -n hotel-management-dev svc/dev-hotel-backend 8080:8080
kubectl port-forward -n hotel-management-dev svc/dev-hotel-frontend 3000:80
```

### Production Environment

```bash
# Update secrets first!
# Edit k8s/base/secrets.yaml with production values
# Or use sealed-secrets or external secrets operator

# Apply production configuration
kubectl apply -k k8s/overlays/prod

# Check deployment status
kubectl get pods -n hotel-management
kubectl get ingress -n hotel-management

# Monitor rollout
kubectl rollout status deployment/hotel-backend -n hotel-management
```

## Configuration

### Secrets

**Important**: Never commit real secrets to git!

Create secrets manually:
```bash
kubectl create secret generic hotel-secrets \
  --from-literal=database-name=hotel_management \
  --from-literal=database-user=admin \
  --from-literal=database-password=YOUR_PASSWORD \
  --from-literal=jwt-secret=YOUR_JWT_SECRET \
  --from-literal=stripe-api-key=YOUR_STRIPE_KEY \
  -n hotel-management
```

### ConfigMap

Update `k8s/base/configmap.yaml` for environment-specific configuration.

## Scaling

### Manual Scaling
```bash
# Scale backend
kubectl scale deployment hotel-backend --replicas=5 -n hotel-management

# Scale frontend
kubectl scale deployment hotel-frontend --replicas=3 -n hotel-management
```

### Auto-scaling (HPA)
HPA is configured to scale based on CPU and memory usage:
- Backend: 3-10 pods
- Frontend: 2-5 pods

Check HPA status:
```bash
kubectl get hpa -n hotel-management
```

## Monitoring

### View logs
```bash
# Backend logs
kubectl logs -f deployment/hotel-backend -n hotel-management

# Frontend logs
kubectl logs -f deployment/hotel-frontend -n hotel-management

# Database logs
kubectl logs -f deployment/postgres -n hotel-management
```

### Health checks
```bash
# Check pod health
kubectl get pods -n hotel-management

# Describe pod for details
kubectl describe pod <pod-name> -n hotel-management
```

## Database Management

### Access PostgreSQL
```bash
kubectl exec -it deployment/postgres -n hotel-management -- psql -U postgres -d hotel_management
```

### Backup Database
```bash
kubectl exec -it deployment/postgres -n hotel-management -- \
  pg_dump -U postgres hotel_management > backup.sql
```

### Restore Database
```bash
kubectl exec -i deployment/postgres -n hotel-management -- \
  psql -U postgres hotel_management < backup.sql
```

## Troubleshooting

### Pod not starting
```bash
kubectl describe pod <pod-name> -n hotel-management
kubectl logs <pod-name> -n hotel-management
```

### Service not accessible
```bash
kubectl get svc -n hotel-management
kubectl get endpoints -n hotel-management
```

### Ingress issues
```bash
kubectl describe ingress hotel-ingress -n hotel-management
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller
```

## Rolling Updates

### Update backend image
```bash
kubectl set image deployment/hotel-backend \
  hotel-backend=hotel-management/backend:v1.1.0 \
  -n hotel-management

kubectl rollout status deployment/hotel-backend -n hotel-management
```

### Rollback deployment
```bash
kubectl rollout undo deployment/hotel-backend -n hotel-management
kubectl rollout history deployment/hotel-backend -n hotel-management
```

## Clean Up

### Delete development environment
```bash
kubectl delete -k k8s/overlays/dev
```

### Delete production environment
```bash
kubectl delete -k k8s/overlays/prod
```

### Delete namespace and all resources
```bash
kubectl delete namespace hotel-management
```

## Security Best Practices

1. **Secrets Management**: Use external secrets operator or sealed-secrets
2. **Network Policies**: Implement network policies for pod-to-pod communication
3. **RBAC**: Configure proper role-based access control
4. **Pod Security**: Use Pod Security Policies or Pod Security Standards
5. **Image Security**: Scan images for vulnerabilities
6. **TLS**: Always use TLS for external communication

## Performance Tuning

1. **Resource Limits**: Adjust based on actual usage
2. **HPA Thresholds**: Tune based on traffic patterns
3. **Connection Pooling**: Configure database connection pools
4. **Caching**: Optimize Redis cache settings
5. **CDN**: Use CDN for static assets

## Monitoring & Observability

Consider integrating:
- **Prometheus**: Metrics collection
- **Grafana**: Visualization
- **ELK Stack**: Log aggregation
- **Jaeger**: Distributed tracing
- **Kiali**: Service mesh observability

## CI/CD Integration

Example GitHub Actions workflow:
```yaml
name: Deploy to Kubernetes
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build and push images
        run: |
          docker build -t hotel-management/backend:${{ github.sha }} ./backend
          docker push hotel-management/backend:${{ github.sha }}
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/hotel-backend \
            hotel-backend=hotel-management/backend:${{ github.sha }} \
            -n hotel-management
```
