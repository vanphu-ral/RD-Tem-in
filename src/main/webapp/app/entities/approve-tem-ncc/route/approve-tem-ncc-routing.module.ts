import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { ApproveTemNccComponent } from "../list/approve-tem-ncc.component";
import { ApproveTemNccDetailComponent } from "../approve-tem-ncc-detail/approve-tem-ncc-detail.component";

const infoTemNccRoute: Routes = [
  {
    path: "",
    component: ApproveTemNccComponent,
    data: {
      mapToCanActivate: "id, asc",
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: "approve-tem-ncc",
    component: ApproveTemNccComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: "approve-tem-ncc-detail",
    component: ApproveTemNccDetailComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(infoTemNccRoute)],
  exports: [RouterModule],
})
export class ApproveTemNccRoutingModule {}
