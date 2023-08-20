import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EventoFormService } from './evento-form.service';
import { EventoService } from '../service/evento.service';
import { IEvento } from '../evento.model';
import { ITarifa } from 'app/entities/tarifa/tarifa.model';
import { TarifaService } from 'app/entities/tarifa/service/tarifa.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';

import { EventoUpdateComponent } from './evento-update.component';

describe('Evento Management Update Component', () => {
  let comp: EventoUpdateComponent;
  let fixture: ComponentFixture<EventoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventoFormService: EventoFormService;
  let eventoService: EventoService;
  let tarifaService: TarifaService;
  let colaboradorService: ColaboradorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), EventoUpdateComponent],
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
      .overrideTemplate(EventoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventoFormService = TestBed.inject(EventoFormService);
    eventoService = TestBed.inject(EventoService);
    tarifaService = TestBed.inject(TarifaService);
    colaboradorService = TestBed.inject(ColaboradorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call tarifa query and add missing value', () => {
      const evento: IEvento = { id: 456 };
      const tarifa: ITarifa = { id: 2621 };
      evento.tarifa = tarifa;

      const tarifaCollection: ITarifa[] = [{ id: 22247 }];
      jest.spyOn(tarifaService, 'query').mockReturnValue(of(new HttpResponse({ body: tarifaCollection })));
      const expectedCollection: ITarifa[] = [tarifa, ...tarifaCollection];
      jest.spyOn(tarifaService, 'addTarifaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(tarifaService.query).toHaveBeenCalled();
      expect(tarifaService.addTarifaToCollectionIfMissing).toHaveBeenCalledWith(tarifaCollection, tarifa);
      expect(comp.tarifasCollection).toEqual(expectedCollection);
    });

    it('Should call Colaborador query and add missing value', () => {
      const evento: IEvento = { id: 456 };
      const encargado: IColaborador = { id: 17788 };
      evento.encargado = encargado;

      const colaboradorCollection: IColaborador[] = [{ id: 18697 }];
      jest.spyOn(colaboradorService, 'query').mockReturnValue(of(new HttpResponse({ body: colaboradorCollection })));
      const additionalColaboradors = [encargado];
      const expectedCollection: IColaborador[] = [...additionalColaboradors, ...colaboradorCollection];
      jest.spyOn(colaboradorService, 'addColaboradorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(colaboradorService.query).toHaveBeenCalled();
      expect(colaboradorService.addColaboradorToCollectionIfMissing).toHaveBeenCalledWith(
        colaboradorCollection,
        ...additionalColaboradors.map(expect.objectContaining)
      );
      expect(comp.colaboradorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const evento: IEvento = { id: 456 };
      const tarifa: ITarifa = { id: 23729 };
      evento.tarifa = tarifa;
      const encargado: IColaborador = { id: 6550 };
      evento.encargado = encargado;

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(comp.tarifasCollection).toContain(tarifa);
      expect(comp.colaboradorsSharedCollection).toContain(encargado);
      expect(comp.evento).toEqual(evento);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvento>>();
      const evento = { id: 123 };
      jest.spyOn(eventoFormService, 'getEvento').mockReturnValue(evento);
      jest.spyOn(eventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evento }));
      saveSubject.complete();

      // THEN
      expect(eventoFormService.getEvento).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventoService.update).toHaveBeenCalledWith(expect.objectContaining(evento));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvento>>();
      const evento = { id: 123 };
      jest.spyOn(eventoFormService, 'getEvento').mockReturnValue({ id: null });
      jest.spyOn(eventoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evento: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evento }));
      saveSubject.complete();

      // THEN
      expect(eventoFormService.getEvento).toHaveBeenCalled();
      expect(eventoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvento>>();
      const evento = { id: 123 };
      jest.spyOn(eventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventoService.update).toHaveBeenCalled();
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

    describe('compareColaborador', () => {
      it('Should forward to colaboradorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(colaboradorService, 'compareColaborador');
        comp.compareColaborador(entity, entity2);
        expect(colaboradorService.compareColaborador).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
