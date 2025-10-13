import { Component, Input, OnInit } from "@angular/core";
import { UntypedFormBuilder } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { ITEMS_PER_PAGE } from "app/config/pagination.constants";
import { DoiChieuLenhSanXuatComponent } from "./doi-chieu-lenh-san-xuat.component";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { HttpClient } from "@angular/common/http";
import { AccountService } from "app/core/auth/account.service";
import { GoogleChartInterface, GoogleChartType } from "ng2-google-charts";
import {
  ApexChart,
  ApexDataLabels,
  ApexNonAxisChartSeries,
  ApexTitleSubtitle,
} from "ng-apexcharts";
import { formatDate } from "@angular/common";
import * as XLSX from "xlsx";
import { PageEvent } from "@angular/material/paginator";
import { AlertService } from "app/core/util/alert.service";
@Component({
  selector: "jhi-scan-check",
  templateUrl: "./scan-check.component.html",
  styleUrls: ["./scan-check.component.scss"],
  standalone: false,
})
export class ScanCheckComponent implements OnInit {
  WorkOrderDetailUrl = this.applicationConfigService.getEndpointFor(
    "api/scan-work-order",
  );
  DetaiChecklUrl = this.applicationConfigService.getEndpointFor(
    "api/scan-work-order/detail",
  );
  userLoginlUrl = this.applicationConfigService.getEndpointFor(
    "api/user-login-history",
  );
  listOfMachineURL =
    this.applicationConfigService.getEndpointFor("api/scan-machines");
  tongHopURL = this.applicationConfigService.getEndpointFor("api/tong-hop");
  profileURL =
    this.applicationConfigService.getEndpointFor("api/profile-check");
  postUserLoginURL =
    this.applicationConfigService.getEndpointFor("api/login-history");
  updateWorkingURL = this.applicationConfigService.getEndpointFor(
    "api/work-order-working",
  );
  scanCheckExportUrl = this.applicationConfigService.getEndpointFor(
    "api/scan-check-export",
  );
  //
  chartSeries: ApexNonAxisChartSeries = [1, 0];

  chartDetails: ApexChart = {
    type: "pie",
    toolbar: {
      show: false,
    },
  };
  chartColors = ["#0000FF", "#F44336"];
  chartLabels = ["PASS", "NG"];
  //disablebtn
  disableStart = false;
  disablePause = true;
  disableFinish = false;
  chartTitle: ApexTitleSubtitle = {
    text: "Biểu đồ thống kê",
    align: "center",
  };

  chartDataLabels: ApexDataLabels = {
    enabled: true,
  };
  //Dữ liệu profilecheck tổng hợp
  listProfileCheck: any[] = [];
  //btn
  start = true;
  pause = true;
  predicate!: string;
  ascending!: boolean;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  page1?: number;
  page2?: number;

  title = "ScanSystem";
  result = "";
  dataWorkOrder: any[] = [{}];
  workOrder = "";
  checkValue = "";
  tieuChiKiemTra = "";
  checkName = "";
  scanValue = "";
  totalScans = 0;
  totalPass = 0;
  totalFail = 0;
  rateCompleted = "";
  runTime = 0;
  stopTime = 0;
  timer: any;
  elapsedTime = 64;
  stationName = "BG XK03";
  running = false;
  numberPlan = this.dataWorkOrder[0].sanLuong;
  timeRecord = "";
  scanHistory: {
    recordValue: string;
    status: string;
    stationName: string;
    machineId: number;
    recordName: string;
    result: string;
    position: number;
    username: string;
    orderId: number;
    timeRecorded: string;
    createAt: string;
  }[] = [];
  totalItems = 0;
  pageSize = 10;
  pageIndex = 0;
  public pieChart: GoogleChartInterface = {
    chartType: GoogleChartType.PieChart,
    dataTable: [
      ["Parameters", "Count"],
      ["PASS", 1],
      ["NG", 0],
    ],
    //firstRowIsData: true,
    options: {
      width: 650,
      height: 650,
      backgroundColor: "transparent",
      legend: {
        position: "right",
        alignment: "center",
        fontSize: "14px",
      },
    },
  };
  public lastTime = Date.now();
  public lastCmd = null;

  popupChiTietThongTinScan = false;
  popupConfirmSave = false;

