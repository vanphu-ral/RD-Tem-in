import { HttpClient } from "@angular/common/http";
import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  AfterViewChecked,
  Input,
} from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import {
  faFileImport,
  faEye,
  faEdit,
  faTrash,
  faPrint,
  faArrowUp,
  faSave,
  faPlusCircle,
  faFileExcel,
  faFilter,
  faArrowDown,
  faQrcode,
  faFileCsv,
  faCheckCircle,
  faRotateBack,
} from "@fortawesome/free-solid-svg-icons";
import JsBarcode from "jsbarcode";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { MatDialog } from "@angular/material/dialog";
import { Observable } from "rxjs/internal/Observable";
import { GenerateTemInConfirmDialogComponent } from "../generate-tem-in-modal-confirm/modal-confirm.component";
import { PageEvent } from "@angular/material/paginator";
import { MatCardActions } from "@angular/material/card";
import { CachedWarehouse } from "app/entities/list-material/services/warehouse-db";
import { WarehouseCacheService } from "app/entities/list-material/services/warehouse-cache.service";
import { FormControl } from "@angular/forms";

export interface MaterialItem {
  stt: number; // Số thứ tự hiển thị
  id: number;
  productOfRequestId: number;
  reelId: string;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
  qrCode: string;
  requestCreateTemId: number;
  temQuantity: number;
  numberOfPrints: number;
  isEditing?: boolean;
  isSaving?: boolean;
}
export interface GenerateTemResponse {
  success: boolean;
  message: string;
  totalTems: number;
}

interface CsvDataItem {
  productOfRequestId: number;
  reelId: string;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
  qrCode: string;
  slTemQuantity: number;
}

//interface trong tem in
export interface TemDetail {
  id: number;
  productOfRequestId: number;
  reelId: string;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  initialQuantity: number;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  storageUnit: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
  qrCode: string;
  slTemQuantity: number;
}
export interface ListRequestCreateTem {
  id: number;
  vendor: string;
  userData5: string;
  createdBy: string;
  numberProduction: number;
  totalQuantity: number;
  status: string;
}
@Component({
  selector: "jhi-generate-tem-in-import",
  templateUrl: "./generate-tem-in-detail.component.html",
  styleUrl: "./generate-tem-in-detail.component.scss",
  standalone: false,
})
export class GenerateTemInDetailComponent implements OnInit, AfterViewChecked {
  //biến lấy id PO
  poNumber = "";
  //disable
  isDisable = false;
  isDisableInputLocation = false;
  isDisableGenerate = false;
  isDelete = false;
  //biến in
  printMode: "single" | "all" = "all";

  //id nhận từ requestCreateTem
  @Input() requestId!: number;

  //column table
  displayedColumns: string[] = [
    "stt",
    "actions",
    "numberOfPrints",
    "sapCode",
    "userData5",
    "partNumber",
    "lot",
    "storageUnit",
    "temQuantity",
    "initialQuantity",
    "vendor",
    "manufacturingDate",
    "expirationDate",
    "arrivalDate",
  ];
  temDetailColumns: string[] = [
    "stt",
    "id",
    "reelId",
    "sapCode",
    "productName",
    "partNumber",
    "lot",
    "initialQuantity",
    "vendor",
    "userData1",
    "userData2",
    "userData3",
    "userData4",
    "userData5",
    "storageUnit",
    "expirationDate",
    "manufacturingDate",
    "arrivalDate",
    "slTemQuantity",
    "qrCode",
  ];
  // CSV columns
  csvColumns: string[] = [
    "reelId",
    "sapCode",
    "partNumber",
    "lot",
    "storageUnit",
    "initialQuantity",
    "vendor",
    "userData1",
    "userData2",
    "userData3",
    "userData4",
    "userData5",
    "expirationDate",
    "manufacturingDate",
    "arrivalDate",
    "slTemQuantity",
  ];

  //biến chứa kho
  storageUnits: CachedWarehouse[] = [];
  filteredUnits: string[] = [];
  selectedStorageUnit = "";

  requestList: ListRequestCreateTem[] = [];
  selectedRequestId: number | null = null;

