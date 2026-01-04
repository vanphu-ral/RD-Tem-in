import {
  Component,
  Inject,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
} from "@angular/core";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { trigger, transition, style, animate } from "@angular/animations";
import { ZXingScannerComponent } from "@zxing/ngx-scanner";
import { BarcodeFormat } from "@zxing/library";
import { CommonModule } from "@angular/common";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { QRCodeComponent } from "angularx-qrcode";
import { MatTabsModule } from "@angular/material/tabs";
import { ZXingScannerModule } from "@zxing/ngx-scanner";
import { FormsModule } from "@angular/forms";
import { HttpClient } from "@angular/common/http";
import { HttpClientModule } from "@angular/common/http";
import { PlanningWorkOrderService } from "../service/planning-work-order.service";
import { finalize } from "rxjs/operators";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../confirm-dialog/confirm-dialog.component";
import { BoxDetailData } from "../detail-box-dialog/detail-box-dialog.component";

export interface PalletData {
  id: string;
  maPallet: string;
  tenSanPham: string;
  qrCode: string;
  thungScan?: number;
  soThung: number;
  maLenhSanXuatId: number;
  validReelIds?: string[];
  existingScannedGlobal?: Set<string>;
  wmsSendStatus?: boolean;
  boxItems?: any[];
  locationCode?: string;
  locationId?: number;
}

interface BoxScan {
  code: string;
  timestamp: Date | null;
  status: "success" | "error";
  message?: string;
  soLuong?: number;
  mappingId?: number;
}

interface UnscannedBox {
  code: string;
  soLuong?: number;
  rank?: string;
  vendor?: string;
  lotNumber?: string;
  locationCode?: string;
  locationId?: number;
}

export interface SerialBoxPalletMapping {
  id: number;
  ma_lenh_san_xuat_id: number;
  serial_box: string;
  serial_pallet: string;
  status: number;
  updated_at: string;
  updated_by: string;
}

@Component({
  selector: "jhi-scan-pallet-dialog",
  templateUrl: "./scan-pallet-dialog.component.html",
  styleUrls: ["./scan-pallet-dialog.component.scss"],
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressBarModule,
    QRCodeComponent,
    MatTabsModule,
    ZXingScannerModule,
    FormsModule,
    HttpClientModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
  ],
  animations: [
    trigger("slideIn", [
      transition(":enter", [
        style({ opacity: 0, transform: "translateY(-10px)" }),
        animate(
          "200ms ease-out",
          style({ opacity: 1, transform: "translateY(0)" }),
        ),
      ]),
    ]),
  ],
})
export class ScanPalletDialogComponent implements OnInit, OnDestroy {
  @ViewChild("scanner") scanner!: ZXingScannerComponent;
  @ViewChild("scannerInput") scannerInput!: ElementRef<HTMLInputElement>;
  scanMode: "zebra" | "camera" = "zebra";
  palletData: PalletData;

  scanModeActive = false;
  scannedCode = "";
  lastScanResult: { type: "success" | "error"; message: string } | null = null;
  isProcessing = false;

  isMobile = false;
  cameraActive = false;
  availableCameras: MediaDeviceInfo[] = [];
  selectedDevice: MediaDeviceInfo | undefined = undefined;

  scannedBoxes: BoxScan[] = [];
  errorBoxes: BoxScan[] = [];
  allBoxes: BoxScan[] = [];
  validBoxes: string[] = [];

  existingScannedBoxes: Set<string> = new Set();
  existingScannedGlobal: Set<string> = new Set();
  isLoadingHistory = false;

  unscannedBoxes: UnscannedBox[] = [];
  unscannedCodes: string[] = [];
  boxItems: BoxDetailData[] = [];
  scannedFromValidCodes: string[] = [];
  unscannedCount = 0;

  selectedUnscannedBoxes: Set<string> = new Set();
  isAddingSelectedBoxes = false;

  existingScannedBoxesGlobal: Set<string> = new Set();

  selectedTabIndex = 0;
  scannedCount = 0;

  isTestMode = true;
  formats: BarcodeFormat[] = [
    BarcodeFormat.QR_CODE,
    BarcodeFormat.CODE_128,
    BarcodeFormat.EAN_13,
  ];
  private testCounter = 1;

