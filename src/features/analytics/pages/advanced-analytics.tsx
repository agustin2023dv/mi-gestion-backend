import { Section } from '../../../shared/components/ui/section';
import { StatCard } from '../../../shared/components/ui/stat-card';
import { PeakHoursHeatmap } from '../components/peak-hours-heatmap';
import { DollarSign, ShoppingBag, Receipt, Users, BarChart3 } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, BarChart, Bar, Cell } from 'recharts';
import { ChartCard } from '../../../shared/components/charts/chart-card';
import { useChartTheme } from '../../../shared/hooks/use-chart-theme';

function EmptyChartState({ message }: { message: string }) {
  return (
    <div className="flex flex-col items-center justify-center h-full min-h-[200px] text-center p-6">
      <div className="w-14 h-14 bg-stone-50 rounded-full flex items-center justify-center mb-4">
        <BarChart3 className="w-7 h-7 text-stone-200" />
      </div>
      <p className="text-sm font-medium text-stone-400">{message}</p>
    </div>
  );
}

export default function AdvancedAnalytics() {
  const theme = useChartTheme();

  // TODO: Replace with real API calls (e.g., useQuery for analytics endpoints)
  const salesData: { name: string; sales: number }[] = [];
  const categoryData: { name: string; value: number }[] = [];

  return (
    <Section title="Analíticas Avanzadas">
      {/* KPIs — show $0 / 0 when no real data */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatCard label="Ingresos Totales" value="$0" icon={<DollarSign className="w-5 h-5" />} delay={0.1} />
        <StatCard label="Pedidos Totales" value="0" icon={<ShoppingBag className="w-5 h-5" />} delay={0.2} />
        <StatCard label="Ticket Promedio" value="$0" icon={<Receipt className="w-5 h-5" />} delay={0.3} />
        <StatCard label="Clientes Activos" value="0" icon={<Users className="w-5 h-5" />} delay={0.4} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
        {/* Main Chart */}
        <ChartCard
          title="Tendencia de Ingresos"
          subtitle="Últimos 7 días"
          className="lg:col-span-2"
          height={300}
        >
          {salesData.length > 0 ? (
            <AreaChart data={salesData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <defs>
                <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor={theme.primary} stopOpacity={0.3} />
                  <stop offset="95%" stopColor={theme.primary} stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke={theme.grid} />
              <XAxis dataKey="name" {...theme.xAxis} />
              <YAxis {...theme.yAxis} />
              <Tooltip {...theme.tooltip} />
              <Area type="monotone" dataKey="sales" stroke={theme.primary} strokeWidth={3} fillOpacity={1} fill="url(#colorSales)" />
            </AreaChart>
          ) : (
            <EmptyChartState message="No hay datos de ingresos disponibles." />
          )}
        </ChartCard>

        {/* Categories Chart */}
        <ChartCard
          title="Top Categorías"
          subtitle="Distribución de ventas"
          height={300}
        >
          {categoryData.length > 0 ? (
            <BarChart data={categoryData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke={theme.grid} />
              <XAxis type="number" hide />
              <YAxis dataKey="name" type="category" {...theme.yAxis} width={70} />
              <Tooltip cursor={{ fill: '#f5f5f4' }} {...theme.tooltip} />
              <Bar dataKey="value" radius={[0, 4, 4, 0]} barSize={24}>
                {categoryData.map((_entry, index) => (
                  <Cell key={`cell-${index}`} fill={theme.primary} fillOpacity={1 - index * 0.15} />
                ))}
              </Bar>
            </BarChart>
          ) : (
            <EmptyChartState message="No hay datos de categorías disponibles." />
          )}
        </ChartCard>
      </div>

      {/* Heatmap */}
      <PeakHoursHeatmap />
    </Section>
  );
}