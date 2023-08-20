import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEvento } from '../evento.model';
import { EventoService } from '../service/evento.service';

export const eventoResolve = (route: ActivatedRouteSnapshot): Observable<null | IEvento> => {
  const id = route.params['id'];
  if (id) {
    return inject(EventoService)
      .find(id)
      .pipe(
        mergeMap((evento: HttpResponse<IEvento>) => {
          if (evento.body) {
            return of(evento.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default eventoResolve;
