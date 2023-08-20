import { IDetalleFactura, NewDetalleFactura } from './detalle-factura.model';

export const sampleWithRequiredData: IDetalleFactura = {
  id: 30444,
};

export const sampleWithPartialData: IDetalleFactura = {
  id: 11350,
};

export const sampleWithFullData: IDetalleFactura = {
  id: 30543,
  fechaEmitido: 24859,
};

export const sampleWithNewData: NewDetalleFactura = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
