import { Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { QRCodeComponent } from "angularx-qrcode";

export interface PrintPalletData {
  khachHang: string;
  serialPallet: string;
  tenSanPham: string;
  poNumber: string;
  itemNoSku: string;
  nganh: string;
  led2: string;
  soQdsx: string;
  to: string;
  lpl2: string;
  ngaySanXuat: string;
  dateCode: string;
  soLuongCaiDatPallet: number;
  thuTuGiaPallet: number;
  soLuongBaoNgoaiThungGiaPallet: string;
  slThung: number;
  nguoiKiemTra: string;
  ketQuaKiemTra: string;
  productCode: string;
  serialBox: string;
  qty: number;
  lot: string;
  date: string;
  scannedBoxes?: string[];
}

@Component({
  selector: "jhi-print-pallet-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    QRCodeComponent,
  ],
  templateUrl: "./print-pallet-dialog.component.html",
  styleUrls: ["./print-pallet-dialog.component.scss"],
})
export class PrintPalletDialogComponent implements OnInit {
  // ===== SUPPORT MULTIPLE PALLETS =====
  pallets: PrintPalletData[] = [];
  isMultiMode = false;

  constructor(
    public dialogRef: MatDialogRef<PrintPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletData | PrintPalletData[],
  ) {}

  ngOnInit(): void {
    this.initializePallets();
    this.debugPrintData();
  }

  initializePallets(): void {
    // Check if data is array or single object
    if (Array.isArray(this.data)) {
      this.pallets = this.data;
      this.isMultiMode = true;
      console.log(` Multi-mode: ${this.pallets.length} pallets`);
    } else {
      this.pallets = [this.data];
      this.isMultiMode = false;
      console.log(" Single-mode: 1 pallet");
    }
  }

  debugPrintData(): void {
    console.log("===  PRINT DIALOG DATA ===");
    console.log("Mode:", this.isMultiMode ? "MULTI" : "SINGLE");
    console.log("Total pallets:", this.pallets.length);

    this.pallets.forEach((pallet, index) => {
      console.log(`\nPallet ${index + 1}:`, {
        serialPallet: pallet.serialPallet,
        serialBox: pallet.serialBox,
        soLuongSP: pallet.soLuongCaiDatPallet,
        soThung: pallet.soLuongBaoNgoaiThungGiaPallet,
      });
    });
  }

  onPrint(): void {
    console.log(` Printing ${this.pallets.length} pallet(s)...`);
    window.print();
  }

  onClose(): void {
    console.log(" Closing print dialog");
    this.dialogRef.close();
  }
}
