import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef,
  ViewChild,
  ElementRef,
} from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { forkJoin, of, Subscription, switchMap, take } from "rxjs";
import {
  InboundWMSPallet,
  ScanPalletPayload,
  ScanWMSService,
} from "../service/scan-to-wms..service";
import { AccountService } from "app/core/auth/account.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { DialogContentExampleDialogComponent } from "app/entities/list-material/confirm-dialog/confirm-dialog.component";
import { MatDialog } from "@angular/material/dialog";

export interface ScannedItem {
  id: number;
  stt: number;
  pallet: string;
  thung: string;
  maLenhWo: string;
  maSp: string;
  tenSp: string;
  soLuong: number;
  workOrderCode?: string;
  lotNumber?: string;
  soThung: number;
  wmsSendStatus: string | null;
}

@Component({
  selector: "jhi-add-scan",
  templateUrl: "./add-scan.component.html",
  styleUrls: ["./add-scan.component.scss"],
  standalone: false,
})
export class AddScanComponent implements OnInit, OnDestroy {
  // ============================================================
  // SESSION STATE
  // ============================================================
  sessionId: number | null = null;
  isCreatingSession = false;
  isLoadingSession = false;
  isSessionCompleted = false;

  // ============================================================
  // TAB & FORM
  // ============================================================
  activeTab: "pallet" | "thung" = "pallet";
  scanForm!: UntypedFormGroup;
  // ============================================================
  // scan
  // ============================================================
  scanError: string | null = null;
  isScanning = false;

  // ============================================================
  // TABLE DATA
  // ============================================================
  scannedItems: ScannedItem[] = [];

  // ============================================================
  // UI STATE
  // ============================================================
  isSendingWMS = false;
  isMobile = false;
  isScanReady = false;
  @ViewChild("scanInputEl") scanInputEl!: ElementRef<HTMLInputElement>;
  private bpSub!: Subscription;
  constructor(
    private fb: UntypedFormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private breakpointObserver: BreakpointObserver,
    private scanWMSService: ScanWMSService,
    private accountService: AccountService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog,
  ) {}

  // ============================================================
  // LIFECYCLE
  // ============================================================
  ngOnInit(): void {
    this.scanForm = this.fb.group({
      scanInput: [""],
      ghiChu: [""],
    });

    // Đọc sessionId từ query param nếu đã có (?id=123)
    this.route.queryParamMap.pipe(take(1)).subscribe((params) => {
      const idParam = params.get("id");
      if (idParam) {
        this.sessionId = parseInt(idParam, 10);
        this.loadExistingSession(this.sessionId);
      }
    });

    this.bpSub = this.breakpointObserver
      .observe([Breakpoints.Handset])
      .subscribe((r) => {
        this.isMobile = r.matches;
      });
  }

  ngOnDestroy(): void {
    this.bpSub?.unsubscribe();
  }
  focusScanInput(): void {
    this.scanInputEl?.nativeElement?.focus();
  }

  // ============================================================
  // TAB
  // ============================================================
  setTab(tab: "pallet" | "thung"): void {
    this.activeTab = tab;
    this.scanForm.patchValue({ scanInput: "" });
  }

  // ============================================================
  // APPLY CODE — tạo session nếu chưa có, rồi thêm item
  // ============================================================
  applyCode(): void {
    const val = this.scanForm.value.scanInput?.trim();
    if (!val || this.isScanning || this.isCreatingSession) {
      return;
    }

    if (this.sessionId === null) {
      this.createSessionThenAdd(val);
    } else {
      this.callScanPallet(val);
    }
  }
  // ============================================================
  // HELPERS
  // ============================================================
  get scanPlaceholder(): string {
    return this.activeTab === "pallet"
      ? "Quét mã pallet..."
      : "Quét mã thùng...";
  }

  get itemCount(): number {
    return this.scannedItems.length;
  }

