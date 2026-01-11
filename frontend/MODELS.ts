// TypeScript interfaces for API models

export interface Product {
  id?: number;
  sku: string;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  category: string;
  images?: string[];
  tags?: string[];
  active?: boolean;
  rating?: number;
  reviewCount?: number;
}

export interface Customer {
  id?: number;
  keycloakId: string;
  email: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  dateOfBirth?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  profilePictureUrl?: string;
  newsletterSubscribed?: boolean;
  preferredLanguage?: string;
  addresses?: CustomerAddress[];
  totalOrders?: number;
  totalSpent?: number;
  loyaltyPoints?: number;
  active?: boolean;
}

export interface CustomerAddress {
  id?: number;
  label?: string;
  fullName: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state?: string;
  zipCode?: string;
  country: string;
  phone?: string;
  isDefault?: boolean;
}

export interface Order {
  id?: number;
  orderNumber?: string;
  customerId: string;
  customerEmail?: string;
  status: OrderStatus;
  totalAmount: number;
  items: OrderItem[];
  shippingAddress?: ShippingAddress;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export interface OrderItem {
  id?: number;
  productId: number;
  productName: string;
  productSku?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface ShippingAddress {
  fullName: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state?: string;
  zipCode?: string;
  country: string;
  phone?: string;
}

export interface Invoice {
  id?: number;
  invoiceNumber: string;
  orderId: number;
  orderNumber?: string;
  customerId: string;
  customerName?: string;
  customerEmail?: string;
  invoiceDate: string;
  dueDate?: string;
  subtotal: number;
  taxAmount: number;
  discountAmount?: number;
  totalAmount: number;
  status: InvoiceStatus;
  pdfPath?: string;
}

export enum InvoiceStatus {
  DRAFT = 'DRAFT',
  ISSUED = 'ISSUED',
  SENT = 'SENT',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

export interface ChatMessage {
  conversationId?: string;
  userId: string;
  message: string;
  response?: string;
  intent?: string;
  timestamp?: string;
  isFromBot?: boolean;
}

export interface User {
  id?: number;
  keycloakId: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  roles: UserRole[];
  companyName?: string;
  companyAddress?: string;
  taxId?: string;
  commissionRate?: number;
  active?: boolean;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  VENDOR = 'VENDOR'
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
