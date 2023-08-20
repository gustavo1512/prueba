import dayjs from 'dayjs/esm';

import { IEvento, NewEvento } from './evento.model';

export const sampleWithRequiredData: IEvento = {
  id: 25200,
};

export const sampleWithPartialData: IEvento = {
  id: 31703,
  fechaHora: dayjs('2023-08-06T14:47'),
  capacidadMenor: 17449,
};

export const sampleWithFullData: IEvento = {
  id: 3322,
  nombreEvento: 'fritas Soluciones',
  fechaHora: dayjs('2023-08-06T07:52'),
  capacidadAdulto: 23494,
  capacidadMenor: 4740,
};

export const sampleWithNewData: NewEvento = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
