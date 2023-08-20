import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHabitacion } from '../habitacion.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../habitacion.test-samples';

import { HabitacionService } from './habitacion.service';

const requireRestSample: IHabitacion = {
  ...sampleWithRequiredData,
};

describe('Habitacion Service', () => {
  let service: HabitacionService;
  let httpMock: HttpTestingController;
  let expectedResult: IHabitacion | IHabitacion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HabitacionService);
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

    it('should create a Habitacion', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const habitacion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(habitacion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Habitacion', () => {
      const habitacion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(habitacion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Habitacion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Habitacion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Habitacion', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addHabitacionToCollectionIfMissing', () => {
      it('should add a Habitacion to an empty array', () => {
        const habitacion: IHabitacion = sampleWithRequiredData;
        expectedResult = service.addHabitacionToCollectionIfMissing([], habitacion);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(habitacion);
      });

      it('should not add a Habitacion to an array that contains it', () => {
        const habitacion: IHabitacion = sampleWithRequiredData;
        const habitacionCollection: IHabitacion[] = [
          {
            ...habitacion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHabitacionToCollectionIfMissing(habitacionCollection, habitacion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Habitacion to an array that doesn't contain it", () => {
        const habitacion: IHabitacion = sampleWithRequiredData;
        const habitacionCollection: IHabitacion[] = [sampleWithPartialData];
        expectedResult = service.addHabitacionToCollectionIfMissing(habitacionCollection, habitacion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(habitacion);
      });

      it('should add only unique Habitacion to an array', () => {
        const habitacionArray: IHabitacion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const habitacionCollection: IHabitacion[] = [sampleWithRequiredData];
        expectedResult = service.addHabitacionToCollectionIfMissing(habitacionCollection, ...habitacionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const habitacion: IHabitacion = sampleWithRequiredData;
        const habitacion2: IHabitacion = sampleWithPartialData;
        expectedResult = service.addHabitacionToCollectionIfMissing([], habitacion, habitacion2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(habitacion);
        expect(expectedResult).toContain(habitacion2);
      });

      it('should accept null and undefined values', () => {
        const habitacion: IHabitacion = sampleWithRequiredData;
        expectedResult = service.addHabitacionToCollectionIfMissing([], null, habitacion, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(habitacion);
      });

      it('should return initial array if no Habitacion is added', () => {
        const habitacionCollection: IHabitacion[] = [sampleWithRequiredData];
        expectedResult = service.addHabitacionToCollectionIfMissing(habitacionCollection, undefined, null);
        expect(expectedResult).toEqual(habitacionCollection);
      });
    });

    describe('compareHabitacion', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHabitacion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareHabitacion(entity1, entity2);
        const compareResult2 = service.compareHabitacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareHabitacion(entity1, entity2);
        const compareResult2 = service.compareHabitacion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareHabitacion(entity1, entity2);
        const compareResult2 = service.compareHabitacion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
