import { HttpClient } from "@angular/common/http";
import { Component, Input, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { ITEMS_PER_PAGE } from "app/config/pagination.constants";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { ScanCheckComponent } from "./scan-check.component";
import { SharedDataService } from "./shared-data.service";
import { PageEvent } from "@angular/material/paginator";

@Component({
  selector: "jhi-doi-chieu-lenh-san-xuat",
  templateUrl: "./doi-chieu-lenh-san-xuat.component.html",
  styleUrls: ["./doi-chieu-lenh-san-xuat.component.scss"],
  standalone: false,
})
export class DoiChieuLenhSanXuatComponent implements OnInit {
  doiChieuLenhSanXuatUrl = this.applicationConfigService.getEndpointFor(
    "api/scan-work-order/groupId",
  );
  WorkOrderDetailUrl = this.applicationConfigService.getEndpointFor(
    "api/scan-work-order",
  );
  tongHopURL = this.applicationConfigService.getEndpointFor("api/tong-hop");
  listOfWorkOrderPanigationURL = this.applicationConfigService.getEndpointFor(
    "api/scan-work-order/panigation",
  );
  totalItemURL = this.applicationConfigService.getEndpointFor(
    "api/scan-work-order/total",
  );
  totalPassNgURL = this.applicationConfigService.getEndpointFor(
    "api/scan-work-order/total-pass-ng",
  );
  predicate!: string;
  ascending!: boolean;
  page?: number;
  doiChieuLenhSanXuats?: any[];

  popupChiTietThongTinScan = false;
  popupConfirmSave = false;

  @Input() workOrder = "";
  @Input() lot = "";
  @Input() machineId = "";
  @Input() productCode = "";
  @Input() productName = "";
  @Input() groupName = "";
  @Input() working = "";
  @Input() createdAt = null;
  @Input() position = "";
  @Input() checkValue = "";
  @Input() itemPerPage = 10;

  //list lenh san xuat
  listOfLenhSanXuat: any[] = [];

  totalPass = 0;
  totalFail = 0;
  //phân trang
  pageNumber = 1;
  itemsPerPage = ITEMS_PER_PAGE;
  // thông tin phân trang
  totalData = 0;
  nextPageBtn = false;
  lastPageBtn = false;
  backPageBtn = true;
  firstPageBtn = true;
  totalItems = 0;
  pageSize = 10;
  pageIndex = 0;
  //Dữ liệu tìm kiếm
  body: {
    workOrder: string;
    lot: string;
    productCode: string;
    productName: string;
    groupName: string;
    createAt: string | null;
    itemPerPage: number;
    pageNumber: number;
  } = {
    workOrder: "",
    lot: "",
    productCode: "",
    productName: "",
    groupName: "",
    createAt: null,
    itemPerPage: this.itemPerPage,
    pageNumber: this.pageNumber,
  };

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
    protected applicationConfigService: ApplicationConfigService,

    protected http: HttpClient,
    private sharedDataService: SharedDataService,
    protected scanCheck: ScanCheckComponent, // private scanCheck: ScanCheckComponent
  ) {}

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadPage();
  }
  loadPage(page?: number, dontNavigate?: boolean): void {
    // this.isLoading = true;
    // const pageToLoad: number = page ?? this.page ?? 1;
    // this.lenhSanXuatService
    //   .query({
    //     page: pageToLoad - 1,
    //     size: this.itemsPerPage,
    //   })
    //   .subscribe({
    //     next: (res: HttpResponse<ILenhSanXuat[]>) => {
    //       this.isLoading = false;
    //       this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
    //     },
    //     error: () => {
    //       this.isLoading = false;
    //       this.onError();
    //     },
    //   });
  }
  getStatusClass(status: string): string {
    switch (status) {
      case "Waitting":
        return "badge-waiting";
      case "Finish":
        return "badge-finish";
      case "Running":
        return "badge-running";
      default:
        return "badge-default";
    }
  }

  mappingBodySearchAndPagination(): void {
    this.body.workOrder = this.workOrder;
    this.body.lot = this.lot;
    this.body.createAt = this.createdAt;
    this.body.productCode = this.productCode;
    this.body.productName = this.productName;
    this.body.groupName = this.groupName;
    this.body.itemPerPage = this.itemPerPage;
    this.body.pageNumber = this.pageNumber;
    // // console.log('body: ', this.body);
  }
  nextPage(): void {
    this.pageNumber++;
    this.mappingBodySearchAndPagination();
    this.backPageBtn = false;
    this.firstPageBtn = false;
    if (this.pageNumber === Math.floor(this.totalData / this.itemPerPage) + 1) {
      this.nextPageBtn = true;
    }
    this.getWorkOrderList();
  }
  lastPage(): void {
    this.pageNumber = Math.floor(this.totalData / this.itemPerPage) + 1;
    this.mappingBodySearchAndPagination();
    this.backPageBtn = false;
    this.firstPageBtn = false;
    this.lastPageBtn = true;
    this.nextPageBtn = true;
    this.getWorkOrderList();
  }
  backPage(): void {
    this.pageNumber--;
    this.mappingBodySearchAndPagination();
    this.nextPageBtn = false;
    this.lastPageBtn = false;
    if (this.pageNumber === 1) {
      this.backPageBtn = true;
      this.firstPageBtn = true;
    }
    this.getWorkOrderList();
  }
  firstPage(): void {
    this.pageNumber = 1;
    this.mappingBodySearchAndPagination();
    this.nextPageBtn = false;
    this.lastPageBtn = false;
    this.backPageBtn = true;
    this.firstPageBtn = true;
    this.getWorkOrderList();
  }
  findFucntion(): void {
    this.mappingBodySearchAndPagination();
    setTimeout(() => {
      this.getWorkOrderList();
      this.getTotalData();
    }, 100);
  }
  getTotalData(): void {
    this.http.post<any>(this.totalItemURL, this.body).subscribe((res) => {
      this.totalData = res;
      if (this.totalData < this.itemPerPage) {
        this.nextPageBtn = true;
        this.lastPageBtn = true;
      } else {
        this.nextPageBtn = false;
        this.lastPageBtn = false;
      }
      // console.log('total data', res, Math.floor(this.totalData / this.itemPerPage));
    });
  }
  getWorkOrderList(): void {
    this.http
      .post<any>(this.listOfWorkOrderPanigationURL, this.body)
      .subscribe((res) => {
        this.listOfLenhSanXuat = res;
        // console.log('tesst 1: ', this.pageNumber, res);
        setTimeout(() => {
          this.listOfLenhSanXuat.forEach((item: any) => {
            item.tenTrangThai = "";
            if (item.trangThai === 0) {
              item.tenTrangThai = "Waitting";
            } else if (item.trangThai === 1) {
              item.tenTrangThai = "Running";
            } else if (item.trangThai === 2) {
              item.tenTrangThai = "Finish";
            } else if (item.trangThai === 3) {
              item.tenTrangThai = "Paused";
            }
            item.ng = item.ng === null ? 0 : item.ng;
            item.pass = item.pass === null ? 0 : item.pass;
          });
          // for (let i = 0; i < this.listOfLenhSanXuat.length; i++) {
          //   this.listOfLenhSanXuat[i].ng = this.listOfLenhSanXuat[i].ng === null ? 0 : this.listOfLenhSanXuat[i].ng;
          //   this.listOfLenhSanXuat[i].pass = this.listOfLenhSanXuat[i].pass === null ? 0 : this.listOfLenhSanXuat[i].pass;
          // }
          this.http
            .post<any>(this.totalItemURL, this.body)
            .subscribe((res1) => {
              // console.log('tongsoluong', res1);
              this.totalData = res1;
            });
        }, 300);
      });
  }
  ngOnInit(): void {
    this.getWorkOrderList();
    // this.http.get<any>(this.doiChieuLenhSanXuatUrl).subscribe(res => {
    //   res.forEach((item: { trangThai: string | number }) => {
    //     if (item.trangThai === 0) {
    //       item.trangThai = 'Waitting';
    //     } else if (item.trangThai === 1) {
    //       item.trangThai = 'Running';
    //     } else if (item.trangThai === 2) {
    //       item.trangThai = 'Finish';
    //     } else if (item.trangThai === 3) {
    //       item.trangThai = 'Paused';
    //     }
    //   });
    //   this.listOfLenhSanXuat = res;

    //   console.log('lsx', res);
    //   for (let i = 0; i < this.listOfLenhSanXuat.length; i++) {
    //     this.listOfLenhSanXuat[i].totalFail = 0;
    //     this.listOfLenhSanXuat[i].totalPass = 0;
    //     this.http.get<any>(`${this.tongHopURL}/${this.listOfLenhSanXuat[i].orderId as string}`).subscribe((res2: any[]) => {
    //       if (res2.length > 0) {
    //         for (let j = 0; j < res2.length; j++) {
    //           if (res2[j].result === 'NG') {
    //             this.listOfLenhSanXuat[i].totalFail = res2[j].recordValue;
    //           } else {
    //             this.listOfLenhSanXuat[i].totalPass = res2[j].recordValue;
    //           }
    //         }
    //       }
    //       // console.log('ng', this.listOfLenhSanXuat)
    //       // console.log('ng', res2)
    //     });
    // }
    // console.log('lsx', res);
    // });
  }

  openPopupChiTietThongTinScan(): void {
    this.popupChiTietThongTinScan = true;
  }

  closePopupChiTietThongTinScan(): void {
    this.popupChiTietThongTinScan = false;
  }
  getWorkOrderDetail(id: any, groupId: any): any {
    // console.log({ index: id, idgroup: groupId });
    sessionStorage.setItem("orderId", id);
    sessionStorage.setItem("groupId", groupId);
  }

  openPopupConfirmSave(): void {
    this.popupConfirmSave = true;
  }

  closePopupConfirmSave(): void {
    this.popupConfirmSave = false;
  }
}
