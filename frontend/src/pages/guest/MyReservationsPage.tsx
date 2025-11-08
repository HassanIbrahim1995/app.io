import React from 'react';
import { Container, Typography } from '@mui/material';
import { useQuery } from 'react-query';
import { reservationService } from '../../services/reservationService';

const MyReservationsPage: React.FC = () => {
  const { data: reservations, isLoading } = useQuery(
    'myReservations',
    reservationService.getMyReservations
  );

  if (isLoading) return <div>Loading...</div>;

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        My Reservations
      </Typography>
      <Typography variant="body1">
        Found {reservations?.length || 0} reservations
      </Typography>
    </Container>
  );
};

export default MyReservationsPage;
