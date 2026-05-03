import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse, PageResponse, PaginationParams } from '../../../shared/types/api';
import type { Customer } from '../types';

export const customersApi = {
  getCustomers: async (params: PaginationParams) => {
    const response = await apiClient.get<ApiResponse<PageResponse<Customer>>>('/api/v1/clientes', { params });
    return response.data;
  },

  getCustomerById: async (id: number) => {
    const response = await apiClient.get<ApiResponse<Customer>>(`/api/v1/clientes/${id}`);
    return response.data;
  }
};
