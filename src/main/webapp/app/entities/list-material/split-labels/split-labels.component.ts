import { ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import {
  SPLIT_BUSINESS_TYPE,
  SplitInventoryQuery,
  SplitInventoryRecord,
  SplitInventoryService,
} from "app/entities/info-tem-ncc/shared/split-inventory.service";
import { toVendorNccLabel } from "app/entities/info-tem-ncc/shared/split-label-print.util";
import { VendorNccPrintDialogComponent } from "app/entities/generate-tem-in/receiving-supplies/vendor-ncc-print-dialog/vendor-ncc-print-dialog.component";
import type { VendorLabelInfoDto } from "app/entities/info-tem-ncc/services/info-tem-ncc.service";
import { openSplitLabelPrintDialog } from "app/entities/warehouse-export/split-label-print-dialog/split-label-print-dialog.component";
import {
  SplitPrintLabel,
  toLabelDate,
} from "app/entities/warehouse-export/rd-label/rd-label.model";
import { NotificationService } from "../services/notification.service";
import { AccountService } from "app/core/auth/account.service";
import { openChoiceDialog } from "app/entities/info-tem-ncc/shared/choice-dialog/choice-dialog.component";
import { forkJoin, Observable, of } from "rxjs";
import { catchError, map, switchMap, take } from "rxjs/operators";

type PrintFilter = "all" | "printed" | "unprinted";
type SearchField = NonNullable<SplitInventoryQuery["searchField"]>;

/** Nguồn tách (businessType) → nhãn hiển thị */
const SOURCE_LABELS: Record<string, string> = {
  [SPLIT_BUSINESS_TYPE.transfer]: "Tách từ chuyển kho",
  [SPLIT_BUSINESS_TYPE.vendorLabel]: "Tách từ nhập tem NCC",
};

/**
 * Tem tách chờ in — danh sách tem đã tách (Xuất chuyển kho / Nhập tem NCC) từ
 * GET /split-inventories; chọn nhiều tem để in hàng loạt theo mẫu Rạng Đông hoặc NCC.
 */
@Component({
  selector: "jhi-split-labels",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatDialogModule,
    MatIconModule,
  ],
  templateUrl: "./split-labels.component.html",
  styleUrls: ["./split-labels.component.scss"],
})
export class SplitLabelsComponent implements OnInit {
  readonly pageSizes = [20, 50, 100];
  readonly sources = [
    { value: "", label: "Tất cả nguồn" },
    {
      value: SPLIT_BUSINESS_TYPE.transfer,
      label: SOURCE_LABELS[SPLIT_BUSINESS_TYPE.transfer],
    },
    {
      value: SPLIT_BUSINESS_TYPE.vendorLabel,
      label: SOURCE_LABELS[SPLIT_BUSINESS_TYPE.vendorLabel],
    },
  ];
  readonly searchFields: { value: SearchField; label: string }[] = [
    { value: "reelId", label: "ReelID tem tách" },
    { value: "parentMaterialIdentifier", label: "Tem gốc" },
    { value: "sapCode", label: "Mã SAP" },
    { value: "lot", label: "Lô" },
  ];

  items: SplitInventoryRecord[] = [];
  total = 0;
  page = 0;
  size = 50;
  isLoading = false;
  loadError = "";

  printFilter: PrintFilter = "all";
  source = "";
  searchField: SearchField = "reelId";
  search = "";
  sourceId = "";

  /** id các tem đang chọn (trong trang hiện tại) */
  selected = new Set<number>();

