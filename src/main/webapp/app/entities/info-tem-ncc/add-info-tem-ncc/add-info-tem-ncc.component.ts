import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import { CommonModule, DatePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";

import { MatTableModule, MatTableDataSource } from "@angular/material/table";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";

import { AlertService } from "app/core/util/alert.service";
import {
  LotDetailDialogComponent,
  LotDetailDialogData,
} from "../lot-detail-dialog/lot-detail-dialog.component";
import {
  ScanItemDialogComponent,
  ScannedItem,
} from "../scan-item-dialog/scan-item-dialog.component";

// ==================== INTERFACES ====================

export interface LotItem {
  lotNumber: string;
  boxCount: number;
  totalQty: number;
  details: [];
  [key: string]: any;
}

export interface ChildItem {
  sapCode: string;
  productName: string;
  partNumber: string;
  boxCount: number;
  totalQty: number;
  orderBoxCount: number;
  orderQty: number;
  lots: LotItem[];
}

export interface ParentItem {
  id: number;
  sapCode: string;
  materialName: string;
  partNumber: string;
  boxScan: number;
  totalQuantity: number;
  quantityBoxOrder: number;
  totalQuantityOrder: number;
  lots: LotItem[];
}

export interface FilterValues {
  sapCode: string;
  materialName: string;
  partNumber: string;
}

// ==================== COMPONENT ====================

@Component({
  selector: "jhi-info-tem-ncc",
  standalone: false,
  templateUrl: "./add-info-tem-ncc.component.html",
  styleUrls: ["./add-info-tem-ncc.component.scss"],
  animations: [
    trigger("detailExpand", [
      state(
        "collapsed",
        style({ height: "0px", minHeight: "0", overflow: "hidden" }),
      ),
      state("expanded", style({ height: "*", overflow: "hidden" })),
      transition(
        "expanded <=> collapsed",
        animate("220ms cubic-bezier(0.4, 0.0, 0.2, 1)"),
      ),
    ]),
  ],
})
export class AddInfoTemNccComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = [
    "expand",
    "sapCode",
    "materialName",
    "partNumber",
    "boxScan",
    "totalQuantity",
    "quantityBoxOrder",
    "totalQuantityOrder",
  ];
  isScanning = false;
  scanMessage = "";
  lotColumns = [
    { key: "lotNumber", label: "Lot", minWidth: 130 },
    { key: "reelId", label: "ReelId", minWidth: 190 },
    { key: "partNumber", label: "Part Number", minWidth: 140 },
    { key: "vendor", label: "Vendor", minWidth: 110 },
    { key: "boxCount", label: "Số thùng", minWidth: 90 },
    { key: "totalQty", label: "Tổng SL", minWidth: 90 },
    { key: "initialQuantity", label: "Quantity", minWidth: 100 },
    { key: "userData1", label: "Userdata1", minWidth: 110 },
    { key: "userData2", label: "Userdata2", minWidth: 110 },
    { key: "userData3", label: "Userdata3", minWidth: 110 },
    { key: "userData4", label: "Userdata4", minWidth: 110 },
    { key: "userData5", label: "Userdata5", minWidth: 110 },
    { key: "msl", label: "MSL", minWidth: 80 },
    { key: "storageUnit", label: "StorageUnit", minWidth: 120 },
    { key: "manufacturingDate", label: "ManufacturingDate", minWidth: 150 },
    { key: "expirationDate", label: "ExpirationDate", minWidth: 150 },
  ];

  statusOptions = ["Đã tạo mã QR", "Bản nháp"];

  lotBulkValues: { [key: string]: any } = {};

  lotEditingCell: { rowId: number; lotIdx: number; col: string } | null = null;

  filterValues: FilterValues = {
    sapCode: "",
    materialName: "",
    partNumber: "",
  };

  orderInfo = {
    poCode: "",
    vendorName: "",
    arrivalDate: null as Date | null,
    importScenario: "",
    warehouse: "",
    approver: "",
  };

  dataSource = new MatTableDataSource<ParentItem>([]);
  totalItems = 0;
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  /** Tracks which parent rows are expanded */
  private expandedRows = new Set<number>();

  /** Tracks which child items are expanded */
  private expandedChildren = new Set<string>();

  private expandedMobileRows = new Set<number>();

  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
  ) {}

  // ==================== LIFECYCLE ====================

  ngOnInit(): void {
    this.loadData();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.buildFilterPredicate();
  }

  // ==================== EXPAND LOGIC ====================

  toggleRow(row: ParentItem): void {
    if (this.expandedRows.has(row.id)) {
      this.expandedRows.delete(row.id);
    } else {
      this.expandedRows.add(row.id);
    }
  }

  toggleMobileRow(item: ParentItem): void {
    if (this.expandedMobileRows.has(item.id)) {
      this.expandedMobileRows.delete(item.id);
    } else {
      this.expandedMobileRows.add(item.id);
    }
  }

  isMobileExpanded(item: ParentItem): boolean {
    return this.expandedMobileRows.has(item.id);
  }

  onApply(): void {
    // Filter/load danh sách vật tư theo orderInfo
  }

  onScan(): void {
    const dialogRef = this.dialog.open(ScanItemDialogComponent, {
      width: "95vw",
      maxWidth: "1400px",
      panelClass: "scan-dialog-panel",
      autoFocus: false,
    });

    dialogRef.afterClosed().subscribe((result: ScannedItem[] | null) => {
      if (!result) {
        return;
      } // user cancelled

      // result is the confirmed list of scanned items
      console.log("Scanned items:", result);

      // TODO: map result to your component's data model
      // e.g. result.forEach(item => this.processScan(item.code));
    });
  }
  onCancelScan(): void {
    this.isScanning = false;
    this.scanMessage = "";
  }
  hasLots(row: ParentItem): boolean {
    return row.lots && row.lots.length > 0;
  }

  onSubmitApproval(): void {
    // Gửi phê duyệt
  }

  isExpanded(row: ParentItem): boolean {
    return this.expandedRows.has(row.id);
  }
  applyLotColumn(row: ParentItem, colKey: string): void {
    const value = this.lotBulkValues[row.id + "_" + colKey];
    if (value === "" || value === null || value === undefined) {
      return;
    }
    row.lots = row.lots.map((lot) => ({ ...lot, [colKey]: value }));
  }

  startLotEdit(rowId: number, lotIdx: number, col: string): void {
    this.lotEditingCell = { rowId, lotIdx, col };
  }

  stopLotEdit(): void {
    this.lotEditingCell = null;
  }
  toggleChild(child: ChildItem): void {
    const key = child.sapCode + "_" + child.partNumber;
    if (this.expandedChildren.has(key)) {
      this.expandedChildren.delete(key);
    } else {
      this.expandedChildren.add(key);
    }
  }

  isChildExpanded(child: ChildItem): boolean {
    const key = child.sapCode + "_" + child.partNumber;
    return this.expandedChildren.has(key);
  }

  // ==================== FILTER ====================

  applyFilter(): void {
    this.dataSource.filterPredicate = this.buildFilterPredicate();
    this.dataSource.filter = JSON.stringify(this.filterValues);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  applyDateFilter(): void {
    this.applyFilter();
  }

  // ==================== MOBILE DATA ====================

  get mobileDataSource(): ParentItem[] {
    const data = this.dataSource.filteredData ?? this.dataSource.data ?? [];
    if (!this.paginator) {
      return data;
    }
    const start = this.paginator.pageIndex * this.paginator.pageSize;
    return data.slice(start, start + this.paginator.pageSize);
  }

  // ==================== STATUS COLOR ====================

  statusColor(status: string): { [key: string]: string } {
    switch ((status ?? "").toLowerCase().trim()) {
      case "bản nháp":
        return {
          backgroundColor: "#FFF9B0",
          color: "#7A6A00",
          border: "1px solid #F0E68C",
        };
      case "đã tạo mã qr":
      case "đã tạo mã":
      case "đã gen mã":
        return {
          backgroundColor: "#2be265",
          color: "#1B5E35",
          border: "1px solid #A3D9B8",
        };
      default:
        return {
          backgroundColor: "#E0E0E0",
          color: "#333",
          border: "1px solid #CCC",
        };
    }
  }

  // ==================== ACTIONS ====================

  onDelete(item: ParentItem): void {}

  onEditChild(child: ChildItem): void {
    // Implement edit child logic
  }

  onViewLot(lot: LotItem): void {
    this.dialog.open(LotDetailDialogComponent, {
      width: "100vw",
      height: "100vh",
      maxWidth: "100vw",
      maxHeight: "100vh",
      panelClass: "lot-detail-dialog-panel",
      data: {
        partNumber: lot.lotNumber,
        manufacturingDate: "20250603",
        rows: lot.details ?? [],
      } as LotDetailDialogData,
    });
  }

  // ==================== PRIVATE ====================

  private performDelete(item: ParentItem): void {
    // Call service to delete, then reload
  }

  private loadData(): void {
    // Example mock data — replace with real service call
    const mockData: ParentItem[] = [
      {
        id: 1,
        sapCode: "00098081",
        materialName: "Module DR-ML MXL1142 24W-TC (Yankon)",
        partNumber: "31120166",
        boxScan: 3,
        totalQuantity: 30000,
        quantityBoxOrder: 3,
        totalQuantityOrder: 30000,
        lots: [
          { lotNumber: "31120166", boxCount: 1, totalQty: 10000, details: [] },
          { lotNumber: "31120166", boxCount: 1, totalQty: 10000, details: [] },
          { lotNumber: "31120166", boxCount: 1, totalQty: 10000, details: [] },
        ],
      },
      {
        id: 2,
        sapCode: "00098081",
        materialName: "Module LED ZDL1356 0.1W 3000K",
        partNumber: "00098081_V1.1",
        boxScan: 0,
        totalQuantity: 0,
        quantityBoxOrder: 1,
        totalQuantityOrder: 10000,
        lots: [],
      },
      {
        id: 3,
        sapCode: "00012345",
        materialName: "Driver MTSL1056 3W SMT (Yankon)",
        partNumber: "00088021_V1.1",
        boxScan: 0,
        totalQuantity: 0,
        quantityBoxOrder: 1,
        totalQuantityOrder: 10000,
        lots: [],
      },
    ];
    this.dataSource.data = mockData;
    this.totalItems = mockData.length;
  }

  private buildFilterPredicate() {
    return (item: ParentItem, filterJson: string): boolean => {
      const f: FilterValues = JSON.parse(filterJson);
      const match = (field: string, query: string): boolean =>
        !query || (field ?? "").toLowerCase().includes(query.toLowerCase());
      return (
        match(item.sapCode, f.sapCode) &&
        match(item.materialName, f.materialName) &&
        match(item.partNumber, f.partNumber)
      );
    };
  }

  private sameDate(a: Date | string, b: Date): boolean {
    const d = new Date(a);
    return (
      d.getFullYear() === b.getFullYear() &&
      d.getMonth() === b.getMonth() &&
      d.getDate() === b.getDate()
    );
  }
}
