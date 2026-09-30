import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import {
  CreatePalletDialogComponent,
  CreatePalletDialogData,
} from "./create-pallet-dialog/create-pallet-dialog.component";
import { PalletMngtService } from "./pallet-mngt.service";
import { PrintPalletDialogComponent } from "./print-pallet-dialog/print-pallet-dialog.component";
import {
  CreatePalletDialogResult,
  PalletBoxItem,
  PalletItem,
  PrintPalletDialogData,
} from "./pallet-management.model";

@Component({
  selector: "jhi-pallet-management",
  templateUrl: "./pallet-management.component.html",
  styleUrls: ["./pallet-management.component.scss"],
  standalone: false,
})
export class PalletManagementComponent implements OnInit {
  displayedColumns = [
    "stt",
    "actions",
    "serialPallet",
    "locationName",
    "numberOfBox",
    "totalQuantity",
    "createAt",
    "createBy",
    "status",
    "note",
    "updatedAt",
    "updatedBy",
  ];
  expandedDetailColumns = ["expandedDetail"];

  dataSource = new MatTableDataSource<PalletItem>([]);
  filterValues: Record<string, string> = {
    serialPallet: "",
    locationName: "",
    numberOfBox: "",
    totalQuantity: "",
    createAt: "",
    createBy: "",
    status: "",
    note: "",
    updatedAt: "",
    updatedBy: "",
  };

  expandedSerial: string | null = null;
  boxesBySerial: Record<string, PalletBoxItem[]> = {};
  boxLoadingSerials: Record<string, boolean> = {};
  boxErrorSerials: Record<string, boolean> = {};

  pageSize = 20;
  pageSizeOptions = [10, 20, 50, 100];
  filterExpanded = false;
  isLoading = false;

  @ViewChild(MatPaginator) set paginator(p: MatPaginator) {
    if (p) {
      this.dataSource.paginator = p;
    }
  }

  @ViewChild(MatSort) set sort(s: MatSort) {
    if (s) {
      this.dataSource.sort = s;
    }
  }

  constructor(
    private dialog: MatDialog,
    private palletMngtService: PalletMngtService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.dataSource.filterPredicate = (row, filterJson) =>
      this.matchFilters(row, filterJson);
    this.loadPallets();
  }

  get nextSequence(): number {
    return this.dataSource.data.length + 1;
  }

  get mobileDataSource(): PalletItem[] {
    const data = this.dataSource.filteredData ?? this.dataSource.data ?? [];
    const pageIndex = this.dataSource.paginator?.pageIndex ?? 0;
    const pageSize = this.dataSource.paginator?.pageSize ?? this.pageSize;
    const start = pageIndex * pageSize;
    return data.slice(start, start + pageSize);
  }

  get extraFilterActiveCount(): number {
    const keys = [
      "locationName",
      "numberOfBox",
      "totalQuantity",
      "createAt",
      "createBy",
      "status",
      "note",
      "updatedAt",
      "updatedBy",
    ];
    return keys.filter((k) => (this.filterValues[k] ?? "").trim()).length;
  }

