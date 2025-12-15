import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of, EMPTY } from "rxjs";
import { mergeMap } from "rxjs/operators";
import dayjs from "dayjs/esm";

import {
  IChiTietLenhSanXuat,
  ChiTietLenhSanXuat,
} from "../chi-tiet-lenh-san-xuat.model";
import { ChiTietLenhSanXuatService } from "../service/chi-tiet-lenh-san-xuat.service";
import { PlanningWorkOrderService } from "../../pallet-note-management/service/planning-work-order.service";

@Injectable({ providedIn: "root" })
export class ChiTietLenhSanXuatRoutingResolveService {
  constructor(
    protected service: ChiTietLenhSanXuatService,
    protected planningService: PlanningWorkOrderService,
    protected router: Router,
  ) {}

  resolve(
    route: ActivatedRouteSnapshot,
  ): Observable<IChiTietLenhSanXuat> | Observable<never> {
    const id = route.params["id"];
    if (id) {
      // Use the with-children endpoint to get complete data from partner3 database
      return this.planningService.findWarehouseNoteWithChildren(+id).pipe(
        mergeMap((response: HttpResponse<any>) => {
          if (response.body && response.body.warehouseNoteInfo) {
            const warehouseNoteInfo = response.body.warehouseNoteInfo;
            // Transform to the format expected by components
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
    return of(new ChiTietLenhSanXuat());
  }
}
