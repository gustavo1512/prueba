import { IReservarHabitacion } from 'app/entities/reservar-habitacion/reservar-habitacion.model';
import { IReservarEvento } from 'app/entities/reservar-evento/reservar-evento.model';

export interface IDetalleFactura {
  id: number;
  fechaEmitido?: number | null;
  habitacionReservada?: Pick<IReservarHabitacion, 'id'> | null;
  eventoReservado?: Pick<IReservarEvento, 'id'> | null;
}

export type NewDetalleFactura = Omit<IDetalleFactura, 'id'> & { id: null };
