import {
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { ReceivingSuppliesService } from "app/entities/generate-tem-in/service/receiving-supplies.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import {
  InfoTemNccService,
  SapPor1BatchItem,
  toText,
} from "../../services/info-tem-ncc.service";

/** 1 dòng vật tư trong PO (từ sap-po-info) */
export interface PoImportRow {
  key: string;
  poCode: string;
  sapCode: string;
  itemName: string;
  partNumber: string;
  quantity: number;
  unit: string;
  whsCode: string;
  lineNum: string;
  selected: boolean;
  /** Đã có trên bảng đơn → không chọn lại */
  onTable: boolean;
  /** Part number OITM của mã SAP — gợi ý cho ô Part */
  partOptions: string[];
  /** Dữ liệu dòng PO từ sap-po-info → payload lưu (chưa có deliveryNotificationId) */
  payload: Omit<SapPor1BatchItem, "deliveryNotificationId">;
}

/** Kho SAP (/api/owhs) — khai báo local tránh eslint any từ type-import */
interface WarehouseOption {
  whsCode: string;
  whsName: string;
}

type FilterField = "sap" | "name" | "part";

interface LoadedPo {
  poCode: string;
  vendorCode: string;
  vendorName: string;
  rows: PoImportRow[];
  /** oporCardCode khác mã NCC của đơn */
  vendorMismatch: boolean;
}

export interface PoImportDialogData {
  /** Khóa "PO|mã SAP" đã có trên bảng */
  existingKeys: string[];
  /** Mã nhà cung cấp của đơn — đối chiếu với oporCardCode của PO */
  vendorCode?: string;
  /** Tên nhà cung cấp của đơn — hiển thị khi lệch NCC */
  vendorName?: string;
}

export interface PoImportSelection {
  poCode: string;
  vendorCode: string;
  vendorName: string;
  rows: PoImportRow[];
}

/** Dialog Nhập PO: nhập nhiều PO, tích chọn vật tư ở từng PO, Xác nhận → thêm vào bảng */
@Component({
  selector: "jhi-po-import-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: "./po-import-dialog.component.html",
  styleUrls: ["./po-import-dialog.component.scss"],
})
export class PoImportDialogComponent implements OnInit {
  @ViewChild("poInputRef") poInputRef?: ElementRef<HTMLInputElement>;

  poInput = "";
  isFetching = false;
  loadedPos: LoadedPo[] = [];
  activePoCode = "";

  filterSap = "";
  filterName = "";
  filterPart = "";

  /** Ô kho ở header cột Mã kho SAP — chọn để áp cho vật tư đã tích / sẽ tích */
  headerWarehouse = "";
  /** Mã kho header đang áp dụng (rỗng = không áp) */
  headerWarehouseCode = "";

  /** Kho SAP cho autocomplete cột Mã kho SAP */
  warehouses: WarehouseOption[] = [];
  isLoadingWarehouses = false;

  /** Mã / tên NCC của đơn đang mở */
  readonly orderVendorCode: string;
  readonly orderVendorName: string;

  private existingKeys: Set<string>;

  constructor(
    private dialogRef: MatDialogRef<
      PoImportDialogComponent,
      PoImportSelection[]
    >,
    @Inject(MAT_DIALOG_DATA) data: PoImportDialogData,
    private receivingService: ReceivingSuppliesService,
    private infoTemNccService: InfoTemNccService,
    private notificationService: NotificationService,
  ) {
    this.orderVendorCode = toText(data?.vendorCode);
    this.orderVendorName = toText(data?.vendorName);
    this.existingKeys = new Set(
      (data?.existingKeys ?? []).map((k) => k.toLowerCase()),
    );
  }

  ngOnInit(): void {
    this.isLoadingWarehouses = true;
    this.receivingService.getSapWarehouses().subscribe({
      next: (list) => {
        this.isLoadingWarehouses = false;
        this.warehouses = (list ?? []).map((w) => ({
          whsCode: toText(w.whsCode),
          whsName: toText(w.whsName),
        }));
      },
      error: () => {
        this.isLoadingWarehouses = false;
      },
    });
  }

