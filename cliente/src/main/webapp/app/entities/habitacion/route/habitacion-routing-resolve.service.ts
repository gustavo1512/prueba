import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHabitacion } from '../habitacion.model';
import { HabitacionService } from '../service/habitacion.service';

export const habitacionResolve = (route: ActivatedRouteSnapshot): Observable<null | IHabitacion> => {
  const id = route.params['id'];
  if (id) {
    return inject(HabitacionService)
      .find(id)
      .pipe(
        mergeMap((habitacion: HttpResponse<IHabitacion>) => {
          if (habitacion.body) {
            return of(habitacion.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default habitacionResolve;
