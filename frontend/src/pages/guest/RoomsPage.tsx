import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Button,
  TextField,
  Paper,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useQuery } from 'react-query';
import { roomService } from '../../services/roomService';
import { RoomSearchParams } from '../../types';

const RoomsPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useState<RoomSearchParams>({});

  const { data: rooms, isLoading } = useQuery(
    ['rooms', searchParams],
    () => roomService.searchRooms(searchParams)
  );

  const handleSearch = () => {
    // Trigger search by updating query params
  };

  if (isLoading) return <Box>Loading...</Box>;

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        Available Rooms
      </Typography>

      {/* Search Filters */}
      <Paper sx={{ p: 3, mb: 4 }}>
        <Grid container spacing={2}>
          <Grid item xs={12} md={3}>
            <TextField
              fullWidth
              type="date"
              label="Check-in"
              InputLabelProps={{ shrink: true }}
              value={searchParams.checkInDate || ''}
              onChange={(e) =>
                setSearchParams({ ...searchParams, checkInDate: e.target.value })
              }
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <TextField
              fullWidth
              type="date"
              label="Check-out"
              InputLabelProps={{ shrink: true }}
              value={searchParams.checkOutDate || ''}
              onChange={(e) =>
                setSearchParams({ ...searchParams, checkOutDate: e.target.value })
              }
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <TextField
              fullWidth
              type="number"
              label="Guests"
              value={searchParams.guests || ''}
              onChange={(e) =>
                setSearchParams({ ...searchParams, guests: parseInt(e.target.value) })
              }
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <Button
              fullWidth
              variant="contained"
              size="large"
              onClick={handleSearch}
              sx={{ height: '56px' }}
            >
              Search
            </Button>
          </Grid>
        </Grid>
      </Paper>

      {/* Room List */}
      <Grid container spacing={3}>
        {rooms?.map((room) => (
          <Grid item xs={12} md={4} key={room.id}>
            <Card>
              <CardMedia
                component="img"
                height="200"
                image={room.imageUrls[0] || '/placeholder-room.jpg'}
                alt={room.roomTypeName}
              />
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {room.roomTypeName} - Room {room.roomNumber}
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  {room.description}
                </Typography>
                <Typography variant="h6" color="primary" gutterBottom>
                  ${room.basePrice} / night
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  Max Occupancy: {room.maxOccupancy} guests
                </Typography>
                <Button
                  fullWidth
                  variant="contained"
                  onClick={() => navigate(`/rooms/${room.id}`)}
                >
                  View Details
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
};

export default RoomsPage;
