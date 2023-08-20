import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IReservarHabitacion } from '../reservar-habitacion.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../reservar-habitacion.test-samples';

import { ReservarHabitacionService, RestReservarHabitacion } from './reservar-habitacion.service';

const requireRestSample: RestReservarHabitacion = {
  ...sampleWithRequiredData,
  fechaReserva: sampleWithRequiredData.fechaReserva?.toJSON(),
  fechaInicio: sampleWithRequiredData.fechaInicio?.toJSON(),
  fechaFinal: sampleWithRequiredData.fechaFinal?.toJSON(),
};

describe('ReservarHabitacion Service', () => {
  let service: ReservarHabitacionService;
  let httpMock: HttpTestingController;
  let expectedResult: IReservarHabitacion | IReservarHabitacion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ReservarHabitacionService);
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

    it('should create a ReservarHabitacion', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const reservarHabitacion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(reservarHabitacion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ReservarHabitacion', () => {
      const reservarHabitacion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(reservarHabitacion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ReservarHabitacion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ReservarHabitacion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ReservarHabitacion', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReservarHabitacionToCollectionIfMissing', () => {
      it('should add a ReservarHabitacion to an empty array', () => {
        const reservarHabitacion: IReservarHabitacion = sampleWithRequiredData;
        expectedResult = service.addReservarHabitacionToCollectionIfMissing([], reservarHabitacion);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservarHabitacion);
      });

      it('should not add a ReservarHabitacion to an array that contains it', () => {
        const reservarHabitacion: IReservarHabitacion = sampleWithRequiredData;
        const reservarHabitacionCollection: IReservarHabitacion[] = [
          {
            ...reservarHabitacion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReservarHabitacionToCollectionIfMissing(reservarHabitacionCollection, reservarHabitacion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ReservarHabitacion to an array that doesn't contain it", () => {
        const reservarHabitacion: IReservarHabitacion = sampleWithRequiredData;
        const reservarHabitacionCollection: IReservarHabitacion[] = [sampleWithPartialData];
        expectedResult = service.addReservarHabitacionToCollectionIfMissing(reservarHabitacionCollection, reservarHabitacion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservarHabitacion);
      });

      it('should add only unique ReservarHabitacion to an array', () => {
        const reservarHabitacionArray: IReservarHabitacion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const reservarHabitacionCollection: IReservarHabitacion[] = [sampleWithRequiredData];
        expectedResult = service.addReservarHabitacionToCollectionIfMissing(reservarHabitacionCollection, ...reservarHabitacionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const reservarHabitacion: IReservarHabitacion = sampleWithRequiredData;
        const reservarHabitacion2: IReservarHabitacion = sampleWithPartialData;
        expectedResult = service.addReservarHabitacionToCollectionIfMissing([], reservarHabitacion, reservarHabitacion2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservarHabitacion);
        expect(expectedResult).toContain(reservarHabitacion2);
      });

      it('should accept null and undefined values', () => {
        const reservarHabitacion: IReservarHabitacion = sampleWithRequiredData;
        expectedResult = service.addReservarHabitacionToCollectionIfMissing([], null, reservarHabitacion, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservarHabitacion);
      });

      it('should return initial array if no ReservarHabitacion is added', () => {
        const reservarHabitacionCollection: IReservarHabitacion[] = [sampleWithRequiredData];
        expectedResult = service.addReservarHabitacionToCollectionIfMissing(reservarHabitacionCollection, undefined, null);
        expect(expectedResult).toEqual(reservarHabitacionCollection);
      });
    });

    describe('compareReservarHabitacion', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReservarHabitacion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareReservarHabitacion(entity1, entity2);
        const compareResult2 = service.compareReservarHabitacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareReservarHabitacion(entity1, entity2);
        const compareResult2 = service.compareReservarHabitacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareReservarHabitacion(entity1, entity2);
        const compareResult2 = service.compareReservarHabitacion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
