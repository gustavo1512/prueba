import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PersonaDetailComponent } from './persona-detail.component';

describe('Persona Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonaDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PersonaDetailComponent,
              resolve: { persona: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(PersonaDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load persona on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PersonaDetailComponent);

      // THEN
      expect(instance.persona).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
