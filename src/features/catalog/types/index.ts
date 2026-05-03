export interface Product {
  id: number;
  tenantId: number;
  subcategoriaId: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  costoUnitarioCalculado?: number;
  costoUnitarioManualOverride?: number;
  usaCostoCalculado?: boolean;
  stock: number;
  stockMinimo: number;
  imagenUrl?: string;
  sku?: string;
  esPersonalizable?: boolean;
  esServicio?: boolean;
  duracionMinutos?: number;
  permiteBooking?: boolean;
  bufferEntreTurnosMin?: number;
  requiereVerificacionEdad?: boolean;
  edadMinima?: number;
  requiereEmpleadoEspecifico?: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductRequest {
  nombre: string;
  descripcion?: string;
  precio: number;
  costoUnitarioManualOverride?: number;
  usaCostoCalculado?: boolean;
  stock?: number;
  stockMinimo?: number;
  imagenUrl?: string;
  sku?: string;
  esPersonalizable?: boolean;
  esServicio?: boolean;
  duracionMinutos?: number;
  permiteBooking?: boolean;
  bufferEntreTurnosMin?: number;
  requiereVerificacionEdad?: boolean;
  edadMinima?: number;
  requiereEmpleadoEspecifico?: boolean;
  isActive?: boolean;
  subcategoriaId: number;
}

export interface Category {
  id: number;
  nombre: string;
  descripcion: string;
  isActive: boolean;
  subcategorias?: Subcategory[];
  createdAt: string;
  updatedAt: string;
}

export interface Subcategory {
  id: number;
  categoria?: {
    id: number;
    nombre: string;
  };
  nombre: string;
  descripcion: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}
