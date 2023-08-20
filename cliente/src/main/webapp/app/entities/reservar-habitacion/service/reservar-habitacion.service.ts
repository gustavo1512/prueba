import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReservarHabitacion, NewReservarHabitacion } from '../reservar-habitacion.model';

export type PartialUpdateReservarHabitacion = Partial<IReservarHabitacion> & Pick<IReservarHabitacion, 'id'>;

type RestOf<T extends IReservarHabitacion | NewReservarHabitacion> = Omit<T, 'fechaReserva' | 'fechaInicio' | 'fechaFinal'> & {
  fechaReserva?: string | null;
  fechaInicio?: string | null;
  fechaFinal?: string | null;
};

export type RestReservarHabitacion = RestOf<IReservarHabitacion>;

export type NewRestReservarHabitacion = RestOf<NewReservarHabitacion>;

export type PartialUpdateRestReservarHabitacion = RestOf<PartialUpdateReservarHabitacion>;

export type EntityResponseType = HttpResponse<IReservarHabitacion>;
export type EntityArrayResponseType = HttpResponse<IReservarHabitacion[]>;

@Injectable({ providedIn: 'root' })
export class ReservarHabitacionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reservar-habitacions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reservarHabitacion: NewReservarHabitacion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservarHabitacion);
    return this.http
      .post<RestReservarHabitacion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(reservarHabitacion: IReservarHabitacion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservarHabitacion);
    return this.http
      .put<RestReservarHabitacion>(`${this.resourceUrl}/${this.getReservarHabitacionIdentifier(reservarHabitacion)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(reservarHabitacion: PartialUpdateReservarHabitacion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservarHabitacion);
    return this.http
      .patch<RestReservarHabitacion>(`${this.resourceUrl}/${this.getReservarHabitacionIdentifier(reservarHabitacion)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReservarHabitacion>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReservarHabitacion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReservarHabitacionIdentifier(reservarHabitacion: Pick<IReservarHabitacion, 'id'>): number {
    return reservarHabitacion.id;
  }

  compareReservarHabitacion(o1: Pick<IReservarHabitacion, 'id'> | null, o2: Pick<IReservarHabitacion, 'id'> | null): boolean {
    return o1 && o2 ? this.getReservarHabitacionIdentifier(o1) === this.getReservarHabitacionIdentifier(o2) : o1 === o2;
  }

  addReservarHabitacionToCollectionIfMissing<Type extends Pick<IReservarHabitacion, 'id'>>(
    reservarHabitacionCollection: Type[],
    ...reservarHabitacionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const reservarHabitacions: Type[] = reservarHabitacionsToCheck.filter(isPresent);
    if (reservarHabitacions.length > 0) {
      const reservarHabitacionCollectionIdentifiers = reservarHabitacionCollection.map(
        reservarHabitacionItem => this.getReservarHabitacionIdentifier(reservarHabitacionItem)!
      );
      const reservarHabitacionsToAdd = reservarHabitacions.filter(reservarHabitacionItem => {
        const reservarHabitacionIdentifier = this.getReservarHabitacionIdentifier(reservarHabitacionItem);
        if (reservarHabitacionCollectionIdentifiers.includes(reservarHabitacionIdentifier)) {
          return false;
        }
        reservarHabitacionCollectionIdentifiers.push(reservarHabitacionIdentifier);
        return true;
      });
      return [...reservarHabitacionsToAdd, ...reservarHabitacionCollection];
    }
    return reservarHabitacionCollection;
  }

  protected convertDateFromClient<T extends IReservarHabitacion | NewReservarHabitacion | PartialUpdateReservarHabitacion>(
    reservarHabitacion: T
  ): RestOf<T> {
    return {
      ...reservarHabitacion,
      fechaReserva: reservarHabitacion.fechaReserva?.toJSON() ?? null,
      fechaInicio: reservarHabitacion.fechaInicio?.toJSON() ?? null,
      fechaFinal: reservarHabitacion.fechaFinal?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReservarHabitacion: RestReservarHabitacion): IReservarHabitacion {
    return {
      ...restReservarHabitacion,
      fechaReserva: restReservarHabitacion.fechaReserva ? dayjs(restReservarHabitacion.fechaReserva) : undefined,
      fechaInicio: restReservarHabitacion.fechaInicio ? dayjs(restReservarHabitacion.fechaInicio) : undefined,
      fechaFinal: restReservarHabitacion.fechaFinal ? dayjs(restReservarHabitacion.fechaFinal) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReservarHabitacion>): HttpResponse<IReservarHabitacion> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReservarHabitacion[]>): HttpResponse<IReservarHabitacion[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
