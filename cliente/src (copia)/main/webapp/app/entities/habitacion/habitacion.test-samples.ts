import { IHabitacion, NewHabitacion } from './habitacion.model';

export const sampleWithRequiredData: IHabitacion = {
  id: 30780,
};

export const sampleWithPartialData: IHabitacion = {
  id: 29249,
  tipo: 'Murcia moderador',
};

export const sampleWithFullData: IHabitacion = {
  id: 1664,
  tipo: 'Universal',
  capacidadAdulto: 21194,
  capacidadMenor: 19981,
  disponible: true,
};

export const sampleWithNewData: NewHabitacion = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
