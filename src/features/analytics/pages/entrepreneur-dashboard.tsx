import { motion } from 'framer-motion';
import { 
  TrendingUp, 
  Users, 
  ShoppingBag, 
  ArrowUpRight,
  Plus
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { cn } from '../../../shared/utils/cn';

interface StatCardProps {
  label: string;
  value: string;
  trend?: string;
  icon: any;
}

const StatCard = ({ label, value, trend, icon: Icon }: StatCardProps) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    className="bg-white p-8 border border-stone-100 flex flex-col justify-between"
  >
    <div className="flex justify-between items-start mb-6">
      <div className="p-3 bg-stone-50 text-stone-900 rounded-none">
        <Icon className="w-5 h-5" />
      </div>
      {trend && (
        <span className="flex items-center text-[10px] font-bold text-green-600 tracking-wider">
          <ArrowUpRight className="w-3 h-3 mr-1" />
          {trend}
        </span>
      )}
    </div>
    <div>
      <p className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 mb-1">{label}</p>
      <h3 className="text-3xl font-serif text-stone-900">{value}</h3>
    </div>
  </motion.div>
);

export default function EntrepreneurDashboard() {
  return (
    <div className="space-y-12">
      {/* Header Section */}
      <div className="flex flex-col md:flex-row md:items-end justify-between space-y-6 md:space-y-0">
        <div>
          <h1 className="font-serif text-5xl mb-4 text-stone-900">Buen día, Jane.</h1>
          <p className="text-stone-500 font-medium max-w-md">Esto es lo que está pasando hoy en tu tienda Maison.</p>
        </div>
        <div className="flex space-x-4">
          <Button variant="outline" className="px-6">Ver Tienda</Button>
          <Button className="px-6"><Plus className="w-4 h-4 mr-2" /> Nuevo Producto</Button>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard label="Ventas Totales" value="$12,840" trend="+12.5%" icon={TrendingUp} />
        <StatCard label="Pedidos Nuevos" value="24" trend="+8.2%" icon={ShoppingBag} />
        <StatCard label="Clientes Activos" value="1,120" trend="+4.1%" icon={Users} />
        <StatCard label="Conversión" value="3.2%" icon={TrendingUp} />
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
                      <span className={cn(
                        "inline-flex px-2 py-1 text-[10px] font-bold tracking-widest uppercase",
                        "bg-stone-100 text-stone-500"
                      )}>
                        Procesando
                      </span>
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
