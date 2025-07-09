import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { ListMaterialComponent } from "../list/list-material.component";
import { ListMaterialUpdateComponent } from "../update/list-material-update.component";
import { ListMaterialSumaryComponent } from "../sumary/list-material-sumary.component";

const listMaterialRoute: Routes = [
  {
    path: "",
    component: ListMaterialComponent,
    data: {
      mapToCanActivate: "id, asc",
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: "list-material",
    component: ListMaterialComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: "update-list",
    component: ListMaterialUpdateComponent,
    canActivate: [UserRouteAccessService],
  },
  // {
  //   path: "list-material/sumary",
  //   component: ListMaterialSumaryComponent,
  //   canActivate: [UserRouteAccessService],
  // },
];

@NgModule({
  imports: [RouterModule.forChild(listMaterialRoute)],
  exports: [RouterModule],
})
export class ListMaterialRoutingModule {}
