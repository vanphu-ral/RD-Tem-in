import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { PageEvent } from "@angular/material/paginator";
import { Router } from "@angular/router";
import { Subscription } from "rxjs";
import { MobileDetailDialogComponent } from "./dialog/mobile-detail.dialog.component";
import {
  ScanWMSService,
  InboundWMSSession,
} from "../service/scan-to-wms..service";

// ============================================================
// MODELS — map từ API response sang view model
// ============================================================
export interface ScanRecord {
  id: number;
  ngayTao: string | null;
  nguoiTao: string | null;
  slPallet: string;
  slThung: string;
  tongSoSp: number;
  ghiChu: string | null;
  thoiGianGuiKho: string | null;
  trangThai: "hoan-thanh" | "chua-hoan-thanh";
  raw: InboundWMSSession;
}

// ============================================================
// COMPONENT
// ============================================================
@Component({
  selector: "jhi-scan-to-wms",
  templateUrl: "./scan-to-wms.component.html",
  styleUrls: ["./scan-to-wms.component.scss"],
  standalone: false,
})
export class ScanToWMSComponent implements OnInit, OnDestroy {
  // ============================================================
  // FILTER STATE
  // ============================================================
  filterForm!: UntypedFormGroup;
  sessionDetailCache: Map<number, InboundWMSSession> = new Map();
  loadingDetailIds: Set<number> = new Set();

  trangThaiOptions: { label: string; value: string }[] = [
    { label: "Tất cả", value: "tat-ca" },
    { label: "Hoàn thành", value: "hoan-thanh" },
    { label: "Đang thực hiện", value: "chua-hoan-thanh" },
  ];

  // ============================================================
  // TABLE DATA
  // ============================================================
  records: ScanRecord[] = [];
  expandedRows: Set<number> = new Set();

  // ============================================================
  // PAGINATION
  // ============================================================
  totalItems = 0;
  pageSize = 15;
  currentPage = 0;
  pageSizeOptions = [15, 30, 50];

  // ============================================================
  // LOADING / ERROR STATE
  // ============================================================
  isLoading = false;
  isMobile = false;
  errorMsg: string | null = null;

  private bpSub!: Subscription;

  constructor(
    private fb: UntypedFormBuilder,
    private dialog: MatDialog,
    private router: Router,
    private breakpointObserver: BreakpointObserver,
    private scanWMSService: ScanWMSService,
  ) {}

  // ============================================================
  // LIFECYCLE
  // ============================================================
  ngOnInit(): void {
    this.initForm();
    this.loadSessions();

    this.bpSub = this.breakpointObserver
      .observe([Breakpoints.Handset])
      .subscribe((result) => {
        this.isMobile = result.matches;
      });
  }

  ngOnDestroy(): void {
    this.bpSub?.unsubscribe();
  }

  // ============================================================
  // FORM INITIALIZATION
  // ============================================================
  initForm(): void {
    this.filterForm = this.fb.group({
      thoiGian: [null],
      ghiChu: [""],
      trangThai: ["tat-ca"],
    });
  }

  // ============================================================
  // DATA LOADING
  // ============================================================
  loadSessions(): void {
    this.isLoading = true;
    this.errorMsg = null;

    const f = this.filterForm.value;

    const filters = {
      createdAt: f.thoiGian ? new Date(f.thoiGian).toDateString() : null,
      note: f.ghiChu?.trim() || null,
      status:
        f.trangThai !== "tat-ca"
          ? this.mapTrangThaiToStatus(f.trangThai)
          : null,
    };

    this.scanWMSService
      .getSessions(this.currentPage, this.pageSize, filters)
      .subscribe({
        next: (res) => {
          const body = res.body ?? [];
          const totalHeader = res.headers.get("X-Total-Count");
          this.totalItems = totalHeader
            ? parseInt(totalHeader, 10)
            : body.length;
          this.records = body.map((item) => this.mapToRecord(item));
          this.isLoading = false;
        },
        error: (err) => {
          console.error("Lỗi tải dữ liệu:", err);
          this.errorMsg = "Không thể tải dữ liệu. Vui lòng thử lại.";
          this.isLoading = false;
        },
      });
  }

  getGroupedByWOWithBoxes(session: InboundWMSSession): {
    woCode: string;
    maSp: string;
    tenSp: string;
    maThung: string;
    component: string;
    soLuong: number;
  }[] {
    const result: {
      woCode: string;
      maSp: string;
      tenSp: string;
      maThung: string;
      component: string;
      soLuong: number;
    }[] = [];

    for (const pallet of session.inboundWMSPallets ?? []) {
      const info = pallet.warehouseNoteInfo;
      for (const box of pallet.listBox ?? []) {
        result.push({
          woCode: info?.work_order_code ?? "—",
          maSp: info?.sap_code ?? "—",
          tenSp: info?.sap_name ?? "—",
          maThung: box.boxCode ?? "—",
          component: info?.ma_lenh_san_xuat ?? "—",
          soLuong: box.quantity ?? 0,
        });
      }
    }

    return result;
  }

