import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReservarEvento, NewReservarEvento } from '../reservar-evento.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReservarEvento for edit and NewReservarEventoFormGroupInput for create.
 */
type ReservarEventoFormGroupInput = IReservarEvento | PartialWithRequiredKeyOf<NewReservarEvento>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReservarEvento | NewReservarEvento> = Omit<T, 'fechaReservacion'> & {
  fechaReservacion?: string | null;
};

type ReservarEventoFormRawValue = FormValueOf<IReservarEvento>;

type NewReservarEventoFormRawValue = FormValueOf<NewReservarEvento>;

type ReservarEventoFormDefaults = Pick<NewReservarEvento, 'id' | 'fechaReservacion'>;

type ReservarEventoFormGroupContent = {
  id: FormControl<ReservarEventoFormRawValue['id'] | NewReservarEvento['id']>;
  fechaReservacion: FormControl<ReservarEventoFormRawValue['fechaReservacion']>;
  totalReservacion: FormControl<ReservarEventoFormRawValue['totalReservacion']>;
  clienteReservaEvento: FormControl<ReservarEventoFormRawValue['clienteReservaEvento']>;
  colaboradorReservaEvento: FormControl<ReservarEventoFormRawValue['colaboradorReservaEvento']>;
};

export type ReservarEventoFormGroup = FormGroup<ReservarEventoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReservarEventoFormService {
  createReservarEventoFormGroup(reservarEvento: ReservarEventoFormGroupInput = { id: null }): ReservarEventoFormGroup {
    const reservarEventoRawValue = this.convertReservarEventoToReservarEventoRawValue({
      ...this.getFormDefaults(),
      ...reservarEvento,
    });
    return new FormGroup<ReservarEventoFormGroupContent>({
      id: new FormControl(
        { value: reservarEventoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fechaReservacion: new FormControl(reservarEventoRawValue.fechaReservacion),
      totalReservacion: new FormControl(reservarEventoRawValue.totalReservacion),
      clienteReservaEvento: new FormControl(reservarEventoRawValue.clienteReservaEvento),
      colaboradorReservaEvento: new FormControl(reservarEventoRawValue.colaboradorReservaEvento),
    });
  }

  getReservarEvento(form: ReservarEventoFormGroup): IReservarEvento | NewReservarEvento {
    return this.convertReservarEventoRawValueToReservarEvento(
      form.getRawValue() as ReservarEventoFormRawValue | NewReservarEventoFormRawValue
    );
  }

  resetForm(form: ReservarEventoFormGroup, reservarEvento: ReservarEventoFormGroupInput): void {
    const reservarEventoRawValue = this.convertReservarEventoToReservarEventoRawValue({ ...this.getFormDefaults(), ...reservarEvento });
    form.reset(
      {
        ...reservarEventoRawValue,
        id: { value: reservarEventoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ReservarEventoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaReservacion: currentTime,
    };
  }

  private convertReservarEventoRawValueToReservarEvento(
    rawReservarEvento: ReservarEventoFormRawValue | NewReservarEventoFormRawValue
  ): IReservarEvento | NewReservarEvento {
    return {
      ...rawReservarEvento,
      fechaReservacion: dayjs(rawReservarEvento.fechaReservacion, DATE_TIME_FORMAT),
    };
  }

  private convertReservarEventoToReservarEventoRawValue(
    reservarEvento: IReservarEvento | (Partial<NewReservarEvento> & ReservarEventoFormDefaults)
  ): ReservarEventoFormRawValue | PartialWithRequiredKeyOf<NewReservarEventoFormRawValue> {
    return {
      ...reservarEvento,
      fechaReservacion: reservarEvento.fechaReservacion ? reservarEvento.fechaReservacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
