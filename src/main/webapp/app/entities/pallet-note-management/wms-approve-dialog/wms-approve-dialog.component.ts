import {
  ChangeDetectorRef,
  Component,
  computed,
  ElementRef,
  HostListener,
  Inject,
  OnInit,
  QueryList,
  signal,
  ViewChild,
  ViewChildren,
} from "@angular/core";
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
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatTabsModule } from "@angular/material/tabs";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatSpinner } from "@angular/material/progress-spinner";
import { SelectionModel } from "@angular/cdk/collections";
import { firstValueFrom } from "rxjs";
import { MatCheckboxChange } from "@angular/material/checkbox";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../confirm-dialog/confirm-dialog.component";
import { PlanningWorkOrderService } from "../service/planning-work-order.service";
import { HttpResponse } from "@angular/common/http";
import { PalletDetailData } from "../detail-pallet-dialog/detail-pallet-dialog.component";
import { QRCodeComponent } from "angularx-qrcode";

// ==================== INTERFACES ====================

export interface WmsDialogData {
  maLenhSanXuatId: number;
  preloadedPallets: any;
  warehouseNoteInfo: any;
  warehouseNoteInfoDetails: any;
  serialBoxPalletMappings?: Array<{
    serial_box: string;
    serial_pallet: string;
  }>;
}

export interface PalletDialogItem {
  id: number;
  stt: number;
  qrCode: string;
  palletCode: string;
  scanProgress: string;
  capacity: number;
  isSent: boolean;

  // Hiển thị thêm
  productName?: string;
  lotNumber?: string;

  // Payload fields
  poNumber?: string;
  customerName?: string;
  productionDecisionNumber?: string;
  itemNoSku?: string;
  dateCode?: string;
  productionDate?: string;
  note?: string;
  quantityPerBox?: number;
  numBoxActual?: number;

  totalBoxesCount?: number;
  sentBoxesCount?: number;
  sentBoxesPercent?: number;
}

export interface WarehouseNoteResponse {
  warehouse_note_info: any;
  warehouse_note_info_details: any[];
  serial_box_pallet_mappings: any[];
  pallet_infor_details: any[];
}
export interface WmsBox {
  box_code: string; // tên trường theo spec WMS
  quantity: number;
  note?: string;
}

export interface WmsPalletItem {
  serial_pallet: string;
  po_number?: string;
  inventory_code?: string;
  customer_name?: string;
  production_decision_number?: string;
  item_no_sku?: string;
  date_code?: string;
  list_serial_items?: string;
  note?: string;
  list_box: WmsBox[];
}

export interface WmsGeneralInfo {
  client_id: string;
  inventory_code?: string;
  inventory_name?: string;
  wo_code?: string;
  production_date?: string;
  lot_number?: string;
  note?: string;
  created_by?: string;
  branch?: string;
  production_team?: string;
  number_of_pallet: number;
  number_of_box: number;
  quantity: number;
  destination_warehouse?: number;
  pallet_note_creation_id?: number;
  list_pallet: WmsPalletItem[];
}

export interface WmsPayload {
  general_info: WmsGeneralInfo;
}
type BoxDetail = Record<string, any>;
type MappingItem = { serial_box: string; serial_pallet: string };
type BoxDetailMap = Map<string, BoxDetail>;
type BoxToPalletMap = Map<string, string>;
type PalletAggregate = {
  totalBoxes: number;
  sentBoxes: number;
  boxSerials: string[];
};
type PalletAggregatesMap = Map<string, PalletAggregate>;
// ==================== COMPONENT ====================

@Component({
  selector: "jhi-pallet-wms-approval-dialog",
  templateUrl: "./wms-approve-dialog.component.html",
  styleUrls: ["./wms-approve-dialog.component.scss"],
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatCheckboxModule,
    MatTabsModule,
    QRCodeComponent,
    MatSpinner,
  ],
})
export class WmsApproveDialogComponent implements OnInit {
  // Data
  pallets = signal<PalletDialogItem[]>([]);
  selection = new SelectionModel<PalletDialogItem>(true, []);

  // Computed
  pendingPallets = computed(() => this.pallets().filter((p) => !p.isSent));
  sentPallets = computed(() => this.pallets().filter((p) => p.isSent));

