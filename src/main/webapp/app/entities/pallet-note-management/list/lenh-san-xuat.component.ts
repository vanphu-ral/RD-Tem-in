import dayjs from "dayjs/esm";
import { UntypedFormBuilder } from "@angular/forms";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { Component, Input, OnInit } from "@angular/core";
import { HttpHeaders, HttpResponse, HttpClient } from "@angular/common/http";
import { ActivatedRoute, Router } from "@angular/router";
import { combineLatest } from "rxjs";
import { NgbModal, NgbDateStruct } from "@ng-bootstrap/ng-bootstrap";

import { ILenhSanXuat } from "../lenh-san-xuat.model";

import {
  ASC,
  DESC,
  ITEMS_PER_PAGE,
  SORT,
} from "app/config/pagination.constants";
import { LenhSanXuatService } from "../service/lenh-san-xuat.service";
import { LenhSanXuatDeleteDialogComponent } from "../delete/lenh-san-xuat-delete-dialog.component";
import { PageEvent } from "@angular/material/paginator";
import { environment } from "app/environments/environment";
@Component({
  selector: "jhi-lenh-san-xuat",
  templateUrl: "./lenh-san-xuat.component.html",
  styleUrls: ["./lenh-san-xuat.component.css"],
  standalone: false,
})
export class LenhSanXuatComponent implements OnInit {
  testUrl = `${environment.baseInTemApiUrl}/warehouse-note-infos`;
  resourceUrl =
    this.applicationConfigService.getEndpointFor("api/lenh-san-xuat");
  // totalDataUrl = this.applicationConfigService.getEndpointFor(
  //   "api/lenh-san-xuat/totalData",
  // );

  // maLenhSanXuatResourceUrl = this.applicationConfigService.getEndpointFor(
  //   "api/lenhsx/ma-lenh-san-xuat",
  // );
  versionResourceUrl =
    this.applicationConfigService.getEndpointFor("api/lenhsx/version");
  sapCodetResourceUrl = this.applicationConfigService.getEndpointFor(
    "api/lenhsx/sap-code",
  );
  sapNameResourceUrl = this.applicationConfigService.getEndpointFor(
    "api/lenhsx/sap-name",
  );
  workOrderCodeResourceUrl = this.applicationConfigService.getEndpointFor(
    "api/lenhsx/work-order-code",
  );

  formSearch = this.formBuilder.group({
    maLenhSanXuat: "",
    sapCode: "",
    product_type: "",
    sapName: "",
    workOrderCode: "",
    version: "",
    storageCode: "",
    createBy: "",
    trangThai: "",
  });

