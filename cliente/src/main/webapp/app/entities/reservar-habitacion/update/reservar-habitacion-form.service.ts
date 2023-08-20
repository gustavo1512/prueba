import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReservarHabitacion, NewReservarHabitacion } from '../reservar-habitacion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReservarHabitacion for edit and NewReservarHabitacionFormGroupInput for create.
 */
type ReservarHabitacionFormGroupInput = IReservarHabitacion | PartialWithRequiredKeyOf<NewReservarHabitacion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReservarHabitacion | NewReservarHabitacion> = Omit<T, 'fechaReserva' | 'fechaInicio' | 'fechaFinal'> & {
  fechaReserva?: string | null;
  fechaInicio?: string | null;
  fechaFinal?: string | null;
};

type ReservarHabitacionFormRawValue = FormValueOf<IReservarHabitacion>;

type NewReservarHabitacionFormRawValue = FormValueOf<NewReservarHabitacion>;

type ReservarHabitacionFormDefaults = Pick<NewReservarHabitacion, 'id' | 'fechaReserva' | 'fechaInicio' | 'fechaFinal'>;

type ReservarHabitacionFormGroupContent = {
  id: FormControl<ReservarHabitacionFormRawValue['id'] | NewReservarHabitacion['id']>;
  fechaReserva: FormControl<ReservarHabitacionFormRawValue['fechaReserva']>;
  fechaInicio: FormControl<ReservarHabitacionFormRawValue['fechaInicio']>;
  fechaFinal: FormControl<ReservarHabitacionFormRawValue['fechaFinal']>;
  totalReservacion: FormControl<ReservarHabitacionFormRawValue['totalReservacion']>;
  habitacion: FormControl<ReservarHabitacionFormRawValue['habitacion']>;
  clienteReservaHabitacion: FormControl<ReservarHabitacionFormRawValue['clienteReservaHabitacion']>;
  colaboradorReservaHabitacion: FormControl<ReservarHabitacionFormRawValue['colaboradorReservaHabitacion']>;
  ocupantes: FormControl<ReservarHabitacionFormRawValue['ocupantes']>;
};

export type ReservarHabitacionFormGroup = FormGroup<ReservarHabitacionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReservarHabitacionFormService {
  createReservarHabitacionFormGroup(reservarHabitacion: ReservarHabitacionFormGroupInput = { id: null }): ReservarHabitacionFormGroup {
    const reservarHabitacionRawValue = this.convertReservarHabitacionToReservarHabitacionRawValue({
      ...this.getFormDefaults(),
      ...reservarHabitacion,
    });
    return new FormGroup<ReservarHabitacionFormGroupContent>({
      id: new FormControl(
        { value: reservarHabitacionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fechaReserva: new FormControl(reservarHabitacionRawValue.fechaReserva),
      fechaInicio: new FormControl(reservarHabitacionRawValue.fechaInicio),
      fechaFinal: new FormControl(reservarHabitacionRawValue.fechaFinal),
      totalReservacion: new FormControl(reservarHabitacionRawValue.totalReservacion),
      habitacion: new FormControl(reservarHabitacionRawValue.habitacion),
      clienteReservaHabitacion: new FormControl(reservarHabitacionRawValue.clienteReservaHabitacion),
      colaboradorReservaHabitacion: new FormControl(reservarHabitacionRawValue.colaboradorReservaHabitacion),
      ocupantes: new FormControl(reservarHabitacionRawValue.ocupantes),
    });
  }

  getReservarHabitacion(form: ReservarHabitacionFormGroup): IReservarHabitacion | NewReservarHabitacion {
    return this.convertReservarHabitacionRawValueToReservarHabitacion(
      form.getRawValue() as ReservarHabitacionFormRawValue | NewReservarHabitacionFormRawValue
    );
  }

  resetForm(form: ReservarHabitacionFormGroup, reservarHabitacion: ReservarHabitacionFormGroupInput): void {
    const reservarHabitacionRawValue = this.convertReservarHabitacionToReservarHabitacionRawValue({
      ...this.getFormDefaults(),
      ...reservarHabitacion,
    });
    form.reset(
      {
        ...reservarHabitacionRawValue,
        id: { value: reservarHabitacionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ReservarHabitacionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaReserva: currentTime,
      fechaInicio: currentTime,
      fechaFinal: currentTime,
    };
  }

  private convertReservarHabitacionRawValueToReservarHabitacion(
    rawReservarHabitacion: ReservarHabitacionFormRawValue | NewReservarHabitacionFormRawValue
  ): IReservarHabitacion | NewReservarHabitacion {
    return {
      ...rawReservarHabitacion,
      fechaReserva: dayjs(rawReservarHabitacion.fechaReserva, DATE_TIME_FORMAT),
      fechaInicio: dayjs(rawReservarHabitacion.fechaInicio, DATE_TIME_FORMAT),
      fechaFinal: dayjs(rawReservarHabitacion.fechaFinal, DATE_TIME_FORMAT),
    };
  }

  private convertReservarHabitacionToReservarHabitacionRawValue(
    reservarHabitacion: IReservarHabitacion | (Partial<NewReservarHabitacion> & ReservarHabitacionFormDefaults)
  ): ReservarHabitacionFormRawValue | PartialWithRequiredKeyOf<NewReservarHabitacionFormRawValue> {
    return {
      ...reservarHabitacion,
      fechaReserva: reservarHabitacion.fechaReserva ? reservarHabitacion.fechaReserva.format(DATE_TIME_FORMAT) : undefined,
      fechaInicio: reservarHabitacion.fechaInicio ? reservarHabitacion.fechaInicio.format(DATE_TIME_FORMAT) : undefined,
      fechaFinal: reservarHabitacion.fechaFinal ? reservarHabitacion.fechaFinal.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
