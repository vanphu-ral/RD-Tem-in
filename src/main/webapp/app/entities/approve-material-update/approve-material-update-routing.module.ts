import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ApproveMaterialUpdateComponent } from './approve-material-update.component';

const approveMaterialRoute: Routes = [
  {
    path: '',
    component: ApproveMaterialUpdateComponent,
    data: {
      mapToCanActivate: 'id, asc',
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(approveMaterialRoute)],
  exports: [RouterModule],
})
export class ApproveMaterialUpdateRoutingModule {}
