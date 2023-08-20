import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TarifaService } from '../service/tarifa.service';

import { TarifaComponent } from './tarifa.component';

describe('Tarifa Management Component', () => {
  let comp: TarifaComponent;
  let fixture: ComponentFixture<TarifaComponent>;
  let service: TarifaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'tarifa', component: TarifaComponent }]), HttpClientTestingModule, TarifaComponent],
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
      .overrideTemplate(TarifaComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TarifaComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TarifaService);

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
    expect(comp.tarifas?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to tarifaService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTarifaIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTarifaIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
