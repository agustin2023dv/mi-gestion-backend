import { useState } from 'react';
import { motion } from 'framer-motion';
import { useQuery } from '@tanstack/react-query';
import {
  MoreVertical,
  ExternalLink,
  Package,
  Loader2
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import OrderDetail from '../components/order-detail';
import { DataTable, type Column } from '../../../shared/components/ui/data-table';
import { StatusBadge } from '../../../shared/components/ui/status-badge';
import { ordersApi } from '../api/orders';

const TABS = [
  { id: 'ALL', label: 'Todos' },
  { id: 'PENDING', label: 'Pendientes' },
  { id: 'READY', label: 'Preparados' },
  { id: 'SHIPPED', label: 'En Camino' },
  { id: 'DELIVERED', label: 'Entregados' },
];

import { useDebounce } from '../../../shared/hooks/use-debounce';

export default function OrderList() {
  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 500);
  const [selectedOrder, setSelectedOrder] = useState<any>(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [page, setPage] = useState(0);
  const pageSize = 20;

  const { data, isLoading, isError } = useQuery({
    queryKey: ['orders', filter, debouncedSearch, page],
    queryFn: () => ordersApi.getOrders({ 
      page,
      size: pageSize, 
      status: filter === 'ALL' ? undefined : filter,
      search: debouncedSearch
    }),
  });

  const handleOpenDetail = (order: any) => {
    setSelectedOrder(order);
    setIsDetailOpen(true);
  };

  const rawOrders = data?.data?.content || [];
  const totalElements = data?.data?.totalElements || 0;
  const totalPages = data?.data?.totalPages || 0;
  
  // Map backend orders to UI format if necessary
  const orders = rawOrders.map(o => ({
    ...o,
    client: `${o.cliente?.nombre || 'Cliente'} ${o.cliente?.apellido || ''}`,
    date: new Date(o.fechaPedido).toLocaleDateString('es-AR', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }),
    items: o.items?.length || 0,
    status: o.estado
  }));

  const columns: Column<any>[] = [
    {
      header: 'ID / Fecha',
      accessor: (order) => (
        <div className="flex flex-col">
          <span className="font-serif text-lg text-stone-900">#{order.numeroPedido}</span>
          <span className="text-[10px] text-stone-400 font-bold uppercase tracking-wider">{order.date}</span>
        </div>
      ),
    },
    {
      header: 'Cliente',
      accessor: (order) => <span className="text-sm font-bold text-stone-900 uppercase tracking-wide">{order.client}</span>,
    },
    {
      header: 'Artículos',
      accessor: (order) => <span className="text-xs font-medium text-stone-500">{order.items} {order.items === 1 ? 'artículo' : 'artículos'}</span>,
    },
    {
      header: 'Estado',
      accessor: (order) => <StatusBadge status={order.status} />,
    },
    {
      header: 'Total',
      align: 'right',
      accessor: (order) => <span className="font-serif text-lg text-stone-900">${order.total.toLocaleString()}</span>,
    },
    {
      header: '',
      align: 'right',
      accessor: (order) => (
        <div className="flex justify-end space-x-2">
          <button
            onClick={(e) => { e.stopPropagation(); handleOpenDetail(order); }}
            title="Ver detalles"
            className="p-2 text-stone-400 hover:text-stone-900 transition-colors"
          >
            <ExternalLink className="w-4 h-4" />
          </button>
          <button className="p-2 text-stone-400 hover:text-stone-900 transition-colors">
            <MoreVertical className="w-4 h-4" />
          </button>
        </div>
      ),
    },
  ];

  if (isError) {
    return (
      <div className="p-8 text-center bg-red-50 border border-red-100 rounded-2xl text-red-600">
        Error al cargar los pedidos. Por favor, intenta de nuevo.
      </div>
    );
  }

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

      {/* Tabs */}
      <div className="flex border-b border-stone-200 overflow-x-auto no-scrollbar">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setFilter(tab.id)}
            className={`px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase transition-all relative whitespace-nowrap ${filter === tab.id ? 'text-stone-900' : 'text-stone-400 hover:text-stone-600'
              }`}
          >
            {tab.label}
            {filter === tab.id && (
              <motion.div layoutId="order-filter-tab" className="absolute bottom-0 left-0 right-0 h-0.5 bg-stone-900" />
            )}
          </button>
        ))}
      </div>

      <DataTable
        columns={columns}
        data={orders}
        isLoading={isLoading}
        onRowClick={handleOpenDetail}
        searchPlaceholder="Buscar por cliente o ID de pedido..."
        onSearch={setSearchTerm}
        searchValue={searchTerm}
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={setPage}
        emptyState={{
          title: 'No encontramos pedidos',
          description: 'Intenta ajustar tus filtros o buscar con otros términos.',
          icon: <Package className="w-8 h-8 text-stone-200" />
        }}
      />

      <OrderDetail
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        order={selectedOrder}
      />
    </div>
  );
}