  expandedRowIndex: number | null = null;
  selectedWarehouseRow: string = "";
  selectedItem: any = null;
  isGenerating = false;
  //qrcode row
  qrTableVisible = false;
  generatedQRCodes: any[] = [];
  qrReady: boolean = false;

  //prepare icon
  faRotateBack = faRotateBack;
  faFileExcel = faFileExcel;
  faPlusCircle = faPlusCircle;
  faPrint = faPrint;
  faEdit = faEdit;
  faTrash = faTrash;
  faEye = faEye;
  faFilter = faFilter;
  faArrowDown = faArrowDown;
  faQrcode = faQrcode;
  faFileCsv = faFileCsv;
  faCheckCircle = faCheckCircle;

  //pagination detail
  materialCurrentPage = 0; // Material paginator bắt đầu từ 0
  materialPageSize = 25;
  materialTotalItems = 0;
  materialPageSizeOptions = [5, 10, 25, 50, 100];
  pagedMaterials: MaterialItem[] = [];

  // Modal states
  showQrModal = false;
  showCsvModal = false;
  showPrintModal = false;
  isLoadingTemDetails = false;

  // Dữ liệu của product được click (từ list_product_of_request)
  selectedProductItem: any = null;

  // Danh sách tem chi tiết (từ info_tem_detail)
  temDetailList: TemDetail[] = [];
  //tat ca tem
  allTemDetails: TemDetail[] = [];

  // CSV Preview data
  csvFileName = "CSV_IN_TEM_6_10_2025.csv";
  csvData: CsvDataItem[] = [];

  // Print Preview data
  printLabels: TemDetail[] = [];

  materials: MaterialItem[] = [];

  //bien edit
  // isEditing = false;

  //flag
  private _filteredLabelsCache: TemDetail[] = [];
  private barcodesGenerated = false;
  private isGeneratingBarcodes = false;
  constructor(
    private http: HttpClient,
    private generateTemInService: GenerateTemInService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
    private warehouseCache: WarehouseCacheService,
  ) {}
  ngAfterViewChecked(): void {
    if (
      !this.barcodesGenerated &&
      !this.isGeneratingBarcodes &&
      this.showPrintModal &&
      this._filteredLabelsCache.length > 0
    ) {
      this.isGeneratingBarcodes = true;
      setTimeout(() => {
        this.generateBarcodes();
        this.barcodesGenerated = true;
        this.isGeneratingBarcodes = false;
      }, 0);
    }
  }

  ngOnInit(): void {
    const requestId = Number(this.route.snapshot.paramMap.get("id"));
    this.requestId = requestId;
    if (requestId) {
      this.getListRequest(requestId);
    }
  }
  //back btn
  goBack(): void {
    this.router.navigate(["/generate-tem-in"]);
  }
  getListRequest(requestId: number): void {
    this.generateTemInService
      .getProductsByRequestId(requestId)
      .subscribe((data) => {
        this.materials = data.map((item: MaterialItem) => ({
          ...item,
          isEditing: false,
          isSaving: false,
        }));
        this.materialTotalItems = this.materials.length;
        this.updatePagedMaterials();

        if (this.materials.length > 0) {
          this.poNumber = this.materials[0].userData5;
          const productId = this.materials[0].id;
          this.getListAllTemDetails(productId);
        }
      });
  }
  //phan trang
  updatePagedMaterials(): void {
    const startIndex = this.materialCurrentPage * this.materialPageSize;
    const endIndex = startIndex + this.materialPageSize;
    this.pagedMaterials = this.materials.slice(startIndex, endIndex);
  }
  onPageChange(event: PageEvent): void {
    this.materialCurrentPage = event.pageIndex;
    this.materialPageSize = event.pageSize;
    this.updatePagedMaterials();
  }

  getListAllTemDetails(requestId: number): void {
    this.generateTemInService
      .getTemDetailsByProductId(requestId)
      .subscribe((data) => {
        this.temDetailList = data;
        this.isDisable = data.length === 0;
        this.isDisableGenerate = data.length > 0;
        console.log("data detail: ", data);
      });
  }

