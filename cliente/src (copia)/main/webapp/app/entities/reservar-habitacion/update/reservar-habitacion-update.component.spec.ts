import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReservarHabitacionFormService } from './reservar-habitacion-form.service';
import { ReservarHabitacionService } from '../service/reservar-habitacion.service';
import { IReservarHabitacion } from '../reservar-habitacion.model';
import { IHabitacion } from 'app/entities/habitacion/habitacion.model';
import { HabitacionService } from 'app/entities/habitacion/service/habitacion.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';
import { IPersona } from 'app/entities/persona/persona.model';
import { PersonaService } from 'app/entities/persona/service/persona.service';

import { ReservarHabitacionUpdateComponent } from './reservar-habitacion-update.component';

describe('ReservarHabitacion Management Update Component', () => {
  let comp: ReservarHabitacionUpdateComponent;
  let fixture: ComponentFixture<ReservarHabitacionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reservarHabitacionFormService: ReservarHabitacionFormService;
  let reservarHabitacionService: ReservarHabitacionService;
  let habitacionService: HabitacionService;
  let clienteService: ClienteService;
  let colaboradorService: ColaboradorService;
  let personaService: PersonaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ReservarHabitacionUpdateComponent],
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
      .overrideTemplate(ReservarHabitacionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservarHabitacionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reservarHabitacionFormService = TestBed.inject(ReservarHabitacionFormService);
    reservarHabitacionService = TestBed.inject(ReservarHabitacionService);
    habitacionService = TestBed.inject(HabitacionService);
    clienteService = TestBed.inject(ClienteService);
    colaboradorService = TestBed.inject(ColaboradorService);
    personaService = TestBed.inject(PersonaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call habitacion query and add missing value', () => {
      const reservarHabitacion: IReservarHabitacion = { id: 456 };
      const habitacion: IHabitacion = { id: 32279 };
      reservarHabitacion.habitacion = habitacion;

      const habitacionCollection: IHabitacion[] = [{ id: 30171 }];
      jest.spyOn(habitacionService, 'query').mockReturnValue(of(new HttpResponse({ body: habitacionCollection })));
      const expectedCollection: IHabitacion[] = [habitacion, ...habitacionCollection];
      jest.spyOn(habitacionService, 'addHabitacionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservarHabitacion });
      comp.ngOnInit();

      expect(habitacionService.query).toHaveBeenCalled();
      expect(habitacionService.addHabitacionToCollectionIfMissing).toHaveBeenCalledWith(habitacionCollection, habitacion);
      expect(comp.habitacionsCollection).toEqual(expectedCollection);
    });

    it('Should call Cliente query and add missing value', () => {
      const reservarHabitacion: IReservarHabitacion = { id: 456 };
      const clienteReservaHabitacion: ICliente = { id: 1342 };
      reservarHabitacion.clienteReservaHabitacion = clienteReservaHabitacion;

      const clienteCollection: ICliente[] = [{ id: 20013 }];
      jest.spyOn(clienteService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteCollection })));
      const additionalClientes = [clienteReservaHabitacion];
      const expectedCollection: ICliente[] = [...additionalClientes, ...clienteCollection];
      jest.spyOn(clienteService, 'addClienteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservarHabitacion });
      comp.ngOnInit();

      expect(clienteService.query).toHaveBeenCalled();
      expect(clienteService.addClienteToCollectionIfMissing).toHaveBeenCalledWith(
        clienteCollection,
        ...additionalClientes.map(expect.objectContaining)
      );
      expect(comp.clientesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Colaborador query and add missing value', () => {
      const reservarHabitacion: IReservarHabitacion = { id: 456 };
      const colaboradorReservaHabitacion: IColaborador = { id: 23026 };
      reservarHabitacion.colaboradorReservaHabitacion = colaboradorReservaHabitacion;

      const colaboradorCollection: IColaborador[] = [{ id: 11007 }];
      jest.spyOn(colaboradorService, 'query').mockReturnValue(of(new HttpResponse({ body: colaboradorCollection })));
      const additionalColaboradors = [colaboradorReservaHabitacion];
      const expectedCollection: IColaborador[] = [...additionalColaboradors, ...colaboradorCollection];
      jest.spyOn(colaboradorService, 'addColaboradorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservarHabitacion });
      comp.ngOnInit();

      expect(colaboradorService.query).toHaveBeenCalled();
      expect(colaboradorService.addColaboradorToCollectionIfMissing).toHaveBeenCalledWith(
        colaboradorCollection,
        ...additionalColaboradors.map(expect.objectContaining)
      );
      expect(comp.colaboradorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Persona query and add missing value', () => {
      const reservarHabitacion: IReservarHabitacion = { id: 456 };
      const ocupantes: IPersona = { id: 30708 };
      reservarHabitacion.ocupantes = ocupantes;

      const personaCollection: IPersona[] = [{ id: 3554 }];
      jest.spyOn(personaService, 'query').mockReturnValue(of(new HttpResponse({ body: personaCollection })));
      const additionalPersonas = [ocupantes];
      const expectedCollection: IPersona[] = [...additionalPersonas, ...personaCollection];
      jest.spyOn(personaService, 'addPersonaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservarHabitacion });
      comp.ngOnInit();

      expect(personaService.query).toHaveBeenCalled();
      expect(personaService.addPersonaToCollectionIfMissing).toHaveBeenCalledWith(
        personaCollection,
        ...additionalPersonas.map(expect.objectContaining)
      );
      expect(comp.personasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reservarHabitacion: IReservarHabitacion = { id: 456 };
      const habitacion: IHabitacion = { id: 22843 };
      reservarHabitacion.habitacion = habitacion;
      const clienteReservaHabitacion: ICliente = { id: 22612 };
      reservarHabitacion.clienteReservaHabitacion = clienteReservaHabitacion;
      const colaboradorReservaHabitacion: IColaborador = { id: 3363 };
      reservarHabitacion.colaboradorReservaHabitacion = colaboradorReservaHabitacion;
      const ocupantes: IPersona = { id: 1988 };
      reservarHabitacion.ocupantes = ocupantes;

      activatedRoute.data = of({ reservarHabitacion });
      comp.ngOnInit();

      expect(comp.habitacionsCollection).toContain(habitacion);
      expect(comp.clientesSharedCollection).toContain(clienteReservaHabitacion);
      expect(comp.colaboradorsSharedCollection).toContain(colaboradorReservaHabitacion);
      expect(comp.personasSharedCollection).toContain(ocupantes);
      expect(comp.reservarHabitacion).toEqual(reservarHabitacion);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservarHabitacion>>();
      const reservarHabitacion = { id: 123 };
      jest.spyOn(reservarHabitacionFormService, 'getReservarHabitacion').mockReturnValue(reservarHabitacion);
      jest.spyOn(reservarHabitacionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservarHabitacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservarHabitacion }));
      saveSubject.complete();

      // THEN
      expect(reservarHabitacionFormService.getReservarHabitacion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reservarHabitacionService.update).toHaveBeenCalledWith(expect.objectContaining(reservarHabitacion));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservarHabitacion>>();
      const reservarHabitacion = { id: 123 };
      jest.spyOn(reservarHabitacionFormService, 'getReservarHabitacion').mockReturnValue({ id: null });
      jest.spyOn(reservarHabitacionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservarHabitacion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservarHabitacion }));
      saveSubject.complete();

      // THEN
      expect(reservarHabitacionFormService.getReservarHabitacion).toHaveBeenCalled();
      expect(reservarHabitacionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservarHabitacion>>();
      const reservarHabitacion = { id: 123 };
      jest.spyOn(reservarHabitacionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservarHabitacion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reservarHabitacionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareHabitacion', () => {
      it('Should forward to habitacionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(habitacionService, 'compareHabitacion');
        comp.compareHabitacion(entity, entity2);
        expect(habitacionService.compareHabitacion).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCliente', () => {
      it('Should forward to clienteService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(clienteService, 'compareCliente');
        comp.compareCliente(entity, entity2);
        expect(clienteService.compareCliente).toHaveBeenCalledWith(entity, entity2);
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

    describe('comparePersona', () => {
      it('Should forward to personaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(personaService, 'comparePersona');
        comp.comparePersona(entity, entity2);
        expect(personaService.comparePersona).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
