import { HttpClient } from "@angular/common/http";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { IChiTietLenhSanXuat } from "app/entities/chi-tiet-lenh-san-xuat/chi-tiet-lenh-san-xuat.model";
import { Component, OnInit, ViewChild, ElementRef, Input } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import * as XLSX from "xlsx";
import { ILenhSanXuat } from "../lenh-san-xuat.model";
import { ngxCsv } from "ngx-csv/ngx-csv";
import { PageEvent } from "@angular/material/paginator";
import dayjs from "dayjs/esm";
import {
  WarehouseNoteInfo,
  WarehouseNoteResponse,
} from "../add-new/add-new-lenh-san-xuat.component";

@Component({
  selector: "jhi-lenh-san-xuat-detail",
  templateUrl: "./lenh-san-xuat-detail.component.html",
  styleUrls: ["./lenh-san-xuat-detail.component.css"],
  standalone: false,
})
export class LenhSanXuatDetailComponent implements OnInit {
  resourceUrl = this.applicationConfigService.getEndpointFor(
    "/api/warehouse-stamp-infos",
  );
  lenhSanXuat: ILenhSanXuat | null = null;
  fileName = "Chi-tiet-lenh-san-xuat";
  chiTietLenhSanXuats: IChiTietLenhSanXuat[] = [];
  chiTietLenhSanXuatExport: IChiTietLenhSanXuat[] = [];
  predicate!: string;
  ascending!: boolean;
  @Input() itemPerPage = 10;
  page?: number;

