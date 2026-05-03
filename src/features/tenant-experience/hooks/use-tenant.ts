import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { tenantApi } from '../api/tenant';

export interface TenantInfo {
  id: string;
  name: string;
  slug: string;
  logo?: string;
  description?: string;
  color?: string;
}

export function useTenant() {
  const { tenantSlug } = useParams<{ tenantSlug: string }>();

  const { data, isLoading } = useQuery({
    queryKey: ['tenant', tenantSlug],
    queryFn: () => tenantApi.getBySlug(tenantSlug!),
    enabled: !!tenantSlug,
    retry: 1,
  });

  const tenant = data?.success ? {
    id: data.data.id.toString(),
    name: data.data.nombreNegocio,
    slug: data.data.slug,
    logo: data.data.logoUrl,
    color: data.data.colorPrimario,
    description: data.data.descripcion || '',
  } : null;

  return {
    tenant,
    isLoading,
    error: !isLoading && tenantSlug && !tenant ? 'Tenant not found' : null,
  };
}
