import { NgxPaginationModule } from 'ngx-pagination';
import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LenhSanXuatComponent } from './list/lenh-san-xuat.component';
import { LenhSanXuatDetailComponent } from './detail/lenh-san-xuat-detail.component';
import { LenhSanXuatUpdateComponent } from './update/lenh-san-xuat-update.component';
import { LenhSanXuatDeleteDialogComponent } from './delete/lenh-san-xuat-delete-dialog.component';
import { LenhSanXuatRoutingModule } from './route/lenh-san-xuat-routing.module';
// import { Ng2SearchPipeModule } from 'ng2-search-filter';
@NgModule({
  imports: [
    SharedModule,
    LenhSanXuatRoutingModule,
    NgxPaginationModule,
    // Ng2SearchPipeModule
  ],
  declarations: [LenhSanXuatComponent, LenhSanXuatDetailComponent, LenhSanXuatUpdateComponent, LenhSanXuatDeleteDialogComponent],
})
export class LenhSanXuatModule {}
