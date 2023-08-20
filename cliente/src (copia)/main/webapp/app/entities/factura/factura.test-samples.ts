import { IFactura, NewFactura } from './factura.model';

export const sampleWithRequiredData: IFactura = {
  id: 2376,
};

export const sampleWithPartialData: IFactura = {
  id: 10527,
  impuesto: 19210,
};

export const sampleWithFullData: IFactura = {
  id: 5643,
  metodoPago: 'estandardización base',
  subtotal: 27168,
  montoTotal: 6489,
  impuesto: 3682,
};

export const sampleWithNewData: NewFactura = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
