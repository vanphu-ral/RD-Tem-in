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
import { QRCodeComponent } from "angularx-qrcode";
import { HttpLink } from "apollo-angular/http";
import { InMemoryCache } from "@apollo/client/cache";
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

import { ApproveTemNccComponent } from "./list/approve-tem-ncc.component";
import { OrderSummaryDialogComponent } from "./list/order-summary-dialog/order-summary-dialog.component";
import { ApproveTemNccRoutingModule } from "./route/approve-tem-ncc-routing.module";
import { ApproveTemNccDetailComponent } from "./approve-tem-ncc-detail/approve-tem-ncc-detail.component";
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
    ApproveTemNccRoutingModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
    MatPaginatorModule,
    SharedModule,
    MatAutocompleteModule,
    QRCodeComponent,
    MatDialogModule,
    MatExpansionModule,
  ],

  declarations: [
    ApproveTemNccComponent,
    OrderSummaryDialogComponent,
    ApproveTemNccDetailComponent,
  ],

  exports: [
    ApproveTemNccComponent,
    OrderSummaryDialogComponent,
    ApproveTemNccDetailComponent,
  ],
})
export class ApproveTemNccModule {}
