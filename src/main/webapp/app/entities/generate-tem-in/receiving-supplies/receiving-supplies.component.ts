import {
  Component,
  OnInit,
  AfterViewInit,
  AfterViewChecked,
  ViewChild,
  ChangeDetectorRef,
} from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import {
  catchError,
  forkJoin,
  map,
  Observable,
  of,
  switchMap,
  take,
} from "rxjs";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AccountService } from "app/core/auth/account.service";
import { ExcelImportData } from "../models/list-product-of-request.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { GenerateTemInConfirmDialogComponent } from "../generate-tem-in-modal-confirm/modal-confirm.component";
import { TemDetail } from "../detail/generate-tem-in-detail.component";
import {
  GoodsReceiptPoLine,
  PoReconcileResponse,
  ReceivingSuppliesService,
  resolveHttpErrorMessage,
  SapOcrd,
  SapOitmDto,
  SapPoInfoResponse,
  validateStorageUnitForSap,
  WarehouseLocation,
} from "../service/receiving-supplies.service";

interface SavedProductRow {
  id: number;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number;
  temQuantity: number;
  vendor: string;
  vendorName?: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
}
import {
  generateLabelBarcodes,
  runLabelPrint,
} from "./receiving-label-print.util";

interface SapOitmRowInfo {
  code: string;
  itemName: string;
  partNumber: string;
}

export interface ReceivingLotRow {
  rowKey: string;
  /** ID sản phẩm đã lưu (list_product_of_request) — dùng map tem sau khi tạo mã. */
  productId?: number;
  lotLabel?: string;
  location: string;
  /** true khi user tự chọn vị trí ở lớp con — không ghi đè khi đổi vị trí cha. */
  locationOverridden: boolean;
  lotNumber: string;
  quantity: number | null;
  temQuantity: number | null;
  manufacturingDate: Date | null;
  expirationDate: Date | null;
  arrivalDate: Date | null;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  vendor: string;
  vendorName: string;
  note: string;
  /** Đã gửi SAP thành công trong phiên hiện tại. */
  sapSent?: boolean;
}

export interface ReceivingLotDetailView {
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  vendor: string;
  vendorName: string;
  temQuantity: number | null;
  initialQuantity: number | null;
  storageUnit: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  manufacturingDate: Date | null;
  expirationDate: Date | null;
  arrivalDate: Date | null;
}

/** Dòng tem trong modal — cùng cấu trúc bảng detail. */
export interface ReceivingTemPreviewRow {
  id: number | null;
  reelId: string;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number | null;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit: string;
  expirationDate: Date | null;
  manufacturingDate: Date | null;
  arrivalDate: Date | null;
  qrCode: string;
}

/** Nhãn in tem — ngày dạng chuỗi giống detail. */
export interface ReceivingPrintLabel {
  id: string;
  qrCode: string;
  reelId: string;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number | null;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit: string;
  manufacturingDate: string;
  expirationDate: string;
  arrivalDate: string;
}

export type ReconcileStatus = "pending" | "passed" | "failed";

export interface ReceivingMaterialRow {
  id: number;
  sapCode: string;
  itemName: string;
  partNumber: string;
  location: string;
  quantityByPo: number;
  quantity: number | null;
  lotNumber: string;
  temQuantity: number | null;
  manufacturingDate: Date | null;
  expirationDate: Date | null;
  note: string;
  lots: ReceivingLotRow[];
  expanded: boolean;
  /** Trạng thái đối chiếu PO — chỉ dòng cha. */
  reconcileStatus: ReconcileStatus;
}

