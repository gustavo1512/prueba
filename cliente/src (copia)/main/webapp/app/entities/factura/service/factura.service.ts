import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFactura, NewFactura } from '../factura.model';

export type PartialUpdateFactura = Partial<IFactura> & Pick<IFactura, 'id'>;

export type EntityResponseType = HttpResponse<IFactura>;
export type EntityArrayResponseType = HttpResponse<IFactura[]>;

@Injectable({ providedIn: 'root' })
export class FacturaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/facturas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(factura: NewFactura): Observable<EntityResponseType> {
    return this.http.post<IFactura>(this.resourceUrl, factura, { observe: 'response' });
  }

  update(factura: IFactura): Observable<EntityResponseType> {
    return this.http.put<IFactura>(`${this.resourceUrl}/${this.getFacturaIdentifier(factura)}`, factura, { observe: 'response' });
  }

  partialUpdate(factura: PartialUpdateFactura): Observable<EntityResponseType> {
    return this.http.patch<IFactura>(`${this.resourceUrl}/${this.getFacturaIdentifier(factura)}`, factura, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFactura>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFactura[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFacturaIdentifier(factura: Pick<IFactura, 'id'>): number {
    return factura.id;
  }

  compareFactura(o1: Pick<IFactura, 'id'> | null, o2: Pick<IFactura, 'id'> | null): boolean {
    return o1 && o2 ? this.getFacturaIdentifier(o1) === this.getFacturaIdentifier(o2) : o1 === o2;
  }

  addFacturaToCollectionIfMissing<Type extends Pick<IFactura, 'id'>>(
    facturaCollection: Type[],
    ...facturasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const facturas: Type[] = facturasToCheck.filter(isPresent);
    if (facturas.length > 0) {
      const facturaCollectionIdentifiers = facturaCollection.map(facturaItem => this.getFacturaIdentifier(facturaItem)!);
      const facturasToAdd = facturas.filter(facturaItem => {
        const facturaIdentifier = this.getFacturaIdentifier(facturaItem);
        if (facturaCollectionIdentifiers.includes(facturaIdentifier)) {
          return false;
        }
        facturaCollectionIdentifiers.push(facturaIdentifier);
        return true;
      });
      return [...facturasToAdd, ...facturaCollection];
    }
    return facturaCollection;
  }
}
