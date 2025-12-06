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
import { PlanningWorkOrderService } from "../service/planning-work-order.service";
import { finalize } from "rxjs";
import { BoxDetailData } from "../detail-box-dialog/detail-box-dialog.component";

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
  note: string;
  nguoiKiemTra?: string;
  ketQuaKiemTra?: string;
  subItems?: PalletBoxItem[];
  branch?: string; // Ngành
  team?: string; // Tổ
  boxItems?: any[]; // Box data for print dialog
  maLenhSanXuatId?: number; // Production order ID
  validReelIds?: string[]; // Valid reel IDs for this production order
  tienDoScan?: any;
  scannedBoxes?: any;
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
  lotNumber?: string;
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

  isLoadingProgress: { [maPallet: string]: boolean } = {};

  // Mode tracking
  isMultipleMode = false;
  palletSources: PalletDetailData[] = [];

  // Single mode data
  singlePalletData?: PalletDetailData;

  // Box data from parent component
  boxItems: BoxDetailData[] = [];

  // Pagination
  pageSize = 10;
  pageIndex = 0;
  totalItems = 0;

  constructor(
    public dialogRef: MatDialogRef<PalletDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MultiPalletDialogData,
    private dialog: MatDialog,
    private planningService: PlanningWorkOrderService,
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

    console.log("=== DEBUG onPrint ===");
    console.log("item.scannedBoxes:", item.scannedBoxes);
    console.log("sourceData.boxItems:", sourceData.boxItems);

    // ===== FIX: Tính tổng số sản phẩm ĐÚNG =====
    let tongSoSanPhamTrongPallet = 0;
    let soLuongSanPhamTrongMotThung = 0;
    let firstBoxSerial = "";
    let lotNumber = "";

    if (item.scannedBoxes && item.scannedBoxes.length > 0) {
      item.scannedBoxes.forEach((scannedSerial, index) => {
        const trimmedSerial = scannedSerial.trim();

        for (const box of sourceData.boxItems ?? []) {
          // Ưu tiên check subItems trước
          const subItem = box.subItems?.find(
            (sub: { maThung: string }) => sub.maThung === trimmedSerial,
          );
          if (subItem) {
            tongSoSanPhamTrongPallet += subItem.soLuong || 0;
            if (index === 0) {
              soLuongSanPhamTrongMotThung = subItem.soLuong || 0;
              firstBoxSerial = subItem.maThung;
              lotNumber = box.lotNumber || "";
            }
            break;
          }

          // Nếu không phải subItem thì mới check box cha
          if (box.serialBox === trimmedSerial) {
            tongSoSanPhamTrongPallet += box.soLuongSp || 0;
            if (index === 0) {
              soLuongSanPhamTrongMotThung = box.soLuongSp || 0;
              firstBoxSerial = box.serialBox;
              lotNumber = box.lotNumber || "";
            }
            break;
          }
        }
      });
    }

    const tienDoThung = `${item.scannedBoxes?.length ?? 0}/${item.tongSoThung}`;

    const printData: PrintPalletData = {
      khachHang: sourceData.khachHang ?? "N/A",
      serialPallet: item.maPallet,
      tenSanPham: sourceData.tenSanPham,
      poNumber: sourceData.poNumber ?? "",
      itemNoSku: sourceData.noSKU ?? "",
      dateCode: sourceData.dateCode ?? "",
      soQdsx: sourceData.qdsx ?? "",
      ngaySanXuat: new Date(sourceData.createdAt).toLocaleDateString("vi-VN"),
      nguoiKiemTra: sourceData.nguoiKiemTra ?? "",
      ketQuaKiemTra: sourceData.ketQuaKiemTra ?? "",

      nganh: sourceData.branch ?? "",
      led2: sourceData.branch ?? "",
      to: sourceData.team ?? "",
      lpl2: sourceData.team ?? "",

      soLuongCaiDatPallet: tongSoSanPhamTrongPallet,
      thuTuGiaPallet: item.stt,
      soLuongBaoNgoaiThungGiaPallet: item.tongSoThung.toString(),
      slThung: sourceData.tongSlSp,
      note: sourceData.note ?? "",

      productCode: sourceData.tenSanPham,
      serialBox: firstBoxSerial || item.maPallet,
      qty: tongSoSanPhamTrongPallet,
      lot: lotNumber || item.maPallet,
      date: new Date().toLocaleDateString("vi-VN"),
      scannedBoxes: item.scannedBoxes,
    };

    console.log("=== Print single pallet ===", printData);

    // Truyền 1 phiếu dưới dạng object (không phải array)
    this.openPrintDialog(printData);
  }

  onPrintAll(): void {
    const allPrintData: PrintPalletData[] = [];

    this.palletBoxItems.forEach((item, index) => {
      console.log(
        `Processing pallet ${index + 1}/${this.palletBoxItems.length}`,
      );

      const sourceData = this.isMultipleMode
        ? this.palletSources[item.parentPalletIndex ?? 0]
        : this.singlePalletData;

      if (!sourceData) {
        console.warn(`Skipping item ${index + 1} - no source data`);
        return;
      }

      // ===== TÍNH TOÁN SỐ LƯỢNG GIỐNG HỆT onPrint =====
      let tongSoSanPhamTrongPallet = 0;
      let soLuongSanPhamTrongMotThung = 0;
      let firstBoxSerial = "";
      let lotNumber = "";

      if (item.scannedBoxes && item.scannedBoxes.length > 0) {
        item.scannedBoxes.forEach((scannedSerial, boxIndex) => {
          const trimmedSerial = scannedSerial.trim();

          for (const box of sourceData.boxItems ?? []) {
            // Ưu tiên check subItems trước
            const subItem = box.subItems?.find(
              (sub: { maThung: string }) => sub.maThung === trimmedSerial,
            );
            if (subItem) {
              tongSoSanPhamTrongPallet += subItem.soLuong || 0;
              if (boxIndex === 0) {
                soLuongSanPhamTrongMotThung = subItem.soLuong || 0;
                firstBoxSerial = subItem.maThung;
                lotNumber = box.lotNumber || "";
              }
              break;
            }

            // Nếu không phải subItem thì check box cha
            if (box.serialBox === trimmedSerial) {
              tongSoSanPhamTrongPallet += box.soLuongSp || 0;
              if (boxIndex === 0) {
                soLuongSanPhamTrongMotThung = box.soLuongSp || 0;
                firstBoxSerial = box.serialBox;
                lotNumber = box.lotNumber || "";
              }
              break;
            }
          }
        });
      }

      const tienDoThung = `${item.scannedBoxes?.length ?? 0}/${item.tongSoThung}`;

      const printData: PrintPalletData = {
        khachHang: sourceData.khachHang ?? "N/A",
        serialPallet: item.maPallet,
        tenSanPham: sourceData.tenSanPham,
        poNumber: sourceData.poNumber ?? "",
        itemNoSku: sourceData.noSKU ?? "",
        dateCode: sourceData.dateCode ?? "",
        soQdsx: sourceData.qdsx ?? "",
        ngaySanXuat: new Date(sourceData.createdAt).toLocaleDateString("vi-VN"),
        nguoiKiemTra: sourceData.nguoiKiemTra ?? "",
        ketQuaKiemTra: sourceData.ketQuaKiemTra ?? "",

        nganh: sourceData.branch ?? "",
        led2: sourceData.branch ?? "",
        to: sourceData.team ?? "",
        lpl2: sourceData.team ?? "",

        soLuongCaiDatPallet: tongSoSanPhamTrongPallet,
        thuTuGiaPallet: item.stt,
        soLuongBaoNgoaiThungGiaPallet: item.tongSoThung.toString(),
        slThung: sourceData.tongSlSp,
        note: sourceData.note ?? "",

        productCode: sourceData.tenSanPham,
        serialBox: firstBoxSerial || item.maPallet,
        qty: tongSoSanPhamTrongPallet,
        lot: lotNumber || item.maPallet,
        date: new Date().toLocaleDateString("vi-VN"),
        scannedBoxes: item.scannedBoxes,
      };

      console.log(
        `Added print data for pallet ${index + 1}:`,
        printData.serialPallet,
      );
      allPrintData.push(printData);
    });

    console.log(`Total print data created: ${allPrintData.length}`);

    if (allPrintData.length === 0) {
      console.error("No print data generated!");
      alert("Không có pallet nào để in!");
      return;
    }

    // ===== MỞ DIALOG VỚI ARRAY =====
    console.log(
      "Opening print dialog with array of",
      allPrintData.length,
      "items",
    );
    this.openPrintDialog(allPrintData);
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

    const dialogRef = this.dialog.open(ScanPalletDialogComponent, {
      width: "100vw",
      height: "90vh",
      maxWidth: "1200px",
      maxHeight: "900px",
      data: scanData,
      panelClass: "scan-dialog-panel",
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        // CÂP NHẬT PROGRESS VÀ SCANNED BOXES
        item.tienDoScan = result.progressPercent ?? 0;
        item.scannedBoxes =
          (result.scannedBoxes as Array<{ code: string }>)?.map(
            (box) => box.code,
          ) ?? [];

        console.log(" Updated item:", {
          maPallet: item.maPallet,
          tienDoScan: item.tienDoScan,
          scannedBoxesCount: item.scannedBoxes?.length,
        });

        //  CẬP NHẬT LẠI SOURCE DATA
        if (this.isMultipleMode && item.parentPalletIndex !== undefined) {
          const source = this.palletSources[item.parentPalletIndex];
          if (source.subItems) {
            const subItemIndex = source.subItems.findIndex(
              (sub) => sub.maPallet === item.maPallet,
            );
            if (subItemIndex > -1) {
              source.subItems[subItemIndex].tienDoScan =
                result.progressPercent ?? 0;
              source.subItems[subItemIndex].scannedBoxes = item.scannedBoxes;
            }
          }
        } else if (this.singlePalletData) {
          this.singlePalletData.tienDoScan = result.progressPercent ?? 0;
          this.singlePalletData.scannedBoxes = item.scannedBoxes;
        }

        if (sourceData.maLenhSanXuatId) {
          this.loadPalletProgress(item, sourceData.maLenhSanXuatId);
        }
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
  private openPrintDialog(data: PrintPalletData | PrintPalletData[]): void {
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
      console.log(` Processing source ${sourceIndex + 1}:`, {
        serialPallet: source.serialPallet,
        tienDoScan: source.tienDoScan,
        scannedBoxes: source.scannedBoxes?.length,
        subItems: source.subItems?.length,
      });

      if (source.subItems && source.subItems.length > 0) {
        // ===== CÓ SUB-PALLETS =====
        source.subItems.forEach((subItem, subIndex) => {
          //CHECK: subItem đã có dữ liệu chưa?
          const hasTienDo =
            typeof subItem.tienDoScan === "number" && subItem.tienDoScan > 0;
          const hasBoxes =
            Array.isArray(subItem.scannedBoxes) &&
            subItem.scannedBoxes.length > 0;

          console.log(`  📋 SubItem ${subIndex + 1}:`, {
            maPallet: subItem.maPallet,
            hasTienDo,
            tienDoScan: subItem.tienDoScan,
            hasBoxes,
            scannedBoxesCount: subItem.scannedBoxes?.length,
          });

          const item: PalletBoxItem = {
            ...subItem,
            stt: globalStt++,
            parentPalletIndex: sourceIndex,
            tenSanPham: source.tenSanPham,
            noSKU: source.noSKU,

            //SỬ DỤNG DỮ LIỆU ĐÃ CÓ, KHÔNG GHI ĐÈ
            scannedBoxes: subItem.scannedBoxes ?? [],
            tienDoScan: subItem.tienDoScan ?? 0, // ← GIỮ NGUYÊN giá trị đã load
          };

          this.palletBoxItems.push(item);

          // CHỈ LOAD KHI CHƯA CÓ DỮ LIỆU
          if (!hasTienDo && !hasBoxes && source.maLenhSanXuatId) {
            console.log(`  🔄 Loading progress for ${subItem.maPallet}...`);
            this.loadPalletProgress(item, source.maLenhSanXuatId);
          } else {
            console.log(` Using existing progress for ${subItem.maPallet}`);
          }
        });
      } else {
        // ===== KHÔNG CÓ SUB-PALLETS =====
        const hasTienDo =
          typeof source.tienDoScan === "number" && source.tienDoScan > 0;
        const hasBoxes =
          Array.isArray(source.scannedBoxes) && source.scannedBoxes.length > 0;

        console.log(`  📋 Direct pallet:`, {
          serialPallet: source.serialPallet,
          hasTienDo,
          tienDoScan: source.tienDoScan,
          hasBoxes,
          scannedBoxesCount: source.scannedBoxes?.length,
        });

        const item: PalletBoxItem = {
          stt: globalStt++,
          maPallet: source.serialPallet,
          qrCode: source.serialPallet,
          tongSoThung: source.tongSoThung ?? 0,
          sucChua: `${source.tongSoThung ?? 0} thùng`,
          parentPalletIndex: sourceIndex,
          tenSanPham: source.tenSanPham,
          noSKU: source.noSKU,

          // SỬ DỤNG DỮ LIỆU ĐÃ CÓ TỪ SOURCE
          scannedBoxes: source.scannedBoxes ?? [],
          tienDoScan: source.tienDoScan ?? 0, // ← GIỮ NGUYÊN giá trị đã load
        };

        this.palletBoxItems.push(item);

        // CHỈ LOAD KHI CHƯA CÓ DỮ LIỆU
        if (!hasTienDo && !hasBoxes && source.maLenhSanXuatId) {
          console.log(` Loading progress for ${source.serialPallet}...`);
          this.loadPalletProgress(item, source.maLenhSanXuatId);
        } else {
          console.log(` Using existing progress for ${source.serialPallet}`);
        }
      }
    });

    this.totalItems = this.palletBoxItems.length;

    console.log(` SUMMARY:`);
    console.log(`  Total pallets: ${this.palletBoxItems.length}`);
    console.log(
      `  Pallets with progress:`,
      this.palletBoxItems.filter((p) => (p.tienDoScan ?? 0) > 0).length,
    );
    console.log(` loadPalletBoxItems() completed`);
  }

  private updatePaginatedItems(): void {
    const startIndex = this.pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedItems = this.palletBoxItems.slice(startIndex, endIndex);
  }

  private loadPalletProgress(
    item: PalletBoxItem,
    maLenhSanXuatId: number,
  ): void {
    if (!item.maPallet) {
      console.warn(" Missing maPallet, cannot load progress");
      return;
    }

    // Set loading state
    this.isLoadingProgress[item.maPallet] = true;

    // Gọi API lấy danh sách box đã scan cho pallet này
    this.planningService
      .getMappings(item.maPallet)
      .pipe(
        finalize(() => {
          this.isLoadingProgress[item.maPallet] = false;
          this.updatePaginatedItems(); // Refresh UI
        }),
      )
      .subscribe({
        next: (mappings) => {
          // Lọc ra các box có status = 1 (success)
          const successMappings = mappings.filter((m) => m.status === 1);

          // Update tiến độ
          item.tienDoScan = successMappings.length;
          item.scannedBoxes = successMappings.map((m) => m.serial_box);

          console.log(
            ` Pallet ${item.maPallet}: ${item.tienDoScan}/${item.tongSoThung} thùng đã scan`,
          );
        },
        error: (error) => {
          // 404 = chưa có box nào được scan → OK
          if (error.status === 404) {
            console.log(` Pallet ${item.maPallet}: Chưa có box nào được scan`);
            item.tienDoScan = 0;
            item.scannedBoxes = [];
          } else {
            console.error(
              `Error loading progress for ${item.maPallet}:`,
              error,
            );
            item.tienDoScan = 0;
          }
        },
      });
  }
}
