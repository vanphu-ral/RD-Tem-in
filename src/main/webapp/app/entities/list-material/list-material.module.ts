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
import { ListMaterialComponent } from "./list/list-material.component";
import { ListMaterialUpdateComponent } from "./update/list-material-update.component";
import { ListMaterialRoutingModule } from "./route/list-material-routing.module";
import { ListMaterialUpdateDialogComponent } from "./dialog/list-material-update-dialog";
import { MatDialogContent } from "@angular/material/dialog";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatDialogActions } from "@angular/material/dialog";
import { MatSpinner } from "@angular/material/progress-spinner";
import { ListMaterialSumaryComponent } from "./sumary/list-material-sumary.component";
import { MatRadioModule } from "@angular/material/radio";
// import { ApolloModule } from 'apollo-angular';
// import { HttpClientModule } from '@angular/common/http';

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
    MatRadioModule,
    // ApolloModule,
    // HttpClientModule,
    MatListModule,
    MatSpinner,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
    MatPaginatorModule,
    ListMaterialRoutingModule,
    SharedModule,
    MatDialogContent,
    MatAutocompleteModule,
    MatDialogActions,
  ],

  declarations: [
    ListMaterialComponent,
    ListMaterialUpdateComponent,
    ListMaterialUpdateDialogComponent,
  ],

  exports: [
    ListMaterialComponent,
    ListMaterialUpdateComponent,
    ListMaterialUpdateDialogComponent,
  ],
})
export class ListMaterialModule {}
