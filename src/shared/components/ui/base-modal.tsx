import { createPortal } from 'react-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { X } from 'lucide-react';
import { cn } from '../../utils/cn';

interface BaseModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  title?: string;
  description?: string;
  className?: string;
  showCloseButton?: boolean;
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
}

const SIZES = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-lg',
  xl: 'max-w-xl',
  full: 'max-w-full m-4',
};

export function BaseModal({
  isOpen,
  onClose,
  children,
  title,
  description,
  className,
  showCloseButton = true,
  size = 'md'
}: BaseModalProps) {
  const modalContent = (
    <AnimatePresence>
      {isOpen && (
        <>
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-stone-900/40 backdrop-blur-sm z-[100]"
          />

          {/* Modal Container */}
          <div className="fixed inset-0 flex items-center justify-center z-[101] p-4 pointer-events-none">
            <motion.div
              initial={{ opacity: 0, scale: 0.95, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95, y: 20 }}
              transition={{ type: 'spring', duration: 0.5, bounce: 0.3 }}
              className={cn(
                "w-full bg-white border border-stone-200 shadow-2xl rounded-3xl overflow-hidden pointer-events-auto relative",
                SIZES[size],
                className
              )}
            >
              {showCloseButton && (
                <button
                  onClick={onClose}
                  className="absolute top-6 right-6 p-2 text-stone-400 hover:text-stone-900 hover:bg-stone-50 rounded-full transition-all z-10"
                >
                  <X className="w-5 h-5" />
                </button>
              )}

              {(title || description) && (
                <div className="p-8 pb-0 text-center">
                  {title && (
                    <h3 className="font-serif text-2xl text-stone-900 mb-2">
                      {title}
                    </h3>
                  )}
                  {description && (
                    <p className="text-stone-500 font-medium px-4">
                      {description}
                    </p>
                  )}
                </div>
              )}

              {children}
            </motion.div>
          </div>
        </>
      )}
    </AnimatePresence>
  );

  if (typeof document === 'undefined') return null;
  return createPortal(modalContent, document.body);
}
