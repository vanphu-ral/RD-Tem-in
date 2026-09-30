import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { PalletManagementComponent } from "../list/pallet-management.component";

const palletManagementRoute: Routes = [
  {
    path: "",
    component: PalletManagementComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(palletManagementRoute)],
  exports: [RouterModule],
})
export class PalletManagementRoutingModule {}
