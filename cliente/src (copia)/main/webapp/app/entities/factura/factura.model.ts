import { IDetalleFactura } from 'app/entities/detalle-factura/detalle-factura.model';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';

export interface IFactura {
  id: number;
  metodoPago?: string | null;
  subtotal?: number | null;
  montoTotal?: number | null;
  impuesto?: number | null;
  detalleFactura?: Pick<IDetalleFactura, 'id'> | null;
  clienteFactura?: Pick<ICliente, 'id'> | null;
  clienteColaborador?: Pick<IColaborador, 'id'> | null;
}

export type NewFactura = Omit<IFactura, 'id'> & { id: null };
