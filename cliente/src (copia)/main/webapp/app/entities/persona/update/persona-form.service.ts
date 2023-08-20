import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPersona, NewPersona } from '../persona.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPersona for edit and NewPersonaFormGroupInput for create.
 */
type PersonaFormGroupInput = IPersona | PartialWithRequiredKeyOf<NewPersona>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPersona | NewPersona> = Omit<T, 'fechaNacimiento'> & {
  fechaNacimiento?: string | null;
};

type PersonaFormRawValue = FormValueOf<IPersona>;

type NewPersonaFormRawValue = FormValueOf<NewPersona>;

type PersonaFormDefaults = Pick<NewPersona, 'id' | 'fechaNacimiento'>;

type PersonaFormGroupContent = {
  id: FormControl<PersonaFormRawValue['id'] | NewPersona['id']>;
  fechaNacimiento: FormControl<PersonaFormRawValue['fechaNacimiento']>;
  tipo: FormControl<PersonaFormRawValue['tipo']>;
};

export type PersonaFormGroup = FormGroup<PersonaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PersonaFormService {
  createPersonaFormGroup(persona: PersonaFormGroupInput = { id: null }): PersonaFormGroup {
    const personaRawValue = this.convertPersonaToPersonaRawValue({
      ...this.getFormDefaults(),
      ...persona,
    });
    return new FormGroup<PersonaFormGroupContent>({
      id: new FormControl(
        { value: personaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fechaNacimiento: new FormControl(personaRawValue.fechaNacimiento),
      tipo: new FormControl(personaRawValue.tipo),
    });
  }

  getPersona(form: PersonaFormGroup): IPersona | NewPersona {
    return this.convertPersonaRawValueToPersona(form.getRawValue() as PersonaFormRawValue | NewPersonaFormRawValue);
  }

  resetForm(form: PersonaFormGroup, persona: PersonaFormGroupInput): void {
    const personaRawValue = this.convertPersonaToPersonaRawValue({ ...this.getFormDefaults(), ...persona });
    form.reset(
      {
        ...personaRawValue,
        id: { value: personaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PersonaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaNacimiento: currentTime,
    };
  }

  private convertPersonaRawValueToPersona(rawPersona: PersonaFormRawValue | NewPersonaFormRawValue): IPersona | NewPersona {
    return {
      ...rawPersona,
      fechaNacimiento: dayjs(rawPersona.fechaNacimiento, DATE_TIME_FORMAT),
    };
  }

  private convertPersonaToPersonaRawValue(
    persona: IPersona | (Partial<NewPersona> & PersonaFormDefaults)
  ): PersonaFormRawValue | PartialWithRequiredKeyOf<NewPersonaFormRawValue> {
    return {
      ...persona,
      fechaNacimiento: persona.fechaNacimiento ? persona.fechaNacimiento.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
