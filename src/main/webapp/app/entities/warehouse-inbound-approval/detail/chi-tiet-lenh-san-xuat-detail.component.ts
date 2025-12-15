import { ngxCsv } from "ngx-csv/ngx-csv";
import dayjs from "dayjs";
import { HttpClient } from "@angular/common/http";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { ILenhSanXuat } from "./../../lenh-san-xuat/lenh-san-xuat.model";
import { Component, OnInit, ViewChild, ElementRef, Input } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import * as XLSX from "xlsx";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";

import { IChiTietLenhSanXuat } from "../chi-tiet-lenh-san-xuat.model";
import { ChiTietLenhSanXuatService } from "../service/chi-tiet-lenh-san-xuat.service";
import { PageEvent } from "@angular/material/paginator";

@Component({
  selector: "jhi-chi-tiet-lenh-san-xuat-detail",
  templateUrl: "./chi-tiet-lenh-san-xuat-detail.component.html",
  styleUrls: ["./chi-tiet-lenh-san-xuat-detail.component.css"],
  standalone: false,
})
export class ChiTietLenhSanXuatDetailComponent implements OnInit {
  resourceUrl = this.applicationConfigService.getEndpointFor(
    "/api/warehouse-stamp-info-details",
  );
  resource1Url = this.applicationConfigService.getEndpointFor(
    "/api/quan-ly-phe-duyet/trang-thai",
  );

  chiTietLenhSanXuat: IChiTietLenhSanXuat | null = null;
  lenhSanXuat: ILenhSanXuat | null = null;
  chiTietLenhSanXuats: IChiTietLenhSanXuat[] = [];
  chiTietLenhSanXuatExport: IChiTietLenhSanXuat[] = [];
  lenhSanXuats: ILenhSanXuat[] = [];

  @Input() itemPerPage = 10;
  page?: number;
  totalItems = 0;
  pageSize = 10;
  pageIndex = 0;
  predicate!: string;
  ascending!: boolean;
  isDisable = false;

  fileName = "Chi-tiet-lenh-san-xuat";

  data: {
    reelID?: string;
    partNumber?: string;
    vendor?: string;
    lot?: string;
    userData1?: string;
    userData2?: string;
    userData3?: string;
    userData4?: string;
    userData5?: number;
    initialQuantity?: number;
    msdLevel?: string | null;
    msdInitialFloorTime?: string | null;
    msdBagSealDate?: string | null;
    marketUsage?: string | null;
    quantityOverride?: number;
    shelfTime?: string | null;
    spMaterialName?: string | null;
    warningLimit?: string | null;
    maximumLimit?: string | null;
    comments?: string | null;
    warmupTime?: string | null;
    storageUnit?: string;
    subStorageUnit?: string | null;
    locationOverride?: string | null;
    expirationDate?: string;
    manufacturingDate?: string;
    partClass?: string | null;
    sapCode?: string | null;
  }[] = [];

