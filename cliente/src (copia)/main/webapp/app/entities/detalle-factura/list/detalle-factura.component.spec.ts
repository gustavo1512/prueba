import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DetalleFacturaService } from '../service/detalle-factura.service';

import { DetalleFacturaComponent } from './detalle-factura.component';

describe('DetalleFactura Management Component', () => {
  let comp: DetalleFacturaComponent;
  let fixture: ComponentFixture<DetalleFacturaComponent>;
  let service: DetalleFacturaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'detalle-factura', component: DetalleFacturaComponent }]),
        HttpClientTestingModule,
        DetalleFacturaComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(DetalleFacturaComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DetalleFacturaComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DetalleFacturaService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.detalleFacturas?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to detalleFacturaService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDetalleFacturaIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDetalleFacturaIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
