import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown, ArrowUpRight } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import { cn } from '../../utils/cn';
import React from 'react';

interface StatCardProps {
  label: string;
  value: string;
  trend?: string | number;
  icon: LucideIcon | React.ReactNode;
  index?: number;
  delay?: number;
  className?: string;
}

export function StatCard({ 
  label, 
  value, 
  trend, 
  icon: Icon, 
  index = 0, 
  delay,
  className 
}: StatCardProps) {
  const animationDelay = delay ?? index * 0.1;
  
  // Parse trend
  const isNumberTrend = typeof trend === 'number';
  const isPositive = isNumberTrend ? (trend as number) >= 0 : trend?.toString().startsWith('+');
  const trendValue = isNumberTrend ? `${Math.abs(trend as number)}%` : trend;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: animationDelay, duration: 0.4 }}
      className={cn(
        "bg-white p-6 rounded-2xl border border-stone-200 shadow-sm flex flex-col h-full justify-between hover:border-stone-300 transition-colors group",
        className
      )}
    >
      <div className="flex justify-between items-start mb-6">
        <div className="w-10 h-10 rounded-xl bg-stone-50 border border-stone-100 flex items-center justify-center text-stone-600 group-hover:bg-stone-900 group-hover:text-white transition-all duration-300">
          {typeof Icon === 'function' ? <Icon className="w-5 h-5" /> : Icon}
        </div>
        {trend !== undefined && (
          <div className={cn(
            "flex items-center gap-1 text-[10px] font-bold px-2 py-1 rounded-full tracking-wider uppercase",
            isPositive ? "bg-emerald-50 text-emerald-700" : "bg-rose-50 text-rose-700"
          )}>
            {isNumberTrend ? (
              isPositive ? <TrendingUp className="w-3 h-3" /> : <TrendingDown className="w-3 h-3" />
            ) : (
              <ArrowUpRight className="w-3 h-3" />
            )}
            {trendValue}
          </div>
        )}
      </div>
      <div>
        <p className="text-stone-500 text-[10px] font-bold tracking-[0.2em] uppercase mb-1">
          {label}
        </p>
        <h3 className="text-3xl font-serif font-bold text-stone-900">
          {value}
        </h3>
      </div>
    </motion.div>
  );
}
