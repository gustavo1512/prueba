import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FacturaFormService } from './factura-form.service';
import { FacturaService } from '../service/factura.service';
import { IFactura } from '../factura.model';
import { IDetalleFactura } from 'app/entities/detalle-factura/detalle-factura.model';
import { DetalleFacturaService } from 'app/entities/detalle-factura/service/detalle-factura.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';

import { FacturaUpdateComponent } from './factura-update.component';

describe('Factura Management Update Component', () => {
  let comp: FacturaUpdateComponent;
  let fixture: ComponentFixture<FacturaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let facturaFormService: FacturaFormService;
  let facturaService: FacturaService;
  let detalleFacturaService: DetalleFacturaService;
  let clienteService: ClienteService;
  let colaboradorService: ColaboradorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), FacturaUpdateComponent],
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
      .overrideTemplate(FacturaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FacturaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    facturaFormService = TestBed.inject(FacturaFormService);
    facturaService = TestBed.inject(FacturaService);
    detalleFacturaService = TestBed.inject(DetalleFacturaService);
    clienteService = TestBed.inject(ClienteService);
    colaboradorService = TestBed.inject(ColaboradorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DetalleFactura query and add missing value', () => {
      const factura: IFactura = { id: 456 };
      const detalleFactura: IDetalleFactura = { id: 13571 };
      factura.detalleFactura = detalleFactura;

      const detalleFacturaCollection: IDetalleFactura[] = [{ id: 6168 }];
      jest.spyOn(detalleFacturaService, 'query').mockReturnValue(of(new HttpResponse({ body: detalleFacturaCollection })));
      const additionalDetalleFacturas = [detalleFactura];
      const expectedCollection: IDetalleFactura[] = [...additionalDetalleFacturas, ...detalleFacturaCollection];
      jest.spyOn(detalleFacturaService, 'addDetalleFacturaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ factura });
      comp.ngOnInit();

      expect(detalleFacturaService.query).toHaveBeenCalled();
      expect(detalleFacturaService.addDetalleFacturaToCollectionIfMissing).toHaveBeenCalledWith(
        detalleFacturaCollection,
        ...additionalDetalleFacturas.map(expect.objectContaining)
      );
      expect(comp.detalleFacturasSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cliente query and add missing value', () => {
      const factura: IFactura = { id: 456 };
      const clienteFactura: ICliente = { id: 24398 };
      factura.clienteFactura = clienteFactura;

      const clienteCollection: ICliente[] = [{ id: 11822 }];
      jest.spyOn(clienteService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteCollection })));
      const additionalClientes = [clienteFactura];
      const expectedCollection: ICliente[] = [...additionalClientes, ...clienteCollection];
      jest.spyOn(clienteService, 'addClienteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ factura });
      comp.ngOnInit();

      expect(clienteService.query).toHaveBeenCalled();
      expect(clienteService.addClienteToCollectionIfMissing).toHaveBeenCalledWith(
        clienteCollection,
        ...additionalClientes.map(expect.objectContaining)
      );
      expect(comp.clientesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Colaborador query and add missing value', () => {
      const factura: IFactura = { id: 456 };
      const clienteColaborador: IColaborador = { id: 29160 };
      factura.clienteColaborador = clienteColaborador;

      const colaboradorCollection: IColaborador[] = [{ id: 11269 }];
      jest.spyOn(colaboradorService, 'query').mockReturnValue(of(new HttpResponse({ body: colaboradorCollection })));
      const additionalColaboradors = [clienteColaborador];
      const expectedCollection: IColaborador[] = [...additionalColaboradors, ...colaboradorCollection];
      jest.spyOn(colaboradorService, 'addColaboradorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ factura });
      comp.ngOnInit();

      expect(colaboradorService.query).toHaveBeenCalled();
      expect(colaboradorService.addColaboradorToCollectionIfMissing).toHaveBeenCalledWith(
        colaboradorCollection,
        ...additionalColaboradors.map(expect.objectContaining)
      );
      expect(comp.colaboradorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const factura: IFactura = { id: 456 };
      const detalleFactura: IDetalleFactura = { id: 28568 };
      factura.detalleFactura = detalleFactura;
      const clienteFactura: ICliente = { id: 12582 };
      factura.clienteFactura = clienteFactura;
      const clienteColaborador: IColaborador = { id: 21428 };
      factura.clienteColaborador = clienteColaborador;

      activatedRoute.data = of({ factura });
      comp.ngOnInit();

      expect(comp.detalleFacturasSharedCollection).toContain(detalleFactura);
      expect(comp.clientesSharedCollection).toContain(clienteFactura);
      expect(comp.colaboradorsSharedCollection).toContain(clienteColaborador);
      expect(comp.factura).toEqual(factura);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFactura>>();
      const factura = { id: 123 };
      jest.spyOn(facturaFormService, 'getFactura').mockReturnValue(factura);
      jest.spyOn(facturaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ factura });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: factura }));
      saveSubject.complete();

      // THEN
      expect(facturaFormService.getFactura).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(facturaService.update).toHaveBeenCalledWith(expect.objectContaining(factura));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFactura>>();
      const factura = { id: 123 };
      jest.spyOn(facturaFormService, 'getFactura').mockReturnValue({ id: null });
      jest.spyOn(facturaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ factura: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: factura }));
      saveSubject.complete();

      // THEN
      expect(facturaFormService.getFactura).toHaveBeenCalled();
      expect(facturaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFactura>>();
      const factura = { id: 123 };
      jest.spyOn(facturaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ factura });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(facturaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDetalleFactura', () => {
      it('Should forward to detalleFacturaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(detalleFacturaService, 'compareDetalleFactura');
        comp.compareDetalleFactura(entity, entity2);
        expect(detalleFacturaService.compareDetalleFactura).toHaveBeenCalledWith(entity, entity2);
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
  });
});
