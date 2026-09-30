import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";
import {
  ChoiceDialogData,
  openChoiceDialog,
} from "../../shared/choice-dialog/choice-dialog.component";
import { PalletMngtService } from "app/entities/pallet-management/list/pallet-mngt.service";

/** Trạng thái pallet: chưa có thùng / đang chứa thùng */
type PalletStatus = "UNUSED" | "IN_USE";

/** Bản ghi pallet-mngt (payload PUT /pallet-mngts/{id}) — khai báo local tránh eslint any từ type-import */
interface PalletRecord {
  id: number;
  serialPallet: string;
  locationName: string | null;
  status: string;
  note: string | null;
  createAt: string | null;
  createBy: string | null;
  updatedAt: string;
  updatedBy: string;
}
import { MatAutocompleteTrigger } from "@angular/material/autocomplete";
import { ManagerTemNccService } from "app/entities/list-material/services/info-tem-ncc.service";
import {
  ReceivingSuppliesService,
  SapOwhsDto,
  WarehouseLocation,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";
import { forkJoin, Observable, of, retry, take, timer } from "rxjs";
import { catchError, map, switchMap } from "rxjs/operators";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { downloadVendorImportSampleExcel } from "../../shared/import-sample.util";
import {
  isVendorQrFieldMapped,
  normalizeVendorDateToYyyyMmDd,
  parseVendorQrByMappingConfig,
  VendorQrMappingConfig,
} from "../../shared/vendor-qr-mapping.util";
import {
  CreateVendorLabelInfoPayload,
  PalletDetailDto,
  DeliveryNotificationDetailDto,
  SapPor1R1Dto,
  VendorLabelInfoDto,
  InfoTemNccService,
  toText,
} from "../../services/info-tem-ncc.service";
import {
  ScanBoxRow,
  ScanImportDialogData,
  ScanImportDialogResult,
  ScanImportMode,
  ScanListTab,
  ScanPalletRow,
} from "./scan-import.models";

/** Pallet đang quét trong phiên — khai báo local để tránh eslint any từ type-import */
interface ActivePalletSession {
  palletCode: string;
  scanningBoxCount: number;
}

/** Mobile — Chi tiết thông tin (local types — tránh eslint any từ type-import) */
type MobileInfoStep = "scan" | "pos" | "materials" | "lots";

interface MobileInfoBox {
  id: string;
  code: string;
  quantity: number;
  vendor: string;
  mfgDate: string;
  palletCode: string;
  missingInfo: boolean;
  /** id vendor-label-info — dùng cho PUT */
  recordId: number;
}

interface MobileInfoLot {
  id: string;
  lotNumber: string;
  boxCount: number;
  totalQty: number;
  complete: boolean;
  boxes: MobileInfoBox[];
  quantity: number | null;
  po: string;
  location: string;
  warehouseCode: string;
  mfgDate: string;
  userData4: string;
  msl: string;
  rankAp: string;
  rankQuang: string;
  rankMau: string;
  hsd: string;
  expiryMode: "month" | "year";
  expiryOffset: number | null;
}

interface MobileInfoMaterial {
  id: string;
  materialCode: string;
  reelHint: string;
  materialName: string;
  boxCount: number;
  warehouseCode: string;
  poQty: number;
  location: string;
  receivedQty: number;
  lotCount: number;
  complete: boolean;
  lots: MobileInfoLot[];
}

interface MobileInfoPo {
  id: string;
  poCode: string;
  warehouseKeeper: string;
  vendorName: string;
  vehicleNumber: string;
  materialCount: number;
  boxCount: number;
  receivedQty: number;
  totalQty: number;
  status: "waiting" | "importing" | "done";
  materials: MobileInfoMaterial[];
}

/** Dòng vật tư trong đơn (id = sapPor1Id) */
interface ParentItem {
  id: number;
  partNumber: string;
  sapCode: string;
  orderQty: number;
  materialName: string;
  poCode?: string;
}

/** Local copy — tránh eslint any từ type-import */
interface ScenarioOption {
  id: number;
  vendorCode: string;
  vendorName: string;
  mappingConfig: string;
}

@Component({
  selector: "jhi-scan-import-dialog",
  templateUrl: "./scan-import-dialog.component.html",
  styleUrls: ["./scan-import-dialog.component.scss"],
  standalone: false,
})
export class ScanImportDialogComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  @ViewChild("palletInputRef") palletInputRef?: ElementRef<HTMLInputElement>;
  @ViewChild("boxInputRef") boxInputRef?: ElementRef<HTMLInputElement>;
  @ViewChild("locationInputRef")
  locationInputRef?: ElementRef<HTMLInputElement>;
  @ViewChild("importFileInput")
  importFileInput?: ElementRef<HTMLInputElement>;

  readonly data: ScanImportDialogData;

  mode: ScanImportMode = "scan";
  listTab: ScanListTab = "box";

  /** Kịch bản scan (editable trên mobile) — mặc định = mã vendor đơn */
  scenarioCodeValue = "";
  scenarioOptions: ScenarioOption[] = [];
  filteredScenarioOptions: ScenarioOption[] = [];
  isLoadingScenarios = false;

  palletCode = "";
  boxCode = "";
  locationCode = "";

  /** Pallet đang quét — hiện card theo thiết kế */
  activePallet: ActivePalletSession | null = null;

  boxRows: ScanBoxRow[] = [];
  palletRows: ScanPalletRow[] = [];

  selectedFileName = "";
  isDragging = false;

  /** Mobile — Chi tiết thông tin */
  mobileInfoStep: MobileInfoStep = "scan";
  infoPos: MobileInfoPo[] = [];
  selectedInfoPo: MobileInfoPo | null = null;
  selectedInfoMaterial: MobileInfoMaterial | null = null;
  editingLot: MobileInfoLot | null = null;
  unassignedMaterialCount = 0;
  /** Vật tư chưa xác định PO (sapPor1Id rỗng) — gom thành 1 nhóm, vào thẳng màn LOT */
  unassignedMaterial: MobileInfoMaterial | null = null;
  isLoadingInfo = false;
  isSavingLot = false;
  /** Đang kiểm tra / tạo / gỡ pallet vừa quét */
  isCheckingPallet = false;
  /** Date cho mat-datepicker của form LOT (ô nhập vẫn là chuỗi dd/MM/yyyy) */
  lotEditMfgPickerDate: Date | null = null;
  lotEditHsdPickerDate: Date | null = null;
  /** true khi đang xem LOT từ chip "Vật tư chưa có PO" */
  infoLotsFromUnassigned = false;
  readonly monthOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  readonly yearOptions = [1, 2, 3, 4, 5];

  /** LOT form — vị trí / mã kho */
  filteredLocationOptions: WarehouseLocation[] = [];
  lastLocationSearchTerm = "";
  locationSearchPending = false;
  locationSearchSettled = false;
  sapWarehouseList: SapOwhsDto[] = [];
  filteredSapWarehouseList: SapOwhsDto[] = [];
  isLoadingSapWarehouses = false;
  /** Đã tải xong /owhs (thành công hoặc hết 3 lần thử) — không tự gọi lại */
  sapWarehouseLoadDone = false;

  /** Số request lưu thùng đang chờ */
  savingCount = 0;

  private currentUser = "";
  /** ReelID đã có trong đơn + đã scan/đang lưu trong phiên — chặn trùng */
  private usedReelIds = new Set<string>();
  /** Bản ghi thùng đầy đủ theo id dòng — dùng cho PUT / DELETE */
  private boxRecords = new Map<string, VendorLabelInfoDto>();
  /** Bản ghi pallet-mngt theo mã (để PUT trạng thái), key = serialPallet */
  private palletRecords = new Map<string, PalletRecord>();
  /** Bản ghi thùng cho màn Chi tiết thông tin (key = id bản ghi) */
  private infoRecords = new Map<number, VendorLabelInfoDto>();
  /** Thay đổi đã "Áp dụng" nhưng chưa gửi — key = id bản ghi, value = payload PUT đầy đủ */
  private pendingInfoEdits = new Map<number, VendorLabelInfoDto>();
  private lastInfoDetail: DeliveryNotificationDetailDto | null = null;
  private lastInfoBoxes: VendorLabelInfoDto[] = [];
  /** Part number lấy từ OITM theo mã SAP (key = sapCode) */
  private partsBySapCode = new Map<string, string[]>();
  private selectedFile: File | null = null;
  private expandedPalletIds = new Set<string>();
  private expandedLotIds = new Set<string>();
  private readonly mobileBreakpoint = 768;
  private readonly SCENARIO_DISPLAY_LIMIT = 50;
  private locationSearchSeq = 0;
  private locationSearchTimer: ReturnType<typeof setTimeout> | null = null;
  private activeLocationTrigger: MatAutocompleteTrigger | null = null;

  constructor(
    private dialogRef: MatDialogRef<
      ScanImportDialogComponent,
      ScanImportDialogResult | null
    >,
    @Inject(MAT_DIALOG_DATA) data: ScanImportDialogData,
    private managerTemNccService: ManagerTemNccService,
    private receivingService: ReceivingSuppliesService,
    private cdr: ChangeDetectorRef,
    private infoTemNccService: InfoTemNccService,
    private accountService: AccountService,
    private notificationService: NotificationService,
    private dialog: MatDialog,
    private palletMngtService: PalletMngtService,
  ) {
    this.data = {
      poCode: data?.poCode,
      vendorCode: data?.vendorCode,
      warehouse: data?.warehouse,
      vehicleNumber: data?.vehicleNumber,
      contractCode: data?.contractCode,
      scenarioCode: data?.scenarioCode,
      mappingConfig: data?.mappingConfig,
      parentItems: data?.parentItems,
      deliveryNotificationId: data?.deliveryNotificationId ?? null,
      arrivalDate: data?.arrivalDate ?? null,
      existingReelIds: data?.existingReelIds ?? [],
    };
    this.scenarioCodeValue = this.resolveScenario();
    for (const reelId of this.data.existingReelIds ?? []) {
      const id = toText(reelId);
      if (id) {
        this.usedReelIds.add(id);
      }
    }
  }

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "";
      });
    this.loadScenarios();
    // Kho SAP (/owhs) chỉ tải khi mở form "Điền thông tin LOT" — xem openLotEdit()
    this.loadParentPartNumbers();
    this.loadExistingBoxes();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.boxInputRef?.nativeElement?.focus(), 50);
  }

  ngOnDestroy(): void {
    if (this.locationSearchTimer) {
      clearTimeout(this.locationSearchTimer);
      this.locationSearchTimer = null;
    }
  }

  get isMobile(): boolean {
    return (
      typeof window !== "undefined" &&
      window.innerWidth <= this.mobileBreakpoint
    );
  }

  get headerTitle(): string {
    const vehicle = this.data.vehicleNumber;
    if (typeof vehicle === "string" && vehicle.length > 0) {
      return `Số xe ${vehicle}`;
    }
    return "Quét vật tư";
  }

  get headerSubtitle(): string {
    const contract = this.data.contractCode;
    if (typeof contract === "string" && contract.length > 0) {
      return `Hợp đồng ${contract}`;
    }
    return "Scan hoặc Import - hỗ trợ cả mã Thùng và mã Pallet";
  }

  get scenarioCode(): string {
    if (this.scenarioCodeValue.length > 0) {
      return this.scenarioCodeValue;
    }
    return this.resolveScenario();
  }

  get codeCount(): number {
    let nested = 0;
    for (const pallet of this.palletRows) {
      nested += pallet.boxes.length;
    }
    return this.boxRows.length + nested;
  }

  get totalQty(): number {
    let boxQty = 0;
    for (const row of this.boxRows) {
      boxQty += Number(row.quantity ?? 0);
    }
    let palletQty = 0;
    for (const row of this.palletRows) {
      palletQty += Number(row.totalQty ?? 0);
    }
    return boxQty + palletQty;
  }

  get materialCount(): number {
    const parts: string[] = [];
    for (const row of this.boxRows) {
      parts.push(String(row.partNumber ?? ""));
    }
    for (const pallet of this.palletRows) {
      for (const box of pallet.boxes) {
        parts.push(String(box.partNumber ?? ""));
      }
    }
    return new Set(parts.filter((p) => p.length > 0)).size;
  }

  /** Số thùng đã sửa, chờ bấm "Cập nhật" */
  get pendingInfoCount(): number {
    return this.pendingInfoEdits.size;
  }

  /** Mobile: tab Pallet chỉ hiện pallet đang quét (và các thùng của nó); desktop: tất cả */
  get visiblePalletRows(): ScanPalletRow[] {
    if (!this.isMobile) {
      return this.palletRows;
    }
    const code = this.activePallet?.palletCode;
    return code ? this.palletRows.filter((p) => p.palletCode === code) : [];
  }

  get palletCount(): number {
    return this.palletRows.length;
  }

  setMode(mode: ScanImportMode): void {
    this.mode = mode;
    if (mode === "scan") {
      setTimeout(() => this.palletInputRef?.nativeElement?.focus(), 50);
    }
  }

  setListTab(tab: ScanListTab): void {
    this.listTab = tab;
  }

  isPalletExpanded(row: ScanPalletRow): boolean {
    return this.expandedPalletIds.has(row.id);
  }

  togglePallet(row: ScanPalletRow): void {
    if (this.expandedPalletIds.has(row.id)) {
      this.expandedPalletIds.delete(row.id);
    } else {
      this.expandedPalletIds.add(row.id);
    }
    // clone để Angular detect change
    this.expandedPalletIds = new Set(this.expandedPalletIds);
  }

  onPalletKeydown(event: KeyboardEvent): void {
    if (event.key !== "Enter") {
      return;
    }
    event.preventDefault();
    const code = this.palletCode.trim();
    if (!code || this.isCheckingPallet) {
      return;
    }
    this.checkScannedPallet(code);
  }

  onBoxKeydown(event: KeyboardEvent): void {
    if (event.key !== "Enter") {
      return;
    }
    event.preventDefault();
    const code = this.boxCode.trim();
    if (!code) {
      return;
    }
    // Scan thùng → clear ô, giữ focus để quét thùng tiếp (không nhảy sang Vị trí)
    this.submitBoxScan(code);
    this.boxCode = "";
    setTimeout(() => {
      this.boxInputRef?.nativeElement?.focus();
    }, 0);
  }

  onLocationKeydown(event: KeyboardEvent): void {
    if (event.key !== "Enter") {
      return;
    }
    event.preventDefault();
    const code = this.locationCode.trim();
    if (!code) {
      return;
    }
    // Chỉ xử lý khi user tự focus vào ô Vị trí — clear rồi giữ focus tại đây
    this.submitLocationScan(code);
    this.locationCode = "";
    setTimeout(() => {
      this.locationInputRef?.nativeElement?.focus();
    }, 0);
  }

  /** Chuyển sang quét pallet khác */
  switchActivePallet(): void {
    // Bỏ pallet đang quét → danh sách pallet (mobile) ẩn, focus ô pallet để quét pallet khác
    this.activePallet = null;
    this.palletCode = "";
    this.boxCode = "";
    this.locationCode = "";
    this.listTab = "pallet";
    this.focusPalletInput();
  }

  removeBoxRow(row: ScanBoxRow): void {
    this.deleteBox(row, () => {
      this.boxRows = this.boxRows.filter((r) => r.id !== row.id);
    });
  }

  /** Xóa pallet = xóa lần lượt các thùng trong pallet (DELETE từng thùng) */
  removePalletRow(row: ScanPalletRow): void {
    const removePallet = (): void => {
      this.palletRows = this.palletRows.filter((r) => r.id !== row.id);
      this.expandedPalletIds.delete(row.id);
    };
    if (!row.boxes.length) {
      removePallet();
      return;
    }
    this.confirmAction({
      title: "Xóa pallet",
      highlight: row.palletCode,
      message: `Xóa pallet và ${row.boxes.length} thùng bên trong?`,
      confirmText: "Xóa",
      cancelText: "Hủy",
      tone: "danger",
    }).subscribe((ok) => {
      if (!ok) {
        return;
      }
      const boxes = [...row.boxes];
      forkJoin(
        boxes.map((box) =>
          this.deleteBoxRecord(box).pipe(
            map((done) => {
              if (done) {
                this.removeBoxFromPalletLocal(row, box);
              }
              return done;
            }),
          ),
        ),
      ).subscribe((results) => {
        const ok2 = results.filter(Boolean).length;
        const failed = results.length - ok2;
        if (!row.boxes.length) {
          removePallet();
          this.setPalletStatus(row.palletCode, "UNUSED");
        }
        if (!ok2) {
          this.notificationService.error(
            `Xóa pallet "${row.palletCode}" thất bại.`,
          );
        } else if (failed) {
          this.notificationService.warning(
            `Đã xóa ${ok2}/${results.length} thùng, ${failed} thùng lỗi.`,
          );
        } else {
          this.notificationService.success(
            `Đã xóa pallet "${row.palletCode}" (${ok2} thùng).`,
          );
        }
        this.cdr.markForCheck();
      });
    });
  }

  removeBoxFromPallet(pallet: ScanPalletRow, box: ScanBoxRow): void {
    this.deleteBox(box, () => {
      this.removeBoxFromPalletLocal(pallet, box);
      if (!pallet.boxes.length) {
        this.setPalletStatus(pallet.palletCode, "UNUSED");
      }
    });
  }

  onShowPalletQr(_row: ScanPalletRow): void {
    // In / xem QR sẽ nối sau
  }

  onPrintPallet(_row: ScanPalletRow): void {
    // In tem pallet sẽ nối sau
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
    this.selectedFileName = this.selectedFile?.name ?? "";
  }

  openImportFilePicker(): void {
    this.importFileInput?.nativeElement?.click();
  }

  downloadTemplate(): void {
    downloadVendorImportSampleExcel("create_label_import_template.xlsx");
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
    this.selectedFile = event.dataTransfer?.files?.[0] ?? null;
    this.selectedFileName = this.selectedFile?.name ?? "";
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onConfirm(): void {
    this.dialogRef.close({
      mode: this.mode,
      boxRows: [...this.boxRows],
      palletRows: [...this.palletRows],
    });
  }

  onSaveDraft(): void {
    this.dialogRef.close({
      mode: this.mode,
      boxRows: [...this.boxRows],
      palletRows: [...this.palletRows],
    });
  }

  onDetailInfo(): void {
    this.mobileInfoStep = "pos";
    this.selectedInfoPo = null;
    this.selectedInfoMaterial = null;
    this.editingLot = null;
    this.infoLotsFromUnassigned = false;
    this.loadInfoData();
  }

  backFromInfo(): void {
    if (this.editingLot) {
      this.editingLot = null;
      return;
    }
    if (this.mobileInfoStep === "lots") {
      if (this.infoLotsFromUnassigned) {
        this.mobileInfoStep = "pos";
        this.selectedInfoMaterial = null;
        this.infoLotsFromUnassigned = false;
        return;
      }
      this.mobileInfoStep = "materials";
      this.selectedInfoMaterial = null;
      return;
    }
    if (this.mobileInfoStep === "materials") {
      this.mobileInfoStep = "pos";
      this.selectedInfoPo = null;
      return;
    }
    if (this.mobileInfoStep === "pos") {
      this.mobileInfoStep = "scan";
    }
  }

  openInfoPo(po: MobileInfoPo): void {
    this.selectedInfoPo = po;
    this.mobileInfoStep = "materials";
    this.infoLotsFromUnassigned = false;
  }

  openInfoMaterial(mat: MobileInfoMaterial): void {
    this.selectedInfoMaterial = mat;
    this.mobileInfoStep = "lots";
    this.infoLotsFromUnassigned = false;
    this.expandedLotIds = new Set<string>();
    if (mat.lots.length) {
      this.expandedLotIds.add(mat.lots[0].id);
      this.expandedLotIds = new Set(this.expandedLotIds);
    }
  }

  /** Chip "Vật tư chưa có PO" → thẳng màn LOT */
  openUnassignedLots(): void {
    const mat = this.unassignedMaterial;
    if (!mat || !mat.lots.length) {
      this.notificationService.info("Không có vật tư nào chưa xác định PO.");
      return;
    }
    this.selectedInfoPo = null;
    this.selectedInfoMaterial = mat;
    this.infoLotsFromUnassigned = true;
    this.mobileInfoStep = "lots";
    this.expandedLotIds = new Set<string>();
    if (mat.lots.length) {
      this.expandedLotIds.add(mat.lots[0].id);
      this.expandedLotIds = new Set(this.expandedLotIds);
    }
  }

  toggleInfoLot(lot: MobileInfoLot): void {
    if (this.expandedLotIds.has(lot.id)) {
      this.expandedLotIds.delete(lot.id);
    } else {
      this.expandedLotIds.add(lot.id);
    }
    this.expandedLotIds = new Set(this.expandedLotIds);
  }

  isInfoLotExpanded(lot: MobileInfoLot): boolean {
    return this.expandedLotIds.has(lot.id);
  }

  /**
   * Mở form LOT: tự điền từ dữ liệu thùng (giá trị chung của cả LOT).
   * Trống → default: Rank áp/màu/quang = "NO", MSL = "1", UserData4 = "<mã SAP>-<ddMMyyyy MFG>".
   * Vị trí / Mã kho không có dữ liệu thì để trống cho người dùng chọn.
   */
  openLotEdit(lot: MobileInfoLot, event?: Event): void {
    event?.stopPropagation();
    const sapCode = this.distinctText(
      lot.boxes.map((b) => this.infoRecords.get(b.recordId)?.sapCode),
    );
    const mfgKey = toText(lot.mfgDate).replace(/\D/g, "");
    this.editingLot = {
      ...lot,
      boxes: [...lot.boxes],
      rankAp: toText(lot.rankAp) || "NO",
      rankMau: toText(lot.rankMau) || "NO",
      rankQuang: toText(lot.rankQuang) || "NO",
      msl: toText(lot.msl) || "1",
      userData4:
        toText(lot.userData4) ||
        (sapCode && !sapCode.includes(",") && mfgKey.length === 8
          ? `${sapCode}-${mfgKey}`
          : ""),
    };
    this.syncLotEditPickerDates();
    // Lần đầu mở form LOT mới tải danh sách kho SAP (tối đa 3 lần, lỗi mới báo)
    if (!this.sapWarehouseList.length) {
      this.loadSapWarehouses(this.editingLot.warehouseCode);
    }
  }

  closeLotEdit(): void {
    this.editingLot = null;
  }

  /** Chọn ngày trên lịch → điền dd/MM/yyyy vào ô tương ứng */
  onLotEditDatePicked(field: "mfgDate" | "hsd", value: Date | null): void {
    const elot = this.editingLot;
    if (!elot || !value) {
      return;
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    elot[field] =
      `${pad(value.getDate())}/${pad(value.getMonth() + 1)}/${value.getFullYear()}`;
    if (field === "mfgDate") {
      this.recalcLotEditExpiry();
    } else {
      // Chọn HSD tay → bỏ chọn tháng/năm để không bị tính đè
      elot.expiryOffset = null;
    }
    this.syncLotEditPickerDates();
  }

  /** Gõ tay ngày → đồng bộ lịch; đổi MFG thì tính lại HSD */
  onLotEditDateTyped(): void {
    this.recalcLotEditExpiry();
    this.syncLotEditPickerDates();
  }

  /** Form LOT: đổi Tháng/Năm → bỏ số đã chọn nếu vượt danh sách mới, rồi tính lại HSD */
  setLotEditExpiryMode(mode: "month" | "year"): void {
    const elot = this.editingLot;
    if (!elot) {
      return;
    }
    elot.expiryMode = mode;
    const options = mode === "year" ? this.yearOptions : this.monthOptions;
    if (elot.expiryOffset !== null && !options.includes(elot.expiryOffset)) {
      elot.expiryOffset = null;
    }
    this.recalcLotEditExpiry();
  }

  /** Form LOT: HSD = Ngày sản xuất + số tháng/năm đã chọn → điền vào ô Hạn sử dụng */
  recalcLotEditExpiry(): void {
    const elot = this.editingLot;
    if (!elot?.expiryOffset) {
      return;
    }
    const mfg = this.toApiDate(elot.mfgDate);
    if (!/^\d{8}$/.test(mfg)) {
      this.notificationService.warning(
        "Nhập Ngày sản xuất (dd/MM/yyyy) để tự tính Hạn sử dụng.",
      );
      return;
    }
    elot.hsd = this.toDisplayDate(
      this.addExpiry(mfg, elot.expiryMode, Number(elot.expiryOffset)),
    );
    this.syncLotEditPickerDates();
  }

  /**
   * "Áp dụng tất cả thùng": ghi thông tin LOT vào mọi thùng trong LOT (chưa gửi API) —
   * bấm "Cập nhật" mới PUT. Vào từ "Vật tư chưa có PO" thì chỉ các thùng chưa có PO đó.
   */
  applyLotEditToAllBoxes(): void {
    const edited = this.editingLot;
    if (!edited || this.isSavingLot) {
      return;
    }
    const mfg = this.toApiDate(edited.mfgDate);
    let hsd = this.toApiDate(edited.hsd);
    if (edited.expiryOffset && mfg) {
      hsd = this.addExpiry(mfg, edited.expiryMode, edited.expiryOffset);
    }
    const poCode = toText(edited.po);
    const orKeep = (value: unknown, original: unknown): unknown =>
      toText(value) ? toText(value) : original;

    const payloads: VendorLabelInfoDto[] = [];
    for (const box of edited.boxes) {
      const rec = this.infoRecords.get(box.recordId);
      if (!rec?.id) {
        continue;
      }
      // Thùng chưa có PO + đã nhập PO → gán vào dòng vật tư khớp PO + mã SAP/Part
      let sapPor1Id = rec.sapPor1Id;
      if (!sapPor1Id && poCode) {
        sapPor1Id =
          this.findParentByPo(
            poCode,
            toText(rec.sapCode),
            toText(rec.partNumber),
          )?.id ?? null;
      }
      payloads.push({
        ...rec,
        initialQuantity:
          edited.quantity === null || edited.quantity === undefined
            ? rec.initialQuantity
            : Number(edited.quantity),
        userData5: orKeep(poCode, rec.userData5) as string | null,
        subStorageUnit: orKeep(edited.location, rec.subStorageUnit) as
          | string
          | null,
        storageUnit: orKeep(edited.warehouseCode, rec.storageUnit) as
          | string
          | null,
        manufacturingDate: orKeep(mfg, rec.manufacturingDate) as string | null,
        expirationDate: orKeep(hsd, rec.expirationDate) as string | null,
        userData4: orKeep(edited.userData4, rec.userData4) as string | null,
        msdLevel: orKeep(edited.msl, rec.msdLevel) as string | null,
        userData1: orKeep(edited.rankAp, rec.userData1) as string | null,
        userData2: orKeep(edited.rankMau, rec.userData2) as string | null,
        userData3: orKeep(edited.rankQuang, rec.userData3) as string | null,
        sapPor1Id,
        palletBoxMapping: undefined,
      });
    }
    for (const payload of payloads) {
      this.pendingInfoEdits.set(payload.id, payload);
      this.infoRecords.set(payload.id, payload);
    }
    this.editingLot = null;
    if (payloads.length) {
      this.notificationService.info(
        `Đã áp dụng cho ${payloads.length} thùng — bấm "Cập nhật" để lưu.`,
      );
    }
    // Dựng lại màn từ dữ liệu đã áp dụng (số thùng, tổng SL, trạng thái đủ thông tin)
    if (this.lastInfoDetail) {
      this.buildInfoData(this.lastInfoDetail, this.lastInfoBoxes);
    }
  }

  /** Nút "Cập nhật": PUT /vendor-label-infos/{id} cho mọi thùng đã áp dụng thay đổi */
  onUpdateInfo(): void {
    if (this.isSavingLot) {
      return;
    }
    const payloads = [...this.pendingInfoEdits.values()];
    if (!payloads.length) {
      this.notificationService.info("Không có thùng nào thay đổi.");
      return;
    }
    this.isSavingLot = true;
    forkJoin(
      payloads.map((payload) =>
        this.infoTemNccService.updateVendorLabelInfo(payload).pipe(
          map(() => ({ id: payload.id, ok: true })),
          catchError(() => of({ id: payload.id, ok: false })),
        ),
      ),
    ).subscribe((results) => {
      this.isSavingLot = false;
      for (const r of results) {
        if (r.ok) {
          this.pendingInfoEdits.delete(r.id);
        }
      }
      const ok = results.filter((r) => r.ok).length;
      const failed = results.length - ok;
      if (!ok) {
        this.notificationService.error(
          `Cập nhật thất bại ${failed} thùng — vui lòng thử lại.`,
        );
      } else if (failed) {
        this.notificationService.warning(
          `Cập nhật ${ok}/${results.length} thùng thành công, ${failed} thùng lỗi.`,
        );
      } else {
        this.notificationService.success(`Cập nhật thành công ${ok} thùng.`);
      }
      this.loadInfoData();
    });
  }

  infoPoStatusLabel(status: MobileInfoPo["status"]): string {
    if (status === "done") {
      return "Hoàn thành";
    }
    if (status === "importing") {
      return "Đang nhập";
    }
    return "Chờ nhập";
  }

  infoPoProgress(po: MobileInfoPo): number {
    if (!po.totalQty) {
      return 0;
    }
    return Math.min(100, Math.round((po.receivedQty / po.totalQty) * 100));
  }

  boxMetaLine(row: ScanBoxRow): string {
    const date = this.formatMfg(row.mfgDate);
    return `${row.vendor} - ${row.lot} - ${date}`;
  }

  infoBoxMeta(box: MobileInfoBox): string {
    return `Số lượng: ${box.quantity} - ${box.vendor} - - ${box.mfgDate} - ${box.palletCode}`;
  }

  formatMfg(value: string): string {
    const raw = (value ?? "").trim();
    if (/^\d{8}$/.test(raw)) {
      return `${raw.slice(6, 8)}/${raw.slice(4, 6)}/${raw.slice(0, 4)}`;
    }
    return raw;
  }

  trackBox(_: number, row: ScanBoxRow): string {
    return String(row.id);
  }

  trackPallet(_: number, row: ScanPalletRow): string {
    return String(row.id);
  }

  trackInfoPo(_: number, row: MobileInfoPo): string {
    return String(row.id);
  }

  trackInfoMat(_: number, row: MobileInfoMaterial): string {
    return String(row.id);
  }

  trackInfoLot(_: number, row: MobileInfoLot): string {
    return String(row.id);
  }

  trackInfoBox(_: number, row: MobileInfoBox): string {
    return String(row.id);
  }

  // ==================== Scenario autocomplete ====================

  onScenarioSearch(value: string): void {
    const lower = (value ?? "").toLowerCase().trim();
    const filtered = lower
      ? this.scenarioOptions.filter(
          (s) =>
            s.vendorName.toLowerCase().includes(lower) ||
            s.vendorCode.toLowerCase().includes(lower),
        )
      : this.scenarioOptions;
    this.filteredScenarioOptions = filtered.slice(
      0,
      this.SCENARIO_DISPLAY_LIMIT,
    );
  }

  onScenarioSelected(scenario: ScenarioOption): void {
    this.scenarioCodeValue = scenario.vendorCode;
    this.data.scenarioCode = scenario.vendorCode;
    try {
      this.data.mappingConfig = JSON.parse(scenario.mappingConfig);
    } catch {
      this.data.mappingConfig = null;
    }
  }

  onScenarioSelectedByCode(code: string): void {
    const matched = this.scenarioOptions.find((s) => s.vendorCode === code);
    if (matched) {
      this.onScenarioSelected(matched);
      return;
    }
    this.scenarioCodeValue = (code ?? "").trim();
    this.data.mappingConfig = null;
  }

  // ==================== Location / warehouse autocomplete ====================

  onLotLocationSearch(
    keyword: string,
    trigger?: MatAutocompleteTrigger | null,
  ): void {
    if (this.locationSearchTimer) {
      clearTimeout(this.locationSearchTimer);
    }
    if (trigger) {
      this.activeLocationTrigger = trigger;
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
          this.cdr.detectChanges();
          this.reopenLocationPanel();
        })
        .catch(() => {
          if (seq !== this.locationSearchSeq) {
            return;
          }
          this.filteredLocationOptions = [];
          this.lastLocationSearchTerm = term;
          this.locationSearchPending = false;
          this.locationSearchSettled = true;
          this.cdr.detectChanges();
          this.reopenLocationPanel();
        });
    }, 200);
  }

  showLocationNoResults(): boolean {
    return (
      this.locationSearchSettled &&
      !this.locationSearchPending &&
      this.lastLocationSearchTerm.length > 0 &&
      this.filteredLocationOptions.length === 0
    );
  }

  onLotLocationSelected(selected: string | WarehouseLocation): void {
    if (!this.editingLot) {
      return;
    }
    this.editingLot.location = this.resolveLocationName(selected);
  }

  onLotWarehouseSearch(keyword: string): void {
    // Chưa có danh sách kho → tải nền (không khóa ô nhập)
    if (!this.sapWarehouseList.length && !this.isLoadingSapWarehouses) {
      this.loadSapWarehouses(keyword);
    }
    const term = toText(keyword).toLowerCase();
    this.filteredSapWarehouseList = term
      ? this.sapWarehouseList.filter(
          (whs) =>
            toText(whs.whsCode).toLowerCase().includes(term) ||
            toText(whs.whsName).toLowerCase().includes(term),
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

  onLotWarehouseSelected(code: string): void {
    if (!this.editingLot) {
      return;
    }
    this.editingLot.warehouseCode = (code ?? "").trim();
  }

  /**
   * Xử lý mã thùng vừa quét — cùng logic tách chuỗi với scan-item-dialog cũ:
   * tách QR theo kịch bản → đối chiếu vật tư trong đơn → POST vendor-label-infos
   * → lưu thành công mới hiện lên bảng.
   */
  private submitBoxScan(rawCode: string): void {
    const mappingConfig = this.data.mappingConfig as
      | VendorQrMappingConfig
      | null
      | undefined;
    if (!mappingConfig?.fieldMappings?.length) {
      this.notificationService.warning(
        "Chưa chọn kịch bản scan — không tách được mã QR.",
      );
      return;
    }
    const deliveryNotificationId = this.data.deliveryNotificationId ?? null;
    if (deliveryNotificationId === null) {
      this.notificationService.warning(
        "Chưa có đơn giao hàng — không thể lưu thông tin thùng.",
      );
      return;
    }

    const fieldMap = parseVendorQrByMappingConfig(rawCode, mappingConfig);
    const mapped = (key: string, ...aliases: string[]): string => {
      if (!isVendorQrFieldMapped(mappingConfig, key)) {
        return "";
      }
      return this.firstNonEmpty(
        fieldMap[key],
        ...aliases.map((a) => fieldMap[a]),
      );
    };

    const scannedPartNumber = toText(fieldMap["partNumber"]);
    const scannedSap = toText(fieldMap["sapCode"]);
    const matchedRow = this.findParentRow(scannedPartNumber, scannedSap);

    // Không khớp vật tư/PO nào trong đơn → vẫn lưu (sapPor1Id = null),
    // hiện ở "Vật tư chưa có PO" để gán PO sau
    const noPoMatched = (this.data.parentItems ?? []).length > 0 && !matchedRow;

    const reelId = toText(fieldMap["reelId"]);
    if (!reelId) {
      this.notificationService.warning(
        "Không tách được ReelID từ mã vừa quét.",
      );
      return;
    }
    if (this.usedReelIds.has(reelId)) {
      this.notificationService.warning(
        `ReelID "${reelId}" đã được scan trước đó.`,
      );
      return;
    }
    const quantity = Number(fieldMap["initialQuantity"]) || 0;
    if (quantity <= 0) {
      this.notificationService.warning(
        `Số lượng không hợp lệ (${quantity}) — mã "${reelId}" không được lưu.`,
      );
      return;
    }

    let manufacturingDate = mapped("manufacturingDate")
      ? normalizeVendorDateToYyyyMmDd(fieldMap["manufacturingDate"])
      : "";
    if (
      !manufacturingDate &&
      isVendorQrFieldMapped(mappingConfig, "manufacturingDate")
    ) {
      manufacturingDate = normalizeVendorDateToYyyyMmDd(
        this.data.arrivalDate ? this.toIsoDate(this.data.arrivalDate) : "",
      );
    }
    const expirationDate = mapped("expirationDate")
      ? normalizeVendorDateToYyyyMmDd(fieldMap["expirationDate"])
      : "";
    const lot =
      mapped("lotNumber", "lot") ||
      (scannedPartNumber && manufacturingDate
        ? `${scannedPartNumber}${manufacturingDate}`
        : "");
    const vendor = this.firstNonEmpty(fieldMap["vendor"], this.data.vendorCode);
    const orNull = (v: string): string | null => (v ? v : null);

    const payload: CreateVendorLabelInfoPayload = {
      reelId,
      partNumber: orNull(scannedPartNumber),
      vendor: orNull(vendor),
      lot: orNull(lot),
      userData1: orNull(mapped("userData1")),
      userData2: orNull(mapped("userData2")),
      userData3: orNull(mapped("userData3")),
      userData4: orNull(mapped("userData4")),
      userData5: orNull(
        this.firstNonEmpty(
          fieldMap["userData5"],
          fieldMap["poNumber"],
          matchedRow?.poCode,
          this.data.poCode,
        ),
      ),
      initialQuantity: quantity,
      msdLevel: orNull(mapped("msl", "msdLevel")),
      msdInitialFloorTime: null,
      msdBagSealDate: null,
      marketUsage: null,
      quantityOverride: Number(fieldMap["quantityOverride"]) || null,
      shelfTime: null,
      spMaterialName: orNull(mapped("spMaterialName")),
      warningLimit: null,
      maximumLimit: null,
      comments: null,
      warmupTime: null,
      storageUnit: orNull(mapped("storageUnit")),
      subStorageUnit: null,
      locationOverride: orNull(mapped("locationOverride")),
      expirationDate: orNull(expirationDate),
      manufacturingDate: orNull(manufacturingDate),
      partClass: null,
      sapCode: orNull(this.firstNonEmpty(matchedRow?.sapCode, scannedSap)),
      vendorQrCode: rawCode,
      status: null,
      createdBy: orNull(this.currentUser),
      createdAt: null,
      updatedBy: null,
      updatedAt: null,
      vendorAdditionalData: null,
      panaSendStatus: null,
      sapSendStatus: null,
      deliveryNotificationId,
      sapPor1Id: matchedRow?.id ?? null,
    };

    // Đang có pallet → POST /vendor-label-infos/pallet; không có → POST /vendor-label-infos
    const palletCode = this.activePallet?.palletCode ?? null;
    const save$: Observable<VendorLabelInfoDto | undefined> = palletCode
      ? this.infoTemNccService
          .createPalletVendorLabelInfos({
            serialPallet: palletCode,
            vendorLabelInfoList: [payload],
          })
          .pipe(
            map(
              (list): VendorLabelInfoDto | undefined =>
                list.find((r) => toText(r.reelId) === reelId) ?? list[0],
            ),
          )
      : this.infoTemNccService
          .createVendorLabelInfo(payload)
          .pipe(map((res): VendorLabelInfoDto | undefined => res ?? undefined));

    this.usedReelIds.add(reelId);
    this.savingCount++;
    save$.subscribe({
      next: (saved) => {
        this.savingCount--;
        // Backend không trả bản ghi → giữ payload đã gửi (không có id thì không PUT/DELETE được)
        const record: VendorLabelInfoDto = {
          ...payload,
          ...(saved ?? {}),
          id: saved?.id ?? 0,
          serialPallet: palletCode,
        };
        const row = this.toBoxRow(record, this.nowTime());
        if (noPoMatched) {
          this.notificationService.warning(
            `Thùng "${reelId}" chưa xác định được PO — xem ở "Vật tư chưa có PO".`,
          );
        }
        if (palletCode) {
          this.addBoxToPallet(palletCode, row);
          this.setPalletStatus(palletCode, "IN_USE");
        } else {
          this.boxRows = [row, ...this.boxRows];
          this.listTab = "box";
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.savingCount--;
        this.usedReelIds.delete(reelId);
        this.notificationService.error(`Lưu thùng "${reelId}" thất bại.`);
        this.cdr.markForCheck();
      },
    });
  }

  /**
   * Quét mã pallet:
   *  - chưa có trong DS quản lý → popup [Quét mã khác] / [Xác nhận tạo pallet]
   *  - đã có, chưa có thùng → dùng luôn, focus ô thùng
   *  - đã có thùng → popup [Quét mã pallet khác] / [Gỡ thùng khỏi pallet]
   */
  private checkScannedPallet(code: string): void {
    this.isCheckingPallet = true;
    this.infoTemNccService.getPalletBySerial(code).subscribe({
      next: (pallet) => {
        this.isCheckingPallet = false;
        if (!pallet) {
          this.askCreatePallet(code);
          return;
        }
        this.rememberPallet(code, pallet);
        const boxes = pallet.vendorLabelInfoList ?? [];
        if (!boxes.length) {
          this.setPalletStatus(code, "UNUSED");
          this.activatePallet(code);
          return;
        }
        this.setPalletStatus(code, "IN_USE");
        this.askRemoveBoxesFromPallet(code, pallet);
      },
      error: () => {
        this.isCheckingPallet = false;
        this.notificationService.error(
          `Không kiểm tra được pallet "${code}" — vui lòng thử lại.`,
        );
        this.focusPalletInput(false);
      },
    });
  }

  private askCreatePallet(code: string): void {
    this.confirmAction({
      title: "Pallet chưa có trong danh sách",
      highlight: code,
      message: "Mã pallet này chưa có trong danh sách quản lý. Tạo pallet mới?",
      confirmText: "Tạo pallet",
      cancelText: "Quét mã khác",
      tone: "info",
    }).subscribe((ok) => {
      if (!ok) {
        this.focusPalletInput();
        return;
      }
      const now = new Date().toISOString();
      this.isCheckingPallet = true;
      this.palletMngtService
        .createPallet({
          serialPallet: code,
          status: "UNUSED",
          note: "",
          createAt: now,
          createBy: this.currentUser,
          updatedAt: now,
          updatedBy: this.currentUser,
        })
        .subscribe({
          next: (created) => {
            this.isCheckingPallet = false;
            if (created.id) {
              this.palletRecords.set(code, {
                id: created.id,
                serialPallet: created.serialPallet || code,
                locationName: created.locationName,
                status: created.status ?? "UNUSED",
                note: created.note,
                createAt: created.createAt,
                createBy: created.createBy,
                updatedAt: created.updatedAt ?? now,
                updatedBy: created.updatedBy ?? this.currentUser,
              });
            }
            // Đảm bảo pallet mới ở trạng thái chưa sử dụng
            this.setPalletStatus(code, "UNUSED");
            this.notificationService.success(`Đã tạo pallet "${code}".`);
            this.activatePallet(code);
          },
          error: () => {
            this.isCheckingPallet = false;
            this.notificationService.error(`Tạo pallet "${code}" thất bại.`);
            this.focusPalletInput(false);
          },
        });
    });
  }

  private askRemoveBoxesFromPallet(
    code: string,
    pallet: PalletDetailDto,
  ): void {
    const boxes = pallet.vendorLabelInfoList ?? [];
    this.confirmAction({
      title: "Pallet đã có thùng",
      highlight: code,
      message: `Pallet đang chứa ${boxes.length} thùng. Gỡ hết thùng để quét lại, hoặc quét pallet khác.`,
      confirmText: "Gỡ thùng",
      cancelText: "Quét pallet khác",
      tone: "warning",
    }).subscribe((ok) => {
      if (!ok) {
        this.focusPalletInput();
        return;
      }
      // id gỡ = palletBoxMapping.id của thùng (thiếu thì dùng palletBoxMappingId)
      const mappingIds: number[] = [];
      for (const b of boxes) {
        const raw: unknown = b.palletBoxMapping?.id ?? b.palletBoxMappingId;
        const id = Number(raw);
        if (Number.isFinite(id) && id > 0) {
          mappingIds.push(id);
        }
      }
      if (!mappingIds.length) {
        this.notificationService.warning(
          "Không tìm thấy liên kết thùng–pallet để gỡ.",
        );
        this.focusPalletInput(false);
        return;
      }
      this.isCheckingPallet = true;
      const deletes: Array<Observable<boolean>> = [];
      for (const id of mappingIds) {
        const request: Observable<boolean> = this.infoTemNccService
          .deletePalletBoxMapping(id)
          .pipe(
            map((): boolean => true),
            catchError(() => of(false)),
          );
        deletes.push(request);
      }
      forkJoin(deletes).subscribe((results: boolean[]) => {
        this.isCheckingPallet = false;
        const removed = results.filter(Boolean).length;
        const failed = results.length - removed;
        // Tải lại thùng của đơn: thùng đã gỡ trở thành thùng lẻ
        this.reloadScannedBoxes();
        if (failed) {
          this.notificationService.error(
            `Gỡ được ${removed}/${results.length} thùng, ${failed} thùng lỗi — pallet chưa trống.`,
          );
          this.focusPalletInput(false);
          return;
        }
        this.notificationService.success(
          `Đã gỡ ${removed} thùng khỏi pallet "${code}".`,
        );
        this.setPalletStatus(code, "UNUSED");
        this.activatePallet(code);
      });
    });
  }

  /** Ghi nhớ bản ghi pallet-mngt lấy từ GET serial-pallet (để PUT trạng thái) */
  private rememberPallet(code: string, pallet: PalletDetailDto): void {
    if (!pallet.id) {
      return;
    }
    this.palletRecords.set(code, {
      id: pallet.id,
      serialPallet: toText(pallet.serialPallet) || code,
      locationName: pallet.locationName ?? null,
      status: toText(pallet.status),
      note: pallet.note ?? null,
      createAt: pallet.createAt ?? null,
      createBy: pallet.createBy ?? null,
      updatedAt: toText(pallet.updatedAt),
      updatedBy: toText(pallet.updatedBy),
    });
  }

  /**
   * PUT /api/pallet-mngts/{id} đổi trạng thái pallet (UNUSED / IN_USE).
   * Đã đúng trạng thái → bỏ qua; chưa có bản ghi → GET theo mã trước.
   * Đặt trạng thái lạc quan để quét nhiều thùng liên tiếp không PUT lặp.
   */
  private setPalletStatus(code: string, status: PalletStatus): void {
    const cached = this.palletRecords.get(code);
    if (cached && toText(cached.status).toUpperCase() === status) {
      return;
    }
    const previous = cached ? cached.status : null;
    if (cached) {
      cached.status = status;
    }
    const record$: Observable<PalletRecord | null> = cached
      ? of(cached)
      : this.infoTemNccService.getPalletBySerial(code).pipe(
          map((pallet): PalletRecord | null => {
            if (!pallet) {
              return null;
            }
            this.rememberPallet(code, pallet);
            return this.palletRecords.get(code) ?? null;
          }),
        );

    record$
      .pipe(
        switchMap((rec: PalletRecord | null): Observable<boolean> => {
          if (!rec?.id) {
            return of(false);
          }
          const body: PalletRecord = {
            ...rec,
            status,
            updatedAt: new Date().toISOString(),
            updatedBy: this.currentUser,
          };
          const put$: Observable<boolean> =
            this.infoTemNccService.updatePalletMngt(body);
          return put$.pipe(
            map((): boolean => {
              this.palletRecords.set(code, body);
              return true;
            }),
          );
        }),
        catchError(() => of(false)),
      )
      .subscribe((ok: boolean) => {
        if (ok) {
          return;
        }
        const rec = this.palletRecords.get(code);
        if (rec && previous !== null) {
          rec.status = previous;
        }
        this.notificationService.warning(
          `Không cập nhật được trạng thái pallet "${code}".`,
        );
      });
  }

  /** Đặt pallet đang quét, chỉ hiện pallet này ở tab Pallet, focus ô thùng */
  private activatePallet(code: string): void {
    let row = this.palletRows.find((p) => p.palletCode === code);
    if (!row) {
      row = {
        id: `pallet-${code}`,
        time: this.nowTime(),
        palletCode: code,
        createdBy: this.currentUser,
        boxCount: 0,
        materialTypeCount: 0,
        totalQty: 0,
        note: "",
        warehouseCode: "",
        locationLabel: "Chưa gán",
        usageLabel: "",
        statusLabel: "Chưa hoàn thành",
        boxes: [],
      };
      this.palletRows = [row, ...this.palletRows];
    }
    this.expandedPalletIds = new Set([...this.expandedPalletIds, row.id]);
    this.activePallet = {
      palletCode: code,
      scanningBoxCount: row.boxes.length,
    };
    this.palletCode = code;
    this.listTab = "pallet";
    this.boxCode = "";
    this.cdr.markForCheck();
    setTimeout(() => {
      this.boxInputRef?.nativeElement?.focus();
      this.boxInputRef?.nativeElement?.select();
    }, 0);
  }

  /** Focus ô pallet để quét mã khác (mặc định xóa mã đang có) */
  private focusPalletInput(clear = true): void {
    if (clear) {
      this.palletCode = "";
    }
    this.cdr.markForCheck();
    setTimeout(() => {
      this.palletInputRef?.nativeElement?.focus();
      this.palletInputRef?.nativeElement?.select();
    }, 50);
  }

  /** Làm mới danh sách thùng / pallet từ API (sau khi gỡ thùng khỏi pallet) */
  private reloadScannedBoxes(): void {
    this.boxRows = [];
    this.palletRows = [];
    this.boxRecords.clear();
    this.loadExistingBoxes();
  }

  /** Mở dialog → load các thùng đã scan trước đó của đơn */
  private loadExistingBoxes(): void {
    const deliveryId = this.data.deliveryNotificationId ?? null;
    if (deliveryId === null) {
      return;
    }
    this.infoTemNccService.getVendorLabelInfosByDelivery(deliveryId).subscribe({
      next: (records) => {
        const standalone: ScanBoxRow[] = [];
        const pallets = new Map<string, ScanBoxRow[]>();
        for (const rec of records) {
          const reelId = toText(rec.reelId);
          if (reelId) {
            this.usedReelIds.add(reelId);
          }
          const row = this.toBoxRow(rec, this.formatTime(rec.createdAt));
          const serial = toText(
            rec.serialPallet ?? rec.palletBoxMapping?.serialPallet,
          );
          if (serial) {
            pallets.set(serial, [...(pallets.get(serial) ?? []), row]);
          } else {
            standalone.push(row);
          }
        }
        // Giữ các thùng vừa scan trong lúc đang load (nếu có)
        const loadedIds = new Set(standalone.map((r) => r.id));
        this.boxRows = [
          ...this.boxRows.filter((r) => !loadedIds.has(r.id)),
          ...standalone,
        ];
        pallets.forEach((boxes, serial) => {
          for (const box of [...boxes].reverse()) {
            if (
              !this.palletRows.some((p) => p.boxes.some((b) => b.id === box.id))
            ) {
              this.addBoxToPallet(serial, box, false);
            }
          }
        });
        this.cdr.markForCheck();
      },
      error: () => {
        this.notificationService.error(
          "Không tải được danh sách thùng đã scan.",
        );
      },
    });
  }

  /**
   * Scan vị trí → gán cho các thùng chưa có vị trí (thuộc pallet đang quét, hoặc thùng lẻ
   * nếu không quét pallet): PUT /vendor-label-infos/{id} từng thùng với subStorageUnit = vị trí.
   */
  private submitLocationScan(location: string): void {
    const onPalletTab = this.listTab === "pallet";
    const activeCode = this.activePallet?.palletCode ?? "";
    if (onPalletTab && !activeCode) {
      this.notificationService.warning("Quét mã pallet trước khi quét vị trí.");
      return;
    }
    // Tab Pallet: mọi thùng của pallet đang quét (không động tới pallet khác);
    // tab Thùng: các thùng lẻ chưa có vị trí
    const pending: ScanBoxRow[] = onPalletTab
      ? (this.palletRows.find((p) => p.palletCode === activeCode)?.boxes ?? [])
      : this.boxRows.filter((b) => !toText(b.location));
    if (!pending.length) {
      this.notificationService.warning(
        onPalletTab
          ? `Pallet "${activeCode}" chưa có thùng nào.`
          : "Không có thùng nào chưa gán vị trí.",
      );
      return;
    }

    let ok = 0;
    let failed = 0;
    const done = (): void => {
      if (ok + failed < pending.length) {
        return;
      }
      if (!ok) {
        this.notificationService.error(
          `Gán vị trí "${location}" thất bại cho ${failed} thùng.`,
        );
      } else if (failed) {
        this.notificationService.warning(
          `Gán vị trí "${location}": ${ok} thùng thành công, ${failed} thùng lỗi.`,
        );
      } else {
        this.notificationService.success(
          `Đã gán vị trí "${location}" cho ${ok} thùng.`,
        );
      }
      if (onPalletTab && ok) {
        const pallet = this.palletRows.find((p) => p.palletCode === activeCode);
        if (pallet) {
          pallet.locationLabel = location;
        }
      }
      this.cdr.markForCheck();
    };

    for (const box of pending) {
      const record = this.boxRecords.get(box.id);
      if (!record?.id) {
        failed++;
        done();
        continue;
      }
      const body: VendorLabelInfoDto = {
        ...record,
        subStorageUnit: location,
        palletBoxMapping: undefined,
      };
      this.savingCount++;
      this.infoTemNccService.updateVendorLabelInfo(body).subscribe({
        next: (saved) => {
          this.savingCount--;
          this.boxRecords.set(box.id, { ...body, ...(saved ?? {}) });
          box.location = location;
          ok++;
          done();
        },
        error: () => {
          this.savingCount--;
          failed++;
          done();
        },
      });
    }
  }

  private removeBoxFromPalletLocal(
    pallet: ScanPalletRow,
    box: ScanBoxRow,
  ): void {
    pallet.boxes = pallet.boxes.filter((b) => b.id !== box.id);
    pallet.boxCount = pallet.boxes.length;
    if (this.activePallet?.palletCode === pallet.palletCode) {
      this.activePallet = {
        ...this.activePallet,
        scanningBoxCount: pallet.boxCount,
      };
    }
    pallet.totalQty = pallet.boxes.reduce(
      (s: number, b: ScanBoxRow): number => s + Number(b.quantity ?? 0),
      0,
    );
    pallet.materialTypeCount = new Set(
      pallet.boxes
        .map((b) => String(b.partNumber ?? ""))
        .filter((p) => p.length > 0),
    ).size;
  }

  /** Hỏi xác nhận (modal) → DELETE /vendor-label-infos/{id}; thành công mới bỏ khỏi bảng */
  private deleteBox(box: ScanBoxRow, onDeleted: () => void): void {
    if (!this.boxRecords.get(box.id)?.id) {
      onDeleted();
      return;
    }
    this.confirmAction({
      title: "Xóa thùng",
      highlight: box.reelId,
      message: "Thùng sẽ bị xóa khỏi đơn. Tiếp tục?",
      confirmText: "Xóa",
      cancelText: "Hủy",
      tone: "danger",
    }).subscribe((ok) => {
      if (!ok) {
        return;
      }
      this.deleteBoxRecord(box).subscribe((done) => {
        if (done) {
          onDeleted();
          this.notificationService.success(`Đã xóa thùng "${box.reelId}".`);
        } else {
          this.notificationService.error(`Xóa thùng "${box.reelId}" thất bại.`);
        }
        this.cdr.markForCheck();
      });
    });
  }

  /** DELETE 1 thùng (không hỏi, không thông báo) → true nếu thành công */
  private deleteBoxRecord(box: ScanBoxRow): Observable<boolean> {
    const record = this.boxRecords.get(box.id);
    if (!record?.id) {
      return of(true);
    }
    return this.infoTemNccService.deleteVendorLabelInfo(record.id).pipe(
      map(() => {
        this.boxRecords.delete(box.id);
        this.usedReelIds.delete(toText(box.reelId));
        return true;
      }),
      catchError(() => of(false)),
    );
  }

  /** Modal xác nhận gọn (thay window.confirm) — vừa màn hình mobile */
  /** Modal lựa chọn 2 nút (thay window.confirm) — gọn, dễ bấm trên mobile */
  private confirmAction(data: ChoiceDialogData): Observable<boolean> {
    return openChoiceDialog(this.dialog, data);
  }

  /** Dựng dòng hiển thị từ bản ghi thùng + lưu bản ghi để PUT/DELETE */
  private toBoxRow(rec: VendorLabelInfoDto, time: string): ScanBoxRow {
    const reelId = toText(rec.reelId);
    const id = `box-${rec.id || reelId}`;
    this.boxRecords.set(id, rec);
    return {
      id,
      dbId: rec.id || undefined,
      time,
      lot: toText(rec.lot),
      reelId,
      partNumber: toText(rec.partNumber),
      vendor: toText(rec.vendor),
      quantity: Number(rec.initialQuantity ?? 0),
      mfgDate: toText(rec.manufacturingDate),
      hsd: toText(rec.expirationDate),
      location: toText(rec.subStorageUnit),
      sapCode: toText(rec.sapCode),
    };
  }

  private nowTime(): string {
    return new Date().toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  }

  private formatTime(value: unknown): string {
    const d = new Date(toText(value));
    return toText(value) && !isNaN(d.getTime())
      ? d.toLocaleTimeString("vi-VN", {
          hour: "2-digit",
          minute: "2-digit",
          second: "2-digit",
        })
      : "";
  }

  /** Thùng scan khi đang có pallet → gom vào dòng pallet tương ứng */
  /** fromScan = false khi dựng lại từ dữ liệu đã lưu: không mở rộng pallet, không đổi tab */
  private addBoxToPallet(
    palletCode: string,
    box: ScanBoxRow,
    fromScan = true,
  ): void {
    let pallet = this.palletRows.find((p) => p.palletCode === palletCode);
    if (!pallet) {
      pallet = {
        id: `pallet-${palletCode}`,
        time: box.time,
        palletCode,
        createdBy: this.currentUser,
        boxCount: 0,
        materialTypeCount: 0,
        totalQty: 0,
        note: "",
        warehouseCode: "",
        locationLabel: "Chưa gán",
        usageLabel: "",
        statusLabel: "Chưa hoàn thành",
        boxes: [],
      };
      this.palletRows = fromScan
        ? [pallet, ...this.palletRows]
        : [...this.palletRows, pallet];
      if (fromScan) {
        this.expandedPalletIds = new Set([
          ...this.expandedPalletIds,
          pallet.id,
        ]);
      }
    }
    pallet.boxes = [box, ...pallet.boxes];
    pallet.boxCount = pallet.boxes.length;
    pallet.totalQty = pallet.boxes.reduce(
      (s: number, b: ScanBoxRow): number => s + Number(b.quantity ?? 0),
      0,
    );
    pallet.materialTypeCount = new Set(
      pallet.boxes
        .map((b: ScanBoxRow): string =>
          String(b.sapCode ? b.sapCode : (b.partNumber ?? "")),
        )
        .filter((p: string) => p.length > 0),
    ).size;
    if (this.activePallet?.palletCode === palletCode) {
      this.activePallet = {
        ...this.activePallet,
        scanningBoxCount: pallet.boxCount,
      };
    }
    if (fromScan) {
      this.listTab = "pallet";
    }
  }

  /**
   * Tìm dòng vật tư trong đơn: Part QR ↔ Part (OITM) → Part QR ↔ mã SAP → SAP QR ↔ mã SAP.
   * Giống findParentRowForImport của scan-item-dialog.
   */
  private findParentRow(
    partNumber: string,
    sapCode: string,
  ): ParentItem | undefined {
    const parents: ParentItem[] = this.data.parentItems ?? [];
    const norm = (v: unknown): string => toText(v).toLowerCase();
    const part = norm(partNumber);
    if (part) {
      const byPart = parents.find((r) => this.partsOf(r).includes(part));
      if (byPart) {
        return byPart;
      }
      const byPartVsSap = parents.find(
        (r) => !!norm(r.sapCode) && norm(r.sapCode) === part,
      );
      if (byPartVsSap) {
        return byPartVsSap;
      }
    }
    const sap = norm(sapCode);
    if (!sap) {
      return undefined;
    }
    return parents.find((r) => !!norm(r.sapCode) && norm(r.sapCode) === sap);
  }

  /** Tất cả part number (lowercase) của 1 vật tư: từ trang cha + từ API OITM */
  private partsOf(r: ParentItem): string[] {
    const fromParent = toText(r.partNumber)
      .split(",")
      .map((p) => p.trim().toLowerCase())
      .filter(Boolean);
    const fromApi = (this.partsBySapCode.get(toText(r.sapCode)) ?? []).map(
      (p) => p.toLowerCase(),
    );
    return [...fromParent, ...fromApi];
  }

  /** Lấy part number OITM cho các vật tư chưa có part (service cache theo mã SAP) */
  private loadParentPartNumbers(): void {
    const codes = new Set<string>();
    for (const p of this.data.parentItems ?? []) {
      const code = toText(p.sapCode);
      if (code && !toText(p.partNumber)) {
        codes.add(code);
      }
    }
    codes.forEach((code) => {
      this.infoTemNccService
        .getPartNumbersBySapCode(code)
        .subscribe((parts) => {
          this.partsBySapCode.set(code, parts);
        });
    });
  }

  private firstNonEmpty(...values: unknown[]): string {
    for (const value of values) {
      const t = toText(value);
      if (t) {
        return t;
      }
    }
    return "";
  }

  private toIsoDate(value: string | Date): string {
    if (value instanceof Date) {
      if (isNaN(value.getTime())) {
        return "";
      }
      const pad = (n: number): string => String(n).padStart(2, "0");
      return `${value.getFullYear()}-${pad(value.getMonth() + 1)}-${pad(value.getDate())}`;
    }
    return value;
  }

  private resolveScenario(): string {
    const scenario = this.data.scenarioCode;
    if (typeof scenario === "string" && scenario.trim().length > 0) {
      return scenario.trim();
    }
    const vendor = this.data.vendorCode;
    if (typeof vendor === "string" && vendor.trim().length > 0) {
      return vendor.trim();
    }
    return "";
  }

  private loadScenarios(): void {
    this.isLoadingScenarios = true;
    this.managerTemNccService.getTemIdentificationScenarios().subscribe({
      next: (data) => {
        const list: ScenarioOption[] = [];
        for (const item of data) {
          list.push({
            id: Number(item.id),
            vendorCode: String(item.vendorCode ?? ""),
            vendorName: String(item.vendorName ?? ""),
            mappingConfig: String(item.mappingConfig ?? ""),
          });
        }
        this.scenarioOptions = list;
        this.filteredScenarioOptions = list.slice(
          0,
          this.SCENARIO_DISPLAY_LIMIT,
        );
        // Chưa có mappingConfig từ trang ngoài → tự áp kịch bản khớp mã mặc định
        if (!this.data.mappingConfig && this.scenarioCodeValue) {
          const matched = list.find(
            (s) => s.vendorCode === this.scenarioCodeValue,
          );
          if (matched) {
            this.onScenarioSelected(matched);
          }
        }
        this.isLoadingScenarios = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoadingScenarios = false;
        this.cdr.markForCheck();
      },
    });
  }

  /**
   * /api/owhs: gọi tối đa 3 lần (lần đầu + 2 lần thử lại, cách nhau 3–5s).
   * Vẫn lỗi → snackbar báo lỗi, không tự gọi lại nữa trong phiên dialog.
   */
  private loadSapWarehouses(keyword = ""): void {
    if (this.isLoadingSapWarehouses || this.sapWarehouseLoadDone) {
      return;
    }
    this.isLoadingSapWarehouses = true;
    this.receivingService
      .getSapWarehousesOrError()
      .pipe(
        retry({
          count: 2,
          delay: () => timer(3000 + Math.floor(Math.random() * 2000)),
        }),
      )
      .subscribe({
        next: (data) => {
          this.sapWarehouseList = data ?? [];
          this.isLoadingSapWarehouses = false;
          this.sapWarehouseLoadDone = true;
          // Lọc theo nội dung ô đang nhập (nếu form LOT đang mở) hoặc từ khóa ban đầu
          this.onLotWarehouseSearch(this.editingLot?.warehouseCode ?? keyword);
          this.cdr.markForCheck();
        },
        error: () => {
          this.isLoadingSapWarehouses = false;
          this.sapWarehouseLoadDone = true;
          this.notificationService.error(
            "Không tải được thông tin kho — có thể nhập tay mã kho.",
          );
          this.cdr.markForCheck();
        },
      });
  }

  private resolveLocationName(selected: string | WarehouseLocation): string {
    if (selected == null) {
      return "";
    }
    if (typeof selected !== "string") {
      const name = selected.locationName ?? selected.locationFullName ?? "";
      return name.trim();
    }
    const trimmed = selected.trim();
    const matched = this.filteredLocationOptions.find(
      (w) => w.locationFullName === trimmed || w.locationName === trimmed,
    );
    return matched?.locationName ?? trimmed;
  }

  private reopenLocationPanel(): void {
    setTimeout(() => {
      const panelTrigger = this.activeLocationTrigger;
      if (panelTrigger && !panelTrigger.panelOpen) {
        panelTrigger.openPanel();
      }
    });
  }

  private syncLotEditPickerDates(): void {
    this.lotEditMfgPickerDate = this.parseAnyDate(
      toText(this.editingLot?.mfgDate),
    );
    this.lotEditHsdPickerDate = this.parseAnyDate(toText(this.editingLot?.hsd));
  }

  /** Chi tiết đơn (PO / vật tư) + toàn bộ thùng đã scan (kể cả thùng chưa có PO) */
  private loadInfoData(): void {
    const deliveryId = this.data.deliveryNotificationId ?? null;
    if (deliveryId === null) {
      this.infoPos = [];
      this.unassignedMaterial = null;
      this.unassignedMaterialCount = 0;
      return;
    }
    this.isLoadingInfo = true;
    forkJoin({
      detail: this.infoTemNccService.getDeliveryNotificationDetail(deliveryId),
      boxes: this.infoTemNccService.getVendorLabelInfosByDelivery(deliveryId),
    }).subscribe({
      next: ({ detail, boxes }) => {
        this.isLoadingInfo = false;
        this.lastInfoDetail = detail;
        this.lastInfoBoxes = boxes;
        this.buildInfoData(detail, boxes);
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoadingInfo = false;
        this.notificationService.error(
          "Không tải được chi tiết thông tin đơn.",
        );
      },
    });
  }

  private buildInfoData(
    detail: DeliveryNotificationDetailDto,
    allBoxes: VendorLabelInfoDto[],
  ): void {
    this.infoRecords.clear();
    const lines = detail.sapPor1R1List ?? [];
    const lineIds = new Set(lines.map((l) => l.id));

    // Thùng theo dòng vật tư (sapPor1Id); không có / không thuộc đơn → chưa có PO
    const boxesByLine = new Map<number, VendorLabelInfoDto[]>();
    const orphans: VendorLabelInfoDto[] = [];
    const source = allBoxes.length
      ? allBoxes
      : lines.flatMap((l) => l.vendorLabelInfoList ?? []);
    for (const raw of source) {
      // Thùng đã "Áp dụng" nhưng chưa gửi → hiển thị theo giá trị đã sửa
      const b = this.pendingInfoEdits.get(raw.id) ?? raw;
      this.infoRecords.set(b.id, b);
      const lineId = b.sapPor1Id ?? null;
      if (lineId !== null && lineIds.has(lineId)) {
        boxesByLine.set(lineId, [...(boxesByLine.get(lineId) ?? []), b]);
      } else {
        orphans.push(b);
      }
    }

    // PO = docEntry
    const linesByPo = new Map<string, SapPor1R1Dto[]>();
    for (const l of lines) {
      const po = toText(l.docEntry);
      linesByPo.set(po, [...(linesByPo.get(po) ?? []), l]);
    }

    const pos: MobileInfoPo[] = [];
    linesByPo.forEach((poLines, poCode) => {
      const materials = poLines.map((l) =>
        this.buildInfoMaterial(
          `mat-${l.id}`,
          toText(l.itemCode),
          toText(l.dscription),
          toText(l.whsCode),
          Number(l.quantity ?? 0),
          boxesByLine.get(l.id) ?? [],
          poCode,
        ),
      );
      const receivedQty = materials.reduce((s, m) => s + m.receivedQty, 0);
      const totalQty = materials.reduce((s, m) => s + m.poQty, 0);
      let status: MobileInfoPo["status"] = "waiting";
      if (totalQty > 0 && receivedQty >= totalQty) {
        status = "done";
      } else if (receivedQty > 0) {
        status = "importing";
      }
      pos.push({
        id: `po-${poCode}`,
        poCode: poCode || "—",
        warehouseKeeper:
          this.distinctText(poLines.map((l) => l.whsCode)) || "—",
        vendorName: toText(detail.vendorName),
        vehicleNumber: toText(detail.contNo),
        materialCount: materials.length,
        boxCount: materials.reduce((s, m) => s + m.boxCount, 0),
        receivedQty,
        totalQty,
        status,
        materials,
      });
    });
    this.infoPos = pos;

    this.unassignedMaterial = orphans.length
      ? this.buildInfoMaterial(
          "mat-unassigned",
          this.distinctText(orphans.map((b) => b.sapCode)),
          "Vật tư chưa có PO",
          "",
          0,
          orphans,
          "",
        )
      : null;
    this.unassignedMaterialCount = new Set(
      orphans.map((b) => toText(b.sapCode) || toText(b.partNumber)),
    ).size;

    // Giữ màn đang xem sau khi tải lại
    if (this.selectedInfoPo) {
      this.selectedInfoPo =
        this.infoPos.find((p) => p.id === this.selectedInfoPo?.id) ?? null;
    }
    if (this.selectedInfoMaterial) {
      const matId = this.selectedInfoMaterial.id;
      this.selectedInfoMaterial = this.infoLotsFromUnassigned
        ? this.unassignedMaterial
        : (this.selectedInfoPo?.materials.find((m) => m.id === matId) ?? null);
      if (!this.selectedInfoMaterial && this.mobileInfoStep === "lots") {
        this.mobileInfoStep = this.selectedInfoPo ? "materials" : "pos";
      }
    }
  }

  /** Vật tư → LOT (gom theo lot) → thùng; đếm số thùng + tổng SL theo từng LOT */
  private buildInfoMaterial(
    id: string,
    materialCode: string,
    materialName: string,
    whsCode: string,
    poQty: number,
    boxes: VendorLabelInfoDto[],
    poCode: string,
  ): MobileInfoMaterial {
    const byLot = new Map<string, VendorLabelInfoDto[]>();
    for (const b of boxes) {
      const lot = toText(b.lot);
      byLot.set(lot, [...(byLot.get(lot) ?? []), b]);
    }
    const lots: MobileInfoLot[] = [];
    byLot.forEach((lotBoxes, lotNumber) => {
      const infoBoxes: MobileInfoBox[] = lotBoxes.map((b) => ({
        id: `box-${b.id}`,
        code: toText(b.reelId),
        quantity: Number(b.initialQuantity ?? 0),
        vendor: toText(b.vendor),
        mfgDate: this.toDisplayDate(b.manufacturingDate),
        palletCode: toText(b.serialPallet) || "—",
        missingInfo: !this.isBoxComplete(b),
        recordId: b.id,
      }));
      const common = (pick: (b: VendorLabelInfoDto) => unknown): string => {
        const set = new Set(lotBoxes.map((b) => toText(pick(b))));
        return set.size === 1 ? [...set][0] : "";
      };
      const qtySet = new Set(
        lotBoxes.map((b) => Number(b.initialQuantity ?? 0)),
      );
      lots.push({
        id: `${id}-lot-${lotNumber}`,
        lotNumber: lotNumber || "—",
        boxCount: infoBoxes.length,
        totalQty: infoBoxes.reduce((s, b) => s + b.quantity, 0),
        complete: infoBoxes.every((b) => !b.missingInfo),
        boxes: infoBoxes,
        quantity: qtySet.size === 1 ? [...qtySet][0] : null,
        po: poCode || common((b) => b.userData5),
        location: common((b) => b.subStorageUnit),
        warehouseCode: common((b) => b.storageUnit),
        mfgDate: this.toDisplayDate(common((b) => b.manufacturingDate)),
        userData4: common((b) => b.userData4),
        msl: common((b) => b.msdLevel),
        rankAp: common((b) => b.userData1),
        rankMau: common((b) => b.userData2),
        rankQuang: common((b) => b.userData3),
        hsd: this.toDisplayDate(common((b) => b.expirationDate)),
        expiryMode: "month",
        expiryOffset: null,
      });
    });

    const partHint =
      (this.partsBySapCode.get(materialCode) ?? []).join(", ") ||
      this.distinctText(boxes.map((b) => b.partNumber));
    return {
      id,
      materialCode: materialCode || "—",
      reelHint: partHint || "—",
      materialName,
      boxCount: boxes.length,
      warehouseCode:
        whsCode || this.distinctText(boxes.map((b) => b.storageUnit)) || "—",
      poQty,
      location:
        this.distinctText(boxes.map((b) => b.subStorageUnit)) || "Chưa gán",
      receivedQty: boxes.reduce(
        (s, b) => s + Number(b.initialQuantity ?? 0),
        0,
      ),
      lotCount: lots.length,
      complete: lots.length > 0 && lots.every((l) => l.complete),
      lots,
    };
  }

  /** Đủ thông tin: MFG, HSD, vị trí, mã kho */
  private isBoxComplete(b: VendorLabelInfoDto): boolean {
    return !!(
      toText(b.manufacturingDate) &&
      toText(b.expirationDate) &&
      toText(b.subStorageUnit) &&
      toText(b.storageUnit)
    );
  }

  /** Dòng vật tư trong đơn theo mã PO + mã SAP (hoặc Part) */
  private findParentByPo(
    poCode: string,
    sapCode: string,
    partNumber: string,
  ): ParentItem | undefined {
    const po = poCode.toLowerCase();
    const candidates = (this.data.parentItems ?? []).filter(
      (p) => toText(p.poCode).toLowerCase() === po,
    );
    const bySap = candidates.find(
      (p) =>
        !!sapCode && toText(p.sapCode).toLowerCase() === sapCode.toLowerCase(),
    );
    if (bySap) {
      return bySap;
    }
    const part = partNumber.toLowerCase();
    return part
      ? candidates.find((p) => this.partsOf(p).includes(part))
      : undefined;
  }

  private distinctText(values: unknown[]): string {
    return [...new Set(values.map((v) => toText(v)).filter(Boolean))].join(
      ", ",
    );
  }

  /** yyyyMMdd / yyyy-MM-dd / dd/MM/yyyy → dd/MM/yyyy */
  private toDisplayDate(value: unknown): string {
    const d = this.parseAnyDate(toText(value));
    if (!d) {
      return toText(value);
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
  }

  /** → yyyyMMdd (định dạng lưu khi scan) */
  private toApiDate(value: unknown): string {
    const d = this.parseAnyDate(toText(value));
    if (!d) {
      return toText(value);
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`;
  }

  private addExpiry(
    mfgYyyyMmDd: string,
    mode: "month" | "year",
    offset: number,
  ): string {
    const d = this.parseAnyDate(mfgYyyyMmDd);
    if (!d) {
      return "";
    }
    if (mode === "year") {
      d.setFullYear(d.getFullYear() + offset);
    } else {
      d.setMonth(d.getMonth() + offset);
    }
    const pad = (n: number): string => String(n).padStart(2, "0");
    return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`;
  }

  private parseAnyDate(raw: string): Date | null {
    const slash = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(raw);
    if (slash) {
      return new Date(Number(slash[3]), Number(slash[2]) - 1, Number(slash[1]));
    }
    const digits = raw.replace(/\D/g, "");
    if (digits.length >= 8 && Number(digits.slice(0, 4)) > 1900) {
      return new Date(
        Number(digits.slice(0, 4)),
        Number(digits.slice(4, 6)) - 1,
        Number(digits.slice(6, 8)),
      );
    }
    return null;
  }
}
