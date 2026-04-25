import { ReactNode } from 'react';
import { LucideIcon } from 'lucide-react';
import { cn } from '../../utils/cn';

interface SectionProps {
  title: string;
  icon?: LucideIcon;
  children: ReactNode;
  className?: string;
}

export function Section({ title, icon: Icon, children, className }: SectionProps) {
  return (
    <section className={cn("space-y-8 bg-white p-8 border border-stone-100", className)}>
      <div className="flex items-center space-x-3 text-stone-900 border-b border-stone-50 pb-4">
        {Icon && <Icon className="w-5 h-5" />}
        <h2 className="text-xs font-bold tracking-[0.2em] uppercase">
          {title}
        </h2>
      </div>
      {children}
    </section>
  );
}
