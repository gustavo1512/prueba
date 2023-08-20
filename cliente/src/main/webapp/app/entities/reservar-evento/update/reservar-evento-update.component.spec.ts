import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReservarEventoFormService } from './reservar-evento-form.service';
import { ReservarEventoService } from '../service/reservar-evento.service';
import { IReservarEvento } from '../reservar-evento.model';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';

import { ReservarEventoUpdateComponent } from './reservar-evento-update.component';

describe('ReservarEvento Management Update Component', () => {
  let comp: ReservarEventoUpdateComponent;
  let fixture: ComponentFixture<ReservarEventoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reservarEventoFormService: ReservarEventoFormService;
  let reservarEventoService: ReservarEventoService;
  let clienteService: ClienteService;
  let colaboradorService: ColaboradorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ReservarEventoUpdateComponent],
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
      .overrideTemplate(ReservarEventoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservarEventoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reservarEventoFormService = TestBed.inject(ReservarEventoFormService);
    reservarEventoService = TestBed.inject(ReservarEventoService);
    clienteService = TestBed.inject(ClienteService);
    colaboradorService = TestBed.inject(ColaboradorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Cliente query and add missing value', () => {
      const reservarEvento: IReservarEvento = { id: 456 };
      const clienteReservaEvento: ICliente = { id: 15871 };
      reservarEvento.clienteReservaEvento = clienteReservaEvento;

      const clienteCollection: ICliente[] = [{ id: 1272 }];
      jest.spyOn(clienteService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteCollection })));
      const additionalClientes = [clienteReservaEvento];
      const expectedCollection: ICliente[] = [...additionalClientes, ...clienteCollection];
      jest.spyOn(clienteService, 'addClienteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservarEvento });
      comp.ngOnInit();

      expect(clienteService.query).toHaveBeenCalled();
      expect(clienteService.addClienteToCollectionIfMissing).toHaveBeenCalledWith(
        clienteCollection,
        ...additionalClientes.map(expect.objectContaining)
      );
      expect(comp.clientesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Colaborador query and add missing value', () => {
      const reservarEvento: IReservarEvento = { id: 456 };
      const colaboradorReservaEvento: IColaborador = { id: 24423 };
      reservarEvento.colaboradorReservaEvento = colaboradorReservaEvento;

      const colaboradorCollection: IColaborador[] = [{ id: 14564 }];
      jest.spyOn(colaboradorService, 'query').mockReturnValue(of(new HttpResponse({ body: colaboradorCollection })));
      const additionalColaboradors = [colaboradorReservaEvento];
      const expectedCollection: IColaborador[] = [...additionalColaboradors, ...colaboradorCollection];
      jest.spyOn(colaboradorService, 'addColaboradorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservarEvento });
      comp.ngOnInit();

      expect(colaboradorService.query).toHaveBeenCalled();
      expect(colaboradorService.addColaboradorToCollectionIfMissing).toHaveBeenCalledWith(
        colaboradorCollection,
        ...additionalColaboradors.map(expect.objectContaining)
      );
      expect(comp.colaboradorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reservarEvento: IReservarEvento = { id: 456 };
      const clienteReservaEvento: ICliente = { id: 15541 };
      reservarEvento.clienteReservaEvento = clienteReservaEvento;
      const colaboradorReservaEvento: IColaborador = { id: 6588 };
      reservarEvento.colaboradorReservaEvento = colaboradorReservaEvento;

      activatedRoute.data = of({ reservarEvento });
      comp.ngOnInit();

      expect(comp.clientesSharedCollection).toContain(clienteReservaEvento);
      expect(comp.colaboradorsSharedCollection).toContain(colaboradorReservaEvento);
      expect(comp.reservarEvento).toEqual(reservarEvento);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservarEvento>>();
      const reservarEvento = { id: 123 };
      jest.spyOn(reservarEventoFormService, 'getReservarEvento').mockReturnValue(reservarEvento);
      jest.spyOn(reservarEventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservarEvento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservarEvento }));
      saveSubject.complete();

      // THEN
      expect(reservarEventoFormService.getReservarEvento).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reservarEventoService.update).toHaveBeenCalledWith(expect.objectContaining(reservarEvento));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservarEvento>>();
      const reservarEvento = { id: 123 };
      jest.spyOn(reservarEventoFormService, 'getReservarEvento').mockReturnValue({ id: null });
      jest.spyOn(reservarEventoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservarEvento: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservarEvento }));
      saveSubject.complete();

      // THEN
      expect(reservarEventoFormService.getReservarEvento).toHaveBeenCalled();
      expect(reservarEventoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservarEvento>>();
      const reservarEvento = { id: 123 };
      jest.spyOn(reservarEventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservarEvento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reservarEventoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
  });
});
