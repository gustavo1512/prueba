import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { HabitacionDetailComponent } from './habitacion-detail.component';

describe('Habitacion Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HabitacionDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: HabitacionDetailComponent,
              resolve: { habitacion: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(HabitacionDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load habitacion on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', HabitacionDetailComponent);

      // THEN
      expect(instance.habitacion).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
