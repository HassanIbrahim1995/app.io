import React, { useState } from 'react';
import {
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import { Add, CheckCircle, PlayArrow, Cancel } from '@mui/icons-material';

const HousekeepingPage: React.FC = () => {
  const [openDialog, setOpenDialog] = useState(false);
  const [tasks] = useState([
    {
      id: 1,
      roomNumber: '101',
      taskType: 'CLEANING',
      title: 'Daily Cleaning',
      status: 'PENDING',
      priority: 'MEDIUM',
      assignedTo: 'John Doe',
      scheduledDate: '2024-01-15T10:00:00',
    },
    {
      id: 2,
      roomNumber: '102',
      taskType: 'INSPECTION',
      title: 'Room Inspection',
      status: 'IN_PROGRESS',
      priority: 'HIGH',
      assignedTo: 'Jane Smith',
      scheduledDate: '2024-01-15T11:00:00',
    },
    {
      id: 3,
      roomNumber: '103',
      taskType: 'MAINTENANCE',
      title: 'Fix AC',
      status: 'PENDING',
      priority: 'URGENT',
      assignedTo: 'Mike Johnson',
      scheduledDate: '2024-01-15T09:00:00',
    },
  ]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return 'success';
      case 'IN_PROGRESS':
        return 'info';
      case 'PENDING':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'URGENT':
        return 'error';
      case 'HIGH':
        return 'warning';
      case 'MEDIUM':
        return 'info';
      case 'LOW':
        return 'default';
      default:
        return 'default';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12}>
          <Typography variant="h3" component="h1" gutterBottom>
            Housekeeping Dashboard
          </Typography>
        </Grid>

        {/* Statistics Cards */}
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Pending Tasks
              </Typography>
              <Typography variant="h4">12</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                In Progress
              </Typography>
              <Typography variant="h4">5</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Completed Today
              </Typography>
              <Typography variant="h4">23</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Rooms Available
              </Typography>
              <Typography variant="h4">35</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Create Task Button */}
      <Button
        variant="contained"
        startIcon={<Add />}
        onClick={() => setOpenDialog(true)}
        sx={{ mb: 3 }}
      >
        Create Task
      </Button>

      {/* Tasks Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Room</TableCell>
              <TableCell>Task Type</TableCell>
              <TableCell>Title</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Priority</TableCell>
              <TableCell>Assigned To</TableCell>
              <TableCell>Scheduled</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tasks.map((task) => (
              <TableRow key={task.id}>
                <TableCell>{task.roomNumber}</TableCell>
                <TableCell>{task.taskType}</TableCell>
                <TableCell>{task.title}</TableCell>
                <TableCell>
                  <Chip
                    label={task.status}
                    color={getStatusColor(task.status) as any}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <Chip
                    label={task.priority}
                    color={getPriorityColor(task.priority) as any}
                    size="small"
                  />
                </TableCell>
                <TableCell>{task.assignedTo}</TableCell>
                <TableCell>
                  {new Date(task.scheduledDate).toLocaleString()}
                </TableCell>
                <TableCell>
                  {task.status === 'PENDING' && (
                    <Button size="small" startIcon={<PlayArrow />}>
                      Start
                    </Button>
                  )}
                  {task.status === 'IN_PROGRESS' && (
                    <Button size="small" startIcon={<CheckCircle />}>
                      Complete
                    </Button>
                  )}
                  <Button size="small" color="error" startIcon={<Cancel />}>
                    Cancel
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Create Task Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Create Housekeeping Task</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Room Number" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Task Type</InputLabel>
                <Select label="Task Type" defaultValue="">
                  <MenuItem value="CLEANING">Cleaning</MenuItem>
                  <MenuItem value="INSPECTION">Inspection</MenuItem>
                  <MenuItem value="MAINTENANCE">Maintenance</MenuItem>
                  <MenuItem value="TURNDOWN">Turndown Service</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Title" required />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Description" multiline rows={3} />
            </Grid>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Priority</InputLabel>
                <Select label="Priority" defaultValue="MEDIUM">
                  <MenuItem value="LOW">Low</MenuItem>
                  <MenuItem value="MEDIUM">Medium</MenuItem>
                  <MenuItem value="HIGH">High</MenuItem>
                  <MenuItem value="URGENT">Urgent</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Scheduled Date"
                type="datetime-local"
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Assign To" />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Notes" multiline rows={2} />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => setOpenDialog(false)}>
            Create Task
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default HousekeepingPage;