  // ============================================================
  // REMOVE / CLEAR
  // ============================================================
  removeItem(index: number): void {
    const item = this.scannedItems[index]; // lấy item từ index trước

    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận xóa",
        message: "Bạn có chắc chắn muốn xóa pallet này?",
        confirmText: "Xóa",
        cancelText: "Hủy",
      },
    });

    dialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((result) => {
        if (result !== true) {
          return;
        }

        this.scanWMSService.deleteScanItem(item.id).subscribe({
          next: () => {
            this.notificationService.success("Xóa pallet thành công!");
            this.scannedItems = this.scannedItems
              .filter((_, i) => i !== index)
              .map((s, i) => ({ ...s, stt: i + 1 }));
            this.cdr.detectChanges();
          },
          error: () => {
            this.notificationService.error("Xóa pallet thất bại!");
          },
        });
      });
  }

  clearAll(): void {
    if (this.scannedItems.length === 0) {
      return;
    }

    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận xóa tất cả",
        message: `Bạn có chắc chắn muốn xóa tất cả ${this.scannedItems.length} pallet?`,
        confirmText: "Xóa tất cả",
        cancelText: "Hủy",
      },
    });

    dialogRef
      .afterClosed()
      .pipe(
        take(1),
        switchMap((result) => {
          if (result !== true) {
            return of(null);
          }
          const deleteObservables = this.scannedItems.map((item) =>
            this.scanWMSService.deleteScanItem(item.id),
          );
          return forkJoin(deleteObservables);
        }),
      )
      .subscribe({
        next: (result) => {
          if (result === null) {
            return;
          }
          this.scannedItems = [];
          this.cdr.detectChanges();
          this.notificationService.success("Đã xóa tất cả pallet!");
        },
        error: () => {
          this.notificationService.error("Xóa thất bại, vui lòng thử lại!");
        },
      });
  }

  // ============================================================
  // NAVIGATION
  // ============================================================
  goBack(): void {
    this.router.navigate(["/scan-to-wms"]);
  }

  // ============================================================
  // ACTIONS
  // ============================================================
  onLuu(): void {
    console.log("Luu — sessionId:", this.sessionId, this.scannedItems);
  }

  onGuiQMS(): void {
    console.log("Gui QMS — sessionId:", this.sessionId, this.scannedItems);
  }

  onGuiWMS(): void {
    if (!this.sessionId) {
      this.notificationService.warning("Chưa có đơn để gửi WMS!");
      return;
    }

    if (this.scannedItems.length === 0) {
      this.notificationService.warning("Chưa có pallet nào trong đơn!");
      return;
    }

    const ref = this.dialog.open(DialogContentExampleDialogComponent, {
      data: {
        title: "Xác nhận gửi WMS",
        message: `Bạn có chắc muốn gửi đơn #${this.sessionId} lên WMS? Hành động này không thể hoàn tác.`,
        confirmText: "Gửi WMS",
        cancelText: "Hủy",
      },
      width: "400px",
    });

    ref
      .afterClosed()
      .pipe(
        take(1),
        switchMap((confirmed) => {
          if (!confirmed) {
            return of(null);
          }
          this.isSendingWMS = true;
          const sessionId = this.sessionId!;
          const sentAt = new Date().toISOString();

          return this.scanWMSService
            .submitWarehouseEntryApproval(sessionId)
            .pipe(
              switchMap(() =>
                this.accountService.identity().pipe(
                  take(1),
                  switchMap((account) =>
                    this.scanWMSService.updatePalletStatuses({
                      serialPallets: this.scannedItems.map(
                        (item) => item.pallet,
                      ),
                      wmsSendStatus: true,
                      updatedBy: account?.login ?? "unknown",
                    }),
                  ),
                ),
              ),
              switchMap(() =>
                this.scanWMSService.getSessionById(sessionId).pipe(
                  take(1),
                  switchMap((res) => {
                    const session = res.body!;
                    return this.scanWMSService.updateSession({
                      id: sessionId,
                      status: "Hoàn thành",
                      note: this.scanForm.value.ghiChu ?? "",
                      createdBy: session.createdBy,
                      createdAt: session.createdAt,
                      wmsSentAt: sentAt,
                    });
                  }),
                ),
              ),
            );
        }),
      )
      .subscribe({
        next: (result) => {
          if (result === null) {
            return;
          }
          this.isSendingWMS = false;
          this.scannedItems = this.scannedItems.map((item) => ({
            ...item,
            wmsSendStatus: "true",
          }));
          this.setSessionCompleted(true);
          this.notificationService.success("Gửi WMS thành công!");
        },
        error: (err) => {
          this.isSendingWMS = false;
          console.error("Lỗi gửi WMS:", err);
          this.notificationService.error(
            err?.error?.message ?? "Gửi WMS thất bại. Vui lòng thử lại!",
          );
        },
      });
  }
  get totalBoxCount(): number {
    return this.scannedItems.reduce(
      (acc, item) => acc + (item.soThung ?? 0),
      0,
    );
  }

  get totalQuantity(): number {
    return this.scannedItems.reduce(
      (acc, item) => acc + (item.soLuong ?? 0),
      0,
    );
  }
  // ============================================================
  // TẠO SESSION MỚI
  // ============================================================
  private createSessionThenAdd(scannedVal: string): void {
    this.isCreatingSession = true;

    const payload = {
      status: "Đang thực hiện",
      createdBy: "",
      note: this.scanForm.value.ghiChu ?? "",
      createdAt: new Date().toISOString(),
    };

    this.accountService
      .identity()
      .pipe(take(1))
      .subscribe({
        next: (account) => {
          payload.createdBy = account?.login ?? "unknown";
          this.callCreateSession(payload, scannedVal);
        },
        error: () => {
          this.callCreateSession(payload, scannedVal);
        },
      });
  }
  private callCreateSession(payload: any, scannedVal: string): void {
    this.scanWMSService.createSession(payload).subscribe({
      next: (res) => {
        if (res.body) {
          this.sessionId = res.body.id;
          this.router.navigate([], {
            relativeTo: this.route,
            queryParams: { id: this.sessionId },
            queryParamsHandling: "merge",
            replaceUrl: true,
          });
        }
        this.isCreatingSession = false;
        this.callScanPallet(scannedVal);
      },
      error: (err) => {
        console.error("Lỗi tạo session:", err);
        this.isCreatingSession = false;
      },
    });
  }

  private loadExistingSession(id: number): void {
    this.isLoadingSession = true;

    this.scanWMSService.getSessionById(id).subscribe({
      next: (res) => {
        if (res.body) {
          this.scanForm.patchValue({ ghiChu: res.body.note ?? "" });
          this.setSessionCompleted(
            (res.body.status ?? "").toLowerCase().trim() === "hoàn thành",
          );

          this.scannedItems = (res.body.inboundWMSPallets ?? []).map(
            (pallet, i) => {
              const info = pallet.warehouseNoteInfo;
              const soThung =
                pallet.listBox?.reduce(
                  (acc, b) => acc + (b.quantity ?? 0),
                  0,
                ) ?? 0;

              return {
                id: pallet.id,
                stt: i + 1,
                pallet: pallet.serialPallet ?? "",
                thung: pallet.listBox?.[0]?.boxCode ?? "",
                maLenhWo: info?.work_order_code ?? "",
                maSp: info?.sap_code ?? "",
                tenSp: info?.sap_name ?? "",
                soLuong: soThung,
                workOrderCode: info?.work_order_code ?? "",
                lotNumber: info?.lot_number ?? "",
                soThung: pallet.listBox?.length ?? 0,
                wmsSendStatus: pallet.wmsSendStatus ?? null,
              };
            },
          );
        }
        this.isLoadingSession = false;
      },
      error: (err) => {
        console.error("Lỗi load session:", err);
        this.isLoadingSession = false;
      },
    });
  }

  private callScanPallet(serialPallet: string): void {
    // Check trùng trước khi gọi API
    const isDuplicate = this.scannedItems.some(
      (item) => item.pallet.toLowerCase() === serialPallet.toLowerCase(),
    );

    if (isDuplicate) {
      this.notificationService.warning(
        `Mã "${serialPallet}" đã có trong danh sách!`,
      );
      this.scanForm.patchValue({ scanInput: "" });
      return;
    }

    this.isScanning = true;
    this.scanError = null;

    this.accountService
      .identity()
      .pipe(take(1))
      .subscribe({
        next: (account) => {
          const payload: ScanPalletPayload = {
            inbound_wms_session_id: this.sessionId!,
            serial_pallet: serialPallet,
            scaned_by: account?.login ?? "unknown",
            scaned_at: new Date().toISOString(),
          };

          this.scanWMSService.scanPallet(payload).subscribe({
            next: (res) => {
              if (res.body) {
                const palletData: InboundWMSPallet = {
                  ...res.body.inboundpalletInfo,
                  warehouseNoteInfo: res.body.warehouseNoteInfo,
                };

                if (palletData.wmsSendStatus === "true") {
                  this.notificationService.warning(
                    `Pallet "${serialPallet}" đã được gửi WMS, không thể thêm vào danh sách!`,
                  );
                  this.isScanning = false;
                  this.scanForm.patchValue({ scanInput: "" });
                  return;
                }

                this.appendPalletToList(palletData);
                this.cdr.detectChanges();
              }
              this.isScanning = false;
              this.scanForm.patchValue({ scanInput: "" });
            },
            error: (err) => {
              console.error("Lỗi scan pallet:", err);
              this.scanError =
                err?.error?.message ?? "Quét mã thất bại. Vui lòng thử lại.";
              this.isScanning = false;
            },
          });
        },
        error: () => {
          this.isScanning = false;
        },
      });
  }

  private appendPalletToList(pallet: InboundWMSPallet): void {
    const info = pallet.warehouseNoteInfo;
    const soLuong =
      pallet.listBox?.reduce((acc, b) => acc + (b.quantity ?? 0), 0) ?? 0;

    const newItem: ScannedItem = {
      id: pallet.id,
      stt: 0,
      pallet: pallet.serialPallet ?? "",
      thung: pallet.listBox?.[0]?.boxCode ?? "",
      maLenhWo: info?.work_order_code ?? "",
      maSp: info?.sap_code ?? "",
      tenSp: info?.sap_name ?? "",
      soLuong,
      workOrderCode: info?.work_order_code ?? "",
      lotNumber: info?.lot_number ?? "",
      soThung: pallet.listBox?.length ?? 0,
      wmsSendStatus: pallet.wmsSendStatus ?? null,
    };

    this.scannedItems = [newItem, ...this.scannedItems].map((item, i) => ({
      ...item,
      stt: i + 1,
    }));
  }
  // Thêm helper method
  private setSessionCompleted(completed: boolean): void {
    this.isSessionCompleted = completed;
    if (completed) {
      this.scanForm.get("scanInput")?.disable();
    } else {
      this.scanForm.get("scanInput")?.enable();
    }
  }
}
