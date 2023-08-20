import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEvento, NewEvento } from '../evento.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvento for edit and NewEventoFormGroupInput for create.
 */
type EventoFormGroupInput = IEvento | PartialWithRequiredKeyOf<NewEvento>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEvento | NewEvento> = Omit<T, 'fechaHora'> & {
  fechaHora?: string | null;
};

type EventoFormRawValue = FormValueOf<IEvento>;

type NewEventoFormRawValue = FormValueOf<NewEvento>;

type EventoFormDefaults = Pick<NewEvento, 'id' | 'fechaHora'>;

type EventoFormGroupContent = {
  id: FormControl<EventoFormRawValue['id'] | NewEvento['id']>;
  nombreEvento: FormControl<EventoFormRawValue['nombreEvento']>;
  fechaHora: FormControl<EventoFormRawValue['fechaHora']>;
  capacidadAdulto: FormControl<EventoFormRawValue['capacidadAdulto']>;
  capacidadMenor: FormControl<EventoFormRawValue['capacidadMenor']>;
  tarifa: FormControl<EventoFormRawValue['tarifa']>;
  encargado: FormControl<EventoFormRawValue['encargado']>;
};

export type EventoFormGroup = FormGroup<EventoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventoFormService {
  createEventoFormGroup(evento: EventoFormGroupInput = { id: null }): EventoFormGroup {
    const eventoRawValue = this.convertEventoToEventoRawValue({
      ...this.getFormDefaults(),
      ...evento,
    });
    return new FormGroup<EventoFormGroupContent>({
      id: new FormControl(
        { value: eventoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombreEvento: new FormControl(eventoRawValue.nombreEvento),
      fechaHora: new FormControl(eventoRawValue.fechaHora),
      capacidadAdulto: new FormControl(eventoRawValue.capacidadAdulto),
      capacidadMenor: new FormControl(eventoRawValue.capacidadMenor),
      tarifa: new FormControl(eventoRawValue.tarifa),
      encargado: new FormControl(eventoRawValue.encargado),
    });
  }

  getEvento(form: EventoFormGroup): IEvento | NewEvento {
    return this.convertEventoRawValueToEvento(form.getRawValue() as EventoFormRawValue | NewEventoFormRawValue);
  }

  resetForm(form: EventoFormGroup, evento: EventoFormGroupInput): void {
    const eventoRawValue = this.convertEventoToEventoRawValue({ ...this.getFormDefaults(), ...evento });
    form.reset(
      {
        ...eventoRawValue,
        id: { value: eventoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EventoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaHora: currentTime,
    };
  }

  private convertEventoRawValueToEvento(rawEvento: EventoFormRawValue | NewEventoFormRawValue): IEvento | NewEvento {
    return {
      ...rawEvento,
      fechaHora: dayjs(rawEvento.fechaHora, DATE_TIME_FORMAT),
    };
  }

  private convertEventoToEventoRawValue(
    evento: IEvento | (Partial<NewEvento> & EventoFormDefaults)
  ): EventoFormRawValue | PartialWithRequiredKeyOf<NewEventoFormRawValue> {
    return {
      ...evento,
      fechaHora: evento.fechaHora ? evento.fechaHora.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
