import { ChangeDetectorRef, Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatAutocompleteTrigger } from "@angular/material/autocomplete";
import {
  ReceivingSuppliesService,
  SapOwhsDto,
  WarehouseLocation,
} from "app/entities/generate-tem-in/service/receiving-supplies.service";
import {
  AggregateEditRow,
  buildDefaultUserData4,
  fromDateInputValue as fromDateInputValueUtil,
  toDateInputValue as toDateInputValueUtil,
} from "../../shared/scan-item-columns.util";

export interface ScanAggregateReelDialogData {
  rows: AggregateEditRow[];
  sapCode: string;
  partNumber: string;
  itemName: string;
  lotNumber: string;
  poCode: string;
  vendor: string;
  msl: string;
  isMobile: boolean;
}

@Component({
  selector: "jhi-scan-aggregate-reel-dialog",
  templateUrl: "./scan-aggregate-reel-dialog.component.html",
  styleUrls: ["./scan-aggregate-reel-dialog.component.scss"],
})
export class ScanAggregateReelDialogComponent {
  rows: AggregateEditRow[];
  expandedIds = new Set<string>();

  filteredLocationOptions: WarehouseLocation[] = [];
  lastLocationSearchTerm = "";
  locationSearchSettled = false;
  locationSearchPending = false;
  sapWarehouseList: SapOwhsDto[] = [];
  filteredSapWarehouseList: SapOwhsDto[] = [];
  isLoadingSapWarehouses = false;

  private locationSearchSeq = 0;
  private locationSearchTimer: ReturnType<typeof setTimeout> | null = null;
  private activeLocationTrigger: MatAutocompleteTrigger | null = null;

  constructor(
    private dialogRef: MatDialogRef<
      ScanAggregateReelDialogComponent,
      AggregateEditRow[] | null
    >,
    @Inject(MAT_DIALOG_DATA) public data: ScanAggregateReelDialogData,
    private receivingService: ReceivingSuppliesService,
    private cdr: ChangeDetectorRef,
  ) {
    this.rows = data.rows.map((r) => ({ ...r }));
    if (!data.isMobile && this.rows.length <= 8) {
      this.rows.forEach((r) => this.expandedIds.add(r.id));
    }
    void this.receivingService.initWarehouses();
    this.loadSapWarehouses();
  }

  get headerLine1(): string {
    const parts = [
      this.data.sapCode ? `Mã SAP ${this.data.sapCode}` : "",
      this.data.partNumber,
      this.data.itemName,
    ].filter(Boolean);
    return parts.join(" · ");
  }

  get headerLine2(): string {
    const parts = [
      this.data.lotNumber ? `Lô ${this.data.lotNumber}` : "Lô —",
      this.data.poCode ? `PO ${this.data.poCode}` : "",
      this.data.vendor,
      this.data.msl ? `MSL ${this.data.msl}` : "",
    ].filter(Boolean);
    return parts.join(" · ");
  }

  isExpanded(row: AggregateEditRow): boolean {
    return this.expandedIds.has(row.id);
  }

  toggleRow(row: AggregateEditRow): void {
    if (this.expandedIds.has(row.id)) {
      this.expandedIds.delete(row.id);
    } else {
      this.expandedIds.add(row.id);
    }
  }

  isDateColumn(key: string): boolean {
    return key === "manufacturingDate" || key === "expirationDate";
  }

  toDateInputValue(value: string | number | null | undefined): string {
    return toDateInputValueUtil(value == null ? "" : String(value));
  }

  onRowDateChange(row: AggregateEditRow, key: string, dateInput: string): void {
    row[key] = fromDateInputValueUtil(dateInput);
    if (key === "manufacturingDate") {
      row.userData4 = buildDefaultUserData4(row);
    }
  }

  onCellChange(row: AggregateEditRow, key: string): void {
    if (key === "initialQuantity") {
      const n = Number(row.initialQuantity);
      if (Number.isFinite(n) && n < 0) {
        row.initialQuantity = 0;
      }
    }
    if (key === "manufacturingDate" || key === "sapCode") {
      row.userData4 = buildDefaultUserData4(row);
    }
  }

  applyFirstToAll(): void {
    if (this.rows.length < 2) {
      return;
    }
    const src = this.rows[0];
    const keys = [
      "manufacturingDate",
      "expirationDate",
      "locationOverride",
      "storageUnit",
      "lotNumber",
      "msl",
      "userData1",
      "userData2",
      "userData3",
    ] as const;
    this.rows.slice(1).forEach((row) => {
      keys.forEach((key) => {
        row[key] = src[key];
      });
      row.userData4 = buildDefaultUserData4(row);
    });
    this.cdr.markForCheck();
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onConfirm(): void {
    this.dialogRef.close(this.rows);
  }

  trackByRow(_: number, row: AggregateEditRow): string {
    return row.id;
  }

  onLocationSearch(
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
          setTimeout(() => {
            const panelTrigger = this.activeLocationTrigger;
            if (panelTrigger && !panelTrigger.panelOpen) {
              panelTrigger.openPanel();
            }
          });
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

  onRowLocationSelected(
    row: AggregateEditRow,
    selected: string | WarehouseLocation,
  ): void {
    row.locationOverride = this.resolveLocationName(selected);
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

  onRowSapWarehouseSelected(row: AggregateEditRow, code: string): void {
    row.storageUnit = (code ?? "").trim();
  }

  isMissing(row: AggregateEditRow, key: string): boolean {
    return !String(row[key] ?? "").trim();
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
}
