import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { ListMaterialSumaryComponent } from "./list-material-sumary.component";

const ListMaterialSumaryRoute: Routes = [
  {
    path: "",
    component: ListMaterialSumaryComponent,
    data: {
      mapToCanActivate: "id, asc",
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  // imports: [RouterModule.forChild(ListMaterialSumaryRoute)],
  // imports: [RouterModule.forRoot(routes)],
  imports: [RouterModule.forChild(ListMaterialSumaryRoute)],
  exports: [RouterModule],
})
export class ListMaterialSumaryRoutingModule {}
