import { AlertTriangle, Loader2 } from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { BaseModal } from './base-modal';

interface DeleteConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  description: string;
  isLoading?: boolean;
  error?: string | null;
}

export default function DeleteConfirmModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  description,
  isLoading = false,
  error = null
}: DeleteConfirmModalProps) {
  return (
    <BaseModal
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      description={description}
    >
      <div className="p-8 pt-6 flex flex-col items-center">
        <div className="w-16 h-16 bg-red-50 rounded-2xl flex items-center justify-center mb-6">
          <AlertTriangle className="w-8 h-8 text-red-500" />
        </div>
        
        {error && (
          <p className="text-red-500 font-bold text-sm px-4 mb-4">
            {error}
          </p>
        )}

        {/* Actions */}
        <div className="w-full flex flex-col sm:flex-row gap-3">
          <Button
            variant="outline"
            onClick={onClose}
            className="flex-1 order-2 sm:order-1 border-stone-200 hover:bg-stone-50 text-stone-600 font-bold uppercase tracking-widest text-[10px] h-12"
            disabled={isLoading}
          >
            Cancelar
          </Button>
          <Button
            onClick={onConfirm}
            className="flex-1 order-1 sm:order-2 bg-red-600 hover:bg-red-700 text-white border-none font-bold uppercase tracking-widest text-[10px] h-12 shadow-lg shadow-red-200"
            disabled={isLoading}
          >
            {isLoading ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              'Eliminar permanentemente'
            )}
          </Button>
        </div>
      </div>
      
      <div className="bg-stone-50 p-4 border-t border-stone-100 text-center">
        <p className="text-[10px] text-stone-400 uppercase font-bold tracking-[0.2em]">
          Esta acción no se puede deshacer
        </p>
      </div>
    </BaseModal>
  );
}

