import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { 
  Search, 
  Clock, 
  CheckCircle2, 
  Truck, 
  AlertCircle,
  MoreVertical,
  ExternalLink
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import OrderDetail from '../components/order-detail';

const MOCK_ORDERS = [
  { id: '1025', client: 'Alex Rivarola', date: '25 Abr, 14:30', total: 4500, status: 'PENDING', items: 3 },
  { id: '1024', client: 'Maria Garcia', date: '25 Abr, 12:15', total: 8900, status: 'READY', items: 5 },
  { id: '1023', client: 'Juan Perez', date: '24 Abr, 18:45', total: 1200, status: 'SHIPPED', items: 1 },
  { id: '1022', client: 'Lucia Fernandez', date: '24 Abr, 15:20', total: 3400, status: 'DELIVERED', items: 2 },
  { id: '1021', client: 'Carlos Sosa', date: '24 Abr, 10:05', total: 7200, status: 'CANCELLED', items: 4 },
];

const STATUS_CONFIG: any = {
  PENDING: { label: 'Pendiente', color: 'text-stone-400 bg-stone-50', icon: Clock },
  READY: { label: 'Preparado', color: 'text-blue-600 bg-blue-50', icon: CheckCircle2 },
  SHIPPED: { label: 'En Camino', color: 'text-orange-600 bg-orange-50', icon: Truck },
  DELIVERED: { label: 'Entregado', color: 'text-green-600 bg-green-50', icon: CheckCircle2 },
  CANCELLED: { label: 'Cancelado', color: 'text-red-600 bg-red-50', icon: AlertCircle },
};

export default function OrderList() {
  const [filter, setFilter] = useState('ALL');
  const [selectedOrder, setSelectedOrder] = useState<any>(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);

  const handleOpenDetail = (order: any) => {
    setSelectedOrder(order);
    setIsDetailOpen(true);
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="font-serif text-4xl text-stone-900">Pedidos</h1>
          <p className="text-stone-500 font-medium">Gestiona y haz seguimiento de tus ventas.</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm">Exportar CSV</Button>
        </div>
      </div>

      {/* Tabs / Filters */}
      <div className="flex border-b border-stone-200 overflow-x-auto no-scrollbar">
        {['ALL', 'PENDING', 'READY', 'SHIPPED', 'DELIVERED'].map((f) => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={`px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase transition-all relative whitespace-nowrap ${
              filter === f ? 'text-stone-900' : 'text-stone-400 hover:text-stone-600'
            }`}
          >
            {f === 'ALL' ? 'Todos' : STATUS_CONFIG[f].label}
            {filter === f && (
              <motion.div layoutId="order-filter-tab" className="absolute bottom-0 left-0 right-0 h-0.5 bg-stone-900" />
            )}
          </button>
        ))}
      </div>

      {/* Search & Actions */}
      <div className="flex flex-col md:flex-row gap-4 items-center">
        <div className="relative flex-1 w-full">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-stone-400" />
          <input 
            type="text" 
            placeholder="Buscar por cliente o ID de pedido..." 
            className="w-full pl-10 pr-4 py-3 bg-white border border-stone-100 focus:border-stone-900 text-sm outline-none transition-all"
          />
        </div>
      </div>

      {/* Orders Table */}
      <div className="bg-white border border-stone-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-stone-100 bg-stone-50/50">
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">ID / Fecha</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Cliente</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Artículos</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Estado</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 text-right">Total</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 text-right"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-stone-50">
              {MOCK_ORDERS.filter(o => filter === 'ALL' || o.status === filter).map((order) => {
                const StatusIcon = STATUS_CONFIG[order.status].icon;
                return (
                  <tr key={order.id} className="group hover:bg-stone-50/30 transition-colors">
                    <td className="px-6 py-6">
                      <div className="flex flex-col">
                        <span className="font-serif text-lg text-stone-900">#{order.id}</span>
                        <span className="text-[10px] text-stone-400 font-bold uppercase tracking-wider">{order.date}</span>
                      </div>
                    </td>
                    <td className="px-6 py-6">
                      <span className="text-sm font-bold text-stone-900 uppercase tracking-wide">{order.client}</span>
                    </td>
                    <td className="px-6 py-6 text-xs font-medium text-stone-500">
                      {order.items} {order.items === 1 ? 'artículo' : 'artículos'}
                    </td>
                    <td className="px-6 py-6">
                      <div className={`inline-flex items-center px-3 py-1.5 ${STATUS_CONFIG[order.status].color}`}>
                        <StatusIcon className="w-3 h-3 mr-2" />
                        <span className="text-[10px] font-bold tracking-widest uppercase">
                          {STATUS_CONFIG[order.status].label}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-6 text-right font-serif text-lg text-stone-900">
                      ${order.total.toLocaleString()}
                    </td>
                    <td className="px-6 py-6 text-right">
                      <div className="flex justify-end space-x-2">
                        <button 
                          onClick={() => handleOpenDetail(order)}
                          title="Ver detalles" 
                          className="p-2 text-stone-400 hover:text-stone-900 transition-colors"
                        >
                          <ExternalLink className="w-4 h-4" />
                        </button>
                        <button className="p-2 text-stone-400 hover:text-stone-900 transition-colors">
                          <MoreVertical className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      <OrderDetail 
        isOpen={isDetailOpen} 
        onClose={() => setIsDetailOpen(false)} 
        order={selectedOrder} 
      />
    </div>
  );
}
