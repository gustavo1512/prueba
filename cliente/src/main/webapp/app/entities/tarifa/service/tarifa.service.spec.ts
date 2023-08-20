import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITarifa } from '../tarifa.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tarifa.test-samples';

import { TarifaService } from './tarifa.service';

const requireRestSample: ITarifa = {
  ...sampleWithRequiredData,
};

describe('Tarifa Service', () => {
  let service: TarifaService;
  let httpMock: HttpTestingController;
  let expectedResult: ITarifa | ITarifa[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TarifaService);
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

    it('should create a Tarifa', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const tarifa = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tarifa).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tarifa', () => {
      const tarifa = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tarifa).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tarifa', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tarifa', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Tarifa', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTarifaToCollectionIfMissing', () => {
      it('should add a Tarifa to an empty array', () => {
        const tarifa: ITarifa = sampleWithRequiredData;
        expectedResult = service.addTarifaToCollectionIfMissing([], tarifa);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tarifa);
      });

      it('should not add a Tarifa to an array that contains it', () => {
        const tarifa: ITarifa = sampleWithRequiredData;
        const tarifaCollection: ITarifa[] = [
          {
            ...tarifa,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTarifaToCollectionIfMissing(tarifaCollection, tarifa);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tarifa to an array that doesn't contain it", () => {
        const tarifa: ITarifa = sampleWithRequiredData;
        const tarifaCollection: ITarifa[] = [sampleWithPartialData];
        expectedResult = service.addTarifaToCollectionIfMissing(tarifaCollection, tarifa);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tarifa);
      });

      it('should add only unique Tarifa to an array', () => {
        const tarifaArray: ITarifa[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tarifaCollection: ITarifa[] = [sampleWithRequiredData];
        expectedResult = service.addTarifaToCollectionIfMissing(tarifaCollection, ...tarifaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tarifa: ITarifa = sampleWithRequiredData;
        const tarifa2: ITarifa = sampleWithPartialData;
        expectedResult = service.addTarifaToCollectionIfMissing([], tarifa, tarifa2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tarifa);
        expect(expectedResult).toContain(tarifa2);
      });

      it('should accept null and undefined values', () => {
        const tarifa: ITarifa = sampleWithRequiredData;
        expectedResult = service.addTarifaToCollectionIfMissing([], null, tarifa, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tarifa);
      });

      it('should return initial array if no Tarifa is added', () => {
        const tarifaCollection: ITarifa[] = [sampleWithRequiredData];
        expectedResult = service.addTarifaToCollectionIfMissing(tarifaCollection, undefined, null);
        expect(expectedResult).toEqual(tarifaCollection);
      });
    });

    describe('compareTarifa', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTarifa(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTarifa(entity1, entity2);
        const compareResult2 = service.compareTarifa(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTarifa(entity1, entity2);
        const compareResult2 = service.compareTarifa(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTarifa(entity1, entity2);
        const compareResult2 = service.compareTarifa(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
