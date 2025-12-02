import {
  Component,
  Inject,
  OnInit,
  ViewChild,
  ElementRef,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from "@angular/material/dialog";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatSelectModule } from "@angular/material/select";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatSnackBar } from "@angular/material/snack-bar";
import { QRCodeComponent } from "angularx-qrcode";
import { ZXingScannerComponent } from "@zxing/ngx-scanner";
import { BarcodeFormat } from "@zxing/library";
import { PalletDetailDialogComponent } from "../detail-pallet-dialog/detail-pallet-dialog.component";
import { PageEvent } from "@angular/material/paginator";
import {
  PrintPalletData,
  PrintPalletDialogComponent,
} from "../print-pallet-dialog/print-pallet-dialog.component";
import { ScanPalletDialogComponent } from "../scan-pallet-dialog/scan-pallet-dialog.component";
import { MatPaginatorModule } from "@angular/material/paginator";

export interface PalletDetailData {
  stt: number;
  tenSanPham: string;
  noSKU: string;
  createdAt: string;
  tongPallet: number;
  tongSlSp: number;
  tongSoThung: number;
}

export interface PalletBoxItem {
  stt: number;
  maPallet: string;
  qrCode?: string;
  tienDoScan: number;
  tongSoThung: number;
  sucChua: string;
}
@Component({
  selector: "jhi-package-pallet-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressBarModule,
    QRCodeComponent,
    MatPaginatorModule,
  ],
  templateUrl: "./package-pallet-dialog.component.html",
  styleUrls: ["./package-pallet-dialog.component.scss"],
})
export class PackagePalletDialogComponent implements OnInit {
  displayedColumns: string[] = [
    "stt",
    "maPallet",
    "maPalletCode",
    "tienDoScan",
    "sucChua",
    "tuyChon",
  ];

  palletBoxItems: PalletBoxItem[] = [];
  paginatedItems: PalletBoxItem[] = [];

  // Pagination
  pageSize = 10;
  pageIndex = 0;
  totalItems = 0;

  constructor(
    public dialogRef: MatDialogRef<PalletDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PalletDetailData,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.generatePalletBoxItems();
    this.updatePaginatedItems();
  }
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.updatePaginatedItems();
  }

  getProgressPercentage(item: PalletBoxItem): number {
    return (item.tienDoScan / item.tongSoThung) * 100;
  }

  onExport(): void {
    console.log("Xuất file chi tiết pallet");
    // Thực hiện logic xuất file Excel/CSV
  }

  onClose(): void {
    this.dialogRef.close();
  }

  //in phieu thong tin
  onPrint(item: PalletBoxItem): void {
    const printData: PrintPalletData = {
      khachHang: "Lowes-YK",
      serialPallet: item.maPallet,
      tenSanPham: this.data.tenSanPham,
      poNumber: "PO-2025-001",
      itemNoSku: "00039458_V4",
      nganh: "Ngành",
      led2: "LED2",
      soQdsx: "002252",
      to: "Tổ",
      lpl2: "LPL2",
      ngaySanXuat: "2025-11-25",
      dateCode: "0725",
      soLuongCaiDatPallet: item.tongSoThung,
      thuTuGiaPallet: 1,
      soLuongBaoNgoaiThungGiaPallet: `${item.tienDoScan} / ${item.tongSoThung}`,
      slThung: 300,
      nguoiKiemTra: "Tùng",
      ketQuaKiemTra: "<Empty>",
      note: "",
      productCode: "LED121640-1p",
      serialBox: "Example_Box_01",
      qty: 300,
      lot: "2ST1131300BA",
      date: "2025-11-24",
    };

    this.openPrintDialog(printData);

    // Option 2: Load từ API
    /*
          this.printService.getPrintData(item.maPallet).subscribe({
            next: (printData) => {
              this.openPrintDialog(printData);
            },
            error: (error) => {
              console.error('Error loading print data:', error);
              alert('Không thể tải dữ liệu in!');
            }
          });
          */
  }

  openScanDialog(pallet: any): void {
    const dialogRef = this.dialog.open(ScanPalletDialogComponent, {
      width: "100vw",
      height: "90vh",
      maxWidth: "1200px",
      maxHeight: "900px",
      data: {
        id: pallet.id,
        maPallet: pallet.maPallet,
        tenSanPham: pallet.tenSanPham,
        qrCode: pallet.qrCode || pallet.maPallet, // QR data
        soThung: pallet.soThung || 30, // Max boxes
      },
      panelClass: "scan-dialog-panel",
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.success) {
        console.log("Completed scan:", result);
        // Update pallet status
        // this.updatePalletScanData(result);
      }
    });
  }
  private openPrintDialog(data: PrintPalletData): void {
    this.dialog.open(PrintPalletDialogComponent, {
      width: "100vw",
      height: "100vh",
      maxWidth: "100vw",
      maxHeight: "100vh",
      panelClass: "print-dialog-fullscreen",
      data: data,
    });
  }
  private generatePalletBoxItems(): void {
    // Tạo danh sách các pallet box dựa trên tongPallet từ data
    this.palletBoxItems = [];
    const totalPallets = this.data.tongPallet ?? 0;

    for (let i = 1; i <= totalPallets; i++) {
      const item: PalletBoxItem = {
        stt: i,
        maPallet: this.generatePalletCode(i),
        tienDoScan: 0, // Số thùng đã scan
        tongSoThung: this.data.tongSoThung ?? 0,
        sucChua: `${this.data.tongSoThung ?? 0} thùng`,
      };
      this.palletBoxItems.push(item);
    }

    this.totalItems = this.palletBoxItems.length;
  }

  private generatePalletCode(index: number): string {
    // Tạo mã pallet theo format: P + timestamp + index
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const sequence = String(index).padStart(4, "0");

    return `P${year}${month}${day}${sequence}`;
  }

  private updatePaginatedItems(): void {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedItems = this.palletBoxItems.slice(startIndex, endIndex);
  }
}
