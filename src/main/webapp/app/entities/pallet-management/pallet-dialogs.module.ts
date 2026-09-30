import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "app/shared/shared.module";
import { QRCodeComponent } from "angularx-qrcode";

import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTableModule } from "@angular/material/table";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDialogModule } from "@angular/material/dialog";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

import { CreatePalletDialogComponent } from "./list/create-pallet-dialog/create-pallet-dialog.component";
import { PrintPalletDialogComponent } from "./list/print-pallet-dialog/print-pallet-dialog.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    QRCodeComponent,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatDialogModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
  ],
  declarations: [CreatePalletDialogComponent, PrintPalletDialogComponent],
  exports: [CreatePalletDialogComponent, PrintPalletDialogComponent],
})
export class PalletDialogsModule {}
