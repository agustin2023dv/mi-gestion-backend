import { motion } from 'framer-motion';
import { Receipt, Calendar, Tag, MapPin, Edit2, Trash2 } from 'lucide-react';
import type { GastoOperativo, CategoriaGasto, CentroCosto } from '../types';
import { cn } from '../../../shared/utils/cn';

interface ExpenseCardProps {
  expense: GastoOperativo;
  categories: CategoriaGasto[];
  costCentres: CentroCosto[];
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

export function ExpenseCard({ expense, categories, costCentres, onEdit, onDelete }: ExpenseCardProps) {
  const category = categories.find(c => c.id === expense.categoriaGastoId);
  const costCentre = costCentres.find(cc => cc.id === expense.centroCostoId);

  return (
    <motion.div
      layout
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, scale: 0.95 }}
      whileHover={{ y: -4 }}
      className="group bg-white/80 backdrop-blur-sm border border-stone-200 rounded-2xl p-5 shadow-sm hover:shadow-xl hover:border-stone-300 transition-all duration-300"
    >
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center gap-3">
          <div className={cn(
            "w-12 h-12 rounded-xl flex items-center justify-center transition-colors duration-300",
            expense.esDirecto ? "bg-emerald-50 text-emerald-600" : "bg-indigo-50 text-indigo-600"
          )}>
            <Receipt className="w-6 h-6" />
          </div>
          <div>
            <h4 className="font-bold text-stone-900 leading-none mb-1 group-hover:text-stone-950 transition-colors">
              {expense.nombre}
            </h4>
            <div className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-widest text-stone-400">
              <Calendar className="w-3 h-3" />
              {new Date(expense.fechaRegistro).toLocaleDateString('es-AR', { day: 'numeric', month: 'short' })}
              <span className="w-1 h-1 rounded-full bg-stone-300" />
              {expense.periodicidad}
            </div>
          </div>
        </div>
        
        <div className="flex flex-col items-end">
          <span className="font-serif text-xl font-bold text-stone-900">
            ${expense.monto.toLocaleString('es-AR')}
          </span>
          <span className={cn(
            "text-[9px] font-black uppercase tracking-tighter px-1.5 py-0.5 rounded",
            expense.esProrrateable ? "bg-amber-100 text-amber-700" : "bg-stone-100 text-stone-500"
          )}>
            {expense.esProrrateable ? 'Prorrateable' : 'Fijo Directo'}
          </span>
        </div>
      </div>

      <div className="space-y-2 mb-4">
        {category && (
          <div className="flex items-center gap-2 text-xs text-stone-600 bg-stone-50 py-1.5 px-3 rounded-lg border border-stone-100">
            <Tag className="w-3.5 h-3.5 text-stone-400" />
            <span className="font-medium">{category.nombre}</span>
            <span className="ml-auto text-[10px] text-stone-400 uppercase font-bold">{category.tipoNaturaleza}</span>
          </div>
        )}
        {costCentre && (
          <div className="flex items-center gap-2 text-xs text-stone-600 bg-stone-50 py-1.5 px-3 rounded-lg border border-stone-100">
            <MapPin className="w-3.5 h-3.5 text-stone-400" />
            <span className="font-medium">{costCentre.nombre}</span>
            <span className="ml-auto text-[10px] text-stone-400 font-mono">[{costCentre.codigo}]</span>
          </div>
        )}
      </div>

      {expense.descripcion && (
        <p className="text-xs text-stone-500 leading-relaxed mb-4 line-clamp-2 italic">
          "{expense.descripcion}"
        </p>
      )}

      <div className="flex items-center gap-2 pt-4 border-t border-stone-100">
        <button 
          onClick={() => onEdit?.(expense.id)}
          className="flex-1 flex items-center justify-center gap-2 py-2 text-[10px] font-bold uppercase tracking-widest text-stone-500 hover:text-stone-900 hover:bg-stone-50 rounded-lg transition-all"
        >
          <Edit2 className="w-3.5 h-3.5" /> Editar
        </button>
        <button 
          onClick={() => onDelete?.(expense.id)}
          className="flex-1 flex items-center justify-center gap-2 py-2 text-[10px] font-bold uppercase tracking-widest text-red-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
        >
          <Trash2 className="w-3.5 h-3.5" /> Eliminar
        </button>
      </div>
    </motion.div>
  );
}
