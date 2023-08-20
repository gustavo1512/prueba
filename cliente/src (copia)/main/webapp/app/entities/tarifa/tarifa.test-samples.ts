import { ITarifa, NewTarifa } from './tarifa.model';

export const sampleWithRequiredData: ITarifa = {
  id: 16776,
};

export const sampleWithPartialData: ITarifa = {
  id: 25952,
  tipoTarifa: 'Soluciones',
};

export const sampleWithFullData: ITarifa = {
  id: 11723,
  tipoTarifa: 'Ergonómico Ejecutivo',
  tarifaAdulto: 5772,
  tarifaMenor: 6709,
};

export const sampleWithNewData: NewTarifa = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
