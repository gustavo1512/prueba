import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TarifaFormService } from './tarifa-form.service';
import { TarifaService } from '../service/tarifa.service';
import { ITarifa } from '../tarifa.model';

import { TarifaUpdateComponent } from './tarifa-update.component';

describe('Tarifa Management Update Component', () => {
  let comp: TarifaUpdateComponent;
  let fixture: ComponentFixture<TarifaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tarifaFormService: TarifaFormService;
  let tarifaService: TarifaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TarifaUpdateComponent],
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
      .overrideTemplate(TarifaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TarifaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tarifaFormService = TestBed.inject(TarifaFormService);
    tarifaService = TestBed.inject(TarifaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tarifa: ITarifa = { id: 456 };

      activatedRoute.data = of({ tarifa });
      comp.ngOnInit();

      expect(comp.tarifa).toEqual(tarifa);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITarifa>>();
      const tarifa = { id: 123 };
      jest.spyOn(tarifaFormService, 'getTarifa').mockReturnValue(tarifa);
      jest.spyOn(tarifaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tarifa });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tarifa }));
      saveSubject.complete();

      // THEN
      expect(tarifaFormService.getTarifa).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tarifaService.update).toHaveBeenCalledWith(expect.objectContaining(tarifa));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITarifa>>();
      const tarifa = { id: 123 };
      jest.spyOn(tarifaFormService, 'getTarifa').mockReturnValue({ id: null });
      jest.spyOn(tarifaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tarifa: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tarifa }));
      saveSubject.complete();

      // THEN
      expect(tarifaFormService.getTarifa).toHaveBeenCalled();
      expect(tarifaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITarifa>>();
      const tarifa = { id: 123 };
      jest.spyOn(tarifaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tarifa });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tarifaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
