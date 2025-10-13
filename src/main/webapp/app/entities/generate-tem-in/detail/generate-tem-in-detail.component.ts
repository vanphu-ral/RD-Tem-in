import { HttpClient } from "@angular/common/http";
import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  AfterViewChecked,
} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
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
} from "@fortawesome/free-solid-svg-icons";
import JsBarcode from "jsbarcode";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { ListRequestCreateTem } from "../models/list-request-create-tem.model";
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
  lanIn?: number;
  soTem?: number;
}
export interface GenerateTemResponse {
  success: boolean;
  message: string;
  totalTems: number;
}

interface CsvDataItem {
  reelId: string;
  sapCode: string;
  productName: string;
  partNumber: string;
  lot: string;
  storageUnit: string;
  initialQuantity: number;
  vendor: string;
  userData1: string;
}

interface PrintLabel {
  qrCode?: string;
  msd: string;
  exp: string;
  code: string;
  productName: string;
  orderCode: string;
  qty: number;
  vendor: string;
  lot: string;
  rankM: string;
  rankA: string;
  rankQ: string;
  storageUnit: string;
  nw: string;
  po: string;
  nv: string;
  note: string;
  lo: string;
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
@Component({
  selector: "jhi-generate-tem-in-import",
  templateUrl: "./generate-tem-in-detail.component.html",
  styleUrl: "./generate-tem-in-detail.component.scss",
  standalone: false,
})
export class GenerateTemInDetailComponent implements OnInit, AfterViewChecked {
  //biến lấy id PO
  poNumber = "12345";
  //biến in
  printMode: "single" | "all" = "all";

  //biến chứa kho
  storageUnits: string[] = ["RD-01", "RD-02", "RD-03", "RD-04", "RD-05"];
  selectedStorageUnit: string | null = null;
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

  //pagination
  currentPage = 1;
  totalPages = 10;
  pages = [1, 2, 3];

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
  constructor(
    private http: HttpClient,
    private generateTemInService: GenerateTemInService,
  ) {}
  ngAfterViewChecked(): void {
    if (this.printLabels?.length > 0) {
      this.printLabels.forEach((label, i) => {
        const nvTarget = document.getElementById(`barcode-nv-${i}`);
        const poTarget = document.getElementById(`barcode-po-${i}`);

        //rút gọn từ 2025-10-11 -> "251011"
        const rawDate = label.arrivalDate || "000000"; // "2025-10-11"
        const compactDate = rawDate.replace(/-/g, "").slice(2); // "251011"

        if (nvTarget) {
          JsBarcode(nvTarget, compactDate || "000000", {
            format: "CODE128",
            lineColor: "#000",
            width: 2,
            height: 28,
            displayValue: true,
            textAlign: "right",
          });
        }

        if (poTarget) {
          JsBarcode(poTarget, label.userData5 || "00000", {
            format: "CODE128",
            lineColor: "#000",
            width: 2,
            height: 28,
            displayValue: true,
            textAlign: "right",
          });
        }
      });
    }
  }

  ngOnInit(): void {
    this.generateTemInService.getMaterialItems().subscribe((data) => {
      this.materials = data;
    });
    this.generateTemInService.getRequestList().subscribe((data) => {
      this.requestList = data;
    });
  }

  onGenerateTemForRequest(): void {
    if (!this.selectedStorageUnit) {
      alert("Vui lòng chọn kho!");
      return;
    }

    if (
      confirm(
        `Bạn có chắc muốn tạo tem cho TẤT CẢ sản phẩm với kho ${this.selectedStorageUnit}?`,
      )
    ) {
      this.isGenerating = true;

      this.generateTemInService
        .generateTem(this.selectedStorageUnit)
        .subscribe({
          next: (response) => {
            console.log("Response:", response);

            if (response.success) {
              alert(`${response.message}`);
            } else {
              alert(` ${response.message}`);
            }

            this.isGenerating = false;
          },
          error: (err) => {
            console.error("Lỗi:", err);
            alert("Có lỗi xảy ra khi tạo tem!");
            this.isGenerating = false;
          },
        });
    }
  }

  exportToExcel(): void {
    console.log("Export to Excel");
    // Implement Excel export logic here
  }

