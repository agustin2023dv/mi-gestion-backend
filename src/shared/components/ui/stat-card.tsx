import { motion } from 'framer-motion';
import { ArrowUpRight, LucideIcon } from 'lucide-react';

interface StatCardProps {
  label: string;
  value: string;
  trend?: string;
  icon: LucideIcon;
  index?: number;
}

export function StatCard({ label, value, trend, icon: Icon, index = 0 }: StatCardProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: index * 0.1 }}
      className="bg-white p-8 border border-stone-100 flex flex-col justify-between hover:border-stone-200 transition-colors"
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
        <p className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 mb-1">
          {label}
        </p>
        <h3 className="text-3xl font-serif text-stone-900">
          {value}
        </h3>
      </div>
    </motion.div>
  );
}
