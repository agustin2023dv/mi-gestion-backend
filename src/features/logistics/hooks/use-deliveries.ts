import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { entregasApi } from '../api/entregas';
import type { DeliveryStatus } from '../types';

export function useDeliveries() {
  const queryClient = useQueryClient();

  const deliveriesQuery = useQuery({
    queryKey: ['deliveries'],
    queryFn: () => entregasApi.getTenantDeliveries({ size: 100 }),
  });

  const updateStatusMutation = useMutation({
    mutationFn: ({ id, newStatus, reason, notes }: { id: number; newStatus: DeliveryStatus; reason?: string; notes?: string }) =>
      entregasApi.updateStatus(id, { 
        nuevoEstado: newStatus,
        motivoCancelacion: reason,
        notasCancelacion: notes
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['deliveries'] });
    },
  });

  const assignCourierMutation = useMutation({
    mutationFn: ({ id, repartidorId }: { id: number; repartidorId: number }) =>
      entregasApi.assignCourier(id, { repartidorId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['deliveries'] });
    },
  });

  const deliveries = deliveriesQuery.data?.data?.content || [];

  return {
    deliveries,
    isLoading: deliveriesQuery.isLoading,
    isError: deliveriesQuery.isError,
    updateStatus: (id: number, newStatus: DeliveryStatus, reason?: string, notes?: string) =>
      updateStatusMutation.mutate({ id, newStatus, reason, notes }),
    assignCourier: (id: number, repartidorId: number) =>
      assignCourierMutation.mutate({ id, repartidorId }),
    isUpdating: updateStatusMutation.isPending || assignCourierMutation.isPending,
  };
}
