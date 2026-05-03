import { MapPin, Phone, User, Clock, Truck, GripVertical, Printer, XCircle } from 'lucide-react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { Button } from '../../../shared/components/ui/button';
import { StatusBadge } from '../../../shared/components/ui/status-badge';
import { printOrderReceipt } from '../../../shared/utils/print-order';
import type { Delivery, DeliveryStatus } from '../types';

interface DeliveryCardProps {
  delivery: Delivery;
  onUpdateStatus: (id: number, newStatus: DeliveryStatus) => void;
  onAssignCourier: (id: number) => void;
  onCancel: (delivery: Delivery) => void;
}

export function DeliveryCard({ delivery, onUpdateStatus, onAssignCourier, onCancel }: DeliveryCardProps) {
  const { pedido, estado, repartidor } = delivery;

  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({
    id: delivery.id,
    data: {
      type: 'Delivery',
      delivery,
    },
  });

  const style = {
    transform: CSS.Translate.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  const handlePrint = () => {
    printOrderReceipt(delivery);
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className="group bg-white border border-stone-200 rounded-xl shadow-sm p-4 hover:shadow-md transition-shadow flex flex-col gap-3 relative"
    >
      {/* Drag Handle */}
      <div 
        {...attributes} 
        {...listeners}
        className="absolute top-4 right-2 p-1 text-stone-300 hover:text-stone-500 cursor-grab active:cursor-grabbing opacity-0 group-hover:opacity-100 transition-opacity"
      >
        <GripVertical className="w-4 h-4" />
      </div>

      {/* Header */}
      <div className="flex items-center justify-between pr-6">
        <div className="flex items-center gap-1">
          <span className="font-mono text-[10px] font-bold text-stone-500 bg-stone-100 px-2 py-0.5 rounded mr-1">
            #{pedido.numeroPedido}
          </span>
          <button 
            onClick={(e) => {
              e.stopPropagation();
              handlePrint();
            }}
            className="p-1 text-stone-400 hover:text-stone-900 hover:bg-stone-100 rounded transition-colors"
            title="Imprimir Comanda"
          >
            <Printer className="w-3.5 h-3.5" />
          </button>
          {estado !== 'CANCELADA' && estado !== 'ENTREGADA' && (
            <button 
              onClick={(e) => {
                e.stopPropagation();
                onCancel(delivery);
              }}
              className="p-1 text-stone-400 hover:text-rose-600 hover:bg-rose-50 rounded transition-colors"
              title="Cancelar Pedido"
            >
              <XCircle className="w-3.5 h-3.5" />
            </button>
          )}
        </div>
        <StatusBadge status={estado} />
      </div>

      {/* Customer Info */}
      <div>
        <h4 className="font-bold text-stone-900 text-sm flex items-center gap-1.5 mb-1">
          <User className="w-3.5 h-3.5 text-stone-400" />
          {pedido.cliente.nombre} {pedido.cliente.apellido}
        </h4>
        <div className="text-stone-500 text-xs flex items-start gap-1.5 leading-tight mb-1">
          <MapPin className="w-3.5 h-3.5 shrink-0 mt-0.5 text-stone-400" />
          <span className="line-clamp-1">{pedido.direccionEntrega.calle} {pedido.direccionEntrega.numero}</span>
        </div>
        <div className="text-stone-500 text-xs flex items-center gap-1.5">
          <Phone className="w-3.5 h-3.5 text-stone-400" />
          <span>{pedido.cliente.telefono}</span>
        </div>
      </div>

      {/* Order Details */}
      <div className="bg-stone-50 rounded-lg p-3 flex justify-between items-center border border-stone-100">
        <div className="flex flex-col">
          <span className="text-[10px] uppercase text-stone-400 font-bold tracking-wider">Total</span>
          <span className="font-serif font-bold text-stone-900">${pedido.total}</span>
        </div>
        <div className="flex flex-col items-end">
          <span className="text-[10px] uppercase text-stone-400 font-bold tracking-wider">Pago</span>
          <span className="text-xs font-medium text-stone-700 capitalize">{pedido.metodoPago}</span>
        </div>
      </div>

      {/* Courier Assignment */}
      {repartidor ? (
        <div className="flex items-center gap-2 mt-1">
          <div className="w-6 h-6 rounded-full bg-stone-200 flex items-center justify-center">
            <Truck className="w-3.5 h-3.5 text-stone-500" />
          </div>
          <span className="text-xs text-stone-600 font-medium truncate">{repartidor.nombre} {repartidor.apellido}</span>
        </div>
      ) : (
        <div className="text-xs text-amber-600 font-medium flex items-center gap-1 mt-1">
          <Clock className="w-3.5 h-3.5" />
          Esperando repartidor
        </div>
      )}

      {/* Actions */}
      <div className="pt-2 mt-1 border-t border-stone-100 grid gap-2">
        {estado === 'PENDIENTE' && (
          <Button onClick={() => onAssignCourier(delivery.id)} size="sm" className="w-full text-xs">
            Asignar Repartidor
          </Button>
        )}
        {estado === 'ASIGNADA' && (
          <Button onClick={() => onUpdateStatus(delivery.id, 'EN_CAMINO')} variant="outline" size="sm" className="w-full text-xs">
            Marcar En Camino
          </Button>
        )}
        {estado === 'EN_CAMINO' && (
          <Button onClick={() => onUpdateStatus(delivery.id, 'ENTREGADA')} size="sm" className="w-full text-xs bg-emerald-600 hover:bg-emerald-700 text-white">
            Confirmar Entrega
          </Button>
        )}
        {estado === 'ENTREGADA' && (
          <Button variant="outline" size="sm" className="w-full text-xs">
            Ver Comprobante
          </Button>
        )}
      </div>
    </div>
  );
}
