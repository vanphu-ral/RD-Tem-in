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
import * as XLSX from "xlsx";

export interface PalletDetailData {
  stt: number;
  tenSanPham: string;
  noSKU: string;
  createdAt: string;
  tongPallet: number;
  tongSlSp: number;
  tongSoThung: number;
  serialPallet: string;
  khachHang?: string;
  poNumber?: string;
  dateCode?: string;
  qdsx?: string;
  nguoiKiemTra?: string;
  ketQuaKiemTra?: string;
  subItems?: PalletBoxItem[];
  branch?: string; // Ngành
  team?: string; // Tổ
  boxItems?: any[]; // Box data for print dialog
  maLenhSanXuatId?: number; // Production order ID
  validReelIds?: string[]; // Valid reel IDs for this production order
}
export interface PalletExcelRow {
  productionLine: string; // Production Line
  lot: string; // Lot
  poCode: string; // PoCode
  planningCode: string; // Planning Code
  numberOfItems: number; // Number Items
  itemCode: string; // ItemCode
  productName: string; // ProductName
  sapWo: string; // SapWo
  version: string; // Version
  qtyBox: number; // QtyBox
  customers: string; // Customers
  dateCode: string; // DateCode
  manufactureDate: string; // ManufactureDate
  qdsx: string; // QDSX
  inspectionResult: string; // Kết Quả Kiểm Tra
  inspector: string; // Người kiểm tra
  totalQuantity: number; // Tổng số lượng SP
  itemNoSku: string; // Item No/SKU
  boxesPerPallet: number; // SL/Thùng
  branch: string; // Ngành
  team: string; // Tổ
}
export interface PalletBoxItem {
  stt: number;
  maPallet: string;
  tongSoThung: number;
  qrCode?: string;
  tienDoScan?: number;
  sucChua?: string;
  parentPalletIndex?: number;
  tenSanPham?: string;
  noSKU?: string;
  scannedBoxes?: string[];
}

// Multi-mode data interface
export interface MultiPalletDialogData {
  mode: "single" | "multiple";
  singleData?: PalletDetailData;
  multipleData?: PalletDetailData[];
  boxItems?: any[]; // Box data for print dialog
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

