import {
  Component,
  OnInit,
  AfterViewInit,
  AfterViewChecked,
  ViewChild,
  ViewChildren,
  QueryList,
  HostListener,
  ChangeDetectorRef,
} from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { HttpClient } from "@angular/common/http";
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
import { MatAutocompleteTrigger } from "@angular/material/autocomplete";
import * as XLSX from "xlsx";
import { AccountService } from "app/core/auth/account.service";
import { ExcelImportData } from "../models/list-product-of-request.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { ListRequestCreateTem } from "../models/list-request-create-tem.model";
import { GenerateTemInConfirmDialogComponent } from "../generate-tem-in-modal-confirm/modal-confirm.component";
import { TemDetail } from "../detail/generate-tem-in-detail.component";
import {
  GoodsReceiptPoLine,
  PoReconcileResponse,
  ProductInPoStatusCreatePayload,
  ProductInPoStatusDto,
  ReceivingSuppliesService,
  ReceivingProductRow,
  resolveHttpErrorMessage,
  SapOcrd,
  SapOitmDto,
  SapOwhsDto,
  SapPoInfoDetail,
  SapPoInfoResponse,
  validateStorageUnitForSap,
  WarehouseLocation,
} from "../service/receiving-supplies.service";

interface SavedProductRow {
  id: number;
  requestCreateTemId?: number;
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
  numberOfPrints?: number;
  UploadPanacim?: boolean;
  WhsCode?: string | null;
  sapSendStatus?: boolean | null;
}
import {
  generateLabelBarcodes,
  runLabelPrint,
} from "./receiving-label-print.util";
import {
  applyImportedPartNumbers,
  PartNumberPlImportRow,
  PartNumberReconcileResult,
  PartNumberImportParseMeta,
  parsePartNumberImportFile,
  reconcilePartNumbersFromPl,
  TablePartRow,
} from "./part-number-pl-import.util";
import {
  generateTemPanacimCsvBlob,
  generateTemReelDataWorkbook,
  TemPanacimExportInput,
  TemReelDataExportInput,
} from "./tem-panacim-export.util";

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
  /** Mã kho SAP (gửi OPDN StorageUnit). */
  sapWarehouse: string;
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
  /** Đã gửi SAP (lưu DB — sapSendStatus). */
  sapSendStatus?: boolean;
  /** Đã gửi PanaCIM. */
  uploadPanacim?: boolean;
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
  /** Mã kho SAP (gửi OPDN StorageUnit). */
  sapWarehouse: string;
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

export type ExpirationExtendUnit = "month" | "year";

/** Dòng vật tư trong modal chọn PO — chỉ hiển thị trường dễ đọc cho người dùng. */
export interface PoMaterialSelectRow {
  rowKey: string;
  selected: boolean;
  sapCode: string;
  itemName: string;
  quantity: number;
  unit: string;
  warehouse: string;
  alreadyOnTable: boolean;
  /** Đã có trong product-in-po-status và đã có kho — checked sẵn, khóa dòng. */
  alreadyInPoStatus: boolean;
  detail: SapPoInfoDetail;
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
  isSendingPanacim = false;
  isDisableGenerate = false;
  editingRequestId: number | null = null;
  requestTemDetails: TemDetail[] = [];

  poNumber = "";
  /** Mã SAP thuộc PO hiện tại (từ sap-po-info). */
  poSapCodes: string[] = [];
  /** SL theo PO theo mã SAP — từ por1Quantity. */
  poQuantityBySapCode: Record<string, number> = {};
  vendorCode = "";
  vendorName = "";
  storageUnit = "";
  /** Mã kho SAP header — áp dụng toàn bảng. */
  sapWarehouseCode = "";
  sapWarehouseList: SapOwhsDto[] = [];
  filteredSapWarehouseList: SapOwhsDto[] = [];
  isLoadingSapWarehouses = false;
  /** Khóa PO + vendor khi mở đơn đã có PO. */
  poAndVendorLocked = false;

  bulkLotNumber = "";
  bulkExpirationDate: Date | null = null;
  bulkManufacturingDate: Date | null = null;
  bulkArrivalDate: Date | null = new Date();
  bulkExpirationExtendUnit: ExpirationExtendUnit = "month";
  bulkExpirationExtendValue = 3;
  readonly expirationExtendMonthOptions = [
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
  ];
  readonly expirationExtendYearOptions = [1, 2, 3, 4, 5];
  autoGenerateLot = false;
  bulkSapCodesInput = "";
  selectedSapCodes: string[] = [];

  filterItemName = "";
  filterPartNumber = "";
  filterSapCode = "";
  /** Vị trí header — chọn để áp dụng toàn bảng. */
  headerLocation = "";

  filteredLocationOptions: WarehouseLocation[] = [];
  /** Sau khi search xong — dùng hiển thị dòng "Không có kết quả" trong dropdown. */
  lastLocationSearchTerm = "";
  locationSearchSettled = false;
  locationSearchPending = false;
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

  showPoSelectModal = false;
  pendingPoResponse: SapPoInfoResponse | null = null;
  poSelectRows: PoMaterialSelectRow[] = [];
  poSelectAllChecked = false;
  /** Mã kho SAP header trong modal — áp dụng tất cả dòng. */
  poSelectHeaderWarehouse = "";
  poSelectFilterSapCode = "";
  poSelectFilterItemName = "";
  /** Dòng đang focus ô kho SAP trong modal (autocomplete dùng chung). */
  poSelectActiveWarehouseRow: PoMaterialSelectRow | null = null;

  showPartImportModal = false;
  partImportFile: File | null = null;
  partImportFileName = "";
  isParsingPartImport = false;
  partImportRows: PartNumberPlImportRow[] = [];
  partImportResult: PartNumberReconcileResult | null = null;
  partImportMeta: PartNumberImportParseMeta | null = null;

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
  @ViewChildren(MatAutocompleteTrigger)
  private poSelectWhsTriggers!: QueryList<MatAutocompleteTrigger>;
  @ViewChildren(MatAutocompleteTrigger)
  private locationAutocompleteTriggers!: QueryList<MatAutocompleteTrigger>;

  private locationSearchSeq = 0;
  private locationSearchTimer: ReturnType<typeof setTimeout> | null = null;

