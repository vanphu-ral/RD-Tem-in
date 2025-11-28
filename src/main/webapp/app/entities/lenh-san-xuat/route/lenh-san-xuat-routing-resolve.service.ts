import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of, EMPTY } from "rxjs";
import { mergeMap } from "rxjs/operators";

import { ILenhSanXuat, LenhSanXuat } from "../lenh-san-xuat.model";
import { LenhSanXuatService } from "../service/lenh-san-xuat.service";
import { PlanningWorkOrderService } from "../service/planning-work-order.service";
import {
  WarehouseNoteInfo,
  WarehouseNoteResponse,
} from "../add-new/add-new-lenh-san-xuat.component";

@Injectable({ providedIn: "root" })
export class LenhSanXuatRoutingResolveService {
  constructor(
    protected service: LenhSanXuatService,
    protected router: Router,
    protected addNewService: PlanningWorkOrderService,
  ) {}

  // resolve(route: ActivatedRouteSnapshot): Observable<ILenhSanXuat> | Observable<never> {
  //   const id = route.params['id'];
  //   if (id) {
  //     return this.service.find(id).pipe(
  //       mergeMap((lenhSanXuat: HttpResponse<LenhSanXuat>) => {
  //         if (lenhSanXuat.body) {
  //           return of(lenhSanXuat.body);
  //         } else {
  //           this.router.navigate(['404']);
  //           return EMPTY;
  //         }
  //       }),
  //     );
  //   }
  //   return of(new LenhSanXuat());
  // }
  resolve(
    route: ActivatedRouteSnapshot,
  ): Observable<WarehouseNoteResponse> | Observable<never> {
    const id = route.params["id"];
    if (id) {
      return this.addNewService.findWarehouseNoteWithChildren(id).pipe(
        mergeMap((res: HttpResponse<WarehouseNoteResponse>) => {
          if (res.body) {
            return of(res.body);
          } else {
            this.router.navigate(["404"]);
            return EMPTY;
          }
        }),
      );
    }
    return of({
      warehouse_note_info: {} as WarehouseNoteInfo,
      warehouse_note_info_details: [],
      serial_box_pallet_mappings: [],
      pallet_infor_details: [],
    });
  }
}
