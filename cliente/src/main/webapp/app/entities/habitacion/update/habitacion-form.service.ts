import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IHabitacion, NewHabitacion } from '../habitacion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHabitacion for edit and NewHabitacionFormGroupInput for create.
 */
type HabitacionFormGroupInput = IHabitacion | PartialWithRequiredKeyOf<NewHabitacion>;

type HabitacionFormDefaults = Pick<NewHabitacion, 'id' | 'disponible'>;

type HabitacionFormGroupContent = {
  id: FormControl<IHabitacion['id'] | NewHabitacion['id']>;
  tipo: FormControl<IHabitacion['tipo']>;
  capacidadAdulto: FormControl<IHabitacion['capacidadAdulto']>;
  capacidadMenor: FormControl<IHabitacion['capacidadMenor']>;
  disponible: FormControl<IHabitacion['disponible']>;
  tarifa: FormControl<IHabitacion['tarifa']>;
};

export type HabitacionFormGroup = FormGroup<HabitacionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HabitacionFormService {
  createHabitacionFormGroup(habitacion: HabitacionFormGroupInput = { id: null }): HabitacionFormGroup {
    const habitacionRawValue = {
      ...this.getFormDefaults(),
      ...habitacion,
    };
    return new FormGroup<HabitacionFormGroupContent>({
      id: new FormControl(
        { value: habitacionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      tipo: new FormControl(habitacionRawValue.tipo),
      capacidadAdulto: new FormControl(habitacionRawValue.capacidadAdulto),
      capacidadMenor: new FormControl(habitacionRawValue.capacidadMenor),
      disponible: new FormControl(habitacionRawValue.disponible),
      tarifa: new FormControl(habitacionRawValue.tarifa),
    });
  }

  getHabitacion(form: HabitacionFormGroup): IHabitacion | NewHabitacion {
    return form.getRawValue() as IHabitacion | NewHabitacion;
  }

  resetForm(form: HabitacionFormGroup, habitacion: HabitacionFormGroupInput): void {
    const habitacionRawValue = { ...this.getFormDefaults(), ...habitacion };
    form.reset(
      {
        ...habitacionRawValue,
        id: { value: habitacionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): HabitacionFormDefaults {
    return {
      id: null,
      disponible: false,
    };
  }
}
