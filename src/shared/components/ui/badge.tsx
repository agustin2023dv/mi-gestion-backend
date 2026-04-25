import { ReactNode } from 'react';
import { cn } from '../../utils/cn';

interface BadgeProps {
  children: ReactNode;
  variant?: 'default' | 'success' | 'warning' | 'error' | 'outline';
  className?: string;
}

export function Badge({ children, variant = 'default', className }: BadgeProps) {
  const variants = {
    default: 'bg-stone-100 text-stone-500',
    success: 'bg-green-50 text-green-700 border border-green-100',
    warning: 'bg-amber-50 text-amber-700 border border-amber-100',
    error: 'bg-red-50 text-red-700 border border-red-100',
    outline: 'border border-stone-200 text-stone-500',
  };

  return (
    <span className={cn(
      "inline-flex px-2 py-1 text-[10px] font-bold tracking-widest uppercase",
      variants[variant],
      className
    )}>
      {children}
    </span>
  );
}
