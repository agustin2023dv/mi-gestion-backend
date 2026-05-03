import { useState } from 'react';
import { AlertCircle, AlertTriangle } from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { BaseModal } from '../../../shared/components/ui/base-modal';

interface CancelOrderModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (reason: string, notes: string) => void;
  orderNumber: string;
}

const CANCELLATION_REASONS = [
  'El cliente se arrepintió/canceló',
  'Productos sin stock',
  'Dirección inválida',
  'No se recibió el pago',
  'Pedido duplicado',
  'Otro'
];

export function CancelOrderModal({ isOpen, onClose, onConfirm, orderNumber }: CancelOrderModalProps) {
  const [reason, setReason] = useState('');
  const [notes, setNotes] = useState('');

  const handleConfirm = () => {
    if (!reason) return;
    onConfirm(reason, notes);
    onClose();
  };

  return (
    <BaseModal
      isOpen={isOpen}
      onClose={onClose}
      title="Cancelación de pedido"
      description={`Pedido #${orderNumber}`}
    >
      <div className="p-8 pt-6">
        {/* Header Icon */}
        <div className="flex flex-col items-center mb-6">
          <div className="w-16 h-16 bg-rose-50 rounded-full flex items-center justify-center mb-4">
            <AlertCircle className="w-8 h-8 text-rose-500" />
          </div>
        </div>

        {/* Form */}
        <div className="space-y-6">
          <div>
            <label className="block text-sm font-bold text-stone-700 mb-3">
              Selecciona un motivo: <span className="text-rose-500">*</span>
            </label>
            <div className="space-y-2">
              {CANCELLATION_REASONS.map((r) => (
                <label 
                  key={r} 
                  className={`flex items-center gap-3 p-3 rounded-xl border cursor-pointer transition-all ${
                    reason === r 
                      ? 'border-rose-200 bg-rose-50/50 text-rose-700 ring-1 ring-rose-200' 
                      : 'border-stone-100 hover:border-stone-200 text-stone-600'
                  }`}
                >
                  <input
                    type="radio"
                    name="reason"
                    value={r}
                    checked={reason === r}
                    onChange={(e) => setReason(e.target.value)}
                    className="w-4 h-4 text-rose-600 border-stone-300 focus:ring-rose-500"
                  />
                  <span className="text-sm font-medium">{r}</span>
                </label>
              ))}
            </div>
          </div>

          <div>
            <label className="block text-sm font-bold text-stone-700 mb-2">
              Detalle (Opcional)
            </label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Agrega notas adicionales sobre la cancelación..."
              className="w-full h-24 p-4 text-sm bg-stone-50 border border-stone-200 rounded-2xl focus:ring-2 focus:ring-rose-500/20 focus:border-rose-500 outline-none transition-all resize-none"
            />
          </div>

          {/* Warning */}
          <div className="flex gap-3 p-4 bg-amber-50 rounded-2xl border border-amber-100">
            <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0" />
            <p className="text-xs text-amber-800 leading-relaxed">
              Una vez cancelado el pedido no podrá reabrirse. Se mantendrá el registro para historial.
            </p>
          </div>
        </div>

        {/* Actions */}
        <div className="flex gap-3 mt-8">
          <Button
            variant="outline"
            onClick={onClose}
            className="flex-1 rounded-xl py-6 font-bold text-stone-600 hover:bg-stone-50"
          >
            Cancelar
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={!reason}
            className="flex-1 rounded-xl py-6 font-bold bg-rose-500 hover:bg-rose-600 text-white disabled:opacity-50"
          >
            Confirmar
          </Button>
        </div>
      </div>
    </BaseModal>
  );
}

