import { ITarifa } from 'app/entities/tarifa/tarifa.model';

export interface IHabitacion {
  id: number;
  tipo?: string | null;
  capacidadAdulto?: number | null;
  capacidadMenor?: number | null;
  disponible?: boolean | null;
  tarifa?: Pick<ITarifa, 'id'> | null;
}

export type NewHabitacion = Omit<IHabitacion, 'id'> & { id: null };
