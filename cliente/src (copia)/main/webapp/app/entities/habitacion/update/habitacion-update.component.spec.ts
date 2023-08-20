import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { HabitacionFormService } from './habitacion-form.service';
import { HabitacionService } from '../service/habitacion.service';
import { IHabitacion } from '../habitacion.model';
import { ITarifa } from 'app/entities/tarifa/tarifa.model';
import { TarifaService } from 'app/entities/tarifa/service/tarifa.service';

import { HabitacionUpdateComponent } from './habitacion-update.component';

describe('Habitacion Management Update Component', () => {
  let comp: HabitacionUpdateComponent;
  let fixture: ComponentFixture<HabitacionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let habitacionFormService: HabitacionFormService;
  let habitacionService: HabitacionService;
  let tarifaService: TarifaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), HabitacionUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(HabitacionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HabitacionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    habitacionFormService = TestBed.inject(HabitacionFormService);
    habitacionService = TestBed.inject(HabitacionService);
    tarifaService = TestBed.inject(TarifaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call tarifa query and add missing value', () => {
      const habitacion: IHabitacion = { id: 456 };
      const tarifa: ITarifa = { id: 22404 };
      habitacion.tarifa = tarifa;

      const tarifaCollection: ITarifa[] = [{ id: 14552 }];
      jest.spyOn(tarifaService, 'query').mockReturnValue(of(new HttpResponse({ body: tarifaCollection })));
      const expectedCollection: ITarifa[] = [tarifa, ...tarifaCollection];
      jest.spyOn(tarifaService, 'addTarifaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ habitacion });
      comp.ngOnInit();

      expect(tarifaService.query).toHaveBeenCalled();
      expect(tarifaService.addTarifaToCollectionIfMissing).toHaveBeenCalledWith(tarifaCollection, tarifa);
      expect(comp.tarifasCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const habitacion: IHabitacion = { id: 456 };
      const tarifa: ITarifa = { id: 13267 };
      habitacion.tarifa = tarifa;

      activatedRoute.data = of({ habitacion });
      comp.ngOnInit();

      expect(comp.tarifasCollection).toContain(tarifa);
      expect(comp.habitacion).toEqual(habitacion);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHabitacion>>();
      const habitacion = { id: 123 };
      jest.spyOn(habitacionFormService, 'getHabitacion').mockReturnValue(habitacion);
      jest.spyOn(habitacionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ habitacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: habitacion }));
      saveSubject.complete();

      // THEN
      expect(habitacionFormService.getHabitacion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(habitacionService.update).toHaveBeenCalledWith(expect.objectContaining(habitacion));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHabitacion>>();
      const habitacion = { id: 123 };
      jest.spyOn(habitacionFormService, 'getHabitacion').mockReturnValue({ id: null });
      jest.spyOn(habitacionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ habitacion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: habitacion }));
      saveSubject.complete();

      // THEN
      expect(habitacionFormService.getHabitacion).toHaveBeenCalled();
      expect(habitacionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHabitacion>>();
      const habitacion = { id: 123 };
      jest.spyOn(habitacionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ habitacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(habitacionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTarifa', () => {
      it('Should forward to tarifaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tarifaService, 'compareTarifa');
        comp.compareTarifa(entity, entity2);
        expect(tarifaService.compareTarifa).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