  // UI State
  displayedColumnsPending: string[] = [
    "select",
    "stt",
    "qr",
    "info",
    "progress",
    "capacity",
    "sentBoxes",
  ];
  displayedColumnsSent: string[] = [
    "stt",
    "qr",
    "info",
    "progress",
    "capacity",
  ];

  mobileTabIndex = 0;
  isMobile = false;
  isLoading = false;
  scanMode = false;

  productName = "";
  @ViewChildren("scanInput", { read: ElementRef }) scanInputRefs!: QueryList<
    ElementRef<HTMLInputElement>
  >;
  // Context data từ API
  private warehouseNoteInfo: any;
  private warehouseNoteInfoDetails: any;
  private palletDetails: any[] = [];
  private boxDetails: any[] = [];
  private mappings: Array<{ serial_box: string; serial_pallet: string }> = [];
  private serialBoxPalletMappings: Array<{
    serial_box: string;
    serial_pallet: string;
  }> = [];
  private palletAggregates: Map<
    string,
    { totalBoxes: number; sentBoxes: number; boxSerials: string[] }
  > = new Map();
  private boxDetailBySerial: Map<string, any> = new Map();
  private boxToPallet: Map<string, string> = new Map();

  constructor(
    public dialogRef: MatDialogRef<WmsApproveDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: WmsDialogData,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private planningService: PlanningWorkOrderService,
    private cdr: ChangeDetectorRef,
  ) {
    if (data.warehouseNoteInfo) {
      this.warehouseNoteInfo = data.warehouseNoteInfo;
    }
    if (data.warehouseNoteInfoDetails) {
      this.warehouseNoteInfoDetails = data.warehouseNoteInfoDetails;
    }

    // nhận mapping từ data (hỗ trợ cả snake_case và camelCase)
    if (Array.isArray((data as any).serial_box_pallet_mappings)) {
      this.mappings = (data as any).serial_box_pallet_mappings;
    } else if (Array.isArray((data as any).serialBoxPalletMappings)) {
      this.mappings = (data as any).serialBoxPalletMappings;
    }

    const { boxDetailBySerial, boxToPallet, palletAggregates } =
      this.buildBoxAggregates(this.warehouseNoteInfoDetails, this.mappings);
    this.boxDetailBySerial = boxDetailBySerial;
    this.boxToPallet = boxToPallet;
    this.palletAggregates = palletAggregates;

    // lưu vào component để dùng khi map
    if (
      Array.isArray(data?.preloadedPallets) &&
      data.preloadedPallets.length > 0
    ) {
      console.log("Using preloaded pallets:", data.preloadedPallets);
      const mapped = this.mapDetailDataToDialogItems(data.preloadedPallets);
      this.pallets.set(mapped);

      // Lưu raw data để build payload
      this.warehouseNoteInfo = {
        ...(this.warehouseNoteInfo ?? {}),
        id: data.maLenhSanXuatId,
      };
      this.warehouseNoteInfoDetails = {
        ...(this.warehouseNoteInfoDetails ?? {}),
        id: data.maLenhSanXuatId,
      };
    }
    // FALLBACK: Load data trong dialog (không có preload)
    else {
      console.warn("No preloaded data, loading inside dialog");
      // Sẽ gọi trong ngOnInit
    }
  }

  async ngOnInit(): Promise<void> {
    this.checkMobile();
    if (this.pallets().length === 0) {
      await this.loadData();
    }
  }

  // ==================== SEND APPROVAL ====================