  /** Label kho: "whsCode - whsName" */
  displayWarehouse = (code: string | null): string => {
    const c = toText(code);
    if (!c) {
      return "";
    }
    const w = this.warehouses.find((x) => x.whsCode === c);
    return w ? `${w.whsCode} - ${w.whsName}` : c;
  };

  /** Gợi ý kho theo nội dung đang nhập trong ô của dòng */
  warehouseOptionsFor(term: string): WarehouseOption[] {
    // Ô đang hiển thị nhãn "mã - tên" → lọc theo phần mã
    const t = toText(term).split(" - ")[0].trim().toLowerCase();
    const list = t
      ? this.warehouses.filter(
          (w) =>
            w.whsCode.toLowerCase().includes(t) ||
            w.whsName.toLowerCase().includes(t),
        )
      : this.warehouses;
    return list.slice(0, 50);
  }

  /** Gợi ý part cho ô Part của dòng (lọc theo nội dung đang nhập) */
  partOptionsFor(r: PoImportRow): string[] {
    const t = toText(r.partNumber).toLowerCase();
    const opts = r.partOptions;
    if (!t || opts.some((o) => o.toLowerCase() === t)) {
      return opts;
    }
    return opts.filter((o) => o.toLowerCase().includes(t));
  }

  /** Gợi ý cho ô lọc: giá trị khác nhau trong PO đang xem, lọc theo nội dung ô */
  filterOptions(field: FilterField): string[] {
    const po = this.activePo;
    if (!po) {
      return [];
    }
    const pick = (r: PoImportRow): string[] => {
      if (field === "sap") {
        return [r.sapCode];
      }
      if (field === "name") {
        return [r.itemName];
      }
      return r.partOptions.length ? r.partOptions : [r.partNumber];
    };
    let raw = this.filterPart;
    if (field === "sap") {
      raw = this.filterSap;
    } else if (field === "name") {
      raw = this.filterName;
    }
    const term = raw.trim().toLowerCase();
    const values = [
      ...new Set(
        po.rows
          .flatMap(pick)
          .map((v) => toText(v))
          .filter(Boolean),
      ),
    ];
    return values
      .filter((v) => !term || v.toLowerCase().includes(term))
      .slice(0, 50);
  }

  get activePo(): LoadedPo | null {
    return this.loadedPos.find((p) => p.poCode === this.activePoCode) ?? null;
  }

  get visibleRows(): PoImportRow[] {
    const po = this.activePo;
    if (!po) {
      return [];
    }
    const has = (value: string, term: string): boolean =>
      !term.trim() || value.toLowerCase().includes(term.trim().toLowerCase());
    return po.rows.filter(
      (r) =>
        has(r.sapCode, this.filterSap) &&
        has(r.itemName, this.filterName) &&
        has(r.partNumber, this.filterPart),
    );
  }

  get selectableVisible(): PoImportRow[] {
    return this.visibleRows.filter((r) => !r.onTable);
  }

  get allVisibleChecked(): boolean {
    const rows = this.selectableVisible;
    return rows.length > 0 && rows.every((r) => r.selected);
  }

  get someVisibleChecked(): boolean {
    const n = this.selectableVisible.filter((r) => r.selected).length;
    return n > 0 && n < this.selectableVisible.length;
  }

  get totalSelected(): number {
    return this.loadedPos.reduce(
      (s, p) => s + p.rows.filter((r) => r.selected).length,
      0,
    );
  }

  get selectedPoCount(): number {
    return this.loadedPos.filter((p) => p.rows.some((r) => r.selected)).length;
  }

  selectedCount(po: LoadedPo): number {
    return po.rows.filter((r) => r.selected).length;
  }

