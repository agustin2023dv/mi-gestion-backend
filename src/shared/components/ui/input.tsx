import * as React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { cn } from '../../utils/cn';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  suffix?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, label, error, suffix, ...props }, ref) => {
    return (
      <div className="flex flex-col space-y-1 mb-6 relative w-full">
        <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500 mb-1">
          {label}
        </label>
        <div className="relative flex items-center">
          <input
            ref={ref}
            className={cn(
              'bg-transparent border-b py-2 focus:outline-none transition-colors rounded-none w-full text-base font-medium placeholder-stone-300',
              error 
                ? 'border-red-500 focus:border-red-600 text-red-900' 
                : 'border-stone-300 focus:border-stone-900 text-stone-900',
              suffix ? 'pr-20' : '',
              className
            )}
            {...props}
          />
          {suffix && (
            <span className="absolute right-0 text-stone-400 text-xs font-bold tracking-widest uppercase pointer-events-none">
              {suffix}
            </span>
          )}
        </div>
        <AnimatePresence>
          {error && (
            <motion.span
              initial={{ opacity: 0, y: -5 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -5 }}
              className="text-red-500 text-[10px] mt-1 absolute -bottom-5 left-0 font-bold uppercase tracking-wider"
              role="alert"
            >
              {error}
            </motion.span>
          )}
        </AnimatePresence>
      </div>
    );
  }
);

Input.displayName = 'Input';
