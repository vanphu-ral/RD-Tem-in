import { NgModule } from "@angular/core";
import { SharedModule } from "app/shared/shared.module";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatMenuModule } from "@angular/material/menu";
import { MatListModule } from "@angular/material/list";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatPaginatorModule } from "@angular/material/paginator";
import { ApproveMaterialUpdateComponent } from "./approve-material-update.component";
import { ApproveMaterialUpdateRoutingModule } from "./approve-material-update-routing.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatMenuModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
    ApproveMaterialUpdateRoutingModule,
    SharedModule,
    MatPaginatorModule,
  ],

  declarations: [ApproveMaterialUpdateComponent],

  exports: [],
})
export class ApproveMaterialUpdateModule {}
