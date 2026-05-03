import type { LucideIcon } from 'lucide-react';
import { cn } from '../../utils/cn';

interface EmptyStateProps {
  icon?: LucideIcon;
  title: string;
  description?: string;
  className?: string;
}

export function EmptyState({ icon: Icon, title, description, className }: EmptyStateProps) {
  return (
    <div className={cn(
      "flex flex-col items-center justify-center p-8 text-center bg-stone-50/50 border border-dashed border-stone-200 rounded-2xl",
      className
    )}>
      {Icon && <Icon className="w-10 h-10 text-stone-300 mb-3" />}
      <h3 className="text-stone-900 font-bold text-sm">{title}</h3>
      {description && <p className="text-stone-500 text-xs mt-1 max-w-[200px]">{description}</p>}
    </div>
  );
}
