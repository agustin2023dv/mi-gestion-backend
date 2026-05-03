import { useCallback } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { catalogApi } from '../api/productos';
import type { Product, CreateProductRequest } from '../types';
export type { Product, CreateProductRequest };

export function useProducts(page = 0, size = 20, search = '') {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['products', page, size, search],
    queryFn: () => catalogApi.getProducts({ page, size, search }),
  });

  const products = query.data?.data?.content || [];
  const totalElements = query.data?.data?.totalElements || 0;
  const totalPages = query.data?.data?.totalPages || 0;

  const addProductMutation = useMutation({
    mutationFn: (newProduct: CreateProductRequest) => catalogApi.createProduct(newProduct),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });

  const updateProductMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<CreateProductRequest> }) => 
      catalogApi.updateProduct(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });

  const deleteProductMutation = useMutation({
    mutationFn: (id: number) => catalogApi.deleteProduct(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });

  const markAsOutOfStock = useCallback((id: number) => {
    updateProductMutation.mutate({ id, data: { stock: 0 } });
  }, [updateProductMutation]);

  return {
    ...query,
    products,
    totalElements,
    totalPages,
    addProduct: addProductMutation.mutateAsync,
    updateProduct: updateProductMutation.mutateAsync,
    deleteProduct: deleteProductMutation.mutateAsync,
    markAsOutOfStock,
    isDeleting: deleteProductMutation.isPending
  };
}
