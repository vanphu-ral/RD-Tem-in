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

interface PalletData {
  id: string;
  maPallet: string;
  tenSanPham: string;
  qrCode: string;
  soThung: number;
  maLenhSanXuatId: number;
  validReelIds?: string[];
}

interface BoxScan {
  code: string;
  timestamp: Date;
  status: "success" | "error";
  message?: string;
}

// Interface cho response từ BE
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

  palletData: PalletData;

  // Scanner state
  scanModeActive = false;
  scannedCode = "";
  lastScanResult: { type: "success" | "error"; message: string } | null = null;
  isProcessing = false; // Prevent multiple simultaneous scans

  // Mobile camera
  isMobile = false;
  cameraActive = false;
  availableCameras: MediaDeviceInfo[] = [];
  selectedDevice: MediaDeviceInfo | undefined = undefined;

  // Boxes
  scannedBoxes: BoxScan[] = [];
  errorBoxes: BoxScan[] = [];
  allBoxes: BoxScan[] = [];
  validBoxes: string[] = [];

  // NEW: Store boxes already scanned in this pallet (from BE)
  existingScannedBoxes: Set<string> = new Set();
  isLoadingHistory = false;

  selectedTabIndex = 0;
  scannedCount = 0;

  // Test mode
  isTestMode = true; // Set to false in production
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
  ) {
    this.palletData = data;
    console.log("Scan dialog constructor data:", data);
  }

  ngOnInit(): void {
    this.checkIfMobile();
    this.initializeValidBoxes();
    this.loadExistingScannedBoxes(); // Load boxes đã scan từ BE

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
      this.palletData.validReelIds &&
      this.palletData.validReelIds.length > 0
    ) {
      this.validBoxes = this.palletData.validReelIds;
      console.log("Valid reel IDs loaded:", this.validBoxes);
      console.log("Total valid boxes:", this.validBoxes.length);
    } else {
      console.warn("No valid reel IDs provided or empty array");
      this.validBoxes = [];
    }
  }

  // ===== NEW: Load boxes đã scan từ BE =====
  loadExistingScannedBoxes(): void {
    if (!this.palletData.maPallet) {
      console.warn("Missing pallet info, cannot load existing scans");
      return;
    }

    this.isLoadingHistory = true;

    this.planningService
      .getMappings(this.palletData.maPallet)
      .pipe(finalize(() => (this.isLoadingHistory = false)))
      .subscribe({
        next: (mappings) => {
          console.log(" Existing scanned boxes loaded:", mappings);

          const successMappings = mappings.filter((m) => m.status === 1);

          this.existingScannedBoxes = new Set(
            successMappings.map((m) => m.serial_box.trim()),
          );

          this.scannedBoxes = successMappings.map((m) => ({
            code: m.serial_box,
            timestamp: new Date(m.updated_at),
            status: "success" as const,
          }));

          this.allBoxes = [...this.scannedBoxes];
          this.scannedCount = this.scannedBoxes.length;

          console.log(
            ` Loaded ${this.scannedCount} boxes already scanned in this pallet`,
          );
          console.log("Existing boxes:", Array.from(this.existingScannedBoxes));
        },
        error: (error) => {
          console.error("Error loading existing scanned boxes:", error);

          if (error.status === 404) {
            console.log("No existing scans found (404) - this is normal");
            this.existingScannedBoxes = new Set();
          } else {
            this.showScanResult(
              "error",
              "Không thể tải lịch sử scan. Vui lòng thử lại.",
            );
          }
        },
      });
  }

  // Desktop Scanner
  toggleScanMode(): void {
    this.scanModeActive = !this.scanModeActive;
    if (this.scanModeActive) {
      setTimeout(() => this.focusScannerInput(), 100);
    }
  }

  focusScannerInput(): void {
    if (this.scannerInput) {
      this.scannerInput.nativeElement.focus();
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

    const code = this.scannedCode.trim();
    this.validateAndAddBox(code);

    // Clear input
    this.scannedCode = "";

    // Refocus for continuous scanning
    if (this.scanModeActive) {
      setTimeout(() => this.focusScannerInput(), 100);
    }
  }

  // Mobile Camera
  startCamera(): void {
    try {
      this.cameraActive = true;
    } catch (error) {
      console.error("Error starting camera:", error);
      this.showScanResult("error", "Không thể mở camera");
    }
  }

  pauseCamera(): void {
    this.cameraActive = false;
  }

  stopCamera(): void {
    this.cameraActive = false;
    if (this.scanner) {
      this.scanner.reset();
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
      this.cameraActive = true;
    } else {
      console.warn("Camera permission denied");
      this.showScanResult("error", "Không có quyền truy cập camera");
    }
  }

  // ===== UPDATED: Validation với check từ BE =====
  validateAndAddBox(code: string): void {
    if (this.isProcessing) {
      console.log("⏳ Still processing, please wait...");
      return;
    }

    const trimmedCode = code.trim();

    console.log("🔍 Validating code:", trimmedCode);
    console.log("📊 Current stats:", {
      scannedCount: this.scannedCount,
      maxAllowed: this.palletData.soThung,
      validBoxesTotal: this.validBoxes.length,
      existingScannedTotal: this.existingScannedBoxes.size,
    });

    // ===== CHECK 1: Đã đủ số lượng chưa? =====
    if (this.scannedCount >= this.palletData.soThung) {
      console.log("❌ Limit reached");
      this.addErrorBox(trimmedCode, "Vượt quá số lượng thùng tối đa");
      // Không gọi API nếu đã đủ số lượng
      return;
    }

    // ===== CHECK 2: Box đã được scan trong pallet này chưa? (Từ BE) =====
    if (this.existingScannedBoxes.has(trimmedCode)) {
      console.log("❌ Box already scanned in this pallet (from BE)");
      this.addErrorBox(
        trimmedCode,
        "Mã thùng đã được scan vào pallet này trước đó",
      );
      // KHÔNG gọi API vì đã có trong DB rồi
      return;
    }

    // ===== CHECK 3: Box có trong danh sách đang scan hiện tại không? =====
    if (this.scannedBoxes.some((box) => box.code === trimmedCode)) {
      console.log("❌ Duplicate in current session");
      this.addErrorBox(trimmedCode, "Mã thùng đã được scan trong phiên này");
      // KHÔNG gọi API
      return;
    }

    // ===== CHECK 4: Box có hợp lệ không? (Có trong validBoxes) =====
    const isValid = this.validBoxes.some(
      (validCode) => validCode.trim() === trimmedCode,
    );

    if (!isValid) {
      console.log("❌ Code not in valid list");
      this.addErrorBox(
        trimmedCode,
        "Mã thùng không hợp lệ hoặc không thuộc lệnh sản xuất này",
      );
      // Gọi API với status = 0 (invalid)
      this.sendMappingRequest(trimmedCode, 0);
      return;
    }

    // =====  VALID BOX - Gọi API =====
    console.log(" Valid box, sending to API...");
    this.isProcessing = true;
    this.sendMappingRequest(trimmedCode, 1, true);
  }

  // ===== UPDATED: Send mapping with callback =====
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
          console.log(" Mapping saved to BE:", response);

          if (isSuccessCase) {
            // Thêm vào danh sách đã scan (local + BE)
            this.addSuccessBox(serialBox);
            this.existingScannedBoxes.add(serialBox.trim());
          }
        },
        error: (error) => {
          console.error("❌ Error saving mapping:", error);

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

  addSuccessBox(code: string): void {
    const box: BoxScan = {
      code,
      timestamp: new Date(),
      status: "success",
    };

    this.scannedBoxes.push(box);
    this.allBoxes.push(box);
    this.scannedCount++;

    this.showScanResult(
      "success",
      `Scan thành công! (${this.scannedCount}/${this.palletData.soThung})`,
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

  // ===== UPDATED: Remove box with API delete =====
  removeBox(index: number): void {
    const box = this.allBoxes[index];

    if (box.status === "success") {
      // Confirm before delete
      if (!confirm(`Xác nhận xóa box "${box.code}" khỏi pallet?`)) {
        return;
      }

      this.isProcessing = true;

      // Call DELETE API
      this.http
        .delete(
          `/api/serial-box-pallet-mappings/serial-box/${box.code}/serial-pallet/${this.palletData.maPallet}/ma-lenh-san-xuat/${this.palletData.maLenhSanXuatId}`,
        )
        .pipe(finalize(() => (this.isProcessing = false)))
        .subscribe({
          next: () => {
            console.log(" Box deleted from BE");

            // Remove from UI
            const successIndex = this.scannedBoxes.findIndex(
              (b) => b.code === box.code,
            );
            if (successIndex > -1) {
              this.scannedBoxes.splice(successIndex, 1);
              this.scannedCount--;
            }

            this.allBoxes.splice(index, 1);

            // Remove from existingScannedBoxes
            this.existingScannedBoxes.delete(box.code.trim());

            this.showScanResult("success", "Đã xóa box khỏi pallet");
          },
          error: (error) => {
            console.error("❌ Error deleting box:", error);
            this.showScanResult(
              "error",
              "Không thể xóa box. Vui lòng thử lại.",
            );
          },
        });
    } else {
      // Error box - just remove from UI
      const errorIndex = this.errorBoxes.findIndex((b) => b.code === box.code);
      if (errorIndex > -1) {
        this.errorBoxes.splice(errorIndex, 1);
      }
      this.allBoxes.splice(index, 1);
    }
  }

  removeSuccessBox(index: number): void {
    const box = this.scannedBoxes[index];

    if (!confirm(`Xác nhận xóa box "${box.code}" khỏi pallet?`)) {
      return;
    }

    this.isProcessing = true;

    this.planningService
      .removeMapping(
        box.code,
        this.palletData.maPallet,
        this.palletData.maLenhSanXuatId,
      )
      .pipe(finalize(() => (this.isProcessing = false)))
      .subscribe({
        next: () => {
          // Cập nhật UI sau khi xóa thành công
          this.scannedBoxes.splice(index, 1);
          this.scannedCount--;

          const allIndex = this.allBoxes.findIndex((b) => b.code === box.code);
          if (allIndex > -1) {
            this.allBoxes.splice(allIndex, 1);
          }

          this.existingScannedBoxes.delete(box.code.trim());

          this.showScanResult("success", "Đã xóa box khỏi pallet");
        },
        error: (error) => {
          console.error("Error deleting box:", error);
          this.showScanResult("error", "Không thể xóa box. Vui lòng thử lại.");
        },
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

  // Progress
  getProgressPercent(): number {
    if (this.palletData.soThung === 0) {
      return 0;
    }
    return Math.round((this.scannedCount / this.palletData.soThung) * 100);
  }

  // Sounds
  playSuccessSound(): void {
    const audio = new Audio("assets/sounds/successed-295058.mp3");
    audio.play().catch(() => {});
  }

  playErrorSound(): void {
    const audio = new Audio("assets/sounds/beep_warning.mp3");
    audio.play().catch(() => {});
  }

  // Test function (development only)
  simulateScan(): void {
    if (!this.isTestMode) {
      return;
    }

    const testCode = `BOX-2024-${String(this.testCounter).padStart(5, "0")}`;
    this.testCounter++;
    this.validateAndAddBox(testCode);
  }

  // Complete
  onComplete(): void {
    if (this.scannedBoxes.length === 0) {
      this.showScanResult("error", "Chưa có box nào được scan");
      return;
    }

    this.stopCamera();

    console.log("Completing scan with progress:", {
      totalScanned: this.scannedCount,
      progressPercent: this.getProgressPercent(),
      scannedBoxes: this.scannedBoxes.length,
    });

    this.dialogRef.close({
      success: true,
      palletId: this.palletData.id,
      scannedBoxes: this.scannedBoxes,
      errorBoxes: this.errorBoxes,
      totalScanned: this.scannedCount,
      progressPercent: this.getProgressPercent(),
    });
  }

  onClose(): void {
    this.stopCamera();

    console.log("Closing scan dialog with current progress:", {
      totalScanned: this.scannedCount,
      progressPercent: this.getProgressPercent(),
      scannedBoxes: this.scannedBoxes.length,
    });

    // ===== QUAN TRỌNG: Luôn return progress khi đóng =====
    this.dialogRef.close({
      success: false, // false vì chưa click "Hoàn thành"
      totalScanned: this.scannedCount,
      progressPercent: this.getProgressPercent(),
      scannedBoxes: this.scannedBoxes, // Trả về toàn bộ list
    });
  }
}