@Component({
  selector: "jhi-receiving-supplies",
  standalone: false,
  templateUrl: "./receiving-supplies.component.html",
  styleUrls: ["./receiving-supplies.component.scss"],
})
export class ReceivingSuppliesComponent
  implements OnInit, AfterViewInit, AfterViewChecked
{
  isLoading = false;
  isFetchingPo = false;
  isSavingOrder = false;
  isGenerating = false;
  isSendingSap = false;
  isDisableGenerate = false;
  withPoMode = false;
  editingRequestId: number | null = null;
  requestTemDetails: TemDetail[] = [];

  poNumber = "";
  /** Mã SAP thuộc PO hiện tại (từ sap-po-info). */
  poSapCodes: string[] = [];
  vendorCode = "";
  vendorName = "";
  storageUnit = "";

  bulkLotNumber = "";
  bulkExpirationDate: Date | null = null;
  bulkManufacturingDate: Date | null = null;
  bulkSapCodesInput = "";
  selectedSapCodes: string[] = [];

  filterItemName = "";
  filterPartNumber = "";
  filterLocation = "";

  filteredLocationOptions: WarehouseLocation[] = [];
  vendorOptions: SapOcrd[] = [];
  filteredVendorOptions: SapOcrd[] = [];
  isLoadingVendors = false;
  currentTransactionId: number | null = null;
  currentUser = "system";

  displayedColumns: string[] = [
    "stt",
    "sapCode",
    "itemName",
    "partNumber",
    "location",
    "quantityByPo",
    "quantity",
    "lotNumber",
    "temQuantity",
    "manufacturingDate",
    "expirationDate",
    "note",
    "actions",
  ];

  dataSource = new MatTableDataSource<ReceivingMaterialRow>([]);
  /** Cache trang hiện tại — tránh gọi hàm trong template gây vòng lặp CD. */
  visiblePageRows: ReceivingMaterialRow[] = [];
  totalItems = 0;
  pageSize = 50;
  pageSizeOptions = [25, 50, 100];
  pageIndex = 0;

  showPoReconcileModal = false;
  reconcilePoInput = "";
  isReconcilingPo = false;
  poReconcilePassed = false;
  poReconcilePartialPassed = false;
  poReconcileResult: PoReconcileResponse | null = null;
  verifiedPoNumber = "";
  matchedSapCodesForApply: string[] = [];

  showLotDetailModal = false;
  lotDetailView: ReceivingLotDetailView | null = null;
  lotDetailTemPreview: ReceivingTemPreviewRow[] = [];

  showPrintModal = false;
  printLabels: ReceivingPrintLabel[] = [];
  barcodesGenerated = false;
  isGeneratingBarcodes = false;

  readonly lotDetailTemColumns: string[] = [
    "stt",
    "qrCode",
    "id",
    "reelId",
    "sapCode",
    "productName",
    "partNumber",
    "lot",
    "initialQuantity",
    "vendor",
    "userData1",
    "userData2",
    "userData3",
    "userData4",
    "userData5",
    "storageUnit",
    "expirationDate",
    "manufacturingDate",
    "arrivalDate",
  ];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private readonly vendorDisplayLimit = 50;
  /** ID sản phẩm chờ xóa khi lưu — chỉ khi user bấm xóa lô/vật tư. */
  private pendingDeletedProductIds: number[] = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private receivingService: ReceivingSuppliesService,
    private generateTemInService: GenerateTemInService,
    private accountService: AccountService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const mode = this.route.snapshot.paramMap.get("mode");
    this.withPoMode = mode === "with-po";

    const requestIdParam = this.route.snapshot.paramMap.get("requestId");
    if (requestIdParam) {
      const parsedId = Number(requestIdParam);
      if (!Number.isNaN(parsedId) && parsedId > 0) {
        this.editingRequestId = parsedId;
      }
    }

    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "system";
      });

    this.initWarehouseCache();

    this.loadVendors();

    this.dataSource.filterPredicate = (row, filter) => {
      const f = JSON.parse(filter) as {
        itemName: string;
        partNumber: string;
        location: string;
      };
      const match = (val: string, term: string): boolean =>
        !term || val.toLowerCase().includes(term.toLowerCase());
      return (
        match(row.itemName, f.itemName) &&
        match(row.partNumber, f.partNumber) &&
        match(row.location, f.location)
      );
    };
    if (this.editingRequestId) {
      this.loadExistingRequest(this.editingRequestId);
    }
  }

  ngAfterViewInit(): void {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
    this.updateVisiblePageRows();
  }

  ngAfterViewChecked(): void {
    if (
      !this.barcodesGenerated &&
      !this.isGeneratingBarcodes &&
      this.showPrintModal &&
      this.printLabels.length > 0
    ) {
      this.isGeneratingBarcodes = true;
      setTimeout(() => {
        generateLabelBarcodes(this.printLabels);
        this.barcodesGenerated = true;
        this.isGeneratingBarcodes = false;
      }, 0);
    }
  }

  goBack(): void {
    this.router.navigate(["/generate-tem-in"]);
  }

  onVendorSearch(value: string): void {
    const lower = (value ?? "").toLowerCase().trim();
    const filtered = lower
      ? this.vendorOptions.filter(
          (v) =>
            v.cardName.toLowerCase().includes(lower) ||
            v.cardCode.toLowerCase().includes(lower),
        )
      : this.vendorOptions;

    this.filteredVendorOptions = filtered.slice(0, this.vendorDisplayLimit);
  }

  onVendorSelected(vendor: SapOcrd): void {
    this.vendorName = vendor.cardName;
    this.vendorCode = vendor.cardCode;
  }

  onLocationSearch(keyword: string): void {
    this.receivingService.searchWarehouses(keyword ?? "").then((list) => {
      this.filteredLocationOptions = list;
      this.cdr.markForCheck();
    });
  }

  onParentLocationInput(
    row: ReceivingMaterialRow,
    value: string | WarehouseLocation,
  ): void {
    const previousLocation = this.normalizeLocationValue(row.location);
    row.location = this.normalizeLocationValue(value);
    this.syncParentLocationToLots(row, previousLocation);
  }

  onLocationSelected(row: ReceivingMaterialRow, loc: WarehouseLocation): void {
    const previousLocation = this.normalizeLocationValue(row.location);
    row.location = loc.locationName;
    this.syncParentLocationToLots(row, previousLocation);
  }

  onLotLocationInput(
    row: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    value: string | WarehouseLocation,
  ): void {
    lot.location = this.normalizeLocationValue(value);
    lot.locationOverridden =
      lot.location !== this.normalizeLocationValue(row.location);
  }

  onLotLocationSelected(
    row: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    loc: WarehouseLocation,
  ): void {
    lot.location = loc.locationName;
    lot.locationOverridden = lot.location !== row.location;
  }

  fetchPoInfo(): void {
    if (!this.poNumber.trim()) {
      this.showSnackbar("Vui lòng nhập mã PO.", "Đóng", 4000, "warning");
      return;
    }

    this.isFetchingPo = true;
    const po = this.poNumber.trim();

    this.receivingService.getSapPoInfo(po).subscribe({
      next: (res) => {
        this.isFetchingPo = false;
        if (!res?.poInfo || !res.poDetails?.length) {
          this.showSnackbar(
            `Không tìm thấy PO ${po} hoặc PO không có dòng chi tiết.`,
            "Đóng",
            6000,
            "error",
          );
          return;
        }

        this.mapSapPoInfoToTable(res);
        this.applyBulkFieldsToAllLots();

        this.applyPoToAllChildLots(po);
        this.resetAllReconcileStatus("pending");

        this.showSnackbar(
          `Đã tải ${res.poDetails.length} vật tư từ PO ${po}.`,
          "Đóng",
          4000,
          "success",
        );
        this.cdr.markForCheck();
      },
      error: () => {
        this.isFetchingPo = false;
        this.showSnackbar(
          "Lỗi khi truy vấn thông tin PO từ SAP.",
          "Đóng",
          6000,
          "error",
        );
      },
    });
  }

  onBulkSapCommit(event: Event): void {
    event.preventDefault();
    const code = this.bulkSapCodesInput.trim();
    if (!code) {
      return;
    }
    this.commitAndApplySingleSap(code);
  }

  hasAnyGeneratedTem(): boolean {
    return this.requestTemDetails.length > 0;
  }

  removeSapChip(code: string): void {
    const trimmed = code.trim();
    const parent = this.dataSource.data.find(
      (r) => r.sapCode.trim() === trimmed,
    );
    const lotCount = parent?.lots.length ?? 0;
    const msg = parent
      ? lotCount
        ? `Xóa mã SAP ${trimmed} và ${lotCount} lô trên bảng?`
        : `Xóa mã SAP ${trimmed} khỏi bảng vật tư?`
      : `Xóa mã SAP ${trimmed} khỏi danh sách?`;
    this.confirmAction(msg, "Xóa mã SAP").subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      if (parent) {
        this.removeParentRowFromTable(parent);
        return;
      }
      this.removeSapChipFromList(trimmed);
      this.cdr.markForCheck();
    });
  }

  onLotQuantityChange(
    row: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    raw: string | number | null,
  ): void {
    lot.quantity = this.sanitizeNonNegativeQuantity(raw);
    this.syncParentQuantityFromLots(row);
    this.markParentReconcilePending(row);
    this.cdr.markForCheck();
  }

  onLotTemQuantityChange(
    lot: ReceivingLotRow,
    raw: string | number | null,
  ): void {
    lot.temQuantity = this.sanitizeNonNegativeQuantity(raw);
    this.cdr.markForCheck();
  }

  applyBulkInfo(): void {
    this.addBulkSapCodeFromInput();

    if (!this.selectedSapCodes.length) {
      if (this.dataSource.data.length && this.hasBulkLotFieldValues()) {
        this.applyBulkFieldsToAllLots();
        this.refreshTableView();
        this.cdr.markForCheck();
        return;
      }
      this.showSnackbar(
        "Vui lòng nhập ít nhất một mã SAP.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }

    const tableSapSet = new Set(this.dataSource.data.map((r) => r.sapCode));
    const codesToCreate = this.selectedSapCodes.filter(
      (code) => !tableSapSet.has(code),
    );

    if (!codesToCreate.length) {
      if (this.dataSource.data.length && this.hasBulkLotFieldValues()) {
        this.applyBulkFieldsToAllLots();
        this.refreshTableView();
        this.cdr.markForCheck();
        return;
      }
      this.showSnackbar(
        "Các mã SAP đã có trên bảng, không tạo bản ghi mới.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }

    const allowedCodesToCreate = codesToCreate.filter((code) =>
      this.isSapCodeInCurrentPo(code),
    );
    const rejectedCodes = codesToCreate.filter(
      (code) => !this.isSapCodeInCurrentPo(code),
    );
    if (rejectedCodes.length) {
      this.notifySapCodesNotInPo(rejectedCodes);
    }

    if (allowedCodesToCreate.length) {
      this.appendValidatedSapRows(allowedCodesToCreate);
      return;
    }

    this.bulkSapCodesInput = "";
    this.refreshTableView();
    this.cdr.markForCheck();
  }

  parentHasTemLot(parent: ReceivingMaterialRow): boolean {
    return parent.lots.some((lot) => this.lotHasTem(lot));
  }

  canRemoveLot(parent: ReceivingMaterialRow, lot: ReceivingLotRow): boolean {
    return !this.lotHasTem(lot);
  }

  canRemoveParent(parent: ReceivingMaterialRow): boolean {
    return !this.parentHasTemLot(parent);
  }

  addLot(row: ReceivingMaterialRow): void {
    if (this.parentHasTemLot(row)) {
      this.showSnackbar(
        "Vật tư này đã có lô đã tạo tem — không thể thêm lô. Hãy thêm mã SAP mới trên bảng.",
        "Đóng",
        6000,
        "warning",
      );
      return;
    }
    const prevLot = row.lots.length > 0 ? row.lots[row.lots.length - 1] : null;
    const lot = this.buildDefaultLotRow(
      row,
      row.location,
      `${row.id}-${Date.now()}`,
      prevLot?.lotNumber ?? "",
    );
    if (prevLot) {
      lot.manufacturingDate = prevLot.manufacturingDate
        ? new Date(prevLot.manufacturingDate)
        : null;
      lot.expirationDate = prevLot.expirationDate
        ? new Date(prevLot.expirationDate)
        : null;
    }
    row.lots = [...row.lots, lot];
    row.lotNumber = `${row.lots.length} lô`;
    row.expanded = true;
    this.syncParentQuantityFromLots(row);
    this.markParentReconcilePending(row);
    this.cdr.markForCheck();
  }

  toggleExpand(row: ReceivingMaterialRow): void {
    row.expanded = !row.expanded;
    this.cdr.markForCheck();
  }

  removeLot(parent: ReceivingMaterialRow, lot: ReceivingLotRow): void {
    if (lot.productId) {
      this.queueProductDeletion(lot.productId);
    }
    parent.lots = parent.lots.filter((l) => l.rowKey !== lot.rowKey);
    parent.lotNumber = parent.lots.length ? `${parent.lots.length} lô` : "";
    this.syncParentQuantityFromLots(parent);
    this.markParentReconcilePending(parent);
    this.cdr.markForCheck();
  }

  applyTableFilter(): void {
    this.dataSource.filter = JSON.stringify({
      itemName: this.filterItemName,
      partNumber: this.filterPartNumber,
      location: this.filterLocation,
    });
    this.refreshTableView();
  }

  onSaveOrder(): void {
    this.persistOrderChanges({ silent: false });
  }

  onConfirmSave(): void {
    this.onSaveOrder();
  }

  /** PO hợp lệ: có giá trị và khác "-". */
  hasValidPoForTemGenerate(): boolean {
    return Boolean(this.getEffectivePoNumber());
  }

  onCreateCode(): void {
    if (this.isGenerating) {
      return;
    }

    if (!this.hasValidPoForTemGenerate()) {
      this.showSnackbar(
        "Chưa có mã PO hợp lệ. Vui lòng nhập PO trước khi tạo mã.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }

    if (!this.editingRequestId) {
      this.showSnackbar(
        "Vui lòng lưu đơn trước khi tạo mã.",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }

    if (this.isDisableGenerate) {
      this.showSnackbar("Tất cả lô đã có tem.", "Đóng", 3000, "warning");
      return;
    }

    this.ensureAllParentLocationsSyncedToLots();

    const validationError = this.validateBeforeGenerateCode();
    if (validationError) {
      this.showSnackbar(validationError, "Đóng", 4000, "warning");
      return;
    }

    void this.validateStorageUnitsForGenerate().then((storageError) => {
      if (storageError) {
        this.showSnackbar(storageError, "Đóng", 4000, "error");
        return;
      }

      const pendingLotCount = this.countLotsPendingTem();
      this.confirmAction(
        pendingLotCount === 1
          ? "Xác nhận tạo mã tem cho lô chưa có tem?"
          : `Xác nhận tạo mã tem cho ${pendingLotCount} lô chưa có tem?`,
      ).subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }

        this.isGenerating = true;
        let syncUpdates: Array<{ productId: number; item: ExcelImportData }>;
        try {
          syncUpdates = this.buildProductSyncUpdatesForPendingGenerate(
            this.buildProductsForSave(),
          );
        } catch (err) {
          const message =
            err instanceof Error ? err.message : "Dữ liệu không hợp lệ.";
          this.showSnackbar(message, "Đóng", 4000, "warning");
          this.isGenerating = false;
          return;
        }

        this.generateTemInService
          .syncSavedProductsForGenerate(this.editingRequestId!, syncUpdates)
          .pipe(
            switchMap(() =>
              this.generateTemInService.generateTemByRequest(
                this.editingRequestId!,
              ),
            ),
            take(1),
            catchError((err: Error) => {
              this.showSnackbar(
                err.message ?? "Có lỗi xảy ra khi lưu hoặc tạo tem!",
                "Đóng",
                3000,
                "error",
              );
              this.isGenerating = false;
              this.cdr.markForCheck();
              return of(null);
            }),
          )
          .subscribe((response) => {
            if (!response) {
              return;
            }
            const type = response.success ? "success" : "error";
            this.showSnackbar(response.message, "Đóng", 3000, type);
            this.isGenerating = false;
            if (response.success) {
              this.refreshTemDataAfterGenerate();
            }
            this.cdr.markForCheck();
          });
      });
    });
  }

  showSnackbar(
    message: string,
    action: string = "Đóng",
    duration: number = 3000,
    type: "success" | "error" | "warning" = "success",
  ): void {
    const panelClass = {
      success: "snackbar-success",
      error: "snackbar-error",
      warning: "snackbar-warning",
    }[type];

    this.snackBar.open(message, action, {
      duration,
      horizontalPosition: "center",
      verticalPosition: "bottom",
      panelClass: [panelClass],
    });
  }

  confirmAction(message: string, title = "Xác nhận"): Observable<boolean> {
    const dialogRef = this.dialog.open(GenerateTemInConfirmDialogComponent, {
      data: { message, title },
    });
    return dialogRef.afterClosed() as Observable<boolean>;
  }

  canSendSapLot(_parent: ReceivingMaterialRow, lot: ReceivingLotRow): boolean {
    if (this.isSendingSap) {
      return false;
    }
    if (lot.sapSent) {
      return false;
    }
    return this.lotHasValidPo(lot);
  }

  canSendSapParent(parent: ReceivingMaterialRow): boolean {
    return parent.lots.some((lot) => this.canSendSapLot(parent, lot));
  }

  canSendSapAll(): boolean {
    return this.dataSource.data.some((parent) =>
      parent.lots.some((lot) => this.canSendSapLot(parent, lot)),
    );
  }

  getSapLotButtonLabel(lot: ReceivingLotRow): string {
    return lot.sapSent ? "Đã gửi SAP" : "Gửi SAP";
  }

  onSendSap(row?: ReceivingMaterialRow): void {
    if (this.isSendingSap) {
      return;
    }
    const entries = row
      ? row.lots
          .filter((lot) => this.canSendSapLot(row, lot))
          .map((lot) => ({ parent: row, lot }))
      : this.dataSource.data.flatMap((parent) =>
          parent.lots
            .filter((lot) => this.canSendSapLot(parent, lot))
            .map((lot) => ({ parent, lot })),
        );
    if (!entries.length) {
      this.showSnackbar(
        "Không có lô nào đủ điều kiện gửi SAP (cần gán PO hợp lệ trên lô).",
        "Đóng",
        5000,
        "warning",
      );
      return;
    }
    const label = row ? row.sapCode : "tất cả";
    this.confirmAction(
      `Gửi ${entries.length} lô SAP (${label})?`,
      "Gửi SAP",
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.executeSendSap(entries);
    });
  }

  onSendSapLot(parent: ReceivingMaterialRow, lot: ReceivingLotRow): void {
    if (!this.canSendSapLot(parent, lot)) {
      this.showSnackbar(
        lot.sapSent
          ? "Lô này đã gửi SAP."
          : 'Lô chưa có PO hợp lệ (userData5 trống hoặc "-").',
        "Đóng",
        4000,
        "warning",
      );
      return;
    }
    const label = lot.lotNumber || parent.sapCode;
    this.confirmAction(`Gửi SAP lô ${label}?`, "Gửi SAP").subscribe(
      (confirmed) => {
        if (!confirmed) {
          return;
        }
        this.executeSendSap([{ parent, lot }]);
      },
    );
  }

  openPoReconcileModal(): void {
    if (this.withPoMode) {
      return;
    }
    const sapCodes = this.collectSapCodesFromTable();
    const lotCount = this.countChildLots();
    if (!sapCodes.length) {
      this.showSnackbar(
        "Chưa có vật tư trên bảng để đối chiếu PO.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }
    if (!lotCount) {
      this.showSnackbar(
        "Chưa có dòng lô con. Hãy apply hoặc thêm lô trước khi đối chiếu PO.",
        "Đóng",
        5000,
        "warning",
      );
      return;
    }
    this.reconcilePoInput = this.poNumber;
    this.poReconcilePassed = false;
    this.poReconcilePartialPassed = false;
    this.poReconcileResult = null;
    this.verifiedPoNumber = "";
    this.matchedSapCodesForApply = [];
    this.showPoReconcileModal = true;
    document.body.classList.add("modal-open");
    this.cdr.markForCheck();
  }

  closePoReconcileModal(): void {
    this.showPoReconcileModal = false;
    this.isReconcilingPo = false;
    this.poReconcilePassed = false;
    this.poReconcilePartialPassed = false;
    this.poReconcileResult = null;
    this.verifiedPoNumber = "";
    this.matchedSapCodesForApply = [];
    document.body.classList.remove("modal-open");
    this.cdr.markForCheck();
  }

  onReconcilePo(): void {
    const po = this.reconcilePoInput.trim();
    if (!po) {
      this.showSnackbar("Vui lòng nhập mã PO.", "Đóng", 4000, "warning");
      return;
    }

    const tableRows = this.collectRowsForReconcile();
    if (!tableRows.length) {
      this.showSnackbar(
        "Chưa có dòng vật tư trên bảng để đối chiếu.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }

    this.isReconcilingPo = true;
    this.poReconcilePassed = false;
    this.poReconcilePartialPassed = false;
    this.poReconcileResult = null;
    this.verifiedPoNumber = "";
    this.matchedSapCodesForApply = [];

    this.receivingService.reconcilePoWithSapInfo(po, tableRows).subscribe({
      next: (result) => {
        this.isReconcilingPo = false;
        this.poReconcileResult = result;
        this.poReconcilePassed = result.matched;
        this.poReconcilePartialPassed = Boolean(result.partialMatched);
        this.matchedSapCodesForApply = result.matchedSapCodes ?? [];
        if (this.matchedSapCodesForApply.length) {
          this.verifiedPoNumber = po;
        }
        this.applyReconcileStatusToTable(result);
        this.cdr.markForCheck();
      },
      error: () => {
        this.isReconcilingPo = false;
        this.poReconcileResult = {
          matched: false,
          message: "Lỗi khi đối chiếu PO. Vui lòng thử lại.",
          poNumber: po,
        };
        this.cdr.markForCheck();
      },
    });
  }

  onApplyReconciledPo(): void {
    if (!this.verifiedPoNumber.trim() || !this.matchedSapCodesForApply.length) {
      return;
    }
    const po = this.verifiedPoNumber.trim();
    const matchedSet = new Set(this.matchedSapCodesForApply);
    this.applyPoToMatchedChildLots(po, matchedSet);

    const appliedLots = this.dataSource.data
      .filter((r) => matchedSet.has(r.sapCode))
      .reduce((sum, r) => sum + r.lots.length, 0);

    this.poNumber = po;
    this.markReconcilePassedForSapCodes(matchedSet);
    this.closePoReconcileModal();

    this.persistOrderChanges({
      silent: true,
      onSuccess: () => {
        this.showSnackbar(
          `Đã áp dụng và lưu PO ${po} cho ${this.matchedSapCodesForApply.length} mã pass (${appliedLots} dòng lô).`,
          "Đóng",
          4000,
          "success",
        );
      },
      onValidationFail: (message) => {
        this.showSnackbar(
          `PO ${po} đã áp dụng trên bảng nhưng chưa lưu được: ${message}`,
          "Đóng",
          8000,
          "warning",
        );
      },
    });
  }

  canApplyReconciledPo(): boolean {
    return (
      !this.isReconcilingPo &&
      !this.isSavingOrder &&
      this.matchedSapCodesForApply.length > 0 &&
      Boolean(this.verifiedPoNumber.trim())
    );
  }

  getApplyPoButtonLabel(): string {
    if (this.poReconcilePassed) {
      return "Áp dụng vào vật tư";
    }
    if (this.poReconcilePartialPassed) {
      return `Áp dụng ${this.matchedSapCodesForApply.length} mã đã pass`;
    }
    return "Áp dụng vào vật tư";
  }

  collectSapCodesFromTable(): string[] {
    return [
      ...new Set(
        this.dataSource.data
          .map((r) => r.sapCode)
          .filter((code): code is string => Boolean(code)),
      ),
    ];
  }

  collectRowsForReconcile(): {
    sapCode: string;
    itemName: string;
    vendor: string;
    totalQuantity: number;
  }[] {
    return this.dataSource.data
      .filter((row) => row.sapCode.trim() && row.lots.length > 0)
      .map((row) => ({
        sapCode: row.sapCode.trim(),
        itemName: row.itemName.trim(),
        vendor: (row.lots[0]?.vendor || this.vendorCode).trim(),
        totalQuantity: row.lots.reduce(
          (sum, lot) => sum + (lot.quantity ?? 0),
          0,
        ),
      }));
  }

  onParentItemNameInput(row: ReceivingMaterialRow, value: string): void {
    row.itemName = value ?? "";
    this.cdr.markForCheck();
  }

  getReconcileStatusLabel(status: ReconcileStatus): string {
    if (status === "passed") {
      return "Đã pass";
    }
    if (status === "failed") {
      return "Chưa pass";
    }
    return "Chưa ĐC";
  }

  countChildLots(): number {
    return this.dataSource.data.reduce((sum, row) => sum + row.lots.length, 0);
  }

  onViewLotDetail(parent: ReceivingMaterialRow, lot: ReceivingLotRow): void {
    this.lotDetailView = this.buildLotDetailView(parent, lot);
    this.lotDetailTemPreview = this.getTemRowsForLot(parent, lot);
    this.showLotDetailModal = true;
    document.body.classList.add("modal-open");
    this.cdr.markForCheck();
  }

  closeLotDetailModal(): void {
    this.showLotDetailModal = false;
    this.lotDetailView = null;
    this.lotDetailTemPreview = [];
    document.body.classList.remove("modal-open");
    this.cdr.markForCheck();
  }

  onPrintLot(parent: ReceivingMaterialRow, lot: ReceivingLotRow): void {
    const labels = this.buildPrintLabelsFromLots(parent, [lot]);
    if (!labels.length) {
      this.showSnackbar(
        "Chưa có mã tem. Vui lòng tạo mã trước.",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }
    this.openPrintPreview(labels);
  }

  onPrintParent(parent: ReceivingMaterialRow): void {
    if (!parent.lots.length) {
      this.showSnackbar("Chưa có lô con để in.", "Đóng", 3000, "warning");
      return;
    }
    const labels = this.buildPrintLabelsFromLots(parent, parent.lots);
    if (!labels.length) {
      this.showSnackbar(
        "Chưa có mã tem. Vui lòng tạo mã trước.",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }
    this.openPrintPreview(labels);
  }

  onExportCsvLot(parent: ReceivingMaterialRow, lot: ReceivingLotRow): void {
    const rows = this.getTemRowsForLot(parent, lot);
    if (!rows.length) {
      this.showSnackbar(
        "Chưa có mã tem. Vui lòng tạo mã trước.",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }
    const lotLabel = lot.lotNumber || "lot";
    this.downloadCsv(rows, `tem_${parent.sapCode}_${lotLabel}.csv`);
  }

  onExportCsvParent(parent: ReceivingMaterialRow): void {
    if (!parent.lots.length) {
      this.showSnackbar("Chưa có lô con để xuất CSV.", "Đóng", 3000, "warning");
      return;
    }
    const rows = parent.lots.flatMap((lot) =>
      this.getTemRowsForLot(parent, lot),
    );
    if (!rows.length) {
      this.showSnackbar(
        "Chưa có mã tem. Vui lòng tạo mã trước.",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }
    this.downloadCsv(rows, `tem_${parent.sapCode}_all.csv`);
  }

  onRemoveParent(parent: ReceivingMaterialRow): void {
    const lotCount = parent.lots.length;
    const msg = lotCount
      ? `Xóa vật tư ${parent.sapCode} và ${lotCount} lô con?`
      : `Xóa vật tư ${parent.sapCode}?`;
    this.confirmAction(msg, "Xóa vật tư").subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.removeParentRowFromTable(parent);
    });
  }

  closePrintModal(): void {
    this.printLabels = [];
    this.showPrintModal = false;
    this.barcodesGenerated = false;
    this.isGeneratingBarcodes = false;
    setTimeout(() => {
      document.body.classList.remove("modal-open");
    }, 0);
    this.cdr.markForCheck();
  }

  printTable(): void {
    runLabelPrint("printPreviewModal", () => this.closePrintModal());
  }

  formatArrivalDate(dateStr: string): string {
    if (!dateStr) {
      return "";
    }
    const parts = dateStr.split("-");
    if (parts.length !== 3) {
      return dateStr;
    }
    return parts[0].slice(2) + parts[1] + parts[2];
  }

  trackByPrintLabelId(index: number, label: ReceivingPrintLabel): string {
    return `${label.id}-${index}`;
  }

  formatDisplayDate(date: Date | null): string {
    if (!date) {
      return "—";
    }
    const d = String(date.getDate()).padStart(2, "0");
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const y = date.getFullYear();
    return `${d}/${m}/${y}`;
  }

  trackByRowId(_index: number, row: ReceivingMaterialRow): number {
    return row.id;
  }

  trackByLotKey(_index: number, lot: ReceivingLotRow): string {
    return lot.rowKey;
  }

  getStt(index: number): number {
    const pageIndex = this.paginator?.pageIndex ?? this.pageIndex;
    const pageSize = this.paginator?.pageSize ?? this.pageSize;
    return pageIndex * pageSize + index + 1;
  }

  onPaginatorChange(): void {
    this.pageIndex = this.paginator?.pageIndex ?? 0;
    this.pageSize = this.paginator?.pageSize ?? this.pageSize;
    this.updateVisiblePageRows();
  }

  formatDateForInput(date: Date | null): string {
    if (!date) {
      return "";
    }
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  }

  onRowDateChange(
    row: ReceivingMaterialRow,
    field: "manufacturingDate" | "expirationDate",
    value: string,
  ): void {
    row[field] = value ? new Date(value) : null;
  }

  onLotDateChange(
    lot: ReceivingLotRow,
    field: "manufacturingDate" | "expirationDate",
    value: string,
  ): void {
    lot[field] = value ? new Date(value) : null;
  }

  private persistOrderChanges(
    options: {
      silent?: boolean;
      onSuccess?: () => void;
      onValidationFail?: (message: string) => void;
    } = {},
  ): void {
    const silent = options.silent ?? false;

    if (this.isSavingOrder) {
      return;
    }

    this.ensureAllParentLocationsSyncedToLots();

    const validationError = this.validateOrderBeforeSave();
    if (validationError) {
      if (options.onValidationFail) {
        options.onValidationFail(validationError);
      } else if (!silent) {
        this.showSnackbar(validationError, "Đóng", 8000, "warning");
      }
      return;
    }

    const products = this.buildProductsForSave();
    const poValue = this.resolveOrderPoValue();
    const vendor = this.vendorCode.trim();
    const vendorName = this.vendorName.trim();

    let syncRows: Array<{ productId?: number; item: ExcelImportData }>;
    try {
      syncRows = this.buildProductSyncRows();
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Dữ liệu lô không hợp lệ.";
      if (options.onValidationFail) {
        options.onValidationFail(message);
      } else if (!silent) {
        this.showSnackbar(message, "Đóng", 8000, "warning");
      }
      return;
    }

    this.isSavingOrder = true;

    if (this.editingRequestId) {
      this.generateTemInService
        .updateRequestProducts(this.editingRequestId, products, {
          rows: syncRows,
          deletedProductIds: this.pendingDeletedProductIds,
        })
        .subscribe({
          next: (savedProducts) => {
            this.isSavingOrder = false;
            this.pendingDeletedProductIds = [];
            if (!silent) {
              this.showSnackbar(
                `Đã cập nhật đơn #${this.editingRequestId}.`,
                "Đóng",
                4000,
                "success",
              );
            }
            this.applyRequestTableAfterSave(savedProducts as SavedProductRow[]);
            options.onSuccess?.();
          },
          error: (err: Error) => {
            this.isSavingOrder = false;
            const message = err.message?.trim() || "Lỗi khi cập nhật đơn.";
            this.showSnackbar(message, "Đóng", 8000, "error");
            this.cdr.markForCheck();
          },
        });
      return;
    }

    this.generateTemInService
      .createRequestAndProducts(
        vendor,
        vendorName,
        poValue,
        this.currentUser,
        products,
      )
      .subscribe({
        next: (result) => {
          this.isSavingOrder = false;
          const requestId = result.requestId;
          this.editingRequestId = requestId;
          if (silent) {
            this.loadExistingRequest(requestId);
            options.onSuccess?.();
            return;
          }
          this.showSnackbar(
            `Đã tạo đơn #${requestId} với ${products.length} dòng sản phẩm.`,
            "Đóng",
            4000,
            "success",
          );
          this.navigateAfterSave(requestId);
        },
        error: (err: Error) => {
          this.isSavingOrder = false;
          this.showSnackbar(
            err.message?.trim() || "Lỗi khi lưu đơn.",
            "Đóng",
            8000,
            "error",
          );
          this.cdr.markForCheck();
        },
      });
  }

  /** Sau lưu: without-po → tiếp tục nhập; with-po → trang detail. */
  private navigateAfterSave(requestId: number): void {
    const mode = this.route.snapshot.paramMap.get("mode");
    if (mode === "without-po" || !this.withPoMode) {
      this.router.navigate([
        "/generate-tem-in/receiving-supplies/without-po",
        requestId,
      ]);
      return;
    }
    this.router.navigate(["/generate-tem-in/detail", requestId]);
  }

  private refreshTableView(): void {
    this.totalItems = this.dataSource.filteredData.length
      ? this.dataSource.filteredData.length
      : this.dataSource.data.length;
    this.updateVisiblePageRows();
    this.cdr.markForCheck();
  }

  private updateVisiblePageRows(): void {
    const filtered = this.dataSource.filteredData.length
      ? this.dataSource.filteredData
      : this.dataSource.data;
    const pageIndex = this.paginator?.pageIndex ?? this.pageIndex;
    const pageSize = this.paginator?.pageSize ?? this.pageSize;
    const start = pageIndex * pageSize;
    this.visiblePageRows = filtered.slice(start, start + pageSize);
  }

  private hasBulkLotFieldValues(): boolean {
    if (this.bulkLotNumber.trim()) {
      return true;
    }
    return (
      this.bulkExpirationDate != null || this.bulkManufacturingDate != null
    );
  }

  private commitAndApplySingleSap(code: string): void {
    const trimmed = code.trim();
    if (!trimmed) {
      return;
    }

    if (this.withPoMode && !this.isSapCodeInCurrentPo(trimmed)) {
      this.notifySapCodeNotInPo(trimmed);
      this.bulkSapCodesInput = "";
      return;
    }

    if (this.isSapCodeOnTable(trimmed)) {
      this.showSnackbar(
        `Mã SAP ${trimmed} đã có trên bảng.`,
        "Đóng",
        3000,
        "warning",
      );
      this.bulkSapCodesInput = "";
      return;
    }

    this.receivingService
      .getSapOitmBySapCode(trimmed)
      .pipe(take(1))
      .subscribe((data) => {
        if (!data) {
          this.showSnackbar(
            `Mã SAP ${trimmed} không tồn tại trong hệ thống.`,
            "Đóng",
            5000,
            "warning",
          );
          this.bulkSapCodesInput = "";
          return;
        }
        this.appendSapRowToTable(trimmed, data);
      });
  }

  private appendValidatedSapRows(codes: string[]): void {
    if (!codes.length) {
      return;
    }

    forkJoin(
      codes.map((code) =>
        this.receivingService
          .getSapOitmBySapCode(code)
          .pipe(map((data) => ({ code, data }))),
      ),
    )
      .pipe(take(1))
      .subscribe((results) => {
        const missing = results.filter((r) => !r.data).map((r) => r.code);
        if (missing.length) {
          this.showSnackbar(
            `Mã SAP không tồn tại trong hệ thống: ${missing.join(", ")}.`,
            "Đóng",
            6000,
            "warning",
          );
        }

        const valid = results.filter(
          (r): r is { code: string; data: SapOitmDto } => Boolean(r.data),
        );
        valid.forEach(({ code, data }) => this.appendSapRowToTable(code, data));

        this.bulkSapCodesInput = "";
        this.refreshTableView();
        this.cdr.markForCheck();
      });
  }

  private appendSapRowToTable(trimmed: string, oitm: SapOitmDto): void {
    if (this.isSapCodeOnTable(trimmed)) {
      return;
    }

    if (!this.selectedSapCodes.includes(trimmed)) {
      this.selectedSapCodes = [...this.selectedSapCodes, trimmed];
    }

    const row = this.buildBulkParentRow(trimmed, this.getNextRowId());
    row.itemName = (oitm.itemName ?? "").trim() || row.itemName;
    row.partNumber = (oitm.uPartNumber ?? "").trim() || row.partNumber;
    this.dataSource.data = [...this.dataSource.data, row];
    this.bulkSapCodesInput = "";
    this.refreshTableView();
    this.cdr.markForCheck();
  }

  private isSapCodeOnTable(sapCode: string): boolean {
    const normalized = this.normalizeSapCode(sapCode);
    return this.dataSource.data.some(
      (r) => this.normalizeSapCode(r.sapCode) === normalized,
    );
  }

  private applyBulkFieldsToAllLots(): void {
    const lotNumber = this.bulkLotNumber.trim();
    const manufacturingDate = this.bulkManufacturingDate
      ? new Date(this.bulkManufacturingDate)
      : null;
    const expirationDate = this.bulkExpirationDate
      ? new Date(this.bulkExpirationDate)
      : manufacturingDate
        ? this.addYears(manufacturingDate, 2)
        : null;

    if (!lotNumber && !manufacturingDate && !expirationDate) {
      return;
    }

    this.dataSource.data = this.dataSource.data.map((parent) => {
      const lots = parent.lots.map((lot) => ({
        ...lot,
        lotNumber: lotNumber || lot.lotNumber,
        manufacturingDate: manufacturingDate ?? lot.manufacturingDate,
        expirationDate: expirationDate ?? lot.expirationDate,
        arrivalDate: lot.arrivalDate ?? new Date(),
      }));
      const row = { ...parent, lots };
      this.syncParentQuantityFromLots(row);
      row.lotNumber = lots.length ? `${lots.length} lô` : parent.lotNumber;
      return row;
    });
  }

  private addBulkSapCodeFromInput(): void {
    const code = this.bulkSapCodesInput.trim();
    if (!code) {
      return;
    }
    if (this.withPoMode && !this.isSapCodeInCurrentPo(code)) {
      this.notifySapCodeNotInPo(code);
      this.bulkSapCodesInput = "";
      return;
    }
    if (this.selectedSapCodes.includes(code)) {
      this.showSnackbar(
        `Mã SAP ${code} đã có trong danh sách.`,
        "Đóng",
        3000,
        "warning",
      );
    } else {
      this.selectedSapCodes = [...this.selectedSapCodes, code];
    }
    this.bulkSapCodesInput = "";
  }

  /** Đồng bộ chip SAP từ các dòng cha trên bảng. */
  private syncSelectedSapCodesFromTable(): void {
    this.selectedSapCodes = [
      ...new Set(
        this.dataSource.data
          .map((r) => r.sapCode.trim())
          .filter((code): code is string => Boolean(code)),
      ),
    ];
  }

  private removeSapChipFromList(code: string): void {
    const trimmed = code.trim();
    this.selectedSapCodes = this.selectedSapCodes.filter(
      (c) => c.trim() !== trimmed,
    );
  }

  private removeParentRowFromTable(parent: ReceivingMaterialRow): void {
    parent.lots.forEach((lot) => {
      if (lot.productId) {
        this.queueProductDeletion(lot.productId);
      }
    });
    this.dataSource.data = this.dataSource.data.filter(
      (r) => r.id !== parent.id,
    );
    this.removeSapChipFromList(parent.sapCode);
    this.refreshTableView();
    this.cdr.markForCheck();
  }

  private sanitizeNonNegativeQuantity(
    raw: string | number | null,
  ): number | null {
    if (raw === "" || raw === null || raw === undefined) {
      return null;
    }
    const value = Number(raw);
    if (Number.isNaN(value) || value < 0) {
      return 0;
    }
    return value;
  }

  private syncParentQuantityFromLots(row: ReceivingMaterialRow): void {
    row.quantity = row.lots.reduce((sum, lot) => sum + (lot.quantity ?? 0), 0);
  }

  private enrichSapOitmRows(rows: ReceivingMaterialRow[]): void {
    const codes = [
      ...new Set(
        rows
          .map((r) => r.sapCode)
          .filter((code): code is string => Boolean(code)),
      ),
    ];
    if (!codes.length) {
      return;
    }

    forkJoin(
      codes.map((code) =>
        this.receivingService.getSapOitmBySapCode(code).pipe(
          map(
            (data: SapOitmDto | null): SapOitmRowInfo => ({
              code,
              itemName: data?.itemName ?? "",
              partNumber: data?.uPartNumber ?? "",
            }),
          ),
        ),
      ),
    ).subscribe((results: SapOitmRowInfo[]) => {
      const infoMap = new Map(results.map((r) => [r.code, r]));
      const targetCodes = new Set(codes);
      this.dataSource.data = this.dataSource.data.map((row) => {
        if (!targetCodes.has(row.sapCode)) {
          return row;
        }
        const info = infoMap.get(row.sapCode);
        if (!info) {
          return row;
        }
        return {
          ...row,
          itemName: info.itemName || row.itemName,
          partNumber: info.partNumber || row.partNumber,
        };
      });
      this.refreshTableView();
    });
  }

  private getNextRowId(): number {
    const ids = this.dataSource.data.map((r) => r.id);
    return ids.length ? Math.max(...ids) + 1 : 1;
  }

  private applyPoToAllChildLots(poCode: string): void {
    this.applyPoToMatchedChildLots(
      poCode,
      new Set(this.dataSource.data.map((r) => r.sapCode)),
    );
  }

  private applyPoToMatchedChildLots(
    poCode: string,
    matchedSapCodes: Set<string>,
  ): void {
    this.dataSource.data = this.dataSource.data.map((row) => {
      if (!matchedSapCodes.has(row.sapCode)) {
        return row;
      }
      return {
        ...row,
        lots: row.lots.map((lot) => ({
          ...lot,
          userData5: poCode,
        })),
      };
    });
    this.refreshTableView();
  }

  private applyReconcileStatusToTable(result: PoReconcileResponse): void {
    const resultMap = new Map(
      (result.rowResults ?? []).map((r) => [r.sapCode, r.passed]),
    );
    this.dataSource.data = this.dataSource.data.map((row) => {
      const passed = resultMap.get(row.sapCode);
      if (passed === undefined) {
        return row;
      }
      return {
        ...row,
        reconcileStatus: passed ? "passed" : "failed",
      };
    });
    this.refreshTableView();
  }

  private resetAllReconcileStatus(status: ReconcileStatus): void {
    this.dataSource.data = this.dataSource.data.map((row) => ({
      ...row,
      reconcileStatus: status,
    }));
    this.refreshTableView();
  }

  private buildBulkParentRow(
    sapCode: string,
    id: number,
  ): ReceivingMaterialRow {
    const location = this.storageUnit || "";
    const row: ReceivingMaterialRow = {
      id,
      sapCode,
      itemName: "",
      partNumber: "",
      location,
      quantityByPo: 0,
      quantity: 0,
      lotNumber: "1 lô",
      temQuantity: 1,
      manufacturingDate: null,
      expirationDate: null,
      note: "",
      lots: [],
      expanded: true,
      reconcileStatus: "pending",
    };
    row.lots = [
      this.buildDefaultLotRow(
        row,
        location,
        `bulk-${id}-${Date.now()}`,
        this.bulkLotNumber,
      ),
    ];
    this.syncParentQuantityFromLots(row);
    return row;
  }

  private buildDefaultLotRow(
    parent: ReceivingMaterialRow,
    location: string,
    rowKey: string,
    lotNumber = "",
  ): ReceivingLotRow {
    const arrivalDate = new Date();
    const manufacturingDate = this.bulkManufacturingDate
      ? new Date(this.bulkManufacturingDate)
      : null;
    const expirationDate = this.bulkExpirationDate
      ? new Date(this.bulkExpirationDate)
      : manufacturingDate
        ? this.addYears(manufacturingDate, 2)
        : null;
    return {
      rowKey,
      lotLabel: "",
      location,
      locationOverridden: false,
      lotNumber,
      quantity: null,
      temQuantity: 1,
      manufacturingDate,
      expirationDate,
      arrivalDate,
      userData1: "NO",
      userData2: "NO",
      userData3: "NO",
      userData4: this.buildUserData4(parent.sapCode, arrivalDate),
      userData5: this.resolveLotPoForSave({
        userData5: this.poNumber,
      } as ReceivingLotRow),
      vendor: this.vendorCode,
      vendorName: this.vendorName,
      note: "",
    };
  }

  private buildUserData4(sapCode: string, arrivalDate: Date): string {
    return `${sapCode}-${this.formatDateDdMMyyyy(arrivalDate)}`;
  }

  private formatDateDdMMyyyy(date: Date): string {
    const d = String(date.getDate()).padStart(2, "0");
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const y = String(date.getFullYear());
    return `${d}${m}${y}`;
  }

  private getTemRowsForLot(
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
  ): ReceivingTemPreviewRow[] {
    if (!this.requestTemDetails.length) {
      return [];
    }

    if (lot.productId != null) {
      return this.requestTemDetails
        .filter((t) => Number(t.productOfRequestId) === Number(lot.productId))
        .map((t) => this.mapTemDetailToPreviewRow(t));
    }

    const sap = parent.sapCode.trim();
    const lotNumber = lot.lotNumber.trim();
    return this.requestTemDetails
      .filter(
        (t) => t.sapCode?.trim() === sap && (t.lot?.trim() ?? "") === lotNumber,
      )
      .map((t) => this.mapTemDetailToPreviewRow(t));
  }

  private mapTemDetailToPreviewRow(t: TemDetail): ReceivingTemPreviewRow {
    return {
      id: Number(t.id),
      reelId: t.reelId,
      sapCode: t.sapCode,
      productName: t.productName,
      partNumber: t.partNumber,
      lot: t.lot,
      initialQuantity: t.initialQuantity,
      vendor: t.vendor,
      userData1: t.userData1,
      userData2: t.userData2,
      userData3: t.userData3,
      userData4: t.userData4,
      userData5: t.userData5,
      storageUnit: t.storageUnit,
      expirationDate: this.parseApiDate(t.expirationDate),
      manufacturingDate: this.parseApiDate(t.manufacturingDate),
      arrivalDate: this.parseApiDate(t.arrivalDate),
      qrCode: t.qrCode,
    };
  }

  private addYears(date: Date, years: number): Date {
    const result = new Date(date);
    result.setFullYear(result.getFullYear() + years);
    return result;
  }

  private formatDateYyyyMmDd(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  }

  private openPrintPreview(labels: ReceivingPrintLabel[]): void {
    if (!labels.length) {
      this.showSnackbar("Không có tem để in.", "Đóng", 3000, "warning");
      return;
    }
    this.printLabels = labels;
    this.barcodesGenerated = false;
    this.isGeneratingBarcodes = false;
    this.showPrintModal = true;
    document.body.classList.add("modal-open");
    this.cdr.markForCheck();
  }

  private buildPrintLabelsFromLots(
    _parent: ReceivingMaterialRow,
    lots: ReceivingLotRow[],
  ): ReceivingPrintLabel[] {
    return lots.flatMap((lot) =>
      this.getTemRowsForLot(_parent, lot).map((row) => this.toPrintLabel(row)),
    );
  }

  private toPrintLabel(row: ReceivingTemPreviewRow): ReceivingPrintLabel {
    return {
      id: row.reelId,
      qrCode: row.qrCode,
      reelId: row.reelId,
      sapCode: row.sapCode,
      productName: row.productName,
      partNumber: row.partNumber,
      lot: row.lot,
      initialQuantity: row.initialQuantity,
      vendor: row.vendor,
      userData1: row.userData1,
      userData2: row.userData2,
      userData3: row.userData3,
      userData4: row.userData4,
      userData5: row.userData5,
      storageUnit: row.storageUnit,
      manufacturingDate: row.manufacturingDate
        ? this.formatDateYyyyMmDd(row.manufacturingDate)
        : "",
      expirationDate: row.expirationDate
        ? this.formatDateYyyyMmDd(row.expirationDate)
        : "",
      arrivalDate: row.arrivalDate
        ? this.formatDateYyyyMmDd(row.arrivalDate)
        : "",
    };
  }

  private downloadCsv(rows: ReceivingTemPreviewRow[], filename: string): void {
    const csvContent = this.generateCsvContent(rows);
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", filename);
    link.style.visibility = "hidden";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    this.showSnackbar(`Đã tải CSV: ${filename}`, "Đóng", 3000, "success");
  }

  private generateCsvContent(rows: ReceivingTemPreviewRow[]): string {
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
      "MSDLevel",
      "MSDInitialFloorTime",
      "MSDBagSealDate",
      "MarketUsage",
      "QuantityOverride",
      "ShelfTime",
      "SPMaterialName",
      "WarningLimit",
      "MaximumLimit",
      "Comments",
      "WarmupTime",
      "StorageUnit",
      "SubStorageUnit",
      "LocationOverride",
      "ExpirationDate",
      "ManufacturingDate",
      "Partclass",
      "SAPCode",
    ];

    const csvRows = rows.map((item) => [
      item.reelId,
      item.partNumber,
      item.vendor,
      item.lot,
      item.userData1,
      item.userData2,
      item.userData3,
      item.userData4,
      item.userData5,
      item.initialQuantity,
      "",
      "",
      "",
      "",
      "1",
      "",
      "",
      "",
      "",
      "",
      "",
      item.storageUnit,
      "",
      "",
      item.expirationDate
        ? this.formatDateYyyyMmDd(item.expirationDate).replace(/-/g, "")
        : "",
      item.manufacturingDate
        ? this.formatDateYyyyMmDd(item.manufacturingDate).replace(/-/g, "")
        : "",
      "",
      item.sapCode,
    ]);

    return (
      "\ufeff" +
      [headers, ...csvRows]
        .map((row) => row.map((cell) => cell ?? "").join(","))
        .join("\n")
    );
  }

  private buildLotDetailView(
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
  ): ReceivingLotDetailView {
    return {
      sapCode: parent.sapCode,
      productName: parent.itemName,
      partNumber: parent.partNumber,
      lot: lot.lotNumber,
      vendor: lot.vendor || this.vendorCode,
      vendorName: lot.vendorName || this.vendorName,
      temQuantity: lot.temQuantity,
      initialQuantity: lot.quantity,
      storageUnit: lot.location,
      userData1: lot.userData1,
      userData2: lot.userData2,
      userData3: lot.userData3,
      userData4: lot.userData4,
      userData5: lot.userData5,
      manufacturingDate: lot.manufacturingDate,
      expirationDate: lot.expirationDate,
      arrivalDate: lot.arrivalDate,
    };
  }

  private normalizeLocationValue(
    value: string | WarehouseLocation | null | undefined,
  ): string {
    if (value == null) {
      return "";
    }
    if (typeof value === "string") {
      return value;
    }
    return value.locationName ?? "";
  }

  /** PO hợp lệ trên lô: userData5 có giá trị và khác "-". */
  private lotHasValidPo(lot: ReceivingLotRow): boolean {
    const po = (lot.userData5 ?? "").trim();
    return Boolean(po) && po !== "-";
  }

  private getLotPoNumber(lot: ReceivingLotRow): string {
    return (lot.userData5 ?? "").trim();
  }

  private executeSendSap(
    entries: Array<{ parent: ReceivingMaterialRow; lot: ReceivingLotRow }>,
  ): void {
    const poNumbers = [
      ...new Set(entries.map(({ lot }) => this.getLotPoNumber(lot))),
    ];
    for (const { parent, lot } of entries) {
      const storageUnit = this.getEffectiveLotLocation(parent, lot);
      const sapUnitError = validateStorageUnitForSap(storageUnit);
      if (sapUnitError) {
        const label = `${parent.sapCode}${lot.lotNumber ? ` / lô ${lot.lotNumber}` : ""}`;
        this.showSnackbar(`${label}: ${sapUnitError}`, "Đóng", 8000, "error");
        return;
      }
    }
    this.isSendingSap = true;
    forkJoin(
      poNumbers.map((po) =>
        this.receivingService.getSapPoInfo(po).pipe(
          map((res) => ({
            po,
            docEntry: Number.parseInt(res?.poInfo?.oporDocEntry ?? "", 10),
          })),
        ),
      ),
    )
      .pipe(
        switchMap((poRows) => {
          const docEntryByPo = new Map(
            poRows.map((row) => [row.po, row.docEntry]),
          );
          for (const po of poNumbers) {
            const docEntry = docEntryByPo.get(po);
            if (!docEntry || Number.isNaN(docEntry)) {
              throw new Error(
                `Không lấy được DocEntry từ PO ${po}. Kiểm tra lại mã PO.`,
              );
            }
          }
          const opdn: GoodsReceiptPoLine[] = entries.flatMap(
            ({ parent, lot }) => {
              const po = this.getLotPoNumber(lot);
              const docEntry = docEntryByPo.get(po)!;
              return this.buildOpdnLines(parent, lot, docEntry);
            },
          );
          if (!opdn.length) {
            throw new Error("Không có dữ liệu tem để gửi SAP.");
          }
          return this.receivingService.postGoodsReceiptPo({ OPDN: opdn });
        }),
      )
      .subscribe({
        next: () => {
          this.isSendingSap = false;
          entries.forEach(({ lot }) => {
            lot.sapSent = true;
          });
          this.showSnackbar(
            `Đã gửi SAP thành công (${entries.length} lô).`,
            "Đóng",
            4000,
            "success",
          );
          this.cdr.markForCheck();
        },
        error: (err: unknown) => {
          this.isSendingSap = false;
          this.showSnackbar(
            resolveHttpErrorMessage(err, "Gửi SAP thất bại."),
            "Đóng",
            8000,
            "error",
          );
          this.cdr.markForCheck();
        },
      });
  }

  private buildOpdnLines(
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    docEntry: number,
  ): GoodsReceiptPoLine[] {
    const tems = this.getTemRowsForLot(parent, lot);
    const vendor = (lot.vendor || this.vendorCode).trim();
    const storageUnit = this.getEffectiveLotLocation(parent, lot);
    const quantity = Number(lot.quantity ?? tems[0]?.initialQuantity ?? 1);
    const partNumber = parent.partNumber.trim();

    return tems.map((tem) => ({
      Vendor: vendor,
      DocEntry: docEntry,
      ItemCode: parent.sapCode.trim(),
      Quantity: quantity,
      LotNum: lot.lotNumber.trim(),
      ReelID: tem.reelId,
      PartNumber: partNumber || tem.partNumber || "",
      ExpirationDate: this.formatSapDateTime(
        lot.expirationDate ?? tem.expirationDate,
      ),
      ManufacturingDate: this.formatSapDateTime(
        lot.manufacturingDate ?? tem.manufacturingDate,
      ),
      StorageUnit: storageUnit,
    }));
  }

  private formatSapDateTime(date: Date | null): string {
    if (!date) {
      return "0001-01-01T00:00:00";
    }
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}T00:00:00`;
  }

  private getEffectiveLotLocation(
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
  ): string {
    const lotLocation = this.normalizeLocationValue(lot.location).trim();
    if (lotLocation) {
      return lotLocation;
    }
    return this.normalizeLocationValue(parent.location).trim();
  }

  private ensureAllParentLocationsSyncedToLots(): void {
    this.dataSource.data.forEach((row) => {
      if (row.lots.length > 0) {
        this.syncParentLocationToLots(row);
      }
    });
  }

  private syncParentLocationToLots(
    row: ReceivingMaterialRow,
    previousParentLocation?: string,
  ): void {
    const parentLocation = this.normalizeLocationValue(row.location);
    const syncedLots = row.lots.map((lot) => {
      const inheritsFromParent =
        !lot.locationOverridden ||
        !lot.location?.trim() ||
        (previousParentLocation !== undefined &&
          lot.location === previousParentLocation);
      if (!inheritsFromParent) {
        return lot;
      }
      return {
        ...lot,
        location: parentLocation,
        locationOverridden: false,
      };
    });

    this.dataSource.data = this.dataSource.data.map((r) =>
      r.id === row.id
        ? { ...r, location: parentLocation, lots: syncedLots }
        : r,
    );
    row.lots = syncedLots;
    row.location = parentLocation;
    this.refreshTableView();
  }

  private initWarehouseCache(): void {
    this.receivingService.initWarehouses().then(() => {
      this.cdr.markForCheck();
    });
  }

  private loadVendors(): void {
    this.isLoadingVendors = true;
    this.receivingService.getSapOcrds().subscribe({
      next: (data) => {
        this.vendorOptions = data;
        this.filteredVendorOptions = data.slice(0, this.vendorDisplayLimit);
        this.isLoadingVendors = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoadingVendors = false;
        this.cdr.markForCheck();
      },
    });
  }

  private mapSapPoInfoToTable(res: SapPoInfoResponse): void {
    this.poSapCodes = (res.poDetails ?? [])
      .map((d) => (d.por1ItemCode ?? "").trim())
      .filter((code): code is string => Boolean(code));

    const poInfo = res.poInfo;
    if (poInfo?.oporCardCode) {
      this.vendorCode = poInfo.oporCardCode;
    }
    if (poInfo?.oporCardName) {
      this.vendorName = poInfo.oporCardName;
    }

    let nextId = this.getNextRowId();
    const rows: ReceivingMaterialRow[] = (res.poDetails ?? []).map((d) => {
      const sapCode = (d.por1ItemCode ?? "").trim();
      const itemName = (d.por1Dscription ?? "").trim();
      const qtyByPo = Number.parseFloat(d.por1Quantity ?? "0") || 0;
      const lineVendor = (d.por1LineVendor ?? this.vendorCode).trim();
      const id = nextId;
      nextId += 1;
      const location = this.storageUnit || "";

      const row: ReceivingMaterialRow = {
        id,
        sapCode,
        itemName,
        partNumber: "",
        location,
        quantityByPo: qtyByPo,
        quantity: 0,
        lotNumber: "1 lô",
        temQuantity: 1,
        manufacturingDate: null,
        expirationDate: null,
        note: "",
        lots: [],
        expanded: true,
        reconcileStatus: "pending",
      };

      const lot = this.buildDefaultLotRow(
        row,
        location,
        `po-${id}-${Date.now()}`,
        this.bulkLotNumber,
      );
      lot.vendor = lineVendor;
      lot.vendorName = this.vendorName;
      row.lots = [lot];
      this.syncParentQuantityFromLots(row);
      return row;
    });

    this.dataSource.data = rows.map((row) => ({
      ...row,
      reconcileStatus: "passed" as ReconcileStatus,
    }));
    this.enrichSapOitmRows(rows);
    this.applyTableFilter();
    this.syncSelectedSapCodesFromTable();
  }

  private resolveOrderPoValue(): string {
    const effective = this.getEffectivePoNumber();
    return effective || "-";
  }

  /** PO lưu xuống DB cho từng lô — bỏ qua placeholder "-". */
  private resolveLotPoForSave(lot: ReceivingLotRow): string {
    const lotPo = (lot.userData5 ?? "").trim();
    if (lotPo && lotPo !== "-") {
      return lotPo;
    }
    const effective = this.getEffectivePoNumber();
    return effective || "-";
  }

  private isValidPoCode(po: string | null | undefined): boolean {
    const normalized = (po ?? "").trim();
    return Boolean(normalized) && normalized !== "-";
  }

  private derivePoFromProductList(
    products: Array<{ userData5?: string | null }>,
  ): string {
    for (const product of products) {
      const po = (product.userData5 ?? "").trim();
      if (this.isValidPoCode(po)) {
        return po;
      }
    }
    return "";
  }

  private validateBeforeGenerateCode(): string | null {
    const saveError = this.validateOrderBeforeSave();
    if (saveError) {
      return saveError;
    }

    if (!this.countLotsPendingTem()) {
      return "Tất cả lô đã có tem.";
    }

    if (!this.hasValidPoForTemGenerate()) {
      return "Chưa có mã PO hợp lệ. Vui lòng nhập PO trước khi tạo mã.";
    }

    if (this.withPoMode) {
      const poError = this.validateWithPoOrderBeforeGenerate();
      if (poError) {
        return poError;
      }
    }

    const rows = this.dataSource.data.filter((r) => r.lots.length > 0);
    for (const parent of rows) {
      for (const lot of parent.lots) {
        if (this.lotHasTem(lot)) {
          continue;
        }
        const label = `${parent.sapCode}${lot.lotNumber ? ` / lô ${lot.lotNumber}` : ""}`;
        if (!parent.itemName.trim()) {
          return `${label}: thiếu tên sản phẩm.`;
        }
        const location = this.getEffectiveLotLocation(parent, lot);
        if (!location) {
          return `${label}: thiếu vị trí kho (StorageUnit).`;
        }
        if (!(lot.vendor || this.vendorCode).trim()) {
          return `${label}: thiếu mã nhà cung cấp.`;
        }
      }
    }

    return null;
  }

  private getEffectivePoNumber(): string {
    const fromField = this.poNumber.trim();
    if (fromField && fromField !== "-") {
      return fromField;
    }
    for (const parent of this.dataSource.data) {
      for (const lot of parent.lots) {
        const lotPo = (lot.userData5 ?? "").trim();
        if (lotPo && lotPo !== "-") {
          return lotPo;
        }
      }
    }
    return "";
  }

  /** Đơn có PO: chỉ kiểm tra SL không vượt PO (không bắt đối chiếu lại). */
  private validateWithPoOrderBeforeGenerate(): string | null {
    const rowsWithPending = this.dataSource.data.filter(
      (row) =>
        row.lots.length > 0 && row.lots.some((lot) => !this.lotHasTem(lot)),
    );

    for (const parent of rowsWithPending) {
      const totalQty = parent.lots.reduce(
        (sum, lot) => sum + (lot.quantity ?? 0),
        0,
      );
      if (parent.quantityByPo > 0 && totalQty > parent.quantityByPo) {
        return `Mã ${parent.sapCode}: tổng số lượng (${totalQty}) vượt quá SL PO (${parent.quantityByPo}). Vui lòng sửa lại.`;
      }
    }

    return null;
  }

  private async validateStorageUnitsForGenerate(): Promise<string | null> {
    const rows = this.dataSource.data.filter((r) => r.lots.length > 0);
    for (const parent of rows) {
      for (const lot of parent.lots) {
        if (this.lotHasTem(lot)) {
          continue;
        }
        const label = `${parent.sapCode}${lot.lotNumber ? ` / lô ${lot.lotNumber}` : ""}`;
        const location = this.getEffectiveLotLocation(parent, lot);
        if (!location) {
          return `${label}: thiếu vị trí kho (StorageUnit).`;
        }
        const results = await this.receivingService.searchWarehouses(location);
        const exists = results.some((r) => r.locationName === location);
        if (!exists) {
          return `${label}: StorageUnit không hợp lệ: ${location}`;
        }
        const sapUnitError = validateStorageUnitForSap(location);
        if (sapUnitError) {
          return `${label}: ${sapUnitError}`;
        }
      }
    }
    return null;
  }

  /** Cập nhật ngầm tem + productId sau khi tạo mã — không reload bảng. */
  private refreshTemDataAfterGenerate(): void {
    if (!this.editingRequestId) {
      return;
    }
    forkJoin({
      products: this.generateTemInService
        .getProductsByRequestId(this.editingRequestId)
        .pipe(take(1)),
      tems: this.generateTemInService
        .getTemDetailsByRequestId(this.editingRequestId)
        .pipe(take(1)),
    }).subscribe({
      next: ({ products, tems }) => {
        this.applyTemAndProductSync(products as SavedProductRow[], tems);
      },
    });
  }

  private applyTemAndProductSync(
    products: SavedProductRow[],
    tems: TemDetail[],
  ): void {
    this.requestTemDetails = tems;
    this.updateGenerateButtonState();
    this.syncProductIdsFromSavedProducts(products);
    this.refreshLotDetailTemPreviewIfOpen();
    this.cdr.markForCheck();
  }

  private lotHasTem(lot: ReceivingLotRow): boolean {
    if (lot.productId == null) {
      return false;
    }
    return this.requestTemDetails.some(
      (t) => Number(t.productOfRequestId) === Number(lot.productId),
    );
  }

  private countLotsPendingTem(): number {
    return this.dataSource.data.reduce(
      (count, parent) =>
        count + parent.lots.filter((lot) => !this.lotHasTem(lot)).length,
      0,
    );
  }

  private updateGenerateButtonState(): void {
    const hasLots = this.dataSource.data.some((row) => row.lots.length > 0);
    if (!hasLots) {
      this.isDisableGenerate = true;
      return;
    }
    this.isDisableGenerate = this.countLotsPendingTem() === 0;
  }

  private markParentReconcilePending(parent: ReceivingMaterialRow): void {
    if (!this.withPoMode || parent.reconcileStatus === "pending") {
      return;
    }
    parent.reconcileStatus = "pending";
  }

  private refreshLotDetailTemPreviewIfOpen(): void {
    if (!this.showLotDetailModal || !this.lotDetailView) {
      return;
    }
    const view = this.lotDetailView;
    const parent = this.dataSource.data.find(
      (p) => p.sapCode.trim() === view.sapCode.trim(),
    );
    if (!parent) {
      return;
    }
    const lot = parent.lots.find(
      (l) => (l.lotNumber?.trim() ?? "") === (view.lot?.trim() ?? ""),
    );
    if (lot) {
      this.lotDetailTemPreview = this.getTemRowsForLot(parent, lot);
    }
  }

  /**
   * Gán productId cho từng lô sau khi lưu/tạo tem.
   * Không dùng sapCode|lot làm khóa duy nhất — nhiều lô tách cùng số lô phải khớp theo SL/số tem hoặc thứ tự.
   */
  private syncProductIdsFromSavedProducts(products: SavedProductRow[]): void {
    this.dataSource.data = this.dataSource.data.map((parent) => {
      const sapCode = parent.sapCode.trim();
      const unmatched = products.filter(
        (p) => (p.sapCode?.trim() ?? "") === sapCode,
      );

      const lots = parent.lots.map((lot) => {
        const takeProduct = (index: number): ReceivingLotRow => {
          const picked = unmatched.splice(index, 1)[0];
          const id = Number(picked?.id);
          if (!picked || Number.isNaN(id) || id <= 0) {
            return lot;
          }
          return lot.productId === id ? lot : { ...lot, productId: id };
        };

        if (lot.productId) {
          const byId = unmatched.findIndex(
            (p) => Number(p.id) === Number(lot.productId),
          );
          if (byId >= 0) {
            return takeProduct(byId);
          }
        }

        const byQty = unmatched.findIndex(
          (p) =>
            (p.lot?.trim() ?? "") === lot.lotNumber.trim() &&
            Number(p.initialQuantity ?? 0) === Number(lot.quantity ?? 0) &&
            Number(p.temQuantity ?? 0) === Number(lot.temQuantity ?? 0),
        );
        if (byQty >= 0) {
          return takeProduct(byQty);
        }

        const byLot = unmatched.findIndex(
          (p) => (p.lot?.trim() ?? "") === lot.lotNumber.trim(),
        );
        if (byLot >= 0) {
          return takeProduct(byLot);
        }

        if (unmatched.length) {
          return takeProduct(0);
        }

        return lot;
      });

      return { ...parent, lots };
    });
    this.refreshTableView();
  }

  private validateOrderBeforeSave(): string | null {
    if (!this.vendorCode.trim()) {
      return "Vui lòng nhập mã nhà cung cấp.";
    }
    if (!this.vendorName.trim()) {
      return "Vui lòng nhập tên nhà cung cấp.";
    }

    const rows = this.dataSource.data.filter((r) => r.lots.length > 0);
    if (!rows.length && !this.pendingDeletedProductIds.length) {
      return "Chưa có dòng lô để lưu đơn.";
    }

    for (const parent of rows) {
      for (const lot of parent.lots) {
        const label = `${parent.sapCode}${lot.lotNumber ? ` / lô ${lot.lotNumber}` : ""}`;
        if (!parent.sapCode.trim()) {
          return "Thiếu mã SAP.";
        }
        if (!parent.partNumber.trim()) {
          return `${label}: thiếu Part Number.`;
        }
        if (!lot.lotNumber.trim()) {
          return `${label}: thiếu số lô.`;
        }
        if (!lot.temQuantity || lot.temQuantity <= 0) {
          return `${label}: số tem phải > 0.`;
        }
        if (lot.quantity == null || lot.quantity <= 0) {
          return `${label}: số lượng phải > 0.`;
        }
        if (!lot.manufacturingDate) {
          return `${label}: thiếu ngày sản xuất.`;
        }
        if (!lot.expirationDate) {
          return `${label}: thiếu hạn sử dụng.`;
        }
        if (!lot.arrivalDate) {
          return `${label}: thiếu ngày về.`;
        }
        if (!this.getEffectiveLotLocation(parent, lot)) {
          return `${label}: thiếu vị trí kho (StorageUnit).`;
        }
      }
    }

    return null;
  }

  private queueProductDeletion(productId: number): void {
    if (!this.pendingDeletedProductIds.includes(productId)) {
      this.pendingDeletedProductIds.push(productId);
    }
  }

  private buildProductSyncRows(): Array<{
    productId?: number;
    item: ExcelImportData;
  }> {
    const products = this.buildProductsForSave();
    let index = 0;
    const rows: Array<{ productId?: number; item: ExcelImportData }> = [];
    const usedProductIds = new Set<number>();
    for (const parent of this.dataSource.data) {
      for (const lot of parent.lots) {
        if (!products[index]) {
          throw new Error("Dữ liệu sản phẩm không khớp với bảng lô.");
        }
        if (lot.productId) {
          if (usedProductIds.has(lot.productId)) {
            throw new Error(
              `Trùng ID sản phẩm #${lot.productId} trên nhiều lô (${parent.sapCode}). Vui lòng tải lại đơn.`,
            );
          }
          usedProductIds.add(lot.productId);
        }
        rows.push({
          productId: lot.productId,
          item: products[index],
        });
        index += 1;
      }
    }
    return rows;
  }

  private buildProductSyncUpdates(
    products: ExcelImportData[],
  ): Array<{ productId: number; item: ExcelImportData }> {
    const updates: Array<{ productId: number; item: ExcelImportData }> = [];
    let index = 0;
    for (const parent of this.dataSource.data) {
      for (const lot of parent.lots) {
        if (!lot.productId) {
          throw new Error(
            "Vui lòng lưu đơn trước khi tạo mã (thiếu ID sản phẩm).",
          );
        }
        if (!products[index]) {
          throw new Error("Dữ liệu sản phẩm không khớp với bảng lô.");
        }
        updates.push({
          productId: lot.productId,
          item: products[index],
        });
        index += 1;
      }
    }
    return updates;
  }

  /** Chỉ cập nhật các lô chưa có tem trước khi gọi generateTem. */
  private buildProductSyncUpdatesForPendingGenerate(
    products: ExcelImportData[],
  ): Array<{ productId: number; item: ExcelImportData }> {
    const updates: Array<{ productId: number; item: ExcelImportData }> = [];
    let index = 0;
    for (const parent of this.dataSource.data) {
      for (const lot of parent.lots) {
        if (!products[index]) {
          throw new Error("Dữ liệu sản phẩm không khớp với bảng lô.");
        }
        if (!this.lotHasTem(lot)) {
          if (!lot.productId) {
            throw new Error(
              "Vui lòng lưu đơn trước khi tạo mã (thiếu ID sản phẩm).",
            );
          }
          updates.push({
            productId: lot.productId,
            item: products[index],
          });
        }
        index += 1;
      }
    }
    if (!updates.length) {
      throw new Error("Không có lô mới cần tạo tem.");
    }
    return updates;
  }

  private buildProductsForSave(): ExcelImportData[] {
    return this.dataSource.data.flatMap((parent) =>
      parent.lots.map((lot) => ({
        sapCode: parent.sapCode.trim(),
        tenSP: parent.itemName.trim(),
        partNumber: parent.partNumber.trim(),
        lot: lot.lotNumber.trim(),
        temQuantity: lot.temQuantity ?? 1,
        initialQuantity: lot.quantity ?? 0,
        vendor: (lot.vendor || this.vendorCode).trim(),
        tenNCC: (lot.vendorName || this.vendorName).trim(),
        userData1: lot.userData1 ?? "",
        userData2: lot.userData2 ?? "",
        userData3: lot.userData3 ?? "",
        userData4: lot.userData4 ?? "",
        userData5: this.resolveLotPoForSave(lot),
        storageUnit: this.getEffectiveLotLocation(parent, lot),
        expirationDate: this.formatDateYyyyMmDd(lot.expirationDate!),
        manufacturingDate: this.formatDateYyyyMmDd(lot.manufacturingDate!),
        arrivalDate: this.formatDateYyyyMmDd(lot.arrivalDate!),
      })),
    );
  }

  private applyRequestTableAfterSave(savedProducts: SavedProductRow[]): void {
    const sapSentByProductId = this.captureLotSapSentState();
    const reconcileBySapCode = this.captureReconcileStatusState();
    const poByProductId = this.captureLotPoState();
    this.mapSavedProductsToTable(savedProducts);
    this.restoreLotSapSentState(sapSentByProductId);
    this.restoreReconcileStatusState(reconcileBySapCode);
    this.restoreLotPoState(poByProductId);
    this.syncPoNumberFromTable();

    if (!this.editingRequestId) {
      this.cdr.markForCheck();
      return;
    }

    this.generateTemInService
      .getTemDetailsByRequestId(this.editingRequestId)
      .pipe(take(1))
      .subscribe({
        next: (tems) => {
          this.applyTemAndProductSync(savedProducts, tems);
          this.cdr.markForCheck();
        },
        error: () => {
          this.cdr.markForCheck();
        },
      });
  }

  private refreshPoSapCodesFromApi(po: string): void {
    this.receivingService.getSapPoInfo(po).subscribe({
      next: (res) => {
        this.poSapCodes = (res?.poDetails ?? [])
          .map((d) => (d.por1ItemCode ?? "").trim())
          .filter((code): code is string => Boolean(code));
        this.cdr.markForCheck();
      },
    });
  }

  private isSapCodeInCurrentPo(sapCode: string): boolean {
    if (!this.withPoMode) {
      return true;
    }
    if (!this.poSapCodes.length) {
      return false;
    }
    const normalized = this.normalizeSapCode(sapCode);
    return this.poSapCodes.some(
      (code) => this.normalizeSapCode(code) === normalized,
    );
  }

  private notifySapCodeNotInPo(sapCode: string): void {
    const po = this.poNumber.trim();
    if (!this.poSapCodes.length) {
      this.showSnackbar(
        po
          ? `Chưa tải danh sách mã SAP từ PO ${po}. Vui lòng lấy thông tin PO trước.`
          : "Vui lòng nhập và lấy thông tin PO trước khi thêm mã SAP.",
        "Đóng",
        5000,
        "warning",
      );
      return;
    }
    this.showSnackbar(
      `Mã SAP ${sapCode} không có trong PO ${po}. Không thể thêm vào bảng.`,
      "Đóng",
      5000,
      "warning",
    );
  }

  private notifySapCodesNotInPo(sapCodes: string[]): void {
    const po = this.poNumber.trim();
    const list = sapCodes.join(", ");
    this.showSnackbar(
      `Mã SAP không có trong PO ${po}: ${list}.`,
      "Đóng",
      6000,
      "warning",
    );
  }

  private normalizeSapCode(code: string): string {
    const trimmed = code.trim();
    const stripped = trimmed.replace(/^0+/, "");
    return stripped || "0";
  }

  private markReconcilePassedForSapCodes(sapCodes: Set<string>): void {
    if (!sapCodes.size) {
      return;
    }
    this.dataSource.data = this.dataSource.data.map((row) =>
      sapCodes.has(row.sapCode)
        ? { ...row, reconcileStatus: "passed" as ReconcileStatus }
        : row,
    );
    this.refreshTableView();
  }

  private captureReconcileStatusState(): Map<string, ReconcileStatus> {
    const reconcileMap = new Map<string, ReconcileStatus>();
    this.dataSource.data.forEach((row) => {
      if (row.sapCode?.trim()) {
        reconcileMap.set(row.sapCode.trim(), row.reconcileStatus);
      }
    });
    return reconcileMap;
  }

  private restoreReconcileStatusState(
    reconcileBySapCode: Map<string, ReconcileStatus>,
  ): void {
    if (!reconcileBySapCode.size) {
      return;
    }
    this.dataSource.data = this.dataSource.data.map((row) => {
      const status = reconcileBySapCode.get(row.sapCode.trim());
      return status ? { ...row, reconcileStatus: status } : row;
    });
    this.refreshTableView();
  }

  private captureLotSapSentState(): Map<number, boolean> {
    const sapSentMap = new Map<number, boolean>();
    this.dataSource.data.forEach((parent) =>
      parent.lots.forEach((lot) => {
        if (lot.productId && lot.sapSent) {
          sapSentMap.set(lot.productId, true);
        }
      }),
    );
    return sapSentMap;
  }

  private restoreLotSapSentState(
    sapSentByProductId: Map<number, boolean>,
  ): void {
    if (!sapSentByProductId.size) {
      return;
    }
    this.dataSource.data = this.dataSource.data.map((parent) => ({
      ...parent,
      lots: parent.lots.map((lot) =>
        lot.productId && sapSentByProductId.has(lot.productId)
          ? { ...lot, sapSent: true }
          : lot,
      ),
    }));
    this.refreshTableView();
  }

  private captureLotPoState(): Map<number, string> {
    const poMap = new Map<number, string>();
    this.dataSource.data.forEach((parent) =>
      parent.lots.forEach((lot) => {
        if (!lot.productId) {
          return;
        }
        const po = (lot.userData5 ?? "").trim();
        if (this.isValidPoCode(po)) {
          poMap.set(lot.productId, po);
        }
      }),
    );
    return poMap;
  }

  private restoreLotPoState(poByProductId: Map<number, string>): void {
    if (!poByProductId.size) {
      return;
    }
    this.dataSource.data = this.dataSource.data.map((parent) => ({
      ...parent,
      lots: parent.lots.map((lot) => {
        if (!lot.productId) {
          return lot;
        }
        const savedPo = poByProductId.get(lot.productId);
        if (!savedPo) {
          return lot;
        }
        const currentPo = (lot.userData5 ?? "").trim();
        if (!this.isValidPoCode(currentPo)) {
          return { ...lot, userData5: savedPo };
        }
        return lot;
      }),
    }));
    this.refreshTableView();
  }

  private syncPoNumberFromTable(): void {
    const effective = this.getEffectivePoNumber();
    if (effective) {
      this.poNumber = effective;
    }
  }

  private loadExistingRequest(requestId: number): void {
    this.isLoading = true;
    forkJoin({
      requests: this.generateTemInService.getRequestList().pipe(take(1)),
      products: this.generateTemInService.getProductsByRequestId(requestId),
      tems: this.generateTemInService
        .getTemDetailsByRequestId(requestId)
        .pipe(take(1)),
    }).subscribe({
      next: ({ requests, products, tems }) => {
        this.isLoading = false;
        const request = requests.find((r) => r.id === requestId);
        if (request) {
          this.vendorCode = request.vendor ?? "";
          this.vendorName = request.vendorName ?? "";
          const po = (request.userData5 ?? "").trim();
          if (po && po !== "-") {
            this.poNumber = po;
            this.withPoMode = true;
          } else {
            this.poNumber = "";
            this.withPoMode = false;
          }
        } else if (products.length) {
          const first = products[0] as SavedProductRow;
          this.vendorCode = first.vendor ?? "";
          this.vendorName = first.vendorName ?? this.vendorName;
        }
        this.mapSavedProductsToTable(products as SavedProductRow[]);
        this.syncPoNumberFromTable();
        this.applyTemAndProductSync(products as SavedProductRow[], tems);
        const poForSap = this.getEffectivePoNumber();
        if (poForSap) {
          this.refreshPoSapCodesFromApi(poForSap);
        }
      },
      error: () => {
        this.isLoading = false;
        this.showSnackbar(
          `Không tải được đơn #${requestId}.`,
          "Đóng",
          6000,
          "error",
        );
        this.cdr.markForCheck();
      },
    });
  }

  private mapSavedProductsToTable(products: SavedProductRow[]): void {
    const grouped = new Map<string, SavedProductRow[]>();
    products.forEach((p) => {
      const key = p.sapCode?.trim() ?? "";
      if (!key) {
        return;
      }
      const list = grouped.get(key) ?? [];
      list.push(p);
      grouped.set(key, list);
    });

    let nextId = 1;
    const rows: ReceivingMaterialRow[] = [];
    const fallbackPo =
      this.getEffectivePoNumber() ||
      this.derivePoFromProductList(products) ||
      "";
    grouped.forEach((items, sapCode) => {
      const first = items[0];
      const parentLocation = first.storageUnit ?? this.storageUnit;
      const row: ReceivingMaterialRow = {
        id: nextId,
        sapCode,
        itemName: first.productName ?? "",
        partNumber: first.partNumber ?? "",
        location: parentLocation,
        quantityByPo: 0,
        quantity: 0,
        lotNumber: `${items.length} lô`,
        temQuantity: first.temQuantity ?? 1,
        manufacturingDate: null,
        expirationDate: null,
        note: "",
        lots: items.map((p, idx) => {
          const lotLocation = p.storageUnit ?? "";
          return {
            rowKey: `saved-${p.id}-${idx}`,
            productId: p.id,
            lotLabel: "",
            location: lotLocation,
            locationOverridden: lotLocation !== parentLocation,
            lotNumber: p.lot ?? "",
            quantity: p.initialQuantity ?? null,
            temQuantity: p.temQuantity ?? 1,
            manufacturingDate: this.parseApiDate(p.manufacturingDate),
            expirationDate: this.parseApiDate(p.expirationDate),
            arrivalDate: this.parseApiDate(p.arrivalDate),
            userData1: p.userData1 ?? "",
            userData2: p.userData2 ?? "",
            userData3: p.userData3 ?? "",
            userData4: p.userData4 ?? "",
            userData5: this.isValidPoCode(p.userData5)
              ? (p.userData5 ?? "").trim()
              : fallbackPo || (p.userData5 ?? "").trim() || "-",
            vendor: p.vendor ?? this.vendorCode,
            vendorName: p.vendorName ?? this.vendorName,
            note: "",
          };
        }),
        expanded: true,
        reconcileStatus: "pending",
      };
      nextId += 1;
      this.syncParentQuantityFromLots(row);
      rows.push(row);
    });

    this.dataSource.data = rows;
    this.applyTableFilter();
    this.syncSelectedSapCodesFromTable();
  }

  private parseApiDate(value: string | null | undefined): Date | null {
    if (!value?.trim()) {
      return null;
    }
    const normalized = value.trim().slice(0, 10);
    const parsed = new Date(normalized);
    return Number.isNaN(parsed.getTime()) ? null : parsed;
  }

  private enrichItemNames(rows: ReceivingMaterialRow[]): void {
    this.enrichSapOitmRows(rows);
  }
}
