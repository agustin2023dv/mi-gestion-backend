import { motion } from 'framer-motion';
import { ChevronLeft } from 'lucide-react';
import { cn } from '../../../shared/utils/cn';

interface AuthFormWrapperProps {
  children: React.ReactNode;
  title: string;
  subtitle: string;
  onBack?: () => void;
  showBack?: boolean;
  className?: string;
}

export function AuthFormWrapper({ 
  children, 
  title, 
  subtitle, 
  onBack, 
  showBack = true,
  className 
}: AuthFormWrapperProps) {
  return (
    <motion.div
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
      className={cn("w-full max-w-md mx-auto", className)}
    >
      <div className="mb-6">
        {showBack && onBack && (
          <button
            onClick={onBack}
            className="mb-8 flex items-center text-sm font-bold tracking-widest uppercase text-stone-400 hover:text-stone-900 transition-colors group focus-visible:outline-stone-900"
            aria-label="Volver atrás"
          >
            <ChevronLeft className="w-4 h-4 mr-2 transform group-hover:-translate-x-1 transition-transform" />
            Atrás
          </button>
        )}
        <h2 className="font-serif text-3xl lg:text-4xl mb-2 text-stone-900">{title}</h2>
        <p className="text-stone-500 font-medium">{subtitle}</p>
      </div>

      <div className="mt-4">
        {children}
      </div>
    </motion.div>
  );
}
