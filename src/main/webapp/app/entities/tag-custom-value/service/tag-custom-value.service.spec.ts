import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITagCustomValue, TagCustomValue } from '../tag-custom-value.model';

import { TagCustomValueService } from './tag-custom-value.service';

describe('Service Tests', () => {
  describe('TagCustomValue Service', () => {
    let service: TagCustomValueService;
    let httpMock: HttpTestingController;
    let elemDefault: ITagCustomValue;
    let expectedResult: ITagCustomValue | ITagCustomValue[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(TagCustomValueService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        value: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a TagCustomValue', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new TagCustomValue()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a TagCustomValue', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            value: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a TagCustomValue', () => {
        const patchObject = Object.assign({}, new TagCustomValue());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of TagCustomValue', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            value: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a TagCustomValue', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addTagCustomValueToCollectionIfMissing', () => {
        it('should add a TagCustomValue to an empty array', () => {
          const tagCustomValue: ITagCustomValue = { id: 123 };
          expectedResult = service.addTagCustomValueToCollectionIfMissing([], tagCustomValue);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(tagCustomValue);
        });

        it('should not add a TagCustomValue to an array that contains it', () => {
          const tagCustomValue: ITagCustomValue = { id: 123 };
          const tagCustomValueCollection: ITagCustomValue[] = [
            {
              ...tagCustomValue,
            },
            { id: 456 },
          ];
          expectedResult = service.addTagCustomValueToCollectionIfMissing(tagCustomValueCollection, tagCustomValue);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a TagCustomValue to an array that doesn't contain it", () => {
          const tagCustomValue: ITagCustomValue = { id: 123 };
          const tagCustomValueCollection: ITagCustomValue[] = [{ id: 456 }];
          expectedResult = service.addTagCustomValueToCollectionIfMissing(tagCustomValueCollection, tagCustomValue);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(tagCustomValue);
        });

        it('should add only unique TagCustomValue to an array', () => {
          const tagCustomValueArray: ITagCustomValue[] = [{ id: 123 }, { id: 456 }, { id: 66982 }];
          const tagCustomValueCollection: ITagCustomValue[] = [{ id: 123 }];
          expectedResult = service.addTagCustomValueToCollectionIfMissing(tagCustomValueCollection, ...tagCustomValueArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const tagCustomValue: ITagCustomValue = { id: 123 };
          const tagCustomValue2: ITagCustomValue = { id: 456 };
          expectedResult = service.addTagCustomValueToCollectionIfMissing([], tagCustomValue, tagCustomValue2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(tagCustomValue);
          expect(expectedResult).toContain(tagCustomValue2);
        });

        it('should accept null and undefined values', () => {
          const tagCustomValue: ITagCustomValue = { id: 123 };
          expectedResult = service.addTagCustomValueToCollectionIfMissing([], null, tagCustomValue, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(tagCustomValue);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
