import { Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from "@angular/material/dialog";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatChipsModule } from "@angular/material/chips";
import { QRCodeComponent } from "angularx-qrcode";
import {
  PrintPalletDialogComponent,
  PrintPalletData,
} from "../print-pallet-dialog/print-pallet-dialog.component";
import { ScanPalletDialogComponent } from "../scan-pallet-dialog/scan-pallet-dialog.component";

export interface PalletDetailData {
  stt: number;
  tenSanPham: string;
  noSKU: string;
  createdAt: string;
  tongPallet: number;
  tongSlSp: number;
  tongSoThung: number;
  serialPallet: string;
}

export interface PalletBoxItem {
  stt: number;
  maPallet: string;
  qrCode?: string;
  tienDoScan: number;
  tongSoThung: number;
  sucChua: string;
  // Thêm fields để phân biệt pallet gốc
  parentPalletIndex?: number;
  tenSanPham?: string;
  noSKU?: string;
}

// Multi-mode data interface
export interface MultiPalletDialogData {
  mode: "single" | "multiple";
  singleData?: PalletDetailData;
  multipleData?: PalletDetailData[];
}

@Component({
  selector: "jhi-detail-pallet-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatChipsModule,
    QRCodeComponent,
  ],
  templateUrl: "./detail-pallet-dialog.component.html",
  styleUrls: ["./detail-pallet-dialog.component.scss"],
})
export class PalletDetailDialogComponent implements OnInit {
  displayedColumns: string[] = [
    "stt",
    "maPallet",
    "maPalletCode",
    "tienDoScan",
    "sucChua",
    "tuyChon",
  ];
  displayedColumnsMulti: string[] = [
    "stt",
    "tenSanPham",
    "maPallet",
    "maPalletCode",
    "tienDoScan",
    "sucChua",
    "tuyChon",
  ];

  palletBoxItems: PalletBoxItem[] = [];
  paginatedItems: PalletBoxItem[] = [];

  // Mode tracking
  isMultipleMode = false;
  palletSources: PalletDetailData[] = [];

  // Single mode data
  singlePalletData?: PalletDetailData;

  // Pagination
  pageSize = 10;
  pageIndex = 0;
  totalItems = 0;

  constructor(
    public dialogRef: MatDialogRef<PalletDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MultiPalletDialogData,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.initializeData();
    this.generatePalletBoxItems();
    this.updatePaginatedItems();
  }

  getDisplayedColumns(): string[] {
    return this.isMultipleMode
      ? this.displayedColumnsMulti
      : this.displayedColumns;
  }

  getDialogTitle(): string {
    if (this.isMultipleMode) {
      return `Chi tiết tất cả Pallet (${this.palletSources.length} đơn)`;
    }
    return "Chi tiết Pallet";
  }

  getTotalStats(): {
    tongPallet: number;
    tongThung: number;
    tongSanPham: number;
  } {
    return {
      tongPallet: this.palletBoxItems.length,
      tongThung: this.palletSources.reduce(
        (sum, p) => sum + p.tongSoThung * p.tongPallet,
        0,
      ),
      tongSanPham: this.palletSources.reduce((sum, p) => sum + p.tongSlSp, 0),
    };
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

  onPrint(item: PalletBoxItem): void {
    const sourceData = this.isMultipleMode
      ? this.palletSources[item.parentPalletIndex ?? 0]
      : this.singlePalletData;

    if (!sourceData) {
      return;
    }

    const printData: PrintPalletData = {
      khachHang: "Lowes-YK",
      serialPallet: item.maPallet,
      tenSanPham: sourceData.tenSanPham,
      poNumber: "PO-2025-001",
      itemNoSku: sourceData.noSKU || "00039458_V4",
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
      productCode: "LED121640-1p",
      serialBox: "Example_Box_01",
      qty: 300,
      lot: "2ST1131300BA",
      date: "2025-11-24",
    };

    this.openPrintDialog(printData);
  }

  openScanDialog(item: PalletBoxItem): void {
    const sourceData = this.isMultipleMode
      ? this.palletSources[item.parentPalletIndex ?? 0]
      : this.singlePalletData;

    if (!sourceData) {
      return;
    }

    const dialogRef = this.dialog.open(ScanPalletDialogComponent, {
      width: "100vw",
      height: "90vh",
      maxWidth: "1200px",
      maxHeight: "900px",
      data: {
        id: item.maPallet,
        maPallet: item.maPallet,
        tenSanPham: sourceData.tenSanPham,
        qrCode: item.qrCode ?? item.maPallet,
        soThung: item.tongSoThung,
      },
      panelClass: "scan-dialog-panel",
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.success) {
        console.log("Completed scan:", result);
        // Update progress
        item.tienDoScan =
          result.totalScanned || result.scannedBoxes?.length || 0;
        this.updatePaginatedItems();
      }
    });
  }
  private initializeData(): void {
    // Check if data has mode property (new format)
    if (this.hasMode(this.data)) {
      this.isMultipleMode = this.data.mode === "multiple";

      if (this.isMultipleMode && this.data.multipleData) {
        this.palletSources = this.data.multipleData;
      } else if (this.data.singleData) {
        this.singlePalletData = this.data.singleData;
        this.palletSources = [this.data.singleData];
      }
    } else {
      // Backward compatibility: old format (single mode)
      this.isMultipleMode = false;
      this.singlePalletData = this.data as PalletDetailData;
      this.palletSources = [this.singlePalletData];
    }
  }

  private hasMode(data: any): data is MultiPalletDialogData {
    return "mode" in data;
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
    this.palletBoxItems = [];
    let globalStt = 1;

    this.palletSources.forEach((source, sourceIndex) => {
      const item: PalletBoxItem = {
        stt: globalStt++,
        maPallet: source.serialPallet,
        qrCode: source.serialPallet,
        tienDoScan: 0,
        tongSoThung: source.tongSoThung ?? 0,
        sucChua: `${source.tongSoThung ?? 0} thùng`,
        parentPalletIndex: sourceIndex,
        tenSanPham: source.tenSanPham,
        noSKU: source.noSKU,
      };
      this.palletBoxItems.push(item);
    });

    this.totalItems = this.palletBoxItems.length;
  }

  private generatePalletCode(sourceIndex: number, palletIndex: number): string {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    if (this.isMultipleMode) {
      // Multiple mode: prefix with source index
      const sequence = String(palletIndex).padStart(4, "0");
      return `P${year}${month}${day}${String(sourceIndex + 1).padStart(2, "0")}${sequence}`;
    } else {
      // Single mode: simple sequence
      const sequence = String(palletIndex).padStart(4, "0");
      return `P${year}${month}${day}${sequence}`;
    }
  }

  private updatePaginatedItems(): void {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedItems = this.palletBoxItems.slice(startIndex, endIndex);
  }
}
