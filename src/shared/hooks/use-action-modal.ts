import { useState } from 'react';

export function useActionModal<T>(actionFn: (item: T) => Promise<any>) {
  const [isOpen, setIsOpen] = useState(false);
  const [itemToActOn, setItemToActOn] = useState<T | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleOpen = (item: T) => {
    setItemToActOn(item);
    setError(null);
    setIsOpen(true);
  };

  const handleClose = () => {
    setIsOpen(false);
    setItemToActOn(null);
    setError(null);
  };

  const handleConfirm = async () => {
    if (itemToActOn) {
      try {
        setError(null);
        await actionFn(itemToActOn);
        handleClose();
      } catch (err: any) {
        console.error('Action failed:', err);
        setError(err.response?.data?.message || 'Error al ejecutar la acción. Verifica la conexión o intenta de nuevo.');
      }
    }
  };

  return {
    isOpen,
    itemToActOn,
    error,
    handleOpen,
    handleClose,
    handleConfirm,
  };
}
