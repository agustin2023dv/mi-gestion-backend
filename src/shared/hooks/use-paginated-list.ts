import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useDebounce } from './use-debounce';
import type { ApiResponse, PageResponse, PaginationParams } from '../types/api';

interface UsePaginatedListOptions<T> {
  queryKey: any[];
  fetchFn: (params: PaginationParams) => Promise<ApiResponse<PageResponse<T>>>;
  initialPageSize?: number;
  debounceDelay?: number;
}

export function usePaginatedList<T>({
  queryKey,
  fetchFn,
  initialPageSize = 20,
  debounceDelay = 500,
}: UsePaginatedListOptions<T>) {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, debounceDelay);

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: [...queryKey, page, pageSize, debouncedSearch],
    queryFn: () => fetchFn({
      page,
      size: pageSize,
      search: debouncedSearch || undefined,
    }),
  });

  const pageData = data?.data;

  return {
    items: pageData?.content || [],
    totalElements: pageData?.totalElements || 0,
    totalPages: pageData?.totalPages || 0,
    isLoading,
    isError,
    page,
    setPage,
    pageSize,
    setPageSize,
    searchTerm,
    setSearchTerm,
    refetch,
  };
}
