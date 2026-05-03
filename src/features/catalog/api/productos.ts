import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse, PageResponse, PaginationParams } from '../../../shared/types/api';
import type { Product, CreateProductRequest, Category, Subcategory } from '../types';

export const catalogApi = {
  getProducts: async (params: PaginationParams) => {
    const response = await apiClient.get<ApiResponse<PageResponse<Product>>>('/api/v1/productos', { params });
    return response.data;
  },

  getProductById: async (id: number) => {
    const response = await apiClient.get<ApiResponse<Product>>(`/api/v1/productos/${id}`);
    return response.data;
  },

  createProduct: async (data: CreateProductRequest) => {
    const response = await apiClient.post<ApiResponse<Product>>('/api/v1/productos', data);
    return response.data;
  },

  updateProduct: async (id: number, data: Partial<CreateProductRequest>) => {
    const response = await apiClient.put<ApiResponse<Product>>(`/api/v1/productos/${id}`, data);
    return response.data;
  },

  getCategories: async (params: PaginationParams) => {
    const response = await apiClient.get<ApiResponse<PageResponse<Category>>>('/api/v1/categorias', { params });
    return response.data;
  },

  getSubcategories: async (params: PaginationParams & { categoriaId?: number }) => {
    const response = await apiClient.get<ApiResponse<PageResponse<Subcategory>>>('/api/v1/subcategorias', { params });
    return response.data;
  }
};
