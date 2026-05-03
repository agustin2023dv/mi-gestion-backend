import type { ReactNode } from 'react';
import { motion } from 'framer-motion';

interface PageHeaderProps {
  title: string;
  description?: string;
  children?: ReactNode;
}

export function PageHeader({ title, description, children }: PageHeaderProps) {
  return (
    <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
      <motion.div
        initial={{ opacity: 0, x: -20 }}
        animate={{ opacity: 1, x: 0 }}
      >
        <h1 className="font-serif text-4xl md:text-5xl text-stone-900 leading-tight">
          {title}
        </h1>
        {description && (
          <p className="text-stone-500 font-medium mt-2 max-w-md">
            {description}
          </p>
        )}
      </motion.div>
      {children && (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="flex items-center gap-4"
        >
          {children}
        </motion.div>
      )}
    </div>
  );
}
