import dayjs from 'dayjs/esm';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';

export interface IReservarEvento {
  id: number;
  fechaReservacion?: dayjs.Dayjs | null;
  totalReservacion?: number | null;
  clienteReservaEvento?: Pick<ICliente, 'id'> | null;
  colaboradorReservaEvento?: Pick<IColaborador, 'id'> | null;
}

export type NewReservarEvento = Omit<IReservarEvento, 'id'> & { id: null };