  constructor(
    private splitInventoryService: SplitInventoryService,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef,
    private accountService: AccountService,
  ) {}

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.total / this.size));
  }

  get allSelected(): boolean {
    return (
      this.items.length > 0 && this.items.every((r) => this.selected.has(r.id))
    );
  }

  get someSelected(): boolean {
    return this.selected.size > 0 && !this.allSelected;
  }

  get selectedRecords(): SplitInventoryRecord[] {
    return this.items.filter((r) => this.selected.has(r.id));
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const sourceId = Number(this.sourceId.trim());
    this.isLoading = true;
    this.loadError = "";
    this.splitInventoryService
      .query({
        page: this.page,
        size: this.size,
        printStatus:
          this.printFilter === "all" ? null : this.printFilter === "printed",
        businessType: this.source || null,
        searchField: this.searchField,
        search: this.search,
        businessSourceId:
          this.sourceId.trim() && Number.isFinite(sourceId) ? sourceId : null,
      })
      .subscribe({
        next: ({
          items,
          total,
        }: {
          items: SplitInventoryRecord[];
          total: number;
        }) => {
          this.items = items;
          this.total = total;
          this.selected.clear();
          this.isLoading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.items = [];
          this.total = 0;
          this.selected.clear();
          this.isLoading = false;
          this.loadError = "Không tải được danh sách tem tách.";
          this.cdr.markForCheck();
        },
      });
  }

  /** Đổi bộ lọc → về trang đầu */
  applyFilter(): void {
    this.page = 0;
    this.load();
  }

  resetFilter(): void {
    this.printFilter = "all";
    this.source = "";
    this.searchField = "reelId";
    this.search = "";
    this.sourceId = "";
    this.applyFilter();
  }

  goPage(page: number): void {
    const next = Math.min(Math.max(0, page), this.totalPages - 1);
    if (next !== this.page) {
      this.page = next;
      this.load();
    }
  }

  onSizeChange(): void {
    this.applyFilter();
  }

  toggle(record: SplitInventoryRecord): void {
    if (this.selected.has(record.id)) {
      this.selected.delete(record.id);
    } else {
      this.selected.add(record.id);
    }
  }

  toggleAll(): void {
    if (this.allSelected) {
      this.selected.clear();
    } else {
      this.items.forEach((r) => this.selected.add(r.id));
    }
  }

  sourceLabel(record: SplitInventoryRecord): string {
    const type = (record.businessType ?? "").trim();
    return SOURCE_LABELS[type] ?? (type || "—");
  }

  isTransfer(record: SplitInventoryRecord): boolean {
    return record.businessType === SPLIT_BUSINESS_TYPE.transfer;
  }

  sapOf(record: SplitInventoryRecord): string {
    return (
      (record.sapCode ?? "").trim() ||
      (record.userData4 ?? "").split("-")[0].trim()
    );
  }

  nameOf(record: SplitInventoryRecord): string {
    return (record.spMaterialName ?? "").trim() || (record.userData3 ?? "");
  }

  trackById(_: number, record: SplitInventoryRecord): number {
    return record.id;
  }

  /** In hàng loạt (hoặc 1 tem) theo mẫu Rạng Đông / NCC */
  print(template: "rd" | "vendor", records = this.selectedRecords): void {
    if (!records.length) {
      this.notificationService.warning("Chọn ít nhất 1 tem để in.");
      return;
    }
    const closed$: Observable<unknown> =
      template === "rd"
        ? openSplitLabelPrintDialog(
            this.dialog,
            records.map((r) => this.toRdLabel(r)),
          )
        : this.openVendorPrint(records);
    // Đóng dialog in → hỏi đã in xong chưa → PATCH printStatus = true cho tem chưa in
    closed$.subscribe(() => this.confirmPrinted(records));
  }

  /** Mở dialog In tem nhà cung cấp → phát 1 lần khi đóng */
  private openVendorPrint(
    records: SplitInventoryRecord[],
  ): Observable<unknown> {
    return this.dialog
      .open(VendorNccPrintDialogComponent, {
        width: "100vw",
        height: "100vh",
        maxWidth: "100vw",
        maxHeight: "100vh",
        panelClass: "vendor-ncc-print-dialog-panel",
        data: {
          labels: records.map((r) =>
            toVendorNccLabel(r as unknown as VendorLabelInfoDto, {
              sapCode: this.sapOf(r),
              materialName: this.nameOf(r),
            }),
          ),
          requestLabel: "tem-tach",
        },
      })
      .afterClosed();
  }

  /** Không ghi nhận được kết quả máy in → người dùng xác nhận đã in xong mới cập nhật trạng thái */
  private confirmPrinted(records: SplitInventoryRecord[]): void {
    const pending = records.filter((r) => !r.printStatus);
    if (!pending.length) {
      return;
    }
    openChoiceDialog(this.dialog, {
      title: "Cập nhật trạng thái in",
      message: `Đã in xong ${pending.length} tem tách?`,
      confirmText: "Đã in xong",
      cancelText: "Chưa in",
    })
      .pipe(
        switchMap((ok) => {
          if (!ok) {
            return of(null);
          }
          return this.accountService.identity().pipe(
            take(1),
            switchMap((account) =>
              forkJoin(
                pending.map((r): Observable<boolean> => {
                  const patch$: Observable<unknown> =
                    this.splitInventoryService.updatePrintStatus(
                      r.id,
                      true,
                      account?.login ?? "unknown",
                    );
                  return patch$.pipe(
                    map(() => true),
                    catchError(() => of(false)),
                  );
                }),
              ),
            ),
          );
        }),
      )
      .subscribe((results: boolean[] | null) => {
        if (!results) {
          return;
        }
        const ok = results.filter(Boolean).length;
        if (ok === results.length) {
          this.notificationService.success(`Đã cập nhật ${ok} tem: Đã in.`);
        } else {
          this.notificationService.warning(
            `Cập nhật trạng thái in ${ok}/${results.length} tem, ${results.length - ok} lỗi.`,
          );
        }
        this.load();
      });
  }

  /** Bản ghi tem tách → tem mẫu Rạng Đông */
  private toRdLabel(r: SplitInventoryRecord): SplitPrintLabel {
    const text = (v: string | null | undefined): string => (v ?? "").trim();
    return {
      id: String(r.id),
      qrCode: text(r.vendorQrCode) || r.reelId,
      reelId: r.reelId,
      sapCode: this.sapOf(r),
      productName: this.nameOf(r),
      partNumber: text(r.partNumber),
      lot: text(r.lot),
      initialQuantity: r.initialQuantity,
      vendor: text(r.vendor),
      userData1: text(r.userData1),
      userData2: text(r.userData2),
      userData3: text(r.userData3),
      userData5: text(r.userData5),
      storageUnit: text(r.storageUnit),
      manufacturingDate: toLabelDate(r.manufacturingDate),
      expirationDate: toLabelDate(r.expirationDate),
      arrivalDate: "",
    };
  }
}
