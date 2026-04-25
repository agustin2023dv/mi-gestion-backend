import * as React from 'react';
import { motion } from 'framer-motion';
import type { HTMLMotionProps } from 'framer-motion';
import { cn } from '../../utils/cn';

interface ButtonProps extends HTMLMotionProps<'button'> {
  isLoading?: boolean;
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, isLoading, variant = 'primary', size = 'md', children, ...props }, ref) => {
    const variants = {
      primary: 'bg-stone-900 text-stone-50 hover:bg-stone-800',
      secondary: 'bg-stone-200 text-stone-900 hover:bg-stone-300',
      outline: 'border-2 border-stone-900 text-stone-900 hover:bg-stone-900 hover:text-stone-50',
      ghost: 'text-stone-900 hover:bg-stone-100',
    };

    const sizes = {
      sm: 'py-2 px-4 text-[10px]',
      md: 'py-4 px-8 text-xs',
      lg: 'py-5 px-10 text-sm',
    };

    return (
      <motion.button
        ref={ref as any}
        whileHover={{ scale: 1.01 }}
        whileTap={{ scale: 0.99 }}
        className={cn(
          'relative overflow-hidden flex items-center justify-center transition-colors disabled:opacity-70 disabled:cursor-not-allowed focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-stone-900 font-bold tracking-[0.2em] uppercase',
          variants[variant],
          sizes[size],
          className
        )}
        disabled={isLoading || props.disabled}
        {...props}
      >
        {isLoading ? (
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ repeat: Infinity, duration: 1, ease: 'linear' }}
            className="w-4 h-4 border-2 border-current border-t-transparent rounded-full"
          />
        ) : (
          children
        )}
      </motion.button>
    );
  }
);

Button.displayName = 'Button';
