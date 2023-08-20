import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IReservarEvento } from '../reservar-evento.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../reservar-evento.test-samples';

import { ReservarEventoService, RestReservarEvento } from './reservar-evento.service';

const requireRestSample: RestReservarEvento = {
  ...sampleWithRequiredData,
  fechaReservacion: sampleWithRequiredData.fechaReservacion?.toJSON(),
};

describe('ReservarEvento Service', () => {
  let service: ReservarEventoService;
  let httpMock: HttpTestingController;
  let expectedResult: IReservarEvento | IReservarEvento[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ReservarEventoService);
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

    it('should create a ReservarEvento', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const reservarEvento = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(reservarEvento).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ReservarEvento', () => {
      const reservarEvento = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(reservarEvento).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ReservarEvento', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ReservarEvento', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ReservarEvento', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReservarEventoToCollectionIfMissing', () => {
      it('should add a ReservarEvento to an empty array', () => {
        const reservarEvento: IReservarEvento = sampleWithRequiredData;
        expectedResult = service.addReservarEventoToCollectionIfMissing([], reservarEvento);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservarEvento);
      });

      it('should not add a ReservarEvento to an array that contains it', () => {
        const reservarEvento: IReservarEvento = sampleWithRequiredData;
        const reservarEventoCollection: IReservarEvento[] = [
          {
            ...reservarEvento,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReservarEventoToCollectionIfMissing(reservarEventoCollection, reservarEvento);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ReservarEvento to an array that doesn't contain it", () => {
        const reservarEvento: IReservarEvento = sampleWithRequiredData;
        const reservarEventoCollection: IReservarEvento[] = [sampleWithPartialData];
        expectedResult = service.addReservarEventoToCollectionIfMissing(reservarEventoCollection, reservarEvento);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservarEvento);
      });

      it('should add only unique ReservarEvento to an array', () => {
        const reservarEventoArray: IReservarEvento[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const reservarEventoCollection: IReservarEvento[] = [sampleWithRequiredData];
        expectedResult = service.addReservarEventoToCollectionIfMissing(reservarEventoCollection, ...reservarEventoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const reservarEvento: IReservarEvento = sampleWithRequiredData;
        const reservarEvento2: IReservarEvento = sampleWithPartialData;
        expectedResult = service.addReservarEventoToCollectionIfMissing([], reservarEvento, reservarEvento2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservarEvento);
        expect(expectedResult).toContain(reservarEvento2);
      });

      it('should accept null and undefined values', () => {
        const reservarEvento: IReservarEvento = sampleWithRequiredData;
        expectedResult = service.addReservarEventoToCollectionIfMissing([], null, reservarEvento, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservarEvento);
      });

      it('should return initial array if no ReservarEvento is added', () => {
        const reservarEventoCollection: IReservarEvento[] = [sampleWithRequiredData];
        expectedResult = service.addReservarEventoToCollectionIfMissing(reservarEventoCollection, undefined, null);
        expect(expectedResult).toEqual(reservarEventoCollection);
      });
    });

    describe('compareReservarEvento', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReservarEvento(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareReservarEvento(entity1, entity2);
        const compareResult2 = service.compareReservarEvento(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareReservarEvento(entity1, entity2);
        const compareResult2 = service.compareReservarEvento(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareReservarEvento(entity1, entity2);
        const compareResult2 = service.compareReservarEvento(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
