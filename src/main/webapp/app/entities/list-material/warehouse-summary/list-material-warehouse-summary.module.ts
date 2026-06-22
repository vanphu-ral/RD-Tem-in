import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatTabsModule } from "@angular/material/tabs";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatSelectModule } from "@angular/material/select";
import { SharedModule } from "app/shared/shared.module";
import { ListMaterialWarehouseSummaryComponent } from "./list-material-warehouse-summary.component";
import { ListMaterialWarehouseSummaryRoutingModule } from "./list-material-warehouse-summary-routing.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatPaginatorModule,
    MatAutocompleteModule,
    MatTabsModule,
    MatProgressBarModule,
    MatSelectModule,
    SharedModule,
    ListMaterialWarehouseSummaryRoutingModule,
  ],
  declarations: [ListMaterialWarehouseSummaryComponent],
})
export class ListMaterialWarehouseSummaryModule {}
