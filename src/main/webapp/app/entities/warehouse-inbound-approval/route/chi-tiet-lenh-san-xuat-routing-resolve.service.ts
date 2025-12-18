import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of } from "rxjs";
import { map, catchError } from "rxjs/operators";
import dayjs from "dayjs/esm";

import {
  IChiTietLenhSanXuat,
  ChiTietLenhSanXuat,
} from "../chi-tiet-lenh-san-xuat.model";
import { ChiTietLenhSanXuatService } from "../service/chi-tiet-lenh-san-xuat.service";
import { PlanningWorkOrderService } from "../../pallet-note-management/service/planning-work-order.service";

/**
 * Kiểu dữ liệu resolver trả về cho component.
 * Nếu bạn có interface cụ thể cho details/genTemConfigs/serialMappings
 * hãy thay `unknown[]` bằng kiểu tương ứng.
 */
interface ResolverData {
  warehouse_note_info: IChiTietLenhSanXuat | Record<string, unknown>;
  warehouse_note_info_details: unknown[];
  genTemConfigs: unknown[];
  serialMappings: unknown[];
}

@Injectable({ providedIn: "root" })
export class ChiTietLenhSanXuatRoutingResolveService {
  constructor(
    protected service: ChiTietLenhSanXuatService,
    protected planningService: PlanningWorkOrderService,
    protected router: Router,
  ) {}

  resolve(
    route: ActivatedRouteSnapshot,
  ): Observable<ResolverData | ChiTietLenhSanXuat> {
    const id = route.params["id"];
    if (!id) {
      // Trường hợp tạo mới: trả về instance model (có kiểu cụ thể)
      return of(new ChiTietLenhSanXuat());
    }

    // Khi có id: gọi API và map về ResolverData (typed)
    return this.planningService.findWarehouseNoteApprovalWithChildren(+id).pipe(
      map((response: HttpResponse<unknown>) => {
        const body = response.body as Record<string, unknown> | undefined;
        console.log("[DEBUG VIEW RESOLVER] Full response:", response);
        console.log(
          "[DEBUG VIEW RESOLVER] Response body keys:",
          body ? Object.keys(body) : "null",
        );

        // Tìm warehouseNoteInfo (hỗ trợ camelCase / snake_case)
        const warehouseNoteInfo =
          (body &&
            (body["warehouseNoteInfo"] ?? body["warehouse_note_info"])) ??
          null;

        console.log(
          "[DEBUG VIEW RESOLVER] warehouseNoteInfo found:",
          !!warehouseNoteInfo,
        );

        if (!body || !warehouseNoteInfo) {
          console.error(
            "[ERROR VIEW RESOLVER] Invalid response structure:",
            body,
          );
          // Trả về object rỗng có kiểu ResolverData
          return {
            warehouse_note_info: body ?? {},
            warehouse_note_info_details: [],
            genTemConfigs: [],
            serialMappings: [],
          } as ResolverData;
        }

        // Build transformed object, dùng các key fallback an toàn
        const transformed: ResolverData = {
          warehouse_note_info: warehouseNoteInfo as
            | IChiTietLenhSanXuat
            | Record<string, unknown>,
          warehouse_note_info_details:
            (body["details"] as unknown[]) ??
            (body["warehouse_note_info_details"] as unknown[]) ??
            (body["warehouseNoteInfoDetails"] as unknown[]) ??
            [],
          genTemConfigs:
            (body["genTemConfigs"] as unknown[]) ??
            (body["gen_tem_configs"] as unknown[]) ??
            [],
          serialMappings:
            (body["serialMappings"] as unknown[]) ??
            (body["serial_mappings"] as unknown[]) ??
            (body["serialBoxPalletMappings"] as unknown[]) ??
            [],
        };

        console.log("[DEBUG VIEW RESOLVER] Transformed data:", transformed);
        return transformed;
      }),
      catchError((error) => {
        console.error("[ERROR VIEW RESOLVER] API error:", error);
        // Trả về object rỗng có kiểu ResolverData
        return of({
          warehouse_note_info: {},
          warehouse_note_info_details: [],
          genTemConfigs: [],
          serialMappings: [],
        } as ResolverData);
      }),
    );
  }
}
