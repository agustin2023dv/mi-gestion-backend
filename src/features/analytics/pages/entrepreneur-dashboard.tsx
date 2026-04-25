import { 
  TrendingUp, 
  Users, 
  ShoppingBag, 
  Plus
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { PageHeader } from '../../../shared/components/ui/page-header';
import { StatCard } from '../../../shared/components/ui/stat-card';
import { Badge } from '../../../shared/components/ui/badge';

export default function EntrepreneurDashboard() {
  return (
    <div className="space-y-12">
      <PageHeader 
        title="Buen día, Jane." 
        description="Esto es lo que está pasando hoy en tu tienda Maison."
      >
        <Button variant="outline" className="px-6">Ver Tienda</Button>
        <Button className="px-6"><Plus className="w-4 h-4 mr-2" /> Nuevo Producto</Button>
      </PageHeader>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard label="Ventas Totales" value="$12,840" trend="+12.5%" icon={TrendingUp} index={0} />
        <StatCard label="Pedidos Nuevos" value="24" trend="+8.2%" icon={ShoppingBag} index={1} />
        <StatCard label="Clientes Activos" value="1,120" trend="+4.1%" icon={Users} index={2} />
        <StatCard label="Conversión" value="3.2%" icon={TrendingUp} index={3} />
      </div>

      {/* Content Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
        {/* Recent Orders */}
        <div className="lg:col-span-2 space-y-8">
          <div className="flex items-center justify-between">
            <h2 className="font-serif text-3xl text-stone-900">Pedidos Recientes</h2>
            <button className="text-xs font-bold tracking-widest uppercase underline underline-offset-4 text-stone-400 hover:text-stone-900 transition-colors">Ver todos</button>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-stone-100">
                  <th className="py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">ID</th>
                  <th className="py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Cliente</th>
                  <th className="py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Estado</th>
                  <th className="py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 text-right">Total</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-stone-50">
                {[1, 2, 3, 4, 5].map((i) => (
                  <tr key={i} className="group hover:bg-stone-50/50 transition-colors cursor-pointer">
                    <td className="py-5 font-serif text-lg text-stone-900">#102{i}</td>
                    <td className="py-5">
                      <div className="flex flex-col">
                        <span className="text-sm font-bold text-stone-900">Alex Rivarola</span>
                        <span className="text-[10px] text-stone-400 font-medium">hace 2 horas</span>
                      </div>
                    </td>
                    <td className="py-5">
                      <Badge>Procesando</Badge>
                    </td>
                    <td className="py-5 text-right font-serif text-lg text-stone-900">$2,450</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Inventory Alert */}
        <div className="space-y-8">
          <h2 className="font-serif text-3xl text-stone-900">Stock Bajo</h2>
          <div className="space-y-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="p-6 bg-white border border-stone-100 flex items-center space-x-4">
                <div className="w-12 h-12 bg-stone-50 rounded-none"></div>
                <div className="flex-1">
                  <h4 className="text-sm font-bold text-stone-900 uppercase tracking-wide">Vela Minimalista #{i}</h4>
                  <p className="text-[10px] text-red-500 font-bold tracking-widest uppercase mt-1">2 unidades restantes</p>
                </div>
              </div>
            ))}
          </div>
          <Button variant="secondary" className="w-full">Gestionar Inventario</Button>
        </div>
      </div>
    </div>
  );
}