  loadPallets(): void {
    this.isLoading = true;
    this.palletMngtService.getAll().subscribe({
      next: (rows) => {
        this.dataSource.data = Array.isArray(rows) ? rows : [];
        this.isLoading = false;
      },
      error: () => {
        this.dataSource.data = [];
        this.isLoading = false;
        this.notificationService.error("Không tải được danh sách pallet.");
      },
    });
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  clearAllFilters(): void {
    Object.keys(this.filterValues).forEach((k) => {
      this.filterValues[k] = "";
    });
    this.applyFilter();
  }

  getRowIndex(row: PalletItem): number {
    const index = this.dataSource.filteredData.indexOf(row);
    return index + 1;
  }

  applyFilter(): void {
    this.dataSource.filter = JSON.stringify(this.filterValues);
    this.dataSource.paginator?.firstPage();
  }

  clearFilter(key: string): void {
    this.filterValues[key] = "";
    this.applyFilter();
  }

  displayText(value: string | number | null | undefined): string {
    if (value === null || value === undefined || value === "") {
      return "—";
    }
    return String(value);
  }

  formatDateTime(value: string | null): string {
    const raw = (value ?? "").trim();
    if (!raw) {
      return "—";
    }
    const date = new Date(raw);
    if (Number.isNaN(date.getTime())) {
      return raw;
    }
    const dd = String(date.getDate()).padStart(2, "0");
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const yyyy = date.getFullYear();
    const hh = String(date.getHours()).padStart(2, "0");
    const mi = String(date.getMinutes()).padStart(2, "0");
    return `${dd}/${mm}/${yyyy} ${hh}:${mi}`;
  }

  statusLabel(status: string | null): string {
    const raw = (status ?? "").trim();
    const key = raw.toUpperCase();
    if (key === "IN_USE" || key === "USING") {
      return "Đang sử dụng";
    }
    if (key === "UNUSED" || key === "NEW") {
      return "Chưa sử dụng";
    }
    return raw.length > 0 ? raw : "—";
  }

  isInUse(status: string | null): boolean {
    const key = (status ?? "").trim().toUpperCase();
    return key === "IN_USE" || key === "USING";
  }

  isUnused(status: string | null): boolean {
    const key = (status ?? "").trim().toUpperCase();
    return key === "UNUSED" || key === "NEW";
  }

  isExpanded(row: PalletItem): boolean {
    const serial = (row.serialPallet ?? "").trim();
    return serial.length > 0 && this.expandedSerial === serial;
  }

  toggleExpand(row: PalletItem, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    const serial = (row.serialPallet ?? "").trim();
    if (!serial) {
      return;
    }
    if (this.expandedSerial === serial) {
      this.expandedSerial = null;
      return;
    }
    this.expandedSerial = serial;
    if (this.boxesBySerial[serial] || this.boxLoadingSerials[serial]) {
      return;
    }
    this.loadBoxes(serial);
  }

  getBoxes(row: PalletItem): PalletBoxItem[] {
    const serial = (row.serialPallet ?? "").trim();
    return this.boxesBySerial[serial] ?? [];
  }

  isBoxLoading(row: PalletItem): boolean {
    const serial = (row.serialPallet ?? "").trim();
    return !!this.boxLoadingSerials[serial];
  }

  hasBoxError(row: PalletItem): boolean {
    const serial = (row.serialPallet ?? "").trim();
    return !!this.boxErrorSerials[serial];
  }

  formatDateOnly(value: string | null | undefined): string {
    const raw = (value ?? "").trim();
    if (!raw) {
      return "—";
    }
    const datePart = raw.includes("T") ? raw.slice(0, 10) : raw;
    if (/^\d{4}-\d{2}-\d{2}$/.test(datePart)) {
      return datePart;
    }
    const date = new Date(raw);
    if (Number.isNaN(date.getTime())) {
      return raw;
    }
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(CreatePalletDialogComponent, {
      width: "640px",
      maxWidth: "95vw",
      disableClose: true,
      data: { nextSequence: this.nextSequence } as CreatePalletDialogData,
      panelClass: "create-pallet-dialog-panel",
    });

    dialogRef
      .afterClosed()
      .subscribe((result: CreatePalletDialogResult | undefined) => {
        if (!result || result.created.length === 0) {
          return;
        }
        const ids: number[] = [];
        for (const pallet of result.created) {
          ids.push(pallet.id);
        }
        this.loadPallets();
        if (result.saveAndPrint) {
          this.openPrintDialog(ids, result.created);
        }
      });
  }

  onPrintPalletList(): void {
    this.openPrintDialog();
  }

  onDelete(row: PalletItem): void {
    this.dataSource.data = this.dataSource.data.filter((p) => p.id !== row.id);
    this.applyFilter();
  }

  onPrint(row: PalletItem): void {
    this.openPrintDialog([row.id]);
  }

  private loadBoxes(serial: string): void {
    this.boxLoadingSerials = { ...this.boxLoadingSerials, [serial]: true };
    this.boxErrorSerials = { ...this.boxErrorSerials, [serial]: false };
    this.palletMngtService.getBoxesBySerialPallet(serial).subscribe({
      next: (boxes) => {
        this.boxesBySerial = { ...this.boxesBySerial, [serial]: boxes };
        this.boxLoadingSerials = { ...this.boxLoadingSerials, [serial]: false };
      },
      error: () => {
        this.boxesBySerial = { ...this.boxesBySerial, [serial]: [] };
        this.boxLoadingSerials = { ...this.boxLoadingSerials, [serial]: false };
        this.boxErrorSerials = { ...this.boxErrorSerials, [serial]: true };
        this.notificationService.error(
          `Không tải được danh sách thùng của pallet ${serial}.`,
        );
      },
    });
  }

  private openPrintDialog(
    preselectedIds?: number[],
    pallets?: PalletItem[],
  ): void {
    this.dialog.open(PrintPalletDialogComponent, {
      width: "980px",
      maxWidth: "96vw",
      maxHeight: "92vh",
      disableClose: true,
      panelClass: "print-pallet-dialog-panel",
      data: {
        pallets: pallets ?? [...this.dataSource.data],
        preselectedIds,
      } as PrintPalletDialogData,
    });
  }

  private matchFilters(row: PalletItem, filterJson: string): boolean {
    let filters: Record<string, string> = {};
    try {
      filters = JSON.parse(filterJson || "{}");
    } catch {
      return true;
    }

    const includes = (
      value: string | number | null | undefined,
      key: string,
    ): boolean => {
      const term = (filters[key] ?? "").trim().toLowerCase();
      if (!term) {
        return true;
      }
      return String(value ?? "")
        .toLowerCase()
        .includes(term);
    };

    const statusTerm = (filters.status ?? "").trim().toLowerCase();
    const statusOk =
      !statusTerm ||
      this.statusLabel(row.status).toLowerCase().includes(statusTerm) ||
      String(row.status ?? "")
        .toLowerCase()
        .includes(statusTerm);

    return (
      includes(row.serialPallet, "serialPallet") &&
      includes(row.locationName, "locationName") &&
      includes(row.numberOfBox, "numberOfBox") &&
      includes(row.totalQuantity, "totalQuantity") &&
      includes(this.formatDateTime(row.createAt), "createAt") &&
      includes(row.createBy, "createBy") &&
      statusOk &&
      includes(row.note, "note") &&
      includes(this.formatDateTime(row.updatedAt), "updatedAt") &&
      includes(row.updatedBy, "updatedBy")
    );
  }
}
