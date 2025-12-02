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
import { PlanningWorkOrderService } from "../../lenh-san-xuat/service/planning-work-order.service";

@Injectable({ providedIn: "root" })
export class ChiTietLenhSanXuatEditRoutingResolveService {
  constructor(
    protected service: PlanningWorkOrderService,
    protected router: Router,
  ) {}

  resolve(
    route: ActivatedRouteSnapshot,
  ): Observable<ILenhSanXuat> | Observable<never> {
    const id = route.params["id"];
    if (id) {
      return this.service.findWarehouseNote(+id).pipe(
        mergeMap((lenhSanXuat: HttpResponse<any>) => {
          if (lenhSanXuat.body) {
            // Transform snake_case to camelCase
            const transformed = {
              id: lenhSanXuat.body.id,
              maLenhSanXuat: lenhSanXuat.body.ma_lenh_san_xuat,
              sapCode: lenhSanXuat.body.sap_code,
              sapName: lenhSanXuat.body.sap_name,
              workOrderCode: lenhSanXuat.body.work_order_code,
              version: lenhSanXuat.body.version,
              storageCode: lenhSanXuat.body.storage_code,
              totalQuantity: lenhSanXuat.body.total_quantity,
              createBy: lenhSanXuat.body.create_by,
              entryTime: lenhSanXuat.body.entry_time
                ? dayjs(lenhSanXuat.body.entry_time)
                : undefined,
              timeUpdate: lenhSanXuat.body.time_update
                ? dayjs(lenhSanXuat.body.time_update)
                : undefined,
              trangThai: lenhSanXuat.body.trang_thai,
              comment: lenhSanXuat.body.comment,
              groupName: lenhSanXuat.body.group_name,
              comment2: lenhSanXuat.body.comment2,
              approverBy: lenhSanXuat.body.approver_by,
              branch: lenhSanXuat.body.branch,
              productType: lenhSanXuat.body.product_type,
              deletedAt: lenhSanXuat.body.deleted_at
                ? dayjs(lenhSanXuat.body.deleted_at)
                : undefined,
              deletedBy: lenhSanXuat.body.deleted_by,
              destinationWarehouse: lenhSanXuat.body.destination_warehouse,
            };
            return of(transformed);
          } else {
            this.router.navigate(["404"]);
            return EMPTY;
          }
        }),
      );
    }
    return of(new LenhSanXuat());
  }
}
