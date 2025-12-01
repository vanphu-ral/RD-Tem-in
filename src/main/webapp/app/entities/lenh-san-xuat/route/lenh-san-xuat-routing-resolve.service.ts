import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of, EMPTY } from "rxjs";
import { mergeMap } from "rxjs/operators";
import dayjs from "dayjs/esm";

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
  ): Observable<ILenhSanXuat> | Observable<never> {
    const id = route.params["id"];
    if (id) {
      return this.addNewService.findWarehouseNote(id).pipe(
        mergeMap((res: HttpResponse<WarehouseNoteInfo>) => {
          if (res.body) {
            const warehouseNote = res.body;
            const lenhSanXuat = new LenhSanXuat();
            lenhSanXuat.id = warehouseNote.id;
            lenhSanXuat.maLenhSanXuat = warehouseNote.ma_lenh_san_xuat;
            lenhSanXuat.sapCode = warehouseNote.sap_code;
            lenhSanXuat.sapName = warehouseNote.sap_name;
            lenhSanXuat.workOrderCode = warehouseNote.work_order_code;
            lenhSanXuat.version = warehouseNote.version;
            lenhSanXuat.storageCode = warehouseNote.storage_code;
            lenhSanXuat.totalQuantity = warehouseNote.total_quantity;
            lenhSanXuat.createBy = warehouseNote.create_by;
            lenhSanXuat.entryTime = dayjs(warehouseNote.entry_time);
            lenhSanXuat.timeUpdate = dayjs(warehouseNote.entry_time); // assuming same
            lenhSanXuat.trangThai = warehouseNote.trang_thai;
            lenhSanXuat.groupName = warehouseNote.group_name;
            lenhSanXuat.branch = warehouseNote.branch;
            lenhSanXuat.productType = warehouseNote.product_type;
            return of(lenhSanXuat);
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
