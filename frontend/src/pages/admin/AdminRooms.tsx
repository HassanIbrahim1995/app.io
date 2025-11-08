import React from 'react';
import { Container, Typography } from '@mui/material';

const AdminRooms: React.FC = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        Room Management
      </Typography>
    </Container>
  );
};

export default AdminRooms;
