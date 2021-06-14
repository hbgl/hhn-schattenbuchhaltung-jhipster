jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITagCustomType, TagCustomType } from '../tag-custom-type.model';
import { TagCustomTypeService } from '../service/tag-custom-type.service';

import { TagCustomTypeRoutingResolveService } from './tag-custom-type-routing-resolve.service';

describe('Service Tests', () => {
  describe('TagCustomType routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: TagCustomTypeRoutingResolveService;
    let service: TagCustomTypeService;
    let resultTagCustomType: ITagCustomType | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(TagCustomTypeRoutingResolveService);
      service = TestBed.inject(TagCustomTypeService);
      resultTagCustomType = undefined;
    });

    describe('resolve', () => {
      it('should return ITagCustomType returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTagCustomType = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTagCustomType).toEqual({ id: 123 });
      });

      it('should return new ITagCustomType if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTagCustomType = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultTagCustomType).toEqual(new TagCustomType());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTagCustomType = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTagCustomType).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
