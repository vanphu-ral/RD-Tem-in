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
import * as XLSX from "xlsx";
import { HttpClient } from "@angular/common/http";
import { AlertService } from "app/core/util/alert.service";
// import {
//   LotDetailDialogComponent,
//   LotDetailDialogData,
// } from "../lot-detail-dialog/lot-detail-dialog.component";
import {
  ApproveVendorTemPayload,
  CreateVendorTemDetailPayload,
  ManagerTemNccService,
  PoDetail,
  PoImportTem,
  VendorTemDetail,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { ActivatedRoute } from "@angular/router";
import {
  ApproveLotDetailDialogComponent,
  LotDetailDialogData,
} from "../approve-lot-detail-dialog/approve-lot-detail-dialog.component";
import { SelectionModel } from "@angular/cdk/collections";
import { forkJoin, map, Observable, of } from "rxjs";
import {
  ConfirmDialogData,
  DialogContentExampleDialogComponent,
} from "../list/confirm-dialog/confirm-dialog.component";
import { StatusBadgeService } from "app/entities/list-material/services/status-badge.service";

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
  // quantityBoxOrder: number;
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
    // "quantityBoxOrder",
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
  selectedLots = new SelectionModel<string>(true, []);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  private expandedLots = new Set<string>();
  /** Tracks which parent rows are expanded */
  private expandedRows = new Set<number>();

  /** Tracks which child items are expanded */
  private expandedChildren = new Set<string>();

  private currentTransactionId: number | null = null;
  private currentPoImportTemId: number | null = null;

  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private managerTemNccService: ManagerTemNccService,
    private notificationService: NotificationService,
    private http: HttpClient,
    public statusBadgeService: StatusBadgeService,
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
  getLotExpandKey(parentId: number, lotIdx: number): string {
    return `${parentId}_lot_${lotIdx}`;
  }

  isLotExpanded(parentId: number, lotIdx: number): boolean {
    return this.expandedLots.has(this.getLotExpandKey(parentId, lotIdx));
  }
  toggleDetailSelect(
    detail: VendorTemDetail,
    parentId: number,
    lotIdx: number,
  ): void {
    if (this.isDetailApproved(detail)) {
      return;
    }
    const key = this.getLotKey(parentId, lotIdx);
    this.selectedLots.toggle(key);
  }
  toggleLotExpand(parentId: number, lotIdx: number): void {
    const key = this.getLotExpandKey(parentId, lotIdx);
    if (this.expandedLots.has(key)) {
      this.expandedLots.delete(key);
    } else {
      this.expandedLots.add(key);
    }
  }

  isDetailApproved(detail: VendorTemDetail): boolean {
    return (detail.status ?? "").toUpperCase() === "APPROVE";
  }

  isDetailSelected(detail: VendorTemDetail): boolean {
    return (
      this.isDetailApproved(detail) ||
      this.getSelectedLotDetails().some((d) => d.id === detail.id)
    );
  }
  getLotKey(parentId: number, lotIdx: number): string {
    return `${parentId}_${lotIdx}`;
  }

  isLotSelected(parentId: number, lotIdx: number): boolean {
    return this.selectedLots.isSelected(this.getLotKey(parentId, lotIdx));
  }

  toggleLotSelect(parentId: number, lotIdx: number): void {
    this.selectedLots.toggle(this.getLotKey(parentId, lotIdx));
  }

  isAllLotsSelected(row: ParentItem): boolean {
    const allLots = row.children.flatMap((c) => c.lots);
    if (!allLots.length) {
      return false;
    }
    return allLots.every((_, i) => this.isLotSelected(row.id, i));
  }

  isSomeLotsSelected(row: ParentItem): boolean {
    const allLots = row.children.flatMap((c) => c.lots);
    return (
      allLots.some((_, i) => this.isLotSelected(row.id, i)) &&
      !this.isAllLotsSelected(row)
    );
  }

  toggleAllLots(row: ParentItem): void {
    const allLots = row.children.flatMap((c) => c.lots);
    if (this.isAllLotsSelected(row)) {
      allLots.forEach((_, i) =>
        this.selectedLots.deselect(this.getLotKey(row.id, i)),
      );
    } else {
      allLots.forEach((_, i) =>
        this.selectedLots.select(this.getLotKey(row.id, i)),
      );
    }
  }

  get selectedLotCount(): number {
    return this.selectedLots.selected.length;
  }

  getSelectedLotDetails(): VendorTemDetail[] {
    const result: VendorTemDetail[] = [];
    this.dataSource.data.forEach((row) => {
      row.children.forEach((child) => {
        child.lots.forEach((lot, i) => {
          if (this.isLotSelected(row.id, i)) {
            result.push(...(lot.details ?? []));
          }
        });
      });
    });
    return result;
  }

  onExportExcel(): void {
    if (!this.selectedLotCount) {
      this.notificationService.warning(
        "Vui lòng chọn ít nhất một LOT để xuất.",
      );
      return;
    }

    const rows: any[] = [];

    this.dataSource.data.forEach((row) => {
      row.children.forEach((child) => {
        child.lots.forEach((lot, i) => {
          if (!this.isLotSelected(row.id, i)) {
            return;
          }

          rows.push({
            "Mã SAP": row.sapCode ?? "",
            "Tên hàng hóa": row.materialName ?? "",
            "Part Number": lot.partNumber ?? "",
            Lot: lot.lotNumber ?? "",
            "Số thùng": lot.boxCount ?? 0,
            "Tổng số lượng": lot.totalQty ?? 0,
          });
        });
      });
    });

    if (!rows.length) {
      this.notificationService.warning("Không có dữ liệu để xuất.");
      return;
    }

    const ws = XLSX.utils.json_to_sheet(rows);

    // style header row xanh la
    const headerRange = XLSX.utils.decode_range(ws["!ref"] ?? "A1");
    for (let col = headerRange.s.c; col <= headerRange.e.c; col++) {
      const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col });
      if (!ws[cellAddress]) {
        continue;
      }
      ws[cellAddress].s = {
        fill: { fgColor: { rgb: "92D050" } },
        font: { bold: true },
        alignment: { horizontal: "center" },
      };
    }

    // do rong cot
    ws["!cols"] = [
      { wch: 14 }, // Ma SAP
      { wch: 40 }, // Ten hang hoa
      { wch: 20 }, // Part Number
      { wch: 22 }, // Lot
      { wch: 10 }, // So thung
      { wch: 16 }, // Tong so luong
    ];

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "VendorTem");

    const fileName = `VendorTem_${this.orderInfo.poCode}_${new Date().toISOString().slice(0, 10)}.xlsx`;
    XLSX.writeFile(wb, fileName);
  }

  onSubmitApproval(): void {
    if (this.selectedLotCount === 0) {
      this.notificationService.warning(
        "Vui lòng chọn ít nhất một LOT để phê duyệt.",
      );
      return;
    }
    if (!this.currentTransactionId) {
      this.notificationService.warning("Không xác định được transaction.");
      return;
    }

    this.openConfirm(
      `Bạn có chắc chắn muốn phê duyệt ${this.selectedLotCount} LOT đã chọn?`,
      "Phê duyệt",
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.executeApproval();
    });
  }

  onReject(): void {
    if (this.selectedLotCount === 0) {
      this.notificationService.warning(
        "Vui lòng chọn ít nhất một LOT để từ chối.",
      );
      return;
    }
    if (!this.currentTransactionId) {
      this.notificationService.warning("Không xác định được transaction.");
      return;
    }

    this.openConfirm(
      `Bạn có chắc chắn muốn từ chối ${this.selectedLotCount} LOT đã chọn?`,
      "Từ chối",
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.executeReject();
    });
  }

  onExportPanacim(): void {
    const selected = this.getSelectedDetails();
    const nonApproved = selected.filter(
      (v) => (v.status ?? "").toUpperCase() !== "APPROVE",
    );

    if (nonApproved.length > 0) {
      this.notificationService.warning(
        "Chỉ có thể xuất Panacim cho các mã đã được phê duyệt.",
      );
      return;
    }
    if (!selected.length) {
      this.notificationService.warning(
        "Vui lòng chọn ít nhất một LOT đã phê duyệt.",
      );
      return;
    }

    this.openConfirm(
      `Bạn có chắc chắn muốn xuất Panacim cho ${selected.length} vật tư đã chọn?`,
      "Xuất Panacim",
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.executeExportPanacim(selected);
    });
  }
  toggleRow(row: ParentItem): void {
    if (this.expandedRows.has(row.id)) {
      this.expandedRows.delete(row.id);
    } else {
      this.expandedRows.add(row.id);
    }
  }

  //logic button

  // co bat ky lot nao duoc chon khong
  get hasSelection(): boolean {
    return this.selectedLotCount > 0;
  }

  // cac detail dang chon co cai nao chua APPROVE khong
  get selectedHasNonApproved(): boolean {
    return this.getSelectedDetails().some(
      (v) => (v.status ?? "").toUpperCase() !== "APPROVE",
    );
  }

  // cac detail dang chon tat ca deu la APPROVE
  get selectedAllApproved(): boolean {
    const selected = this.getSelectedDetails();
    if (!selected.length) {
      return false;
    }
    return selected.every((v) => (v.status ?? "").toUpperCase() === "APPROVE");
  }

  // toan bo don da duoc approve het
  get allDetailsApproved(): boolean {
    const all = this.getAllDetails();
    if (!all.length) {
      return false;
    }
    return all.every((v) => (v.status ?? "").toUpperCase() === "APPROVE");
  }

  // disable nut phe duyet: chua chon, hoac tat ca da approve
  get isApproveDisabled(): boolean {
    return !this.hasSelection || !this.selectedHasNonApproved;
  }

  // disable nut tu choi: giong phe duyet
  get isRejectDisabled(): boolean {
    return !this.hasSelection || !this.selectedHasNonApproved;
  }

  // hien thi panacim: co it nhat 1 detail APPROVE trong toan don
  get showPanacim(): boolean {
    return this.getAllDetails().some(
      (v) => (v.status ?? "").toUpperCase() === "APPROVE",
    );
  }

  // disable panacim: chua chon, hoac selection chua approved het
  get isPanacimDisabled(): boolean {
    return !this.hasSelection || !this.selectedAllApproved;
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

  // onViewLot(lot: LotItem): void {
  //   this.dialog.open(ApproveLotDetailDialogComponent, {
  //     width: "100vw",
  //     height: "100vh",
  //     maxWidth: "100vw",
  //     maxHeight: "100vh",
  //     panelClass: "lot-detail-dialog-panel",
  //     data: {
  //       partNumber: lot.partNumber || lot.lotNumber,
  //       manufacturingDate: lot.manufacturingDate,
  //       rows: lot.details ?? [],
  //     } as unknown as LotDetailDialogData,
  //   });
  // }

  // ==================== PRIVATE ====================

  private performDelete(item: ParentItem): void {
    // Call service to delete, then reload
  }

  private openConfirm(
    message: string,
    confirmText: string,
  ): Observable<boolean> {
    return this.dialog
      .open(DialogContentExampleDialogComponent, {
        width: "400px",
        data: {
          title: "Xác nhận",
          message,
          confirmText,
          cancelText: "Hủy",
        } as ConfirmDialogData,
      })
      .afterClosed()
      .pipe(map((result) => result === true));
  }
  private loadFromState(data: PoImportTem): void {
    this.orderInfo.poCode = data.poNumber;
    this.orderInfo.vendorName = data.vendorName;
    this.orderInfo.arrivalDate = data.entryDate
      ? new Date(data.entryDate)
      : null;

    const transactionId = history.state?.transactionId as number | undefined;

    // const transaction = data.importVendorTemTransactions?.[0];

    const transaction = transactionId
      ? data.importVendorTemTransactions?.find((t) => t.id === transactionId)
      : data.importVendorTemTransactions?.[0];
    if (transaction) {
      this.currentTransactionId = transaction.id;
      this.orderInfo.warehouse = transaction.storageUnit ?? "";
    }
    this.currentPoImportTemId = data.id;

    const allPoDetails = data.importVendorTemTransactions.flatMap(
      (t) => t.poDetails,
    );
    const poDetails = transaction?.poDetails ?? [];
    this.dataSource.data = this.mapPoDetailsToParentItems(poDetails);
    this.totalItems = this.dataSource.data.length;
  }

  private mapPoDetailsToParentItems(poDetails: PoDetail[]): ParentItem[] {
    return poDetails.map((detail, idx) => {
      const pendingDetails = (detail.vendorTemDetails ?? []).filter(
        (v) => (v.status ?? "").toUpperCase() === "PENDING",
      );

      const lotMap = new Map<string, LotItem>();

      pendingDetails.forEach((v) => {
        const lotKey = v.lot || v.partNumber || `lot_${idx}`;

        if (lotMap.has(lotKey)) {
          const existing = lotMap.get(lotKey)!;
          existing.boxCount += 1;
          existing.totalQty += v.initialQuantity ?? 0;
          existing.details.push({
            ...v,
            sapCode: detail.sapCode,
            sapName: detail.sapName,
          });
        } else {
          lotMap.set(lotKey, {
            lotNumber: lotKey,
            boxCount: 1,
            totalQty: v.initialQuantity ?? 0,
            details: [
              {
                ...v,
                sapCode: detail.sapCode,
                sapName: detail.sapName,
              },
            ],
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

  // tat ca VendorTemDetail cua cac lot dang duoc chon
  private getSelectedDetails(): VendorTemDetail[] {
    const result: VendorTemDetail[] = [];
    this.dataSource.data.forEach((row) => {
      row.children.forEach((child) => {
        child.lots.forEach((lot, i) => {
          if (this.isLotSelected(row.id, i)) {
            result.push(...(lot.details ?? []));
          }
        });
      });
    });
    return result;
  }

  // tat ca detail cua toan bo don
  private getAllDetails(): VendorTemDetail[] {
    return this.dataSource.data
      .flatMap((row) => row.children)
      .flatMap((child) => child.lots)
      .flatMap((lot) => lot.details ?? []);
  }

  private executeApproval(): void {
    const now = new Date().toISOString();
    const details = this.getSelectedLotDetails();
    const poImportTemId = this.currentPoImportTemId ?? 0;

    const approvePayload: ApproveVendorTemPayload = {
      id: this.currentTransactionId!,
      poNumber: this.orderInfo.poCode,
      vendorCode: "",
      vendorName: this.orderInfo.vendorName,
      entryDate: this.orderInfo.arrivalDate
        ? (this.datePipe.transform(this.orderInfo.arrivalDate, "yyyy-MM-dd") ??
          "")
        : "",
      storageUnit: this.orderInfo.warehouse,
      mappingConfig: "",
      status: "APPROVE",
      createdBy: "",
      createdAt: now,
      updatedBy: "",
      updatedAt: now,
      deletedBy: "",
      deletedAt: "",
      approver: this.orderInfo.approver,
      poImportTemId,
    };

    const detailPayloads = this.buildDetailPayloads(details, "APPROVE", now);
    this.isLoading = true;

    forkJoin({
      approve: this.managerTemNccService.approveImportVendorTemTransaction(
        this.currentTransactionId!,
        approvePayload,
      ),
      details:
        detailPayloads.length > 0
          ? this.managerTemNccService.batchUpdateVendorTemDetails(
              detailPayloads,
            )
          : of(null),
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.notificationService.success("Phê duyệt thành công.");
        this.updateLocalStatus(detailPayloads, "APPROVE");
      },
      error: (err) => {
        this.isLoading = false;
        console.error("Loi phe duyet:", err);
        this.notificationService.error("Phê duyệt thất bại.");
      },
    });
  }

  private executeReject(): void {
    const now = new Date().toISOString();
    const details = this.getSelectedLotDetails();
    const poImportTemId = this.currentPoImportTemId ?? 0;

    const rejectPayload: ApproveVendorTemPayload = {
      id: this.currentTransactionId!,
      poNumber: this.orderInfo.poCode,
      vendorCode: "",
      vendorName: this.orderInfo.vendorName,
      entryDate: this.orderInfo.arrivalDate
        ? (this.datePipe.transform(this.orderInfo.arrivalDate, "yyyy-MM-dd") ??
          "")
        : "",
      storageUnit: this.orderInfo.warehouse,
      mappingConfig: "",
      status: "REJECT",
      createdBy: "",
      createdAt: now,
      updatedBy: "",
      updatedAt: now,
      deletedBy: "",
      deletedAt: "",
      approver: this.orderInfo.approver,
      poImportTemId,
    };

    const detailPayloads = this.buildDetailPayloads(details, "REJECT", now);
    this.isLoading = true;

    forkJoin({
      reject: this.managerTemNccService.approveImportVendorTemTransaction(
        this.currentTransactionId!,
        rejectPayload,
      ),
      details:
        detailPayloads.length > 0
          ? this.managerTemNccService.batchUpdateVendorTemDetails(
              detailPayloads,
            )
          : of(null),
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.notificationService.success("Từ chối thành công.");
        this.updateLocalStatus(detailPayloads, "REJECT");
      },
      error: (err) => {
        this.isLoading = false;
        console.error("Loi tu choi:", err);
        this.notificationService.error("Từ chối thất bại.");
      },
    });
  }

  private buildDetailPayloads(
    details: VendorTemDetail[],
    status: string,
    now: string,
  ): CreateVendorTemDetailPayload[] {
    return details
      .filter((v) => !!v.id)
      .map((v) => ({
        id: v.id,
        reelId: v.reelId ?? "",
        partNumber: v.partNumber ?? "",
        vendor: v.vendor ?? "",
        lot: v.lot ?? "",
        userData1: v.userData1 ?? "",
        userData2: v.userData2 ?? "",
        userData3: v.userData3 ?? "",
        userData4: v.userData4 ?? "",
        userData5: v.userData5 ?? "",
        initialQuantity: v.initialQuantity ?? 0,
        msdLevel: v.msdLevel ?? "",
        msdInitialFloorTime: "",
        msdBagSealDate: "",
        marketUsage: "",
        quantityOverride: 0,
        shelfTime: "",
        spMaterialName: "",
        warningLimit: "",
        maximumLimit: "",
        comments: "",
        warmupTime: "",
        storageUnit: v.storageUnit ?? "",
        subStorageUnit: "",
        locationOverride: "",
        expirationDate: v.expirationDate ?? "",
        manufacturingDate: v.manufacturingDate ?? "",
        partClass: "",
        sapCode: v.sapCode ?? "",
        vendorQrCode: v.vendorQrCode ?? "",
        status,
        createdBy: v.createdBy ?? "",
        createdAt: v.createdAt ?? now,
        updatedBy: "",
        updatedAt: now,
        poDetailId: v.poDetailId ?? 0,
        importVendorTemTransactionsId: this.currentTransactionId!,
      }));
  }

  private updateLocalStatus(
    payloads: CreateVendorTemDetailPayload[],
    status: string,
  ): void {
    const ids = new Set(payloads.map((d) => d.id));
    this.dataSource.data = this.dataSource.data.map((row) => ({
      ...row,
      children: row.children.map((child) => ({
        ...child,
        lots: child.lots.map((lot) => ({
          ...lot,
          details: lot.details.map((v) =>
            ids.has(v.id) ? { ...v, status } : v,
          ),
        })),
      })),
    }));
    this.selectedLots.clear();
    this.cdr.detectChanges();
  }
  private executeExportPanacim(details: VendorTemDetail[]): void {
    const csvContent = this.generatePanacimCsv(details);
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });

    const formData = new FormData();
    const fileName = `CSV_${this.orderInfo.poCode}_${new Date().toISOString().split("T")[0]}.csv`;
    formData.append("file", blob, fileName);

    this.isLoading = true;

    this.http.post<any>("/api/csv-upload", formData).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response && response.success === true) {
          this.notificationService.success(
            `Đã gửi ${details.length} vật tư tới Panacim thành công.`,
          );
        } else {
          this.notificationService.error(
            `Lỗi: ${response?.message ?? "Không có phản hồi từ server"}`,
          );
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.notificationService.error(
          `Lỗi khi gửi: ${err.error?.message ?? err.message ?? "Không thể kết nối đến server"}`,
        );
      },
    });
  }

  private generatePanacimCsv(details: VendorTemDetail[]): string {
    const headers = [
      "ReelID",
      "PartNumber",
      "Vendor",
      "Lot",
      "UserData1",
      "UserData2",
      "UserData3",
      "UserData4",
      "UserData5",
      "InitialQuantity",
      "MsdLevel",
      "MsdInitialFloorTime",
      "MsdBagSealDate",
      "MarketUsage",
      "QuantityOverride",
      "ShelfTime",
      "SpMaterialName",
      "WarningLimit",
      "MaximumLimit",
      "Comments",
      "WarmupTime",
      "StorageUnit",
      "SubStorageUnit",
      "LocationOverride",
      "ExpirationDate",
      "ManufacturingDate",
      "PartClass",
      "SapCode",
    ];

    const rows = details.map((v) => [
      v.reelId ?? "",
      v.partNumber ?? "",
      v.vendor ?? "",
      v.lot ?? "",
      v.userData1 ?? "",
      v.userData2 ?? "",
      v.userData3 ?? "",
      v.userData4 ?? "",
      v.userData5 ?? "",
      v.initialQuantity ?? "",
      v.msdLevel ?? "",
      "", // msdInitialFloorTime
      "", // msdBagSealDate
      "", // marketUsage
      "0", // quantityOverride
      "", // shelfTime
      "", // spMaterialName
      "", // warningLimit
      "", // maximumLimit
      "", // comments
      "", // warmupTime
      v.storageUnit ?? "",
      "", // subStorageUnit
      "", // locationOverride
      v.expirationDate ?? "",
      v.manufacturingDate ?? "",
      "", // partClass
      v.sapCode ?? "",
    ]);

    const csvRows = [headers, ...rows]
      .map((row) => row.map((cell) => String(cell ?? "")).join(","))
      .join("\n");

    return "\ufeff" + csvRows;
  }
}
