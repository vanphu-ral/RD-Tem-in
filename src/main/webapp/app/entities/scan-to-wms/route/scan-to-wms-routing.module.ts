import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { ScanToWMSComponent } from "../list/scan-to-wms.component";
import { AddScanComponent } from "../add-scan/add-scan.component";

const scanWMSRoute: Routes = [
  {
    path: "",
    component: ScanToWMSComponent,
    data: {
      defaultSort: "id,asc",
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: "add-scan",
    component: AddScanComponent,
    data: {
      defaultSort: "id,asc",
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: "add-scan/:id",
    component: AddScanComponent,
    data: {
      defaultSort: "id,asc",
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(scanWMSRoute)],
  exports: [RouterModule],
})
export class ScanWMSRoutingModule {}
