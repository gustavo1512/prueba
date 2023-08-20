import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TarifaDetailComponent } from './tarifa-detail.component';

describe('Tarifa Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TarifaDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TarifaDetailComponent,
              resolve: { tarifa: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(TarifaDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load tarifa on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TarifaDetailComponent);

      // THEN
      expect(instance.tarifa).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
