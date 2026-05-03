import { useState, useMemo } from 'react';
import { Truck, Map, Filter, Plus, Loader2, PackageOpen } from 'lucide-react';
import { 
  DndContext, 
  DragOverlay, 
  closestCorners, 
  KeyboardSensor, 
  PointerSensor, 
  useSensor, 
  useSensors,
  defaultDropAnimationSideEffects,
  type DragEndEvent,
  type DragStartEvent
} from '@dnd-kit/core';
import { 
  SortableContext, 
  sortableKeyboardCoordinates, 
  verticalListSortingStrategy 
} from '@dnd-kit/sortable';
import { Button } from '../../../shared/components/ui/button';
import { DeliveryCard } from '../components/delivery-card';
import { Section } from '../../../shared/components/ui/section';
import { EmptyState } from '../../../shared/components/ui/empty-state';
import { CancelOrderModal } from '../components/cancel-order-modal';
import { useDeliveries } from '../hooks/use-deliveries';
import { useModalState } from '../../../shared/hooks/use-modal-state';
import type { Delivery, DeliveryStatus } from '../types';

const KANBAN_COLUMNS: { id: string; label: string; color: string; statuses: DeliveryStatus[] }[] = [
  { id: 'nueva', label: 'Nueva', color: 'bg-blue-100/30 border-blue-200', statuses: ['PENDIENTE', 'ASIGNADA'] },
  { id: 'en-camino', label: 'En Camino', color: 'bg-amber-100/30 border-amber-200', statuses: ['EN_CAMINO'] },
  { id: 'completado', label: 'Completado', color: 'bg-emerald-100/30 border-emerald-200', statuses: ['ENTREGADA'] },
  { id: 'cancelado', label: 'Cancelado', color: 'bg-rose-100/30 border-rose-200', statuses: ['CANCELADA'] }
];

export default function LogisticsDashboard() {
  const { deliveries, isLoading, isError, updateStatus, assignCourier } = useDeliveries();
  const [activeId, setActiveId] = useState<number | null>(null);
  const cancelModal = useModalState<Delivery>();

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const activeDelivery = useMemo(() => 
    deliveries.find(d => d.id === activeId), 
    [deliveries, activeId]
  );

  const handleDragStart = (event: DragStartEvent) => {
    setActiveId(event.active.id as number);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    setActiveId(null);

    if (!over) return;

    const deliveryId = active.id as number;
    const overId = over.id as string;

    const currentDelivery = deliveries.find(d => d.id === deliveryId);
    if (!currentDelivery) return;

    // Special case for dropping into Cancelado column: open modal first
    if (overId === 'cancelado' && currentDelivery.estado !== 'CANCELADA') {
      cancelModal.open(currentDelivery);
      return;
    }

    // Normal status updates
    let targetStatus: DeliveryStatus | null = null;
    if (overId === 'nueva') targetStatus = 'PENDIENTE';
    else if (overId === 'en-camino') targetStatus = 'EN_CAMINO';
    else if (overId === 'completado') targetStatus = 'ENTREGADA';
    else {
      const overItem = deliveries.find(d => d.id === (overId as unknown as number));
      if (overItem) targetStatus = overItem.estado;
    }

    if (targetStatus && currentDelivery.estado !== targetStatus) {
      updateStatus(deliveryId, targetStatus);
    }
  };

  const handleCancelConfirm = (reason: string, notes: string) => {
    if (cancelModal.data) {
      updateStatus(cancelModal.data.id, 'CANCELADA', reason, notes);
      cancelModal.close();
    }
  };

  const handleUpdateStatus = (id: number, newStatus: DeliveryStatus) => {
    updateStatus(id, newStatus);
  };

  const handleAssignCourier = (id: number) => {
    // TODO: Open modal to select courier
    assignCourier(id, 70);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader2 className="w-8 h-8 animate-spin text-stone-300" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="p-8 text-center bg-rose-50 border border-rose-100 rounded-2xl text-rose-600 font-medium text-sm">
        Error al cargar las entregas. Por favor, intenta de nuevo.
      </div>
    );
  }

  return (
    <Section title="Logística de Entregas">
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
      <DndContext
        sensors={sensors}
        collisionDetection={closestCorners}
        onDragStart={handleDragStart}
        onDragEnd={handleDragEnd}
      >
        <div className="flex overflow-x-auto pb-6 -mx-4 px-4 sm:mx-0 sm:px-0 gap-6 snap-x min-h-[600px]">
          {KANBAN_COLUMNS.map(col => {
            const columnDeliveries = deliveries.filter(d => col.statuses.includes(d.estado));

            return (
              <div key={col.id} className="min-w-[300px] w-[320px] shrink-0 snap-center flex flex-col">
                <div className="flex items-center justify-between mb-4 px-1">
                  <h3 className="font-bold text-stone-900 flex items-center gap-2 text-sm">
                    {col.label}
                    <span className="bg-white border border-stone-200 text-stone-600 text-[10px] py-0.5 px-2 rounded-full font-mono shadow-sm">
                      {columnDeliveries.length}
                    </span>
                  </h3>
                </div>

                <SortableContext
                  id={col.id}
                  items={columnDeliveries.map(d => d.id)}
                  strategy={verticalListSortingStrategy}
                >
                  <div 
                    id={col.id}
                    className={`flex-1 rounded-2xl p-3 flex flex-col gap-3 border border-dashed transition-colors ${col.color}`}
                  >
                    {columnDeliveries.map(delivery => (
                      <DeliveryCard
                        key={delivery.id}
                        delivery={delivery}
                        onUpdateStatus={handleUpdateStatus}
                        onAssignCourier={handleAssignCourier}
                        onCancel={(d) => cancelModal.open(d)}
                      />
                    ))}

                    {columnDeliveries.length === 0 && (
                      <EmptyState 
                        icon={PackageOpen}
                        title="Sin entregas"
                        className="bg-transparent border-stone-200/50"
                      />
                    )}
                  </div>
                </SortableContext>
              </div>
            );
          })}
        </div>

        <DragOverlay dropAnimation={{
          sideEffects: defaultDropAnimationSideEffects({
            styles: {
              active: {
                opacity: '0.5',
              },
            },
          }),
        }}>
          {activeDelivery ? (
            <div className="w-[294px]">
              <DeliveryCard
                delivery={activeDelivery}
                onUpdateStatus={handleUpdateStatus}
                onAssignCourier={handleAssignCourier}
                onCancel={() => {}}
              />
            </div>
          ) : null}
        </DragOverlay>
      </DndContext>

      {/* Cancellation Modal */}
      <CancelOrderModal
        isOpen={cancelModal.isOpen}
        orderNumber={cancelModal.data?.pedido.numeroPedido || ''}
        onClose={cancelModal.close}
        onConfirm={handleCancelConfirm}
      />
    </Section>
  );
}