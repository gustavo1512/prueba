import dayjs from 'dayjs/esm';

import { IReservarHabitacion, NewReservarHabitacion } from './reservar-habitacion.model';

export const sampleWithRequiredData: IReservarHabitacion = {
  id: 7662,
};

export const sampleWithPartialData: IReservarHabitacion = {
  id: 11246,
  fechaInicio: dayjs('2023-08-06T20:24'),
};

export const sampleWithFullData: IReservarHabitacion = {
  id: 16085,
  fechaReserva: dayjs('2023-08-07T04:54'),
  fechaInicio: dayjs('2023-08-07T00:30'),
  fechaFinal: dayjs('2023-08-06T07:27'),
  totalReservacion: 23640,
};

export const sampleWithNewData: NewReservarHabitacion = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
