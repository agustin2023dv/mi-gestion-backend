export type Periodicidad = 'MENSUAL' | 'QUINCENAL' | 'SEMANAL' | 'DIARIO' | 'UNICO';

export interface GastoOperativo {
  id: number;
  tenantId: number;
  nombre: string;
  monto: number;
  fechaRegistro: string;
  empleadoId: number | null;
  centroCostoId: number | null;
  categoriaGastoId: number;
  periodicidad: Periodicidad;
  esRecurrente: boolean;
  esDirecto: boolean;
  esProrrateable: boolean;
  descripcion?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateGastoRequest {
  nombre: string;
  monto: number;
  fechaRegistro: string;
  empleadoId?: number;
  centroCostoId?: number;
  categoriaGastoId: number;
  periodicidad?: Periodicidad;
  esRecurrente: boolean;
  esDirecto: boolean;
  esProrrateable: boolean;
  descripcion?: string;
}

export interface CategoriaGasto {
  id: number;
  nombre: string;
  tipoNaturaleza: 'FIJO' | 'VARIABLE';
  esDirecto: boolean;
  esProrrateable: boolean;
  descripcion?: string;
}

export interface CentroCosto {
  id: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  isActive: boolean;
}
