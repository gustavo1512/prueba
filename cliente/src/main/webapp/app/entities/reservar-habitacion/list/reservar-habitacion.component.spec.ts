import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservarHabitacionService } from '../service/reservar-habitacion.service';

import { ReservarHabitacionComponent } from './reservar-habitacion.component';

describe('ReservarHabitacion Management Component', () => {
  let comp: ReservarHabitacionComponent;
  let fixture: ComponentFixture<ReservarHabitacionComponent>;
  let service: ReservarHabitacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'reservar-habitacion', component: ReservarHabitacionComponent }]),
        HttpClientTestingModule,
        ReservarHabitacionComponent,
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
      .overrideTemplate(ReservarHabitacionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservarHabitacionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ReservarHabitacionService);

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
    expect(comp.reservarHabitacions?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to reservarHabitacionService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getReservarHabitacionIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getReservarHabitacionIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
