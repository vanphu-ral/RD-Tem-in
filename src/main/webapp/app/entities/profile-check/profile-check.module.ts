import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';
import { NgxPaginationModule } from 'ngx-pagination';
import { ProfileCheckComponent } from './profile-check.component';
const profileCheckRoute: Routes = [
  {
    path: '',
    component: ProfileCheckComponent,
  },
];

@NgModule({
  imports: [SharedModule, RouterModule.forChild(profileCheckRoute), NgxPaginationModule],
  declarations: [ProfileCheckComponent],
  exports: [ProfileCheckComponent],
})
export class ProfileCheckModule {}
