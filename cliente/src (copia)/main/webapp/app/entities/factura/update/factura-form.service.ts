import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFactura, NewFactura } from '../factura.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFactura for edit and NewFacturaFormGroupInput for create.
 */
type FacturaFormGroupInput = IFactura | PartialWithRequiredKeyOf<NewFactura>;

type FacturaFormDefaults = Pick<NewFactura, 'id'>;

type FacturaFormGroupContent = {
  id: FormControl<IFactura['id'] | NewFactura['id']>;
  metodoPago: FormControl<IFactura['metodoPago']>;
  subtotal: FormControl<IFactura['subtotal']>;
  montoTotal: FormControl<IFactura['montoTotal']>;
  impuesto: FormControl<IFactura['impuesto']>;
  detalleFactura: FormControl<IFactura['detalleFactura']>;
  clienteFactura: FormControl<IFactura['clienteFactura']>;
  clienteColaborador: FormControl<IFactura['clienteColaborador']>;
};

export type FacturaFormGroup = FormGroup<FacturaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FacturaFormService {
  createFacturaFormGroup(factura: FacturaFormGroupInput = { id: null }): FacturaFormGroup {
    const facturaRawValue = {
      ...this.getFormDefaults(),
      ...factura,
    };
    return new FormGroup<FacturaFormGroupContent>({
      id: new FormControl(
        { value: facturaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      metodoPago: new FormControl(facturaRawValue.metodoPago),
      subtotal: new FormControl(facturaRawValue.subtotal),
      montoTotal: new FormControl(facturaRawValue.montoTotal),
      impuesto: new FormControl(facturaRawValue.impuesto),
      detalleFactura: new FormControl(facturaRawValue.detalleFactura),
      clienteFactura: new FormControl(facturaRawValue.clienteFactura),
      clienteColaborador: new FormControl(facturaRawValue.clienteColaborador),
    });
  }

  getFactura(form: FacturaFormGroup): IFactura | NewFactura {
    return form.getRawValue() as IFactura | NewFactura;
  }

  resetForm(form: FacturaFormGroup, factura: FacturaFormGroupInput): void {
    const facturaRawValue = { ...this.getFormDefaults(), ...factura };
    form.reset(
      {
        ...facturaRawValue,
        id: { value: facturaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FacturaFormDefaults {
    return {
      id: null,
    };
  }
}
