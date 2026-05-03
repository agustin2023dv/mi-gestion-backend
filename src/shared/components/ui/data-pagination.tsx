import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from './button';

interface DataPaginationProps {
  page: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  isLoading?: boolean;
}

export function DataPagination({
  page,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  isLoading
}: DataPaginationProps) {
  if (totalElements === 0 && !isLoading) return null;

  const startRange = page * pageSize + 1;
  const endRange = Math.min((page + 1) * pageSize, totalElements);

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between px-6 py-4 bg-stone-50/50 border-t border-stone-100 gap-4">
      <div className="text-xs font-medium text-stone-500 uppercase tracking-widest text-stone-400">
        Mostrando <span className="text-stone-900 font-bold">{startRange}-{endRange}</span> de <span className="text-stone-900 font-bold">{totalElements}</span> resultados
      </div>
      
      <div className="flex items-center space-x-2">
        <Button
          variant="secondary"
          size="sm"
          onClick={() => onPageChange(Math.max(0, page - 1))}
          disabled={page === 0 || isLoading}
          className="p-2 h-8 w-8"
        >
          <ChevronLeft className="w-4 h-4" />
        </Button>
        
        <div className="flex items-center px-4 py-1 bg-white border border-stone-200 rounded text-[10px] font-bold uppercase tracking-tighter text-stone-600">
          Página {page + 1} de {totalPages || 1}
        </div>

        <Button
          variant="secondary"
          size="sm"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages - 1 || isLoading}
          className="p-2 h-8 w-8"
        >
          <ChevronRight className="w-4 h-4" />
        </Button>
      </div>
    </div>
  );
}