  // Box data from parent component
  boxItems: any[] = [];

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
    this.loadPalletBoxItems();
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
    const scanned = item.tienDoScan ?? 0;
    return (scanned / item.tongSoThung) * 100;
  }

  public onExport(): void {
    console.log("Starting export...");

    //  1. Thu thập dữ liệu
    const exportData: PalletExcelRow[] = [];

    this.palletBoxItems.forEach((palletBoxItem, index) => {
      const sourceData = this.isMultipleMode
        ? this.palletSources[palletBoxItem.parentPalletIndex ?? 0]
        : this.singlePalletData;

      if (!sourceData) {
        console.warn(` Không tìm thấy source data cho item ${index}`);
        return;
      }

      //  Tạo row data cho mỗi pallet
      const row: PalletExcelRow = {
        // Thông tin sản xuất
        productionLine: "", // Có thể lấy từ metadata
        lot: sourceData.serialPallet ?? "", // Lot từ serial pallet
        poCode: sourceData.poNumber ?? "",
        planningCode: "", // Cần bổ sung nếu có
        numberOfItems: palletBoxItem.tongSoThung ?? 0,

        // Thông tin sản phẩm
        itemCode: sourceData.noSKU ?? "",
        productName: sourceData.tenSanPham ?? "",
        sapWo: "", // Cần lấy từ parent component
        version: "", // Cần lấy từ parent component

        // Thông tin đóng gói
        qtyBox: palletBoxItem.tongSoThung ?? 0,
        customers: sourceData.khachHang ?? "",
        dateCode: sourceData.dateCode ?? "",
        manufactureDate: sourceData.createdAt
          ? new Date(sourceData.createdAt)
              .toLocaleDateString("en-GB")
              .replace(/\//g, "")
          : "",

        // Thông tin kiểm tra
        qdsx: sourceData.qdsx ?? "",
        inspectionResult: sourceData.ketQuaKiemTra ?? "",
        inspector: sourceData.nguoiKiemTra ?? "",

        // Thống kê
        totalQuantity: sourceData.tongSlSp ?? 0,
        itemNoSku: sourceData.noSKU ?? "",
        boxesPerPallet: palletBoxItem.tongSoThung ?? 0,

        // Phân loại
        branch: sourceData.branch ?? "",
        team: sourceData.team ?? "",
      };

      exportData.push(row);
    });

    console.log(" Export data:", exportData);

    //  2. Tạo worksheet với headers Tiếng Việt
    const worksheet = XLSX.utils.json_to_sheet(exportData, {
      header: [
        "productionLine",
        "lot",
        "poCode",
        "planningCode",
        "numberOfItems",
        "itemCode",
        "productName",
        "sapWo",
        "version",
        "qtyBox",
        "customers",
        "dateCode",
        "manufactureDate",
        "qdsx",
        "inspectionResult",
        "inspector",
        "totalQuantity",
        "itemNoSku",
        "boxesPerPallet",
        "branch",
        "team",
      ],
    });

    const headerMapping: { [key: string]: string } = {
      productionLine: "ProductionLine",
      lot: "Lot",
      poCode: "PoCode",
      planningCode: "PlanningCode",
      numberOfItems: "Numbe Items",
      itemCode: "ItemCode",
      productName: "ProductName",
      sapWo: "SapWo",
      version: "Versio",
      qtyBox: "QtyBox",
      customers: "Customers",
      dateCode: "DateCode",
      manufactureDate: "ManufactureDa",
      qdsx: "QDSX",
      inspectionResult: "Kết Quả Kiểm Tra",
      inspector: "Người kiểm tra",
      totalQuantity: "Tổng số lượng SP",
      itemNoSku: "Item No/SKU",
      boxesPerPallet: "SL/Thùng",
      branch: "Ngành",
      team: "Tổ",
    };

    // Replace headers
    const range = XLSX.utils.decode_range(worksheet["!ref"] ?? "A1");
    for (let C = range.s.c; C <= range.e.c; ++C) {
      const address = XLSX.utils.encode_col(C) + "1";
      if (!worksheet[address]) {
        continue;
      }

      const originalHeader = worksheet[address].v;
      if (headerMapping[originalHeader]) {
        worksheet[address].v = headerMapping[originalHeader];
      }
    }

    //  4. Styling (optional - set column widths)
    const columnWidths = [
      { wch: 20 }, // ProductionLine
      { wch: 15 }, // Lot
      { wch: 12 }, // PoCode
      { wch: 15 }, // PlanningCode
      { wch: 12 }, // Numbe Items
      { wch: 12 }, // ItemCode
      { wch: 30 }, // ProductName
      { wch: 15 }, // SapWo
      { wch: 10 }, // Version
      { wch: 10 }, // QtyBox
      { wch: 15 }, // Customers
      { wch: 12 }, // DateCode
      { wch: 15 }, // ManufactureDate
      { wch: 12 }, // QDSX
      { wch: 18 }, // Kết Quả Kiểm Tra
      { wch: 15 }, // Người kiểm tra
      { wch: 15 }, // Tổng số lượng SP
      { wch: 15 }, // Item No/SKU
      { wch: 12 }, // SL/Thùng
      { wch: 12 }, // Ngành
      { wch: 12 }, // Tổ
    ];
    worksheet["!cols"] = columnWidths;

    //  5. Tạo workbook và xuất file
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Pallet Details");

    // Tạo tên file với timestamp
    const timestamp = new Date()
      .toISOString()
      .replace(/[:.]/g, "-")
      .slice(0, -5);
    const fileName = `Pallet_Export_${timestamp}.xlsx`;

    // Xuất file
    XLSX.writeFile(workbook, fileName);

    console.log(` Exported to ${fileName}`);
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onPrint(item: PalletBoxItem): void {
    const sourceData = this.isMultipleMode
      ? this.palletSources[item.parentPalletIndex ?? 0]
      : this.singlePalletData;

    if (!sourceData) {
      console.error("Không tìm thấy source data cho pallet");
      return;
    }

    const printData: PrintPalletData = {
      //  Data từ pallet đã tạo
      khachHang: sourceData.khachHang ?? "N/A",
      serialPallet: item.maPallet,
      tenSanPham: sourceData.tenSanPham,
      poNumber: sourceData.poNumber ?? "",
      itemNoSku: sourceData.noSKU ?? "",
      dateCode: sourceData.dateCode ?? "",
      soQdsx: sourceData.qdsx ?? "",
      ngaySanXuat: new Date(sourceData.createdAt).toLocaleDateString("vi-VN"),
      nguoiKiemTra: sourceData.nguoiKiemTra ?? "",
      ketQuaKiemTra: sourceData.ketQuaKiemTra ?? "<Empty>",

      // Data từ item hiện tại
      soLuongCaiDatPallet: item.tongSoThung,
      soLuongBaoNgoaiThungGiaPallet: `${item.tienDoScan ?? 0}`,

      // Data cố định (nếu cần có thể truyền từ parent qua dialog data)
      nganh: sourceData.branch ?? "",
      led2: sourceData.branch ?? "",
      to: sourceData.team ?? "",
      lpl2: sourceData.team ?? "",
      thuTuGiaPallet: item.stt,
      slThung: item.tongSoThung * sourceData.tongPallet,

      productCode:
        this.boxItems && this.boxItems.length > 0
          ? `LED${this.boxItems[0].maSanPham}`
          : `LED${item.tenSanPham ?? sourceData.tenSanPham}`,
      serialBox:
        this.boxItems &&
        this.boxItems.length > 0 &&
        this.boxItems[0].subItems &&
        this.boxItems[0].subItems.length > 0
          ? this.boxItems[0].subItems[0].maThung
          : this.boxItems && this.boxItems.length > 0
            ? this.boxItems[0].serialBox
            : item.scannedBoxes && item.scannedBoxes.length > 0
              ? item.scannedBoxes[0]
              : item.maPallet,
      qty:
        this.boxItems && this.boxItems.length > 0
          ? this.boxItems[0].soLuongSp
          : item.scannedBoxes
            ? item.scannedBoxes.length
            : (item.tienDoScan ?? 0),
      lot:
        this.boxItems && this.boxItems.length > 0
          ? this.boxItems[0].lotNumber
          : (sourceData.serialPallet ?? item.maPallet),
      date: new Date().toLocaleDateString("vi-VN"),
      scannedBoxes: item.scannedBoxes,
    };

    console.log(" Print data:", printData);
    this.openPrintDialog(printData);
  }

  openScanDialog(item: PalletBoxItem): void {
    const sourceData = this.isMultipleMode
      ? this.palletSources[item.parentPalletIndex ?? 0]
      : this.singlePalletData;

    if (!sourceData) {
      return;
    }

    const scanData = {
      id: item.maPallet,
      maPallet: item.maPallet,
      tenSanPham: sourceData.tenSanPham,
      qrCode: item.qrCode ?? item.maPallet,
      soThung: item.tongSoThung,
      maLenhSanXuatId: sourceData.maLenhSanXuatId,
      validReelIds: sourceData.validReelIds,
    };
    console.log("Opening scan dialog with data:", scanData);

    const dialogRef = this.dialog.open(ScanPalletDialogComponent, {
      width: "100vw",
      height: "90vh",
      maxWidth: "1200px",
      maxHeight: "900px",
      data: scanData,
      panelClass: "scan-dialog-panel",
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.success) {
        console.log("Completed scan:", result);
        // Update progress
        item.tienDoScan =
          result.totalScanned || result.scannedBoxes?.length || 0;
        // Store scanned box codes
        item.scannedBoxes =
          result.scannedBoxes?.map((box: { code: string }) => box.code) ?? [];
        this.updatePaginatedItems();
      }
    });
  }
  private initializeData(): void {
    // Extract box items from dialog data
    this.boxItems = this.data.boxItems ?? [];

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

  private loadPalletBoxItems(): void {
    this.palletBoxItems = [];
    let globalStt = 1;

    this.palletSources.forEach((source, sourceIndex) => {
      //  Nếu có subItems thì dùng subItems
      if (source.subItems && source.subItems.length > 0) {
        source.subItems.forEach((subItem) => {
          this.palletBoxItems.push({
            ...subItem,
            stt: globalStt++,
            parentPalletIndex: sourceIndex,
            tenSanPham: source.tenSanPham,
            noSKU: source.noSKU,
            scannedBoxes: subItem.scannedBoxes ?? [],
          });
        });
      } else {
        //  Fallback: Generate nếu không có subItems (backward compatibility)
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
          scannedBoxes: [],
        };
        this.palletBoxItems.push(item);
      }
    });

    this.totalItems = this.palletBoxItems.length;

    console.log(" Loaded palletBoxItems:", this.palletBoxItems.length);
    console.log(" Data:", this.palletBoxItems);
  }

  private updatePaginatedItems(): void {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedItems = this.palletBoxItems.slice(startIndex, endIndex);
  }
}
