import { HttpClient } from "@angular/common/http";
import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  AfterViewChecked,
  Input,
  OnDestroy,
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
  faCloudUpload,
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
import { EditProductDialogComponent } from "./edit-product-dialog.component";

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
  UploadPanacim?: boolean;
}
export interface GenerateTemResponse {
  success: boolean;
  message: string;
  totalTems: number;
}

interface CsvDataItem {
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
export class GenerateTemInDetailComponent
  implements OnInit, AfterViewChecked, OnDestroy
{
  //biến lấy id PO
  poNumber = "";
  //disable
  isDisable = false;
  isDisableInputLocation = false;
  isDisableGenerate = false;
  isDelete = false;
  allUploaded: boolean = false;
  isMobile = false;
  //biến in
  printMode: "single" | "all" = "all";

  //id nhận từ requestCreateTem
  @Input() requestId!: number;

  //column table
  displayedColumns: string[] = [
    "stt",
    "actions",
    // "numberOfPrints",
    "sapCode",
    "productName",
    "userData1",
    "userData2",
    "userData3",
    "userData4",
    "userData5",
    "partNumber",
    "lot",
    "storageUnit",
    "temQuantity",
    "initialQuantity",
    "vendor",
    "vendorName",
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
    // "slTemQuantity",
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
    // "slTemQuantity",
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
  isDeletePermission = true;
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
  faCloudUpload = faCloudUpload;

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
  searchReelId: string = "";
  filteredTemDetailList: any[] = [];
  //tat ca tem
  allTemDetails: TemDetail[] = [];

  // CSV Preview data
  csvFileName = "CSV_IN_TEM.csv";
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
    this.checkScreenSize();
    window.addEventListener("resize", () => this.checkScreenSize());
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
        this.allUploaded = this.materials.every(
          (item) => item.UploadPanacim === true,
        );
        this.updatePagedMaterials();

        if (this.materials.length > 0) {
          this.poNumber = this.materials[0].userData5;
          const productId = this.materials[0].id;
          this.getListAllTemDetails(productId);
        }
      });
  }
  checkScreenSize(): void {
    this.isMobile = window.innerWidth <= 767;
  }
  ngOnDestroy(): void {
    window.removeEventListener("resize", () => this.checkScreenSize());
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

  getListAllTemDetails(productId: number): void {
    this.generateTemInService
      .getTemDetailsByProductId(productId)
      .subscribe((data) => {
        this.temDetailList = data;
        this.isDisable = data.length === 0;
        this.isDisableGenerate = data.length > 0;
        // console.log("data detail: ", data);
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

    this.warehouseCache
      .searchByName(keyword)
      .then((results: CachedWarehouse[]) => {
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
      `Xác nhận tạo mã tem cho tất cả sản phẩm thuộc yêu cầu này?`,
    ).subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }

      this.isGenerating = true;

      this.generateTemInService.generateTemByRequest(requestId).subscribe({
        next: (response) => {
          // console.log("Response:", response);

          const type = response.success ? "success" : "error";
          this.showSnackbar(response.message, "Đóng", 3000, type);

          this.isGenerating = false;
          setTimeout(() => {
            this.getListRequest(this.requestId);
          }, 1000);
        },
        error: (err) => {
          this.showSnackbar(
            err.message ?? "Có lỗi xảy ra khi tạo tem!",
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
    // console.log("Export to Excel");
    // Implement Excel export logic here
  }
  get filteredLabels(): TemDetail[] {
    return this._filteredLabelsCache;
  }

  printTable(): void {
    // console.log("🖨️ Starting print...");
    // console.log("Total labels:", this.filteredLabels.length);

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
    printContainer.id = "printPreviewClone";

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
      #printPreviewClone {
        display: block !important;
        visibility: visible !important;
        position: static !important;
        margin: 0 !important;
        padding: 0 !important;
        overflow: visible !important;
        width: 102.5mm !important;
        height: auto !important;
      }

      #printPreviewClone,
      #printPreviewClone * {
        visibility: visible !important;
      }

      /* Label = page */
      #printPreviewClone .label {
        width: 102.5mm !important;
        height: 35mm !important;
        margin: 0 !important;
        padding: 1mm 3mm !important;
        position: relative !important;
        overflow: hidden !important;
        background: white !important;
        page-break-after: always !important;
        page-break-inside: avoid !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .label:last-of-type {
        page-break-after: avoid !important;
      }

      /* Content - 3 cột chính */
      #printPreviewClone .label-content {
        position: relative !important;
        width: 100% !important;
        flex: 1 !important;
        padding: 0.8mm !important;
        gap: 5mm !important;
        transform: none !important;
        background: white !important;
        box-sizing: border-box !important;
        display: flex !important;
      }

      /* Cột trái - QR CODE NHỎ HƠN */
      #printPreviewClone .col-left {
        width: 22mm !important;
        gap: 1mm !important;
        padding: 1mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .qr-box {
        width: 18mm !important;
        height: 18mm !important;
        border: 0.3px solid #000 !important;
        border-radius: 3px !important;
        padding: 1mm !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
      }

      /* QR CODE - Giữ độ phân giải cao nhưng hiển thị nhỏ */
      #printPreviewClone .qr-box qrcode,
      #printPreviewClone .qr-box canvas {
        width: 16mm !important;
        height: 16mm !important;
        max-width: 16mm !important;
        max-height: 16mm !important;
      }

      #printPreviewClone .msd-info {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
      }

      #printPreviewClone .msd-title {
        font-size: 6pt !important;
        margin-bottom: 0.3mm !important;
      }

      #printPreviewClone .msd-item {
        font-size: 6.5pt !important;
        line-height: 1.15 !important;
        font-weight: bold !important;
      }

      /* Cột giữa */
      #printPreviewClone .col-center {
        flex: 1 !important;
        gap: 1mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .logo-box {
        height: 5mm !important;
      }

      #printPreviewClone .logo-img {
        height: 4.5mm !important;
      }

      #printPreviewClone .sap-code {
        font-size: 16pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .part-number {
        font-size: 10pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .reel-id-tem {
        font-size: 8pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .info-grid-tem {
        gap: 0.2mm !important;
        line-height: 1 !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .info-row-tem {
        font-size: 6pt !important;
        line-height: 1.1 !important;
        gap: 1mm !important;
        display: flex !important;
      }

      #printPreviewClone .info-label-tem {
        min-width: 10mm !important;
        font-weight: bold !important;
      }

      #printPreviewClone .qty-big {
        font-size: 9pt !important;
        font-weight: bold !important;
      }
      
      #printPreviewClone .info-value-tem {   
        font-size: 7pt !important;
        font-weight: bold !important;
      }

      /* Cột phải */
      #printPreviewClone .col-right {
        width: 28mm !important;
        gap: 0.3mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .rank-section {
        gap: 0.2mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .rank-item-small {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
        font-weight: bold !important;
        display: flex !important;
        justify-content: space-between !important;
      }

      #printPreviewClone .storage-unit {
        font-size: 6pt !important;
        padding: 0.3mm 0.5mm !important;
        line-height: 1.2 !important;
      }

      #printPreviewClone .lot-info {
        font-size: 5.5pt !important;
        line-height: 1.1 !important;
        display: flex !important;
        gap: 1mm !important;
      }

      #printPreviewClone .barcode-box {
        padding: 0.5mm !important;
        gap: 0.2mm !important;
        max-height: 15.5mm !important;
        border: 0.5px solid #333 !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .bc-item-compact {
        gap: 0.2mm !important;
        display: flex !important;
        flex-direction: column !important;
      }

      #printPreviewClone .bc-line {
        display: flex !important;
        align-items: center !important;
        gap: 0.3mm !important;
      }

      #printPreviewClone .bc-label {
        font-size: 5.5pt !important;
        line-height: 1 !important;
        font-weight: bold !important;
      }

      #printPreviewClone .bc-code {
        font-size: 5.5pt !important;
        line-height: 1 !important;
      }

      #printPreviewClone .bc-svg {
        height: 3.5mm !important;
        flex: 1 !important;
      }

      #printPreviewClone .bc-svg svg {
        height: 100% !important;
        width: auto !important;
        max-width: 100% !important;
        object-fit: contain !important;
      }

      /* PRODUCT NAME - Hiển thị ở dưới cùng */
      #printPreviewClone .product-name-small {
        display: block !important;
        visibility: visible !important;
        font-size: 7pt !important;
        line-height: 0.4 !important;
        padding: 0.5mm 1mm !important;
        margin: 0 !important;
        width: 100% !important;
        box-sizing: border-box !important;
        overflow: hidden !important;
        text-overflow: ellipsis !important;
        white-space: nowrap !important;
        background: white !important;
      }

      /* Colors */
      #printPreviewClone * {
        print-color-adjust: exact !important;
        -webkit-print-color-adjust: exact !important;
      }

      /* QR & Barcodes */
      #printPreviewClone canvas,
      #printPreviewClone qrcode,
      #printPreviewClone qrcode canvas,
      #printPreviewClone svg,
      #printPreviewClone img {
        visibility: visible !important;
        display: block !important;
        opacity: 1 !important;
        print-color-adjust: exact !important;
        -webkit-print-color-adjust: exact !important;
      }

      body>*:not(#printPreviewClone) {
        display: none !important;
      }
    }
  `;
    document.head.appendChild(style);

    // console.log(" Temp container created");

    //  Đợi DOM render xong
    setTimeout(() => {
      window.print();

      //  Cleanup sau khi print
      const cleanup = (): void => {
        // console.log(" Cleaning up...");

        // Xóa temp elements
        printContainer.remove();
        style.remove();

        // Hiện lại modal
        modal.style.display = "block";

        window.removeEventListener("afterprint", cleanup);
        // console.log(" Cleanup done");
      };

      window.addEventListener("afterprint", cleanup);
      setTimeout(cleanup, 3000); // Fallback
      this.closePrintModal();
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
      // console.log("TemDetailList:", this.temDetailList);
      // console.log("SelectedItem ID:", item.id);
      this.loadTemDetails(item.id, () => {
        this.prepareSingleCsvData(Number(item.id));
        item.exportedCsv = true;
      });

      document.body.classList.add("modal-open");
    }
    if (action === "exportCsvToFixedIP") {
      if (action === "exportCsvToFixedIP") {
        this.confirmAction(
          "Bạn có chắc chắn muốn gửi dữ liệu sản phẩm '" +
            item.sapCode +
            "' đến hệ thống không?",
        ).subscribe((confirmed) => {
          if (confirmed) {
            this.selectedItem = item;
            this.loadTemDetails(item.id, () => {
              this.exportSingleProductCsvToFixedIP(Number(item.id));
              item.UploadPanacim = true;
            });
            document.body.classList.add("modal-open");
          }
        });
      }
    }
    if (action === "edit") {
      this.openEditDialog(item, index);
    }

    if (action === "print") {
      this.printMode = "single";
      this.selectedProductItem = item;
      document.body.classList.add("modal-open");

      this.loadTemDetails(item.id, () => {
        this.openPrintPreviewForProduct(item.id);
        item.printed = true;
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
  canDelete(item: any): boolean {
    // Nếu không có quyền xóa thì luôn false
    if (!this.isDeletePermission) {
      return false;
    }

    // Nếu đã xuất CSV, gửi server hoặc in tem thì không cho xóa
    return !item.exportedCsv && !item.UploadPanacim && !item.printed;
  }

  openEditDialog(item: any, index: number): void {
    const dialogRef = this.dialog.open(EditProductDialogComponent, {
      width: "800px",
      maxHeight: "90vh",
      data: { item: { ...item } }, // Clone object
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result && result.action === "save") {
        this.updateProduct(item, result.data);
      }
    });
  }
  updateProduct(item: any, formData: any): void {
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

    const payload = {
      input: {
        id: Number(item.id),
        sapCode: formData.sapCode,
        productName: formData.productName,
        requestCreateTemId: item.requestCreateTemId,
        partNumber: formData.partNumber,
        lot: formData.lot,
        temQuantity: formData.temQuantity,
        initialQuantity: formData.initialQuantity,
        vendor: formData.vendor,
        vendorName: formData.vendorName,
        userData1: formData.userData1,
        userData2: formData.userData2,
        userData3: formData.userData3,
        userData4: formData.userData4,
        userData5: formData.userData5,
        storageUnit: formData.storageUnit,
        expirationDate: formData.expirationDate,
        manufacturingDate: formData.manufacturingDate,
        arrivalDate: formData.arrivalDate,
        numberOfPrints: item.numberOfPrints,
      },
    };

    // Gọi mutation
    this.generateTemInService.updateProductOfRequest(payload.input).subscribe({
      next: (res) => {
        if (res.data?.updateProductOfRequest?.success) {
          // Cập nhật item trong danh sách với data đã format
          Object.assign(item, {
            ...formData,
            // Đảm bảo các trường date được cập nhật đúng
            expirationDate: formData.expirationDate,
            manufacturingDate: formData.manufacturingDate,
            arrivalDate: formData.arrivalDate,
          });

          this.showSnackbar("Cập nhật thành công!", "Đóng", 3000, "success");
        } else {
          this.showSnackbar(
            "Cập nhật thất bại: " + res.data?.updateProductOfRequest,
            "Đóng",
            3000,
            "error",
          );
        }
      },
      error: (err) => {
        console.error("Lỗi khi gọi mutation:", err);
        this.showSnackbar("Lỗi khi cập nhật", "Đóng", 3000, "error");
      },
    });
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
        // console.log("Loaded tem details:", data.length);
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
  filterTemDetails(): void {
    const keyword = this.searchReelId.trim().toLowerCase();
    if (!keyword) {
      this.filteredTemDetailList = this.temDetailList;
      return;
    }

    this.filteredTemDetailList = this.temDetailList.filter((item) =>
      item.reelId?.toLowerCase().includes(keyword),
    );
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
    // console.log("Kho đã chọn:", unit);
  }
  //save location button
  saveLocation(): void {
    // console.log("Kho đang chọn:", this.selectedStorageUnit);
    // console.log("Request ID:", this.requestId);

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
              this.getListRequest(this.requestId);
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
  openCsvPreview(requestId: number): void {
    this.prepareCsvData(requestId);
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
    // console.log("Matched tem:", matchedItems);
    this.csvData = matchedItems.map((matched) => ({
      id: matched.productOfRequestId,
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
    }));
    this.showCsvModal = true;
  }

  prepareCsvDataToIP(requestId: number): void {
    this.generateTemInService
      .getTemDetailsByRequestId(requestId)
      .subscribe((data) => {
        this.temDetailList = data;

        this.csvData = data.map((item) => ({
          id: item.productOfRequestId,
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
        }));

        this.isDisable = this.csvData.length === 0;

        if (this.csvData.length > 0) {
          // console.log("📋 First item sample:", this.csvData[0]);
        }
      });
  }

  prepareCsvData(requestId: number): void {
    this.generateTemInService
      .getTemDetailsByRequestId(requestId)
      .subscribe((data) => {
        this.temDetailList = data;

        this.csvData = data.map((item) => ({
          id: item.productOfRequestId,
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
        }));

        this.isDisable = this.csvData.length === 0;

        if (this.csvData.length > 0) {
          // console.log("📋 First item sample:", this.csvData[0]);
        }

        this.showCsvModal = true;
        document.body.classList.add("modal-open");
      });
  }

  exportCsv(requestId: number): void {
    // Đảm bảo csvData được chuẩn bị trước khi tạo CSV
    if (!this.csvData || this.csvData.length === 0) {
      this.prepareCsvData(requestId);
    }

    // Kiểm tra lại sau khi prepare
    if (!this.csvData || this.csvData.length === 0) {
      this.showSnackbar(
        "Không có dữ liệu để xuất CSV!",
        "Đóng",
        3000,
        "warning",
      );
      return;
    }

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
    // console.log("🔍 generateCsvContent - csvData length:", this.csvData.length);

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

    const rows = this.csvData.map((item) => {
      const row = [
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
        "1",
        "",
        "",
        "",
        "",
        "", // MSD fields & others
        "",
        item.storageUnit,
        "", // SubStorageUnit
        "", // LocationOverride
        item.expirationDate?.replace(/-/g, ""),
        item.manufacturingDate?.replace(/-/g, ""),
        "", // Partclass
        item.sapCode,
      ];
      // console.log("Row data:", row);
      return row;
    });

    // console.log("Total rows (including header):", rows.length + 1);

    const csvRows = [headers, ...rows]
      .map((row) => row.map((cell) => cell ?? "").join(","))
      .join("\n");

    return "\ufeff" + csvRows;
  }

  onExportAllClicked(): void {
    this.confirmAction(
      "Bạn có chắc chắn muốn gửi toàn bộ dữ liệu đến hệ thống không?",
    ).subscribe((confirmed) => {
      if (confirmed) {
        this.exportCsvToFixedIP(this.requestId);
      }
    });
  }

  exportCsvToFixedIP(requestId: number): void {
    this.generateTemInService.getTemDetailsByRequestId(requestId).subscribe({
      next: (data) => {
        this.temDetailList = data;

        this.csvData = data.map((item) => ({
          id: item.productOfRequestId,
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
        }));

        if (!this.csvData || this.csvData.length === 0) {
          this.showSnackbar(
            "Không có dữ liệu để xuất CSV!",
            "Đóng",
            3000,
            "warning",
          );
          return;
        }

        const csvContent = this.generateCsvContent();
        const blob = new Blob([csvContent], {
          type: "text/csv;charset=utf-8;",
        });

        const apiEndpoint = "/api/csv-upload";
        const formData = new FormData();
        formData.append("file", blob, this.csvFileName);

        this.http.post<any>(apiEndpoint, formData).subscribe({
          next: (response) => {
            if (response.success) {
              this.showSnackbar(
                "Gửi thông tin tới hệ thống thành công!",
                "Đóng",
                5000,
                "success",
              );

              // Gọi mutation cập nhật UploadPanacim cho từng sản phẩm
              this.csvData.forEach((item) => {
                const product = this.materials.find(
                  (p) => Number(p.id) === Number(item.productOfRequestId),
                );
                if (product) {
                  this.generateTemInService
                    .updateUploadPanacim(product)
                    .subscribe({
                      next: (res) => {
                        // console.log(
                        //   `Đã cập nhật UploadPanacim cho sản phẩm ${product.id}`,
                        // );
                        // product.uploadPanacim = true;
                        this.allUploaded = this.materials.every(
                          (p) => p.UploadPanacim === true,
                        );
                        setTimeout(() => {
                          this.getListRequest(this.requestId);
                        }, 1000);
                      },
                      error: (err) => {
                        console.error(
                          `Lỗi khi cập nhật UploadPanacim cho sản phẩm ${product.id}:`,
                          err,
                        );
                      },
                    });
                }
              });
            } else {
              this.showSnackbar(
                `Lỗi: ${response.message}`,
                "Đóng",
                5000,
                "error",
              );
            }
          },
          error: (error) => {
            console.error("Error uploading CSV:", error);
            this.showSnackbar(
              `Lỗi khi gửi: ${error.error?.message ?? error.message ?? "Không thể kết nối đến server"}`,
              "Đóng",
              5000,
              "error",
            );
          },
        });
      },
      error: (err) => {
        console.error("Lỗi khi tải tem detail:", err);
        this.showSnackbar("Không thể tải dữ liệu tem!", "Đóng", 3000, "error");
      },
    });
  }

  // Export CSV to fixed IP for single product row
  // This function now uses the backend API endpoint
  exportSingleProductCsvToFixedIP(productId: number): void {
    // Delegate to the new independent function
    this.importSingleProductCsvToFixedIP(productId);
  }

  /**
   * Independent function to import CSV data to fixed IP address
   * This function works independently without affecting other UI functions
   * @param csvDataItems - Array of CSV data items to import
   * @param targetIP - Target IP address (default: //192.168.68.16/Upload_software)
   * @param fileName - Custom file name (optional)
   */
  importCsvToFixedIP(csvDataItems: CsvDataItem[], fileName?: string): void {
    // console.log("=== importCsvToFixedIP START ===");
    // console.log("CSV data items count:", csvDataItems?.length);

    if (!csvDataItems || csvDataItems.length === 0) {
      // console.warn("No CSV data to send");
      this.showSnackbar("Không có dữ liệu để gửi", "Đóng", 3000, "warning");
      return;
    }

    // Generate CSV headers
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

    // Generate CSV rows from data
    const rows = csvDataItems.map((item) => [
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
      "1",
      "",
      "",
      "",
      "",
      "",
      "",
      item.storageUnit,
      "",
      "",
      item.expirationDate?.replace(/-/g, ""),
      item.manufacturingDate?.replace(/-/g, ""),
      "",
      item.sapCode,
    ]);

    // console.log("CSV rows generated:", rows.length);
    // console.log("First CSV row:", rows[0]);

    // Create CSV content
    const csvRows = [headers, ...rows]
      .map((row) => row.map((cell) => cell ?? "").join(","))
      .join("\n");

    const csvContent = "\ufeff" + csvRows;
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });

    // console.log("CSV blob created, size:", blob.size, "bytes");

    // Prepare form data
    const formData = new FormData();
    const defaultFileName =
      fileName ?? `CSV_IMPORT_${new Date().toISOString().split("T")[0]}.csv`;
    formData.append("file", blob, defaultFileName);

    // console.log("File name:", defaultFileName);
    // console.log("FormData prepared");

    // Use backend API endpoint instead of direct SMB access
    const apiEndpoint = "/api/csv-upload";
    // console.log("Sending POST request to:", apiEndpoint);

    // Upload via backend API
    this.http.post<any>(apiEndpoint, formData).subscribe({
      next: (response) => {
        // console.log("=== SERVER RESPONSE ===");
        // console.log("Response:", response);
        // console.log("Success:", response.success);
        // console.log("Message:", response.message);
        // console.log("File path:", response.filePath);
        // console.log("======================");

        if (response.success) {
          this.showSnackbar(
            `Đã gửi ${csvDataItems.length} bản ghi tới hệ thống thành công!`,
            "Đóng",
            3000,
            "success",
          );
        } else {
          this.showSnackbar(`Lỗi: ${response.message}`, "Đóng", 5000, "error");
        }
      },
      error: (error) => {
        console.error("=== HTTP ERROR ===");
        console.error("Error object:", error);
        console.error("Error status:", error.status);
        console.error("Error message:", error.message);
        console.error("Error body:", error.error);
        console.error("==================");

        this.showSnackbar(
          `Lỗi khi gửi: ${error.error?.message ?? error.message ?? "Không thể kết nối đến server"}`,
          "Đóng",
          5000,
          "error",
        );
      },
    });
  }

  /**
   * Import CSV for a specific product to fixed IP
   * This is a wrapper function that uses the independent importCsvToFixedIP
   * @param productId - Product ID to export
   */
  importSingleProductCsvToFixedIP(productId: number): void {
    // console.log("=== importSingleProductCsvToFixedIP START ===");
    // console.log("Product ID:", productId, "Type:", typeof productId);

    this.loadTemDetails(productId, () => {
      // console.log(
      //   "Tem details loaded, total items:",
      //   this.temDetailList.length,
      // );

      const matchedItems = this.temDetailList.filter(
        (item) => item.productOfRequestId === productId,
      );

      // console.log("Matched items for product:", matchedItems.length);
      // console.log("Matched items data:", matchedItems);

      if (matchedItems.length === 0) {
        console.warn("No matched items found for productId:", productId);
        this.showSnackbar(
          "Không tìm thấy dữ liệu cho sản phẩm này",
          "Đóng",
          3000,
          "warning",
        );
        return;
      }

      const csvData = matchedItems.map((matched) => ({
        id: matched.productOfRequestId, // dùng để cập nhật UploadPanacim
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
      }));

      // console.log("CSV data prepared, rows:", csvData.length);
      // console.log("First row sample:", csvData[0]);

      const fileName = `CSV_UP_Panacim_${new Date().toISOString().split("T")[0]}.csv`;
      // console.log("File name:", fileName);

      this.importCsvToFixedIP(csvData, fileName);
      const product = this.materials.find(
        (p) => Number(p.id) === Number(productId),
      );
      // console.log("Danh sách sản phẩm:", this.materials);
      // console.log("Đang tìm sản phẩm có id =", productId);

      if (!product) {
        console.warn(
          "Không tìm thấy sản phẩm trong danh sách gốc để cập nhật UploadPanacim",
        );
        return;
      }
      this.generateTemInService.updateUploadPanacim(product).subscribe({
        next: (res) => {
          // console.log("Đã cập nhật UploadPanacim:", res.data);
          setTimeout(() => {
            this.getListRequest(this.requestId);
          }, 1000);
        },
        error: (err) => {
          console.error("Lỗi khi cập nhật UploadPanacim:", err);
        },
      });
    });
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
          // console.log("Tổng số tem để in:", this.printLabels.length);
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
        // Tìm MaterialItem tương ứng
        const material = this.materials.find(
          (m) => m.productOfRequestId === label.productOfRequestId,
        );

        // Nếu tìm thấy, lấy temQuantity từ đó
        const count = material?.temQuantity ?? 1;

        for (let i = 0; i < count; i++) {
          expanded.push(JSON.parse(JSON.stringify(label)));
        }
      });

      this._filteredLabelsCache = expanded;
    }
  }

  private generateBarcodes(): void {
    // console.log(" Generating barcodes...");

    this._filteredLabelsCache.forEach((label, i) => {
      //"Dùng ID mới: label.id + index
      const uniqueId = `${label.id}-${i}`;
      const nvTarget = document.getElementById(`barcode-nv-${uniqueId}`);
      const poTarget = document.getElementById(`barcode-po-${uniqueId}`);

      const rawDate = label.arrivalDate ?? "000000";
      const compactDate = rawDate.replace(/-/g, "").slice(2);

      if (nvTarget) {
        try {
          JsBarcode(nvTarget, compactDate ?? "000000", {
            format: "CODE128",
            lineColor: "#000",
            width: 2,
            height: 25,
            displayValue: false,
            margin: 0,
          });
        } catch (e) {
          console.error(`Barcode NV error at ${i}:`, e);
        }
      }

      if (poTarget) {
        try {
          JsBarcode(poTarget, label.userData5 ?? "00000", {
            format: "CODE128",
            lineColor: "#000",
            width: 2,
            height: 25,
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
