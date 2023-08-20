import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDetalleFactura, NewDetalleFactura } from '../detalle-factura.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDetalleFactura for edit and NewDetalleFacturaFormGroupInput for create.
 */
type DetalleFacturaFormGroupInput = IDetalleFactura | PartialWithRequiredKeyOf<NewDetalleFactura>;

type DetalleFacturaFormDefaults = Pick<NewDetalleFactura, 'id'>;

type DetalleFacturaFormGroupContent = {
  id: FormControl<IDetalleFactura['id'] | NewDetalleFactura['id']>;
  fechaEmitido: FormControl<IDetalleFactura['fechaEmitido']>;
  habitacionReservada: FormControl<IDetalleFactura['habitacionReservada']>;
  eventoReservado: FormControl<IDetalleFactura['eventoReservado']>;
};

export type DetalleFacturaFormGroup = FormGroup<DetalleFacturaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DetalleFacturaFormService {
  createDetalleFacturaFormGroup(detalleFactura: DetalleFacturaFormGroupInput = { id: null }): DetalleFacturaFormGroup {
    const detalleFacturaRawValue = {
      ...this.getFormDefaults(),
      ...detalleFactura,
    };
    return new FormGroup<DetalleFacturaFormGroupContent>({
      id: new FormControl(
        { value: detalleFacturaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fechaEmitido: new FormControl(detalleFacturaRawValue.fechaEmitido),
      habitacionReservada: new FormControl(detalleFacturaRawValue.habitacionReservada),
      eventoReservado: new FormControl(detalleFacturaRawValue.eventoReservado),
    });
  }

  getDetalleFactura(form: DetalleFacturaFormGroup): IDetalleFactura | NewDetalleFactura {
    return form.getRawValue() as IDetalleFactura | NewDetalleFactura;
  }

  resetForm(form: DetalleFacturaFormGroup, detalleFactura: DetalleFacturaFormGroupInput): void {
    const detalleFacturaRawValue = { ...this.getFormDefaults(), ...detalleFactura };
    form.reset(
      {
        ...detalleFacturaRawValue,
        id: { value: detalleFacturaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DetalleFacturaFormDefaults {
    return {
      id: null,
    };
  }
}
