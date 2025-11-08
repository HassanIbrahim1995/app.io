import React, { useEffect, useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  MenuItem,
  Alert,
  CircularProgress,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';

interface Employee {
  id: number;
  employeeNumber: string;
  department: string;
  position: string;
  employmentType: string;
  employmentStatus: string;
  hireDate: string;
  salary: number;
  user: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
  };
}

const EmployeesPage: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [openDialog, setOpenDialog] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    // Mock data for demonstration
    setEmployees([
      {
        id: 1,
        employeeNumber: 'EMP001',
        department: 'Front Desk',
        position: 'Receptionist',
        employmentType: 'FULL_TIME',
        employmentStatus: 'ACTIVE',
        hireDate: '2023-01-15',
        salary: 45000,
        user: {
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@hotel.com',
          phone: '+1234567890',
        },
      },
      {
        id: 2,
        employeeNumber: 'EMP002',
        department: 'Housekeeping',
        position: 'Supervisor',
        employmentType: 'FULL_TIME',
        employmentStatus: 'ACTIVE',
        hireDate: '2022-06-01',
        salary: 55000,
        user: {
          firstName: 'Jane',
          lastName: 'Smith',
          email: 'jane.smith@hotel.com',
          phone: '+1234567891',
        },
      },
    ]);
    setLoading(false);
  }, []);

  const getStatusColor = (status: string) => {
    const colors: { [key: string]: 'success' | 'warning' | 'error' | 'default' } = {
      ACTIVE: 'success',
      ON_LEAVE: 'warning',
      SUSPENDED: 'error',
      TERMINATED: 'error',
    };
    return colors[status] || 'default';
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
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
          <Typography variant="h4">Employee Management</Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setOpenDialog(true)}
          >
            Add Employee
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Employee ID</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Department</TableCell>
                <TableCell>Position</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Hire Date</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {employees.map((employee) => (
                <TableRow key={employee.id}>
                  <TableCell>{employee.employeeNumber}</TableCell>
                  <TableCell>
                    {employee.user.firstName} {employee.user.lastName}
                  </TableCell>
                  <TableCell>{employee.department}</TableCell>
                  <TableCell>{employee.position}</TableCell>
                  <TableCell>
                    <Chip
                      label={employee.employmentType.replace('_', ' ')}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={employee.employmentStatus}
                      color={getStatusColor(employee.employmentStatus)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>{employee.hireDate}</TableCell>
                  <TableCell>
                    <Button size="small">View</Button>
                    <Button size="small">Edit</Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        {employees.length === 0 && (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography variant="body1" color="textSecondary">
              No employees found
            </Typography>
          </Box>
        )}
      </Paper>

      {/* Add Employee Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Add New Employee</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="First Name" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Last Name" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Email" type="email" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Phone" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Department" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Position" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                select
                fullWidth
                label="Employment Type"
                defaultValue="FULL_TIME"
              >
                <MenuItem value="FULL_TIME">Full Time</MenuItem>
                <MenuItem value="PART_TIME">Part Time</MenuItem>
                <MenuItem value="CONTRACT">Contract</MenuItem>
                <MenuItem value="SEASONAL">Seasonal</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Hire Date"
                type="date"
                InputLabelProps={{ shrink: true }}
                required
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Salary" type="number" />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => setOpenDialog(false)}>
            Add Employee
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default EmployeesPage;