  // ============================================================
  // FILTER ACTIONS
  // ============================================================
  applyFilter(): void {
    this.currentPage = 0;
    this.loadSessions();
  }

  // ============================================================
  // ROW EXPAND / COLLAPSE
  // ============================================================
  toggleRow(id: number): void {
    if (this.expandedRows.has(id)) {
      this.expandedRows.delete(id);
    } else {
      this.expandedRows.add(id);
      // Chỉ load nếu chưa có trong cache
      if (!this.sessionDetailCache.has(id)) {
        this.loadSessionDetail(id);
      }
    }
  }
  loadSessionDetail(id: number): void {
    this.loadingDetailIds.add(id);

    this.scanWMSService.getSessionById(id).subscribe({
      next: (res) => {
        if (res.body) {
          this.sessionDetailCache.set(id, res.body);
          // Cập nhật lại record trong danh sách với dữ liệu chi tiết
          this.records = this.records.map((r) =>
            r.id === id ? { ...r, raw: res.body! } : r,
          );
        }
        this.loadingDetailIds.delete(id);
      },
      error: (err) => {
        console.error(`Lỗi load chi tiết session ${id}:`, err);
        this.loadingDetailIds.delete(id);
      },
    });
  }

  isLoadingDetail(id: number): boolean {
    return this.loadingDetailIds.has(id);
  }
  isExpanded(id: number): boolean {
    return this.expandedRows.has(id);
  }

  // ============================================================
  // PAGINATION
  // ============================================================
  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadSessions();
  }

  // ============================================================
  // SCAN ACTION
  // ============================================================
  openScan(): void {
    this.router.navigate(["/scan-to-wms/add-scan"]);
  }

  // ============================================================
  // VIEW DETAIL
  // ============================================================
  viewDetail(record: ScanRecord): void {
    this.router.navigate(["/scan-to-wms/add-scan"], {
      queryParams: { id: record.id },
    });
  }

  openMobileDetail(record: ScanRecord): void {
    this.dialog.open(MobileDetailDialogComponent, {
      data: { record },
      width: "100vw",
      maxWidth: "100vw",
      height: "100vh",
      panelClass: "mobile-detail-dialog",
    });
  }

  // ============================================================
  // HELPER
  // ============================================================
  //nhom theo wo
  getGroupedByWO(
    session: InboundWMSSession,
  ): { woCode: string; maSp: string; tenSp: string; soLuong: number }[] {
    const map = new Map<
      string,
      { maSp: string; tenSp: string; soLuong: number }
    >();
    const seenPallets = new Set<string>();

    for (const pallet of session.inboundWMSPallets ?? []) {
      const info = pallet.warehouseNoteInfo;
      if (!info) {
        continue;
      }

      const key = info.work_order_code ?? "—";
      const palletKey = `${key}__${pallet.serialPallet}`;

      const qty = seenPallets.has(palletKey)
        ? 0
        : (pallet.listBox?.reduce((acc, b) => acc + (b.quantity ?? 0), 0) ?? 0);

      seenPallets.add(palletKey);

      const existing = map.get(key);
      if (existing) {
        existing.soLuong += qty;
      } else {
        map.set(key, {
          maSp: info.sap_code ?? "—",
          tenSp: info.sap_name ?? "—",
          soLuong: qty,
        });
      }
    }

    return Array.from(map.entries()).map(([woCode, val]) => ({
      woCode,
      ...val,
    }));
  }
  formatDate(dateStr: string | null): string {
    if (!dateStr) {
      return "—";
    }
    try {
      return new Date(dateStr).toLocaleString("vi-VN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      });
    } catch {
      return dateStr;
    }
  }
  // ============================================================
  // MAPPING API -> VIEW MODEL
  // ============================================================
  private mapToRecord(item: InboundWMSSession): ScanRecord {
    const status = (item.status ?? "").toLowerCase().trim();
    const isDone =
      status === "hoàn thành" ||
      status === "hoan thanh" ||
      status === "done" ||
      status === "completed";

    return {
      id: item.id,
      ngayTao: item.createdAt,
      nguoiTao: item.createdBy,
      slPallet: item.numberOfPallets ? `${item.numberOfPallets}` : "—",
      slThung: item.numberOfBox ? `${item.numberOfBox}` : "—",
      tongSoSp: item.totalQuantity ?? 0,
      ghiChu: item.note,
      thoiGianGuiKho: item.wmsSentAt,
      trangThai: isDone ? "hoan-thanh" : "chua-hoan-thanh",
      raw: item,
    };
  }
  private mapTrangThaiToStatus(trangThai: string): string | null {
    const map: Record<string, string> = {
      "hoan-thanh": "Hoàn thành",
      "chua-hoan-thanh": "Đang thực hiện",
    };
    return map[trangThai] ?? null;
  }
}