  @ViewChild("dvData") dvData!: ElementRef;

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected applicationConfigService: ApplicationConfigService,
    protected http: HttpClient,
    protected chiTietLenhSanXuatService: ChiTietLenhSanXuatService,
    protected modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lenhSanXuat }) => {
      console.log("[DEBUG] Route data received:", lenhSanXuat);

      // Check if we received data from the resolver with warehouse_note_info structure
      if (lenhSanXuat && lenhSanXuat.warehouse_note_info) {
        const warehouseNoteInfo = lenhSanXuat.warehouse_note_info;
        const warehouseNoteInfoDetails =
          lenhSanXuat.warehouse_note_info_details || [];

        // Map warehouse_note_info to lenhSanXuat structure
        this.lenhSanXuat = {
          id: warehouseNoteInfo.id,
          maLenhSanXuat: warehouseNoteInfo.ma_lenh_san_xuat,
          sapCode: warehouseNoteInfo.sap_code,
          sapName: warehouseNoteInfo.sap_name,
          workOrderCode: warehouseNoteInfo.work_order_code,
          version: warehouseNoteInfo.version,
          storageCode: warehouseNoteInfo.storage_code,
          totalQuantity: warehouseNoteInfo.total_quantity,
          createBy: warehouseNoteInfo.create_by,
          entryTime: warehouseNoteInfo.entry_time,
          timeUpdate: warehouseNoteInfo.time_update,
          trangThai: warehouseNoteInfo.trang_thai,
          comment: warehouseNoteInfo.comment,
          groupName: warehouseNoteInfo.group_name,
          comment2: warehouseNoteInfo.comment2,
        };

        // Use warehouse_note_info_details directly
        this.chiTietLenhSanXuats = warehouseNoteInfoDetails;
        this.chiTietLenhSanXuatExport = this.chiTietLenhSanXuats.filter(
          (a) => (a as any).trang_thai === "ACTIVE",
        );
        this.dataExport(this.chiTietLenhSanXuatExport);
      } else {
        console.error("[ERROR] Invalid data structure from resolver");
        // alert('Lỗi: Không tìm thấy dữ liệu lệnh sản xuất. Vui lòng kiểm tra lại đường dẫn.');
      }
    });
  }
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  dataExport(list: IChiTietLenhSanXuat[]): void {
    for (let i = 0; i < this.chiTietLenhSanXuatExport.length; i++) {
      const data1: {
        reelID?: string;
        partNumber?: string;
        vendor?: string;
        lot?: string;
        userData1?: string;
        userData2?: string;
        userData3?: string;
        userData4?: string;
        userData5?: number;
        initialQuantity?: number;
        msdLevel?: string | null;
        msdInitialFloorTime?: string | null;
        msdBagSealDate?: string | null;
        marketUsage?: string | null;
        quantityOverride?: number;
        shelfTime?: string | null;
        spMaterialName?: string | null;
        warningLimit?: string | null;
        maximumLimit?: string | null;
        comments?: string | null;
        warmupTime?: string | null;
        storageUnit?: string;
        subStorageUnit?: string | null;
        locationOverride?: string | null;
        expirationDate?: string;
        manufacturingDate?: string;
        partClass?: string | null;
        sapCode?: string | null;
      } = {
        reelID: (list[i] as any).reel_id || "",
        partNumber: (list[i] as any).part_number || "",
        vendor: (list[i] as any).vendor || "",
        lot: (list[i] as any).lot || "",
        userData1: (list[i] as any).user_data1 || "",
        userData2: (list[i] as any).user_data2 || "",
        userData3: (list[i] as any).user_data3 || "",
        userData4: (list[i] as any).user_data4 || "",
        userData5: (list[i] as any).user_data5 || 0,
        initialQuantity: (list[i] as any).initial_quantity || 0,
        msdLevel: (list[i] as any).msd_level || "",
        msdInitialFloorTime: (list[i] as any).msd_initial_floor_time || "",
        msdBagSealDate: (list[i] as any).msd_bag_seal_date || "",
        marketUsage: (list[i] as any).market_usage || "",
        quantityOverride: (list[i] as any).quantity_override || 0,
        shelfTime: (list[i] as any).shelf_time || "",
        spMaterialName: (list[i] as any).sp_material_name || "",
        warningLimit: (list[i] as any).warning_limit || "",
        maximumLimit: (list[i] as any).maximum_limit || "",
        comments: (list[i] as any).comments || "",
        warmupTime: (list[i] as any).warmup_time || "",
        storageUnit: (list[i] as any).storage_unit || "",
        subStorageUnit: (list[i] as any).sub_storage_unit || "",
        locationOverride: (list[i] as any).location_override || "",
        expirationDate: (list[i] as any).expiration_date || "",
        manufacturingDate: (list[i] as any).manufacturing_date || "",
        partClass: (list[i] as any).part_class || "",
        sapCode: (list[i] as any).sap_code || "",
      };
      this.data.push(data1);
    }
  }
  exportCSV(): void {
    this.lenhSanXuat!.trangThai = "Đã xuất csv";
    this.http.post<any>(this.resource1Url, this.lenhSanXuat).subscribe();
    const options = {
      fieldSeparator: ",",
      quoteStrings: "",
      decimalseparator: ".",
      showLabels: true,
      showTitle: false,
      title: "Your title",
      useBom: true,
      noDownload: false,
      headers: [
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
        "MsdLevel",
        "MsdInitialFloorTime",
        "MsdBagSealDate",
        "MarketUsage",
        "QuantityOverride",
        "ShelfTime",
        "SpMaterialName",
        "WarningLimit",
        "MaximumLimit",
        "Comments",
        "WarmupTime",
        "StorageUnit",
        "SubStorageUnit",
        "LocationOverride",
        "ExpirationDate",
        "ManufacturingDate",
        "PartClass",
        "SapCode",
      ],
    };
    new ngxCsv(this.data, this.fileName, options);
  }

  exportCsvToFixedIP(): void {
    if (!this.data || this.data.length === 0) {
      alert("Không có dữ liệu để xuất CSV!");
      return;
    }

    // Generate CSV content
    const csvContent = this.generateCsvContentForServer();
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });

    // Prepare form data
    const formData = new FormData();
    const fileName = `CSV_${this.lenhSanXuat?.maLenhSanXuat ?? "export"}_${new Date().toISOString().split("T")[0]}.csv`;
    formData.append("file", blob, fileName);

    // Use backend API endpoint
    const apiEndpoint = "/api/csv-upload";

    // Upload via backend API
    this.http.post<any>(apiEndpoint, formData).subscribe({
      next: (response) => {
        if (response.success) {
          alert(`Đã gửi ${this.data.length} bản ghi tới hệ thống thành công!`);
          // Update status
          this.lenhSanXuat!.trangThai = "Đã xuất csv đến server";
          this.http.post<any>(this.resource1Url, this.lenhSanXuat).subscribe();
        } else {
          alert(`Lỗi: ${response.message}`);
        }
      },
      error: (error) => {
        console.error("Error uploading CSV:", error);
        alert(
          `Lỗi khi gửi: ${error.error?.message ?? error.message ?? "Không thể kết nối đến server"}`,
        );
      },
    });
  }

  generateCsvContentForServer(): string {
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

    const rows = this.data.map((item) => [
      item.reelID,
      item.partNumber,
      item.vendor,
      item.lot,
      item.userData1,
      item.userData2,
      item.userData3,
      item.userData4,
      item.userData5,
      item.initialQuantity,
      item.msdLevel ?? "",
      item.msdInitialFloorTime ?? "",
      item.msdBagSealDate ?? "",
      item.marketUsage ?? "",
      item.quantityOverride ?? "1",
      item.shelfTime ?? "",
      item.spMaterialName ?? "",
      item.warningLimit ?? "",
      item.maximumLimit ?? "",
      item.comments ?? "",
      item.warmupTime ?? "",
      item.storageUnit,
      item.subStorageUnit ?? "",
      item.locationOverride ?? "",
      item.expirationDate?.replace(/-/g, "") ?? "",
      item.manufacturingDate?.replace(/-/g, "") ?? "",
      item.partClass ?? "",
      item.sapCode,
    ]);

    const csvRows = [headers, ...rows]
      .map((row) => row.map((cell) => cell ?? "").join(","))
      .join("\n");

    return "\ufeff" + csvRows;
  }

  exportToExcel(): void {
    // const data = document.getElementById("table-data");
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.data);
    // create workbook
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "ChiTietSanXuatHangNgay");
    XLSX.writeFile(wb, `${this.fileName}.xlsx`);
  }
  openConfirmModal(content: any): void {
    this.modalService
      .open(content, { ariaLabelledBy: "modal-basic-title" })
      .result.then(
        (result) => {
          if (result === "confirm") {
            this.exportCsvToFixedIP();
          }
        },
        (reason) => {
          // Modal dismissed
        },
      );
  }

  panacimError(): void {
    this.lenhSanXuat!.trangThai = "Lỗi Panacim";
    this.http.post<any>(this.resource1Url, this.lenhSanXuat).subscribe(() => {
      alert("Cập nhật lỗi thành công");
      window.history.back();
    });
  }
  previousState(): void {
    window.history.back();
  }
}
