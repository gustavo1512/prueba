import dayjs from 'dayjs/esm';
import { IHabitacion } from 'app/entities/habitacion/habitacion.model';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { IPersona } from 'app/entities/persona/persona.model';

export interface IReservarHabitacion {
  id: number;
  fechaReserva?: dayjs.Dayjs | null;
  fechaInicio?: dayjs.Dayjs | null;
  fechaFinal?: dayjs.Dayjs | null;
  totalReservacion?: number | null;
  habitacion?: Pick<IHabitacion, 'id'> | null;
  clienteReservaHabitacion?: Pick<ICliente, 'id'> | null;
  colaboradorReservaHabitacion?: Pick<IColaborador, 'id'> | null;
  ocupantes?: Pick<IPersona, 'id'> | null;
}

export type NewReservarHabitacion = Omit<IReservarHabitacion, 'id'> & { id: null };