  printTable(): void {
    // Convert canvas QR codes to images for printing
    const qrElements = document.querySelectorAll(".label qrcode");
    const originalContents: { element: Element; html: string }[] = [];

    //Phân biệt chế độ in single||all
    const filteredQRElements = Array.from(qrElements).filter((el) => {
      if (this.printMode === "single") {
        const reelId = el
          .closest(".label")
          ?.querySelector(".big-code")
          ?.textContent?.trim();
        return reelId === this.selectedProductItem?.reelId;
      }
      return true;
    });

    filteredQRElements.forEach((qrElement) => {
      const canvas = qrElement.querySelector("canvas");
      if (canvas) {
        // Lưu nội dung gốc
        originalContents.push({
          element: qrElement,
          html: qrElement.innerHTML,
        });

        // Convert canvas to image
        const imgDataUrl = canvas.toDataURL("image/png");
        const img = document.createElement("img");
        img.src = imgDataUrl;
        img.style.width = canvas.width + "px";
        img.style.height = canvas.height + "px";
        img.style.display = "block";

        // Thay canvas bằng img
        qrElement.innerHTML = "";
        qrElement.appendChild(img);
      }
    });

    // Print sau khi đã convert xong
    setTimeout(() => {
      window.print();

      // Restore lại canvas sau khi print (khi user đóng print dialog)
      const restoreQRCodes = (): void => {
        originalContents.forEach(({ element, html }) => {
          element.innerHTML = html;
        });
        window.removeEventListener("afterprint", restoreQRCodes);
      };

      // Listen cho afterprint event
      window.addEventListener("afterprint", restoreQRCodes);

      // Fallback: restore sau 2 giây nếu afterprint không fire
      setTimeout(restoreQRCodes, 2000);
    }, 300);
  }
  onAction(action: string, item: any, index: number): void {
    if (action === "view") {
      this.selectedProductItem = item;
      this.showQrModal = true;
      this.loadTemDetails(item.id); // item.id là id của list_product_of_request
      document.body.classList.add("modal-open");
    }
    if (action === "print") {
      this.printMode = "single";
      this.selectedProductItem = item;
      document.body.classList.add("modal-open");

      this.loadTemDetails(item.id, () => {
        this.openPrintPreviewForProduct();
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

        if (callback) {
          callback(); // gọi sau khi dữ liệu đã sẵn sàng
        }
      },
      error: (err) => {
        console.error("Error loading tem details:", err);
        alert("Không thể tải danh sách tem!");
        this.isLoadingTemDetails = false;
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

  //save btn
  save(): void {
    console.log("Save changes");
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

  prepareCsvData(): void {
    // Generate CSV preview data from materials
    this.csvData = this.materials.map((item, index) => ({
      reelId: "92520250628162829",
      sapCode: item.sapCode,
      productName: item.productName,
      partNumber: item.partNumber,
      lot: item.lot,
      storageUnit: item.storageUnit,
      initialQuantity: item.initialQuantity,
      vendor: "V900000136",
      userData1: "250916110017A",
    }));
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
      "SAPCode",
      "Tên SP",
      "PartNumber",
      "LOT",
      "StorageUnit",
      "InitialQuantity",
      "Vendor",
      "UserData1",
    ];
    const rows = this.csvData.map((item) => [
      item.reelId,
      item.sapCode,
      item.productName,
      item.partNumber,
      item.lot,
      item.storageUnit,
      item.initialQuantity,
      item.vendor,
      item.userData1,
    ]);

    const csvRows = [headers, ...rows].map((row) => row.join(",")).join("\n");
    return "\ufeff" + csvRows; // Add BOM for UTF-8
  }

  // Print Preview Methods
  // Print Preview Methods
  openPrintPreview(): void {
    this.preparePrintLabels();
    this.showPrintModal = true;
    document.body.classList.add("modal-open");
  }
  openPrintPreviewForProduct(): void {
    console.log("🖨️ In tem cho product:", this.selectedProductItem?.partNumber);

    if (!this.temDetailList || this.temDetailList.length === 0) {
      alert("Chưa có tem nào cho sản phẩm này!");
      return;
    }
    const matchedTem = this.temDetailList.find(
      (tem) => tem.productOfRequestId === Number(this.selectedProductItem?.id),
    );

    if (!matchedTem) {
      alert("Không tìm thấy tem phù hợp để in!");
      return;
    }

    this.printLabels = [matchedTem];

    console.log("Số tem của product này:", this.printLabels.length);

    this.showPrintModal = true;
  }

  openPrintPreviewAll(): void {
    this.printMode = "all";
    console.log("🖨️ In TẤT CẢ tem");

    // Load tất cả tem từ info_tem_detail
    this.generateTemInService.getAllTemDetails().subscribe({
      next: (data) => {
        this.printLabels = data;
        console.log("Tổng số tem để in:", this.printLabels.length);

        if (this.printLabels.length === 0) {
          alert("Chưa có tem nào được tạo!");
          return;
        }

        this.showPrintModal = true;
        document.body.classList.add("modal-open");
      },
      error: (err) => {
        console.error("Lỗi khi load tem:", err);
        alert("Không thể tải danh sách tem!");
      },
    });
  }

  closePrintModal(): void {
    this.showPrintModal = false;
    document.body.classList.remove("modal-open");
  }

  preparePrintLabels(): void {
    // Lấy trực tiếp từ temDetailList (đã load từ modal)
    this.printLabels = [...this.temDetailList];

    console.log("Print labels prepared:", this.printLabels.length);
  }

  closeAllModals(): void {
    this.closeCsvModal();
    this.closePrintModal();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
}