  private readonly vendorDisplayLimit = 50;
  /** ID sản phẩm chờ xóa khi lưu — chỉ khi user bấm xóa lô/vật tư. */
  private pendingDeletedProductIds: number[] = [];
  /** Vật tư đã chọn trong modal PO — gửi createProductInPoStatus khi lưu đơn. */
  private pendingProductInPoStatus: ProductInPoStatusCreatePayload[] = [];
  private savedProductsById = new Map<number, SavedProductRow>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private receivingService: ReceivingSuppliesService,
    private generateTemInService: GenerateTemInService,
    private accountService: AccountService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private http: HttpClient,
  ) {}

  ngOnInit(): void {
    this.bulkExpirationDate = this.computeBulkExpirationDate();

    let requestIdParam =
      this.route.snapshot.paramMap.get("requestId") ??
      this.route.snapshot.paramMap.get("id");
    if (!requestIdParam) {
      const mode = this.route.snapshot.paramMap.get("mode");
      if (mode && /^\d+$/.test(mode)) {
        requestIdParam = mode;
      }
    }
    if (!requestIdParam) {
      const legacyMode = this.route.snapshot.paramMap.get("mode");
      const legacyId = this.route.snapshot.paramMap.get("requestId");
      if (
        legacyId &&
        /^\d+$/.test(legacyId) &&
        (legacyMode === "with-po" || legacyMode === "without-po")
      ) {
        requestIdParam = legacyId;
      }
    }
    if (requestIdParam && /^\d+$/.test(requestIdParam)) {
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
    this.loadSapWarehouses();

    this.dataSource.filterPredicate = (row, filter) => {
      const f = JSON.parse(filter) as {
        sapCode: string;
        itemName: string;
        partNumber: string;
      };
      const match = (val: string, term: string): boolean =>
        !term || val.toLowerCase().includes(term.toLowerCase());
      return (
        match(row.sapCode, f.sapCode) &&
        match(row.itemName, f.itemName) &&
        match(row.partNumber, f.partNumber)
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
    if (this.locationSearchTimer) {
      clearTimeout(this.locationSearchTimer);
    }

    const term = (keyword ?? "").trim();
    if (!term) {
      this.locationSearchSeq += 1;
      this.filteredLocationOptions = [];
      this.lastLocationSearchTerm = "";
      this.locationSearchSettled = false;
      this.locationSearchPending = false;
      this.cdr.markForCheck();
      return;
    }

    this.locationSearchPending = true;
    this.locationSearchSettled = false;

    this.locationSearchTimer = setTimeout(() => {
      const seq = ++this.locationSearchSeq;
      void this.receivingService
        .searchWarehouses(term)
        .then((list) => {
          if (seq !== this.locationSearchSeq) {
            return;
          }
          this.filteredLocationOptions = list;
          this.lastLocationSearchTerm = term;
          this.locationSearchPending = false;
          this.locationSearchSettled = true;
          this.cdr.markForCheck();
          this.openActiveLocationAutocompletePanel();
        })
        .catch(() => {
          if (seq !== this.locationSearchSeq) {
            return;
          }
          this.filteredLocationOptions = [];
          this.lastLocationSearchTerm = term;
          this.locationSearchPending = false;
          this.locationSearchSettled = true;
          this.cdr.markForCheck();
          this.openActiveLocationAutocompletePanel();
        });
    }, 200);
  }

  showLocationNoResults(): boolean {
    return (
      this.locationSearchSettled &&
      !this.locationSearchPending &&
      Boolean(this.lastLocationSearchTerm) &&
      this.filteredLocationOptions.length === 0
    );
  }

  onParentLocationInput(
    row: ReceivingMaterialRow,
    value: string | WarehouseLocation,
  ): void {
    row.location = this.normalizeLocationValue(value);
  }

  onLocationSelected(
    row: ReceivingMaterialRow,
    selected: string | WarehouseLocation,
  ): void {
    const loc = this.resolveWarehouseLocation(selected);
    const previousLocation = this.normalizeLocationValue(row.location);
    row.location = loc?.locationName ?? this.normalizeLocationValue(selected);
    this.syncParentLocationToLots(row, previousLocation);
  }

  onParentLocationBlur(row: ReceivingMaterialRow): void {
    this.syncParentLocationToLots(row);
  }

  onLotLocationInput(
    row: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    value: string | WarehouseLocation,
  ): void {
    if (!this.isLotEditable(lot)) {
      return;
    }
    lot.location = this.normalizeLocationValue(value);
    lot.locationOverridden =
      lot.location !== this.normalizeLocationValue(row.location);
  }

  onLotLocationSelected(
    row: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    selected: string | WarehouseLocation,
  ): void {
    if (!this.isLotEditable(lot)) {
      return;
    }
    const loc = this.resolveWarehouseLocation(selected);
    lot.location = loc?.locationName ?? this.normalizeLocationValue(selected);
    lot.locationOverridden = lot.location !== row.location;
  }

  fetchPoInfo(): void {
    if (!this.canFetchPoInfo()) {
      this.showSnackbar("Vui lòng nhập mã PO.", "Đóng", 4000, "warning");
      return;
    }

    this.isFetchingPo = true;
    const po = this.poNumber.trim();

    forkJoin({
      poRes: this.receivingService.getSapPoInfo(po),
      poStatus: this.receivingService
        .getProductInPoStatusByUserData5(po)
        .pipe(catchError(() => of([] as ProductInPoStatusDto[]))),
    }).subscribe({
      next: ({ poRes, poStatus }) => {
        this.isFetchingPo = false;
        if (!poRes?.poInfo || !poRes.poDetails?.length) {
          this.showSnackbar(
            `Không tìm thấy PO ${po} hoặc PO không có dòng chi tiết.`,
            "Đóng",
            6000,
            "error",
          );
          return;
        }

        this.applyPoResponseMetadata(poRes);
        this.openPoSelectModal(poRes, poStatus);
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

  openPoSelectModal(
    res: SapPoInfoResponse,
    poStatusRecords: ProductInPoStatusDto[] = [],
  ): void {
    this.pendingPoResponse = res;
    this.poSelectFilterSapCode = "";
    this.poSelectFilterItemName = "";
    this.syncPoSelectHeaderWarehouseFromForm(poStatusRecords);
    this.loadSapWarehouses(
      this.poSelectHeaderWarehouse || this.sapWarehouseCode,
    );
    this.poSelectRows = this.buildPoSelectRows(res, poStatusRecords);
    const selectable = this.getPoSelectSelectableRows();
    this.poSelectAllChecked =
      selectable.length > 0 && selectable.every((r) => r.selected);
    this.showPoSelectModal = true;
    document.body.classList.add("modal-open");
    this.cdr.markForCheck();
  }

  closePoSelectModal(): void {
    this.showPoSelectModal = false;
    this.pendingPoResponse = null;
    this.poSelectRows = [];
    this.poSelectAllChecked = false;
    this.poSelectFilterSapCode = "";
    this.poSelectFilterItemName = "";
    this.poSelectActiveWarehouseRow = null;
    this.closePoSelectWarehousePanels();
    document.body.classList.remove("modal-open");
    this.cdr.markForCheck();
  }

  getPoSelectVisibleRows(): PoMaterialSelectRow[] {
    const sap = this.poSelectFilterSapCode.trim().toLowerCase();
    const name = this.poSelectFilterItemName.trim().toLowerCase();
    return this.poSelectRows.filter((row) => {
      if (sap && !row.sapCode.toLowerCase().includes(sap)) {
        return false;
      }
      if (name && !row.itemName.toLowerCase().includes(name)) {
        return false;
      }
      return true;
    });
  }

  getPoSelectVisibleSelectableRows(): PoMaterialSelectRow[] {
    return this.getPoSelectVisibleRows().filter(
      (r) => !r.alreadyOnTable && !r.alreadyInPoStatus,
    );
  }

  getPoSelectVisibleSelectedCount(): number {
    const visibleKeys = new Set(
      this.getPoSelectVisibleRows().map((r) => r.rowKey),
    );
    return this.poSelectRows.filter(
      (r) =>
        r.selected &&
        !r.alreadyOnTable &&
        !r.alreadyInPoStatus &&
        visibleKeys.has(r.rowKey),
    ).length;
  }

  getPoSelectAllCheckboxSelectableCount(): number {
    const hasFilter =
      Boolean(this.poSelectFilterSapCode.trim()) ||
      Boolean(this.poSelectFilterItemName.trim());
    return hasFilter
      ? this.getPoSelectVisibleSelectableRows().length
      : this.getPoSelectSelectableRows().length;
  }

  onPoSelectFilterChange(): void {
    this.syncPoSelectAllChecked();
    this.cdr.markForCheck();
  }

  onPoSelectRowWarehouseFocus(row: PoMaterialSelectRow): void {
    this.poSelectActiveWarehouseRow = row;
    if (!this.sapWarehouseList.length && !this.isLoadingSapWarehouses) {
      this.loadSapWarehouses(row.warehouse);
      return;
    }
    this.onSapWarehouseSearch(row.warehouse);
  }

  onPoSelectRowWarehouseSelectedFromPanel(code: string): void {
    if (!this.poSelectActiveWarehouseRow) {
      return;
    }
    this.onPoSelectRowWarehouseSelected(this.poSelectActiveWarehouseRow, code);
  }

  @HostListener("document:mousedown", ["$event"])
  onDocumentMouseDownForPoSelectWhs(event: MouseEvent): void {
    if (!this.showPoSelectModal) {
      return;
    }
    const target = event.target as HTMLElement | null;
    if (!target) {
      this.closePoSelectWarehousePanels();
      return;
    }
    if (target.closest(".po-select-whs-input")) {
      return;
    }
    if (target.closest(".mat-mdc-autocomplete-panel")) {
      return;
    }
    this.closePoSelectWarehousePanels();
  }

  onPoSelectHeaderWarehouseSearch(keyword: string): void {
    if (!this.sapWarehouseList.length && !this.isLoadingSapWarehouses) {
      this.loadSapWarehouses(keyword);
      return;
    }
    this.onSapWarehouseSearch(keyword);
  }

  onPoSelectHeaderWarehouseChange(value: string): void {
    const code = this.normalizeWarehouseCode(value);
    this.poSelectHeaderWarehouse = code || value.trim();
    if (code) {
      this.sapWarehouseCode = code;
    }
    this.cdr.markForCheck();
  }

  onPoSelectHeaderWarehouseSelected(code: string): void {
    this.poSelectHeaderWarehouse = code;
    this.sapWarehouseCode = code;
    this.cdr.markForCheck();
  }

  onPoSelectRowWarehouseChange(row: PoMaterialSelectRow, value: string): void {
    if (row.alreadyInPoStatus) {
      return;
    }
    const code = this.normalizeWarehouseCode(value);
    row.warehouse = code || value.trim();
    this.cdr.markForCheck();
  }

  onPoSelectRowWarehouseSelected(row: PoMaterialSelectRow, code: string): void {
    if (row.alreadyInPoStatus) {
      return;
    }
    row.warehouse = code;
    this.cdr.markForCheck();
  }

  getPoSelectSelectableRows(): PoMaterialSelectRow[] {
    return this.poSelectRows.filter(
      (r) => !r.alreadyOnTable && !r.alreadyInPoStatus,
    );
  }

  getPoSelectSelectedCount(): number {
    return this.poSelectRows.filter(
      (r) => r.selected && !r.alreadyOnTable && !r.alreadyInPoStatus,
    ).length;
  }

  canConfirmPoSelect(): boolean {
    return this.getPoSelectSelectedCount() > 0;
  }

  onPoSelectAllChange(checked: boolean): void {
    this.poSelectAllChecked = checked;
    const visibleKeys = new Set(
      this.getPoSelectVisibleSelectableRows().map((r) => r.rowKey),
    );
    this.poSelectRows = this.poSelectRows.map((row) => {
      if (row.alreadyOnTable || row.alreadyInPoStatus) {
        return row;
      }
      if (!visibleKeys.has(row.rowKey)) {
        return row;
      }
      return { ...row, selected: checked };
    });
    this.cdr.markForCheck();
  }

  onPoSelectRowChange(row: PoMaterialSelectRow, checked: boolean): void {
    if (row.alreadyOnTable || row.alreadyInPoStatus) {
      return;
    }
    row.selected = checked;
    this.syncPoSelectAllChecked();
    this.cdr.markForCheck();
  }

  onConfirmPoSelect(): void {
    if (!this.canConfirmPoSelect() || !this.pendingPoResponse) {
      this.showSnackbar(
        "Vui lòng chọn ít nhất một vật tư.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }

    const selectedRows = this.poSelectRows.filter(
      (r) => r.selected && !r.alreadyOnTable && !r.alreadyInPoStatus,
    );
    const missingWarehouse = selectedRows.filter(
      (r) => !this.resolvePoSelectRowWhsCode(r),
    );
    if (missingWarehouse.length) {
      this.showSnackbar(
        "Vui lòng chọn mã kho SAP (header hoặc từng dòng) cho vật tư đã chọn.",
        "Đóng",
        5000,
        "warning",
      );
      return;
    }
    const selectedDetails = selectedRows.map((r) => r.detail);

    const headerWhs = this.normalizeWarehouseCode(this.poSelectHeaderWarehouse);
    if (headerWhs) {
      this.sapWarehouseCode = headerWhs;
      this.poSelectHeaderWarehouse = headerWhs;
    } else {
      const firstSelectedWhs = this.resolvePoSelectRowWhsCode(selectedRows[0]);
      if (firstSelectedWhs) {
        this.sapWarehouseCode = firstSelectedWhs;
        this.poSelectHeaderWarehouse = firstSelectedWhs;
      }
    }

    const added = this.appendPoDetailsToTable(selectedDetails, selectedRows);
    this.applyPoSelectWarehousesToTable(selectedRows);
    this.cachePendingProductInPoStatus(this.poSelectRows);
    const po = this.poNumber.trim();

    if (added > 0) {
      this.applyBulkFieldsToAllLots();
      this.applyPoToAllChildLots(po);
      this.showSnackbar(
        `Đã thêm ${added} vật tư từ PO ${po} vào bảng.`,
        "Đóng",
        4000,
        "success",
      );
    } else {
      this.showSnackbar(
        "Không có vật tư mới nào được thêm vào bảng.",
        "Đóng",
        4000,
        "warning",
      );
    }

    this.closePoSelectModal();
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
    if (!this.isLotEditable(lot)) {
      return;
    }
    lot.quantity = this.sanitizeNonNegativeQuantity(raw);
    this.syncParentQuantityFromLots(row);
    this.notifyIfLotTotalExceedsPo(row);
    this.markParentReconcilePending(row);
    this.cdr.markForCheck();
  }

  onLotTemQuantityChange(
    row: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    raw: string | number | null,
  ): void {
    if (!this.isLotEditable(lot)) {
      return;
    }
    lot.temQuantity = this.sanitizeNonNegativeQuantity(raw);
    this.syncParentQuantityFromLots(row);
    this.notifyIfLotTotalExceedsPo(row);
    this.markParentReconcilePending(row);
    this.cdr.markForCheck();
  }

  /** SL theo PO của từng lô = số lượng lô × số tem lô. */
  getLotLineTotal(lot: ReceivingLotRow): number {
    return (lot.quantity ?? 0) * (lot.temQuantity ?? 0);
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

  /** Lô đã tạo tem — không cho sửa / không gửi lên khi lưu đơn. */
  isLotEditable(lot: ReceivingLotRow): boolean {
    return !this.lotHasTem(lot);
  }

  canFetchPoInfo(): boolean {
    return Boolean(this.poNumber.trim()) && !this.isFetchingPo;
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
      row.quantityByPo > 0 ? row.quantityByPo : null,
    );
    if (prevLot) {
      lot.manufacturingDate = prevLot.manufacturingDate
        ? new Date(prevLot.manufacturingDate)
        : null;
      lot.expirationDate = prevLot.expirationDate
        ? new Date(prevLot.expirationDate)
        : null;
      lot.arrivalDate = prevLot.arrivalDate
        ? new Date(prevLot.arrivalDate)
        : new Date();
    }
    if (this.autoGenerateLot) {
      lot.lotNumber = this.generateAutoLotNumber(row, lot);
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
    if (!this.isLotEditable(lot)) {
      return;
    }
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
      sapCode: this.filterSapCode,
      itemName: this.filterItemName,
      partNumber: this.filterPartNumber,
    });
    this.refreshTableView();
  }

  onSapWarehouseSearch(keyword: string): void {
    if (!this.sapWarehouseList.length && !this.isLoadingSapWarehouses) {
      this.loadSapWarehouses(keyword);
      return;
    }
    const term = (keyword ?? "").trim().toLowerCase();
    this.filteredSapWarehouseList = term
      ? this.sapWarehouseList.filter(
          (whs) =>
            whs.whsCode.toLowerCase().includes(term) ||
            whs.whsName.toLowerCase().includes(term),
        )
      : [...this.sapWarehouseList];
  }

  displaySapWarehouse = (code: string | null): string => {
    if (!code) {
      return "";
    }
    const whs = this.sapWarehouseList.find((w) => w.whsCode === code);
    return whs ? `${whs.whsCode} - ${whs.whsName}` : code;
  };

  onHeaderSapWarehouseChange(value: string): void {
    const code = this.normalizeWarehouseCode(value);
    this.sapWarehouseCode = code || value.trim();
    if (!code) {
      return;
    }
    this.applySapWarehouseToAll(code);
  }

  onHeaderSapWarehouseSelected(code: string): void {
    this.sapWarehouseCode = code;
    this.applySapWarehouseToAll(code);
  }

  onHeaderLocationInput(value: string): void {
    this.headerLocation = value;
    if (value.trim()) {
      this.applyHeaderLocationToAll(value.trim());
    }
  }

  onHeaderLocationSelected(selected: string | WarehouseLocation): void {
    const loc = this.resolveWarehouseLocation(selected);
    const name = loc?.locationName ?? this.normalizeLocationValue(selected);
    this.headerLocation = name;
    this.applyHeaderLocationToAll(name);
  }

  onBulkExpirationExtendUnitChange(unit: ExpirationExtendUnit): void {
    this.bulkExpirationExtendUnit = unit;
    this.bulkExpirationExtendValue =
      unit === "month"
        ? this.expirationExtendMonthOptions[0]
        : this.expirationExtendYearOptions[0];
    this.applyBulkExpirationExtend();
  }

  onBulkExpirationExtendValueChange(value: number): void {
    this.bulkExpirationExtendValue = value;
    this.applyBulkExpirationExtend();
  }

  get bulkExpirationExtendOptions(): number[] {
    return this.bulkExpirationExtendUnit === "month"
      ? this.expirationExtendMonthOptions
      : this.expirationExtendYearOptions;
  }

  /** Chọn NSX → HSD = NSX + số tháng/năm đã chọn. */
  onBulkManufacturingDateChange(): void {
    this.bulkExpirationDate = this.computeBulkExpirationDate();
    this.onBulkDateFieldChange();
  }

  /** Chọn NSX / Ngày về / HSD (thủ công) ở bulk → áp dụng ngay. */
  onBulkDateFieldChange(): void {
    if (!this.dataSource.data.length) {
      return;
    }
    this.applyBulkFieldsToAllLots();
    this.refreshTableView();
    this.cdr.markForCheck();
  }

  generateAutoLotForAll(): void {
    this.dataSource.data = this.dataSource.data.map((parent) => {
      const lots = parent.lots.map((lot) => {
        if (!this.shouldApplyBulkToLot(lot)) {
          return lot;
        }
        return {
          ...lot,
          lotNumber: this.generateAutoLotNumber(parent, lot),
        };
      });
      return { ...parent, lots };
    });
    this.refreshTableView();
    this.cdr.markForCheck();
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

        this.receivingService
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
    if (this.lotIsSapSent(lot)) {
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
    return this.lotIsSapSent(lot) ? "Đã gửi SAP" : "Gửi SAP";
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
        this.lotIsSapSent(lot)
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

  openPartImportModal(): void {
    const sapCodes = this.collectSapCodesFromTable();
    if (!sapCodes.length) {
      this.showSnackbar(
        "Chưa có vật tư trên bảng để đối chiếu partnumber.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }
    this.partImportFile = null;
    this.partImportFileName = "";
    this.partImportRows = [];
    this.partImportResult = null;
    this.partImportMeta = null;
    this.showPartImportModal = true;
    document.body.classList.add("modal-open");
    this.cdr.markForCheck();
  }

  closePartImportModal(): void {
    this.showPartImportModal = false;
    this.isParsingPartImport = false;
    this.partImportFile = null;
    this.partImportFileName = "";
    this.partImportRows = [];
    this.partImportResult = null;
    this.partImportMeta = null;
    document.body.classList.remove("modal-open");
    this.cdr.markForCheck();
  }

  onPartImportFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    input.value = "";
    if (!file) {
      return;
    }
    this.partImportFile = file;
    this.partImportFileName = file.name;
    this.partImportResult = null;
    this.partImportRows = [];
    this.partImportMeta = null;
    this.isParsingPartImport = true;
    this.cdr.markForCheck();

    parsePartNumberImportFile(file)
      .then(({ rows, meta }) => {
        this.isParsingPartImport = false;
        this.partImportRows = rows;
        this.partImportMeta = meta;
        this.onReconcilePartNumbers();
        this.cdr.markForCheck();
      })
      .catch((err: unknown) => {
        this.isParsingPartImport = false;
        this.partImportFile = null;
        this.partImportFileName = "";
        this.partImportRows = [];
        this.partImportResult = null;
        this.partImportMeta = null;
        const message =
          err instanceof Error ? err.message : "Không đọc được file Excel.";
        this.showSnackbar(message, "Đóng", 6000, "warning");
        this.cdr.markForCheck();
      });
  }

  onReconcilePartNumbers(): void {
    if (!this.partImportRows.length) {
      this.showSnackbar(
        "Vui lòng chọn file Excel có cột DInvCode/Part number và RDCode trước.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }
    const tableRows = this.collectRowsForPartReconcile();
    if (!tableRows.length) {
      this.showSnackbar(
        "Chưa có dòng vật tư trên bảng để đối chiếu.",
        "Đóng",
        4000,
        "warning",
      );
      return;
    }
    this.partImportResult = reconcilePartNumbersFromPl(
      tableRows,
      this.partImportRows,
    );
    if (this.editingRequestId) {
      this.updateRequestWithPoNumber();
    }
    this.cdr.markForCheck();
  }

  canApplyImportedPartNumbers(): boolean {
    if (!this.partImportResult?.importPartBySap) {
      return false;
    }
    return this.partImportResult.rows.some((r) => r.status === "mismatch");
  }

  onApplyImportedPartNumbers(): void {
    if (!this.canApplyImportedPartNumbers() || !this.partImportResult) {
      return;
    }
    const tableRows = this.collectRowsForPartReconcile();
    const applied = applyImportedPartNumbers(
      tableRows,
      this.partImportResult.importPartBySap,
      true,
    );
    this.dataSource.data.forEach((parent) => {
      const match = tableRows.find(
        (r) => r.sapCode.trim() === parent.sapCode.trim(),
      );
      if (match) {
        parent.partNumber = match.partNumber;
      }
    });
    this.onReconcilePartNumbers();
    this.showSnackbar(
      applied
        ? `Đã áp dụng mã part từ file import cho ${applied} dòng.`
        : "Không có mã part nào được áp dụng.",
      "Đóng",
      5000,
      applied ? "success" : "warning",
    );
    this.cdr.markForCheck();
  }

  collectRowsForPartReconcile(): TablePartRow[] {
    return this.dataSource.data
      .filter((row) => row.sapCode.trim())
      .map((row) => ({
        sapCode: row.sapCode.trim(),
        itemName: row.itemName.trim(),
        partNumber: row.partNumber.trim(),
      }));
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

    this.receivingService
      .getSapPoInfo(po)
      .pipe(take(1))
      .subscribe({
        next: (res) => {
          if (res) {
            this.populatePoQuantityMap(res);
            this.dataSource.data.forEach((row) => {
              if (matchedSet.has(row.sapCode.trim())) {
                this.applyPoQuantityToRow(row);
              }
            });
            this.refreshTableView();
            this.cdr.markForCheck();
          }
        },
      });

    this.persistOrderChanges({
      silent: true,
      allowEmptyProductSync: true,
      onSuccess: () => {
        if (this.editingRequestId) {
          this.updateRequestWithPoNumberFromModal(po);
        }
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
          (sum, lot) => sum + this.getLotLineTotal(lot),
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

  onExportExcelAll(): void {
    const rows = this.collectAllTemPreviewRows();
    if (!rows.length) {
      this.showSnackbar(
        "Chưa có mã tem. Vui lòng tạo mã trước.",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }
    const date = new Date().toISOString().split("T")[0];
    const requestLabel = this.editingRequestId ?? "new";
    this.downloadExcel(rows, `tem_export_${requestLabel}_${date}.xlsx`);
  }

  canSendPanacimLot(
    _parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
  ): boolean {
    return (
      !this.isSendingPanacim &&
      lot.productId != null &&
      this.lotHasTem(lot) &&
      !lot.uploadPanacim
    );
  }

  canSendPanacimParent(parent: ReceivingMaterialRow): boolean {
    if (this.isSendingPanacim) {
      return false;
    }
    return parent.lots.some(
      (lot) =>
        lot.productId != null && this.lotHasTem(lot) && !lot.uploadPanacim,
    );
  }

  onSendPanacimLot(parent: ReceivingMaterialRow, lot: ReceivingLotRow): void {
    if (!this.canSendPanacimLot(parent, lot)) {
      return;
    }
    const label = lot.lotNumber || parent.sapCode;
    this.confirmAction(
      `Gửi dữ liệu tem lô ${label} đến PanaCIM?`,
      "Gửi PanaCIM",
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
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
      const fileName = `CSV_UP_Panacim_${parent.sapCode}_${label}_${new Date().toISOString().split("T")[0]}.csv`;
      this.uploadTemRowsToPanacim(rows, fileName, [Number(lot.productId)]);
    });
  }

  onSendPanacimParent(parent: ReceivingMaterialRow): void {
    if (!this.canSendPanacimParent(parent)) {
      return;
    }
    this.confirmAction(
      `Gửi dữ liệu tem mã SAP ${parent.sapCode} đến PanaCIM?`,
      "Gửi PanaCIM",
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      const lots = parent.lots.filter(
        (lot) =>
          lot.productId != null && this.lotHasTem(lot) && !lot.uploadPanacim,
      );
      const rows = lots.flatMap((lot) => this.getTemRowsForLot(parent, lot));
      if (!rows.length) {
        this.showSnackbar(
          "Chưa có mã tem. Vui lòng tạo mã trước.",
          "Đóng",
          3000,
          "warning",
        );
        return;
      }
      const productIds = [...new Set(lots.map((lot) => Number(lot.productId)))];
      const fileName = `CSV_UP_Panacim_${parent.sapCode}_${new Date().toISOString().split("T")[0]}.csv`;
      this.uploadTemRowsToPanacim(rows, fileName, productIds);
    });
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
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
    field: "manufacturingDate" | "expirationDate" | "arrivalDate",
    value: string,
  ): void {
    if (!this.isLotEditable(lot)) {
      return;
    }
    lot[field] = value ? new Date(value) : null;
    if (!this.shouldApplyBulkToLot(lot)) {
      return;
    }
    if (field === "manufacturingDate") {
      lot.expirationDate = this.computeExpirationFromExtend(
        this.bulkExpirationExtendUnit,
        this.bulkExpirationExtendValue,
        lot.manufacturingDate,
      );
    }
    if (field === "arrivalDate" && lot.arrivalDate) {
      lot.userData4 = this.buildUserData4(parent.sapCode, lot.arrivalDate);
    }
    if (this.autoGenerateLot && field === "arrivalDate") {
      lot.lotNumber = this.generateAutoLotNumber(parent, lot);
    }
    this.cdr.markForCheck();
  }

  /** Giữ / khôi phục mã kho header modal — đồng bộ form ngoài, không mất khi mở lại. */
  private syncPoSelectHeaderWarehouseFromForm(
    poStatusRecords: ProductInPoStatusDto[] = [],
  ): void {
    const fromForm = this.normalizeWarehouseCode(this.sapWarehouseCode);
    const fromModal = this.normalizeWarehouseCode(this.poSelectHeaderWarehouse);
    const fromPoStatus =
      this.resolveCommonWarehouseFromPoStatus(poStatusRecords);
    const code = fromForm || fromModal || fromPoStatus;
    this.poSelectHeaderWarehouse = code;
    if (code && !fromForm) {
      this.sapWarehouseCode = code;
    }
    if (code) {
      this.onSapWarehouseSearch(code);
    }
  }

  private applySapWarehouseToAll(code: string): void {
    const normalized = this.normalizeWarehouseCode(code);
    if (!normalized) {
      return;
    }
    this.dataSource.data = this.dataSource.data.map((parent) => {
      const lots = parent.lots.map((lot) => ({
        ...lot,
        sapWarehouse: normalized,
      }));
      return { ...parent, sapWarehouse: normalized, lots };
    });
    this.refreshTableView();
    this.cdr.markForCheck();
  }

  private resolvePoSelectRowWhsCode(row: PoMaterialSelectRow): string {
    const rowWh = this.normalizeWarehouseCode(row.warehouse);
    if (rowWh) {
      return rowWh;
    }
    const modalHeaderWh = this.normalizeWarehouseCode(
      this.poSelectHeaderWarehouse,
    );
    if (modalHeaderWh) {
      return modalHeaderWh;
    }
    return this.normalizeWarehouseCode(this.sapWarehouseCode);
  }

  private normalizeWarehouseCode(value: string | null | undefined): string {
    const trimmed = (value ?? "").trim();
    if (!trimmed) {
      return "";
    }
    const exact = this.sapWarehouseList.find((w) => w.whsCode === trimmed);
    if (exact) {
      return exact.whsCode;
    }
    const dashIdx = trimmed.indexOf(" - ");
    if (dashIdx > 0) {
      const codePart = trimmed.substring(0, dashIdx).trim();
      const byCode = this.sapWarehouseList.find((w) => w.whsCode === codePart);
      if (byCode) {
        return byCode.whsCode;
      }
      return codePart;
    }
    const byLabel = this.sapWarehouseList.find(
      (w) => `${w.whsCode} - ${w.whsName}` === trimmed,
    );
    return byLabel ? byLabel.whsCode : trimmed;
  }

  private closePoSelectWarehousePanels(): void {
    this.poSelectWhsTriggers?.forEach((trigger) => {
      if (trigger.panelOpen) {
        trigger.closePanel();
      }
    });
  }

  private applyHeaderLocationToAll(location: string): void {
    this.storageUnit = location;
    this.dataSource.data = this.dataSource.data.map((parent) => {
      const lots = parent.lots.map((lot) => ({
        ...lot,
        location,
        locationOverridden: false,
      }));
      return { ...parent, location, lots };
    });
    this.refreshTableView();
    this.cdr.markForCheck();
  }

  private computeBulkExpirationDate(): Date {
    return this.computeExpirationFromExtend(
      this.bulkExpirationExtendUnit,
      this.bulkExpirationExtendValue,
      this.bulkManufacturingDate,
    );
  }

  private computeExpirationFromExtend(
    unit: ExpirationExtendUnit,
    value: number,
    baseDate: Date | null = null,
  ): Date {
    const result = baseDate ? new Date(baseDate) : new Date();
    if (unit === "month") {
      result.setMonth(result.getMonth() + value);
    } else {
      result.setFullYear(result.getFullYear() + value);
    }
    return result;
  }

  private applyBulkExpirationExtend(): void {
    this.bulkExpirationDate = this.computeBulkExpirationDate();
    this.onBulkDateFieldChange();
  }

  private generateAutoLotNumber(
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
  ): string {
    const today = new Date();
    const arrival = lot.arrivalDate ?? this.bulkArrivalDate ?? today;
    const referenceWarehouse =
      this.sapWarehouseCode.trim() || parent.sapWarehouse.trim();
    const lotWarehouse = (lot.sapWarehouse || parent.sapWarehouse).trim();
    const suffix =
      referenceWarehouse && lotWarehouse && referenceWarehouse !== lotWarehouse
        ? "02"
        : "01";
    return `${this.formatYyMmDd(today)}${this.formatYyMmDd(arrival)}${suffix}`;
  }

  /** Lô chưa persist DB (chưa có productId). */
  private isUnsavedLot(lot: ReceivingLotRow): boolean {
    return lot.productId == null;
  }

  /** Đơn mới hoặc lô chưa lưu — bulk chỉ áp dụng cho đối tượng này khi sửa đơn đã lưu. */
  private shouldApplyBulkToLot(lot: ReceivingLotRow): boolean {
    if (!this.editingRequestId) {
      return true;
    }
    return this.isUnsavedLot(lot);
  }

  private formatYyMmDd(date: Date): string {
    const yy = String(date.getFullYear()).slice(-2);
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    return `${yy}${mm}${dd}`;
  }

  private getPoQuantityForSapCode(sapCode: string): number {
    const normalized = this.normalizeSapCode(sapCode);
    const entry = Object.entries(this.poQuantityBySapCode).find(
      ([code]) => this.normalizeSapCode(code) === normalized,
    );
    return entry ? entry[1] : 0;
  }

  private populatePoQuantityMap(res: SapPoInfoResponse): void {
    const qtyMap: Record<string, number> = {};
    (res.poDetails ?? []).forEach((d) => {
      const code = (d.por1ItemCode ?? "").trim();
      if (!code) {
        return;
      }
      qtyMap[code] = Number.parseFloat(d.por1Quantity ?? "0") || 0;
    });
    this.poQuantityBySapCode = qtyMap;
  }

  /** Cập nhật SL theo PO trên dòng cha; chỉ gán SL lô từ PO cho lô chưa lưu DB. */
  private applyPoQuantityToRow(row: ReceivingMaterialRow): void {
    const qty = this.getPoQuantityForSapCode(row.sapCode);
    if (qty <= 0) {
      return;
    }
    row.quantityByPo = qty;
    row.lots = row.lots.map((lot) =>
      this.isUnsavedLot(lot) ? { ...lot, quantity: qty } : lot,
    );
    this.syncParentQuantityFromLots(row);
  }

  private persistOrderChanges(
    options: {
      silent?: boolean;
      /** Gọi onSuccess khi không có lô cần sync (vd. chỉ cập nhật PO header). */
      allowEmptyProductSync?: boolean;
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

    const poValue = this.resolveOrderPoValue();
    const vendor = this.vendorCode.trim();
    const vendorName = this.vendorName.trim();
    const orderWhsCode = this.normalizeWarehouseCode(this.sapWarehouseCode);

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

    if (
      this.editingRequestId &&
      !syncRows.length &&
      !this.pendingDeletedProductIds.length
    ) {
      if (options.allowEmptyProductSync) {
        options.onSuccess?.();
        return;
      }
      if (!silent) {
        this.showSnackbar(
          "Không có lô mới hoặc thay đổi cần lưu.",
          "Đóng",
          3000,
          "warning",
        );
      }
      return;
    }

    const products = syncRows.map((row) => row.item);

    this.isSavingOrder = true;

    if (this.editingRequestId) {
      this.receivingService
        .updateRequestProducts(this.editingRequestId, [], {
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
            this.applyRequestTableAfterSave(savedProducts);
            this.syncRequestHeaderWhsCode(orderWhsCode);
            this.flushPendingProductInPoStatus();
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

    this.findExistingRequestByPoAndWhs(poValue, orderWhsCode)
      .pipe(
        take(1),
        switchMap((existing) => {
          if (existing?.id) {
            const requestId = existing.id;
            return this.receivingService
              .updateRequestProducts(requestId, products, {
                rows: syncRows,
                deletedProductIds: [],
              })
              .pipe(
                map((savedProducts) => ({
                  requestId,
                  savedProducts,
                  merged: true,
                })),
              );
          }
          return this.receivingService
            .createRequestAndProducts(
              vendor,
              vendorName,
              poValue,
              this.currentUser,
              products,
              orderWhsCode,
            )
            .pipe(
              map((result) => ({
                requestId: result.requestId,
                savedProducts: result.products,
                merged: false,
              })),
            );
        }),
      )
      .subscribe({
        next: ({ requestId, savedProducts, merged }) => {
          this.handleNewOrderPersistSuccess(
            requestId,
            savedProducts,
            merged,
            orderWhsCode,
            silent,
            options.onSuccess,
          );
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

  /**
   * Tìm đơn đã có cùng PO (userData5) và mã kho SAP trước khi tạo mới.
   */
  private findExistingRequestByPoAndWhs(
    po: string,
    whsCode: string,
  ): Observable<ListRequestCreateTem | null> {
    const normalizedPo = po.trim();
    const normalizedWhs = this.normalizeWarehouseCode(whsCode);
    if (!this.isValidPoCode(normalizedPo) || !normalizedWhs) {
      return of(null);
    }

    return this.generateTemInService
      .getAllRequests({
        userData5: normalizedPo,
        whsCode: normalizedWhs,
        page: 0,
        size: 25,
      })
      .pipe(
        map((page) => {
          const match = page.content.find((item) => item.id != null);
          return match ?? null;
        }),
        catchError(() => of(null)),
      );
  }

  private handleNewOrderPersistSuccess(
    requestId: number,
    savedProducts: ReceivingProductRow[],
    merged: boolean,
    orderWhsCode: string,
    silent: boolean,
    onSuccess?: () => void,
  ): void {
    this.isSavingOrder = false;
    this.editingRequestId = requestId;
    this.pendingDeletedProductIds = [];
    if (merged && this.isValidPoCode(this.poNumber.trim())) {
      this.poAndVendorLocked = true;
    }
    this.applyRequestTableAfterSave(savedProducts);
    this.syncRequestHeaderWhsCode(orderWhsCode);
    this.flushPendingProductInPoStatus();

    if (silent) {
      this.loadExistingRequest(requestId);
      onSuccess?.();
      return;
    }

    if (merged) {
      this.showSnackbar(
        `Đơn #${requestId} đã tồn tại (cùng PO và mã kho SAP). Đã thêm vật tư vào đơn này.`,
        "Đóng",
        5000,
        "success",
      );
    } else {
      this.showSnackbar(
        `Đã tạo đơn #${requestId} với ${savedProducts.length} dòng sản phẩm.`,
        "Đóng",
        4000,
        "success",
      );
    }
    this.navigateAfterSave(requestId);
    onSuccess?.();
  }

  /** Sau lưu: luôn ở lại receiving-supplies. */
  private navigateAfterSave(requestId: number): void {
    this.router.navigate(["/generate-tem-in/receiving-supplies", requestId]);
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
    if (this.autoGenerateLot) {
      return true;
    }
    return (
      this.bulkExpirationDate != null ||
      this.bulkManufacturingDate != null ||
      this.bulkArrivalDate != null
    );
  }

  private commitAndApplySingleSap(code: string): void {
    const trimmed = code.trim();
    if (!trimmed) {
      return;
    }

    if (this.poSapCodes.length && !this.isSapCodeInCurrentPo(trimmed)) {
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

        this.applyBulkFieldsToAllLots();
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
      : this.computeExpirationFromExtend(
          this.bulkExpirationExtendUnit,
          this.bulkExpirationExtendValue,
          manufacturingDate,
        );
    const arrivalDate = this.bulkArrivalDate
      ? new Date(this.bulkArrivalDate)
      : null;

    if (
      !lotNumber &&
      !manufacturingDate &&
      !expirationDate &&
      !arrivalDate &&
      !this.autoGenerateLot
    ) {
      return;
    }

    this.dataSource.data = this.dataSource.data.map((parent) => {
      const lots = parent.lots.map((lot) => {
        if (!this.shouldApplyBulkToLot(lot)) {
          return lot;
        }
        const updated = {
          ...lot,
          lotNumber: lotNumber || lot.lotNumber,
          manufacturingDate: manufacturingDate ?? lot.manufacturingDate,
          expirationDate: expirationDate ?? lot.expirationDate,
          arrivalDate: arrivalDate ?? lot.arrivalDate ?? new Date(),
        };
        if (this.autoGenerateLot) {
          updated.lotNumber = this.generateAutoLotNumber(parent, updated);
        }
        updated.userData4 = this.buildUserData4(
          parent.sapCode,
          updated.arrivalDate ?? new Date(),
        );
        return updated;
      });
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
    if (this.poSapCodes.length && !this.isSapCodeInCurrentPo(code)) {
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
    const value = Number.parseFloat(String(raw).replace(",", "."));
    if (Number.isNaN(value) || value < 0) {
      return 0;
    }
    return value;
  }

  private syncParentQuantityFromLots(row: ReceivingMaterialRow): void {
    row.quantity = row.lots.reduce(
      (sum, lot) => sum + this.getLotLineTotal(lot),
      0,
    );
  }

  private notifyIfLotTotalExceedsPo(row: ReceivingMaterialRow): void {
    if (row.quantityByPo > 0 && (row.quantity ?? 0) > row.quantityByPo) {
      this.showSnackbar(
        `Mã ${row.sapCode}: tổng SL lô (${row.quantity}) vượt SL theo PO (${row.quantityByPo}).`,
        "Đóng",
        5000,
        "warning",
      );
    }
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
    const location = this.headerLocation || this.storageUnit || "";
    const sapWarehouse = this.sapWarehouseCode.trim();
    const poQty = this.getPoQuantityForSapCode(sapCode);
    const row: ReceivingMaterialRow = {
      id,
      sapCode,
      itemName: "",
      partNumber: "",
      location,
      sapWarehouse,
      quantityByPo: poQty,
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
        poQty,
      ),
    ];
    if (this.autoGenerateLot) {
      row.lots[0].lotNumber = this.generateAutoLotNumber(row, row.lots[0]);
    }
    this.syncParentQuantityFromLots(row);
    return row;
  }

  private buildDefaultLotRow(
    parent: ReceivingMaterialRow,
    location: string,
    rowKey: string,
    lotNumber = "",
    defaultQuantity: number | null = null,
  ): ReceivingLotRow {
    const arrivalDate = this.bulkArrivalDate
      ? new Date(this.bulkArrivalDate)
      : new Date();
    const manufacturingDate = this.bulkManufacturingDate
      ? new Date(this.bulkManufacturingDate)
      : null;
    const expirationDate = manufacturingDate
      ? this.computeExpirationFromExtend(
          this.bulkExpirationExtendUnit,
          this.bulkExpirationExtendValue,
          manufacturingDate,
        )
      : this.bulkExpirationDate
        ? new Date(this.bulkExpirationDate)
        : this.computeBulkExpirationDate();
    const qty =
      defaultQuantity ?? (parent.quantityByPo > 0 ? parent.quantityByPo : null);
    return {
      rowKey,
      lotLabel: "",
      location,
      sapWarehouse: parent.sapWarehouse || this.sapWarehouseCode.trim(),
      locationOverridden: false,
      lotNumber,
      quantity: qty,
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

  private formatDateForPanacimExport(value: Date | string | null): string {
    if (!value) {
      return "";
    }
    if (value instanceof Date) {
      return this.formatDateYyyyMmDd(value);
    }
    return value.trim();
  }

  private downloadCsv(rows: ReceivingTemPreviewRow[], filename: string): void {
    const blob = generateTemPanacimCsvBlob(
      this.toPanacimExportRows(rows),
      (date) => this.formatDateForPanacimExport(date),
    );
    this.downloadBlob(blob, filename);
    this.showSnackbar(`Đã tải CSV: ${filename}`, "Đóng", 3000, "success");
  }

  private downloadExcel(
    rows: ReceivingTemPreviewRow[],
    filename: string,
  ): void {
    const workbook = generateTemReelDataWorkbook(
      this.toReelDataExportRows(rows),
      (date) => this.formatDateForPanacimExport(date),
    );
    XLSX.writeFileXLSX(workbook, filename, {
      bookType: "xlsx",
      bookSST: false,
      type: "binary",
      compression: true,
    });
    this.showSnackbar(`Đã tải Excel: ${filename}`, "Đóng", 3000, "success");
  }

  private downloadBlob(blob: Blob, filename: string): void {
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", filename);
    link.style.visibility = "hidden";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }

  private collectAllTemPreviewRows(): ReceivingTemPreviewRow[] {
    return this.requestTemDetails.map((t) => this.mapTemDetailToPreviewRow(t));
  }

  private toPanacimExportRows(
    rows: ReceivingTemPreviewRow[],
  ): TemPanacimExportInput[] {
    return rows.map((item) => ({
      reelId: item.reelId,
      partNumber: item.partNumber,
      vendor: item.vendor,
      lot: item.lot,
      userData1: item.userData1,
      userData2: item.userData2,
      userData3: item.userData3,
      userData4: item.userData4,
      userData5: item.userData5,
      initialQuantity: item.initialQuantity,
      storageUnit: item.storageUnit,
      expirationDate: item.expirationDate,
      manufacturingDate: item.manufacturingDate,
      sapCode: item.sapCode,
    }));
  }

  private toReelDataExportRows(
    rows: ReceivingTemPreviewRow[],
  ): TemReelDataExportInput[] {
    return rows.map((item) => {
      const ctx = this.resolveLotContextForTem(item);
      const parent = ctx?.parent;
      const lot = ctx?.lot;

      return {
        numberOfPlanning:
          parent && parent.quantityByPo > 0 ? parent.quantityByPo : null,
        itemCode: item.sapCode,
        productName: item.productName,
        sapWo: "",
        lot: item.lot,
        version: "",
        timeReceived: lot?.arrivalDate ?? item.arrivalDate,
        reelId: item.reelId,
        partNumber: item.partNumber,
        vendor: item.vendor,
        quantityOfPackage: lot?.quantity ?? item.initialQuantity ?? null,
        mfgDate: lot?.manufacturingDate ?? item.manufacturingDate,
        productionShift: "",
        opName: "",
        comments: "",
        comments2: "",
        storageUnit: item.storageUnit,
        tpNk: "",
        rank: "",
        entryDate: lot?.arrivalDate ?? item.arrivalDate,
        expirationDate: lot?.expirationDate ?? item.expirationDate,
        po: lot ? this.resolveLotPoForSave(lot) : item.userData5,
        userData1: lot?.userData1 ?? item.userData1,
        userData2: lot?.userData2 ?? item.userData2,
        userData3: lot?.userData3 ?? item.userData3,
        qrCode: item.qrCode,
      };
    });
  }

  private resolveLotContextForTem(item: ReceivingTemPreviewRow): {
    parent: ReceivingMaterialRow;
    lot: ReceivingLotRow;
  } | null {
    const sap = item.sapCode.trim();
    const lotNumber = (item.lot ?? "").trim();
    const temDetail = this.requestTemDetails.find(
      (t) => t.reelId === item.reelId,
    );
    const productId = temDetail?.productOfRequestId;

    for (const parent of this.dataSource.data) {
      for (const lot of parent.lots) {
        if (productId != null && Number(lot.productId) === Number(productId)) {
          return { parent, lot };
        }
        if (
          parent.sapCode.trim() === sap &&
          lot.lotNumber.trim() === lotNumber
        ) {
          return { parent, lot };
        }
      }
    }

    return null;
  }

  private uploadTemRowsToPanacim(
    rows: ReceivingTemPreviewRow[],
    fileName: string,
    productIds: number[],
  ): void {
    const blob = generateTemPanacimCsvBlob(
      this.toPanacimExportRows(rows),
      (date) => this.formatDateForPanacimExport(date),
    );
    const formData = new FormData();
    formData.append("file", blob, fileName);
    this.isSendingPanacim = true;
    this.cdr.markForCheck();

    this.http
      .post<{
        success?: boolean;
        message?: string;
      }>("/api/csv-upload", formData)
      .subscribe({
        next: (response) => {
          this.isSendingPanacim = false;
          if (response?.success) {
            this.markLotsUploadPanacim(productIds);
            this.showSnackbar(
              `Đã gửi ${rows.length} bản ghi tới PanaCIM thành công!`,
              "Đóng",
              5000,
              "success",
            );
          } else {
            this.showSnackbar(
              `Lỗi: ${response?.message ?? "Gửi PanaCIM thất bại."}`,
              "Đóng",
              5000,
              "error",
            );
          }
          this.cdr.markForCheck();
        },
        error: (error) => {
          this.isSendingPanacim = false;
          this.showSnackbar(
            `Lỗi khi gửi: ${error.error?.message ?? error.message ?? "Không thể kết nối đến server"}`,
            "Đóng",
            5000,
            "error",
          );
          this.cdr.markForCheck();
        },
      });
  }

  private markLotsUploadPanacim(productIds: number[]): void {
    const idSet = new Set(productIds.map((id) => Number(id)));
    this.dataSource.data = this.dataSource.data.map((parent) => ({
      ...parent,
      lots: parent.lots.map((lot) => {
        if (lot.productId == null || !idSet.has(Number(lot.productId))) {
          return lot;
        }
        const product = this.savedProductsById.get(Number(lot.productId));
        if (product) {
          product.UploadPanacim = true;
          this.generateTemInService.updateUploadPanacim(product).subscribe({
            error: (err) => console.error("Lỗi cập nhật UploadPanacim:", err),
          });
        }
        return { ...lot, uploadPanacim: true };
      }),
    }));
    this.refreshTableView();
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

  private resolveWarehouseLocation(
    selected: string | WarehouseLocation,
  ): WarehouseLocation | null {
    if (typeof selected === "string") {
      const trimmed = selected.trim();
      return (
        this.filteredLocationOptions.find(
          (w) => w.locationFullName === trimmed || w.locationName === trimmed,
        ) ?? null
      );
    }
    return selected;
  }

  private openActiveLocationAutocompletePanel(): void {
    setTimeout(() => {
      const active = document.activeElement;
      this.locationAutocompleteTriggers?.forEach((trigger) => {
        const inputEl = (
          trigger as unknown as { _element?: { nativeElement: HTMLElement } }
        )._element?.nativeElement;
        if (inputEl === active && !trigger.panelOpen) {
          trigger.openPanel();
        }
      });
    });
  }

  /** PO hợp lệ trên lô: userData5 có giá trị và khác "-". */
  private lotHasValidPo(lot: ReceivingLotRow): boolean {
    const po = (lot.userData5 ?? "").trim();
    return Boolean(po) && po !== "-";
  }

  /** Đã gửi SAP (DB sapSendStatus hoặc phiên hiện tại). */
  private lotIsSapSent(lot: ReceivingLotRow): boolean {
    return lot.sapSendStatus === true || lot.sapSent === true;
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
      const storageUnit = this.getEffectiveSapWarehouse(parent, lot);
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
          entries.forEach(({ parent, lot }) => {
            lot.sapSent = true;
            lot.sapSendStatus = true;
          });
          const statusUpdates = entries
            .filter(
              ({ lot }) =>
                this.editingRequestId && lot.productId && lot.productId > 0,
            )
            .map(({ parent, lot }) =>
              this.receivingService.updateSapSendStatus(
                lot.productId!,
                this.editingRequestId!,
                this.buildExcelItemForLot(parent, lot),
                true,
              ),
            );
          const finish = (): void => {
            this.isSendingSap = false;
            this.showSnackbar(
              `Đã gửi SAP thành công (${entries.length} lô).`,
              "Đóng",
              4000,
              "success",
            );
            this.cdr.markForCheck();
          };
          if (!statusUpdates.length) {
            finish();
            return;
          }
          forkJoin(statusUpdates).subscribe({
            next: () => finish(),
            error: (err: unknown) => {
              finish();
              this.showSnackbar(
                `Đã gửi SAP nhưng lưu trạng thái thất bại: ${resolveHttpErrorMessage(err, "Lỗi cập nhật trạng thái.")}`,
                "Đóng",
                8000,
                "warning",
              );
            },
          });
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
    const storageUnit = this.getEffectiveSapWarehouse(parent, lot);
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
      if (!this.isLotEditable(lot)) {
        return lot;
      }
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

  private loadSapWarehouses(keyword = ""): void {
    if (this.isLoadingSapWarehouses) {
      return;
    }
    this.isLoadingSapWarehouses = true;
    this.receivingService.getSapWarehouses().subscribe({
      next: (data) => {
        this.sapWarehouseList = data;
        this.onSapWarehouseSearch(keyword);
        this.isLoadingSapWarehouses = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoadingSapWarehouses = false;
        this.cdr.markForCheck();
      },
    });
  }

  private applyPoResponseMetadata(res: SapPoInfoResponse): void {
    this.populatePoQuantityMap(res);
    this.poSapCodes = (res.poDetails ?? [])
      .map((d) => (d.por1ItemCode ?? "").trim())
      .filter((code): code is string => Boolean(code));

    const poInfo = res.poInfo;
    if (poInfo?.oporCardCode && !this.poAndVendorLocked) {
      this.vendorCode = poInfo.oporCardCode;
    }
    if (poInfo?.oporCardName && !this.poAndVendorLocked) {
      this.vendorName = poInfo.oporCardName;
    }
  }

  private buildPoSelectRows(
    res: SapPoInfoResponse,
    poStatusRecords: ProductInPoStatusDto[] = [],
  ): PoMaterialSelectRow[] {
    const onTable = new Set(
      this.dataSource.data.map((r) => r.sapCode.trim()).filter(Boolean),
    );
    const poStatusBySap = this.buildPoStatusBySapCode(poStatusRecords);

    return (res.poDetails ?? []).map((d, idx) => {
      const sapCode = (d.por1ItemCode ?? "").trim();
      const lineNum = (d.por1LineNum ?? String(idx)).trim();
      const alreadyOnTable = onTable.has(sapCode);
      const statusRecord = poStatusBySap.get(sapCode);
      const alreadyInPoStatus =
        this.isPoStatusLockedWithWarehouse(statusRecord);
      const warehouse = statusRecord
        ? this.normalizeWarehouseCode(statusRecord.whsCode)
        : "";

      return {
        rowKey: `${sapCode}-${lineNum}`,
        selected: !alreadyOnTable && alreadyInPoStatus,
        sapCode,
        itemName: (d.por1Dscription ?? "").trim(),
        quantity:
          statusRecord?.quantityByPo && statusRecord.quantityByPo > 0
            ? statusRecord.quantityByPo
            : Number.parseFloat(d.por1Quantity ?? "0") || 0,
        unit: (
          statusRecord?.uomcode ??
          d.por1UOMCode ??
          d.por1UnitMsr ??
          ""
        ).trim(),
        warehouse,
        alreadyOnTable,
        alreadyInPoStatus,
        detail: d,
      };
    });
  }

  /** Khóa dòng khi đã lưu product-in-po-status và có mã kho SAP. */
  private isPoStatusLockedWithWarehouse(
    record: ProductInPoStatusDto | undefined,
  ): boolean {
    if (!record) {
      return false;
    }
    return Boolean(this.normalizeWarehouseCode(record.whsCode));
  }

  private buildPoStatusBySapCode(
    records: ProductInPoStatusDto[],
  ): Map<string, ProductInPoStatusDto> {
    const statusMap = new Map<string, ProductInPoStatusDto>();
    records.forEach((record) => {
      const sapCode = record.sapCode?.trim();
      if (sapCode) {
        statusMap.set(sapCode, record);
      }
    });
    return statusMap;
  }

  private resolveCommonWarehouseFromPoStatus(
    records: ProductInPoStatusDto[],
  ): string {
    const codes = records
      .map((r) => this.normalizeWarehouseCode(r.whsCode))
      .filter((code) => Boolean(code));
    const unique = [...new Set(codes)];
    return unique.length === 1 ? unique[0] : "";
  }

  /** Ghi product-in-po-status khi lưu đơn — tất cả mã PO; đã tick gửi whsCode, chưa tick gửi "". */
  private cachePendingProductInPoStatus(rows: PoMaterialSelectRow[]): void {
    const po = this.poNumber.trim();
    if (!po) {
      return;
    }
    const pendingByKey = new Map(
      this.pendingProductInPoStatus.map(
        (p) => [`${p.userData5}|${p.sapCode.trim()}`, p] as const,
      ),
    );
    rows.forEach((row) => {
      if (row.alreadyInPoStatus || row.alreadyOnTable) {
        return;
      }
      const sapCode = row.sapCode.trim();
      if (!sapCode) {
        return;
      }
      pendingByKey.set(
        `${po}|${sapCode}`,
        this.buildProductInPoStatusCreatePayload(row, po),
      );
    });
    this.pendingProductInPoStatus = Array.from(pendingByKey.values());
  }

  private buildProductInPoStatusCreatePayload(
    row: PoMaterialSelectRow,
    po: string,
  ): ProductInPoStatusCreatePayload {
    const detail = row.detail;
    const lineVendor = (detail.por1LineVendor ?? this.vendorCode).trim();
    const uomcode =
      [row.unit, detail.por1UOMCode, detail.por1UnitMsr]
        .map((v) => (v ?? "").trim())
        .find((v) => v.length > 0) ?? "";
    const qty = Number.isFinite(row.quantity) ? row.quantity : 0;

    return {
      sapCode: row.sapCode.trim(),
      productName: row.itemName.trim(),
      whsCode: row.selected ? this.resolvePoSelectRowWhsCode(row) : "",
      userData5: po,
      createdAt: new Date().toISOString(),
      createBy: this.currentUser,
      quantityByPo: Math.max(0, Math.round(qty)),
      vendor: lineVendor,
      vendorName: this.vendorName.trim(),
      uomcode,
    };
  }

  private flushPendingProductInPoStatus(): void {
    if (!this.pendingProductInPoStatus.length) {
      return;
    }
    const requestId = this.editingRequestId;
    if (!requestId) {
      return;
    }
    const payloads = this.pendingProductInPoStatus.map((p) => ({
      sapCode: p.sapCode.trim(),
      productName: p.productName.trim(),
      whsCode: p.whsCode.trim(),
      userData5: p.userData5.trim(),
      createdAt: new Date().toISOString(),
      createBy: this.currentUser,
      quantityByPo: p.quantityByPo ?? 0,
      vendor: p.vendor.trim(),
      vendorName: p.vendorName.trim(),
      uomcode: p.uomcode.trim(),
      listRequestCreateTemId: requestId,
    }));
    forkJoin(
      payloads.map((payload) =>
        this.receivingService.createProductInPoStatus(payload).pipe(
          map((created) => {
            if (!created) {
              throw new Error(payload.sapCode);
            }
            return created;
          }),
        ),
      ),
    ).subscribe({
      next: () => {
        this.pendingProductInPoStatus = [];
      },
      error: (err: unknown) => {
        const failedCode = err instanceof Error ? err.message?.trim() : "";
        this.showSnackbar(
          failedCode
            ? `Đã lưu đơn nhưng chưa ghi trạng thái chọn PO cho mã ${failedCode}.`
            : "Đã lưu đơn nhưng chưa ghi hết trạng thái chọn PO.",
          "Đóng",
          6000,
          "warning",
        );
        this.cdr.markForCheck();
      },
    });
  }

  private syncPoSelectAllChecked(): void {
    const hasFilter =
      Boolean(this.poSelectFilterSapCode.trim()) ||
      Boolean(this.poSelectFilterItemName.trim());
    const selectable = hasFilter
      ? this.getPoSelectVisibleSelectableRows()
      : this.getPoSelectSelectableRows();
    this.poSelectAllChecked =
      selectable.length > 0 && selectable.every((r) => r.selected);
  }

  private appendPoDetailsToTable(
    details: SapPoInfoDetail[],
    poSelectRows: PoMaterialSelectRow[] = [],
  ): number {
    if (!details.length) {
      return 0;
    }

    const warehouseBySap = new Map(
      poSelectRows.map(
        (r) => [r.sapCode.trim(), this.resolvePoSelectRowWhsCode(r)] as const,
      ),
    );

    const existingSap = new Set(
      this.dataSource.data.map((r) => r.sapCode.trim()).filter(Boolean),
    );
    const toAdd = details.filter((d) => {
      const code = (d.por1ItemCode ?? "").trim();
      return code && !existingSap.has(code);
    });

    if (!toAdd.length) {
      return 0;
    }

    let nextId = this.getNextRowId();
    const newRows: ReceivingMaterialRow[] = toAdd.map((d) => {
      const sapCode = (d.por1ItemCode ?? "").trim();
      const itemName = (d.por1Dscription ?? "").trim();
      const qtyByPo = Number.parseFloat(d.por1Quantity ?? "0") || 0;
      const lineVendor = (d.por1LineVendor ?? this.vendorCode).trim();
      const id = nextId;
      nextId += 1;
      const location = this.headerLocation || this.storageUnit || "";
      const fromModalRow = warehouseBySap.get(sapCode)?.trim();
      const fromModalHeader = this.normalizeWarehouseCode(
        this.poSelectHeaderWarehouse,
      );
      const fromFormHeader = this.normalizeWarehouseCode(this.sapWarehouseCode);
      let sapWarehouse = "";
      if (fromModalRow) {
        sapWarehouse = fromModalRow;
      } else if (fromModalHeader) {
        sapWarehouse = fromModalHeader;
      } else {
        sapWarehouse = fromFormHeader;
      }

      const row: ReceivingMaterialRow = {
        id,
        sapCode,
        itemName,
        partNumber: "",
        location,
        sapWarehouse,
        quantityByPo: qtyByPo,
        quantity: 0,
        lotNumber: "1 lô",
        temQuantity: 1,
        manufacturingDate: null,
        expirationDate: null,
        note: "",
        lots: [],
        expanded: true,
        reconcileStatus: "passed",
      };

      const lot = this.buildDefaultLotRow(
        row,
        location,
        `po-${id}-${Date.now()}`,
        this.bulkLotNumber,
        qtyByPo,
      );
      lot.vendor = lineVendor;
      lot.vendorName = this.vendorName;
      if (this.autoGenerateLot) {
        lot.lotNumber = this.generateAutoLotNumber(row, lot);
      }
      row.lots = [lot];
      this.syncParentQuantityFromLots(row);
      return row;
    });

    this.dataSource.data = [...this.dataSource.data, ...newRows];
    this.enrichSapOitmRows(newRows);
    this.applyTableFilter();
    this.syncSelectedSapCodesFromTable();
    return newRows.length;
  }

  private applyPoSelectWarehousesToTable(rows: PoMaterialSelectRow[]): void {
    const warehouseBySap = new Map(
      rows
        .map((r) => {
          const whs = this.resolvePoSelectRowWhsCode(r);
          return whs ? ([r.sapCode.trim(), whs] as const) : null;
        })
        .filter((entry): entry is readonly [string, string] => entry !== null),
    );
    if (!warehouseBySap.size) {
      return;
    }

    this.dataSource.data = this.dataSource.data.map((parent) => {
      const whs = warehouseBySap.get(parent.sapCode.trim());
      if (!whs) {
        return parent;
      }
      const lots = parent.lots.map((lot) => ({ ...lot, sapWarehouse: whs }));
      return { ...parent, sapWarehouse: whs, lots };
    });
    this.refreshTableView();
    this.cdr.markForCheck();
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

    const poQtyError = this.validateWithPoOrderBeforeGenerate();
    if (poQtyError) {
      return poQtyError;
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
        (sum, lot) => sum + this.getLotLineTotal(lot),
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
        const sapUnitError = validateStorageUnitForSap(
          this.getEffectiveSapWarehouse(parent, lot),
        );
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
      products: this.receivingService
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
    this.savedProductsById.clear();
    products.forEach((p) => {
      const id = Number(p.id);
      if (!Number.isNaN(id) && id > 0) {
        this.savedProductsById.set(id, p);
      }
    });
    this.updateGenerateButtonState();
    this.syncProductIdsFromSavedProducts(products);
    this.syncUploadPanacimFromSavedProducts(products);
    this.refreshLotDetailTemPreviewIfOpen();
    this.cdr.markForCheck();
  }

  private syncUploadPanacimFromSavedProducts(
    products: SavedProductRow[],
  ): void {
    const byId = new Map(
      products.map((p) => [Number(p.id), p.UploadPanacim === true]),
    );
    this.dataSource.data = this.dataSource.data.map((parent) => ({
      ...parent,
      lots: parent.lots.map((lot) => {
        if (lot.productId == null) {
          return lot;
        }
        const uploaded = byId.get(Number(lot.productId));
        if (uploaded == null) {
          return lot;
        }
        return { ...lot, uploadPanacim: uploaded };
      }),
    }));
    this.refreshTableView();
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
    if (!this.poSapCodes.length || parent.reconcileStatus === "pending") {
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
          return {
            ...(lot.productId === id ? lot : { ...lot, productId: id }),
            uploadPanacim: picked.UploadPanacim === true,
          };
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
    const editableLots = rows.flatMap((parent) =>
      parent.lots
        .filter((lot) => this.isLotEditable(lot))
        .map((lot) => ({ parent, lot })),
    );
    if (!editableLots.length && !this.pendingDeletedProductIds.length) {
      return "Chưa có lô mới hoặc thay đổi cần lưu đơn.";
    }

    for (const { parent, lot } of editableLots) {
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
    const rows: Array<{ productId?: number; item: ExcelImportData }> = [];
    const usedProductIds = new Set<number>();
    for (const parent of this.dataSource.data) {
      for (const lot of parent.lots) {
        if (this.lotHasTem(lot)) {
          continue;
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
          item: this.buildExcelItemForLot(parent, lot),
        });
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
      parent.lots.map((lot) => this.buildExcelItemForLot(parent, lot)),
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
        if (res) {
          this.populatePoQuantityMap(res);
        }
        this.poSapCodes = (res?.poDetails ?? [])
          .map((d) => (d.por1ItemCode ?? "").trim())
          .filter((code): code is string => Boolean(code));
        this.dataSource.data.forEach((row) => this.applyPoQuantityToRow(row));
        this.refreshTableView();
        this.cdr.markForCheck();
      },
    });
  }

  private getEffectiveSapWarehouse(
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
  ): string {
    const lotWh = (lot.sapWarehouse ?? "").trim();
    if (lotWh) {
      return lotWh;
    }
    const parentWh = (parent.sapWarehouse ?? "").trim();
    if (parentWh) {
      return parentWh;
    }
    return this.sapWarehouseCode.trim();
  }

  private buildExcelItemForLot(
    parent: ReceivingMaterialRow,
    lot: ReceivingLotRow,
  ): ExcelImportData {
    return {
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
      whsCode: this.getEffectiveSapWarehouse(parent, lot),
      sapSendStatus: this.lotIsSapSent(lot),
    };
  }

  private isSapCodeInCurrentPo(sapCode: string): boolean {
    if (!this.poSapCodes.length) {
      return true;
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
        if (lot.productId && this.lotIsSapSent(lot)) {
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
          ? { ...lot, sapSent: true, sapSendStatus: true }
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
      products: this.receivingService.getProductsByRequestId(requestId),
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
            this.poAndVendorLocked = true;
          } else {
            this.poNumber = "";
            this.poAndVendorLocked = false;
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
      const parentWhs =
        (first.WhsCode ?? "").trim() || this.sapWarehouseCode.trim();
      const row: ReceivingMaterialRow = {
        id: nextId,
        sapCode,
        itemName: first.productName ?? "",
        partNumber: first.partNumber ?? "",
        location: parentLocation,
        sapWarehouse: parentWhs,
        quantityByPo: this.getPoQuantityForSapCode(sapCode),
        quantity: 0,
        lotNumber: `${items.length} lô`,
        temQuantity: first.temQuantity ?? 1,
        manufacturingDate: null,
        expirationDate: null,
        note: "",
        lots: items.map((p, idx) => {
          const lotLocation = p.storageUnit ?? "";
          const lotWhs =
            (p.WhsCode ?? "").trim() ||
            parentWhs ||
            this.sapWarehouseCode.trim();
          const sapSent = p.sapSendStatus === true;
          return {
            rowKey: `saved-${p.id}-${idx}`,
            productId: p.id,
            lotLabel: "",
            location: lotLocation,
            sapWarehouse: lotWhs,
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
            uploadPanacim: p.UploadPanacim === true,
            sapSendStatus: sapSent,
            sapSent: sapSent,
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
    const headerWhs = (products[0]?.WhsCode ?? "").trim();
    if (headerWhs && !this.sapWarehouseCode.trim()) {
      this.sapWarehouseCode = headerWhs;
    }
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

  private syncRequestHeaderWhsCode(whsCode: string): void {
    if (!this.editingRequestId || !whsCode) {
      return;
    }
    this.generateTemInService
      .updateRequest(this.editingRequestId, { WhsCode: whsCode })
      .subscribe({
        error: (err: unknown) => {
          console.error("[syncRequestHeaderWhsCode] Lỗi:", err);
        },
      });
  }

  private updateRequestWithPoNumber(): void {
    if (!this.editingRequestId) {
      return;
    }
    const po = this.poNumber.trim() || this.getEffectivePoNumber();
    const totalQty = this.dataSource.data.reduce(
      (sum, row) => sum + (row.quantity ?? 0),
      0,
    );
    const lotCount = this.countChildLots();
    const orderWhsCode = this.normalizeWarehouseCode(this.sapWarehouseCode);
    this.generateTemInService
      .updateRequest(this.editingRequestId, {
        vendor: this.vendorCode,
        vendorName: this.vendorName,
        userData5: po || "-",
        createdBy: this.currentUser,
        numberProduction: lotCount,
        totalQuantity: totalQty,
        status: "Bản nháp",
        createdDate: new Date().toISOString().slice(0, 10),
        type: true,
        entryDate: new Date(Date.now() + 86400000).toISOString().slice(0, 10),
        WhsCode: orderWhsCode || undefined,
      })
      .subscribe({
        next: (res) => {
          if (res.success) {
            console.log(
              "[updateRequestWithPoNumber] Thành công:",
              res.message,
              "userData5:",
              po,
            );
            if (po && po !== "-" && !this.poNumber) {
              this.poNumber = po;
            }
          }
        },
        error: (err) => {
          console.error("[updateRequestWithPoNumber] Lỗi:", err);
        },
      });
  }

  private updateRequestWithPoNumberFromModal(po: string): void {
    if (!this.editingRequestId) {
      return;
    }
    const totalQty = this.dataSource.data.reduce(
      (sum, row) => sum + (row.quantity ?? 0),
      0,
    );
    const lotCount = this.countChildLots();
    const orderWhsCode = this.normalizeWarehouseCode(this.sapWarehouseCode);
    this.generateTemInService
      .updateRequest(this.editingRequestId, {
        vendor: this.vendorCode,
        vendorName: this.vendorName,
        userData5: po,
        createdBy: this.currentUser,
        numberProduction: lotCount,
        totalQuantity: totalQty,
        status: "Bản nháp",
        createdDate: new Date().toISOString().slice(0, 10),
        type: true,
        entryDate: new Date(Date.now() + 86400000).toISOString().slice(0, 10),
        WhsCode: orderWhsCode || undefined,
      })
      .subscribe({
        next: (res) => {
          if (res.success) {
            console.log(
              "[updateRequestWithPoNumberFromModal] Thành công - userData5:",
              po,
            );
            if (this.poNumber !== po) {
              this.poNumber = po;
            }
          }
        },
        error: (err) => {
          console.error("[updateRequestWithPoNumberFromModal] Lỗi:", err);
        },
      });
  }
}
