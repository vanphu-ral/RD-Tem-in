import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of, EMPTY } from "rxjs";
import { mergeMap, catchError } from "rxjs/operators";
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

  resolve(route: ActivatedRouteSnapshot): Observable<any> {
    const id = route.params["id"];
    if (id) {
      // Use the with-children endpoint to get complete data from partner3 database
      return this.planningService.findWarehouseNoteWithChildren(+id).pipe(
        mergeMap((response: HttpResponse<any>) => {
          console.log("[DEBUG VIEW RESOLVER] Full response:", response);
          console.log("[DEBUG VIEW RESOLVER] Response body:", response.body);
          console.log(
            "[DEBUG VIEW RESOLVER] Response body keys:",
            response.body ? Object.keys(response.body) : "null",
          );

          // Check for both camelCase and snake_case keys
          const warehouseNoteInfo =
            response.body?.warehouseNoteInfo ||
            response.body?.warehouse_note_info;

          console.log(
            "[DEBUG VIEW RESOLVER] warehouseNoteInfo found:",
            !!warehouseNoteInfo,
          );

          if (response.body && warehouseNoteInfo) {
            // Transform to the format expected by components
            const transformed: any = {
              warehouse_note_info: warehouseNoteInfo,
              warehouse_note_info_details:
                response.body.details ||
                response.body.warehouse_note_info_details ||
                response.body.warehouseNoteInfoDetails ||
                [],
              genTemConfigs:
                response.body.genTemConfigs ||
                response.body.gen_tem_configs ||
                [],
              serialMappings:
                response.body.serialMappings ||
                response.body.serial_mappings ||
                response.body.serialBoxPalletMappings ||
                [],
            };
            console.log("[DEBUG VIEW RESOLVER] Transformed data:", transformed);
            return of(transformed);
          } else {
            console.error(
              "[ERROR VIEW RESOLVER] Invalid response structure:",
              response.body,
            );
            // Return empty object instead of navigating to 404
            return of({
              warehouse_note_info: response.body || {},
              warehouse_note_info_details: [],
              genTemConfigs: [],
              serialMappings: [],
            });
          }
        }),
        catchError((error) => {
          console.error("[ERROR VIEW RESOLVER] API error:", error);
          // Return empty object instead of navigating to 404
          return of({
            warehouse_note_info: {},
            warehouse_note_info_details: [],
            genTemConfigs: [],
            serialMappings: [],
          });
        }),
      );
    }
    return of(new ChiTietLenhSanXuat());
  }
}
