import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReservarHabitacion } from '../reservar-habitacion.model';
import { ReservarHabitacionService } from '../service/reservar-habitacion.service';

export const reservarHabitacionResolve = (route: ActivatedRouteSnapshot): Observable<null | IReservarHabitacion> => {
  const id = route.params['id'];
  if (id) {
    return inject(ReservarHabitacionService)
      .find(id)
      .pipe(
        mergeMap((reservarHabitacion: HttpResponse<IReservarHabitacion>) => {
          if (reservarHabitacion.body) {
            return of(reservarHabitacion.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default reservarHabitacionResolve;
