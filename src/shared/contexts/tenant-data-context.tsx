import React, { createContext, useContext, useMemo } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { catalogApi } from '../../features/catalog/api/productos';
import { ordersApi } from '../../features/orders/api/orders';
import { customersApi } from '../../features/customers/api/clientes';
import type { Product } from '../../features/catalog/types';
import type { Order } from '../../features/orders/api/orders';
import type { Customer } from '../../features/customers/types';

interface TenantDataContextType {
  products: Product[];
  orders: Order[];
  customers: Customer[];
  isLoading: boolean;
  isError: boolean;
  refreshProducts: () => Promise<void>;
  refreshOrders: () => Promise<void>;
  refreshCustomers: () => Promise<void>;
  refreshAll: () => Promise<void>;
}

const TenantDataContext = createContext<TenantDataContextType | undefined>(undefined);

export const TenantDataProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const queryClient = useQueryClient();

  // We fetch a large enough page to "store" the main entities of the tenant
  const productsQuery = useQuery({
    queryKey: ['tenant-products'],
    queryFn: () => catalogApi.getProducts({ size: 1000 }),
    staleTime: Infinity, // Avoid refetching unless data changes manually
  });

  const ordersQuery = useQuery({
    queryKey: ['tenant-orders'],
    queryFn: () => ordersApi.getOrders({ size: 1000 }),
    staleTime: Infinity,
  });

  const customersQuery = useQuery({
    queryKey: ['tenant-customers'],
    queryFn: () => customersApi.getCustomers({ size: 1000 }),
    staleTime: Infinity,
  });

  const isLoading = productsQuery.isLoading || ordersQuery.isLoading || customersQuery.isLoading;
  const isError = productsQuery.isError || ordersQuery.isError || customersQuery.isError;

  const value = useMemo(() => ({
    products: productsQuery.data?.data?.content || [],
    orders: ordersQuery.data?.data?.content || [],
    customers: customersQuery.data?.data?.content || [],
    isLoading,
    isError,
    refreshProducts: async () => {
      await queryClient.invalidateQueries({ queryKey: ['tenant-products'] });
    },
    refreshOrders: async () => {
      await queryClient.invalidateQueries({ queryKey: ['tenant-orders'] });
    },
    refreshCustomers: async () => {
      await queryClient.invalidateQueries({ queryKey: ['tenant-customers'] });
    },
    refreshAll: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['tenant-products'] }),
        queryClient.invalidateQueries({ queryKey: ['tenant-orders'] }),
        queryClient.invalidateQueries({ queryKey: ['tenant-customers'] }),
      ]);
    },
  }), [
    productsQuery.data, 
    ordersQuery.data, 
    customersQuery.data, 
    isLoading, 
    isError, 
    queryClient
  ]);

  return (
    <TenantDataContext.Provider value={value}>
      {children}
    </TenantDataContext.Provider>
  );
};

export const useTenantData = () => {
  const context = useContext(TenantDataContext);
  if (context === undefined) {
    throw new Error('useTenantData must be used within a TenantDataProvider');
  }
  return context;
};
