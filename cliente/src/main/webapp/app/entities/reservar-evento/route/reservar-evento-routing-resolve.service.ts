import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReservarEvento } from '../reservar-evento.model';
import { ReservarEventoService } from '../service/reservar-evento.service';

export const reservarEventoResolve = (route: ActivatedRouteSnapshot): Observable<null | IReservarEvento> => {
  const id = route.params['id'];
  if (id) {
    return inject(ReservarEventoService)
      .find(id)
      .pipe(
        mergeMap((reservarEvento: HttpResponse<IReservarEvento>) => {
          if (reservarEvento.body) {
            return of(reservarEvento.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default reservarEventoResolve;
