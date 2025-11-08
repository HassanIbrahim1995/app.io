export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: 'GUEST' | 'FRONT_DESK' | 'HOUSEKEEPING' | 'MANAGER' | 'ADMIN';
  isActive: boolean;
  isEmailVerified: boolean;
  profileImageUrl?: string;
  address?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  createdAt: string;
  lastLogin?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Room {
  id: number;
  roomNumber: string;
  roomTypeId: number;
  roomTypeName: string;
  floor: number;
  basePrice: number;
  status: 'AVAILABLE' | 'OCCUPIED' | 'RESERVED' | 'CLEANING' | 'MAINTENANCE' | 'OUT_OF_ORDER' | 'BLOCKED';
  maxOccupancy: number;
  isAccessible: boolean;
  isSmokingAllowed: boolean;
  description?: string;
  imageUrls: string[];
  amenities: Amenity[];
  averageRating?: number;
}

export interface Amenity {
  id: number;
  name: string;
  description?: string;
  iconName?: string;
  isPremium: boolean;
}

export interface Reservation {
  id: number;
  reservationNumber: string;
  guestId: number;
  guestName: string;
  roomId: number;
  roomNumber: string;
  checkInDate: string;
  checkOutDate: string;
  actualCheckIn?: string;
  actualCheckOut?: string;
  numberOfGuests: number;
  numberOfAdults: number;
  numberOfChildren: number;
  status: 'PENDING' | 'CONFIRMED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED' | 'NO_SHOW' | 'COMPLETED';
  totalAmount: number;
  paidAmount: number;
  discountAmount: number;
  taxAmount: number;
  specialRequests?: string;
  promoCode?: string;
  source: string;
  createdAt: string;
}

export interface RoomSearchParams {
  checkInDate?: string;
  checkOutDate?: string;
  roomTypeId?: number;
  guests?: number;
  minPrice?: number;
  maxPrice?: number;
  isAccessible?: boolean;
  isSmokingAllowed?: boolean;
}

export interface ReservationRequest {
  roomId: number;
  checkInDate: string;
  checkOutDate: string;
  numberOfGuests: number;
  numberOfAdults: number;
  numberOfChildren?: number;
  specialRequests?: string;
  promoCode?: string;
  addons?: AddonRequest[];
}

export interface AddonRequest {
  addonType: string;
  name: string;
  description?: string;
  quantity: number;
  pricePerUnit: number;
}

export interface ErrorResponse {
  status: number;
  message: string;
  errors?: { [key: string]: string };
  timestamp: string;
}
