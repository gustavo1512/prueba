import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITarifa } from '../tarifa.model';
import { TarifaService } from '../service/tarifa.service';

export const tarifaResolve = (route: ActivatedRouteSnapshot): Observable<null | ITarifa> => {
  const id = route.params['id'];
  if (id) {
    return inject(TarifaService)
      .find(id)
      .pipe(
        mergeMap((tarifa: HttpResponse<ITarifa>) => {
          if (tarifa.body) {
            return of(tarifa.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default tarifaResolve;
