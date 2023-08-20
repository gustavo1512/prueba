import { IColaborador, NewColaborador } from './colaborador.model';

export const sampleWithRequiredData: IColaborador = {
  id: 15525,
};

export const sampleWithPartialData: IColaborador = {
  id: 30645,
  numTelefono: 2377,
  correo: 'Guapa Violeta Cantabria',
};

export const sampleWithFullData: IColaborador = {
  id: 7619,
  nombreColaborador: 'Azul Adelante',
  numTelefono: 10935,
  correo: 'Ladrillo Carlos Cataluña',
};

export const sampleWithNewData: NewColaborador = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