  /** Lấy thông tin PO: GET /api/sap-po-info/{po} */
  onFetchPo(): void {
    const po = this.poInput.trim();
    if (!po || this.isFetching) {
      return;
    }
    const existing = this.loadedPos.find(
      (p) => p.poCode.toLowerCase() === po.toLowerCase(),
    );
    if (existing) {
      this.activePoCode = existing.poCode;
      this.poInput = "";
      this.notificationService.info(`PO ${existing.poCode} đã được tải.`);
      return;
    }
    this.isFetching = true;
    this.receivingService.getSapPoInfo(po).subscribe({
      next: (res) => {
        this.isFetching = false;
        const details = res?.poDetails ?? [];
        if (!res || !details.length) {
          this.notificationService.warning(
            `Không tìm thấy thông tin PO "${po}" hoặc PO không có vật tư.`,
          );
          this.focusPoInput();
          return;
        }
        // Mã PO trên bảng = docEntry (giống dữ liệu detail)
        const header = (res.poInfo ?? {}) as Record<string, unknown>;
        const poCode =
          toText(header["oporDocEntry"]) || toText(header["oporDocNum"]) || po;
        const rows: PoImportRow[] = details.map((d, i) => {
          const sapCode = toText(d.por1ItemCode);
          const raw = d as unknown as Record<string, unknown>;
          const onTable = this.existingKeys.has(
            `${poCode}|${sapCode}`.toLowerCase(),
          );
          return {
            key: `${poCode}-${toText(d.por1LineNum) || i}-${sapCode}`,
            poCode,
            sapCode,
            itemName: toText(d.por1Dscription),
            partNumber: "",
            quantity: Number(d.por1Quantity ?? 0) || 0,
            unit: toText(d.por1UnitMsr) || toText(d.por1UOMCode),
            whsCode: toText(d.por1WhsCode),
            lineNum: toText(d.por1LineNum),
            selected: false,
            onTable,
            partOptions: [],
            payload: this.toPayload(raw, header, poCode),
          };
        });
        const poVendor = toText(res.poInfo?.oporCardCode);
        // Đối chiếu mã NCC của PO với mã NCC của đơn
        const vendorMismatch =
          !!this.orderVendorCode &&
          poVendor.toLowerCase() !== this.orderVendorCode.toLowerCase();
        if (vendorMismatch) {
          const poName =
            toText(res.poInfo?.oporCardName) || poVendor || "không rõ";
          const orderName = this.orderVendorName || this.orderVendorCode;
          this.notificationService.warning(
            `PO ${poCode} là của "${poName}", khác nhà cung cấp của đơn "${orderName}".`,
          );
        }
        this.loadedPos = [
          ...this.loadedPos,
          {
            poCode,
            vendorCode: poVendor,
            vendorName: toText(res.poInfo?.oporCardName),
            rows,
            vendorMismatch,
          },
        ];
        this.activePoCode = poCode;
        this.poInput = "";
        this.clearFilters();
        this.loadPartNumbers(rows);
        this.focusPoInput();
      },
      error: () => {
        this.isFetching = false;
        this.notificationService.error(`Lỗi khi lấy thông tin PO "${po}".`);
      },
    });
  }

  selectPo(po: LoadedPo): void {
    this.activePoCode = po.poCode;
    this.clearFilters();
  }

  removePo(po: LoadedPo, event: Event): void {
    event.stopPropagation();
    this.loadedPos = this.loadedPos.filter((p) => p !== po);
    if (this.activePoCode === po.poCode) {
      this.activePoCode = this.loadedPos[0]?.poCode ?? "";
    }
  }

  /** Chọn kho ở header → áp cho vật tư đang tích; vật tư tích sau cũng nhận kho này */
  applyWarehouseToSelected(code: string): void {
    const whs = toText(code);
    if (!whs) {
      return;
    }
    this.headerWarehouseCode = whs;
    for (const po of this.loadedPos) {
      for (const r of po.rows) {
        if (r.selected && !r.onTable) {
          r.whsCode = whs;
        }
      }
    }
  }

  /** Xóa ô kho header → thôi áp kho cho vật tư tích sau */
  onHeaderWarehouseInput(value: string): void {
    if (!toText(value)) {
      this.headerWarehouseCode = "";
    }
  }

