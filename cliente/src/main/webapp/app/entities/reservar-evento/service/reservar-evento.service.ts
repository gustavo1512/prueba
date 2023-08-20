import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReservarEvento, NewReservarEvento } from '../reservar-evento.model';

export type PartialUpdateReservarEvento = Partial<IReservarEvento> & Pick<IReservarEvento, 'id'>;

type RestOf<T extends IReservarEvento | NewReservarEvento> = Omit<T, 'fechaReservacion'> & {
  fechaReservacion?: string | null;
};

export type RestReservarEvento = RestOf<IReservarEvento>;

export type NewRestReservarEvento = RestOf<NewReservarEvento>;

export type PartialUpdateRestReservarEvento = RestOf<PartialUpdateReservarEvento>;

export type EntityResponseType = HttpResponse<IReservarEvento>;
export type EntityArrayResponseType = HttpResponse<IReservarEvento[]>;

@Injectable({ providedIn: 'root' })
export class ReservarEventoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reservar-eventos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reservarEvento: NewReservarEvento): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservarEvento);
    return this.http
      .post<RestReservarEvento>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(reservarEvento: IReservarEvento): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservarEvento);
    return this.http
      .put<RestReservarEvento>(`${this.resourceUrl}/${this.getReservarEventoIdentifier(reservarEvento)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(reservarEvento: PartialUpdateReservarEvento): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservarEvento);
    return this.http
      .patch<RestReservarEvento>(`${this.resourceUrl}/${this.getReservarEventoIdentifier(reservarEvento)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReservarEvento>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReservarEvento[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReservarEventoIdentifier(reservarEvento: Pick<IReservarEvento, 'id'>): number {
    return reservarEvento.id;
  }

  compareReservarEvento(o1: Pick<IReservarEvento, 'id'> | null, o2: Pick<IReservarEvento, 'id'> | null): boolean {
    return o1 && o2 ? this.getReservarEventoIdentifier(o1) === this.getReservarEventoIdentifier(o2) : o1 === o2;
  }

  addReservarEventoToCollectionIfMissing<Type extends Pick<IReservarEvento, 'id'>>(
    reservarEventoCollection: Type[],
    ...reservarEventosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const reservarEventos: Type[] = reservarEventosToCheck.filter(isPresent);
    if (reservarEventos.length > 0) {
      const reservarEventoCollectionIdentifiers = reservarEventoCollection.map(
        reservarEventoItem => this.getReservarEventoIdentifier(reservarEventoItem)!
      );
      const reservarEventosToAdd = reservarEventos.filter(reservarEventoItem => {
        const reservarEventoIdentifier = this.getReservarEventoIdentifier(reservarEventoItem);
        if (reservarEventoCollectionIdentifiers.includes(reservarEventoIdentifier)) {
          return false;
        }
        reservarEventoCollectionIdentifiers.push(reservarEventoIdentifier);
        return true;
      });
      return [...reservarEventosToAdd, ...reservarEventoCollection];
    }
    return reservarEventoCollection;
  }

  protected convertDateFromClient<T extends IReservarEvento | NewReservarEvento | PartialUpdateReservarEvento>(
    reservarEvento: T
  ): RestOf<T> {
    return {
      ...reservarEvento,
      fechaReservacion: reservarEvento.fechaReservacion?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReservarEvento: RestReservarEvento): IReservarEvento {
    return {
      ...restReservarEvento,
      fechaReservacion: restReservarEvento.fechaReservacion ? dayjs(restReservarEvento.fechaReservacion) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReservarEvento>): HttpResponse<IReservarEvento> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReservarEvento[]>): HttpResponse<IReservarEvento[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
