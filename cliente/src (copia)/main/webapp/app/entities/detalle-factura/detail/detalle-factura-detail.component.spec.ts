import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DetalleFacturaDetailComponent } from './detalle-factura-detail.component';

describe('DetalleFactura Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleFacturaDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DetalleFacturaDetailComponent,
              resolve: { detalleFactura: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(DetalleFacturaDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load detalleFactura on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DetalleFacturaDetailComponent);

      // THEN
      expect(instance.detalleFactura).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