  constructor(
    public dialogRef: MatDialogRef<ScanPalletDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PalletData,
    private http: HttpClient,
    private planningService: PlanningWorkOrderService,
    private dialog: MatDialog,
  ) {
    this.palletData = data;
    this.boxItems = data.boxItems ?? [];
    console.log("Scan dialog constructor data:", data);
  }

  ngOnInit(): void {
    this.checkIfMobile();
    this.initializeValidBoxes();

    if (this.palletData?.existingScannedGlobal) {
      this.existingScannedGlobal = this.palletData.existingScannedGlobal;
      console.log(`Global scanned boxes: ${this.existingScannedGlobal.size}`);
    }
    this.loadExistingScannedBoxes();

    this.logDebugInfo();

    if (this.scanModeActive) {
      this.focusScannerInput();
    }
  }

  ngOnDestroy(): void {
    if (this.cameraActive) {
      this.stopCamera();
    }
  }

  checkIfMobile(): void {
    this.isMobile =
      window.innerWidth <= 768 ||
      /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
  }

  initializeValidBoxes(): void {
    if (
      this.palletData?.validReelIds &&
      this.palletData.validReelIds.length > 0
    ) {
      this.validBoxes = this.palletData.validReelIds.map((c) =>
        this.normalizeCode(c),
      );
      console.log("Valid reel IDs loaded:", this.validBoxes);
    } else {
      console.warn("No valid reel IDs provided or empty array");
      this.validBoxes = [];
    }
  }

  loadExistingScannedBoxes(): void {
    if (!this.palletData?.maPallet) {
      console.warn("Missing pallet info, cannot load existing scans");
      return;
    }

    if (!this.validBoxes || this.validBoxes.length === 0) {
      this.initializeValidBoxes();
    }

    this.isLoadingHistory = true;

    this.planningService
      .getMappings(this.palletData.maPallet)
      .pipe(finalize(() => (this.isLoadingHistory = false)))
      .subscribe({
        next: (mappings) => {
          const successMappings = mappings.filter((m) => m.status === 1);
          const errorMappings = mappings.filter((m) => m.status !== 1);

          this.existingScannedBoxes = new Set(
            successMappings.map((m) => this.normalizeCode(m.serial_box)),
          );

          // Sửa lại để bao gồm soLuong và mappingId
          this.scannedBoxes = successMappings.map((m) => {
            const normalizedCode = this.normalizeCode(m.serial_box);
            const boxInfo = this.findBoxInfo(normalizedCode);

            return {
              code: normalizedCode,
              timestamp: this.parseTimestamp(m.updated_at),
              status: "success" as const,
              soLuong: boxInfo?.soLuongTrongThung || 0,
              mappingId: m.id, // Lưu ID để xóa sau này
            };
          });

          this.errorBoxes = errorMappings.map((m) => ({
            code: this.normalizeCode(m.serial_box),
            timestamp: this.parseTimestamp(m.updated_at),
            status: "error" as const,
            message: "scan error",
          }));

          this.allBoxes = [...this.scannedBoxes, ...this.errorBoxes];
          this.scannedCount = this.scannedBoxes.length;

          console.log(
            `Pallet local: ${this.scannedCount} boxes đã scan vào pallet này`,
          );

          this.computeFromValidAndExisting();
        },
        error: (error) => {
          if (error.status === 404) {
            console.log("No existing scans found (404) - this is normal");
            this.existingScannedBoxes = new Set();
            this.scannedBoxes = [];
            this.errorBoxes = [];
            this.allBoxes = [];
            this.computeFromValidAndExisting();
          } else {
            this.showScanResult(
              "error",
              "Không thể tải lịch sử scan. Vui lòng thử lại.",
            );
          }
        },
      });
  }

