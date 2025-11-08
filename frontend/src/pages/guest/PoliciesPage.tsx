import React, { useEffect, useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Button,
  Chip,
  Alert,
  CircularProgress,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import axios from 'axios';

interface HotelPolicy {
  id: number;
  policyCode: string;
  title: string;
  category: string;
  content: string;
  shortDescription: string;
  isMandatory: boolean;
  requiresAcceptance: boolean;
  isActive: boolean;
}

const PoliciesPage: React.FC = () => {
  const [policies, setPolicies] = useState<HotelPolicy[]>([]);
  const [loading, setLoading] = useState(true);
  const [acceptedPolicies, setAcceptedPolicies] = useState<number[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchPolicies();
  }, []);

  const fetchPolicies = async () => {
    try {
      const response = await axios.get('/api/v1/policies');
      setPolicies(response.data);
    } catch (err: any) {
      setError('Failed to load policies');
    } finally {
      setLoading(false);
    }
  };

  const handleAcceptPolicy = async (policyId: number) => {
    try {
      await axios.post(`/api/v1/policies/${policyId}/accept`);
      setAcceptedPolicies([...acceptedPolicies, policyId]);
    } catch (err: any) {
      setError('Failed to accept policy');
    }
  };

  const getCategoryColor = (category: string) => {
    const colors: { [key: string]: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info' } = {
      CHECK_IN_OUT: 'primary',
      CANCELLATION: 'error',
      PAYMENT: 'success',
      PETS: 'info',
      SMOKING: 'warning',
      CHILDREN: 'secondary',
    };
    return colors[category] || 'default';
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 8 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Hotel Policies
        </Typography>
        <Typography variant="body1" color="textSecondary" paragraph>
          Please review our policies to ensure a pleasant stay
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <Box sx={{ mt: 4 }}>
          {policies.map((policy) => (
            <Accordion key={policy.id} sx={{ mb: 2 }}>
              <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, width: '100%' }}>
                  <Typography variant="h6" sx={{ flexGrow: 1 }}>
                    {policy.title}
                  </Typography>
                  <Chip
                    label={policy.category.replace('_', ' ')}
                    color={getCategoryColor(policy.category)}
                    size="small"
                  />
                  {policy.isMandatory && (
                    <Chip label="Mandatory" color="error" size="small" />
                  )}
                  {acceptedPolicies.includes(policy.id) && (
                    <Chip label="Accepted" color="success" size="small" />
                  )}
                </Box>
              </AccordionSummary>
              <AccordionDetails>
                {policy.shortDescription && (
                  <Typography variant="body2" color="textSecondary" paragraph>
                    {policy.shortDescription}
                  </Typography>
                )}
                <Typography
                  variant="body1"
                  paragraph
                  sx={{ whiteSpace: 'pre-wrap' }}
                >
                  {policy.content}
                </Typography>
                {policy.requiresAcceptance && !acceptedPolicies.includes(policy.id) && (
                  <Box sx={{ mt: 2, textAlign: 'right' }}>
                    <Button
                      variant="contained"
                      onClick={() => handleAcceptPolicy(policy.id)}
                    >
                      Accept Policy
                    </Button>
                  </Box>
                )}
              </AccordionDetails>
            </Accordion>
          ))}
        </Box>

        {policies.length === 0 && (
          <Alert severity="info">
            No policies available at this time
          </Alert>
        )}
      </Paper>
    </Container>
  );
};

export default PoliciesPage;
