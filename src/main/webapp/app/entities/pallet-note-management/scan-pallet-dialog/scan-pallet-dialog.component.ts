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
  thungScan?: number;
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
  isLoadingHistory = false;

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
  ) {
    this.palletData = data;
    console.log("Scan dialog constructor data:", data);
  }

  ngOnInit(): void {
    this.checkIfMobile();
    this.initializeValidBoxes();
    this.loadExistingScannedBoxes();

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
    } else {
      console.warn("No valid reel IDs provided or empty array");
      this.validBoxes = [];
    }
  }

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

          console.log(`Loaded ${this.scannedCount} boxes already scanned`);
        },
        error: (error) => {
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

  // ===== TOGGLE ZEBRA MODE (CHỈ FOCUS INPUT) =====
  toggleScanMode(): void {
    this.scanModeActive = !this.scanModeActive;

    if (this.scanModeActive) {
      // Bật chế độ Zebra - CHỈ focus input
      this.scanMode = "zebra";
      this.cameraActive = false; // Đảm bảo camera TẮT

      console.log("✅ Zebra scanner activated - Input focused");
      setTimeout(() => this.focusScannerInput(), 100);
    } else {
      console.log("❌ Zebra scanner deactivated");
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
      console.log("✅ Input focused");
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
    console.log("📥 Processing code:", code);

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

    const trimmedCode = code.trim();

    // Check 1: Đã đủ số lượng?
    if (this.scannedCount >= (this.palletData?.thungScan ?? 0)) {
      this.addErrorBox(trimmedCode, "Vượt quá số lượng thùng tối đa");
      return;
    }

    // Check 2: Đã scan trong pallet này?
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

    // Check 4: Có hợp lệ không?
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
          if (isSuccessCase) {
            this.addSuccessBox(serialBox);
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

  onClose(): void {
    this.stopCamera();

    this.dialogRef.close({
      success: false,
      totalScanned: this.scannedCount,
      progressPercent: this.getProgressPercent(),
      scannedBoxes: this.scannedBoxes,
    });
  }
}
