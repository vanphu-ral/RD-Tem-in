import {
  ChangeDetectorRef,
  Component,
  Inject,
  OnDestroy,
  OnInit,
} from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import {
  ReceivingSuppliesService,
  SapOwhsDto,
  WarehouseLocation,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";

export interface ReelImportPreviewColumn {
  key: string;
  label: string;
  /** Tỷ lệ cột (%); tổng ~100 để vừa 1 màn, không scroll ngang. */
  width: string;
  editable: boolean;
  headerEditable: boolean;
  required?: boolean;
}

export interface ReelImportPreviewRow {
  uid: string;
  lineNo: number;
  reelId: string;
  partNumber: string;
  initialQuantity: number | string;
  poNumber: string;
  manufacturingDate: string;
  lotNumber: string;
  itemName: string;
  expirationDate: string;
  locationOverride: string;
  storageUnit: string;
  sapCode: string;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  msl: string;
  vendorQrCode: string;
  [key: string]: any;
}

export interface ReelImportPreviewDialogData {
  fileName: string;
  rows: ReelImportPreviewRow[];
  errors: string[];
}

export interface ReelImportPreviewGroup {
  key: string;
  label: string;
  groupBy: "sap" | "part";
  rows: ReelImportPreviewRow[];
  headerValues: Record<string, string>;
  autoFillDefaults: boolean;
}

/** Các trường cần bổ sung để hoàn thiện vật tư (highlight vàng / badge thiếu). */
export const REEL_IMPORT_COMPLETION_FIELDS: {
  key: keyof ReelImportPreviewRow;
  label: string;
}[] = [
  { key: "expirationDate", label: "HSD" },
  { key: "locationOverride", label: "Vị trí" },
  { key: "storageUnit", label: "Mã kho" },
  { key: "sapCode", label: "SAP" },
];

const DEFAULT_USERDATA = "NO";
const DEFAULT_MSL = "1";

@Component({
  selector: "jhi-import-reel-preview-dialog",
  templateUrl: "./import-reel-preview-dialog.component.html",
  styleUrls: ["./import-reel-preview-dialog.component.scss"],
})
export class ImportReelPreviewDialogComponent implements OnInit, OnDestroy {
  columns: ReelImportPreviewColumn[] = [
    {
      key: "reelId",
      label: "ReelID",
      width: "10%",
      editable: false,
      headerEditable: false,
    },
    {
      key: "partNumber",
      label: "Part Number",
      width: "7%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "initialQuantity",
      label: "SL",
      width: "3.5%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "poNumber",
      label: "PO",
      width: "5%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "manufacturingDate",
      label: "MFG",
      width: "5%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "lotNumber",
      label: "Số lô",
      width: "6%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "itemName",
      label: "Tên SP",
      width: "7%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "expirationDate",
      label: "HSD",
      width: "5%",
      editable: true,
      headerEditable: true,
      required: true,
    },
    {
      key: "locationOverride",
      label: "Vị trí",
      width: "5%",
      editable: true,
      headerEditable: true,
      required: true,
    },
    {
      key: "storageUnit",
      label: "Mã kho",
      width: "5%",
      editable: true,
      headerEditable: true,
      required: true,
    },
    {
      key: "sapCode",
      label: "SAP",
      width: "5%",
      editable: true,
      headerEditable: true,
      required: true,
    },
    {
      key: "vendor",
      label: "Vendor",
      width: "4.5%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "userData1",
      label: "UD1",
      width: "4%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "userData2",
      label: "UD2",
      width: "4%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "userData3",
      label: "UD3",
      width: "4%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "userData4",
      label: "UD4",
      width: "5%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "userData5",
      label: "UD5",
      width: "5%",
      editable: true,
      headerEditable: true,
    },
    {
      key: "msl",
      label: "MSL",
      width: "3%",
      editable: true,
      headerEditable: true,
    },
  ];

  groups: ReelImportPreviewGroup[] = [];
  parseErrors: string[] = [];
  fileName = "";

  /** Lọc nhóm theo mã SAP / Part Number trên header. */
  searchSapCode = "";
  searchPartNumber = "";

  /** Autocomplete vị trí (giống receiving-supplies). */
  filteredLocationOptions: WarehouseLocation[] = [];
  lastLocationSearchTerm = "";
  locationSearchSettled = false;
  locationSearchPending = false;

  /** Autocomplete mã kho SAP (giống receiving-supplies). */
  sapWarehouseList: SapOwhsDto[] = [];
  filteredSapWarehouseList: SapOwhsDto[] = [];
  isLoadingSapWarehouses = false;

  private locationSearchSeq = 0;
  private locationSearchTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    public dialogRef: MatDialogRef<
      ImportReelPreviewDialogComponent,
      ReelImportPreviewRow[] | null
    >,
    @Inject(MAT_DIALOG_DATA) public data: ReelImportPreviewDialogData,
    private receivingService: ReceivingSuppliesService,
    private cdr: ChangeDetectorRef,
  ) {
    this.fileName = data.fileName ?? "";
    this.parseErrors = data.errors ?? [];
    this.groups = this.buildGroups(
      (data.rows ?? []).map((row) => ({ ...row })),
    );
  }

  ngOnInit(): void {
    void this.receivingService.initWarehouses();
    this.loadSapWarehouses();
  }

  ngOnDestroy(): void {
    if (this.locationSearchTimer) {
      clearTimeout(this.locationSearchTimer);
      this.locationSearchTimer = null;
    }
  }

  get totalRows(): number {
    return this.groups.reduce((sum, g) => sum + g.rows.length, 0);
  }

  /** Nhóm đang hiển thị sau khi lọc theo mã SAP / Part. */
  get visibleGroups(): ReelImportPreviewGroup[] {
    const sapTerm = this.searchSapCode.trim().toLowerCase();
    const partTerm = this.searchPartNumber.trim().toLowerCase();
    if (!sapTerm && !partTerm) {
      return this.groups;
    }

    return this.groups.filter((group) => {
      const matchSap = !sapTerm || this.groupMatchesSapSearch(group, sapTerm);
      const matchPart =
        !partTerm || this.groupMatchesPartSearch(group, partTerm);
      return matchSap && matchPart;
    });
  }

  get visibleGroupCount(): number {
    return this.visibleGroups.length;
  }

  get visibleRowCount(): number {
    return this.visibleGroups.reduce((sum, g) => sum + g.rows.length, 0);
  }

  get hasActiveGroupFilter(): boolean {
    return (
      Boolean(this.searchSapCode.trim()) ||
      Boolean(this.searchPartNumber.trim())
    );
  }

  clearGroupFilters(): void {
    this.searchSapCode = "";
    this.searchPartNumber = "";
  }

  get globalMissingLabels(): string[] {
    const labels = new Set<string>();
    this.visibleGroups.forEach((g) => {
      this.getGroupMissingFields(g).forEach((f) => labels.add(f.label));
    });
    return Array.from(labels);
  }

  getGroupMissingFields(
    group: ReelImportPreviewGroup,
  ): { key: string; label: string }[] {
    return REEL_IMPORT_COMPLETION_FIELDS.filter((field) =>
      group.rows.some((row) => !String(row[field.key] ?? "").trim()),
    ).map((field) => ({ key: String(field.key), label: field.label }));
  }

  isFieldMissing(row: ReelImportPreviewRow, key: string): boolean {
    if (!REEL_IMPORT_COMPLETION_FIELDS.some((f) => f.key === key)) {
      return false;
    }
    return !String(row[key] ?? "").trim();
  }

  onHeaderValueChange(
    group: ReelImportPreviewGroup,
    key: string,
    value: string,
  ): void {
    group.headerValues[key] = value;
    const trimmed = (value ?? "").trim();
    if (trimmed === "") {
      return;
    }
    group.rows.forEach((row) => {
      if (key === "initialQuantity") {
        const n = Number(value);
        row[key] = Number.isFinite(n) ? n : value;
      } else {
        row[key] = value;
      }
    });
  }

  onAutoFillToggle(group: ReelImportPreviewGroup, checked: boolean): void {
    group.autoFillDefaults = checked;
    if (checked) {
      group.rows.forEach((row) => this.applyDefaultFields(row));
    } else {
      group.rows.forEach((row) => this.clearDefaultFields(row));
    }
  }

  onCellChange(
    group: ReelImportPreviewGroup,
    row: ReelImportPreviewRow,
    key: string,
  ): void {
    if (key === "initialQuantity") {
      const n = Number(row.initialQuantity);
      if (Number.isFinite(n) && n < 0) {
        row.initialQuantity = 0;
      }
    }
    void group;
    void row;
  }

  isDateColumn(key: string): boolean {
    return key === "manufacturingDate" || key === "expirationDate";
  }

  /** yyyyMMdd → yyyy-MM-dd cho input[type=date]. */
  toDateInputValue(value: string | null | undefined): string {
    const digits = String(value ?? "").replace(/\D/g, "");
    if (digits.length !== 8) {
      return "";
    }
    return `${digits.slice(0, 4)}-${digits.slice(4, 6)}-${digits.slice(6, 8)}`;
  }

  /** yyyy-MM-dd từ date picker → yyyyMMdd. */
  fromDateInputValue(value: string | null | undefined): string {
    const digits = String(value ?? "").replace(/\D/g, "");
    return digits.length === 8 ? digits : "";
  }

  onHeaderDateChange(
    group: ReelImportPreviewGroup,
    key: string,
    dateInputValue: string,
  ): void {
    this.onHeaderValueChange(
      group,
      key,
      this.fromDateInputValue(dateInputValue),
    );
  }

  onRowDateChange(
    row: ReelImportPreviewRow,
    key: string,
    dateInputValue: string,
  ): void {
    row[key] = this.fromDateInputValue(dateInputValue);
  }

  // ==================== Location autocomplete ====================

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

  onHeaderLocationSelected(
    group: ReelImportPreviewGroup,
    selected: string | WarehouseLocation,
  ): void {
    const name = this.resolveLocationName(selected);
    group.headerValues["locationOverride"] = name;
    if (!name) {
      return;
    }
    group.rows.forEach((row) => {
      row.locationOverride = name;
    });
  }

  onRowLocationSelected(
    row: ReelImportPreviewRow,
    selected: string | WarehouseLocation,
  ): void {
    row.locationOverride = this.resolveLocationName(selected);
  }

  // ==================== SAP warehouse autocomplete ====================

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

  onHeaderSapWarehouseSelected(
    group: ReelImportPreviewGroup,
    code: string,
  ): void {
    const normalized = (code ?? "").trim();
    group.headerValues["storageUnit"] = normalized;
    if (!normalized) {
      return;
    }
    group.rows.forEach((row) => {
      row.storageUnit = normalized;
    });
  }

  onRowSapWarehouseSelected(row: ReelImportPreviewRow, code: string): void {
    row.storageUnit = (code ?? "").trim();
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onConfirm(): void {
    const rows = this.groups.flatMap((g) => g.rows.map((r) => ({ ...r })));
    this.dialogRef.close(rows);
  }

  trackByGroup(_: number, group: ReelImportPreviewGroup): string {
    return group.key;
  }

  trackByRow(_: number, row: ReelImportPreviewRow): string {
    return row.uid;
  }

  private groupMatchesSapSearch(
    group: ReelImportPreviewGroup,
    term: string,
  ): boolean {
    if (group.groupBy === "sap") {
      const groupKey = group.key.replace(/^sap:/i, "").toLowerCase();
      if (groupKey.includes(term) || group.label.toLowerCase().includes(term)) {
        return true;
      }
    }
    return group.rows.some((row) =>
      String(row.sapCode ?? "")
        .toLowerCase()
        .includes(term),
    );
  }

  private groupMatchesPartSearch(
    group: ReelImportPreviewGroup,
    term: string,
  ): boolean {
    if (group.groupBy === "part") {
      const groupKey = group.key.replace(/^part:/i, "").toLowerCase();
      if (groupKey.includes(term) || group.label.toLowerCase().includes(term)) {
        return true;
      }
    }
    return group.rows.some((row) =>
      String(row.partNumber ?? "")
        .toLowerCase()
        .includes(term),
    );
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

  private resolveLocationName(selected: string | WarehouseLocation): string {
    if (selected == null) {
      return "";
    }
    if (typeof selected !== "string") {
      return (selected.locationName ?? selected.locationFullName ?? "").trim();
    }
    const trimmed = selected.trim();
    const matched = this.filteredLocationOptions.find(
      (w) => w.locationFullName === trimmed || w.locationName === trimmed,
    );
    return matched?.locationName ?? trimmed;
  }

  private buildGroups(rows: ReelImportPreviewRow[]): ReelImportPreviewGroup[] {
    const map = new Map<string, ReelImportPreviewGroup>();

    rows.forEach((row) => {
      const sap = (row.sapCode ?? "").trim();
      const part = (row.partNumber ?? "").trim();
      const groupBy: "sap" | "part" = sap ? "sap" : "part";
      const rawKey = sap || part || `unknown-${row.uid}`;
      const key = `${groupBy}:${rawKey}`;

      let group = map.get(key);
      if (!group) {
        group = {
          key,
          label:
            groupBy === "sap"
              ? `Mã SAP: ${rawKey}`
              : `Mã PartNumber: ${rawKey}`,
          groupBy,
          rows: [],
          headerValues: this.emptyHeaderValues(),
          autoFillDefaults: false,
        };
        map.set(key, group);
      }
      group.rows.push(row);
    });

    return Array.from(map.values());
  }

  private emptyHeaderValues(): Record<string, string> {
    const values: Record<string, string> = {};
    this.columns.forEach((col) => {
      if (col.headerEditable) {
        values[col.key] = "";
      }
    });
    return values;
  }

  private applyDefaultFields(row: ReelImportPreviewRow): void {
    if (!String(row.userData1 ?? "").trim()) {
      row.userData1 = DEFAULT_USERDATA;
    }
    if (!String(row.userData2 ?? "").trim()) {
      row.userData2 = DEFAULT_USERDATA;
    }
    if (!String(row.userData3 ?? "").trim()) {
      row.userData3 = DEFAULT_USERDATA;
    }
    if (!String(row.userData4 ?? "").trim()) {
      row.userData4 = this.buildDefaultUserData4(row);
    }
    if (!String(row.msl ?? "").trim()) {
      row.msl = DEFAULT_MSL;
    }
  }

  private clearDefaultFields(row: ReelImportPreviewRow): void {
    if (row.userData1 === DEFAULT_USERDATA) {
      row.userData1 = "";
    }
    if (row.userData2 === DEFAULT_USERDATA) {
      row.userData2 = "";
    }
    if (row.userData3 === DEFAULT_USERDATA) {
      row.userData3 = "";
    }
    if (
      row.userData4 === this.buildDefaultUserData4(row) ||
      row.userData4 === DEFAULT_USERDATA
    ) {
      row.userData4 = "";
    }
    if (row.msl === DEFAULT_MSL) {
      row.msl = "";
    }
  }

  private buildDefaultUserData4(row: ReelImportPreviewRow): string {
    const sap = (row.sapCode ?? "").trim();
    const mfg = (row.manufacturingDate ?? "").replace(/\D/g, "");
    if (sap && mfg.length === 8) {
      return `${sap}-${mfg.slice(6, 8)}${mfg.slice(4, 6)}${mfg.slice(0, 4)}`;
    }
    return sap;
  }
}