  //checkbox map pallet
  toggleUnscannedBox(code: string): void {
    if (this.selectedUnscannedBoxes.has(code)) {
      this.selectedUnscannedBoxes.delete(code);
    } else {
      // Check sức chứa
      const currentTotal = this.scannedCount + this.selectedUnscannedBoxes.size;
      const maxCapacity = this.palletData?.thungScan ?? 0;

      if (currentTotal >= maxCapacity) {
        this.showScanResult(
          "error",
          `Đã đạt sức chứa tối đa (${maxCapacity} thùng)`,
        );
        return;
      }

      this.selectedUnscannedBoxes.add(code);
    }

    console.log("Selected boxes:", Array.from(this.selectedUnscannedBoxes));
  }
  // Check xem có thể chọn thêm không
  canSelectMore(): boolean {
    const currentTotal = this.scannedCount + this.selectedUnscannedBoxes.size;
    const maxCapacity = this.palletData?.thungScan ?? 0;
    return currentTotal < maxCapacity;
  }
  selectAllUnscanned(): void {
    const maxCapacity = this.palletData?.thungScan ?? 0;
    const availableSlots = maxCapacity - this.scannedCount;

    if (availableSlots <= 0) {
      this.showScanResult("error", "Pallet đã đầy");
      return;
    }

    // Chọn tối đa số lượng slot còn trống
    const boxesToSelect = this.unscannedCodes.slice(0, availableSlots);
    this.selectedUnscannedBoxes = new Set(boxesToSelect);

    console.log(`Selected ${this.selectedUnscannedBoxes.size} boxes`);
  }
  deselectAll(): void {
    this.selectedUnscannedBoxes.clear();
  }
  confirmAddSelectedBoxes(): void {
    if (this.selectedUnscannedBoxes.size === 0) {
      this.showScanResult("error", "Chưa chọn box nào");
      return;
    }

    // Check lại sức chứa
    const finalTotal = this.scannedCount + this.selectedUnscannedBoxes.size;
    const maxCapacity = this.palletData?.thungScan ?? 0;

    if (finalTotal > maxCapacity) {
      this.showScanResult("error", `Vượt quá sức chứa (${maxCapacity} thùng)`);
      return;
    }

    const boxesToAdd = Array.from(this.selectedUnscannedBoxes);

    const dialogData: ConfirmDialogData = {
      title: "Xác nhận",
      message: `Xác nhận thêm ${boxesToAdd.length} thùng vào pallet?`,
      confirmText: "Xác nhận",
      cancelText: "Hủy",
      type: "info",
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "500px",
      data: dialogData,
      disableClose: false,
    });
    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.isAddingSelectedBoxes = true;
        this.processSelectedBoxesBatch(boxesToAdd);
      } else {
        this.isAddingSelectedBoxes = false;
      }
    });
  }
  // ===== TOGGLE ZEBRA MODE (CHỈ FOCUS INPUT) =====
  toggleScanMode(): void {
    this.scanModeActive = !this.scanModeActive;

    if (this.scanModeActive) {
      // Bật chế độ Zebra - CHỈ focus input
      this.scanMode = "zebra";
      this.cameraActive = false; // Đảm bảo camera TẮT

      console.log("Zebra scanner activated - Input focused");
      setTimeout(() => this.focusScannerInput(), 100);
    } else {
      console.log(" Zebra scanner deactivated");
    }
  }

  // ===== MỞ CAMERA (RIÊNG BIỆT) =====
  startCamera(): void {
    // TẮT chế độ Zebra trước
    this.scanModeActive = false;

    // BẬT camera mode
    this.scanMode = "camera";
    this.cameraActive = true;

    console.log("📷 Camera started");
  }

  stopCamera(): void {
    this.cameraActive = false;
    this.scanMode = "zebra"; // Quay lại chế độ Zebra
    if (this.scanner) {
      this.scanner.reset();
    }
    console.log("📷 Camera stopped");
  }

  focusScannerInput(): void {
    if (this.scannerInput && this.scannerInput.nativeElement) {
      this.scannerInput.nativeElement.focus();
      console.log("Input focused");
    }
  }
  processScannedCode(): void {
    if (
      !this.scannedCode ||
      this.scannedCode.trim() === "" ||
      this.isProcessing
    ) {
      return;
    }

    const raw = this.scannedCode.trim();
    // Lấy phần trước dấu '#' nếu scanner trả chuỗi dạng "code#info#..."
    const code = this.getPrimaryScannedCode(raw);

    console.log("Processing code:", raw, "=> primary:", code);

    this.validateAndAddBox(code);
    this.scannedCode = "";

    // Refocus cho lần scan tiếp theo
    if (this.scanModeActive) {
      setTimeout(() => this.focusScannerInput(), 100);
    }
  }

  switchCamera(): void {
    const currentIndex = this.availableCameras.findIndex(
      (d) => d.deviceId === this.selectedDevice?.deviceId,
    );
    const nextIndex = (currentIndex + 1) % this.availableCameras.length;
    this.selectedDevice = this.availableCameras[nextIndex];
  }

  onCamerasFound(devices: MediaDeviceInfo[]): void {
    this.availableCameras = devices;
    if (devices.length > 0) {
      const backCamera = devices.find((d) =>
        d.label.toLowerCase().includes("back"),
      );
      this.selectedDevice = backCamera ?? devices[0];
    }
  }

  onScanSuccess(code: string): void {
    if (!this.isProcessing) {
      this.validateAndAddBox(code);
    }
  }

  onPermissionResponse(hasPermission: boolean): void {
    if (hasPermission) {
      console.log("Camera permission granted");
    } else {
      console.warn("Camera permission denied");
      this.showScanResult("error", "Không có quyền truy cập camera");
      this.cameraActive = false;
    }
  }

  validateAndAddBox(code: string): void {
    if (this.isProcessing) {
      return;
    }

    const trimmedCode = this.getPrimaryScannedCode(code);

    // Check 1: Đã đủ số lượng?
    if (this.scannedCount >= (this.palletData?.thungScan ?? 0)) {
      this.addErrorBox(trimmedCode, "Vượt quá số lượng thùng tối đa");
      return;
    }

    // Check 2: Đã scan vào pallet này rồi? (LOCAL)
    if (this.existingScannedBoxes.has(trimmedCode)) {
      this.addErrorBox(
        trimmedCode,
        "Mã thùng đã được scan vào pallet này trước đó",
      );
      return;
    }

    // Check 3: Trùng trong session hiện tại?
    if (this.scannedBoxes.some((box) => box.code === trimmedCode)) {
      this.addErrorBox(trimmedCode, "Mã thùng đã được scan trong phiên này");
      return;
    }

    // ===== Check 4: ĐÃ SCAN VÀO PALLET KHÁC CHƯA? (GLOBAL) =====
    if (this.existingScannedGlobal.has(trimmedCode)) {
      this.addErrorBox(
        trimmedCode,
        "Mã thùng đã được scan vào pallet khác trong order này",
      );
      return;
    }

    // Check 5: Có hợp lệ không?
    const isValid = this.validBoxes.some(
      (validCode) => validCode.trim() === trimmedCode,
    );

    if (!isValid) {
      this.addErrorBox(
        trimmedCode,
        "Mã thùng không hợp lệ hoặc không thuộc lệnh sản xuất này",
      );
      this.sendMappingRequest(trimmedCode, 0);
      return;
    }
    // Check 6: Kiểm tra trạng thái wms_send_status
    if (this.palletData?.wmsSendStatus === true) {
      this.addErrorBox(
        trimmedCode,
        "Pallet đã được gửi WMS, không thể scan thêm",
      );
      return;
    }

    // Valid box - gọi API
    this.isProcessing = true;
    this.sendMappingRequest(trimmedCode, 1, true);
  }

  sendMappingRequest(
    serialBox: string,
    status: number,
    isSuccessCase: boolean = false,
  ): void {
    this.planningService
      .sendMappingRequest(
        this.palletData.maLenhSanXuatId,
        serialBox,
        this.palletData.maPallet,
        status,
      )
      .pipe(
        finalize(() => {
          if (isSuccessCase) {
            this.isProcessing = false;
          }
        }),
      )
      .subscribe({
        next: (response) => {
          // THÊM LOG ĐỂ XEM CẤU TRÚC RESPONSE
          console.log("🔍 API Response:", response);

          if (isSuccessCase) {
            // Thử nhiều cách lấy ID
            const mappingId =
              response?.id ||
              response?.data?.id ||
              response?.result?.id ||
              response?.mapping?.id;

            console.log("Extracted mappingId:", mappingId);

            this.addSuccessBox(serialBox, mappingId);
            this.existingScannedBoxes.add(serialBox.trim());
          }
        },
        error: (error) => {
          console.error("Error saving mapping:", error);
          if (isSuccessCase) {
            this.showScanResult(
              "error",
              "Lỗi khi lưu dữ liệu. Vui lòng thử lại.",
            );
            this.playErrorSound();
          }
        },
      });
  }

  // Cập nhật addSuccessBox để lưu soLuong
  addSuccessBox(code: string, mappingId?: number): void {
    const boxInfo = this.findBoxInfo(code);

    const box: BoxScan = {
      code,
      timestamp: new Date(),
      status: "success",
      soLuong: boxInfo?.soLuongTrongThung || 0,
      mappingId: mappingId,
    };

    this.scannedBoxes.push(box);
    this.allBoxes.push(box);
    this.scannedCount++;

    this.existingScannedGlobal.add(code);

    this.computeFromValidAndExisting();

    this.showScanResult(
      "success",
      `Scan thành công! (${this.scannedCount}/${this.palletData.thungScan})`,
    );
    this.playSuccessSound();
  }

  addErrorBox(code: string, message: string): void {
    const box: BoxScan = {
      code,
      timestamp: new Date(),
      status: "error",
      message,
    };

    this.errorBoxes.push(box);
    this.allBoxes.push(box);

    this.showScanResult("error", message);
    this.playErrorSound();
  }

  showScanResult(type: "success" | "error", message: string): void {
    this.lastScanResult = { type, message };
    setTimeout(() => {
      this.lastScanResult = null;
    }, 3000);
  }

  removeSuccessBox(index: number): void {
    const box = this.scannedBoxes[index];

    if (!box.mappingId) {
      this.showScanResult("error", "Không tìm thấy ID mapping để xóa");
      return;
    }

    const dialogData: ConfirmDialogData = {
      title: "Xác nhận xóa",
      message: `Xác nhận xóa box "${box.code}" khỏi pallet?`,
      confirmText: "Xóa",
      cancelText: "Hủy",
      type: "warning",
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "400px",
      data: dialogData,
      disableClose: false,
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.performDeleteMapping(box, index);
      }
    });
  }

  removeErrorBox(index: number): void {
    const box = this.errorBoxes[index];
    this.errorBoxes.splice(index, 1);

    const allIndex = this.allBoxes.findIndex((b) => b.code === box.code);
    if (allIndex > -1) {
      this.allBoxes.splice(allIndex, 1);
    }
  }

  getProgressPercent(): number {
    if (this.palletData.thungScan === 0) {
      return 0;
    }
    return Math.round(
      (this.scannedCount / (this.palletData?.thungScan ?? 0)) * 100,
    );
  }

  playSuccessSound(): any {
    const audio = new Audio();
    audio.src = "../../../content/images/successed-295058.mp3";
    audio.load();
    audio.play();
  }

  playErrorSound(): any {
    const audio = new Audio();
    audio.src = "../../../content/images/beep_warning.mp3";
    audio.load();
    audio.play();
  }

  onComplete(): void {
    if (this.scannedBoxes.length === 0) {
      this.showScanResult("error", "Chưa có box nào được scan");
      return;
    }

    this.stopCamera();

    this.dialogRef.close({
      success: true,
      palletId: this.palletData.id,
      scannedBoxes: this.scannedBoxes,
      errorBoxes: this.errorBoxes,
      totalScanned: this.scannedCount,
      progressPercent: this.getProgressPercent(),
    });
  }
  // Check xem box có được chọn không
  isBoxSelected(code: string): boolean {
    return this.selectedUnscannedBoxes.has(code);
  }
  // Get số box đã chọn
  getSelectedCount(): number {
    return this.selectedUnscannedBoxes.size;
  }

  // Get số slot còn trống
  getAvailableSlots(): number {
    const maxCapacity = this.palletData?.thungScan ?? 0;
    return Math.max(0, maxCapacity - this.scannedCount);
  }
  onClose(): void {
    this.stopCamera();

    this.dialogRef.close({
      success: false,
      totalScanned: this.scannedCount,
      progressPercent: this.getProgressPercent(),
      scannedBoxes: this.scannedBoxes,
    });
  }
  logDebugInfo(): void {
    console.group("🐛 DETAILED DEBUG INFO");

    console.log("1️⃣ Valid boxes (codes to scan):", this.validBoxes);

    console.log("2️⃣ BoxItems structure:");
    this.boxItems.forEach((box, i) => {
      console.log(`   Box ${i}:`, {
        tenSanPham: box.tenSanPham,
        kho: box.kho,
        soLuongTrongThung: box.soLuongTrongThung,
        subItemsCount: box.subItems?.length || 0,
        firstSubItem: box.subItems?.[0],
      });
    });

    console.log("3️⃣ Flattened boxes:");
    const flattened = this.flattenBoxItems(this.boxItems || []);
    console.table(flattened.slice(0, 10));

    console.log("4️⃣ Matching test:");
    if (this.validBoxes.length > 0 && flattened.length > 0) {
      const testCode = this.validBoxes[0];
      const found = flattened.find(
        (b) => this.normalizeCode(b.serialBox || "") === testCode,
      );
      console.log(`   Testing code: ${testCode}`);
      console.log(`   Found:`, found);
    }

    console.log("5️⃣ Unscanned boxes result:", this.unscannedBoxes);

    console.groupEnd();
  }

  // Helper method để tìm thông tin box
  private findBoxInfo(code: string): any {
    const flattenedBoxes = this.flattenBoxItems(this.boxItems || []);
    return flattenedBoxes.find(
      (b) => this.normalizeCode(b.serialBox || "") === code,
    );
  }
  private normalizeCode(code: string | null | undefined): string {
    return (code ?? "").toString().trim().toUpperCase();
  }

  private performDeleteMapping(box: BoxScan, index: number): void {
    this.isProcessing = true;

    this.planningService
      .deleteMapping(box.mappingId!)
      .pipe(finalize(() => (this.isProcessing = false)))
      .subscribe({
        next: () => {
          this.scannedBoxes.splice(index, 1);
          this.scannedCount--;

          const allIndex = this.allBoxes.findIndex((b) => b.code === box.code);
          if (allIndex > -1) {
            this.allBoxes.splice(allIndex, 1);
          }

          this.existingScannedBoxes.delete(box.code.trim());
          this.existingScannedGlobal.delete(box.code.trim());

          this.computeFromValidAndExisting();

          this.showScanResult("success", "Đã xóa box khỏi pallet");
        },
        error: (error) => {
          console.error("Error deleting box:", error);
          this.showScanResult("error", "Không thể xóa box. Vui lòng thử lại.");
        },
      });
  }

  private computeFromValidAndExisting(): void {
    const expected = (this.validBoxes || []).map((c) => this.normalizeCode(c));

    this.scannedFromValidCodes = expected.filter((code) =>
      this.existingScannedGlobal.has(code),
    );

    const unscanned = expected.filter(
      (code) => !this.existingScannedGlobal.has(code),
    );

    console.log("🔍 Debug Info:", {
      totalExpected: expected.length,
      unscannedCount: unscanned.length,
      boxItemsCount: this.boxItems?.length || 0,
    });

    // Use helper to flatten
    const flattenedBoxes = this.flattenBoxItems(this.boxItems || []);

    console.log("Flattened boxes:", flattenedBoxes.length);
    if (flattenedBoxes.length > 0) {
      console.log("Sample:", flattenedBoxes[0]);
    }

    // Map unscanned codes
    this.unscannedBoxes = unscanned.map((code) => {
      const boxInfo = flattenedBoxes.find(
        (b) => this.normalizeCode(b.serialBox || "") === code,
      );

      if (boxInfo) {
        console.log(`Mapped ${code}:`, {
          soLuong: boxInfo.soLuongTrongThung,
          kho: boxInfo.kho,
          vendor: boxInfo.vendor,
        });
      } else {
        console.warn(` Not found: ${code}`);
      }

      return {
        code,
        soLuong: boxInfo?.soLuongTrongThung || 0,
        vendor: boxInfo?.vendor || "—",
        lotNumber: boxInfo?.lotNumber || "—",
        locationCode: boxInfo?.kho || this.palletData?.locationCode || "—",
      };
    });

    this.unscannedCodes = unscanned;
    this.unscannedCount = this.unscannedBoxes.length;

    console.log("Result:", {
      unscannedBoxesCount: this.unscannedBoxes.length,
      scannedGloballyCount: this.scannedFromValidCodes.length,
      scannedInPalletCount: this.existingScannedBoxes.size,
    });
  }
  private flattenBoxItems(boxItems: BoxDetailData[]): any[] {
    const flattened: any[] = [];

    boxItems.forEach((parent) => {
      if (!parent.subItems || !Array.isArray(parent.subItems)) {
        return;
      }

      parent.subItems.forEach((sub) => {
        flattened.push({
          // Serial/Code identifiers
          serialBox: sub.maThung || sub.reelID,
          maThung: sub.maThung,
          reelID: sub.reelID,

          // Quantity info
          soLuongTrongThung: parent.soLuongTrongThung, // From parent
          soLuong: sub.soLuong || sub.initialQuantity, // From sub

          // Location
          kho: parent.kho,
          locationCode: parent.kho,

          // Vendor & Lot
          vendor: sub.vendor ?? parent.vendor,
          lotNumber: sub.rank ?? parent.lotNumber,

          // Product info
          tenSanPham: parent.tenSanPham,
          maSanPham: parent.maSanPham,

          // Additional sub info
          partNumber: sub.partNumber,
          note: sub.note,
          qrCode: sub.qrCode,

          // Dates
          manufacturingDate: sub.manufacturingDate,
          expirationDate: sub.expirationDate,

          // Raw data for debugging
          _parentBox: parent,
          _subItem: sub,
        });
      });
    });

    return flattened;
  }
  private parseTimestamp(value: any): Date | null {
    if (!value && value !== 0) {
      return null;
    }
    const d = new Date(value);
    return isNaN(d.getTime()) ? null : d;
  }
  // Xử lý batch các boxes đã chọn
  private async processSelectedBoxesBatch(boxes: string[]): Promise<void> {
    let successCount = 0;
    let errorCount = 0;

    for (const box of boxes) {
      try {
        // Gọi API mapping và nhận response
        const response = await this.sendMappingRequestPromise(box, 1);

        // Lấy mappingId từ response
        const mappingId =
          response?.id || response?.data?.id || response?.result?.id;

        console.log(`Box ${box} - mappingId: ${mappingId}`);

        // Thêm vào danh sách thành công VỚI mappingId
        this.addSuccessBox(box, mappingId);
        this.existingScannedBoxes.add(box);
        successCount++;
      } catch (error) {
        console.error("Error adding box:", box, error);
        this.addErrorBox(box, "Lỗi khi thêm vào pallet");
        errorCount++;
      }
    }

    // Clear selection
    this.selectedUnscannedBoxes.clear();
    this.isAddingSelectedBoxes = false;

    // Cập nhật lại danh sách unscanned
    this.computeFromValidAndExisting();

    // Hiển thị kết quả
    if (successCount > 0) {
      this.showScanResult(
        "success",
        `Đã thêm ${successCount} thùng thành công${errorCount > 0 ? `, ${errorCount} lỗi` : ""}`,
      );
      this.playSuccessSound();
    } else {
      this.showScanResult("error", "Không thể thêm thùng nào");
      this.playErrorSound();
    }
  }
  // Helper: Convert sendMappingRequest sang Promise
  private sendMappingRequestPromise(
    serialBox: string,
    status: number,
  ): Promise<any> {
    return new Promise((resolve, reject) => {
      this.planningService
        .sendMappingRequest(
          this.palletData.maLenhSanXuatId,
          serialBox,
          this.palletData.maPallet,
          status,
        )
        .subscribe({
          next: (response) => resolve(response),
          error: (error) => reject(error),
        });
    });
  }

  //hepler scan
  private getPrimaryScannedCode(raw: string): string {
    if (!raw) {
      return "";
    }
    const first = String(raw).split("#")[0] ?? raw;
    return first.trim();
  }
}
