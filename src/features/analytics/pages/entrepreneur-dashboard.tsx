import {
  TrendingUp,
  Users,
  ShoppingBag,
  Plus,
  Package
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { PageHeader } from '../../../shared/components/ui/page-header';
import { StatCard } from '../../../shared/components/ui/stat-card';
import { DataTable } from '../../../shared/components/ui/data-table';
import { StatusBadge } from '../../../shared/components/ui/status-badge';
import type { Column } from '../../../shared/components/ui/data-table';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../../shared/contexts/auth-context';
import { ordersApi } from '../../orders/api/orders';
import type { Order } from '../../orders/api/orders';
import { catalogApi } from '../../catalog/api/productos';

export default function EntrepreneurDashboard() {
  const { user } = useAuth();
  const userName = user?.nombre || 'Emprendedor';

  const { data: ordersData, isLoading: isLoadingOrders } = useQuery({
    queryKey: ['dashboard-orders'],
    queryFn: () => ordersApi.getOrders({ size: 5, sort: 'fechaPedido,desc' })
  });

  const { data: productsData, isLoading: isLoadingProducts } = useQuery({
    queryKey: ['dashboard-low-stock'],
    queryFn: () => catalogApi.getProducts({ size: 20, sort: 'stock,asc' })
  });


  const recentOrders = ordersData?.data?.content || [];
  const lowStockProducts = productsData?.data?.content?.filter(p => p.stock <= p.stockMinimo).slice(0, 5) || [];

  const columns: Column<Order>[] = [
    {
      header: 'ID',
      accessor: (order) => <span className="font-serif text-lg text-stone-900">#{order.numeroPedido || order.id}</span>,
    },
    {
      header: 'Cliente',
      accessor: (order) => (
        <div className="flex flex-col">
          <span className="text-sm font-bold text-stone-900">{order.cliente?.nombre} {order.cliente?.apellido}</span>
          <span className="text-[10px] text-stone-400 font-medium">
            {order.fechaPedido ? new Date(order.fechaPedido).toLocaleDateString() : 'N/A'}
          </span>
        </div>
      ),
    },
    {
      header: 'Estado',
      accessor: (order) => <StatusBadge status={order.estado} />,
    },
    {
      header: 'Total',
      align: 'right',
      accessor: (order) => <span className="font-serif text-lg text-stone-900">${(order.total || 0).toLocaleString()}</span>,
    },
  ];

  return (
    <div className="space-y-12">
      <PageHeader
        title={`Buen día, ${userName}.`}
        description="Esto es lo que está pasando hoy en tu tienda."
      >
        <Button className="px-6">
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Producto
        </Button>
      </PageHeader>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          label="Ventas Totales"
          value="$0"
          trend="+0%"
          icon={<TrendingUp className="w-5 h-5" />}
          index={0}
        />
        <StatCard
          label="Pedidos Nuevos"
          value="0"
          trend="+0%"
          icon={<ShoppingBag className="w-5 h-5" />}
          index={1}
        />
        <StatCard
          label="Clientes Activos"
          value="0"
          trend="+0%"
          icon={<Users className="w-5 h-5" />}
          index={2}
        />
        <StatCard
          label="Conversión"
          value="0%"
          icon={<TrendingUp className="w-5 h-5" />}
          index={3}
        />
      </div>

      {/* Content Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
        {/* Recent Orders */}
        <div className="lg:col-span-2 space-y-8">
          <div className="flex items-center justify-between">
            <h2 className="font-serif text-3xl text-stone-900">Pedidos Recientes</h2>
            <button className="text-xs font-bold tracking-widest uppercase underline underline-offset-4 text-stone-400 hover:text-stone-900 transition-colors">
              Ver todos
            </button>
          </div>

          <DataTable
            columns={columns}
            data={recentOrders}
            isLoading={isLoadingOrders}
            filterButton={false}
            className="space-y-0"
            emptyState={{
              title: 'Sin pedidos recientes',
              description: 'Los nuevos pedidos aparecerán aquí.',
              icon: <Package className="w-8 h-8 text-stone-200" />
            }}
          />
        </div>

        {/* Inventory Alert */}
        <div className="space-y-8">
          <h2 className="font-serif text-3xl text-stone-900">Stock Bajo</h2>
          <div className="space-y-4">
            {isLoadingProducts ? (
              <p className="text-sm text-stone-500">Cargando...</p>
            ) : lowStockProducts.length > 0 ? (
              lowStockProducts.map((product) => (
                <div key={product.id} className="p-6 bg-white border border-stone-100 flex items-center space-x-4">
                  <div className="w-12 h-12 bg-stone-50 rounded-none overflow-hidden">
                    {product.imagenUrl && <img src={product.imagenUrl} alt={product.nombre} className="w-full h-full object-cover" />}
                  </div>
                  <div className="flex-1">
                    <h4 className="text-sm font-bold text-stone-900 uppercase tracking-wide">
                      {product.nombre}
                    </h4>
                    <p className="text-[10px] text-red-500 font-bold tracking-widest uppercase mt-1">
                      {product.stock} unidades restantes
                    </p>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-sm text-stone-500">El stock está en niveles óptimos.</p>
            )}
          </div>
          <Button variant="secondary" className="w-full">
            Gestionar Inventario
          </Button>
        </div>
      </div>
    </div>
  );
}