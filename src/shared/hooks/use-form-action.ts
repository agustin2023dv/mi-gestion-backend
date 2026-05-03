import { useState } from 'react';
import type { ApiResponse, ApiError } from '../types/api';

interface UseFormActionOptions<R> {
  onSuccess?: (data: R) => void;
  onError?: (error: ApiError) => void;
}

export function useFormAction<T, R>(
  action: (data: T) => Promise<ApiResponse<R>>, 
  options?: UseFormActionOptions<R>
) {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const execute = async (data: T) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await action(data);
      if (response.success) {
        options?.onSuccess?.(response.data);
        return { success: true, data: response.data };
      } else {
        const errorMsg = response.error?.message || 'Ocurrió un error inesperado.';
        setError(errorMsg);
        options?.onError?.(response.error || { code: 'UNKNOWN_ERROR', message: errorMsg, timestamp: new Date().toISOString() });
        return { success: false, error: response.error };
      }
    } catch (err: any) {
      const msg = err.response?.data?.error?.message || 
                  err.message || 
                  'No se pudo conectar con el servidor.';
      setError(msg);
      const apiError: ApiError = { 
        code: err.response?.data?.error?.code || 'CONNECTION_ERROR', 
        message: msg,
        timestamp: new Date().toISOString()
      };
      options?.onError?.(apiError);
      return { success: false, error: apiError };
    } finally {
      setIsLoading(false);
    }
  };

  return { 
    isLoading, 
    error, 
    setError, 
    execute,
    reset: () => {
      setIsLoading(false);
      setError(null);
    }
  };
}
