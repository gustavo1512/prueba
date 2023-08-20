import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DetalleFacturaFormService } from './detalle-factura-form.service';
import { DetalleFacturaService } from '../service/detalle-factura.service';
import { IDetalleFactura } from '../detalle-factura.model';
import { IReservarHabitacion } from 'app/entities/reservar-habitacion/reservar-habitacion.model';
import { ReservarHabitacionService } from 'app/entities/reservar-habitacion/service/reservar-habitacion.service';
import { IReservarEvento } from 'app/entities/reservar-evento/reservar-evento.model';
import { ReservarEventoService } from 'app/entities/reservar-evento/service/reservar-evento.service';

import { DetalleFacturaUpdateComponent } from './detalle-factura-update.component';

describe('DetalleFactura Management Update Component', () => {
  let comp: DetalleFacturaUpdateComponent;
  let fixture: ComponentFixture<DetalleFacturaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let detalleFacturaFormService: DetalleFacturaFormService;
  let detalleFacturaService: DetalleFacturaService;
  let reservarHabitacionService: ReservarHabitacionService;
  let reservarEventoService: ReservarEventoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), DetalleFacturaUpdateComponent],
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
      .overrideTemplate(DetalleFacturaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DetalleFacturaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    detalleFacturaFormService = TestBed.inject(DetalleFacturaFormService);
    detalleFacturaService = TestBed.inject(DetalleFacturaService);
    reservarHabitacionService = TestBed.inject(ReservarHabitacionService);
    reservarEventoService = TestBed.inject(ReservarEventoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call habitacionReservada query and add missing value', () => {
      const detalleFactura: IDetalleFactura = { id: 456 };
      const habitacionReservada: IReservarHabitacion = { id: 27837 };
      detalleFactura.habitacionReservada = habitacionReservada;

      const habitacionReservadaCollection: IReservarHabitacion[] = [{ id: 12366 }];
      jest.spyOn(reservarHabitacionService, 'query').mockReturnValue(of(new HttpResponse({ body: habitacionReservadaCollection })));
      const expectedCollection: IReservarHabitacion[] = [habitacionReservada, ...habitacionReservadaCollection];
      jest.spyOn(reservarHabitacionService, 'addReservarHabitacionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ detalleFactura });
      comp.ngOnInit();

      expect(reservarHabitacionService.query).toHaveBeenCalled();
      expect(reservarHabitacionService.addReservarHabitacionToCollectionIfMissing).toHaveBeenCalledWith(
        habitacionReservadaCollection,
        habitacionReservada
      );
      expect(comp.habitacionReservadasCollection).toEqual(expectedCollection);
    });

    it('Should call eventoReservado query and add missing value', () => {
      const detalleFactura: IDetalleFactura = { id: 456 };
      const eventoReservado: IReservarEvento = { id: 24731 };
      detalleFactura.eventoReservado = eventoReservado;

      const eventoReservadoCollection: IReservarEvento[] = [{ id: 9861 }];
      jest.spyOn(reservarEventoService, 'query').mockReturnValue(of(new HttpResponse({ body: eventoReservadoCollection })));
      const expectedCollection: IReservarEvento[] = [eventoReservado, ...eventoReservadoCollection];
      jest.spyOn(reservarEventoService, 'addReservarEventoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ detalleFactura });
      comp.ngOnInit();

      expect(reservarEventoService.query).toHaveBeenCalled();
      expect(reservarEventoService.addReservarEventoToCollectionIfMissing).toHaveBeenCalledWith(eventoReservadoCollection, eventoReservado);
      expect(comp.eventoReservadosCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const detalleFactura: IDetalleFactura = { id: 456 };
      const habitacionReservada: IReservarHabitacion = { id: 14273 };
      detalleFactura.habitacionReservada = habitacionReservada;
      const eventoReservado: IReservarEvento = { id: 9970 };
      detalleFactura.eventoReservado = eventoReservado;

      activatedRoute.data = of({ detalleFactura });
      comp.ngOnInit();

      expect(comp.habitacionReservadasCollection).toContain(habitacionReservada);
      expect(comp.eventoReservadosCollection).toContain(eventoReservado);
      expect(comp.detalleFactura).toEqual(detalleFactura);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDetalleFactura>>();
      const detalleFactura = { id: 123 };
      jest.spyOn(detalleFacturaFormService, 'getDetalleFactura').mockReturnValue(detalleFactura);
      jest.spyOn(detalleFacturaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ detalleFactura });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: detalleFactura }));
      saveSubject.complete();

      // THEN
      expect(detalleFacturaFormService.getDetalleFactura).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(detalleFacturaService.update).toHaveBeenCalledWith(expect.objectContaining(detalleFactura));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDetalleFactura>>();
      const detalleFactura = { id: 123 };
      jest.spyOn(detalleFacturaFormService, 'getDetalleFactura').mockReturnValue({ id: null });
      jest.spyOn(detalleFacturaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ detalleFactura: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: detalleFactura }));
      saveSubject.complete();

      // THEN
      expect(detalleFacturaFormService.getDetalleFactura).toHaveBeenCalled();
      expect(detalleFacturaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDetalleFactura>>();
      const detalleFactura = { id: 123 };
      jest.spyOn(detalleFacturaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ detalleFactura });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(detalleFacturaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareReservarHabitacion', () => {
      it('Should forward to reservarHabitacionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(reservarHabitacionService, 'compareReservarHabitacion');
        comp.compareReservarHabitacion(entity, entity2);
        expect(reservarHabitacionService.compareReservarHabitacion).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareReservarEvento', () => {
      it('Should forward to reservarEventoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(reservarEventoService, 'compareReservarEvento');
        comp.compareReservarEvento(entity, entity2);
        expect(reservarEventoService.compareReservarEvento).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