  lenhSanXuats?: ILenhSanXuat[];
  lenhSanXuatGoc?: ILenhSanXuat[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  maxResultToShow = 10;
  showingResults = 10;
  currentPage = 1;
  startIndex = 0;
  // thông tin phân trang
  totalData = 0;
  nextPageBtn = false;
  lastPageBtn = false;
  backPageBtn = true;
  firstPageBtn = true;
  @Input() itemPerPage = 20;
  @Input() pageNumber = 1;
  @Input() maLenhSanXuat = "";
  @Input() version = "";
  @Input() product_type = "";
  @Input() sapCode = "";
  @Input() sapName = "";
  @Input() workOrderCode = "";
  @Input() storageCode = "";
  @Input() createBy = "";
  @Input() trangThai = "";
  @Input() entryTime: string | null = null;
  @Input() timeUpdate: string | null = null;
  @Input() groupName = "";
  // body tim kiem + pagination
  body: {
    maLenhSanXuat: string;
    sapCode: string;
    sapName: string;
    workOrderCode: string;
    product_type: string;
    version: string;
    storageCode: string;
    createBy: string;
    trangThai: string;
    entryTime: string | null;
    timeUpdate: string | null;
    itemPerPage: number;
    pageNumber: number;
  } = {
    maLenhSanXuat: "",
    sapCode: "",
    sapName: "",
    workOrderCode: "",
    product_type: "",
    version: "",
    storageCode: "",
    createBy: "",
    trangThai: "",
    entryTime: null,
    timeUpdate: null,
    itemPerPage: this.itemPerPage,
    pageNumber: this.pageNumber,
  };
  searchResult: ILenhSanXuat[] = [];
  // khởi tạo danh sách gợi ý
  listOfMaLenhSanXuat: string[] = [];
  listOfSapCode: string[] = [];
  listOfSapName: string[] = [];
  listOfWorkOrderCode: string[] = [];
  listOfVersion: string[] = [];
  resultSearchDateTime = [];
  pageSize = 20;
  pageIndex = 0;

  constructor(
    protected lenhSanXuatService: LenhSanXuatService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    protected formBuilder: UntypedFormBuilder,
  ) {}
  mappingBodySearchAndPagination(): void {
    this.body.maLenhSanXuat = this.maLenhSanXuat;
    this.body.sapCode = this.sapCode;
    this.body.sapName = this.sapName;
    this.body.workOrderCode = this.workOrderCode;
    this.body.product_type = this.product_type;
    this.body.version = this.version;
    this.body.storageCode = this.storageCode;
    this.body.createBy = this.createBy;
    this.body.entryTime = this.entryTime;
    this.body.timeUpdate = this.timeUpdate;
    this.body.itemPerPage = this.itemPerPage;
    this.body.pageNumber = this.pageNumber;
    this.body.trangThai = this.trangThai;
    // console.log('body: ', this.body);
  }
  // nextPage(): void {
  //   this.pageNumber++;
  //   this.mappingBodySearchAndPagination();
  //   this.backPageBtn = false;
  //   this.firstPageBtn = false;
  //   if (this.pageNumber === Math.floor(this.totalData / this.itemPerPage) + 1) {
  //     this.nextPageBtn = true;
  //     this.lastPageBtn = true;
  //   }
  //   this.getLenhSanXuatList();
  // }
  // lastPage(): void {
  //   this.pageNumber = Math.floor(this.totalData / this.itemPerPage) + 1;
  //   this.mappingBodySearchAndPagination();
  //   this.backPageBtn = false;
  //   this.firstPageBtn = false;
  //   this.lastPageBtn = true;
  //   this.nextPageBtn = true;
  //   this.getLenhSanXuatList();
  // }
  // backPage(): void {
  //   this.pageNumber--;
  //   this.mappingBodySearchAndPagination();
  //   this.nextPageBtn = false;
  //   this.lastPageBtn = false;
  //   if (this.pageNumber === 1) {
  //     this.backPageBtn = true;
  //     this.firstPageBtn = true;
  //   }
  //   this.getLenhSanXuatList();
  // }
  // firstPage(): void {
  //   this.pageNumber = 1;
  //   this.mappingBodySearchAndPagination();
  //   this.nextPageBtn = false;
  //   this.lastPageBtn = false;
  //   this.backPageBtn = true;
  //   this.firstPageBtn = true;
  //   this.getLenhSanXuatList();
  // }
  nextPage(): void {
    this.backPageBtn = false;
    this.firstPageBtn = false;
    if (this.pageNumber * this.itemPerPage < this.totalItems) {
      this.pageNumber++;
      this.getLenhSanXuatList();
    }
  }

  backPage(): void {
    this.nextPageBtn = false;
    this.lastPageBtn = false;
    if (this.pageNumber > 1) {
      this.pageNumber--;
      this.getLenhSanXuatList();
    }
  }

  firstPage(): void {
    this.pageNumber = 1;
    this.nextPageBtn = false;
    this.lastPageBtn = false;
    this.backPageBtn = true;
    this.firstPageBtn = true;
    this.getLenhSanXuatList();
  }

  lastPage(): void {
    this.pageNumber = Math.ceil(this.totalItems / this.itemPerPage);
    this.backPageBtn = false;
    this.firstPageBtn = false;
    this.lastPageBtn = true;
    this.nextPageBtn = true;
    this.getLenhSanXuatList();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadPage();
  }
  findFucntion(): void {
    this.mappingBodySearchAndPagination();
    setTimeout(() => {
      this.getLenhSanXuatList();
      // this.getTotalData();
    }, 100);
  }
  // Thay đổi background color ứng với mỗi trạng thái
  changeColor(): void {
    for (let i = 0; i < this.lenhSanXuats!.length; i++) {
      const element = document.getElementById(i.toString());
      if (!element) {
        continue;
      }

      // Màu theo groupName
      if (this.lenhSanXuats![i].product_type?.includes("TC01")) {
        element.style.backgroundColor = "#FFD8A8";
        element.style.border = "1px solid #E6B98C";
      } else if (this.lenhSanXuats![i].groupName?.includes("Thành phẩm")) {
        element.style.backgroundColor = "#A5D8FF";
        element.style.border = "1px solid #7FBCE6";
      } else if (this.lenhSanXuats![i].groupName?.includes("Bán thành phẩm")) {
        element.style.backgroundColor = "#B2F2BB";
        element.style.border = "1px solid #8BD9A0";
      }

      // Màu theo trạng thái
      switch (this.lenhSanXuats![i].trangThai) {
        case "Chờ duyệt":
          element.style.backgroundColor = "#FFF3BF";
          element.style.color = "#8A6D3B";
          element.style.border = "1px solid #E6DCA5";
          break;
        case "Đã gửi WMS":
          element.style.backgroundColor = "#C6F6D5";
          element.style.color = "#3F6D52";
          element.style.border = "1px solid #A3D9B8";
          break;
        case "Đã phê duyệt":
          element.style.backgroundColor = "#C6F6D5";
          element.style.color = "#3F6D52";
          element.style.border = "1px solid #A3D9B8";
          break;
        case "Sản xuất hủy":
          element.style.backgroundColor = "#FF6B6B";
          element.style.color = "#FFFFFF";
          element.style.border = "1px solid #E65C5C";
          break;
        case "Kho hủy":
          element.style.backgroundColor = "#FA5252";
          element.style.color = "#FFFFFF";
          element.style.border = "1px solid #E04444";
          break;
        case "Bản nháp":
          element.style.backgroundColor = "#D0EBFF";
          element.style.color = "#1C7ED6";
          element.style.border = "1px solid #A5D4F2";
          break;
        case "Từ chối":
          element.style.backgroundColor = "#FFE066";
          element.style.color = "#7F5F00";
          element.style.border = "1px solid #E6C957";
          break;
      }
    }
  }
  getTrangThaiClass(trangThai: string): string {
    switch (trangThai) {
      case "Bản nháp":
        return "badge-draft";
      case "Chờ duyệt":
        return "badge-pending";
      case "Đã phê duyệt":
        return "badge-approved";
      case "Từ chối":
        return "badge-rejected";
      case "Kho hủy":
        return "badge-cancelled";
      case "Sản xuất hủy":
        return "badge-production-cancelled";
      default:
        return "badge-default";
    }
  }

  // getTotalData(): void {
  //   this.http.post<any>(this.totalDataUrl, this.body).subscribe((res) => {
  //     this.totalData = res;
  //     if (this.totalData < this.itemPerPage) {
  //       this.nextPageBtn = true;
  //       this.lastPageBtn = true;
  //     } else {
  //       this.nextPageBtn = false;
  //       this.lastPageBtn = false;
  //     }
  //     // console.log('total data', res, Math.floor(this.totalData / this.itemPerPage));
  //   });
  // }
  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.lenhSanXuatService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ILenhSanXuat[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  ngOnInit(): void {
    const result = sessionStorage.getItem("tem-in-search-body");
    if (result) {
      this.body = JSON.parse(result);
      this.maLenhSanXuat = this.body.maLenhSanXuat;
      this.sapCode = this.body.sapCode;
      this.sapName = this.body.sapName;
      this.product_type = this.body.product_type;
      this.workOrderCode = this.body.workOrderCode;
      this.version = this.body.version;
      this.storageCode = this.body.storageCode;
      this.createBy = this.body.createBy;
      this.entryTime = this.body.timeUpdate;
      this.timeUpdate = this.body.timeUpdate;
      this.itemPerPage = this.body.itemPerPage;
      this.pageNumber = this.body.pageNumber;
      this.trangThai = this.body.trangThai;
      console.log("have result!");
      // this.getTotalData();
      this.getLenhSanXuatList();
      if (this.pageNumber > 1) {
        this.backPageBtn = false;
        this.firstPageBtn = false;
      }
    } else {
      console.log("no result");
      // this.getTotalData();
      this.getLenhSanXuatList();
    }
    // this.createListOfMaLenhSanXuat();
    // this.createListOfSapCode();
    // this.createListOfSapName();
    // this.createListOfVersion();
    // this.createListOfWordOrderCode();
  }

  // getLenhSanXuatList(): void {
  //   this.http.post<any>(this.resourceUrl, this.body).subscribe((res) => {
  //     this.lenhSanXuats = res;
  //     setTimeout(() => {
  //       this.changeColor();
  //       // Lưu lại key word tìm kiếm
  //       sessionStorage.setItem("tem-in-search-body", JSON.stringify(this.body));
  //     }, 500);
  //   });
  // }
  getLenhSanXuatList(): void {
    this.http.get<any[]>(this.testUrl).subscribe((res) => {
      const allData = res.map((item) => ({
        id: item.id,
        maLenhSanXuat: item.ma_lenh_san_xuat,
        sapCode: item.sap_code,
        sapName: item.sap_name,
        workOrderCode: item.work_order_code,
        product_type: item.product_type,
        version: item.version,
        storageCode: item.storage_code,
        totalQuantity: item.total_quantity,
        createBy: item.create_by,
        entryTime: item.entry_time,
        timeUpdate: item.time_update,
        trangThai: item.trang_thai,
        groupName: item.group_name,
        comment2: item.comment2,
        comment: item.comment,
        branch: item.branch,
        productType: item.product_type,
      }));

      this.totalItems = allData.length;

      const startIndex = (this.pageNumber - 1) * this.itemPerPage;
      const endIndex = startIndex + this.itemPerPage;
      this.lenhSanXuats = allData.slice(startIndex, endIndex);
      this.changeColor();
    });
  }
  reloadPage(): void {
    window.location.reload();
  }
  trackId(_index: number, item: ILenhSanXuat): number {
    return item.id!;
  }
  //============================ api lấy danh sách gợi tý từ Backend =====================
  // createListOfMaLenhSanXuat(): void {
  //   this.http.get<any>(this.maLenhSanXuatResourceUrl).subscribe((res) => {
  //     this.listOfMaLenhSanXuat = res;
  //     // // console.log(res);
  //   });
  // }
  createListOfSapCode(): void {
    this.http.get<any>(this.sapCodetResourceUrl).subscribe((res) => {
      this.listOfSapCode = res;
      // // console.log('sap code', res);
    });
  }
  createListOfSapName(): void {
    this.http.get<any>(this.sapNameResourceUrl).subscribe((res) => {
      this.listOfSapName = res;
      // // console.log('sap name', res);
    });
  }
  createListOfWordOrderCode(): void {
    this.http.get<any>(this.workOrderCodeResourceUrl).subscribe((res) => {
      this.listOfWorkOrderCode = res;
      // // console.log('Work order code', res);
    });
  }
  createListOfVersion(): void {
    this.http.get<any>(this.versionResourceUrl).subscribe((res) => {
      this.listOfVersion = res;
      // // console.log('version', res);
    });
  }
  delete(lenhSanXuat: ILenhSanXuat): void {
    const modalRef = this.modalService.open(LenhSanXuatDeleteDialogComponent, {
      size: "md",
      backdrop: "static",
    });
    modalRef.componentInstance.lenhSanXuat = lenhSanXuat;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe((deletedId) => {
      if (deletedId) {
        this.lenhSanXuats =
          this.lenhSanXuats?.filter((item) => item.id !== deletedId) ?? [];
        this.totalItems--;
      }
    });
  }
  sort(): string[] {
    const result = [this.predicate + "," + (this.ascending ? ASC : DESC)];
    if (this.predicate !== "id") {
      result.push("id");
    }
    return result;
  }
  // pop up thông báo
  alertTimeout(mymsg: string, mymsecs: number): void {
    const myelement = document.createElement("div");
    myelement.setAttribute(
      "style",
      "background-color:white;color:Black; width: 300px;height: 70px;position: absolute;top:0;bottom:0;left:0;right:0;margin:auto;border: 1px solid black;font-family:arial;font-size:16px;display: flex; align-items: center; justify-content: center; text-align: center;border-radius:10px",
    );
    myelement.innerHTML = mymsg;
    setTimeout(function () {
      if (myelement.parentNode) {
        myelement.parentNode.removeChild(myelement);
      }
    }, mymsecs);
    document.body.appendChild(myelement);
  }
  handleNavigation(): void {
    combineLatest([
      this.activatedRoute.data,
      this.activatedRoute.queryParamMap,
    ]).subscribe(([data, params]) => {
      const page = params.get("page");
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data["defaultSort"]).split(",");
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (
        pageNumber !== this.page ||
        predicate !== this.predicate ||
        ascending !== this.ascending
      ) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  onSuccess(
    data: ILenhSanXuat[] | null,
    headers: HttpHeaders,
    page: number,
    navigate: boolean,
  ): void {
    this.totalItems = Number(headers.get("X-Total-Count"));
    this.page = page;
    if (navigate) {
      this.router.navigate(["/warehouse-note-infos"], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + "," + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.lenhSanXuats = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
