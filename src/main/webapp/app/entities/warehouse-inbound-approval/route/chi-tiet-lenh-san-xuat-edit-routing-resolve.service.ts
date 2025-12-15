import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of, EMPTY } from "rxjs";
import { mergeMap } from "rxjs/operators";
import dayjs from "dayjs/esm";

import {
  ILenhSanXuat,
  LenhSanXuat,
} from "../../lenh-san-xuat/lenh-san-xuat.model";
import { PlanningWorkOrderService } from "../../pallet-note-management/service/planning-work-order.service";

@Injectable({ providedIn: "root" })
export class ChiTietLenhSanXuatEditRoutingResolveService {
  constructor(
    protected service: PlanningWorkOrderService,
    protected router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<any> | Observable<never> {
    const id = route.params["id"];
    if (id) {
      return this.service.findWarehouseNoteWithChildren(+id).pipe(
        mergeMap((response: HttpResponse<any>) => {
          if (response.body && response.body.warehouseNoteInfo) {
            const warehouseNoteInfo = response.body.warehouseNoteInfo;
            // Transform to the structure expected by components
            const transformed: any = {
              warehouse_note_info: warehouseNoteInfo,
              warehouse_note_info_details: response.body.details || [],
              genTemConfigs: response.body.genTemConfigs || [],
              serialMappings: response.body.serialMappings || [],
            };
            return of(transformed);
          } else {
            this.router.navigate(["404"]);
            return EMPTY;
          }
        }),
      );
    }
    return of({
      warehouse_note_info: new LenhSanXuat(),
      warehouse_note_info_details: [],
      genTemConfigs: [],
      serialMappings: [],
    });
  }
}
