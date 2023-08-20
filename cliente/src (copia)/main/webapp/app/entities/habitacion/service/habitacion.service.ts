import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHabitacion, NewHabitacion } from '../habitacion.model';

export type PartialUpdateHabitacion = Partial<IHabitacion> & Pick<IHabitacion, 'id'>;

export type EntityResponseType = HttpResponse<IHabitacion>;
export type EntityArrayResponseType = HttpResponse<IHabitacion[]>;

@Injectable({ providedIn: 'root' })
export class HabitacionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/habitacions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(habitacion: NewHabitacion): Observable<EntityResponseType> {
    return this.http.post<IHabitacion>(this.resourceUrl, habitacion, { observe: 'response' });
  }

  update(habitacion: IHabitacion): Observable<EntityResponseType> {
    return this.http.put<IHabitacion>(`${this.resourceUrl}/${this.getHabitacionIdentifier(habitacion)}`, habitacion, {
      observe: 'response',
    });
  }

  partialUpdate(habitacion: PartialUpdateHabitacion): Observable<EntityResponseType> {
    return this.http.patch<IHabitacion>(`${this.resourceUrl}/${this.getHabitacionIdentifier(habitacion)}`, habitacion, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHabitacion>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHabitacion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHabitacionIdentifier(habitacion: Pick<IHabitacion, 'id'>): number {
    return habitacion.id;
  }

  compareHabitacion(o1: Pick<IHabitacion, 'id'> | null, o2: Pick<IHabitacion, 'id'> | null): boolean {
    return o1 && o2 ? this.getHabitacionIdentifier(o1) === this.getHabitacionIdentifier(o2) : o1 === o2;
  }

  addHabitacionToCollectionIfMissing<Type extends Pick<IHabitacion, 'id'>>(
    habitacionCollection: Type[],
    ...habitacionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const habitacions: Type[] = habitacionsToCheck.filter(isPresent);
    if (habitacions.length > 0) {
      const habitacionCollectionIdentifiers = habitacionCollection.map(habitacionItem => this.getHabitacionIdentifier(habitacionItem)!);
      const habitacionsToAdd = habitacions.filter(habitacionItem => {
        const habitacionIdentifier = this.getHabitacionIdentifier(habitacionItem);
        if (habitacionCollectionIdentifiers.includes(habitacionIdentifier)) {
          return false;
        }
        habitacionCollectionIdentifiers.push(habitacionIdentifier);
        return true;
      });
      return [...habitacionsToAdd, ...habitacionCollection];
    }
    return habitacionCollection;
  }
}
