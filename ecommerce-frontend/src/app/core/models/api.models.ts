// Product Models
export interface Product {
    id: number;
    sku: string;
    name: string;
    description: string;
    price: number; // BigDecimal from Java
    stockQuantity: number;
    category: string;
    images?: string[];
    tags?: string[];
    active?: boolean;
    rating?: number;
    reviewCount?: number;
    createdAt?: Date;
    updatedAt?: Date;
}

export interface ProductPage {
    content: Product[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

// Order Models
export interface Order {
    id: number;
    orderNumber: string;
    customerId: string;
    customerName: string;
    customerEmail: string;
    items: OrderItem[];
    totalAmount: number;
    status: OrderStatus;
    shippingAddress: Address;
    createdAt: Date;
    updatedAt: Date;
}

export interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    subtotal: number;
}

export interface CreateOrderRequest {
    customerId: string;
    customerName: string;
    customerEmail: string;
    items: CreateOrderItemRequest[];
    shippingAddress: Address;
}

export interface CreateOrderItemRequest {
    productId: number;
    quantity: number;
}

export enum OrderStatus {
    PENDING = 'PENDING',
    CONFIRMED = 'CONFIRMED',
    PROCESSING = 'PROCESSING',
    SHIPPED = 'SHIPPED',
    DELIVERED = 'DELIVERED',
    CANCELLED = 'CANCELLED'
}

// Address Model
export interface Address {
    street: string;
    city: string;
    state: string;
    postalCode: string;
    country: string;
}

// User/Customer Models
export interface UserProfile {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string;
    addresses: Address[];
    createdAt: Date;
}

// Customer Model
export interface Customer {
    id: number;
    externalId: string; // Keycloak ID
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string;
    address?: Address;
    createdAt: Date;
    updatedAt: Date;
}

// Chatbot Models
export interface ChatMessage {
    id?: number;
    conversationId: string;
    message: string;
    response?: string;
    intent?: string;
    confidence?: number;
    timestamp: Date;
    isUser: boolean;
}

export interface ChatbotRequest {
    conversationId: string;
    message: string;
    userId?: string;
}

export interface ChatbotResponse {
    conversationId: string;
    response: string;
    intent: string;
    confidence: number;
    timestamp: Date;
}

// Cart Models
export interface CartItem {
    product: Product;
    quantity: number;
}

export interface Cart {
    items: CartItem[];
    totalAmount: number;
    totalItems: number;
}

// Notification Models
export interface Notification {
    id: number;
    type: NotificationType;
    message: string;
    read: boolean;
    createdAt: Date;
}

export enum NotificationType {
    ORDER_CONFIRMATION = 'ORDER_CONFIRMATION',
    ORDER_SHIPPED = 'ORDER_SHIPPED',
    ORDER_DELIVERED = 'ORDER_DELIVERED',
    PROMOTION = 'PROMOTION'
}

// Invoice Models
export interface Invoice {
    id: number;
    invoiceNumber: string;
    orderId: number;
    customerName: string;
    customerEmail: string;
    amount: number;
    taxAmount: number;
    totalAmount: number;
    status: InvoiceStatus;
    issueDate: Date;
    dueDate: Date;
}

export enum InvoiceStatus {
    DRAFT = 'DRAFT',
    ISSUED = 'ISSUED',
    PAID = 'PAID',
    CANCELLED = 'CANCELLED'
}

// API Response Wrapper
export interface ApiResponse<T> {
    data: T;
    message?: string;
    success: boolean;
}

// Pagination
export interface PageRequest {
    page: number;
    size: number;
    sort?: string;
    sortDir?: 'ASC' | 'DESC';
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

// Sort Direction
export type SortDirection = 'ASC' | 'DESC';

// Product Filter Options
export interface ProductFilterOptions {
    category?: string;
    minPrice?: number;
    maxPrice?: number;
    keyword?: string;
    sortBy?: string;
    sortDir?: SortDirection;
    page?: number;
    size?: number;
}
