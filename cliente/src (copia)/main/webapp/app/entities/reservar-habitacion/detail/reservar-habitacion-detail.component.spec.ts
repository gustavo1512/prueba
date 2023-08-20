import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservarHabitacionDetailComponent } from './reservar-habitacion-detail.component';

describe('ReservarHabitacion Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservarHabitacionDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ReservarHabitacionDetailComponent,
              resolve: { reservarHabitacion: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(ReservarHabitacionDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load reservarHabitacion on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReservarHabitacionDetailComponent);

      // THEN
      expect(instance.reservarHabitacion).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