  sendApproval(): void {
    const selectedPallets = this.selection.selected;

    if (selectedPallets.length === 0) {
      this.snackBar.open("Vui lòng chọn ít nhất 1 pallet", "Đóng", {
        duration: 2000,
      });
      return;
    }

    // Confirm dialog
    const dialogData: ConfirmDialogData = {
      title: "Xác nhận gửi",
      message: `Xác nhận gửi ${selectedPallets.length} pallet?`,
      confirmText: "Xác nhận",
      cancelText: "Hủy",
      type: "info",
    };

    const confirmRef = this.dialog.open(ConfirmDialogComponent, {
      width: "500px",
      data: dialogData,
      disableClose: false,
    });

    // subscribe callback returns void (no async here)
    confirmRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) {
        return;
      }
      // gọi hàm async bên ngoài mà không trả Promise trong callback
      this.performSendApproval(selectedPallets);
    });
  }

  // ==================== UI HELPERS ====================

  @HostListener("window:resize")
  onResize(): void {
    this.checkMobile();
  }

  checkMobile(): void {
    this.isMobile = window.innerWidth < 768;
  }

  computeProgressWidth(scanProgress: string): string {
    if (!scanProgress || !scanProgress.includes("/")) {
      return "0%";
    }

    const parts = scanProgress.split("/");
    const current = parseInt(parts[0].trim(), 10) || 0;
    const total = parseInt(parts[1].trim(), 10) || 1;

    if (total === 0) {
      return "0%";
    }

    const pct = Math.min(Math.round((current / total) * 100), 100);
    return `${pct}%`;
  }

  isProgressFull(row: PalletDialogItem): boolean {
    if (!row?.scanProgress || !row.scanProgress.includes("/")) {
      return false;
    }

    const parts = row.scanProgress.split("/");
    const num = Number(parts[0].trim()) || 0;
    const den = Number(parts[1].trim()) || 0;

    return den > 0 && num >= den;
  }

  isAllSelected(): boolean {
    const selectable = this.pendingPallets().filter((p) =>
      this.isProgressFull(p),
    );
    return (
      selectable.length > 0 &&
      this.selection.selected.length === selectable.length
    );
  }

  toggleAllRows(): void {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      const selectable = this.pendingPallets().filter((p) =>
        this.isProgressFull(p),
      );
      this.selection.select(...selectable);
    }
  }
  // ==================== CLOSE ====================

  close(): void {
    // const result = {
    //   sentPallets: this.sentPallets(),
    //   unsentPallets: this.pendingPallets(),
    //   allPallets: this.pallets(),
    // };

    this.dialogRef.close();
  }

  onRowCheckboxChange(event: MatCheckboxChange, row: PalletDialogItem): void {
    if (!this.isProgressFull(row)) {
      return;
    }

    if (this.selection.isSelected(row)) {
      this.selection.deselect(row);
    } else {
      this.selection.select(row);
    }
  }

  // ==================== SCAN MODE ====================

  toggleScan(): void {
    this.scanMode = !this.scanMode;

    if (this.scanMode) {
      // focus input visible (desktop hoặc mobile)
      requestAnimationFrame(() => {
        const refs = this.scanInputRefs?.toArray() ?? [];
        // tìm input đang hiển thị
        const visibleRef = refs.find((r) => this.isVisible(r.nativeElement));
        const el = visibleRef?.nativeElement ?? refs[0]?.nativeElement;
        if (el) {
          el.value = "";
          el.focus();
          try {
            el.setSelectionRange(0, 0);
          } catch (e) {
            /* ignore */
          }
        } else {
          console.warn("No visible scan input found");
        }
      });
    } else {
      // blur tất cả input
      (this.scanInputRefs?.toArray() ?? []).forEach((r) => {
        try {
          r.nativeElement.blur();
        } catch (e) {
          /* ignore */
        }
      });
    }
  }

  onScanInput(event: Event): void {
    const inputEl = event.target as HTMLInputElement;
    if (!inputEl) {
      return;
    }

    const raw = inputEl.value ?? "";
    // Nếu scanner paste xong và kèm newline, hoặc đủ độ dài, xử lý ngay
    // Tùy bạn: kiểm tra pattern hoặc độ dài
    const trimmed = raw.trim();
    if (!trimmed) {
      return;
    }

    // ví dụ: nếu scanner không gửi Enter, nhưng paste xong, bạn muốn xử lý tự động
    // điều kiện xử lý: có newline hoặc độ dài >= N (tùy mã)
    if (raw.includes("\n") || raw.includes("\r") || trimmed.length >= 6) {
      // loại bỏ newline
      const code = trimmed.replace(/[\r\n]+/g, "");
      this.processScannedCode(code);
      inputEl.value = "";
      // giữ focus
      setTimeout(() => {
        if (this.scanMode) {
          inputEl.focus();
          try {
            inputEl.setSelectionRange(0, 0);
          } catch (e) {
            // một số input/keyboard không hỗ trợ setSelectionRange
            console.warn("setSelectionRange failed after input", e);
          }
        }
      }, 50);
    }
  }

  onScanEnter(event: KeyboardEvent): void {
    const inputEl = event.target as HTMLInputElement;
    if (!inputEl) {
      return;
    }

    const raw = inputEl.value?.trim();
    if (raw) {
      this.processScannedCode(raw);
    }

    inputEl.value = "";
    setTimeout(() => {
      if (this.scanMode) {
        inputEl.focus();
      }
    }, 50);

    event.preventDefault();
  }
  // ==================== UPDATE UI ====================

  private updateUIAfterSend(sentPallets: PalletDialogItem[]): void {
    const sentIds = sentPallets.map((p) => p.id);

    this.pallets.update((current) =>
      current.map((p) => (sentIds.includes(p.id) ? { ...p, isSent: true } : p)),
    );

    this.selection.clear();
  }
  private processScannedCode(code: string): void {
    // Lấy mảng thực tế mà template đang hiển thị
    const list = this.pendingPallets();

    // Tìm object trong chính mảng đó (đảm bảo cùng reference với bảng)
    const found = list.find(
      (p: any) =>
        p.qrCode === code ||
        p.palletCode === code ||
        String(p.id) === String(code),
    );

    if (!found) {
      this.snackBar.open(
        `Mã ${code} không nằm trong danh sách chưa gửi`,
        "Đóng",
        { duration: 1500 },
      );
      return;
    }

    // Select object từ mảng hiển thị (cùng reference) — sẽ tick checkbox
    if (!this.selection.isSelected(found)) {
      this.selection.select(found);
      this.cdr.detectChanges(); // đảm bảo UI cập nhật ngay
    }
  }

  // ==================== API 1: UPDATE STATUS ====================

  private async updatePalletStatus(pallets: PalletDialogItem[]): Promise<void> {
    const payloadArray = pallets.map((p) => ({
      id: p.id,
      wms_send_status: true,
      updated_by: this.warehouseNoteInfo?.approver_by || "admin",
    }));

    console.log("pallet", pallets);

    try {
      await firstValueFrom(
        this.planningService.updatePalletWmsStatus(payloadArray),
      );
      console.log("✓ Pallet status updated");
    } catch (error) {
      console.error("Error updating status:", error);
      throw new Error("Không thể cập nhật trạng thái pallet");
    }
  }

  // ==================== API 2: SEND WMS PAYLOAD ====================

  private async sendWmsPayload(
    selectedPallets: PalletDialogItem[],
  ): Promise<void> {
    const payload = this.buildWmsPayload(selectedPallets);

    console.log("📦 WMS Payload:", payload);

    try {
      await firstValueFrom(this.planningService.sendWmsApproval(payload));
      console.log("✓ WMS approval sent");
    } catch (error) {
      console.error("Error sending WMS:", error);
      throw new Error("Không thể gửi phê duyệt WMS");
    }
  }

  // ==================== BUILD WMS PAYLOAD ====================

  private buildWmsPayload(selectedPallets: PalletDialogItem[]): WmsPayload {
    const info = (this.warehouseNoteInfo ?? {}) as Record<string, any>;
    const firstPallet = selectedPallets[0];

    const formatDate = (isoDate?: string): string => {
      if (!isoDate) {
        return "";
      }
      const d = new Date(isoDate);
      return `${String(d.getDate()).padStart(2, "0")}/${String(d.getMonth() + 1).padStart(2, "0")}/${d.getFullYear()}`;
    };

    // compute totals (kept safe)
    const totalBoxes = selectedPallets.reduce(
      (s, p) => s + Number(p.numBoxActual ?? 0),
      0,
    );
    const totalQuantity = selectedPallets.reduce((sum, p) => {
      const boxes = (p as any).listBox ?? (p as any).boxes ?? [];
      if (Array.isArray(boxes) && boxes.length) {
        return (
          sum +
          boxes.reduce((ss: number, b: any) => ss + Number(b.quantity ?? 0), 0)
        );
      }
      return sum + Number(p.numBoxActual ?? 0) * Number(p.quantityPerBox ?? 0);
    }, 0);

    // build list_pallet (unchanged)
    const listPallet: WmsPalletItem[] = selectedPallets.map((pallet) => {
      const rawListBox = (pallet as any).listBox ?? (pallet as any).boxes ?? [];
      const list_box: WmsBox[] =
        Array.isArray(rawListBox) && rawListBox.length > 0
          ? rawListBox.map((b: any) => ({
              box_code: String(b.boxCode ?? ""),
              quantity: Number(b.quantity ?? 0),
              note: b.note ?? "",
              list_serial_items: b.list_serial_items ?? "",
            }))
          : Array.from({ length: Number(pallet.numBoxActual ?? 0) }).map(
              (_, i) => ({
                box_code: `${pallet.palletCode ?? pallet.qrCode ?? "BOX"}-${i + 1}`,
                quantity: Number(pallet.quantityPerBox ?? 0),
                note: "",
                list_serial_items: "",
              }),
            );

      const totalQuantityForPallet =
        list_box.length > 0
          ? list_box.reduce((s, b) => s + Number(b.quantity ?? 0), 0)
          : Number(pallet.numBoxActual ?? 0) *
            Number(pallet.quantityPerBox ?? 0);

      return {
        serial_pallet: pallet.palletCode ?? pallet.qrCode ?? "",
        quantity_per_box: String(pallet.quantityPerBox ?? ""),
        num_box_per_pallet: String(pallet.numBoxActual ?? ""),
        total_quantity: String(totalQuantityForPallet ?? 0),
        po_number: pallet.poNumber ?? "",
        customer_name: pallet.customerName ?? "",
        production_decision_number: pallet.productionDecisionNumber ?? "",
        item_no_sku: pallet.itemNoSku ?? "",
        date_code: pallet.dateCode ?? "",
        note: pallet.note ?? "",
        list_box,
      } as unknown as WmsPalletItem;
    });

    // Map general_info using warehouse_note_info fields (snake_case) with fallbacks
    const payload: WmsPayload = {
      general_info: {
        client_id: info?.client_id ?? firstPallet?.customerName ?? "", // backend may not provide client_id
        inventory_code: info?.sap_code ?? firstPallet?.itemNoSku ?? "",
        inventory_name: info?.sap_name ?? "",
        wo_code: info?.work_order_code ?? "",
        production_date: formatDate(
          info?.production_date ??
            firstPallet?.productionDate ??
            info?.time_update,
        ),
        lot_number: this.getLotNumber?.() ?? "",
        note: info?.comment ?? info?.comment_2 ?? "",
        created_by: info?.create_by ?? info?.createBy ?? "admin",
        branch: info?.branch ?? "",
        production_team: (info?.group_name ?? "").toString().trim() ?? "",
        number_of_pallet: selectedPallets.length,
        number_of_box: totalBoxes,
        quantity: totalQuantity,
        destination_warehouse: Number(info?.destination_warehouse ?? 1),
        pallet_note_creation_id: Number(
          info?.id ?? this.data?.maLenhSanXuatId ?? 0,
        ),
        list_pallet: listPallet,
      },
    };

    console.log("WMS payload (built):", payload);
    return payload;
  }

  // ==================== LOAD DATA FROM API ====================

  private async loadData(): Promise<void> {
    this.isLoading = true;

    try {
      // Lấy HttpResponse từ service (service vẫn giữ observe: 'response')
      const httpResp = (await firstValueFrom(
        this.planningService.findWarehouseNoteWithChildren(
          this.data.maLenhSanXuatId,
        ),
      )) as HttpResponse<any>;

      // Lấy body và ép kiểu an toàn
      const response = httpResp?.body as WarehouseNoteResponse | null;

      if (!response) {
        throw new Error("Không có dữ liệu trong response.body");
      }

      // Lưu raw data
      this.warehouseNoteInfo = response.warehouse_note_info;
      this.palletDetails = response.pallet_infor_details || [];
      this.boxDetails = response.warehouse_note_info_details || [];
      this.mappings = response.serial_box_pallet_mappings || [];

      this.productName = this.warehouseNoteInfo.sap_name;

      // Map sang PalletDialogItem
      const mappedPallets = this.mapPalletsToPalletDialogItems(
        this.palletDetails,
      );
      this.pallets.set(mappedPallets);

      console.log("✓ Loaded pallets:", mappedPallets);
    } catch (error) {
      console.error("Error loading data:", error);
      this.snackBar.open("Không thể tải dữ liệu pallet", "Đóng", {
        duration: 3000,
      });
      this.dialogRef.close(); // Đóng dialog nếu load thất bại
    } finally {
      this.isLoading = false;
    }
  }

  // ==================== MAP PALLET DATA ====================

  private mapPalletsToPalletDialogItems(
    palletDetails: any[],
  ): PalletDialogItem[] {
    if (!Array.isArray(palletDetails) || palletDetails.length === 0) {
      return [];
    }

    const result: PalletDialogItem[] = [];

    palletDetails.forEach((detail, index) => {
      // Lấy danh sách boxes cho pallet này
      const palletBoxes = this.getBoxesForPallet(detail.serial_pallet);

      result.push({
        id: detail.id,
        stt: index + 1,
        qrCode: detail.serial_pallet || "",
        palletCode: detail.serial_pallet || "",
        scanProgress: `${detail.scan_progress || 0}/${detail.num_box_config || 0}`,
        capacity: detail.num_box_actual || 0,
        isSent: detail.wms_send_status === true,

        // Hiển thị
        productName: this.warehouseNoteInfo?.sap_name || "",
        lotNumber: this.getLotNumber(),

        // Payload fields
        poNumber: detail.po_number || "",
        customerName: detail.customer_name || "",
        productionDecisionNumber: detail.qdsx_no || "",
        itemNoSku: detail.item_no_sku || "",
        dateCode: detail.date_code || "",
        productionDate: detail.production_date || "",
        note: detail.note || "",
        quantityPerBox: detail.quantity_per_box || 0,
        numBoxActual: detail.num_box_actual || 0,

        // THÊM: Danh sách boxes đã map sẵn (không cần listBox nữa)
        boxes: palletBoxes,
      } as any); // Cast to any để add thêm field boxes
    });

    return result;
  }

  // ==================== GET BOXES FOR PALLET ====================

  private getBoxesForPallet(
    serialPallet: string,
  ): Array<{ boxCode: string; quantity: number; note?: string }> {
    const boxes: Array<{ boxCode: string; quantity: number; note?: string }> =
      [];

    // Lấy từ mappings
    if (this.mappings && this.mappings.length > 0) {
      const palletMappings = this.mappings.filter(
        (m) => m.serial_pallet === serialPallet,
      );

      palletMappings.forEach((mapping) => {
        // Tìm box detail để lấy quantity
        const boxDetail = this.boxDetails.find(
          (b) => b.reel_id === mapping.serial_box,
        );

        boxes.push({
          boxCode: mapping.serial_box || "",
          quantity: boxDetail?.initial_quantity || 0,
          note: boxDetail?.comments || "",
        });
      });
    }

    // Fallback: nếu không có mapping, lấy tất cả boxes
    if (boxes.length === 0 && this.boxDetails.length > 0) {
      this.boxDetails.forEach((box) => {
        boxes.push({
          boxCode: box.reel_id || "",
          quantity: box.initial_quantity || 0,
          note: box.comments || "",
        });
      });
    }

    return boxes;
  }

  // ==================== HELPER: GET LOT NUMBER ====================

  private getLotNumber(): string {
    if (this.boxDetails && this.boxDetails.length > 0) {
      const lot = (this.boxDetails[0] as { lot?: string | null })?.lot;
      return lot ?? "";
    }
    return "";
  }

  // Hàm async tách riêng để xử lý luồng gửi
  private async performSendApproval(
    selectedPallets: PalletDialogItem[],
  ): Promise<void> {
    this.isLoading = true;

    try {
      //Send WMS payload
      await this.sendWmsPayload(selectedPallets);
      //Update pallet status
      await this.updatePalletStatus(selectedPallets);

      //Update UI
      this.updateUIAfterSend(selectedPallets);

      this.snackBar.open(
        `✓ Đã gửi ${selectedPallets.length} pallet thành công!`,
        "Đóng",
        { duration: 3000, panelClass: ["success-snackbar"] },
      );

      // Auto switch tab on mobile
      if (this.isMobile) {
        setTimeout(() => {
          this.mobileTabIndex = 1;
        }, 500);
      }
    } catch (error: any) {
      console.error("Error sending approval:", error);
      this.snackBar.open(
        `Lỗi: ${error?.error?.message || error?.message || "Không xác định"}`,
        "Đóng",
        { duration: 4000, panelClass: ["error-snackbar"] },
      );
    } finally {
      this.isLoading = false;
    }
  }
  private mapDetailDataToDialogItems(
    details: PalletDetailData[],
  ): PalletDialogItem[] {
    console.log("Mapping preloaded details:", details);

    return details.map((d, idx) => {
      // LẤY TIẾN ĐỘ TỪ PRELOADED DATA (bạn muốn dùng thungScan làm total)
      const scanned = Number(d.thungScan ?? 0);
      const total = Number(d.thungScan ?? 0);

      // Nếu có scannedBoxes array, ưu tiên lấy length
      const scannedFromBoxes = Array.isArray(d.scannedBoxes)
        ? d.scannedBoxes.length
        : undefined;
      const current =
        typeof scannedFromBoxes === "number" ? scannedFromBoxes : scanned;
      const agg = this.palletAggregates?.get(d.serialPallet ?? "") ?? {
        totalBoxes: 0,
        sentBoxes: 0,
        boxSerials: [],
      };

      console.log(
        `Pallet ${d.serialPallet}: ${current}/${total} (scannedBoxes: ${scannedFromBoxes})`,
      );

      // Map boxes nếu có
      const boxes = this.mapScannedBoxesToList(d.scannedBoxes, d.boxItems);

      // Lấy id từ phần tử d; nếu không có, fallback hoặc ném lỗi tùy yêu cầu
      if (d.id == null) {
        console.error("Missing numeric id for pallet detail at index", idx, d);
        // Nếu id bắt buộc để update, bạn có thể ném lỗi để phát hiện sớm:
        // throw new Error(`Missing id for pallet detail at index ${idx}`);
      }
      const idValue: number =
        typeof d.id === "number" && !Number.isNaN(d.id) ? d.id : idx + 1;
      const rawStatus = (d as any).wms_send_status;
      const isSent =
        rawStatus === true ||
        rawStatus === "true" ||
        rawStatus === 1 ||
        rawStatus === "1";
      const item: PalletDialogItem & { boxes?: any[] } = {
        id: idValue,
        stt: idx + 1,
        qrCode: d.serialPallet ?? "",
        palletCode: d.serialPallet ?? "",
        scanProgress: `${current}/${total}`, // Dùng current/total
        capacity: total,
        isSent: isSent,

        // Display
        productName: d.tenSanPham ?? "",
        lotNumber: "",

        // Payload fields
        poNumber: d.poNumber ?? "",
        customerName: d.khachHang ?? "",
        productionDecisionNumber: d.qdsx ?? "",
        itemNoSku: d.noSKU ?? "",
        dateCode: d.dateCode ?? "",
        productionDate: d.createdAt ?? "",
        note: d.note ?? "",
        quantityPerBox: Math.round((d.tongSlSp ?? 0) / (total || 1)),
        numBoxActual: total,

        // Boxes list
        boxes: boxes,

        totalBoxesCount: agg.totalBoxes,
        sentBoxesCount: agg.sentBoxes,
        sentBoxesPercent:
          agg.totalBoxes > 0
            ? Math.round((agg.sentBoxes / agg.totalBoxes) * 100)
            : 0,
      };

      return item;
    });
  }

  private mapScannedBoxesToList(
    scannedBoxes?: string[],
    boxItems?: any[],
  ): Array<{ boxCode: string; quantity: number; note?: string }> {
    if (!Array.isArray(scannedBoxes) || scannedBoxes.length === 0) {
      return [];
    }

    const boxes: Array<{ boxCode: string; quantity: number; note?: string }> =
      [];

    for (const boxCode of scannedBoxes) {
      let quantity = 0;
      let note = "";

      // 1) tìm trong boxItems (nhóm subItems)
      if (Array.isArray(boxItems)) {
        for (const boxGroup of boxItems) {
          if (Array.isArray(boxGroup.subItems)) {
            const found = boxGroup.subItems.find(
              (sub: any) => sub.maThung === boxCode,
            );
            if (found) {
              quantity = found.soLuong ?? 0;
              note = boxGroup.ghiChu ?? "";
              break;
            }
          }
        }
      }

      // 2) fallback: tìm trong warehouseNoteInfoDetails (flat list)
      if (quantity === 0 && Array.isArray(this.warehouseNoteInfoDetails)) {
        const foundDetail = this.warehouseNoteInfoDetails.find(
          (bd: any) => (bd.reel_id ?? bd.serial_box ?? "") === boxCode,
        );
        if (foundDetail) {
          quantity = Number(
            foundDetail.initial_quantity ?? foundDetail.soLuong ?? 0,
          );
          // note có thể nằm ở trường comments hoặc khác
          note = foundDetail.comments ?? foundDetail.ghiChu ?? "";
        }
      }

      boxes.push({ boxCode, quantity, note });
    }

    return boxes;
  }
  private buildBoxAggregates(
    boxDetails: BoxDetail[] | undefined,
    mappings: MappingItem[] | undefined,
  ): {
    boxDetailBySerial: BoxDetailMap;
    boxToPallet: BoxToPalletMap;
    palletAggregates: PalletAggregatesMap;
  } {
    // Map serial_box -> boxDetail
    const boxDetailBySerial = new Map<string, any>();
    if (Array.isArray(boxDetails)) {
      for (const bd of boxDetails) {
        // bd.reel_id hoặc bd.serial_box tùy tên trường
        const serial = (bd.reel_id ?? bd.serial_box ?? "").toString();
        if (serial) {
          boxDetailBySerial.set(serial, bd);
        }
      }
    }

    // Map serial_box -> serial_pallet (từ mappings)
    const boxToPallet = new Map<string, string>();
    if (Array.isArray(mappings)) {
      for (const m of mappings) {
        if (m?.serial_box && m?.serial_pallet) {
          boxToPallet.set(m.serial_box.toString(), m.serial_pallet.toString());
        }
      }
    }

    // Aggregate: palletSerial -> { totalBoxes, sentBoxes, boxSerials[] }
    const palletAggregates = new Map<
      string,
      { totalBoxes: number; sentBoxes: number; boxSerials: string[] }
    >();

    // Iterate over all known box serials (from boxDetailBySerial or boxToPallet)
    const allBoxSerials = new Set<string>([
      ...boxDetailBySerial.keys(),
      ...boxToPallet.keys(),
    ]);

    for (const boxSerial of allBoxSerials) {
      const palletSerial = boxToPallet.get(boxSerial) ?? ""; // may be empty if not mapped
      const boxDetail = boxDetailBySerial.get(boxSerial);
      const wmsStatus = Boolean(boxDetail?.wms_send_status === true);

      if (!palletAggregates.has(palletSerial)) {
        palletAggregates.set(palletSerial, {
          totalBoxes: 0,
          sentBoxes: 0,
          boxSerials: [],
        });
      }
      const agg = palletAggregates.get(palletSerial)!;
      agg.totalBoxes += 1;
      if (wmsStatus) {
        agg.sentBoxes += 1;
      }
      agg.boxSerials.push(boxSerial);
    }

    return { boxDetailBySerial, boxToPallet, palletAggregates };
  }

  private isVisible(el: HTMLElement | null): boolean {
    if (!el) {
      return false;
    }
    const style = window.getComputedStyle(el);
    if (
      style.display === "none" ||
      style.visibility === "hidden" ||
      style.opacity === "0"
    ) {
      return false;
    }
    const rect = el.getBoundingClientRect();
    return rect.width > 0 && rect.height > 0;
  }

  // ==================== EXTRACT LOT NUMBER ====================
}
