import { MapPin, Phone, User, Clock, Truck } from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { cn } from '../../../shared/utils/cn';
import type { Delivery } from '../types';

interface DeliveryCardProps {
  delivery: Delivery;
  onUpdateStatus: (id: number, newStatus: string) => void;
  onAssignCourier: (id: number) => void;
}

export function DeliveryCard({ delivery, onUpdateStatus, onAssignCourier }: DeliveryCardProps) {
  console.log('[DeliveryCard] Rendering delivery card for ID:', delivery?.id, 'Delivery Data:', delivery);
  const { pedido, estado, repartidor } = delivery;

  const stateColors: Record<string, string> = {
    'PENDIENTE': 'bg-amber-100 text-amber-800 border-amber-200',
    'ASIGNADA': 'bg-blue-100 text-blue-800 border-blue-200',
    'EN_CAMINO': 'bg-indigo-100 text-indigo-800 border-indigo-200',
    'ENTREGADA': 'bg-emerald-100 text-emerald-800 border-emerald-200',
  };

  const colorClass = stateColors[estado] || 'bg-stone-100 text-stone-800 border-stone-200';

  return (
    <div className="bg-white border border-stone-200 rounded-xl shadow-sm p-4 hover:shadow-md transition-shadow flex flex-col gap-3">
      {/* Header */}
      <div className="flex items-center justify-between">
        <span className="font-mono text-xs font-bold text-stone-500 bg-stone-100 px-2 py-1 rounded">
          {pedido.numeroPedido}
        </span>
        <span className={cn("text-[10px] font-bold uppercase tracking-wider px-2 py-1 rounded-full border", colorClass)}>
          {estado}
        </span>
      </div>

      {/* Customer Info */}
      <div>
        <h4 className="font-bold text-stone-900 text-sm flex items-center gap-1.5 mb-1">
          <User className="w-3.5 h-3.5 text-stone-400" />
          {pedido.cliente.nombre} {pedido.cliente.apellido}
        </h4>
        <div className="text-stone-500 text-xs flex items-start gap-1.5 leading-tight mb-1">
          <MapPin className="w-3.5 h-3.5 shrink-0 mt-0.5 text-stone-400" />
          <span>{pedido.direccionEntrega.calle} {pedido.direccionEntrega.numero}, {pedido.direccionEntrega.ciudad}</span>
        </div>
        <div className="text-stone-500 text-xs flex items-center gap-1.5">
          <Phone className="w-3.5 h-3.5 text-stone-400" />
          <span>{pedido.cliente.telefono}</span>
        </div>
      </div>

      {/* Order Details */}
      <div className="bg-stone-50 rounded-lg p-3 flex justify-between items-center border border-stone-100">
        <div className="flex flex-col">
          <span className="text-[10px] uppercase text-stone-400 font-bold tracking-wider">Total a Cobrar</span>
          <span className="font-serif font-bold text-stone-900">${pedido.total}</span>
        </div>
        <div className="flex flex-col items-end">
          <span className="text-[10px] uppercase text-stone-400 font-bold tracking-wider">Pago</span>
          <span className="text-xs font-medium text-stone-700 capitalize">{pedido.metodoPago}</span>
        </div>
      </div>

      {/* Courier Assignment */}
      {repartidor ? (
        <div className="flex items-center gap-2 mt-2">
          <div className="w-6 h-6 rounded-full bg-stone-200 flex items-center justify-center">
            <Truck className="w-3.5 h-3.5 text-stone-500" />
          </div>
          <span className="text-xs text-stone-600 font-medium">{repartidor.nombre} {repartidor.apellido}</span>
        </div>
      ) : (
        <div className="text-xs text-amber-600 font-medium flex items-center gap-1 mt-2">
          <Clock className="w-3.5 h-3.5" />
          Esperando repartidor
        </div>
      )}

      {/* Actions */}
      <div className="pt-2 mt-2 border-t border-stone-100 grid gap-2">
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
