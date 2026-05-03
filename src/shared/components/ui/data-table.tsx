import React, { useState } from 'react'; 
import { Search, Filter, User, Loader2 } from 'lucide-react';
import { cn } from '../../utils/cn';
import { DataPagination } from './data-pagination';

// ✅ Interface exportada como tipo
export interface Column<T> {
  header: string;
  accessor: keyof T | ((item: T) => React.ReactNode);
  className?: string;
  headerClassName?: string;
  align?: 'left' | 'right' | 'center';
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  onRowClick?: (item: T) => void;
  isLoading?: boolean;
  emptyState?: {
    title: string;
    description: string;
    icon?: React.ReactNode;
  };
  searchPlaceholder?: string;
  filterButton?: boolean;
  className?: string;
  rowClassName?: string;
  searchKey?: keyof T | ((item: T, term: string) => boolean);
  footer?: React.ReactNode;
  onSearch?: (term: string) => void;
  searchValue?: string;
  // Pagination props
  page?: number;
  totalPages?: number;
  totalElements?: number;
  pageSize?: number;
  onPageChange?: (page: number) => void;
}

export function DataTable<T>({
  columns,
  data,
  onRowClick,
  isLoading,
  emptyState,
  searchPlaceholder,
  filterButton = true,
  className,
  rowClassName,
  searchKey,
  footer,
  onSearch,
  searchValue,
  page,
  totalPages,
  totalElements,
  pageSize,
  onPageChange
}: DataTableProps<T>) {
  console.log('[DataTable] Rendering with data length:', data?.length, 'isLoading:', isLoading);
  const [internalSearchTerm, setInternalSearchTerm] = useState('');
  
  const currentSearchTerm = onSearch ? (searchValue ?? '') : internalSearchTerm;

  const filteredData = React.useMemo(() => {
    // Skip internal filtering if searching is handled externally
    if (onSearch) return data;
    
    console.log('[DataTable] Computing filtered data. Term:', currentSearchTerm);
    if (!currentSearchTerm || !searchKey) return data;

    return data.filter((item) => {
      if (typeof searchKey === 'function') {
        return searchKey(item, currentSearchTerm);
      }
      const value = item[searchKey];
      return String(value).toLowerCase().includes(currentSearchTerm.toLowerCase());
    });
  }, [data, currentSearchTerm, searchKey, onSearch]);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const term = e.target.value;
    if (onSearch) {
      onSearch(term);
    } else {
      setInternalSearchTerm(term);
    }
  };

  return (
    <div className={cn("space-y-6", className)}>
      {/* Search and Filters */}
      {(searchPlaceholder || filterButton) && (
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          {searchPlaceholder && (
            <div className="relative w-full max-w-md">
              <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-stone-400" />
              <input
                type="text"
                placeholder={searchPlaceholder}
                className="w-full bg-white border border-stone-200 rounded-xl py-3 pl-12 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-stone-900/5 transition-all"
                value={currentSearchTerm}
                onChange={handleSearchChange}
              />
            </div>
          )}
          {filterButton && (
            <div className="flex items-center space-x-3 w-full sm:w-auto">
              <button className="flex-1 sm:flex-none flex items-center justify-center space-x-2 px-4 py-3 bg-white border border-stone-200 rounded-xl text-xs font-bold tracking-wider uppercase hover:bg-stone-50 transition-colors">
                <Filter className="w-4 h-4" />
                <span>Filtros</span>
              </button>
            </div>
          )}
        </div>
      )}

      {/* Table Container */}
      <div className="bg-white border border-stone-100 rounded-2xl overflow-hidden shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-stone-50 border-b border-stone-100">
                {columns.map((column, idx) => (
                  <th
                    key={idx}
                    className={cn(
                      "px-6 py-4 text-[10px] font-bold uppercase tracking-[0.2em] text-stone-400",
                      column.align === 'right' && "text-right",
                      column.align === 'center' && "text-center",
                      column.headerClassName
                    )}
                  >
                    {column.header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-stone-100">
              {isLoading ? (
                <tr>
                  <td colSpan={columns.length} className="px-6 py-20 text-center text-stone-400 text-sm">
                    <Loader2 className="w-6 h-6 animate-spin mx-auto mb-2" />
                    Cargando...
                  </td>
                </tr>
              ) : filteredData.length > 0 ? (
                filteredData.map((item, rowIdx) => (
                  <tr
                    key={rowIdx}
                    className={cn(
                      "hover:bg-stone-50/50 transition-colors group",
                      onRowClick && "cursor-pointer",
                      rowClassName
                    )}
                    onClick={() => onRowClick?.(item)}
                  >
                    {columns.map((column, colIdx) => (
                      <td
                        key={colIdx}
                        className={cn(
                          "px-6 py-5",
                          column.align === 'right' && "text-right",
                          column.align === 'center' && "text-center",
                          column.className
                        )}
                      >
                        {typeof column.accessor === 'function'
                          ? column.accessor(item)
                          : (item[column.accessor] as React.ReactNode)}
                      </td>
                    ))}
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={columns.length} className="px-6">
                    <div className="py-20 text-center">
                      <div className="w-16 h-16 bg-stone-50 rounded-full flex items-center justify-center mx-auto mb-4">
                        {emptyState?.icon || <User className="w-8 h-8 text-stone-200" />}
                      </div>
                      <h3 className="font-serif text-xl text-stone-900 mb-1">{emptyState?.title || 'No se encontraron resultados'}</h3>
                      <p className="text-stone-400 text-sm">{emptyState?.description || 'Intenta ajustar tus criterios de búsqueda.'}</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination / Footer */}
        {footer ? (
          <div className="p-6 bg-stone-50/50 border-t border-stone-100 flex items-center justify-between">
            {footer}
          </div>
        ) : (
          <DataPagination
            page={page ?? 0}
            totalPages={totalPages ?? 1}
            totalElements={totalElements ?? filteredData.length}
            pageSize={pageSize ?? filteredData.length}
            onPageChange={onPageChange ?? (() => {})}
            isLoading={isLoading}
          />
        )}
      </div>
    </div>
  );
}