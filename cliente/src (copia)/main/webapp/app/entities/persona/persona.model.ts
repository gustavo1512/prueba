import dayjs from 'dayjs/esm';

export interface IPersona {
  id: number;
  fechaNacimiento?: dayjs.Dayjs | null;
  tipo?: string | null;
}

export type NewPersona = Omit<IPersona, 'id'> & { id: null };
