import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../reservar-evento.test-samples';

import { ReservarEventoFormService } from './reservar-evento-form.service';

describe('ReservarEvento Form Service', () => {
  let service: ReservarEventoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReservarEventoFormService);
  });

  describe('Service methods', () => {
    describe('createReservarEventoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReservarEventoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaReservacion: expect.any(Object),
            totalReservacion: expect.any(Object),
            clienteReservaEvento: expect.any(Object),
            colaboradorReservaEvento: expect.any(Object),
          })
        );
      });

      it('passing IReservarEvento should create a new form with FormGroup', () => {
        const formGroup = service.createReservarEventoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaReservacion: expect.any(Object),
            totalReservacion: expect.any(Object),
            clienteReservaEvento: expect.any(Object),
            colaboradorReservaEvento: expect.any(Object),
          })
        );
      });
    });

    describe('getReservarEvento', () => {
      it('should return NewReservarEvento for default ReservarEvento initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createReservarEventoFormGroup(sampleWithNewData);

        const reservarEvento = service.getReservarEvento(formGroup) as any;

        expect(reservarEvento).toMatchObject(sampleWithNewData);
      });

      it('should return NewReservarEvento for empty ReservarEvento initial value', () => {
        const formGroup = service.createReservarEventoFormGroup();

        const reservarEvento = service.getReservarEvento(formGroup) as any;

        expect(reservarEvento).toMatchObject({});
      });

      it('should return IReservarEvento', () => {
        const formGroup = service.createReservarEventoFormGroup(sampleWithRequiredData);

        const reservarEvento = service.getReservarEvento(formGroup) as any;

        expect(reservarEvento).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReservarEvento should not enable id FormControl', () => {
        const formGroup = service.createReservarEventoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReservarEvento should disable id FormControl', () => {
        const formGroup = service.createReservarEventoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