  //snackbar notification
  showSnackbar(
    message: string,
    action: string = "Đóng",
    duration: number = 3000,
    type: "success" | "error" | "warning" = "success",
  ): void {
    const panelClass = {
      success: "snackbar-success",
      error: "snackbar-error",
      warning: "snackbar-warning",
    }[type];

    this.snackBar.open(message, action, {
      duration,
      horizontalPosition: "center",
      verticalPosition: "bottom",
      panelClass: [panelClass],
    });
  }

  //confirm dialog action
  confirmAction(message: string): Observable<boolean> {
    const dialogRef = this.dialog.open(GenerateTemInConfirmDialogComponent, {
      data: { message },
    });
    return dialogRef.afterClosed() as Observable<boolean>;
  }

  //ham loc location
  filterStorageUnits(): void {
    const keyword = this.selectedStorageUnit?.toLowerCase() || "";
    if (keyword.length < 2) {
      this.filteredUnits = [];
      return;
    }

    this.warehouseCache.searchByName(keyword).then((results) => {
      this.filteredUnits = results.map((w) => w.locationName).slice(0, 25);
    });
  }

  //format date
  formatArrivalDate(dateStr: string): string {
    if (!dateStr) {
      return "";
    }
    const parts = dateStr.split("-");
    if (parts.length !== 3) {
      return dateStr;
    }
    return parts[0].slice(2) + parts[1] + parts[2]; // "2025" → "25"
  }

