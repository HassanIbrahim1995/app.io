import React, { useEffect, useState } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  Grid,
  TextField,
  Button,
  Chip,
  Divider,
  Alert,
  CircularProgress,
  Card,
  CardContent,
} from '@mui/material';
import { useAuth } from '../../contexts/AuthContext';
import axios from 'axios';

interface GuestProfile {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  nationality: string;
  address: string;
  city: string;
  country: string;
  postalCode: string;
  documentType: string;
  documentNumber: string;
  company: string;
  jobTitle: string;
  loyaltyPoints: number;
  loyaltyTier: string;
  vipStatus: boolean;
  dietaryPreferences: string;
  allergies: string;
  accessibilityNeeds: string;
  languagePreference: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  marketingConsent: boolean;
  emailNotifications: boolean;
  smsNotifications: boolean;
  totalStays: number;
  totalSpent: number;
  lastStayDate: string;
  profileCompleted: boolean;
}

const GuestProfilePage: React.FC = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState<GuestProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await axios.get('/api/v1/guest-profile/me');
      setProfile(response.data);
    } catch (err: any) {
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async () => {
    try {
      await axios.put('/api/v1/guest-profile/me', profile);
      setSuccess('Profile updated successfully');
      setEditing(false);
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError('Failed to update profile');
    }
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (!profile) {
    return (
      <Container maxWidth="md" sx={{ py: 8 }}>
        <Alert severity="error">Profile not found</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 8 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
          <Typography variant="h4">My Profile</Typography>
          {!editing ? (
            <Button variant="contained" onClick={() => setEditing(true)}>
              Edit Profile
            </Button>
          ) : (
            <Box>
              <Button onClick={() => setEditing(false)} sx={{ mr: 1 }}>
                Cancel
              </Button>
              <Button variant="contained" onClick={handleUpdate}>
                Save Changes
              </Button>
            </Box>
          )}
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Grid container spacing={3}>
          {/* Loyalty Status */}
          <Grid item xs={12}>
            <Card sx={{ bgcolor: 'primary.light', color: 'primary.contrastText' }}>
              <CardContent>
                <Grid container spacing={2} alignItems="center">
                  <Grid item xs={12} md={3}>
                    <Typography variant="h6">Loyalty Tier</Typography>
                    <Chip
                      label={profile.loyaltyTier}
                      color={profile.vipStatus ? 'secondary' : 'default'}
                      size="large"
                    />
                  </Grid>
                  <Grid item xs={12} md={3}>
                    <Typography variant="h6">{profile.loyaltyPoints}</Typography>
                    <Typography variant="body2">Points</Typography>
                  </Grid>
                  <Grid item xs={12} md={3}>
                    <Typography variant="h6">{profile.totalStays}</Typography>
                    <Typography variant="body2">Total Stays</Typography>
                  </Grid>
                  <Grid item xs={12} md={3}>
                    <Typography variant="h6">${profile.totalSpent}</Typography>
                    <Typography variant="body2">Total Spent</Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="h6" gutterBottom>Personal Information</Typography>
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="First Name"
              value={profile.firstName}
              onChange={(e) => setProfile({ ...profile, firstName: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Last Name"
              value={profile.lastName}
              onChange={(e) => setProfile({ ...profile, lastName: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Email"
              value={profile.email}
              disabled
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Phone"
              value={profile.phone}
              onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Date of Birth"
              type="date"
              InputLabelProps={{ shrink: true }}
              value={profile.dateOfBirth}
              onChange={(e) => setProfile({ ...profile, dateOfBirth: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Nationality"
              value={profile.nationality || ''}
              onChange={(e) => setProfile({ ...profile, nationality: e.target.value })}
              disabled={!editing}
            />
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="h6" gutterBottom>Contact Information</Typography>
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Address"
              value={profile.address}
              onChange={(e) => setProfile({ ...profile, address: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="City"
              value={profile.city}
              onChange={(e) => setProfile({ ...profile, city: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Country"
              value={profile.country}
              onChange={(e) => setProfile({ ...profile, country: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Postal Code"
              value={profile.postalCode || ''}
              onChange={(e) => setProfile({ ...profile, postalCode: e.target.value })}
              disabled={!editing}
            />
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="h6" gutterBottom>Preferences</Typography>
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Dietary Preferences"
              multiline
              rows={2}
              value={profile.dietaryPreferences || ''}
              onChange={(e) => setProfile({ ...profile, dietaryPreferences: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Allergies"
              multiline
              rows={2}
              value={profile.allergies || ''}
              onChange={(e) => setProfile({ ...profile, allergies: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Accessibility Needs"
              multiline
              rows={2}
              value={profile.accessibilityNeeds || ''}
              onChange={(e) => setProfile({ ...profile, accessibilityNeeds: e.target.value })}
              disabled={!editing}
            />
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="h6" gutterBottom>Emergency Contact</Typography>
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Emergency Contact Name"
              value={profile.emergencyContactName || ''}
              onChange={(e) => setProfile({ ...profile, emergencyContactName: e.target.value })}
              disabled={!editing}
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Emergency Contact Phone"
              value={profile.emergencyContactPhone || ''}
              onChange={(e) => setProfile({ ...profile, emergencyContactPhone: e.target.value })}
              disabled={!editing}
            />
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
};

export default GuestProfilePage;
