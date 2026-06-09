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
import { AlertService } from "app/core/util/alert.service";
import { ExcelImportData } from "../models/list-product-of-request.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { GenerateTemInConfirmDialogComponent } from "../generate-tem-in-modal-confirm/modal-confirm.component";
import { TemDetail } from "../detail/generate-tem-in-detail.component";
import {
  PoReconcileResponse,
  ReceivingSuppliesService,
  SapOcrd,
  SapOitmDto,
  SapPoInfoResponse,
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
  isDisableGenerate = false;
  withPoMode = false;
  editingRequestId: number | null = null;
  requestTemDetails: TemDetail[] = [];

  poNumber = "";
  vendorCode = "";
  vendorName = "";
  storageUnit = "";

  bulkLotNumber = "";
  bulkArrivalDate: Date | null = null;
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
    "qrCode",
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
    private alertService: AlertService,
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
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng nhập mã PO.",
      });
      return;
    }

    this.isFetchingPo = true;
    const po = this.poNumber.trim();

    this.receivingService.getSapPoInfo(po).subscribe({
      next: (res) => {
        this.isFetchingPo = false;
        if (!res?.poInfo || !res.poDetails?.length) {
          this.alertService.addAlert({
            type: "danger",
            message: `Không tìm thấy PO ${po} hoặc PO không có dòng chi tiết.`,
          });
          return;
        }

        this.mapSapPoInfoToTable(res);

        this.applyPoToAllChildLots(po);
        this.resetAllReconcileStatus("pending");

        this.alertService.addAlert({
          type: "success",
          message: `Đã tải ${res.poDetails.length} vật tư từ PO ${po}.`,
        });
        this.cdr.markForCheck();
      },
      error: () => {
        this.isFetchingPo = false;
        this.alertService.addAlert({
          type: "danger",
          message: "Lỗi khi truy vấn thông tin PO từ SAP.",
        });
      },
    });
  }

  onBulkSapCommit(event: Event): void {
    event.preventDefault();
    this.addBulkSapCodeFromInput();
  }

  removeSapChip(code: string): void {
    this.selectedSapCodes = this.selectedSapCodes.filter((c) => c !== code);
  }

  onLotQuantityChange(
    row: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    raw: string | number | null,
  ): void {
    lot.quantity = this.sanitizeNonNegativeQuantity(raw);
    this.syncParentQuantityFromLots(row);
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
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng nhập ít nhất một mã SAP.",
      });
      return;
    }

    const tableSapSet = new Set(this.dataSource.data.map((r) => r.sapCode));
    const codesToCreate = this.selectedSapCodes.filter(
      (code) => !tableSapSet.has(code),
    );

    if (!codesToCreate.length) {
      this.alertService.addAlert({
        type: "info",
        message: "Các mã SAP đã có trên bảng, không tạo bản ghi mới.",
      });
      return;
    }

    if (codesToCreate.length) {
      let nextId = this.getNextRowId();
      const newRows = codesToCreate.map((sapCode) => {
        const row = this.buildBulkParentRow(sapCode, nextId);
        nextId += 1;
        return row;
      });
      this.dataSource.data = [...this.dataSource.data, ...newRows];
      this.enrichSapOitmRows(newRows);
    }

    this.clearBulkFieldsExceptSap();
    this.refreshTableView();
    this.cdr.markForCheck();
  }

  addLot(row: ReceivingMaterialRow): void {
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
    if (this.isSavingOrder) {
      return;
    }

    this.ensureAllParentLocationsSyncedToLots();

    const validationError = this.validateOrderBeforeSave();
    if (validationError) {
      this.alertService.addAlert({
        type: "warning",
        message: validationError,
      });
      return;
    }

    const products = this.buildProductsForSave();
    const poValue = this.resolveOrderPoValue();
    const vendor = this.vendorCode.trim();
    const vendorName = this.vendorName.trim();

    this.isSavingOrder = true;

    if (this.editingRequestId) {
      this.generateTemInService
        .updateRequestProducts(this.editingRequestId, products, {
          rows: this.buildProductSyncRows(),
          deletedProductIds: this.pendingDeletedProductIds,
        })
        .subscribe({
          next: () => {
            this.isSavingOrder = false;
            this.pendingDeletedProductIds = [];
            this.alertService.addAlert({
              type: "success",
              message: `Đã cập nhật đơn #${this.editingRequestId}.`,
            });
            if (!this.withPoMode) {
              this.loadExistingRequest(this.editingRequestId!);
              return;
            }
            this.navigateAfterSave(this.editingRequestId!);
          },
          error: (err: Error) => {
            this.isSavingOrder = false;
            this.alertService.addAlert({
              type: "danger",
              message: err.message ?? "Lỗi khi cập nhật đơn.",
            });
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
          this.alertService.addAlert({
            type: "success",
            message: `Đã tạo đơn #${requestId} với ${products.length} dòng sản phẩm.`,
          });
          this.editingRequestId = requestId;
          this.navigateAfterSave(requestId);
        },
        error: (err: Error) => {
          this.isSavingOrder = false;
          this.alertService.addAlert({
            type: "danger",
            message: err.message ?? "Lỗi khi lưu đơn.",
          });
          this.cdr.markForCheck();
        },
      });
  }

  onConfirmSave(): void {
    this.onSaveOrder();
  }

  onCreateCode(): void {
    if (this.isGenerating) {
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
      this.showSnackbar("Đơn đã tạo mã tem.", "Đóng", 3000, "warning");
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

      this.confirmAction(
        "Xác nhận tạo mã tem cho tất cả sản phẩm thuộc yêu cầu này?",
      ).subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }

        this.isGenerating = true;
        let syncUpdates: Array<{ productId: number; item: ExcelImportData }>;
        try {
          syncUpdates = this.buildProductSyncUpdates(
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
              this.isDisableGenerate = true;
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

  confirmAction(message: string): Observable<boolean> {
    const dialogRef = this.dialog.open(GenerateTemInConfirmDialogComponent, {
      data: { message },
    });
    return dialogRef.afterClosed() as Observable<boolean>;
  }

  onSendSap(row?: ReceivingMaterialRow): void {
    const label = row ? row.sapCode : "tất cả";
    this.alertService.addAlert({
      type: "info",
      message: `Chức năng gửi SAP (${label}) đang được phát triển.`,
    });
  }

  onSendSapLot(parent: ReceivingMaterialRow, lot: ReceivingLotRow): void {
    const label = lot.lotNumber ?? lot.lotLabel ?? parent.sapCode;
    this.alertService.addAlert({
      type: "info",
      message: `Chức năng gửi SAP lô (${label}) đang được phát triển.`,
    });
  }

  openPoReconcileModal(): void {
    const sapCodes = this.collectSapCodesFromTable();
    const lotCount = this.countChildLots();
    if (!sapCodes.length) {
      this.alertService.addAlert({
        type: "warning",
        message: "Chưa có vật tư trên bảng để đối chiếu PO.",
      });
      return;
    }
    if (!lotCount) {
      this.alertService.addAlert({
        type: "warning",
        message:
          "Chưa có dòng lô con. Hãy apply hoặc thêm lô trước khi đối chiếu PO.",
      });
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
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng nhập mã PO.",
      });
      return;
    }

    const tableRows = this.collectRowsForReconcile();
    if (!tableRows.length) {
      this.alertService.addAlert({
        type: "warning",
        message: "Chưa có dòng vật tư trên bảng để đối chiếu.",
      });
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
    this.alertService.addAlert({
      type: "success",
      message: `Đã áp dụng PO ${po} cho ${this.matchedSapCodesForApply.length} mã pass (${appliedLots} dòng lô).`,
    });
    this.closePoReconcileModal();
  }

  canApplyReconciledPo(): boolean {
    return (
      !this.isReconcilingPo &&
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
    this.lotDetailTemPreview = this.getTemRowsForLot(lot);
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
    const rows = this.getTemRowsForLot(lot);
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
    const rows = parent.lots.flatMap((lot) => this.getTemRowsForLot(lot));
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
    if (!window.confirm(msg)) {
      return;
    }
    parent.lots.forEach((lot) => {
      if (lot.productId) {
        this.queueProductDeletion(lot.productId);
      }
    });
    this.dataSource.data = this.dataSource.data.filter(
      (r) => r.id !== parent.id,
    );
    this.refreshTableView();
    this.cdr.markForCheck();
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

  private clearBulkFieldsExceptSap(): void {
    this.bulkLotNumber = "";
    this.bulkArrivalDate = null;
    this.bulkManufacturingDate = null;
  }

  private addBulkSapCodeFromInput(): void {
    const code = this.bulkSapCodesInput.trim();
    if (!code) {
      return;
    }
    if (this.selectedSapCodes.includes(code)) {
      this.alertService.addAlert({
        type: "info",
        message: `Mã SAP ${code} đã có trong danh sách.`,
      });
    } else {
      this.selectedSapCodes = [...this.selectedSapCodes, code];
    }
    this.bulkSapCodesInput = "";
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
    const arrivalDate = this.bulkArrivalDate
      ? new Date(this.bulkArrivalDate)
      : new Date();
    const manufacturingDate = this.bulkManufacturingDate
      ? new Date(this.bulkManufacturingDate)
      : null;
    const expirationDate = manufacturingDate
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
      userData5: this.poNumber.trim(),
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

  private getTemRowsForLot(lot: ReceivingLotRow): ReceivingTemPreviewRow[] {
    if (!lot.productId) {
      return [];
    }
    return this.requestTemDetails
      .filter((t) => t.productOfRequestId === lot.productId)
      .map((t) => this.mapTemDetailToPreviewRow(t));
  }

  private mapTemDetailToPreviewRow(t: TemDetail): ReceivingTemPreviewRow {
    return {
      id: t.id,
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
      this.alertService.addAlert({
        type: "warning",
        message: "Không có tem để in.",
      });
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
      this.getTemRowsForLot(lot).map((row) => this.toPrintLabel(row)),
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
    this.alertService.addAlert({
      type: "success",
      message: `Đã tải CSV: ${filename}`,
    });
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

  /** Vị trí lô con — ưu tiên lô, fallback dòng cha. */
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

    this.dataSource.data = rows;
    this.enrichSapOitmRows(rows);
    this.applyTableFilter();
  }

  private resolveOrderPoValue(): string {
    const po = this.poNumber.trim();
    if (this.withPoMode && po) {
      return po;
    }
    return "-";
  }

  private validateBeforeGenerateCode(): string | null {
    const saveError = this.validateOrderBeforeSave();
    if (saveError) {
      return saveError;
    }

    const rows = this.dataSource.data.filter((r) => r.lots.length > 0);
    for (const parent of rows) {
      for (const lot of parent.lots) {
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

  private async validateStorageUnitsForGenerate(): Promise<string | null> {
    const rows = this.dataSource.data.filter((r) => r.lots.length > 0);
    for (const parent of rows) {
      for (const lot of parent.lots) {
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
        this.requestTemDetails = tems;
        this.isDisableGenerate = tems.length > 0;
        this.syncProductIdsFromSavedProducts(products as SavedProductRow[]);
        this.cdr.markForCheck();
      },
    });
  }

  private syncProductIdsFromSavedProducts(products: SavedProductRow[]): void {
    const productMap = new Map<string, number>();
    products.forEach((p) => {
      const key = `${p.sapCode?.trim() ?? ""}|${p.lot?.trim() ?? ""}`;
      productMap.set(key, p.id);
    });

    this.dataSource.data = this.dataSource.data.map((parent) => ({
      ...parent,
      lots: parent.lots.map((lot) => {
        const key = `${parent.sapCode.trim()}|${lot.lotNumber.trim()}`;
        const productId = productMap.get(key);
        return productId != null ? { ...lot, productId } : lot;
      }),
    }));
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
    if (!rows.length) {
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
    for (const parent of this.dataSource.data) {
      for (const lot of parent.lots) {
        if (!products[index]) {
          throw new Error("Dữ liệu sản phẩm không khớp với bảng lô.");
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

  private buildProductsForSave(): ExcelImportData[] {
    const poValue = this.resolveOrderPoValue();
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
        userData5: lot.userData5 || poValue,
        storageUnit: this.getEffectiveLotLocation(parent, lot),
        expirationDate: this.formatDateYyyyMmDd(lot.expirationDate!),
        manufacturingDate: this.formatDateYyyyMmDd(lot.manufacturingDate!),
        arrivalDate: this.formatDateYyyyMmDd(lot.arrivalDate!),
      })),
    );
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
        this.requestTemDetails = tems;
        this.isDisableGenerate = tems.length > 0;
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
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.alertService.addAlert({
          type: "danger",
          message: `Không tải được đơn #${requestId}.`,
        });
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
            userData5: p.userData5 ?? "",
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
    const codes = [...new Set(rows.map((r) => r.sapCode).filter(Boolean))];
    if (!codes.length) {
      return;
    }

    forkJoin(
      codes.map((code) =>
        this.receivingService
          .getItemName(code)
          .pipe(map((name) => ({ code, name }))),
      ),
    ).subscribe((results) => {
      const nameMap = new Map(results.map((r) => [r.code, r.name]));
      this.dataSource.data = this.dataSource.data.map((row) => ({
        ...row,
        itemName: nameMap.get(row.sapCode) ?? row.itemName,
      }));
      this.refreshTableView();
    });
  }
}
