import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse, PageResponse, PaginationParams } from '../../../shared/types/api';
import type { GastoOperativo, CreateGastoRequest, CategoriaGasto, CentroCosto } from '../types';

export const financeApi = {
  getExpenses: async (params: PaginationParams & { 
    fechaDesde?: string; 
    fechaHasta?: string; 
    categoriaGastoId?: number; 
    centroCostoId?: number; 
    esProrrateable?: boolean;
    sort?: string;
  }) => {
    const response = await apiClient.get<ApiResponse<PageResponse<GastoOperativo>>>('/api/v1/gastos', { params });
    return response.data;
  },

  getExpenseById: async (id: number) => {
    const response = await apiClient.get<ApiResponse<GastoOperativo>>(`/api/v1/gastos/${id}`);
    return response.data;
  },

  createExpense: async (data: CreateGastoRequest) => {
    const response = await apiClient.post<ApiResponse<GastoOperativo>>('/api/v1/gastos', data);
    return response.data;
  },

  updateExpense: async (id: number, data: Partial<CreateGastoRequest>) => {
    const response = await apiClient.patch<ApiResponse<GastoOperativo>>(`/api/v1/gastos/${id}`, data);
    return response.data;
  },

  deleteExpense: async (id: number) => {
    const response = await apiClient.delete<ApiResponse<void>>(`/api/v1/gastos/${id}`);
    return response.data;
  },

  getCategories: async () => {
    const response = await apiClient.get<ApiResponse<CategoriaGasto[]>>('/api/v1/gastos/categorias');
    return response.data;
  },

  getCostCentres: async () => {
    const response = await apiClient.get<ApiResponse<CentroCosto[]>>('/api/v1/gastos/centros-costo');
    return response.data;
  }
};
