import { ITipoCargo, NewTipoCargo } from './tipo-cargo.model';

export const sampleWithRequiredData: ITipoCargo = {
  id: 5093,
};

export const sampleWithPartialData: ITipoCargo = {
  id: 29606,
  nombreCargo: 'Director Datos',
  precioHora: 8932,
};

export const sampleWithFullData: ITipoCargo = {
  id: 17265,
  nombreCargo: 'Corporativo',
  precioHora: 27294,
};

export const sampleWithNewData: NewTipoCargo = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
