import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { HabitacionService } from '../service/habitacion.service';

import { HabitacionComponent } from './habitacion.component';

describe('Habitacion Management Component', () => {
  let comp: HabitacionComponent;
  let fixture: ComponentFixture<HabitacionComponent>;
  let service: HabitacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'habitacion', component: HabitacionComponent }]),
        HttpClientTestingModule,
        HabitacionComponent,
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
      .overrideTemplate(HabitacionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HabitacionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(HabitacionService);

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
    expect(comp.habitacions?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to habitacionService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getHabitacionIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getHabitacionIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
