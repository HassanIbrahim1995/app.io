import React, { useState } from 'react';
import {
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  FormControlLabel,
  Checkbox,
} from '@mui/material';
import { Add, Edit, Delete, Inventory } from '@mui/icons-material';

const ProductsPage: React.FC = () => {
  const [openDialog, setOpenDialog] = useState(false);
  const [products] = useState([
    {
      id: 1,
      sku: 'MINI-001',
      name: 'Premium Wine',
      category: 'MINIBAR',
      price: 45.00,
      quantity: 120,
      status: 'ACTIVE',
      isUpsell: true,
    },
    {
      id: 2,
      sku: 'SPA-001',
      name: 'Massage 60 min',
      category: 'SPA',
      price: 120.00,
      quantity: 0,
      status: 'ACTIVE',
      isUpsell: true,
    },
    {
      id: 3,
      sku: 'LAUN-001',
      name: 'Express Laundry',
      category: 'LAUNDRY',
      price: 25.00,
      quantity: 0,
      status: 'ACTIVE',
      isUpsell: false,
    },
  ]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'success';
      case 'INACTIVE':
        return 'default';
      case 'OUT_OF_STOCK':
        return 'error';
      default:
        return 'default';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12}>
          <Typography variant="h3" component="h1" gutterBottom>
            Product Catalog
          </Typography>
        </Grid>

        {/* Statistics Cards */}
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Products
              </Typography>
              <Typography variant="h4">156</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Active Products
              </Typography>
              <Typography variant="h4">142</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Low Stock
              </Typography>
              <Typography variant="h4" color="error">8</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Upsell Items
              </Typography>
              <Typography variant="h4">23</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Action Buttons */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setOpenDialog(true)}
          >
            Add Product
          </Button>
        </Grid>
        <Grid item>
          <Button variant="outlined" startIcon={<Inventory />}>
            Low Stock Alert
          </Button>
        </Grid>
      </Grid>

      {/* Products Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>SKU</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Category</TableCell>
              <TableCell>Price</TableCell>
              <TableCell>Stock</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Upsell</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {products.map((product) => (
              <TableRow key={product.id}>
                <TableCell>{product.sku}</TableCell>
                <TableCell>{product.name}</TableCell>
                <TableCell>{product.category}</TableCell>
                <TableCell>${product.price.toFixed(2)}</TableCell>
                <TableCell>
                  <Chip
                    label={product.quantity}
                    color={product.quantity > 10 ? 'success' : 'error'}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <Chip
                    label={product.status}
                    color={getStatusColor(product.status) as any}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {product.isUpsell && <Chip label="Upsell" color="primary" size="small" />}
                </TableCell>
                <TableCell>
                  <Button size="small" startIcon={<Edit />}>
                    Edit
                  </Button>
                  <Button size="small" color="error" startIcon={<Delete />}>
                    Delete
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Add/Edit Product Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Add New Product</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="SKU" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Product Name" required />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Description" multiline rows={3} />
            </Grid>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Category</InputLabel>
                <Select label="Category" defaultValue="">
                  <MenuItem value="MINIBAR">Minibar</MenuItem>
                  <MenuItem value="ROOM_SERVICE">Room Service</MenuItem>
                  <MenuItem value="SPA">Spa</MenuItem>
                  <MenuItem value="LAUNDRY">Laundry</MenuItem>
                  <MenuItem value="PARKING">Parking</MenuItem>
                  <MenuItem value="OTHER">Other</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Price" type="number" required />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Cost Price" type="number" />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Initial Stock" type="number" defaultValue={0} />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Min Stock Level" type="number" defaultValue={0} />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField fullWidth label="Tax Rate (%)" type="number" defaultValue={0} />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={<Checkbox />}
                label="Mark as Upsell Product"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Upsell Description"
                multiline
                rows={2}
                helperText="Description shown when upselling this product"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => setOpenDialog(false)}>
            Save Product
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ProductsPage;
