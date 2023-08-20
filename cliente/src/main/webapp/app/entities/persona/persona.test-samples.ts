import dayjs from 'dayjs/esm';

import { IPersona, NewPersona } from './persona.model';

export const sampleWithRequiredData: IPersona = {
  id: 8656,
};

export const sampleWithPartialData: IPersona = {
  id: 28257,
  fechaNacimiento: dayjs('2023-08-06T07:16'),
  tipo: 'Mali Hormigon',
};

export const sampleWithFullData: IPersona = {
  id: 17916,
  fechaNacimiento: dayjs('2023-08-06T08:18'),
  tipo: 'Algodón paralelismo',
};

export const sampleWithNewData: NewPersona = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
