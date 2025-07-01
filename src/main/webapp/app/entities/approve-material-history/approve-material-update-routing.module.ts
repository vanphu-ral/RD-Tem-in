import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ApproveMaterialHistoryComponent } from './approve-material-history.component';

const approveMaterialHítoryRoute: Routes = [
  {
    path: '',
    component: ApproveMaterialHistoryComponent,
    data: {
      mapToCanActivate: 'id, asc',
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(approveMaterialHítoryRoute)],
  exports: [RouterModule],
})
export class ApproveMaterialHistoryRoutingModule {}
