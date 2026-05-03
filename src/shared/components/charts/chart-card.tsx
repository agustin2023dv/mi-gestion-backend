import React from 'react';
import { ResponsiveContainer } from 'recharts';
import { cn } from '../../utils/cn';

interface ChartCardProps {
  title: string;
  subtitle?: string;
  children: React.ReactElement;
  height?: number | string;
  className?: string;
  actions?: React.ReactNode;
}

export function ChartCard({ 
  title, 
  subtitle, 
  children, 
  height = 300, 
  className,
  actions 
}: ChartCardProps) {
  return (
    <div className={cn(
      "bg-white p-6 rounded-2xl border border-stone-200 shadow-sm flex flex-col",
      className
    )}>
      <div className="flex justify-between items-start mb-6">
        <div>
          <h3 className="font-serif text-lg font-bold text-stone-900">{title}</h3>
          {subtitle && <p className="text-stone-400 text-xs mt-1 uppercase tracking-wider font-bold">{subtitle}</p>}
        </div>
        {actions && <div className="flex items-center gap-2">{actions}</div>}
      </div>
      
      <div style={{ height: typeof height === 'number' ? `${height}px` : height }}>
        <ResponsiveContainer width="100%" height="100%">
          {children}
        </ResponsiveContainer>
      </div>
    </div>
  );
}
