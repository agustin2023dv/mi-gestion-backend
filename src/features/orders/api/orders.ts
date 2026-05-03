import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse, PageResponse, PaginationParams } from '../../../shared/types/api';

export interface Order {
  id: number;
  numeroPedido: string;
  cliente: {
    nombre: string;
    apellido: string;
  };
  fechaPedido: string;
  total: number;
  estado: string;
  items?: any[];
}

export const ordersApi = {
  getOrders: async (params: PaginationParams & { status?: string }) => {
    const response = await apiClient.get<ApiResponse<PageResponse<Order>>>('/api/v1/pedidos', { params });
    return response.data;
  },

  getOrderById: async (id: number) => {
    const response = await apiClient.get<ApiResponse<Order>>(`/api/v1/pedidos/${id}`);
    return response.data;
  }
};
