import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDetalleFactura } from '../detalle-factura.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../detalle-factura.test-samples';

import { DetalleFacturaService } from './detalle-factura.service';

const requireRestSample: IDetalleFactura = {
  ...sampleWithRequiredData,
};

describe('DetalleFactura Service', () => {
  let service: DetalleFacturaService;
  let httpMock: HttpTestingController;
  let expectedResult: IDetalleFactura | IDetalleFactura[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DetalleFacturaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DetalleFactura', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const detalleFactura = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(detalleFactura).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DetalleFactura', () => {
      const detalleFactura = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(detalleFactura).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DetalleFactura', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DetalleFactura', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DetalleFactura', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDetalleFacturaToCollectionIfMissing', () => {
      it('should add a DetalleFactura to an empty array', () => {
        const detalleFactura: IDetalleFactura = sampleWithRequiredData;
        expectedResult = service.addDetalleFacturaToCollectionIfMissing([], detalleFactura);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(detalleFactura);
      });

      it('should not add a DetalleFactura to an array that contains it', () => {
        const detalleFactura: IDetalleFactura = sampleWithRequiredData;
        const detalleFacturaCollection: IDetalleFactura[] = [
          {
            ...detalleFactura,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDetalleFacturaToCollectionIfMissing(detalleFacturaCollection, detalleFactura);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DetalleFactura to an array that doesn't contain it", () => {
        const detalleFactura: IDetalleFactura = sampleWithRequiredData;
        const detalleFacturaCollection: IDetalleFactura[] = [sampleWithPartialData];
        expectedResult = service.addDetalleFacturaToCollectionIfMissing(detalleFacturaCollection, detalleFactura);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(detalleFactura);
      });

      it('should add only unique DetalleFactura to an array', () => {
        const detalleFacturaArray: IDetalleFactura[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const detalleFacturaCollection: IDetalleFactura[] = [sampleWithRequiredData];
        expectedResult = service.addDetalleFacturaToCollectionIfMissing(detalleFacturaCollection, ...detalleFacturaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const detalleFactura: IDetalleFactura = sampleWithRequiredData;
        const detalleFactura2: IDetalleFactura = sampleWithPartialData;
        expectedResult = service.addDetalleFacturaToCollectionIfMissing([], detalleFactura, detalleFactura2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(detalleFactura);
        expect(expectedResult).toContain(detalleFactura2);
      });

      it('should accept null and undefined values', () => {
        const detalleFactura: IDetalleFactura = sampleWithRequiredData;
        expectedResult = service.addDetalleFacturaToCollectionIfMissing([], null, detalleFactura, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(detalleFactura);
      });

      it('should return initial array if no DetalleFactura is added', () => {
        const detalleFacturaCollection: IDetalleFactura[] = [sampleWithRequiredData];
        expectedResult = service.addDetalleFacturaToCollectionIfMissing(detalleFacturaCollection, undefined, null);
        expect(expectedResult).toEqual(detalleFacturaCollection);
      });
    });

    describe('compareDetalleFactura', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDetalleFactura(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDetalleFactura(entity1, entity2);
        const compareResult2 = service.compareDetalleFactura(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDetalleFactura(entity1, entity2);
        const compareResult2 = service.compareDetalleFactura(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDetalleFactura(entity1, entity2);
        const compareResult2 = service.compareDetalleFactura(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