  /** Tích 1 vật tư → nếu header đã chọn kho thì áp luôn */
  onRowCheck(r: PoImportRow, checked: boolean): void {
    r.selected = checked;
    if (checked && this.headerWarehouseCode && !r.onTable) {
      r.whsCode = this.headerWarehouseCode;
    }
  }

  toggleAllVisible(checked: boolean): void {
    for (const r of this.selectableVisible) {
      this.onRowCheck(r, checked);
    }
  }

  onConfirm(): void {
    const result: PoImportSelection[] = this.loadedPos
      .map((p) => ({
        poCode: p.poCode,
        vendorCode: p.vendorCode,
        vendorName: p.vendorName,
        rows: p.rows.filter((r) => r.selected),
      }))
      .filter((p) => p.rows.length > 0);
    if (!result.length) {
      this.notificationService.warning("Chưa chọn vật tư nào.");
      return;
    }
    this.dialogRef.close(result);
  }

  onClose(): void {
    this.dialogRef.close([]);
  }

  trackRow(_: number, r: PoImportRow): string {
    return r.key;
  }

  /** Map dòng poDetails (+ header poInfo) → payload sap-por-1-r-1-s */
  private toPayload(
    d: Record<string, unknown>,
    header: Record<string, unknown>,
    poCode: string,
  ): Omit<SapPor1BatchItem, "deliveryNotificationId"> {
    const str = (key: string): string | null => {
      const v = toText(d[key]);
      return v ? v : null;
    };
    const num = (key: string): number | null => {
      const v = d[key];
      if (v === null || v === undefined || v === "") {
        return null;
      }
      const n = Number(v);
      return Number.isFinite(n) ? n : null;
    };
    return {
      lineNum: str("por1LineNum"),
      baseRef: str("por1BaseRef"),
      baseEntry: str("por1BaseEntry"),
      baseLine: str("por1BaseLine"),
      lineStatus: str("por1LineStatus"),
      itemCode: str("por1ItemCode"),
      dscription: str("por1Dscription"),
      quantity: num("por1Quantity") ?? 0,
      shipDate: str("por1ShipDate"),
      price: str("por1Price"),
      currency: str("por1Currency"),
      discPrcnt: str("por1DiscPrcnt"),
      totalSumSy: str("por1TotalSumsy"),
      openSumSys: str("por1OpenSumSys"),
      // Dòng PO không có — lấy trạng thái tồn kho của header PO
      invntSttus: toText(header["oporInvntSttus"]) || null,
      baseDocNum: str("por1BaseDocNum"),
      getuTenkythuat: str("por1UTenkythuat"),
      getuSo: str("por1USo"),
      getuMCode: str("por1UMcode"),
      docEntry: str("poDocEntry") ?? poCode,
      totalFrgn: num("por1TotalFrgn"),
      vatGroup: str("por1VatGroup"),
      uomCode: str("por1UOMCode"),
      unitMsr: str("por1UnitMsr"),
      lineVendor: str("por1LineVendor"),
      trgetEntry: str("por1TrgetEntry"),
      lineTotal: num("por1LineTotal"),
      vatPrcnt: num("por1VatPrcnt"),
      priceAfVat: num("por1PriceAfVat"),
      whsCode: str("por1WhsCode"),
    };
  }

  private clearFilters(): void {
    this.filterSap = "";
    this.filterName = "";
    this.filterPart = "";
  }

  /** Part number OITM theo mã SAP (service có cache) */
  private loadPartNumbers(rows: PoImportRow[]): void {
    const codes = new Set(rows.map((r) => r.sapCode).filter(Boolean));
    codes.forEach((code) => {
      this.infoTemNccService
        .getPartNumbersBySapCode(code)
        .subscribe((parts) => {
          for (const r of rows) {
            if (r.sapCode === code) {
              r.partOptions = parts;
              // Chưa sửa tay → mặc định part đầu tiên của OITM
              if (!r.partNumber) {
                r.partNumber = parts[0] ?? "";
              }
            }
          }
        });
    });
  }

  private focusPoInput(): void {
    setTimeout(() => this.poInputRef?.nativeElement?.focus(), 0);
  }
}
