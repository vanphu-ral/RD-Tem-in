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
import { InfoTemNccComponent } from "./list/info-tem-ncc.component";
import { InfoTemNccRoutingModule } from "./route/info-tem-ncc-routing.module";
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
import { ConfigTemNccComponent } from "./config-tem-ncc/config-tem-ncc.component";
import { ConfigDialogComponent } from "./config-dialog/config-dialog.component";
import { InfoTemNccDetailComponent } from "./info-tem-ncc-detail/info-tem-ncc.-detail.component";
import { AddInfoTemNccComponent } from "./add-info-tem-ncc/add-info-tem-ncc.component";
import { LotDetailDialogComponent } from "./lot-detail-dialog/lot-detail-dialog.component";
import { ScanItemDialogComponent } from "./scan-item-dialog/scan-item-dialog.component";
import { OrderSummaryDialogComponent } from "./list/order-summary-dialog/order-summary-dialog.component";
import { ScanListViewDialogComponent } from "./scan-item-dialog/scan-list-view-dialog/scan-list-view-dialog.component";
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
    InfoTemNccRoutingModule,
    SharedModule,
    MatAutocompleteModule,
    QRCodeComponent,
    MatDialogModule,
    MatExpansionModule,
  ],

  declarations: [
    InfoTemNccComponent,
    ConfigTemNccComponent,
    ConfigDialogComponent,
    InfoTemNccDetailComponent,
    AddInfoTemNccComponent,
    LotDetailDialogComponent,
    ScanItemDialogComponent,
    OrderSummaryDialogComponent,
    ScanListViewDialogComponent,
  ],

  exports: [
    InfoTemNccComponent,
    ConfigTemNccComponent,
    ConfigDialogComponent,
    AddInfoTemNccComponent,
    InfoTemNccDetailComponent,
    LotDetailDialogComponent,
    ScanItemDialogComponent,
    ScanListViewDialogComponent,
    OrderSummaryDialogComponent,
  ],
})
export class InfoTemNccModule {}
