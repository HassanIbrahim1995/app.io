import React from 'react';
import { Container, Typography } from '@mui/material';

const AdminGuests: React.FC = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        Guest Management
      </Typography>
    </Container>
  );
};

export default AdminGuests;
