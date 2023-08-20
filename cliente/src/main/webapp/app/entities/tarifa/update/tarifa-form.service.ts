import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITarifa, NewTarifa } from '../tarifa.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITarifa for edit and NewTarifaFormGroupInput for create.
 */
type TarifaFormGroupInput = ITarifa | PartialWithRequiredKeyOf<NewTarifa>;

type TarifaFormDefaults = Pick<NewTarifa, 'id'>;

type TarifaFormGroupContent = {
  id: FormControl<ITarifa['id'] | NewTarifa['id']>;
  tipoTarifa: FormControl<ITarifa['tipoTarifa']>;
  tarifaAdulto: FormControl<ITarifa['tarifaAdulto']>;
  tarifaMenor: FormControl<ITarifa['tarifaMenor']>;
};

export type TarifaFormGroup = FormGroup<TarifaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TarifaFormService {
  createTarifaFormGroup(tarifa: TarifaFormGroupInput = { id: null }): TarifaFormGroup {
    const tarifaRawValue = {
      ...this.getFormDefaults(),
      ...tarifa,
    };
    return new FormGroup<TarifaFormGroupContent>({
      id: new FormControl(
        { value: tarifaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      tipoTarifa: new FormControl(tarifaRawValue.tipoTarifa),
      tarifaAdulto: new FormControl(tarifaRawValue.tarifaAdulto),
      tarifaMenor: new FormControl(tarifaRawValue.tarifaMenor),
    });
  }

  getTarifa(form: TarifaFormGroup): ITarifa | NewTarifa {
    return form.getRawValue() as ITarifa | NewTarifa;
  }

  resetForm(form: TarifaFormGroup, tarifa: TarifaFormGroupInput): void {
    const tarifaRawValue = { ...this.getFormDefaults(), ...tarifa };
    form.reset(
      {
        ...tarifaRawValue,
        id: { value: tarifaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TarifaFormDefaults {
    return {
      id: null,
    };
  }
}
