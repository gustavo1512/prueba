export interface ITarifa {
  id: number;
  tipoTarifa?: string | null;
  tarifaAdulto?: number | null;
  tarifaMenor?: number | null;
}

export type NewTarifa = Omit<ITarifa, 'id'> & { id: null };
