import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { InfoTemNccComponent } from "../list/info-tem-ncc.component";
import { ConfigTemNccComponent } from "../config-tem-ncc/config-tem-ncc.component";
import { InfoTemNccDetailComponent } from "../info-tem-ncc-detail/info-tem-ncc.-detail.component";
import { AddInfoTemNccComponent } from "../add-info-tem-ncc/add-info-tem-ncc.component";

const infoTemNccRoute: Routes = [
  {
    path: "",
    component: InfoTemNccComponent,
    data: {
      mapToCanActivate: "id, asc",
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: "info-tem-ncc",
    component: InfoTemNccComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: "add-info-tem-ncc",
    component: AddInfoTemNccComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: "add-info-tem-ncc/:id",
    component: AddInfoTemNccComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: "info-tem-ncc-detail",
    component: InfoTemNccDetailComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: "config-tem-ncc",
    component: ConfigTemNccComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(infoTemNccRoute)],
  exports: [RouterModule],
})
export class InfoTemNccRoutingModule {}