  @Input() machineId = "";
  @Input() position = "";
  @Input() itemPerPage = 5;
  @Input() itemPerPage1 = 5;
  @Input() itemPerPage2 = 5;

  dataUser = [{ username: "", timeLogin: "" }];
  // acount
  account: any;
  // Thiet bi
  listOfMachines: any[] = [];
  infoCheckMachine: any[] = [];
  listDataScanCheck: any[] = [];

  dataExcel: {
    serial: string;
    numberProduct: string;
    tenThietBi: string;
    tenNhomThietBi: string;
    tieuChiKiemTra: string;
    noiDungDoiChieu: string;
    ketQuaCheck: string;
    ketLuan: string;
    viTri: string;
    nhanVien: string;
    thoiGianCheck: string;
  }[] = [
    {
      serial: "",
      numberProduct: "",
      tenThietBi: "",
      tenNhomThietBi: "",
      tieuChiKiemTra: "",
      noiDungDoiChieu: "",
      ketQuaCheck: "",
      ketLuan: "",
      viTri: "",
      nhanVien: "",
      thoiGianCheck: "",
    },
  ];

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
    protected formBuilder: UntypedFormBuilder,
    protected applicationConfigService: ApplicationConfigService,
    protected http: HttpClient,
    protected accountService: AccountService,
    protected alertService: AlertService,
  ) {}

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

  ngOnInit(): void {
    this.accountService.identity().subscribe((account) => {
      this.account = account;
      // console.log('acc', this.account);
    });
    const item = sessionStorage.getItem("orderId");
    this.http
      .get<any>(`${this.WorkOrderDetailUrl}/${item as string}`)
      .subscribe((res) => {
        this.dataWorkOrder[0] = res;
        console.log(this.dataWorkOrder);
        this.numberPlan = this.dataWorkOrder[0].sanLuong;
        if (this.dataWorkOrder[0].trangThai === 0) {
          this.dataWorkOrder[0].tenTrangThai = "Waiting";
        } else if (this.dataWorkOrder[0].trangThai === 1) {
          this.dataWorkOrder[0].tenTrangThai = "Running";
        } else if (this.dataWorkOrder[0].trangThai === 2) {
          this.dataWorkOrder[0].tenTrangThai = "Finish";
          this.disableStart = true;
        } else if (this.dataWorkOrder[0].trangThai === 3) {
          this.dataWorkOrder[0].tenTrangThai = "Pause";
        }
        this.elapsedTime = this.dataWorkOrder[0].runTime;
        // console.log('detail', this.dataWorkOrder[0]);
        this.http
          .get<any>(`${this.userLoginlUrl}/${item as string}`)
          .subscribe((res1) => {
            this.dataUser = res1;
            // console.log('login', res1);
          });
        this.http
          .get<any>(`${this.tongHopURL}/${item as string}`)
          .subscribe((res2: any[]) => {
            this.rateCompleted = "0.000";
            if (res2.length === 1) {
              if (res2[0].recordName === "NG") {
                this.totalFail = Number(res2[0].recordValue);
                this.totalPass = 0;
                this.totalScans = this.totalFail + this.totalPass;
                this.rateCompleted = (
                  (this.totalScans / Number(this.numberPlan)) *
                  100
                ).toFixed(3);
                this.chartSeries = [this.totalPass, this.totalFail];
              } else {
                this.totalFail = 0;
                this.totalPass = Number(res2[0].recordValue);
                this.totalScans = this.totalFail + this.totalPass;
                this.rateCompleted = (
                  (this.totalScans / Number(this.numberPlan)) *
                  100
                ).toFixed(3);
                this.chartSeries = [this.totalPass, this.totalFail];
              }
            } else {
              this.totalFail = Number(res2[0].recordValue);
              this.totalPass = Number(res2[1].recordValue);
              this.totalScans = this.totalFail + this.totalPass;
              this.rateCompleted = (
                (this.totalScans / Number(this.numberPlan)) *
                100
              ).toFixed(3);
              this.chartSeries = [this.totalPass, this.totalFail];
            }
            // console.log('tonghop', this.totalFail, this.totalPass, this.totalScans);
          });
        //lấy thông tin trong giai đoạn 1
        this.http
          .get<any>(
            `${this.profileURL}/${this.dataWorkOrder[0].productId as string}`,
          )
          .subscribe((res3) => {
            console.log("profile", res3);
            this.listProfileCheck = res3;
            this.stationName = res3[0].machineName;
            this.checkName = res3[0].recordName;
            this.checkValue = res3[0].recordValue;
            this.position = res3[0].position;
          });
      });
  }

  async onScan(): Promise<any> {
    // console.log({ ttscan: this.totalScans, number: this.numberPlan });
    if (this.scanValue.trim()) {
      this.totalScans++;
      const status = this.scanValue === this.checkValue ? "PASS" : "NG";
      this.scanHistory = [
        {
          recordValue: this.scanValue,
          status,
          stationName: this.stationName,
          recordName: this.checkName,
          result: status,
          position: 1,
          username: this.account.login,
          machineId: 2,
          orderId: this.dataWorkOrder[0].orderId,
          timeRecorded: this.timeRecord,
          createAt: formatDate(Date.now(), "yyyy-MM-dd HH:mm:ss", "en-US"),
        },
      ];

      this.rateCompleted = (
        (this.totalScans / Number(this.numberPlan)) *
        100
      ).toFixed(3);
      if (status === "PASS") {
        this.totalPass++;
        this.http.post<any>(this.DetaiChecklUrl, this.scanHistory).subscribe();
        // console.log('luu PASS thanh cong');
        this.scanValue = "";
      } else {
        await this.playAlertSound();
        // setTimeout(() => {
        //   alert(this.scanValue + 'không chính xác vui lòng kiểm tra lại!!!');
        // }, 0);
        this.totalFail++;
        this.warningNG(this.scanValue);
        this.http.post<any>(this.DetaiChecklUrl, this.scanHistory).subscribe();
        // console.log('luu NG thanh cong');
        this.scanValue = "";
      }
      this.chartSeries = [this.totalPass, this.totalFail];
      // this.pieChartDatasets = [
      //   {
      //     data: [this.totalFail, this.totalPass],
      //   },
      // ];

      // this.scanValue = ''; // Xóa dữ liệu trong ô input
      if (this.totalScans % 10 === 0) {
        this.scanHistory = [];
      }

      if (this.totalScans % 10 === 0) {
        // this.postData();
      }
    }
  }
  getFormattedElapsedTime(): any {
    const hours = Math.floor(this.elapsedTime / 3600);
    const minutes = Math.floor((this.elapsedTime % 3600) / 60);
    const seconds = this.elapsedTime % 60;
    this.timeRecord = `${this.pad(hours)}:${this.pad(minutes)}:${this.pad(seconds)}`;
    return this.timeRecord;
  }
  pad(num: number): string {
    return num.toString().padStart(2, "0");
  }
  warningNG(stringA: string): void {
    this.alertService.addAlert({
      type: "danger",
      message: `${stringA} không chính xác vui lòng kiểm tra lại!!!`,
      timeout: 5000,
      toast: true,
    });
  }
  playAlertSound(): any {
    return new Promise<void>((resolve) => {
      const audio = new Audio();
      audio.src = "../../../content/images/beep_warning.mp3";
      audio.load();
      audio.play();
      audio.onended = () => {
        resolve();
      };
    });
  }

  btnStart(): void {
    this.disableStart = true;
    this.disablePause = false;
    document.getElementById("scanCheck")?.focus();
    this.dataWorkOrder[0].tenTrangThai = "Running";
    this.dataWorkOrder[0].trangThai = 1;
    this.running = true;
    this.timer = setInterval(() => {
      this.elapsedTime++;
    }, 1000);
    const currentTime = formatDate(Date.now(), "yyyy-MM-dd HH:mm:ss", "en-US");
    const item = {
      username: this.account.login,
      timeLogin: currentTime,
      orderId: this.dataWorkOrder[0].orderId,
    };
    const working = {
      working: this.dataWorkOrder[0].trangThai,
      runTime: this.elapsedTime,
      orderId: this.dataWorkOrder[0].orderId,
    };
    this.dataUser.push({
      username: this.account.login,
      timeLogin: currentTime,
    });
    this.http.put<any>(this.postUserLoginURL, item).subscribe();
    this.http.put<any>(this.updateWorkingURL, working).subscribe();
  }

  btnPause(): void {
    this.disablePause = true;
    this.disableStart = false;
    this.dataWorkOrder[0].tenTrangThai = "Pause";
    this.dataWorkOrder[0].trangThai = 3;
    this.running = false;
    clearInterval(this.timer);
    console.log(this.elapsedTime);
    const working = {
      working: this.dataWorkOrder[0].trangThai,
      runTime: this.elapsedTime,
      orderId: this.dataWorkOrder[0].orderId,
    };
    this.http.put<any>(this.updateWorkingURL, working).subscribe();
  }

  btnFinish(): void {
    this.disableFinish = true;
    this.disableStart = true;
    this.dataWorkOrder[0].tenTrangThai = "Finish";
    this.dataWorkOrder[0].trangThai = 2;
    this.running = false;
    clearInterval(this.timer);
    const working = {
      working: this.dataWorkOrder[0].trangThai,
      runTime: this.elapsedTime,
      orderId: this.dataWorkOrder[0].orderId,
    };
    this.http.put<any>(this.updateWorkingURL, working).subscribe();
  }
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadPage();
  }

  searchChiTiet1(): void {
    this.listProfileCheck = this.listProfileCheck.filter((item) =>
      item.position.includes(this.position),
    );
  }

  searchChiTiet2(): void {
    this.infoCheckMachine = this.infoCheckMachine.filter(
      (item2) =>
        item2.machineId.includes(this.machineId) &&
        item2.tieuChiKiemTra.includes(this.tieuChiKiemTra),
    );
  }

  openPopupChiTietThongTinScan(): void {
    const groupId = sessionStorage.getItem("groupId");
    this.http
      .get<any>(`${this.listOfMachineURL}/${groupId as string}`)
      .subscribe((res) => {
        this.listOfMachines = res;
        console.log("machinessss:", res);
      });
    const item = sessionStorage.getItem("orderId");

    this.http
      .get<any>(`${this.scanCheckExportUrl}/${item as string}`)
      .subscribe((res2) => {
        this.infoCheckMachine = res2;
        console.log("tt chi tiet", res2);
      });
    this.popupChiTietThongTinScan = true;
  }

  closePopupChiTietThongTinScan(): void {
    this.popupChiTietThongTinScan = false;
  }

  previousState(): void {
    window.history.back();
  }

  openPopupConfirmSave(): void {
    this.popupConfirmSave = true;
  }

  closePopupConfirmSave(): void {
    this.popupConfirmSave = false;
  }

  getDataExport(): void {
    const item = sessionStorage.getItem("orderId");
    this.http
      .get<any>(`${this.scanCheckExportUrl}/${item as string}`)
      .subscribe((resExcel) => {
        // this.listDataScanCheck = resExcel;
        // console.log('res data', this.listDataScanCheck);
        // this.dataExcel = resExcel;
        setTimeout(() => {
          for (let i = 0; i < resExcel.length; i++) {
            const dataArrage = {
              serial: resExcel[i].serial,
              numberProduct: resExcel[i].numberProduct,
              tenThietBi: resExcel[i].tenThietBi,
              tenNhomThietBi: resExcel[i].tenNhomThietBi,
              tieuChiKiemTra: resExcel[i].tieuChiKiemTra,
              noiDungDoiChieu: resExcel[i].noiDungDoiChieu,
              ketQuaCheck: resExcel[i].ketQuaCheck,
              ketLuan: resExcel[i].ketLuan,
              viTri: resExcel[i].viTri,
              nhanVien: resExcel[i].nhanVien,
              thoiGianCheck: resExcel[i].thoiGianCheck,
            };
            this.dataExcel.push(dataArrage);
          }
          this.exportToExcel();
          console.log("res data 2", this.dataExcel);
        }, 1000);
      });
  }

  exportToExcel(): void {
    // this.getDataExport()
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet([]);
    XLSX.utils.sheet_add_json(ws, this.dataExcel, {
      origin: "A5",
      skipHeader: true,
    });
    const mergeRange = [
      { s: { r: 0, c: 0 }, e: { r: 0, c: 0 } },
      { s: { r: 1, c: 0 }, e: { r: 1, c: 0 } },
      { s: { r: 2, c: 0 }, e: { r: 2, c: 0 } },
      { s: { r: 0, c: 2 }, e: { r: 0, c: 2 } },
      { s: { r: 1, c: 2 }, e: { r: 1, c: 2 } },
      { s: { r: 2, c: 2 }, e: { r: 2, c: 2 } },
      { s: { r: 0, c: 4 }, e: { r: 0, c: 4 } },
      { s: { r: 1, c: 4 }, e: { r: 1, c: 4 } },
      { s: { r: 2, c: 4 }, e: { r: 2, c: 4 } },
      { s: { r: 4, c: 0 }, e: { r: 4, c: 0 } },
      { s: { r: 4, c: 1 }, e: { r: 4, c: 1 } },
      { s: { r: 4, c: 2 }, e: { r: 4, c: 2 } },
      { s: { r: 4, c: 3 }, e: { r: 4, c: 3 } },
      { s: { r: 4, c: 4 }, e: { r: 4, c: 4 } },
      { s: { r: 4, c: 5 }, e: { r: 4, c: 5 } },
      { s: { r: 4, c: 6 }, e: { r: 4, c: 6 } },
      { s: { r: 4, c: 7 }, e: { r: 4, c: 7 } },
      { s: { r: 4, c: 8 }, e: { r: 4, c: 8 } },
      { s: { r: 4, c: 9 }, e: { r: 4, c: 9 } },
      { s: { r: 4, c: 10 }, e: { r: 4, c: 10 } },
    ];
    ws["!merges"] = mergeRange;
    ws["A1"] = { t: "s", v: "Planning - WO" };
    ws["A2"] = { t: "s", v: "Product_code" };
    ws["A3"] = { t: "s", v: "Running_Time" };
    ws["B1"] = { t: "s", v: this.dataWorkOrder[0].workOrder };
    ws["B2"] = { t: "s", v: this.dataWorkOrder[0].productCode };
    ws["B3"] = { t: "s", v: this.getFormattedElapsedTime() };
    ws["C1"] = { t: "s", v: "LOT" };
    ws["C2"] = { t: "s", v: "Product_Name" };
    ws["C3"] = { t: "s", v: "Stop_Time" };
    ws["D1"] = { t: "s", v: this.dataWorkOrder[0].lot };
    ws["D2"] = { t: "s", v: this.dataWorkOrder[0].productName };
    ws["D3"] = { t: "s", v: this.getFormattedElapsedTime() };
    ws["E1"] = { t: "s", v: "San luong KH" };
    ws["E2"] = { t: "s", v: "Start_Time" };
    ws["E3"] = { t: "s", v: "Status" };
    ws["F1"] = { t: "s", v: this.dataWorkOrder[0].sanLuong };
    ws["F2"] = { t: "s", v: this.getFormattedElapsedTime() };
    ws["F3"] = { t: "s", v: this.dataWorkOrder[0].tenTrangThai };
    ws["G1"] = { t: "s", v: "Version" };
    ws["H1"] = { t: "s", v: this.dataWorkOrder[0].version };
    ws["A5"] = { t: "s", v: "Serial" };
    ws["B5"] = { t: "s", v: "Number_Product" };
    ws["C5"] = { t: "s", v: "Tên thiết bị" };
    ws["D5"] = { t: "s", v: "Tên nhóm thiết bị" };
    ws["E5"] = { t: "s", v: "Tiêu chí kiểm tra" };
    ws["F5"] = { t: "s", v: "Nội dung đối chiếu" };
    ws["G5"] = { t: "s", v: "Kết quả check" };
    ws["H5"] = { t: "s", v: "Kết luận" };
    ws["I5"] = { t: "s", v: "Vị trí" };
    ws["J5"] = { t: "s", v: "Nhân viên" };
    ws["K5"] = { t: "s", v: "Thời gian check" };

    const headerStyle = {
      font: { bold: true },
      aligment: { horizontal: "center", vertical: "center" },
    };

    const headers = [
      "A1",
      "A2",
      "A3",
      "C1",
      "C2",
      "C3",
      "E1",
      "E2",
      "E3",
      "G1",
      "A5",
      "B5",
      "C5",
      "D5",
      "E5",
      "F5",
      "G5",
      "H5",
      "I5",
      "J5",
      "K5",
    ];
    headers.forEach((header) => {
      ws[header].s = headerStyle;
    });

    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Báo cáo ản xuất");
    XLSX.writeFile(wb, "Bao-cao-thong-tin-giam-sat-san-xuat.xlsx");
  }
}
