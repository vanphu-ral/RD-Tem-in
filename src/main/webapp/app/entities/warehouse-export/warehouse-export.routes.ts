import { Routes } from "@angular/router";
import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import {
  WarehouseExportListComponent,
  WarehouseExportListRouteData,
} from "./list/warehouse-export-list.component";

/** Xuất chuyển kho: danh sách yêu cầu xuất → Chi tiết xuất (tab Yêu cầu lĩnh vật tư) */
export const WAREHOUSE_EXPORT_ROUTES: Routes = [
  {
    path: "",
    component: WarehouseExportListComponent,
    canActivate: [UserRouteAccessService],
    data: {
      mode: "request",
      title: "Xuất chuyển kho",
      codeLabel: "Mã đề nghị",
      searchPlaceholder: "Nhập mã yêu cầu...",
    } satisfies WarehouseExportListRouteData,
  },
];
