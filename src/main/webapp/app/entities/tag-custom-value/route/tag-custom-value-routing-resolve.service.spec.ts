jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITagCustomValue, TagCustomValue } from '../tag-custom-value.model';
import { TagCustomValueService } from '../service/tag-custom-value.service';

import { TagCustomValueRoutingResolveService } from './tag-custom-value-routing-resolve.service';

describe('Service Tests', () => {
  describe('TagCustomValue routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: TagCustomValueRoutingResolveService;
    let service: TagCustomValueService;
    let resultTagCustomValue: ITagCustomValue | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(TagCustomValueRoutingResolveService);
      service = TestBed.inject(TagCustomValueService);
      resultTagCustomValue = undefined;
    });

    describe('resolve', () => {
      it('should return ITagCustomValue returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTagCustomValue = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTagCustomValue).toEqual({ id: 123 });
      });

      it('should return new ITagCustomValue if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTagCustomValue = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultTagCustomValue).toEqual(new TagCustomValue());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTagCustomValue = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTagCustomValue).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
