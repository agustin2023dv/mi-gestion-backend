import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse, PageResponse, PaginationParams } from '../../../shared/types/api';
import type { Delivery, UpdateStatusRequest, AssignCourierRequest } from '../types';

export const entregasApi = {
  getTenantDeliveries: async (params: PaginationParams & { estado?: string; repartidorId?: number }) => {
    console.log('[entregasApi.getTenantDeliveries] Request params:', params);
    const response = await apiClient.get<ApiResponse<PageResponse<Delivery>>>('/api/v1/entregas/tenant', { params });
    console.log('[entregasApi.getTenantDeliveries] Response:', response.data);
    return response.data;
  },

  getDeliveryById: async (id: number) => {
    console.log('[entregasApi.getDeliveryById] Request ID:', id);
    const response = await apiClient.get<ApiResponse<Delivery>>(`/api/v1/entregas/${id}`);
    console.log('[entregasApi.getDeliveryById] Response:', response.data);
    return response.data;
  },

  updateStatus: async (id: number, data: UpdateStatusRequest) => {
    console.log('[entregasApi.updateStatus] Request ID:', id, 'Data:', data);
    const response = await apiClient.patch<ApiResponse<void>>(`/api/v1/entregas/${id}/estado`, data);
    console.log('[entregasApi.updateStatus] Response:', response.data);
    return response.data;
  },

  assignCourier: async (id: number, data: AssignCourierRequest) => {
    console.log('[entregasApi.assignCourier] Request ID:', id, 'Data:', data);
    const response = await apiClient.post<ApiResponse<void>>(`/api/v1/entregas/${id}/asignar`, data);
    console.log('[entregasApi.assignCourier] Response:', response.data);
    return response.data;
  },

  getReceipt: async (id: number) => {
    console.log('[entregasApi.getReceipt] Request ID:', id);
    const response = await apiClient.get<ApiResponse<{ pdfUrl: string; hashCriptografico: string }>>(`/api/v1/entregas/${id}/comprobante`);
    console.log('[entregasApi.getReceipt] Response:', response.data);
    return response.data;
  }
};
