import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Button,
  Grid,
  Chip,
  Paper,
} from '@mui/material';
import { useQuery } from 'react-query';
import { roomService } from '../../services/roomService';
import { useAuth } from '../../contexts/AuthContext';

const RoomDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const { data: room, isLoading } = useQuery(
    ['room', id],
    () => roomService.getRoomById(Number(id))
  );

  if (isLoading || !room) return <Box>Loading...</Box>;

  const handleBookNow = () => {
    if (isAuthenticated) {
      navigate(`/booking/${id}`);
    } else {
      navigate('/login', { state: { from: `/booking/${id}` } });
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          <Box sx={{ mb: 4 }}>
            <img
              src={room.imageUrls[0] || '/placeholder-room.jpg'}
              alt={room.roomTypeName}
              style={{ width: '100%', borderRadius: '8px' }}
            />
          </Box>

          <Typography variant="h4" gutterBottom>
            {room.roomTypeName} - Room {room.roomNumber}
          </Typography>

          <Typography variant="body1" paragraph>
            {room.description}
          </Typography>

          <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
            Amenities
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 3 }}>
            {room.amenities.map((amenity) => (
              <Chip
                key={amenity.id}
                label={amenity.name}
                color={amenity.isPremium ? 'primary' : 'default'}
              />
            ))}
          </Box>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, position: 'sticky', top: 20 }}>
            <Typography variant="h5" gutterBottom>
              ${room.basePrice}
              <Typography component="span" variant="body2" color="text.secondary">
                {' '}
                / night
              </Typography>
            </Typography>

            <Box sx={{ my: 2 }}>
              <Typography variant="body2" color="text.secondary">
                Floor: {room.floor}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Max Occupancy: {room.maxOccupancy} guests
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Status: {room.status}
              </Typography>
            </Box>

            <Button
              fullWidth
              variant="contained"
              size="large"
              onClick={handleBookNow}
              disabled={room.status !== 'AVAILABLE'}
            >
              {room.status === 'AVAILABLE' ? 'Book Now' : 'Not Available'}
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default RoomDetailsPage;
