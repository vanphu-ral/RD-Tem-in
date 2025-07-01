import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IChiTietSanXuat, ChiTietSanXuat } from '../chi-tiet-san-xuat.model';
import { ChiTietSanXuatService } from '../service/chi-tiet-san-xuat.service';

import { ChiTietSanXuatRoutingResolveService } from './chi-tiet-san-xuat-routing-resolve.service';

describe('ChiTietSanXuat routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ChiTietSanXuatRoutingResolveService;
  let service: ChiTietSanXuatService;
  let resultChiTietSanXuat: IChiTietSanXuat | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(ChiTietSanXuatRoutingResolveService);
    service = TestBed.inject(ChiTietSanXuatService);
    resultChiTietSanXuat = undefined;
  });

  describe('resolve', () => {
    it('should return IChiTietSanXuat returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChiTietSanXuat = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultChiTietSanXuat).toEqual({ id: 123 });
    });

    it('should return new IChiTietSanXuat if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChiTietSanXuat = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultChiTietSanXuat).toEqual(new ChiTietSanXuat());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ChiTietSanXuat })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChiTietSanXuat = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultChiTietSanXuat).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
