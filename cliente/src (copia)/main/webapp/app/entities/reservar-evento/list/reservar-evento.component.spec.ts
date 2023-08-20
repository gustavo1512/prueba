import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservarEventoService } from '../service/reservar-evento.service';

import { ReservarEventoComponent } from './reservar-evento.component';

describe('ReservarEvento Management Component', () => {
  let comp: ReservarEventoComponent;
  let fixture: ComponentFixture<ReservarEventoComponent>;
  let service: ReservarEventoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'reservar-evento', component: ReservarEventoComponent }]),
        HttpClientTestingModule,
        ReservarEventoComponent,
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
      .overrideTemplate(ReservarEventoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservarEventoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ReservarEventoService);

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
    expect(comp.reservarEventos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to reservarEventoService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getReservarEventoIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getReservarEventoIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
