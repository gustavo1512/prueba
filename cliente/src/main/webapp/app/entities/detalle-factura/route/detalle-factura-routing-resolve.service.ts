import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDetalleFactura } from '../detalle-factura.model';
import { DetalleFacturaService } from '../service/detalle-factura.service';

export const detalleFacturaResolve = (route: ActivatedRouteSnapshot): Observable<null | IDetalleFactura> => {
  const id = route.params['id'];
  if (id) {
    return inject(DetalleFacturaService)
      .find(id)
      .pipe(
        mergeMap((detalleFactura: HttpResponse<IDetalleFactura>) => {
          if (detalleFactura.body) {
            return of(detalleFactura.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default detalleFacturaResolve;
