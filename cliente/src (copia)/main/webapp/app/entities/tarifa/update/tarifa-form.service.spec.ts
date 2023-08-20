import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tarifa.test-samples';

import { TarifaFormService } from './tarifa-form.service';

describe('Tarifa Form Service', () => {
  let service: TarifaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TarifaFormService);
  });

  describe('Service methods', () => {
    describe('createTarifaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTarifaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoTarifa: expect.any(Object),
            tarifaAdulto: expect.any(Object),
            tarifaMenor: expect.any(Object),
          })
        );
      });

      it('passing ITarifa should create a new form with FormGroup', () => {
        const formGroup = service.createTarifaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipoTarifa: expect.any(Object),
            tarifaAdulto: expect.any(Object),
            tarifaMenor: expect.any(Object),
          })
        );
      });
    });

    describe('getTarifa', () => {
      it('should return NewTarifa for default Tarifa initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTarifaFormGroup(sampleWithNewData);

        const tarifa = service.getTarifa(formGroup) as any;

        expect(tarifa).toMatchObject(sampleWithNewData);
      });

      it('should return NewTarifa for empty Tarifa initial value', () => {
        const formGroup = service.createTarifaFormGroup();

        const tarifa = service.getTarifa(formGroup) as any;

        expect(tarifa).toMatchObject({});
      });

      it('should return ITarifa', () => {
        const formGroup = service.createTarifaFormGroup(sampleWithRequiredData);

        const tarifa = service.getTarifa(formGroup) as any;

        expect(tarifa).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITarifa should not enable id FormControl', () => {
        const formGroup = service.createTarifaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTarifa should disable id FormControl', () => {
        const formGroup = service.createTarifaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
