import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse, PageResponse, PaginationParams } from '../../../shared/types/api';
import type { Delivery, UpdateStatusRequest, AssignCourierRequest } from '../types';

export const entregasApi = {
  getTenantDeliveries: async (params: PaginationParams & { estado?: string; repartidorId?: number }) => {
    const response = await apiClient.get<ApiResponse<PageResponse<Delivery>>>('/api/v1/entregas/tenant', { params });
    return response.data;
  },

  getDeliveryById: async (id: number) => {
    const response = await apiClient.get<ApiResponse<Delivery>>(`/api/v1/entregas/${id}`);
    return response.data;
  },

  updateStatus: async (id: number, data: UpdateStatusRequest) => {
    const response = await apiClient.patch<ApiResponse<void>>(`/api/v1/entregas/${id}/estado`, data);
    return response.data;
  },

  assignCourier: async (id: number, data: AssignCourierRequest) => {
    const response = await apiClient.post<ApiResponse<void>>(`/api/v1/entregas/${id}/asignar`, data);
    return response.data;
  },

  getReceipt: async (id: number) => {
    const response = await apiClient.get<ApiResponse<{ pdfUrl: string; hashCriptografico: string }>>(`/api/v1/entregas/${id}/comprobante`);
    return response.data;
  }
};
