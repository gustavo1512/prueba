import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EventoDetailComponent } from './evento-detail.component';

describe('Evento Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventoDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: EventoDetailComponent,
              resolve: { evento: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(EventoDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load evento on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EventoDetailComponent);

      // THEN
      expect(instance.evento).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
