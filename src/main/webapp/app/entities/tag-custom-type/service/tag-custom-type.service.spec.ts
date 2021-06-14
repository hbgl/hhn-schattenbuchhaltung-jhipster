import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITagCustomType, TagCustomType } from '../tag-custom-type.model';

import { TagCustomTypeService } from './tag-custom-type.service';

describe('Service Tests', () => {
  describe('TagCustomType Service', () => {
    let service: TagCustomTypeService;
    let httpMock: HttpTestingController;
    let elemDefault: ITagCustomType;
    let expectedResult: ITagCustomType | ITagCustomType[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(TagCustomTypeService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        enabled: false,
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

      it('should create a TagCustomType', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new TagCustomType()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a TagCustomType', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            enabled: true,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a TagCustomType', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
          },
          new TagCustomType()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of TagCustomType', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            enabled: true,
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

      it('should delete a TagCustomType', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addTagCustomTypeToCollectionIfMissing', () => {
        it('should add a TagCustomType to an empty array', () => {
          const tagCustomType: ITagCustomType = { id: 123 };
          expectedResult = service.addTagCustomTypeToCollectionIfMissing([], tagCustomType);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(tagCustomType);
        });

        it('should not add a TagCustomType to an array that contains it', () => {
          const tagCustomType: ITagCustomType = { id: 123 };
          const tagCustomTypeCollection: ITagCustomType[] = [
            {
              ...tagCustomType,
            },
            { id: 456 },
          ];
          expectedResult = service.addTagCustomTypeToCollectionIfMissing(tagCustomTypeCollection, tagCustomType);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a TagCustomType to an array that doesn't contain it", () => {
          const tagCustomType: ITagCustomType = { id: 123 };
          const tagCustomTypeCollection: ITagCustomType[] = [{ id: 456 }];
          expectedResult = service.addTagCustomTypeToCollectionIfMissing(tagCustomTypeCollection, tagCustomType);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(tagCustomType);
        });

        it('should add only unique TagCustomType to an array', () => {
          const tagCustomTypeArray: ITagCustomType[] = [{ id: 123 }, { id: 456 }, { id: 54270 }];
          const tagCustomTypeCollection: ITagCustomType[] = [{ id: 123 }];
          expectedResult = service.addTagCustomTypeToCollectionIfMissing(tagCustomTypeCollection, ...tagCustomTypeArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const tagCustomType: ITagCustomType = { id: 123 };
          const tagCustomType2: ITagCustomType = { id: 456 };
          expectedResult = service.addTagCustomTypeToCollectionIfMissing([], tagCustomType, tagCustomType2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(tagCustomType);
          expect(expectedResult).toContain(tagCustomType2);
        });

        it('should accept null and undefined values', () => {
          const tagCustomType: ITagCustomType = { id: 123 };
          expectedResult = service.addTagCustomTypeToCollectionIfMissing([], null, tagCustomType, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(tagCustomType);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
