import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDetalleFactura, NewDetalleFactura } from '../detalle-factura.model';

export type PartialUpdateDetalleFactura = Partial<IDetalleFactura> & Pick<IDetalleFactura, 'id'>;

export type EntityResponseType = HttpResponse<IDetalleFactura>;
export type EntityArrayResponseType = HttpResponse<IDetalleFactura[]>;

@Injectable({ providedIn: 'root' })
export class DetalleFacturaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/detalle-facturas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(detalleFactura: NewDetalleFactura): Observable<EntityResponseType> {
    return this.http.post<IDetalleFactura>(this.resourceUrl, detalleFactura, { observe: 'response' });
  }

  update(detalleFactura: IDetalleFactura): Observable<EntityResponseType> {
    return this.http.put<IDetalleFactura>(`${this.resourceUrl}/${this.getDetalleFacturaIdentifier(detalleFactura)}`, detalleFactura, {
      observe: 'response',
    });
  }

  partialUpdate(detalleFactura: PartialUpdateDetalleFactura): Observable<EntityResponseType> {
    return this.http.patch<IDetalleFactura>(`${this.resourceUrl}/${this.getDetalleFacturaIdentifier(detalleFactura)}`, detalleFactura, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDetalleFactura>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDetalleFactura[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDetalleFacturaIdentifier(detalleFactura: Pick<IDetalleFactura, 'id'>): number {
    return detalleFactura.id;
  }

  compareDetalleFactura(o1: Pick<IDetalleFactura, 'id'> | null, o2: Pick<IDetalleFactura, 'id'> | null): boolean {
    return o1 && o2 ? this.getDetalleFacturaIdentifier(o1) === this.getDetalleFacturaIdentifier(o2) : o1 === o2;
  }

  addDetalleFacturaToCollectionIfMissing<Type extends Pick<IDetalleFactura, 'id'>>(
    detalleFacturaCollection: Type[],
    ...detalleFacturasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const detalleFacturas: Type[] = detalleFacturasToCheck.filter(isPresent);
    if (detalleFacturas.length > 0) {
      const detalleFacturaCollectionIdentifiers = detalleFacturaCollection.map(
        detalleFacturaItem => this.getDetalleFacturaIdentifier(detalleFacturaItem)!
      );
      const detalleFacturasToAdd = detalleFacturas.filter(detalleFacturaItem => {
        const detalleFacturaIdentifier = this.getDetalleFacturaIdentifier(detalleFacturaItem);
        if (detalleFacturaCollectionIdentifiers.includes(detalleFacturaIdentifier)) {
          return false;
        }
        detalleFacturaCollectionIdentifiers.push(detalleFacturaIdentifier);
        return true;
      });
      return [...detalleFacturasToAdd, ...detalleFacturaCollection];
    }
    return detalleFacturaCollection;
  }
}
