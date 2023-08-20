import { ICliente, NewCliente } from './cliente.model';

export const sampleWithRequiredData: ICliente = {
  id: 13505,
};

export const sampleWithPartialData: ICliente = {
  id: 27431,
  nombre: 'optimizada Productor',
  apellido: 'Cantabria',
  direccion: 'Heredado Azul',
};

export const sampleWithFullData: ICliente = {
  id: 31453,
  nombre: 'Hormigon Integración conocimiento',
  apellido: 'Gris Marroquinería',
  direccion: 'Gris Azul Verde',
  correo: 'Teclado',
  telefono: 'Inversor Zapatos Ladrillo',
};

export const sampleWithNewData: NewCliente = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
