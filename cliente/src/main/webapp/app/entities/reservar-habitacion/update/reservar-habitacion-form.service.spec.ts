import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../reservar-habitacion.test-samples';

import { ReservarHabitacionFormService } from './reservar-habitacion-form.service';

describe('ReservarHabitacion Form Service', () => {
  let service: ReservarHabitacionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReservarHabitacionFormService);
  });

  describe('Service methods', () => {
    describe('createReservarHabitacionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReservarHabitacionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaReserva: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFinal: expect.any(Object),
            totalReservacion: expect.any(Object),
            habitacion: expect.any(Object),
            clienteReservaHabitacion: expect.any(Object),
            colaboradorReservaHabitacion: expect.any(Object),
            ocupantes: expect.any(Object),
          })
        );
      });

      it('passing IReservarHabitacion should create a new form with FormGroup', () => {
        const formGroup = service.createReservarHabitacionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaReserva: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFinal: expect.any(Object),
            totalReservacion: expect.any(Object),
            habitacion: expect.any(Object),
            clienteReservaHabitacion: expect.any(Object),
            colaboradorReservaHabitacion: expect.any(Object),
            ocupantes: expect.any(Object),
          })
        );
      });
    });

    describe('getReservarHabitacion', () => {
      it('should return NewReservarHabitacion for default ReservarHabitacion initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createReservarHabitacionFormGroup(sampleWithNewData);

        const reservarHabitacion = service.getReservarHabitacion(formGroup) as any;

        expect(reservarHabitacion).toMatchObject(sampleWithNewData);
      });

      it('should return NewReservarHabitacion for empty ReservarHabitacion initial value', () => {
        const formGroup = service.createReservarHabitacionFormGroup();

        const reservarHabitacion = service.getReservarHabitacion(formGroup) as any;

        expect(reservarHabitacion).toMatchObject({});
      });

      it('should return IReservarHabitacion', () => {
        const formGroup = service.createReservarHabitacionFormGroup(sampleWithRequiredData);

        const reservarHabitacion = service.getReservarHabitacion(formGroup) as any;

        expect(reservarHabitacion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReservarHabitacion should not enable id FormControl', () => {
        const formGroup = service.createReservarHabitacionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReservarHabitacion should disable id FormControl', () => {
        const formGroup = service.createReservarHabitacionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
