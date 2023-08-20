import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservarEventoDetailComponent } from './reservar-evento-detail.component';

describe('ReservarEvento Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservarEventoDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ReservarEventoDetailComponent,
              resolve: { reservarEvento: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(ReservarEventoDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load reservarEvento on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReservarEventoDetailComponent);

      // THEN
      expect(instance.reservarEvento).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
