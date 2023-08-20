import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITarifa, NewTarifa } from '../tarifa.model';

export type PartialUpdateTarifa = Partial<ITarifa> & Pick<ITarifa, 'id'>;

export type EntityResponseType = HttpResponse<ITarifa>;
export type EntityArrayResponseType = HttpResponse<ITarifa[]>;

@Injectable({ providedIn: 'root' })
export class TarifaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tarifas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tarifa: NewTarifa): Observable<EntityResponseType> {
    return this.http.post<ITarifa>(this.resourceUrl, tarifa, { observe: 'response' });
  }

  update(tarifa: ITarifa): Observable<EntityResponseType> {
    return this.http.put<ITarifa>(`${this.resourceUrl}/${this.getTarifaIdentifier(tarifa)}`, tarifa, { observe: 'response' });
  }

  partialUpdate(tarifa: PartialUpdateTarifa): Observable<EntityResponseType> {
    return this.http.patch<ITarifa>(`${this.resourceUrl}/${this.getTarifaIdentifier(tarifa)}`, tarifa, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITarifa>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITarifa[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTarifaIdentifier(tarifa: Pick<ITarifa, 'id'>): number {
    return tarifa.id;
  }

  compareTarifa(o1: Pick<ITarifa, 'id'> | null, o2: Pick<ITarifa, 'id'> | null): boolean {
    return o1 && o2 ? this.getTarifaIdentifier(o1) === this.getTarifaIdentifier(o2) : o1 === o2;
  }

  addTarifaToCollectionIfMissing<Type extends Pick<ITarifa, 'id'>>(
    tarifaCollection: Type[],
    ...tarifasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tarifas: Type[] = tarifasToCheck.filter(isPresent);
    if (tarifas.length > 0) {
      const tarifaCollectionIdentifiers = tarifaCollection.map(tarifaItem => this.getTarifaIdentifier(tarifaItem)!);
      const tarifasToAdd = tarifas.filter(tarifaItem => {
        const tarifaIdentifier = this.getTarifaIdentifier(tarifaItem);
        if (tarifaCollectionIdentifiers.includes(tarifaIdentifier)) {
          return false;
        }
        tarifaCollectionIdentifiers.push(tarifaIdentifier);
        return true;
      });
      return [...tarifasToAdd, ...tarifaCollection];
    }
    return tarifaCollection;
  }
}
