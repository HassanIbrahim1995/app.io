import React from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Grid,
  Card,
  CardContent,
  CardMedia,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { Hotel, RoomService, Restaurant, Spa } from '@mui/icons-material';

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  const features = [
    {
      icon: <Hotel sx={{ fontSize: 60 }} />,
      title: 'Luxury Rooms',
      description: 'Experience comfort in our well-appointed rooms',
    },
    {
      icon: <RoomService sx={{ fontSize: 60 }} />,
      title: '24/7 Service',
      description: 'Round-the-clock service for your convenience',
    },
    {
      icon: <Restaurant sx={{ fontSize: 60 }} />,
      title: 'Fine Dining',
      description: 'Exquisite cuisine from around the world',
    },
    {
      icon: <Spa sx={{ fontSize: 60 }} />,
      title: 'Spa & Wellness',
      description: 'Rejuvenate your body and mind',
    },
  ];

  return (
    <Box>
      {/* Hero Section */}
      <Box
        sx={{
          bgcolor: 'primary.main',
          color: 'white',
          py: 12,
          textAlign: 'center',
        }}
      >
        <Container maxWidth="lg">
          <Typography variant="h2" component="h1" gutterBottom fontWeight="bold">
            Welcome to Hotel Management System
          </Typography>
          <Typography variant="h5" paragraph>
            Experience luxury and comfort like never before
          </Typography>
          <Box sx={{ mt: 4 }}>
            <Button
              variant="contained"
              size="large"
              onClick={() => navigate('/rooms')}
              sx={{
                bgcolor: 'white',
                color: 'primary.main',
                mr: 2,
                '&:hover': { bgcolor: 'grey.100' },
              }}
            >
              Book Now
            </Button>
            <Button
              variant="outlined"
              size="large"
              onClick={() => navigate('/rooms')}
              sx={{
                borderColor: 'white',
                color: 'white',
                '&:hover': { borderColor: 'grey.100', bgcolor: 'rgba(255,255,255,0.1)' },
              }}
            >
              Explore Rooms
            </Button>
          </Box>
        </Container>
      </Box>

      {/* Features Section */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography variant="h3" component="h2" textAlign="center" gutterBottom>
          Our Features
        </Typography>
        <Grid container spacing={4} sx={{ mt: 4 }}>
          {features.map((feature, index) => (
            <Grid item xs={12} sm={6} md={3} key={index}>
              <Card sx={{ textAlign: 'center', height: '100%' }}>
                <CardContent>
                  <Box sx={{ color: 'primary.main', mb: 2 }}>{feature.icon}</Box>
                  <Typography variant="h6" component="h3" gutterBottom>
                    {feature.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {feature.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* Footer */}
      <Box sx={{ bgcolor: 'grey.900', color: 'white', py: 6, mt: 8 }}>
        <Container maxWidth="lg">
          <Typography variant="body1" textAlign="center">
            © 2024 Hotel Management System. All rights reserved.
          </Typography>
        </Container>
      </Box>
    </Box>
  );
};

export default HomePage;
