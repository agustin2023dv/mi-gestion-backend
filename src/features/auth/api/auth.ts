import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse } from '../../../shared/types/api';
import type { LoginRequest, LoginResponse, RegisterRequest, CreateTenantRequest } from '../types';

export const authApi = {
  login: async (data: LoginRequest) => {
    const response = await apiClient.post<ApiResponse<LoginResponse>>('/api/v1/auth/login', data);
    return response.data;
  },

  register: async (data: RegisterRequest) => {
    const response = await apiClient.post<ApiResponse<LoginResponse>>('/api/v1/auth/register', data);
    return response.data;
  },

  registerTenant: async (data: CreateTenantRequest) => {
    const response = await apiClient.post<ApiResponse<any>>('/api/v1/auth/register-tenant', data);
    return response.data;
  },

  logout: async () => {
    const response = await apiClient.post<ApiResponse<null>>('/api/v1/auth/logout');
    return response.data;
  }
};
