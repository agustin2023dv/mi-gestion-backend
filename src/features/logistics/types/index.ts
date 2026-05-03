export type DeliveryStatus = 'PENDIENTE' | 'ASIGNADA' | 'EN_CAMINO' | 'ENTREGADA' | 'CANCELADA';

export interface Delivery {
  id: number;
  tenantId: number;
  estado: DeliveryStatus;
  latitudActual: number | null;
  longitudActual: number | null;
  distanciaVerificada: number | null;
  geolocalizacionValidada: boolean;
  verificacionEdadHecha: boolean;
  asignadoEn: string | null;
  inicioEntrega: string | null;
  entregaConfirmada: string | null;
  createdAt: string;
  updatedAt: string;
  pedido: DeliveryOrder;
  repartidor: DeliveryCourier | null;
  motivoCancelacion?: string;
  notasCancelacion?: string;
}

export interface DeliveryOrder {
  id: number;
  numeroPedido: string;
  tipoEntrega: string;
  direccionEntrega: DeliveryAddress;
  cliente: DeliveryCustomer;
  total: number;
  metodoPago: string;
  requiereVerificacionEdad: boolean;
}

export interface DeliveryAddress {
  calle: string;
  numero: string;
  ciudad: string;
  provincia: string;
  codigoPostal: string;
  latitud: number;
  longitud: number;
}

export interface DeliveryCustomer {
  nombre: string;
  apellido: string;
  telefono: string;
}

export interface DeliveryCourier {
  id: number;
  nombre: string;
  apellido: string;
  telefono: string;
}

export interface UpdateStatusRequest {
  nuevoEstado: DeliveryStatus;
  latitud?: number;
  longitud?: number;
  codigoEntrega?: string;
  motivoCancelacion?: string;
  notasCancelacion?: string;
}

export interface AssignCourierRequest {
  repartidorId: number;
}
