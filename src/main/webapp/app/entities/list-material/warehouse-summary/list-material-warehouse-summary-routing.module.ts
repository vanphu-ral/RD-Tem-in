import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { ListMaterialWarehouseSummaryComponent } from "./list-material-warehouse-summary.component";

const routes: Routes = [
  {
    path: "",
    component: ListMaterialWarehouseSummaryComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ListMaterialWarehouseSummaryRoutingModule {}
