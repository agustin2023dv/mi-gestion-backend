import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Plus, 
  Search, 
  Filter, 
  TrendingUp, 
  TrendingDown, 
  DollarSign, 
  PieChart, 
  Loader2,
  AlertCircle
} from 'lucide-react';
import { Section } from '../../../shared/components/ui/section';
import { Button } from '../../../shared/components/ui/button';
import { financeApi } from '../api/gastos';
import { ExpenseCard } from '../components/expense-card';
import { cn } from '../../../shared/utils/cn';

export default function ExpensesDashboard() {
  const queryClient = useQueryClient();
  const [searchTerm, setSearchTerm] = useState('');
  const [filterOpen, setFilterOpen] = useState(false);

  // Queries
  const { data: expensesData, isLoading: loadingExpenses, isError: errorExpenses } = useQuery({
    queryKey: ['expenses'],
    queryFn: () => financeApi.getExpenses({ size: 100 }),
  });

  const { data: categoriesData } = useQuery({
    queryKey: ['expense-categories'],
    queryFn: () => financeApi.getCategories(),
  });

  const { data: costCentresData } = useQuery({
    queryKey: ['cost-centres'],
    queryFn: () => financeApi.getCostCentres(),
  });

  // Mutations
  const deleteMutation = useMutation({
    mutationFn: (id: number) => financeApi.deleteExpense(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['expenses'] });
    },
  });

  const expenses = expensesData?.data?.content || [];
  const categories = categoriesData?.data || [];
  const costCentres = costCentresData?.data || [];

  // Derived State
  const filteredExpenses = expenses.filter(e => 
    e.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    e.descripcion?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalSpend = expenses.reduce((acc, curr) => acc + curr.monto, 0);
  const directSpend = expenses.filter(e => e.esDirecto).reduce((acc, curr) => acc + curr.monto, 0);
  const indirectSpend = totalSpend - directSpend;

  if (loadingExpenses) {
    return (
      <div className="flex flex-col items-center justify-center h-[60vh] gap-4">
        <Loader2 className="w-12 h-12 animate-spin text-stone-300" />
        <p className="text-stone-400 font-medium animate-pulse uppercase tracking-[0.2em] text-xs">
          Sincronizando registros financieros...
        </p>
      </div>
    );
  }

  if (errorExpenses) {
    return (
      <div className="flex flex-col items-center justify-center h-[60vh] gap-6 text-center max-w-md mx-auto">
        <div className="w-16 h-16 bg-red-50 rounded-full flex items-center justify-center">
          <AlertCircle className="w-8 h-8 text-red-500" />
        </div>
        <div>
          <h3 className="text-xl font-serif text-stone-900 mb-2">Error de Conexión</h3>
          <p className="text-stone-500 text-sm">No pudimos recuperar los datos financieros. Por favor, verifica tu conexión o intenta más tarde.</p>
        </div>
        <Button onClick={() => queryClient.invalidateQueries({ queryKey: ['expenses'] })} variant="outline">
          Reintentar Conexión
        </Button>
      </div>
    );
  }

  return (
    <Section title="Gestión de Gastos">
      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <KPICard 
          label="Gasto Total" 
          value={`$${totalSpend.toLocaleString('es-AR')}`} 
          icon={<DollarSign className="w-5 h-5" />}
          color="bg-stone-900 text-white"
        />
        <KPICard 
          label="Costos Directos" 
          value={`$${directSpend.toLocaleString('es-AR')}`} 
          icon={<TrendingUp className="w-5 h-5" />}
          color="bg-emerald-50 text-emerald-900 border border-emerald-100"
        />
        <KPICard 
          label="Gastos Indirectos" 
          value={`$${indirectSpend.toLocaleString('es-AR')}`} 
          icon={<PieChart className="w-5 h-5" />}
          color="bg-indigo-50 text-indigo-900 border border-indigo-100"
        />
      </div>

      {/* Toolbar */}
      <div className="flex flex-col lg:flex-row gap-4 justify-between items-center mb-8 sticky top-0 z-20 bg-stone-50/80 backdrop-blur-md py-4 -mx-4 px-4">
        <div className="relative flex-1 w-full max-w-lg">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-stone-400" />
          <input
            type="text"
            placeholder="Buscar por concepto o descripción..."
            className="w-full pl-12 pr-4 py-3 bg-white border border-stone-200 rounded-2xl focus:ring-2 focus:ring-stone-900 focus:border-stone-900 outline-none transition-all shadow-sm text-sm"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        
        <div className="flex items-center gap-3 w-full lg:w-auto">
          <Button 
            variant="outline" 
            onClick={() => setFilterOpen(!filterOpen)}
            className={cn("gap-2 rounded-2xl px-6", filterOpen && "bg-stone-100")}
          >
            <Filter className="w-4 h-4" />
            Filtros Avanzados
          </Button>
          <Button className="gap-2 rounded-2xl px-6 bg-stone-900 shadow-lg shadow-stone-200 hover:shadow-xl hover:-translate-y-0.5 transition-all">
            <Plus className="w-4 h-4" />
            Nuevo Gasto
          </Button>
        </div>
      </div>

      {/* Grid */}
      <AnimatePresence mode="popLayout">
        {filteredExpenses.length > 0 ? (
          <motion.div 
            layout
            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
          >
            {filteredExpenses.map((expense) => (
              <ExpenseCard
                key={expense.id}
                expense={expense}
                categories={categories}
                costCentres={costCentres}
                onDelete={(id) => deleteMutation.mutate(id)}
              />
            ))}
          </motion.div>
        ) : (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="flex flex-col items-center justify-center py-24 bg-white/40 border border-dashed border-stone-200 rounded-3xl"
          >
            <div className="w-16 h-16 bg-stone-50 rounded-full flex items-center justify-center mb-4">
              <Receipt className="w-8 h-8 text-stone-200" />
            </div>
            <h3 className="text-stone-900 font-bold">Sin resultados</h3>
            <p className="text-stone-400 text-sm">No encontramos gastos que coincidan con tu búsqueda.</p>
          </motion.div>
        )}
      </AnimatePresence>
    </Section>
  );
}

function KPICard({ label, value, trend, icon, color }: any) {
  const isPositive = trend ? trend.startsWith('+') : false;
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      className={cn("p-6 rounded-3xl shadow-sm relative overflow-hidden", color)}
    >
      <div className="flex justify-between items-start mb-4">
        <div className="p-2 bg-white/10 rounded-xl backdrop-blur-md">
          {icon}
        </div>
        {trend && (
          <div className={cn(
            "flex items-center gap-1 text-[10px] font-black px-2 py-0.5 rounded-full",
            isPositive ? "bg-red-500/10 text-red-500" : "bg-emerald-500/10 text-emerald-500",
            color?.includes('bg-stone-900') && "bg-white/10 text-white"
          )}>
            {isPositive ? <TrendingUp className="w-3 h-3" /> : <TrendingDown className="w-3 h-3" />}
            {trend}
          </div>
        )}
      </div>
      <div>
        <p className="text-xs font-bold uppercase tracking-widest opacity-60 mb-1">{label}</p>
        <h3 className="text-3xl font-serif font-bold tracking-tight">{value}</h3>
      </div>
      
      {/* Decorative background element */}
      <div className="absolute -right-4 -bottom-4 w-24 h-24 bg-white/5 rounded-full blur-2xl" />
    </motion.div>
  );
}

function Receipt(props: any) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2l-2 1-2-1-2 1-2-1-2 1-2-1-2 1-2-1Z" />
      <path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8" />
      <path d="M12 17.5V6.5" />
    </svg>
  );
}
