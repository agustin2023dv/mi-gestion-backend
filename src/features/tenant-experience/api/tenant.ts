import { apiClient } from '../../../shared/api/api-client';
import type { ApiResponse } from '../../../shared/types/api';

export interface Tenant {
  id: string;
  nombreNegocio: string;
  slug: string;
  descripcion?: string;
  logoUrl?: string;
  colorPrimario?: string;
  colorSecundario?: string;
}

export const tenantApi = {
  getBySlug: async (slug: string) => {
    // Assuming this endpoint exists based on common multi-tenant patterns
    const response = await apiClient.get<ApiResponse<Tenant>>(`/api/v1/platform/tenants/slug/${slug}`);
    return response.data;
  }
};
