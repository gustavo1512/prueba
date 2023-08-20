import dayjs from 'dayjs/esm';

import { IReservarEvento, NewReservarEvento } from './reservar-evento.model';

export const sampleWithRequiredData: IReservarEvento = {
  id: 7639,
};

export const sampleWithPartialData: IReservarEvento = {
  id: 12862,
};

export const sampleWithFullData: IReservarEvento = {
  id: 30812,
  fechaReservacion: dayjs('2023-08-06T09:39'),
  totalReservacion: 13888,
};

export const sampleWithNewData: NewReservarEvento = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
