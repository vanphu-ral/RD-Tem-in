import { HttpClient } from "@angular/common/http";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { IChiTietLenhSanXuat } from "app/entities/chi-tiet-lenh-san-xuat/chi-tiet-lenh-san-xuat.model";
import { Component, OnInit, ViewChild, ElementRef, Input } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import * as XLSX from "xlsx";
import { ILenhSanXuat } from "../lenh-san-xuat.model";
import { ngxCsv } from "ngx-csv/ngx-csv";
import { PageEvent } from "@angular/material/paginator";

@Component({
  selector: "jhi-lenh-san-xuat-detail",
  templateUrl: "./lenh-san-xuat-detail.component.html",
  styleUrls: ["./lenh-san-xuat-detail.component.css"],
  standalone: false,
})
export class LenhSanXuatDetailComponent implements OnInit {
  resourceUrl = this.applicationConfigService.getEndpointFor(
    "/api/chi-tiet-lenh-san-xuat",
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
    this.activatedRoute.data.subscribe(({ lenhSanXuat }) => {
      this.lenhSanXuat = lenhSanXuat;
    });
    if (this.lenhSanXuat?.id) {
      this.http
        .get<any>(`${this.resourceUrl}/${this.lenhSanXuat.id}`)
        .subscribe((res) => {
          this.chiTietLenhSanXuats = res;
          this.chiTietLenhSanXuatExport = this.chiTietLenhSanXuats.filter(
            (a) => (a as any).trang_thai === "Active",
          );
          this.dataExport(this.chiTietLenhSanXuatExport);
        });
    }
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
        reelID: (list[i] as any).id?.toString() || "",
        partNumber: (list[i] as any).sap_code || "",
        vendor: (list[i] as any).create_by || "",
        lot: (list[i] as any).work_order_code || "",
        userData1: (list[i] as any).sap_name || "",
        userData2: (list[i] as any).version || "",
        userData3: (list[i] as any).storage_code || "",
        userData4: (list[i] as any).group_name || "",
        userData5: (list[i] as any).total_quantity || 0,
        initialQuantity: (list[i] as any).total_quantity || 0,
        msdLevel: null,
        msdInitialFloorTime: null,
        msdBagSealDate: null,
        marketUsage: null,
        quantityOverride: (list[i] as any).total_quantity || 0,
        shelfTime: null,
        spMaterialName: null,
        warningLimit: null,
        maximumLimit: null,
        comments: (list[i] as any).comment || "",
        warmupTime: null,
        storageUnit: (list[i] as any).storage_code || "",
        subStorageUnit: null,
        locationOverride: null,
        expirationDate: "",
        manufacturingDate: "",
        partClass: null,
        sapCode: (list[i] as any).sap_code || "",
      };
      this.data.push(data1);
    }
  }
  exportCSV(): void {
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
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  exportToExcel(): void {
    // const data = document.getElementById("table-data");
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.data);
    // create workbook
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "ChiTietSanXuatHangNgay");
    XLSX.writeFile(wb, `${this.fileName}.xlsx`);
  }
  previousState(): void {
    window.history.back();
  }
}
