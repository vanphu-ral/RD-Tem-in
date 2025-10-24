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
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatRadioModule } from "@angular/material/radio";
import { GenerateTemInComponent } from "./list/generate-tem-in.component";
import { GenerateTemInRoutingModule } from "./route/generate-tem-in-routing.module";
import { QRCodeComponent } from "angularx-qrcode";
import { GenerateTemInImportComponent } from "./import/generate-tem-in-import.component";
import { GenerateTemInDetailComponent } from "./detail/generate-tem-in-detail.component";
import { GenerateTemInImportEditDialogComponent } from "./import/generate-tem-in-import-edit-dialog.component";
import { SaveConfirmationDialogComponent } from "./import/save-confirmation-dialog.component";
import { HttpLink } from "apollo-angular/http";
import { InMemoryCache } from "@apollo/client/cache";
import { GenerateTemInConfirmDialogComponent } from "./generate-tem-in-modal-confirm/modal-confirm.component";
import { MatCardModule } from "@angular/material/card";
import { MatExpansionModule } from "@angular/material/expansion";

// import { ApolloModule } from 'apollo-angular';
// import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from "@angular/material/dialog";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatChipsModule } from "@angular/material/chips";
import { MatSortModule } from "@angular/material/sort";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDividerModule } from "@angular/material/divider";
@NgModule({
  imports: [
    // MatDialogModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatSortModule,
    MatTooltipModule,
    MatDividerModule,
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
    MatCardModule,
    // ApolloModule,
    // HttpClientModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
    MatPaginatorModule,
    GenerateTemInRoutingModule,
    SharedModule,
    MatAutocompleteModule,
    QRCodeComponent,
    MatDialogModule,
    MatExpansionModule,
  ],

  declarations: [
    GenerateTemInComponent,
    GenerateTemInImportComponent,
    GenerateTemInDetailComponent,
    GenerateTemInConfirmDialogComponent,
    GenerateTemInImportEditDialogComponent,
    SaveConfirmationDialogComponent,
  ],

  exports: [
    GenerateTemInComponent,
    GenerateTemInImportComponent,
    GenerateTemInDetailComponent,
    GenerateTemInConfirmDialogComponent,
  ],
})
export class GenerateTemInModule {}
