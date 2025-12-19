import { NgxPaginationModule } from "ngx-pagination";
import { NgModule } from "@angular/core";
import { SharedModule } from "app/shared/shared.module";
import { LenhSanXuatComponent } from "./list/lenh-san-xuat.component";
import { LenhSanXuatDetailComponent } from "./detail/lenh-san-xuat-detail.component";
import { LenhSanXuatUpdateComponent } from "./update/lenh-san-xuat-update.component";
import { LenhSanXuatDeleteDialogComponent } from "./delete/lenh-san-xuat-delete-dialog.component";
import { LenhSanXuatRoutingModule } from "./route/lenh-san-xuat-routing.module";
import { AddNewLenhSanXuatComponent } from "./add-new/add-new-lenh-san-xuat.component";

// Angular Material imports
import { MatTabsModule } from "@angular/material/tabs";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatSelectModule } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDialogModule } from "@angular/material/dialog";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatAutocompleteModule } from "@angular/material/autocomplete";

@NgModule({
  imports: [
    SharedModule,
    LenhSanXuatRoutingModule,
    NgxPaginationModule,
    // Angular Material Modules
    MatTooltipModule,
    MatAutocompleteModule,
    MatTabsModule,
    MatTableModule, // Quan trọng cho mat-table
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatProgressBarModule,
    MatInputModule,
    MatDialogModule,
    MatFormFieldModule,
  ],
  declarations: [
    LenhSanXuatComponent,
    AddNewLenhSanXuatComponent,
    LenhSanXuatDetailComponent,
    LenhSanXuatUpdateComponent,
    LenhSanXuatDeleteDialogComponent,
  ],
})
export class LenhSanXuatModule {}
