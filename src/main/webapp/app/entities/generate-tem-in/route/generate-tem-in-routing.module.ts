import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

import { UserRouteAccessService } from "app/core/auth/user-route-access.service";
import { GenerateTemInComponent } from "../list/generate-tem-in.component";
import { GenerateTemInImportComponent } from "../import/generate-tem-in-import.component";
import { GenerateTemInDetailComponent } from "../detail/generate-tem-in-detail.component";

const generateTemInRoute: Routes = [
  {
    path: "",
    component: GenerateTemInComponent,
    data: {
      mapToCanActivate: "id, asc",
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: "import",
    component: GenerateTemInImportComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: "detail/:id",
    component: GenerateTemInDetailComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(generateTemInRoute)],
  exports: [RouterModule],
})
export class GenerateTemInRoutingModule {}
