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
// import {
//   LotDetailDialogComponent,
//   LotDetailDialogData,
// } from "../lot-detail-dialog/lot-detail-dialog.component";
import {
  ManagerTemNccService,
  PoDetail,
  PoImportTem,
  VendorTemDetail,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { ActivatedRoute } from "@angular/router";

// ==================== INTERFACES ====================

export interface LotItem {
  lotNumber: string;
  boxCount: number;
  totalQty: number;
  details: VendorTemDetail[];
  reelId: string;
  partNumber: string;
  manufacturingDate: string;
  expirationDate: string;
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
  children: ChildItem[];
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
  templateUrl: "./approve-tem-ncc-detail.component.html",
  styleUrls: ["./approve-tem-ncc-detail.component.scss"],
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
export class ApproveTemNccDetailComponent implements OnInit, AfterViewInit {
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

  statusOptions = ["Đã tạo mã QR", "Bản nháp"];

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

  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private managerTemNccService: ManagerTemNccService,
    private notificationService: NotificationService,
  ) {}

  // ==================== LIFECYCLE ====================

  ngOnInit(): void {
    // const id = this.route.snapshot.paramMap.get('id');
    // if (id) {
    //   this.loadData(Number(id));
    // } else {
    //   this.loadData();
    // }
    const data = history.state?.data as PoImportTem | undefined;

    console.log("[DETAIL] history.state:", history.state);
    console.log("[DETAIL] data:", data);

    if (data && data.importVendorTemTransactions) {
      this.loadFromState(data);
    } else {
      console.warn(
        "[DETAIL] Không có data hoặc thiếu importVendorTemTransactions",
      );
    }
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

  onApply(): void {
    // Filter/load danh sách vật tư theo orderInfo
  }

  onScan(): void {
    // Mở dialog scan QR
  }

  onSubmitApproval(): void {
    // Gửi phê duyệt
  }

  isExpanded(row: ParentItem): boolean {
    return this.expandedRows.has(row.id);
  }

  hasLots(row: ParentItem): boolean {
    return row.children?.some((c) => c.lots && c.lots.length > 0) ?? false;
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
    // this.dialog.open(LotDetailDialogComponent, {
    //   width: '100vw',
    //   height: '100vh',
    //   maxWidth: '100vw',
    //   maxHeight: '100vh',
    //   panelClass: 'lot-detail-dialog-panel',
    //   data: {
    //     partNumber: lot.partNumber || lot.lotNumber,
    //     manufacturingDate: lot.manufacturingDate,
    //     rows: lot.details ?? [],
    //   } as unknown as LotDetailDialogData,
    // });
  }

  // ==================== PRIVATE ====================

  private performDelete(item: ParentItem): void {
    // Call service to delete, then reload
  }
  private loadFromState(data: PoImportTem): void {
    this.orderInfo.poCode = data.poNumber;
    this.orderInfo.vendorName = data.vendorName;
    this.orderInfo.arrivalDate = data.entryDate
      ? new Date(data.entryDate)
      : null;

    const allPoDetails = data.importVendorTemTransactions.flatMap(
      (t) => t.poDetails,
    );

    console.log("[DETAIL] allPoDetails:", allPoDetails);

    this.dataSource.data = this.mapPoDetailsToParentItems(allPoDetails);
    this.totalItems = this.dataSource.data.length;

    console.log("[DETAIL] dataSource:", this.dataSource.data);
  }
  // private loadData(id?: number): void {
  //   this.isLoading = true;
  //   this.managerTemNccService.getPoImportTemById(id).subscribe({
  //     next: (data) => {
  //       this.orderInfo.poCode = data.poNumber;
  //       this.orderInfo.vendorName = data.vendorName;
  //       this.orderInfo.arrivalDate = data.entryDate ? new Date(data.entryDate) : null;

  //       // Gộp tất cả poDetails từ tất cả transactions
  //       const allPoDetails: PoDetail[] = data.importVendorTemTransactions
  //         .flatMap(t => t.poDetails);

  //       this.dataSource.data = this.mapPoDetailsToParentItems(allPoDetails);
  //       this.totalItems = this.dataSource.data.length;
  //       this.isLoading = false;
  //     },
  //     error: () => {
  //       this.notificationService.error('Không thể tải chi tiết đơn hàng!');
  //       this.isLoading = false;
  //     },
  //   });
  // }

  private mapPoDetailsToParentItems(poDetails: PoDetail[]): ParentItem[] {
    return poDetails.map((detail, idx) => {
      // Gộp vendorTemDetails thành lots theo lotNumber
      const lotMap = new Map<string, LotItem>();

      (detail.vendorTemDetails ?? []).forEach((v) => {
        const lotKey = v.lot || v.partNumber || `lot_${idx}`;
        if (lotMap.has(lotKey)) {
          const existing = lotMap.get(lotKey)!;
          existing.boxCount += 1;
          existing.totalQty += v.initialQuantity ?? 0;
          existing.details.push(v);
        } else {
          lotMap.set(lotKey, {
            lotNumber: lotKey,
            boxCount: 1,
            totalQty: v.initialQuantity ?? 0,
            details: [v],
            reelId: v.reelId,
            partNumber: v.partNumber,
            manufacturingDate: v.manufacturingDate,
            expirationDate: v.expirationDate,
          });
        }
      });

      const lots = Array.from(lotMap.values());
      const scannedBoxCount = lots.reduce((s, l) => s + l.boxCount, 0);
      const scannedQty = lots.reduce((s, l) => s + l.totalQty, 0);

      return {
        id: detail.id,
        sapCode: detail.sapCode,
        materialName: detail.sapName,
        partNumber: detail.partNumber,
        boxScan: scannedBoxCount,
        totalQuantity: scannedQty,
        quantityBoxOrder: detail.quantityContainer,
        totalQuantityOrder: detail.totalQuantity,
        children: [
          {
            sapCode: detail.sapCode,
            productName: detail.sapName,
            partNumber: detail.partNumber,
            boxCount: scannedBoxCount,
            totalQty: scannedQty,
            orderBoxCount: detail.quantityContainer,
            orderQty: detail.totalQuantity,
            lots,
          },
        ],
      } as ParentItem;
    });
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
