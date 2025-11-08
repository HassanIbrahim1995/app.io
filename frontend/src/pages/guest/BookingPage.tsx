import React from 'react';
import { Container, Typography } from '@mui/material';

const BookingPage: React.FC = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        Complete Your Booking
      </Typography>
      <Typography variant="body1">
        Booking form implementation here...
      </Typography>
    </Container>
  );
};

export default BookingPage;