  trackByLabelId(index: number, label: TemDetail): string {
    // Tạo unique key từ id + index
    return `${label.id}-${index}`;
  }
  onGenerateTemForRequest(requestId: number): void {
    this.confirmAction(
      `Bạn có chắc muốn tạo tem cho tất cả sản phẩm thuộc yêu cầu #${requestId}?`,
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }

      this.isGenerating = true;

      this.generateTemInService.generateTemByRequest(requestId).subscribe({
        next: (response) => {
          console.log("Response:", response);

          const type = response.success ? "success" : "error";
          this.showSnackbar(response.message, "Đóng", 3000, type);

          this.isGenerating = false;
          setTimeout(() => {
            window.location.reload();
          }, 1000);
        },
        error: (err) => {
          this.showSnackbar(
            err.message || "Có lỗi xảy ra khi tạo tem!",
            "Đóng",
            3000,
            "error",
          );
          this.isGenerating = false;
        },
      });
    });
  }

  exportToExcel(): void {
    console.log("Export to Excel");
    // Implement Excel export logic here
  }
  get filteredLabels(): TemDetail[] {
    return this._filteredLabelsCache;
  }

  printTable(): void {
    console.log("🖨️ Starting print...");
    console.log("Total labels:", this.filteredLabels.length);

    const modal = document.getElementById("printPreviewModal");
    if (!modal) {
      console.error("Modal not found!");
      return;
    }

    //  Lấy container có labels
    const container = modal.querySelector(".print-preview-container");
    if (!container) {
      console.error("Container not found!");
      return;
    }

    //  Clone container để không ảnh hưởng modal gốc
    const printContainer = container.cloneNode(true) as HTMLElement;
    printContainer.id = "printPreviewModal";

    //  Thêm container mới vào body (ngang level với modal)
    const originalCanvases = container.querySelectorAll("canvas");
    const clonedCanvases = printContainer.querySelectorAll("canvas");

    originalCanvases.forEach((originalCanvas, index) => {
      const clonedCanvas = clonedCanvases[index] as HTMLCanvasElement;
      const ctx = clonedCanvas.getContext("2d");
      if (ctx) {
        clonedCanvas.width = originalCanvas.width;
        clonedCanvas.height = originalCanvas.height;
        ctx.drawImage(originalCanvas, 0, 0);
      }
    });
    //  Ẩn modal gốc
    printContainer.style.display = "none";
    modal.style.display = "none";
    document.body.appendChild(printContainer);

    //  Inject CSS cho print
    const style = document.createElement("style");
    style.id = "temp-print-style";
    style.textContent = `
    @media print {
      @page {
        size: 102.5mm 35mm;
        margin: 0;
      }

      * {
        margin: 0;
        padding: 0;
      }

      body {
        margin: 0 !important;
        padding: 0 !important;
        width: 102.5mm !important;
        height: 35mm !important;
      }

      /* Hide everything */
      body > * {
        display: none !important;
      }

      /* Only show temp container */
      #printPreviewModal {
        display: block !important;
        visibility: visible !important;
        position: static !important;
        margin: 0 !important;
        padding: 0 !important;
        overflow: visible !important;
        width: 102.5mm !important;
        height: auto !important;
      }

      #printPreviewModal,
      #printPreviewModal * {
        visibility: visible !important;
      }

      /* Label = page */
      #printPreviewModal .label {
        width: 102.5mm !important;
        height: 35mm !important;
        margin: 0 !important;
        padding: 2mm !important;
        position: relative !important;
        overflow: hidden !important;
        background: white !important;
        page-break-after: always !important;
        page-break-inside: avoid !important;
      }

      #printPreviewModal .label:last-of-type {
        page-break-after: avoid !important;
      }

      /* Content - Thu nhỏ padding */
      #printPreviewModal .label-content {
        position: relative !important;
        width: 100% !important;
        height: 100% !important;
        padding: 0.8mm !important;
        gap: 1.5mm !important;
        transform: none !important;
        background: white !important;
        box-sizing: border-box !important;
        display: flex !important;
      }

      /* Cột trái - THU NHỎ QR */
      #printPreviewModal .col-left {
        width: 25mm !important;
        gap: 1.5mm !important;
      }

      #printPreviewModal .qr-box {
        width: 22mm !important;
        height: 22mm !important;
        border: 0.5px solid #000 !important;
        border-radius: 1px !important;
        padding: 1mm !important;
      }

      /* THU NHỎ QR CODE */
      #printPreviewModal .qr-box qrcode,
      #printPreviewModal .qr-box canvas {
        width: 18mm !important;
        height: 18mm !important;
        max-width: 18mm !important;
        max-height: 18mm !important;
      }

      #printPreviewModal .msd-info {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
      }

      #printPreviewModal .msd-title {
        font-size: 6pt !important;
        margin-bottom: 0.3mm !important;
      }

      #printPreviewModal .msd-item {
        font-size: 5.5pt !important;
        line-height: 1.15 !important;
        font-weight: bold !important;
      }

      /* Cột giữa */
      #printPreviewModal .col-center {
        flex: 1 !important;
        gap: 2.5mm !important;
      }

      #printPreviewModal .logo-box {
        height: 4mm !important;
      }

      #printPreviewModal .logo-img {
        height: 3.5mm !important;
      }

      #printPreviewModal .sap-code {
        font-size: 10pt !important;
        line-height: 1 !important;
      }

      #printPreviewModal .part-number {
        font-size: 8pt !important;
        line-height: 1 !important;
      }

      #printPreviewModal .reel-id-tem {
        font-size: 7pt !important;
        line-height: 1 !important;
      }

      #printPreviewModal .info-grid-tem {
        gap: 0.2mm !important;
        line-height: 1 !important;
      }

      #printPreviewModal .info-row-tem {
        font-size: 6pt !important;
        line-height: 1.1 !important;
        gap: 1mm !important;
      }

      #printPreviewModal .info-label-tem {
        min-width: 10mm !important;
        font-weight: bold !important;
      }

      #printPreviewModal .qty-big {
        font-size: 7pt !important;
        font-weight: bold !important;
      }

      /* Cột phải */
      #printPreviewModal .col-right {
        width: 28mm !important;
        gap: 0.3mm !important;
      }

      #printPreviewModal .rank-section {
        gap: 0.2mm !important;
      }

      #printPreviewModal .rank-item-small {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
      }

      #printPreviewModal .storage-unit {
        font-size: 6pt !important;
        padding: 0.3mm 0.5mm !important;
        line-height: 1.2 !important;
      }

      #printPreviewModal .lot-info {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
      }

      #printPreviewModal .barcode-box {
        padding: 0.5mm !important;
        gap: 0.2mm !important;
        border: 0.5px solid #333 !important;
      }

      #printPreviewModal .bc-item-compact {
        gap: 0.2mm !important;
      }

      #printPreviewModal .bc-line {
        display: flex !important;
        align-items: center !important;
        gap: 0.3mm !important;
      }

      #printPreviewModal .bc-label {
        font-size: 5.5pt !important;
        line-height: 1 !important;
      }

      #printPreviewModal .bc-code {
        font-size: 5.5pt !important;
        line-height: 1 !important;
      }

      #printPreviewModal .bc-svg {
        height: 3.5mm !important;
        flex: 1 !important;
      }

      #printPreviewModal .bc-svg svg {
        height: 100% !important;
        width: auto !important;
        max-width: 100% !important;
        object-fit: contain !important;
      }

      #printPreviewModal .product-name-small {
        font-size: 4.5pt !important;
        line-height: 1.1 !important;
        margin-top: 0.3mm !important;
      }

      /* Colors */
      #printPreviewModal * {
        print-color-adjust: exact !important;
        -webkit-print-color-adjust: exact !important;
      }

      /* QR & Barcodes */
      #printPreviewModal canvas,
      #printPreviewModal qrcode,
      #printPreviewModal qrcode canvas,
      #printPreviewModal svg,
      #printPreviewModal img {
        visibility: visible !important;
        display: block !important;
        opacity: 1 !important;
        print-color-adjust: exact !important;
        -webkit-print-color-adjust: exact !important;
      }

      body>*:not(#printPreviewModal) {
        display: none !important;
      }
    }
  `;
    document.head.appendChild(style);

    console.log(" Temp container created");

    //  Đợi DOM render xong
    setTimeout(() => {
      window.print();

      //  Cleanup sau khi print
      const cleanup = (): void => {
        console.log(" Cleaning up...");

        // Xóa temp elements
        printContainer.remove();
        style.remove();

        // Hiện lại modal
        modal.style.display = "block";

        window.removeEventListener("afterprint", cleanup);
        console.log(" Cleanup done");
      };

      window.addEventListener("afterprint", cleanup);
      setTimeout(cleanup, 3000); // Fallback
    }, 500);
  }
  onAction(action: string, item: any, index: number): void {
    if (action === "view") {
      this.selectedProductItem = item;
      this.showQrModal = true;
      this.loadTemDetails(item.id); // item.id là id của list_product_of_request
      document.body.classList.add("modal-open");
    }
    if (action === "exportCsv") {
      this.selectedItem = item;
      console.log("TemDetailList:", this.temDetailList);
      console.log("SelectedItem ID:", item.id);
      this.loadTemDetails(item.id, () => {
        this.prepareSingleCsvData(Number(item.id));
      });

      document.body.classList.add("modal-open");
    }
    if (action === "edit") {
      item.isEditing = true;
    }
    if (action === "confirm") {
      // Kiểm tra validation
      if (!item.id) {
        this.showSnackbar(
          "Thiếu ID sản phẩm không thể cập nhật",
          "Đóng",
          3000,
          "warning",
        );
        return;
      }

      // Đánh dấu đang lưu trên chính object item
      item.isSaving = true;

      const payload = {
        input: {
          id: Number(item.id),
          sapCode: item.sapCode,
          requestCreateTemId: item.requestCreateTemId,
          partNumber: item.partNumber,
          lot: item.lot,
          temQuantity: item.temQuantity,
          initialQuantity: item.initialQuantity,
          vendor: item.vendor,
          userData1: item.userData1,
          userData2: item.userData2,
          userData3: item.userData3,
          userData4: item.userData4,
          userData5: item.userData5,
          storageUnit: item.storageUnit,
          expirationDate: item.expirationDate,
          manufacturingDate: item.manufacturingDate,
          arrivalDate: item.arrivalDate,
          numberOfPrints: item.numberOfPrints,
        },
      };

      console.log("📦 Payload gửi lên mutation:", payload);

      // Gọi mutation
      this.generateTemInService.updateProductOfRequest(item).subscribe({
        next: (res) => {
          if (res.data?.updateProductOfRequest?.success) {
            // Cập nhật trực tiếp trên object item
            item.isSaving = false;
            item.isEditing = false;
            this.showSnackbar("Cập nhật thành công!", "Đóng", 3000, "success");
          } else {
            item.isSaving = false;
            this.showSnackbar(
              "Cập nhật thất bại: " + `${res.data?.updateProductOfRequest}`,
              "Đóng",
              3000,
              "error",
            );
          }
        },
        error: (err) => {
          console.error("Lỗi khi gọi mutation:", err);
          item.isSaving = false;
          this.showSnackbar("Lỗi khi cập nhật", "Đóng", 3000, "error");
        },
      });
    }

    if (action === "print") {
      this.printMode = "single";
      this.selectedProductItem = item;
      document.body.classList.add("modal-open");

      this.loadTemDetails(item.id, () => {
        this.openPrintPreviewForProduct(item.id);
      });
    }
    if (action === "delete") {
      this.confirmAction(
        `Bạn có chắc muốn xóa sản phẩm này?\nSAP Code: ${item.sapCode}`,
      ).subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }

        this.isDelete = true;

        this.generateTemInService.deleteReqById(Number(item.id)).subscribe({
          next: () => {
            this.materials.splice(index, 1);
            this.updatePagedMaterials();
            this.showSnackbar("Đã xóa sản phẩm!", "Đóng", 3000, "success");
            this.isDelete = false;
          },
          error: (err) => {
            this.showSnackbar("Lỗi khi xóa sản phẩm!", "Đóng", 3000, "error");
            console.error("Delete error:", err);
            this.isDelete = false;
          },
        });
      });
    }
  }
  /**
   * Load danh sách tem từ info_tem_detail
   * Lọc theo Product_of_request_id = productId
   */
  loadTemDetails(productId: number, callback?: () => void): void {
    this.isLoadingTemDetails = true;
    this.temDetailList = [];

    this.generateTemInService.getTemDetailsByProductId(productId).subscribe({
      next: (data) => {
        this.temDetailList = data;
        console.log("Loaded tem details:", data.length);
        this.isLoadingTemDetails = false;

        this.isDisable = data.length === 0;
        if (callback) {
          callback(); // gọi sau khi dữ liệu đã sẵn sàng
        }
      },
      error: (err) => {
        console.error("Error loading tem details:", err);
        this.showSnackbar(
          "Không thể tải danh sách tem!",
          "Đóng",
          3000,
          "error",
        );
        this.isLoadingTemDetails = false;
        this.isDisable = true;
      },
    });
  }

  closeQrModal(): void {
    this.showQrModal = false;
    document.body.classList.remove("modal-open");
  }
  //generate Qrcode row
  generateQRCodeRow(item: any, warehouse: string): void {
    // xử lý tạo QR code cho item với kho đã chọn
    this.generatedQRCodes = [
      {
        code: item.sapCode,
        productName: item.tenSP,
        storageUnit: warehouse,
        quantity: item.initialQuantity,
      },
    ];
    this.qrTableVisible = true;
  }

  //choose location
  onSelectUnit(unit: string): void {
    this.selectedStorageUnit = unit;
    console.log("Kho đã chọn:", unit);
  }
  //save location button
  saveLocation(): void {
    console.log("Kho đang chọn:", this.selectedStorageUnit);
    console.log("Request ID:", this.requestId);

    if (!this.selectedStorageUnit || !this.requestId) {
      this.showSnackbar("Vui lòng chọn kho!", "Đóng", 3000, "error");
      return;
    }

    this.generateTemInService
      .updateStorageUnitForRequest(this.requestId, this.selectedStorageUnit)
      .subscribe({
        next: (res) => {
          if (res.success) {
            this.showSnackbar(
              "Đã cập nhật kho thành công!",
              "Đóng",
              3000,
              "success",
            );
            setTimeout(() => {
              window.location.reload();
            }, 1000);
          } else {
            this.showSnackbar("Cập nhật thất bại", "Đóng", 3000, "error");
          }
        },
        error: (err) => {
          console.error("Lỗi khi cập nhật kho:", err);
          this.showSnackbar(
            "Có lỗi xảy ra khi lưu kho!",
            "Đóng",
            3000,
            "error",
          );
        },
      });
  }
  // CSV Preview Methods
  openCsvPreview(): void {
    this.prepareCsvData();
    this.showCsvModal = true;
    document.body.classList.add("modal-open");
  }

  closeCsvModal(): void {
    this.showCsvModal = false;
    document.body.classList.remove("modal-open");
  }

  prepareSingleCsvData(productId: number): void {
    const matchedItems = this.temDetailList.filter(
      (item) => item.productOfRequestId === productId,
    );
    console.log("Matched tem:", matchedItems);
    this.csvData = matchedItems.map((matched) => ({
      productOfRequestId: matched.productOfRequestId,
      reelId: matched.reelId,
      sapCode: matched.sapCode,
      productName: matched.productName,
      partNumber: matched.partNumber,
      lot: matched.lot,
      initialQuantity: matched.initialQuantity,
      vendor: matched.vendor,
      userData1: matched.userData1,
      userData2: matched.userData2,
      userData3: matched.userData3,
      userData4: matched.userData4,
      userData5: matched.userData5,
      storageUnit: matched.storageUnit,
      expirationDate: matched.expirationDate,
      manufacturingDate: matched.manufacturingDate,
      arrivalDate: matched.arrivalDate,
      qrCode: matched.qrCode,
      slTemQuantity: matched.slTemQuantity,
    }));
    this.showCsvModal = true;
  }

  prepareCsvData(): void {
    this.csvData = this.temDetailList.map((item) => ({
      productOfRequestId: item.productOfRequestId,
      reelId: item.reelId,
      sapCode: item.sapCode,
      productName: item.productName,
      partNumber: item.partNumber,
      lot: item.lot,
      initialQuantity: item.initialQuantity,
      vendor: item.vendor,
      userData1: item.userData1,
      userData2: item.userData2,
      userData3: item.userData3,
      userData4: item.userData4,
      userData5: item.userData5,
      storageUnit: item.storageUnit,
      expirationDate: item.expirationDate,
      manufacturingDate: item.manufacturingDate,
      arrivalDate: item.arrivalDate,
      qrCode: item.qrCode,
      slTemQuantity: item.slTemQuantity,
    }));
    this.isDisable = this.temDetailList.length === 0;
  }

  exportCsv(): void {
    const csvContent = this.generateCsvContent();
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);

    link.setAttribute("href", url);
    link.setAttribute("download", this.csvFileName);
    link.style.visibility = "hidden";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    this.closeCsvModal();
  }

  generateCsvContent(): string {
    const headers = [
      "ReelID",
      "PartNumber",
      "Vendor",
      "Lot",
      "UserData1",
      "UserData2",
      "UserData3",
      "UserData4",
      "UserData5",
      "InitialQuantity",
      "MSDLevel",
      "MSDInitialFloorTime",
      "MSDBagSealDate",
      "MarketUsage",
      "QuantityOverride",
      "ShelfTime",
      "SPMaterialName",
      "WarningLimit",
      "MaximumLimit",
      "Comments",
      "WarmupTime",
      "StorageUnit",
      "SubStorageUnit",
      "LocationOverride",
      "ExpirationDate",
      "ManufacturingDate",
      "Partclass",
      "SAPCode",
    ];

    const rows = this.csvData.map((item) => [
      item.reelId,
      item.partNumber,
      item.vendor,
      item.lot,
      item.userData1,
      item.userData2,
      item.userData3,
      item.userData4,
      item.userData5,
      item.initialQuantity,
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "", // MSD fields & others
      "",
      item.storageUnit,
      "", // SubStorageUnit, LocationOverride
      "", // SubStorageUnit, LocationOverride
      item.expirationDate?.replace(/-/g, ""),
      item.manufacturingDate?.replace(/-/g, ""),
      "", // Partclass
      item.sapCode,
    ]);

    const csvRows = [headers, ...rows]
      .map((row) => row.map((cell) => cell ?? "").join(","))
      .join("\n");

    return "\ufeff" + csvRows;
  }

  // Print Preview Methods
  openPrintPreviewForProduct(requestId: number): void {
    // console.log(" In tem cho product:", this.selectedProductItem?.partNumber);

    if (!this.temDetailList || this.temDetailList.length === 0) {
      this.showSnackbar(
        "Chưa có tem nào cho sản phẩm này!",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }

    const matchedTems = this.temDetailList.filter(
      (tem) => tem.productOfRequestId === Number(requestId),
    );

    if (!matchedTems) {
      this.showSnackbar(
        "Không tìm thấy tem phù hợp để in!",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }

    this.printLabels = matchedTems;
    this.calculateFilteredLabels();

    // console.log("Số tem của product này:", this.printLabels.length);
    // console.log("Số tem của product này:", this.printLabels);

    this.showPrintModal = true;
  }

  openPrintPreviewAll(requestId: number): void {
    this.printMode = "all";
    // console.log("In TẤT CẢ tem");

    // CRITICAL: Reset tất cả trước khi load
    this.barcodesGenerated = false;
    this.isGeneratingBarcodes = false;
    this._filteredLabelsCache = [];
    this.printLabels = [];

    //  Đóng modal nếu đang mở (clear DOM cũ)
    // this.showPrintModal = false;

    this.generateTemInService
      .getTemDetailsByRequestId(this.requestId)
      .subscribe({
        next: (data) => {
          this.printLabels = data;
          this.isDisable = data.length === 0;
          console.log("Tổng số tem để in:", this.printLabels.length);
          if (this.isDisable) {
            this.showSnackbar(
              "Chưa có tem nào được tạo!",
              "Đóng",
              3000,
              "warning",
            );
            return;
          }

          this.calculateFilteredLabels();

          //  Đợi một chút để Angular clear DOM
          setTimeout(() => {
            this.showPrintModal = true;
            document.body.classList.add("modal-open");
            // console.log(
            //   "Modal opened, should render",
            //   this._filteredLabelsCache.length,
            //   "labels",
            // );
          }, 100);
        },
        error: (err) => {
          console.error("Lỗi khi load tem:", err);
          this.showSnackbar(
            "Không thể tải danh sách tem!",
            "Đóng",
            3000,
            "error",
          );
        },
      });
  }

  closePrintModal(): void {
    this._filteredLabelsCache = [];
    this.showPrintModal = false;
    this.barcodesGenerated = false;
    this.isGeneratingBarcodes = false;
    setTimeout(() => {
      document.body.classList.remove("modal-open");
    }, 0);
  }

  preparePrintLabels(): void {
    // Lấy trực tiếp từ temDetailList (đã load từ modal)
    this.printLabels = [...this.temDetailList];

    // console.log("Print labels prepared:", this.printLabels.length);
  }

  closeAllModals(): void {
    this.closeCsvModal();
    this.closePrintModal();
  }

  private calculateFilteredLabels(): void {
    if (this.printMode === "single") {
      this._filteredLabelsCache = [...this.printLabels];
    } else {
      const expanded: TemDetail[] = [];
      this.printLabels.forEach((label) => {
        const count = Number(label.slTemQuantity) || 1;
        for (let i = 0; i < count; i++) {
          // Deep copy để tránh reference issues
          expanded.push(JSON.parse(JSON.stringify(label)));
        }
      });
      this._filteredLabelsCache = expanded;
    }

    // console.log(
    //   `📦 Calculated ${this._filteredLabelsCache.length} labels to render`,
    // );
  }
  private generateBarcodes(): void {
    // console.log(" Generating barcodes...");

    this._filteredLabelsCache.forEach((label, i) => {
      //"Dùng ID mới: label.id + index
      const uniqueId = `${label.id}-${i}`;
      const nvTarget = document.getElementById(`barcode-nv-${uniqueId}`);
      const poTarget = document.getElementById(`barcode-po-${uniqueId}`);

      const rawDate = label.arrivalDate || "000000";
      const compactDate = rawDate.replace(/-/g, "").slice(2);

      if (nvTarget) {
        try {
          JsBarcode(nvTarget, compactDate || "000000", {
            format: "CODE128",
            lineColor: "#000",
            width: 2,
            height: 28,
            displayValue: false,
            margin: 0,
          });
        } catch (e) {
          console.error(`Barcode NV error at ${i}:`, e);
        }
      }

      if (poTarget) {
        try {
          JsBarcode(poTarget, label.userData5 || "00000", {
            format: "CODE128",
            lineColor: "#000",
            width: 2,
            height: 28,
            displayValue: false,
            margin: 0,
          });
        } catch (e) {
          console.error(`Barcode PO error at ${i}:`, e);
        }
      }
    });

    // console.log("Generated ${this._filteredLabelsCache.length} barcodes`");
  }
  //filter kho
  private async handleKhoSearch(keyword: string | null): Promise<void> {
    if (!keyword || keyword.length < 2) {
      this.storageUnits = [];
      return;
    }

    this.storageUnits = await this.warehouseCache.searchByName(keyword);
  }
}