  // khởi tạo biến lưu dữ liệu xuất file
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
  totalItems = 0;
  pageSize = 10;
  pageIndex = 0;
  @ViewChild("dvData") dvData!: ElementRef;
  constructor(
    protected activatedRoute: ActivatedRoute,
    protected applicationConfigService: ApplicationConfigService,
    protected http: HttpClient,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      // The resolver returns WarehouseNoteResponse, not ILenhSanXuat
      const response = data["lenhSanXuat"] as WarehouseNoteResponse;

      if (response && response.warehouse_note_info) {
        // Map warehouse_note_info to lenhSanXuat (Table 1)
        this.lenhSanXuat = this.mapWarehouseNoteInfoToLenhSanXuat(
          response.warehouse_note_info,
        );

        // Map warehouse_note_info_details to chiTietLenhSanXuats (Table 2)
        if (
          response.warehouse_note_info_details &&
          response.warehouse_note_info_details.length > 0
        ) {
          this.chiTietLenhSanXuats = response.warehouse_note_info_details.map(
            (d: any): IChiTietLenhSanXuat => ({
              id: d.id,
              reelID: d.reel_id,
              partNumber: d.part_number,
              vendor: d.vendor,
              lot: d.lot,
              userData1: d.user_data_1,
              userData2: d.user_data_2,
              userData3: d.user_data_3,
              userData4: d.user_data_4,
              userData5: d.user_data_5,
              initialQuantity: d.initial_quantity,
              msdLevel: d.msd_level,
              msdInitialFloorTime: d.msd_initial_floor_time,
              msdBagSealDate: d.msd_bag_seal_date,
              marketUsage: d.market_usage,
              quantityOverride: d.quantity_override,
              shelfTime: d.shelf_time,
              spMaterialName: d.sp_material_name,
              warningLimit: d.warning_limit,
              maximumLimit: d.maximum_limit,
              comments: d.comments,
              warmupTime: d.warmup_time,
              storageUnit: d.storage_unit,
              subStorageUnit: d.sub_storage_unit,
              locationOverride: d.location_override,
              expirationDate: d.expiration_date,
              manufacturingDate: d.manufacturing_date,
              partClass: d.part_class,
              sapCode: d.sap_code,
              trangThai: d.trang_thai,
            }),
          );
          this.chiTietLenhSanXuatExport = this.chiTietLenhSanXuats.filter(
            (a) =>
              (a as any).trang_thai === "Active" ||
              (a as any).trang_thai === "ACTIVE",
          );
          this.dataExport(this.chiTietLenhSanXuatExport);
        }
      }
    });
  }

  mapWarehouseNoteInfoToLenhSanXuat(info: WarehouseNoteInfo): ILenhSanXuat {
    return {
      id: info.id,
      maLenhSanXuat: info.ma_lenh_san_xuat,
      sapCode: info.sap_code,
      sapName: info.sap_name,
      workOrderCode: info.work_order_code,
      version: info.version,
      storageCode: info.storage_code,
      totalQuantity: info.total_quantity,
      createBy: info.create_by,
      entryTime: info.entry_time ? dayjs(info.entry_time) : null,
      trangThai: info.trang_thai,
      comment: (info as any).comment ?? null,
      groupName: info.group_name,
      branch: info.branch,
      productType: info.product_type,
    };
  }
  dataExport(list: IChiTietLenhSanXuat[]): void {
    for (let i = 0; i < this.chiTietLenhSanXuatExport.length; i++) {
      const item = list[i] as any;
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
        reelID: item.reel_id || "",
        partNumber: item.part_number || "",
        vendor: item.vendor || "",
        lot: item.lot || "",
        userData1: item.user_data_1 || "",
        userData2: item.user_data_2 || "",
        userData3: item.user_data_3 || "",
        userData4: item.user_data_4 || "",
        userData5: item.user_data_5 || 0,
        initialQuantity: item.initial_quantity || 0,
        msdLevel: item.msd_level || null,
        msdInitialFloorTime: item.msd_initial_floor_time || null,
        msdBagSealDate: item.msd_bag_seal_date || null,
        marketUsage: item.market_usage || null,
        quantityOverride: item.quantity_override || 0,
        shelfTime: item.shelf_time || null,
        spMaterialName: item.sp_material_name || null,
        warningLimit: item.warning_limit || null,
        maximumLimit: item.maximum_limit || null,
        comments: item.comments || "",
        warmupTime: item.warmup_time || null,
        storageUnit: item.storage_unit || "",
        subStorageUnit: item.sub_storage_unit || null,
        locationOverride: item.location_override || null,
        expirationDate: item.expiration_date || "",
        manufacturingDate: item.manufacturing_date || "",
        partClass: item.part_class || null,
        sapCode: item.sap_code || "",
      };
      this.data.push(data1);
    }
  }
  exportCSV(): void {
    if (!this.chiTietLenhSanXuats?.length) {
      return;
    }

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
      "PartClass",
      "SapCode",
    ];

    const rows: (string | number | null | undefined)[][] =
      this.chiTietLenhSanXuats.map((item: IChiTietLenhSanXuat) => [
        item.reelID ?? "",
        item.partNumber ?? "",
        item.vendor ?? "",
        item.lot ?? "",
        item.userData1 ?? "",
        item.userData2 ?? "",
        item.userData3 ?? "",
        item.userData4 ?? "",
        item.userData5 ?? "",
        item.initialQuantity ?? "",
        item.msdLevel ?? "",
        item.msdInitialFloorTime ?? "",
        item.msdBagSealDate ?? "",
        item.marketUsage ?? "",
        item.quantityOverride ?? "",
        item.shelfTime ?? "",
        item.spMaterialName ?? "",
        item.warningLimit ?? "",
        item.maximumLimit ?? "",
        item.comments ?? "",
        item.warmupTime ?? "",
        item.storageUnit ?? "",
        item.subStorageUnit ?? "",
        item.locationOverride ?? "",
        item.expirationDate ?? "",
        item.manufacturingDate ?? "",
        item.partClass ?? "",
        item.sapCode ?? "",
      ]);

    const csvRows = [headers, ...rows]
      .map((row) => row.map((cell) => cell ?? "").join(","))
      .join("\n");

    const blob = new Blob(["\ufeff" + csvRows], {
      type: "text/csv;charset=utf-8;",
    });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `${this.fileName}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }

  exportToExcel(): void {
    if (!this.chiTietLenhSanXuats?.length) {
      return;
    }

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
      "PartClass",
      "SapCode",
    ];

    const rows: (string | number | null | undefined)[][] =
      this.chiTietLenhSanXuats.map((item: IChiTietLenhSanXuat) => [
        item.reelID ?? "",
        item.partNumber ?? "",
        item.vendor ?? "",
        item.lot ?? "",
        item.userData1 ?? "",
        item.userData2 ?? "",
        item.userData3 ?? "",
        item.userData4 ?? "",
        item.userData5 ?? "",
        item.initialQuantity ?? "",
        item.msdLevel ?? "",
        item.msdInitialFloorTime ?? "",
        item.msdBagSealDate ?? "",
        item.marketUsage ?? "",
        item.quantityOverride ?? "",
        item.shelfTime ?? "",
        item.spMaterialName ?? "",
        item.warningLimit ?? "",
        item.maximumLimit ?? "",
        item.comments ?? "",
        item.warmupTime ?? "",
        item.storageUnit ?? "",
        item.subStorageUnit ?? "",
        item.locationOverride ?? "",
        item.expirationDate ?? "",
        item.manufacturingDate ?? "",
        item.partClass ?? "",
        item.sapCode ?? "",
      ]);

    const ws: XLSX.WorkSheet = XLSX.utils.aoa_to_sheet([headers, ...rows]);
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "ChiTietSanXuatHangNgay");
    XLSX.writeFile(wb, `${this.fileName}.xlsx`);
  }
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  previousState(): void {
    window.history.back();
  }
}
