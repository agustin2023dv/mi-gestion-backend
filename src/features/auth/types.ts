export type AuthView = 'login' | 'register' | 'forgot';

export interface AuthMockupProps {
  onLogin: (token: string) => void;
}

export interface AuthFormProps {
  setView: (v: AuthView) => void;
  onLogin?: (token: string) => void;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  telefono?: string;
  password: string;
  tenantId?: number;
}

export interface CreateTenantRequest {
  nombreNegocio: string;
  slug: string;
  planSuscripcionId: number;
  propietarioNombre: string;
  propietarioApellido: string;
  propietarioEmail: string;
  propietarioTelefono?: string;
  password: string;
}

export interface TenantResponse {
  id: number;
  nombreNegocio: string;
  slug: string;
}
