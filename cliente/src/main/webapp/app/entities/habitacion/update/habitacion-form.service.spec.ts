import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../habitacion.test-samples';

import { HabitacionFormService } from './habitacion-form.service';

describe('Habitacion Form Service', () => {
  let service: HabitacionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HabitacionFormService);
  });

  describe('Service methods', () => {
    describe('createHabitacionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHabitacionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            capacidadAdulto: expect.any(Object),
            capacidadMenor: expect.any(Object),
            disponible: expect.any(Object),
            tarifa: expect.any(Object),
          })
        );
      });

      it('passing IHabitacion should create a new form with FormGroup', () => {
        const formGroup = service.createHabitacionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            capacidadAdulto: expect.any(Object),
            capacidadMenor: expect.any(Object),
            disponible: expect.any(Object),
            tarifa: expect.any(Object),
          })
        );
      });
    });

    describe('getHabitacion', () => {
      it('should return NewHabitacion for default Habitacion initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createHabitacionFormGroup(sampleWithNewData);

        const habitacion = service.getHabitacion(formGroup) as any;

        expect(habitacion).toMatchObject(sampleWithNewData);
      });

      it('should return NewHabitacion for empty Habitacion initial value', () => {
        const formGroup = service.createHabitacionFormGroup();

        const habitacion = service.getHabitacion(formGroup) as any;

        expect(habitacion).toMatchObject({});
      });

      it('should return IHabitacion', () => {
        const formGroup = service.createHabitacionFormGroup(sampleWithRequiredData);

        const habitacion = service.getHabitacion(formGroup) as any;

        expect(habitacion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHabitacion should not enable id FormControl', () => {
        const formGroup = service.createHabitacionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHabitacion should disable id FormControl', () => {
        const formGroup = service.createHabitacionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
