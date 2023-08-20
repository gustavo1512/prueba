export interface ITipoCargo {
  id: number;
  nombreCargo?: string | null;
  precioHora?: number | null;
}

export type NewTipoCargo = Omit<ITipoCargo, 'id'> & { id: null };
