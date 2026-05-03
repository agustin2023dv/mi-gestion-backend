export interface CustomerOrder {
  id: number;
  date: string;
  total: number;
  status: 'PENDIENTE' | 'PREPARADO' | 'EN_CAMINO' | 'ENTREGADO' | 'CANCELADO';
  itemsCount: number;
}

export interface Customer {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  totalOrders: number;
  totalSpent: number;
  lastOrderDate: string;
  status: 'VIP' | 'Recurrente' | 'Nuevo';
  orders: CustomerOrder[];
}
