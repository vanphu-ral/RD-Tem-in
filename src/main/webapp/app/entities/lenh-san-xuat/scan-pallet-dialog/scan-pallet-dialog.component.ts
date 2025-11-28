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

interface PalletData {
  id: string;
  maPallet: string;
  tenSanPham: string;
  qrCode: string;
  soThung: number;
}

interface BoxScan {
  code: string;
  timestamp: Date;
  status: "success" | "error";
  message?: string;
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

  // Mobile camera
  isMobile = false;
  cameraActive = false;
  availableCameras: MediaDeviceInfo[] = [];
  selectedDevice: MediaDeviceInfo | undefined = undefined;

  // Boxes
  scannedBoxes: BoxScan[] = [];
  errorBoxes: BoxScan[] = [];
  allBoxes: BoxScan[] = [];

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
  ) {
    this.palletData = data;
  }

  ngOnInit(): void {
    this.checkIfMobile();
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
    if (!this.scannedCode || this.scannedCode.trim() === "") {
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
      // Prefer back camera
      const backCamera = devices.find((d) =>
        d.label.toLowerCase().includes("back"),
      );
      this.selectedDevice = backCamera ?? devices[0];
    }
  }

  onScanSuccess(code: string): void {
    this.validateAndAddBox(code);
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

  // Validation & Processing
  validateAndAddBox(code: string): void {
    // Check if limit reached
    if (this.scannedCount >= this.palletData.soThung) {
      this.addErrorBox(code, "Vượt quá số lượng thùng tối đa");
      return;
    }

    // Check for duplicate
    if (this.scannedBoxes.some((box) => box.code === code)) {
      this.addErrorBox(code, "Mã thùng đã được scan trước đó");
      return;
    }

    // Validate box code format (customize as needed)
    if (!this.isValidBoxCode(code)) {
      this.addErrorBox(
        code,
        "Mã thùng không hợp lệ hoặc không thuộc pallet này",
      );
      return;
    }

    // Add success
    this.addSuccessBox(code);
  }

  isValidBoxCode(code: string): boolean {
    // Implement your validation logic here
    // Example: Check if code starts with "BOX-" or matches a pattern
    return code.length > 3;
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

    this.showScanResult("success", "Scan thành công!");
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

  // Remove boxes
  removeBox(index: number): void {
    const box = this.allBoxes[index];

    if (box.status === "success") {
      const successIndex = this.scannedBoxes.findIndex(
        (b) => b.code === box.code,
      );
      if (successIndex > -1) {
        this.scannedBoxes.splice(successIndex, 1);
        this.scannedCount--;
      }
    } else {
      const errorIndex = this.errorBoxes.findIndex((b) => b.code === box.code);
      if (errorIndex > -1) {
        this.errorBoxes.splice(errorIndex, 1);
      }
    }

    this.allBoxes.splice(index, 1);
  }

  removeSuccessBox(index: number): void {
    this.scannedBoxes.splice(index, 1);
    this.scannedCount--;

    // Remove from allBoxes
    const allIndex = this.allBoxes.findIndex(
      (b) => b.code === this.scannedBoxes[index]?.code,
    );
    if (allIndex > -1) {
      this.allBoxes.splice(allIndex, 1);
    }
  }

  removeErrorBox(index: number): void {
    const box = this.errorBoxes[index];
    this.errorBoxes.splice(index, 1);

    // Remove from allBoxes
    const allIndex = this.allBoxes.findIndex((b) => b.code === box.code);
    if (allIndex > -1) {
      this.allBoxes.splice(allIndex, 1);
    }
  }

  // Progress
  getProgressPercent(): number {
    return Math.round((this.scannedCount / this.palletData.soThung) * 100);
  }

  // Sounds
  playSuccessSound(): void {
    const audio = new Audio("assets/sounds/success.mp3");
    audio.play().catch(() => {});
  }

  playErrorSound(): void {
    const audio = new Audio("assets/sounds/error.mp3");
    audio.play().catch(() => {});
  }

  // Test function (development only)
  simulateScan(): void {
    const testCode = `BOX-2024-${String(this.testCounter).padStart(5, "0")}`;
    this.testCounter++;
    this.validateAndAddBox(testCode);
  }

  // Complete
  onComplete(): void {
    if (this.scannedBoxes.length === 0) {
      return;
    }

    this.stopCamera();

    this.dialogRef.close({
      success: true,
      palletId: this.palletData.id,
      scannedBoxes: this.scannedBoxes,
      errorBoxes: this.errorBoxes,
      totalScanned: this.scannedCount,
    });
  }

  onClose(): void {
    this.stopCamera();
    this.dialogRef.close();
  }
}
