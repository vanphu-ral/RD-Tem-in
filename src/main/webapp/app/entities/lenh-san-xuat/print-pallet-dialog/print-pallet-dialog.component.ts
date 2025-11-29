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
  qrCodeUrl1: string = "";
  qrCodeUrl2: string = "";

  constructor(
    public dialogRef: MatDialogRef<PrintPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PrintPalletData,
  ) {}

  ngOnInit(): void {
    this.generateQRCodes();
  }

  generateQRCodes(): void {
    try {
      // QR code từ Serial pallet
      this.qrCodeUrl1 = this.data.serialPallet;

      // QR code từ serial box (từ paginatedItems trong detail-box-dialog)
      this.qrCodeUrl2 = this.data.serialBox;
    } catch (error) {
      console.error("Error generating QR codes:", error);
      this.qrCodeUrl1 = "ERROR";
      this.qrCodeUrl2 = "ERROR";
    }
  }

  onPrint(): void {
    window.print();
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
