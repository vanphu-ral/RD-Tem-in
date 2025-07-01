import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';
import { NgxPaginationModule } from 'ngx-pagination';
import { QuanLyThietBiComponent } from './quan-ly-thiet-bi.component';
const quanLyThietBiRoute: Routes = [
  {
    path: '',
    component: QuanLyThietBiComponent,
  },
];
@NgModule({
  imports: [SharedModule, RouterModule.forChild(quanLyThietBiRoute), NgxPaginationModule],
  declarations: [QuanLyThietBiComponent],
  exports: [RouterModule],
})
export class QuanLyThietBiModule {}
