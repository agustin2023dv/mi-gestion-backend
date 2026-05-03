import { cn } from '../../utils/cn';

export type StatusType = 
  | 'PENDING' | 'READY' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' // Orders
  | 'VIP' | 'Recurrente' | 'Nuevo' // Customers
  | 'AVAILABLE' | 'OUT_OF_STOCK' | 'LOW_STOCK' // Inventory
  | string;

interface StatusBadgeProps {
  status: StatusType;
  className?: string;
}

const STATUS_STYLES: Record<string, string> = {
  // Orders
  PENDING: 'bg-stone-50 text-stone-600 border-stone-200',
  READY: 'bg-blue-50 text-blue-700 border-blue-200',
  SHIPPED: 'bg-amber-50 text-amber-700 border-amber-200',
  DELIVERED: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  CANCELLED: 'bg-rose-50 text-rose-700 border-rose-200',
  
  // Logistics
  PENDIENTE: 'bg-blue-50 text-blue-700 border-blue-200',
  ASIGNADA: 'bg-indigo-50 text-indigo-700 border-indigo-200',
  EN_CAMINO: 'bg-amber-50 text-amber-700 border-amber-200',
  ENTREGADA: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  CANCELADA: 'bg-rose-50 text-rose-700 border-rose-200',

  // Customers
  VIP: 'bg-stone-900 text-white border-stone-900',
  Recurrente: 'bg-blue-50 text-blue-700 border-blue-200',
  Nuevo: 'bg-stone-50 text-stone-600 border-stone-200',
  // Inventory
  AVAILABLE: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  OUT_OF_STOCK: 'bg-rose-50 text-rose-700 border-rose-200',
  LOW_STOCK: 'bg-amber-50 text-amber-700 border-amber-200',
};

const STATUS_LABELS: Record<string, string> = {
  PENDING: 'Pendiente',
  READY: 'Preparado',
  SHIPPED: 'En Camino',
  DELIVERED: 'Entregado',
  CANCELLED: 'Cancelado',
  PENDIENTE: 'Nueva',
  ASIGNADA: 'Asignada',
  EN_CAMINO: 'En Camino',
  ENTREGADA: 'Completado',
  CANCELADA: 'Cancelado',
  VIP: 'VIP',
  Recurrente: 'Recurrente',
  Nuevo: 'Nuevo',
  AVAILABLE: 'Disponible',
  OUT_OF_STOCK: 'Agotado',
  LOW_STOCK: 'Stock Bajo',
};

export function StatusBadge({ status, className }: StatusBadgeProps) {
  const style = STATUS_STYLES[status] || 'bg-stone-50 text-stone-600 border-stone-200';
  const label = STATUS_LABELS[status] || status;

  return (
    <span className={cn(
      "text-[9px] font-bold uppercase tracking-widest px-2 py-1 rounded-full border",
      style,
      className
    )}>
      {label}
    </span>
  );
}
