import dayjs from 'dayjs/esm';
import { ITarifa } from 'app/entities/tarifa/tarifa.model';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';

export interface IEvento {
  id: number;
  nombreEvento?: string | null;
  fechaHora?: dayjs.Dayjs | null;
  capacidadAdulto?: number | null;
  capacidadMenor?: number | null;
  tarifa?: Pick<ITarifa, 'id'> | null;
  encargado?: Pick<IColaborador, 'id'> | null;
}

export type NewEvento = Omit<IEvento, 'id'> & { id: null };
