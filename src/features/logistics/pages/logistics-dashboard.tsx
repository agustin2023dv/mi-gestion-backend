import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Truck, Map, Filter, Plus, Loader2 } from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { DeliveryCard } from '../components/delivery-card';
import { Section } from '../../../shared/components/ui/section';
import { entregasApi } from '../api/entregas';
import type { DeliveryStatus } from '../types';

const KANBAN_COLUMNS: { id: DeliveryStatus; label: string; color: string }[] = [
  { id: 'PENDIENTE', label: 'Pendientes', color: 'bg-amber-100/50 border-amber-200' },
  { id: 'ASIGNADA', label: 'Asignadas', color: 'bg-blue-100/50 border-blue-200' },
  { id: 'EN_CAMINO', label: 'En Camino', color: 'bg-indigo-100/50 border-indigo-200' },
  { id: 'ENTREGADA', label: 'Entregadas', color: 'bg-emerald-100/50 border-emerald-200' }
];

export default function LogisticsDashboard() {
  const queryClient = useQueryClient();

  const { data, isLoading, isError } = useQuery({
    queryKey: ['deliveries'],
    queryFn: () => entregasApi.getTenantDeliveries({ size: 100 }),
  });

  const updateStatusMutation = useMutation({
    mutationFn: ({ id, newStatus }: { id: number; newStatus: DeliveryStatus }) =>
      entregasApi.updateStatus(id, { nuevoEstado: newStatus }),
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

  const handleUpdateStatus = (id: number, newStatus: string) => {
    updateStatusMutation.mutate({ id, newStatus: newStatus as DeliveryStatus });
  };

  const handleAssignCourier = (id: number) => {
    // In a real app, this would probably open a modal to select a courier.
    // For now, we use a placeholder ID as in the original mockup.
    assignCourierMutation.mutate({ id, repartidorId: 70 });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader2 className="w-8 h-8 animate-spin text-stone-400" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="p-8 text-center bg-red-50 border border-red-100 rounded-2xl text-red-600">
        Error al cargar las entregas. Por favor, intenta de nuevo.
      </div>
    );
  }

  const deliveries = data?.data?.content || [];

  return (
    <Section title="Logística de Entregas"> {/* ✅ description eliminada */}

      {/* Toolbar */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
        <div className="flex bg-white rounded-lg border border-stone-200 p-1 shadow-sm">
          <button className="px-4 py-2 text-sm font-medium bg-stone-900 text-white rounded-md flex items-center gap-2">
            <Truck className="w-4 h-4" />
            Tablero Kanban
          </button>
          <button className="px-4 py-2 text-sm font-medium text-stone-600 hover:bg-stone-50 rounded-md flex items-center gap-2 transition-colors">
            <Map className="w-4 h-4" />
            Mapa en Vivo
          </button>
        </div>

        <div className="flex items-center gap-3">
          <Button variant="outline" className="gap-2 bg-white">
            <Filter className="w-4 h-4" />
            Filtros
          </Button>
          <Button className="gap-2">
            <Plus className="w-4 h-4" />
            Nueva Entrega
          </Button>
        </div>
      </div>

      {/* Kanban Board */}
      <div className="flex overflow-x-auto pb-6 -mx-4 px-4 sm:mx-0 sm:px-0 gap-6 snap-x min-h-[600px]">
        {KANBAN_COLUMNS.map(col => {
          const columnDeliveries = deliveries.filter(d => d.estado === col.id);

          return (
            <div key={col.id} className="min-w-[300px] w-[320px] shrink-0 snap-center flex flex-col">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-bold text-stone-900 flex items-center gap-2">
                  {col.label}
                  <span className="bg-stone-100 text-stone-600 text-xs py-0.5 px-2 rounded-full font-mono">
                    {columnDeliveries.length}
                  </span>
                </h3>
              </div>

              <div className={`flex-1 rounded-2xl p-3 flex flex-col gap-3 border border-dashed transition-colors ${col.color}`}>
                {columnDeliveries.map(delivery => (
                  <DeliveryCard
                    key={delivery.id}
                    delivery={delivery}
                    onUpdateStatus={handleUpdateStatus}
                    onAssignCourier={handleAssignCourier}
                  />
                ))}

                {columnDeliveries.length === 0 && (
                  <div className="h-24 border-2 border-dashed border-stone-300 rounded-xl flex items-center justify-center text-stone-400 text-sm font-medium">
                    Sin entregas
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </Section>
  );
}