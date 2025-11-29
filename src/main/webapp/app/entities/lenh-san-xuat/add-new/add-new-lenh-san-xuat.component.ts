import { HttpClient } from "@angular/common/http";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { IChiTietLenhSanXuat } from "app/entities/chi-tiet-lenh-san-xuat/chi-tiet-lenh-san-xuat.model";
import { Component, OnInit, ViewChild, ElementRef, Input } from "@angular/core";
import { ActivatedRoute, Router, Routes } from "@angular/router";
import * as XLSX from "xlsx";
import { ILenhSanXuat } from "../lenh-san-xuat.model";
import { ngxCsv } from "ngx-csv/ngx-csv";
import { PageEvent } from "@angular/material/paginator";
import { MatTabGroup, MatTab } from "@angular/material/tabs";
import { DataSource } from "@angular/cdk/collections";
import {
  CreateDialogComponent,
  DialogData,
  DialogResult,
  BoxFormData,
  PalletFormData,
} from "../create-dialog/create-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import {
  PalletDetailDialogComponent,
  PalletDetailData,
  MultiPalletDialogData,
} from "../detail-pallet-dialog/detail-pallet-dialog.component";
import {
  BoxDetailData,
  DetailBoxDialogComponent,
} from "../detail-box-dialog/detail-box-dialog.component";
import { PackagePalletDialogComponent } from "../pakage-pallet-dialog/package-pallet-dialog.component";
import {
  PlanningWorkOrderService,
  PlanningWorkOrder,
  WarehouseNotePayload,
  WorkshopHierarchy,
} from "../service/planning-work-order.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AccountService } from "app/core/auth/account.service";
import { catchError, of, timeout } from "rxjs";

interface ProductionOrder {
  maLenhSanXuat: string;
  maSAP: string;
  tenHangHoa: string;
  maWO: string;
  version: string;
  maKhoNhap: string;
  nganhRaw?: string; // branchCode từ search (raw)
  toRaw?: string; // groupCode từ search (raw)
  // các mã sau khi map từ hierarchy
  xuong?: string; // workshop_code (VD: '01')
  nganh?: string; // branch_code (VD: 'DTTD')
  to?: string;
  tongSoLuong: number;
  trangThai: string;
  loaiSanPham: string;
  woId?: string;
}

export interface WarehouseNoteInfo {
  id: number;
  ma_lenh_san_xuat: string;
  sap_code: string;
  sap_name: string;
  work_order_code: string;
  version: string;
  storage_code: string;
  total_quantity: number;
  create_by: string;
  entry_time: string;
  trang_thai: string;
  group_name: string;
  branch: string;
  product_type: string;
}
export interface ReelData {
  reelID: string;
  partNumber: string;
  vendor: string;
  lot: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  initialQuantity: number;
  msdLevel: string;
  msdInitialFloorTime: string;
  msdBagSealDate: string;
  marketUsage: string;
  quantityOverride: string;
  shelfTime: string;
  spMaterialName: string;
  warningLimit: string;
  maximumLimit: string;
  comments: string;
  warmupTime: string;
  storageUnit: string;
  subStorageUnit: string;
  locationOverride: string;
  expirationDate: string;
  manufacturingDate: string;
  partClass: string;
  sapCode: string;
  trangThai: string;
}
interface BoxItem {
  stt: number;
  maSanPham: string;
  lotNumber: string;
  vendor: string;
  version: string;
  tenSanPham: string;
  soLuongThung: number;
  soLuongSp: number;
  soLuongTrongThung: number;
  ghiChu: string;
  kho: string;
  trangThaiIn: boolean;
  serialBox: string;
  subItems: BoxSubItem[];
}
export interface BoxSubItem {
  stt: number;
  maThung: string;
  soLuong: number;
}
export interface PalletItem {
  stt: number;
  tenSanPham: string;
  noSKU: string;
  createdAt: string;
  tongPallet: number;
  tongSlSp: number;
  tongSoThung: number;
  khachHang: string;
  poNumber: string;
  dateCode: string;
  qdsx: string;
  nguoiKiemTra: string;
  ketQuaKiemTra: string;
  trangThaiIn: boolean;
  serialPallet: string;
  subItems: PalletBoxItem[];
  tienDoScan?: number;
}
export interface PalletBoxItem {
  stt: number;
  maPallet: string;
  tongSoThung: number;
  qrCode?: string;
  tienDoScan?: number;
  sucChua?: string;
}
export interface WarehouseNoteResponse {
  warehouse_note_info: WarehouseNoteInfo;
  warehouse_note_info_details: any[];
  serial_box_pallet_mappings: any[];
  pallet_infor_details: any[];
}
@Component({
  selector: "jhi-lenh-san-xuat-detail",
  templateUrl: "./add-new-lenh-san-xuat.component.html",
  styleUrls: ["./add-new-lenh-san-xuat.component.scss"],
  standalone: false,
})
export class AddNewLenhSanXuatComponent implements OnInit {
  woId = "";
  maLenhSanXuatId?: number;

  displayedColumns: string[] = [
    "maLenhSanXuat",
    "maSAP",
    "tenHangHoa",
    "maWO",
    "version",
    "maKhoNhap",
    "nganh",
    "to",
    "tongSoLuong",
    "trangThai",
    "loaiSanPham",
  ];

  boxColumns: string[] = [
    "stt",
    "maSanPham",
    "tenSanPham",
    "lotNumber",
    "vendor",
    "soLuongThung",
    "soLuongSp",
    "ghiChu",
    "kho",
    "trangThaiIn",
    "tuyChon",
  ];

  palletColumns: string[] = [
    "stt",
    "tenSanPham",
    "noSKU",
    "createdAt",
    "tongPallet",
    "tongSlSp",
    "tongSoThung",
    "khachHang",
    "poNumber",
    "dateCode",
    "qdsx",
    "nguoiKiemTra",
    "ketQuaKiemTra",
    "trangThaiIn",
    "tuyChon",
  ];
  //tạo tem
  reelColumns: string[] = [
    "stt",
    "reelID",
    "partNumber",
    "vendor",
    "lot",
    "userData1",
    "userData2",
    "userData3",
    "userData4",
    "userData5",
    "initialQuantity",
    "msdLevel",
    "msdInitialFloorTime",
    "msdBagSealDate",
    "marketUsage",
    "quantityOverride",
    "shelfTime",
    "spMaterialName",
    "warningLimit",
    "maximumLimit",
    "comments",
    "warmupTime",
    "storageUnit",
    "subStorageUnit",
    "locationOverride",
    "expirationDate",
    "manufacturingDate",
    "partClass",
    "sapCode",
    "trangThai",
  ];

  // Data source
  productionOrders: ProductionOrder[] = [];

  boxItems: BoxItem[] = [];

  palletItems: PalletItem[] = [];

  reelDataList: ReelData[] = [];

  selectedBranch = "Led1";
  boxQuantity = 1;
  quantityPerBox = 200;

  //biến tab
  selectedTabIndex = 0;
  fabMenuOpen = false;

  //form
  form = this.fb.group({
    woId: ["", Validators.required],
  });
  storageUnitHeaderValue = "";
  //loader
  loading = false;
  loadingBox = false;

  //mobile
  isMobile = false;

  //flag tab
  showThungTab = false;
  showTemBtpTab = false;
  showPalletTab = false;
  lenhSanXuat: any;
  isThanhPham: boolean = false;

  warehouseNoteInfo!: WarehouseNoteInfo; // hoặc interface cụ thể
  details: any[] = [];
  mappings: any[] = [];
  pallets: any[] = [];
  //log
  constructor(
    private dialog: MatDialog,
    private planningService: PlanningWorkOrderService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private accountService: AccountService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.selectedTabIndex = 0;
    this.isMobile = window.innerWidth < 768;

    const id = this.route.snapshot.params["id"];
    if (id) {
      this.route.data.subscribe((data) => {
        const response = data["lenhSanXuat"] as WarehouseNoteResponse;
        if (response) {
          this.warehouseNoteInfo = response.warehouse_note_info;
          this.details = (response.warehouse_note_info_details ??
            []) as ReelData[];
          this.mappings = response.serial_box_pallet_mappings ?? [];
          this.pallets = (response.pallet_infor_details ?? []) as PalletItem[];

          this.maLenhSanXuatId = this.warehouseNoteInfo.id;

          this.productionOrders = [
            {
              maLenhSanXuat: this.warehouseNoteInfo.ma_lenh_san_xuat,
              maSAP: this.warehouseNoteInfo.sap_code,
              tenHangHoa: this.warehouseNoteInfo.sap_name,
              maWO: this.warehouseNoteInfo.work_order_code,
              version: this.warehouseNoteInfo.version,
              maKhoNhap: this.warehouseNoteInfo.storage_code,
              tongSoLuong: this.warehouseNoteInfo.total_quantity,
              trangThai: this.warehouseNoteInfo.trang_thai,
              loaiSanPham: this.warehouseNoteInfo.product_type,
              nganh: this.warehouseNoteInfo.branch,
              to: this.warehouseNoteInfo.group_name,
              woId: this.warehouseNoteInfo.work_order_code,
            },
          ];
          this.isThanhPham =
            this.productionOrders[0]?.loaiSanPham === "Thành phẩm";

          if (this.details && this.details.length > 0) {
            if (this.warehouseNoteInfo.product_type === "Thành phẩm") {
              this.boxItems = this.mapDetailsToBoxItems(this.details);
            } else if (
              this.warehouseNoteInfo.product_type === "Bán thành phẩm"
            ) {
              this.reelDataList = this.mapDetailsToReelData(this.details);
            }
          }

          if (this.pallets && this.pallets.length > 0) {
            this.palletItems = this.mapPalletsToPalletItems(this.pallets);
          }

          const loai = this.productionOrders[0]?.loaiSanPham;
          if (loai === "Thành phẩm") {
            this.showThungTab = true;
            this.showPalletTab = true;
            this.showTemBtpTab = false;
          } else if (loai === "Bán thành phẩm") {
            this.showTemBtpTab = true;
            this.showPalletTab = true;
            this.showThungTab = false;
          }
        }
      });
    } else {
      this.productionOrders = [];
    }

    window.addEventListener("resize", () => {
      this.isMobile = window.innerWidth < 768;
    });
  }

  //gui phe duyet
  onApprove(): void {
    if (this.isThanhPham) {
      // Xử lý cho Thành phẩmD
      this.sendWmsApproval();
    } else {
      // xử lý cho Bán thành phẩm
      this.sendApproval();
    }
  }

  //get data
  onApply(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const { woId } = this.form.value;

    this.planningService
      .search(woId!)
      .pipe(
        timeout(10000), // ⏱ nếu quá 10s thì coi như lỗi
        catchError((err) => {
          console.error("Lỗi hoặc timeout:", err);
          this.snackBar.open("Không tìm thấy thông tin đơn sản xuất", "Đóng", {
            duration: 3000,
          });
          this.loading = false;
          return of({ content: [] }); // fallback dữ liệu rỗng
        }),
      )
      .subscribe({
        next: (res) => {
          const filtered = res.content.filter((item) => item.woId === woId);

          if (filtered.length === 0) {
            this.snackBar.open(
              "Không tìm thấy thông tin đơn sản xuất",
              "Đóng",
              { duration: 3000 },
            );
          }

          this.productionOrders = filtered.map((item) => ({
            maLenhSanXuat: item.sapWoId,
            maSAP: item.productCode,
            tenHangHoa: item.productName,
            maWO: item.woId,
            version: item.bomVersion,
            maKhoNhap: "",
            tongSoLuong: 0,
            trangThai: "Bản nháp",
            loaiSanPham:
              item.productType === "1" ? "Thành phẩm" : "Bán thành phẩm",
            nganhRaw: item.branchCode, // dữ liệu thô
            toRaw: item.groupCode,
          }));
          this.planningService.getHierarchy().subscribe({
            next: (workshops) => {
              this.productionOrders = this.productionOrders.map((order) => {
                const mapped = this.resolveCodesFromHierarchy(
                  workshops,
                  order.nganhRaw,
                  order.toRaw,
                );
                return { ...order, ...mapped };
              });
            },
            error: (err) => {
              console.error("Lỗi lấy hierarchy:", err);
              this.snackBar.open(
                "Không lấy được thông tin xưởng/ngành/tổ",
                "Đóng",
                { duration: 3000 },
              );
            },
          });
          const loai = this.productionOrders[0]?.loaiSanPham;
          if (loai === "Thành phẩm") {
            this.showThungTab = true;
            this.showPalletTab = true;
            this.showTemBtpTab = false;
          } else if (loai === "Bán thành phẩm") {
            this.showTemBtpTab = true;
            this.showPalletTab = true;
            this.showThungTab = false;
          }
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  toggleFabMenu(): void {
    this.fabMenuOpen = !this.fabMenuOpen;
  }
  printAll(): void {
    // Your logic
  }
  onCreateLabels(): void {
    this.generateReelData();
  }

  //chuyển tab button
  goToBoxTab(): void {
    this.selectedTabIndex = 0;
  }

  goToPalletTab(): void {
    this.selectedTabIndex = 1;
  }

  goToCreateTemTab(): void {
    this.selectedTabIndex = 2;
  }

  //xuất excel
  exportReelToExcel(): void {
    if (this.reelDataList.length === 0) {
      this.snackBar.open("Chưa có dữ liệu reel để xuất!", "Đóng", {
        duration: 3000,
        horizontalPosition: "center",
        verticalPosition: "bottom",
      });
      return;
    }

    const worksheet = XLSX.utils.json_to_sheet(this.reelDataList);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Reel Data");

    const fileName = `Reel_${new Date().getTime()}.xlsx`;
    XLSX.writeFile(workbook, fileName);
  }

  //nhap kho
  applyStorageUnitToAll(): void {
    this.reelDataList = this.reelDataList.map((item) => ({
      ...item,
      storageUnit: this.storageUnitHeaderValue,
    }));
  }

  // Mở dialog tạo thùng sản phẩm
  openCreateBoxDialog(): void {
    const firstOrder = this.productionOrders[0];
    if (!firstOrder) {
      console.warn("Không có productionOrder nào để tạo thùng/tem");
      return;
    }

    const dialogData: DialogData = {
      type: "box",
      maSanPham: firstOrder.tenHangHoa,
      woId: firstOrder.woId,
      loaiSanPham: firstOrder.loaiSanPham, //  truyền loại sản phẩm
    };

    const dialogRef = this.dialog.open(CreateDialogComponent, {
      width: "750px",
      maxWidth: "100vw",
      data: dialogData,
      disableClose: false,
      autoFocus: true,
    });

    dialogRef.afterClosed().subscribe((result: DialogResult) => {
      if (result && result.type === "box") {
        const boxData = result.data as BoxFormData;

        //  check loại sản phẩm để gọi đúng hàm
        if (dialogData.loaiSanPham === "Thành phẩm") {
          this.handleBoxCreation(boxData, dialogData.woId!);
        } else if (dialogData.loaiSanPham === "Bán thành phẩm") {
          this.handleTemBTPCreation(boxData, dialogData.woId!);
        }
      }
    });
  }

  // Mở dialog tạo pallet
  openCreatePalletDialog(): void {
    const dialogData: DialogData = {
      type: "pallet",
      tenSanPham: this.productionOrders[0]?.tenHangHoa,
    };

    const dialogRef = this.dialog.open(CreateDialogComponent, {
      width: "750px",
      maxWidth: "100vw",
      data: dialogData,
      disableClose: false,
      autoFocus: true,
    });

    dialogRef.afterClosed().subscribe((result: DialogResult) => {
      if (result && result.type === "pallet") {
        const palletData = result.data as PalletFormData;
        this.handlePalletCreation(palletData);
      }
    });
  }
  //detail pallet
  openPalletDetailDialog(pallet: PalletItem): void {
    const dialogData: MultiPalletDialogData = {
      mode: "single",
      singleData: {
        stt: pallet.stt,
        tenSanPham: pallet.tenSanPham,
        noSKU: pallet.noSKU,
        createdAt: pallet.createdAt,
        tongPallet: pallet.tongPallet,
        tongSlSp: pallet.tongSlSp,
        tongSoThung: pallet.tongSoThung,
        serialPallet: pallet.serialPallet,
        //  THÊM CÁC FIELD CẦN CHO PRINT
        khachHang: pallet.khachHang,
        poNumber: pallet.poNumber,
        dateCode: pallet.dateCode,
        qdsx: pallet.qdsx,
        nguoiKiemTra: pallet.nguoiKiemTra,
        ketQuaKiemTra: pallet.ketQuaKiemTra,
        subItems: pallet.subItems || [],
      },
    };

    const dialogRef = this.dialog.open(PalletDetailDialogComponent, {
      width: "1200px",
      maxWidth: "100vw",
      maxHeight: "90vh",
      data: dialogData,
      disableClose: false,
      autoFocus: false,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log("Dialog đã đóng với kết quả:", result);
      }
    });
  }

  // Method mở dialog chi tiết thùng
  openBoxDetailDialog(box: BoxItem): void {
    const dialogData: BoxDetailData = {
      stt: box.stt,
      maSanPham: box.maSanPham,
      lotNumber: box.lotNumber,
      vendor: box.vendor,
      tenSanPham: box.tenSanPham,
      soLuongThung: box.soLuongThung,
      soLuongSp: box.soLuongSp,
      soLuongTrongThung: box.soLuongTrongThung,
      kho: box.kho,
      serialBox: box.serialBox,
      subItems: box.subItems || [], //  THÊM DÒNG NÀY
    };

    const dialogRef = this.dialog.open(DetailBoxDialogComponent, {
      width: "900px",
      maxWidth: "100vw",
      maxHeight: "90vh",
      data: dialogData,
      disableClose: false,
      autoFocus: false,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log("Dialog đã đóng với kết quả:", result);
      }
    });
  }

  openPackagePalletDialog(): void {
    // Flatten tất cả pallet con từ palletItems
    const allPallets: PalletDetailData[] = this.palletItems.reduce(
      (acc: PalletDetailData[], pallet: PalletItem) => {
        const subData = pallet.subItems.map(
          (sub: PalletBoxItem, index: number) => ({
            stt: index + 1,
            tenSanPham: pallet.tenSanPham,
            noSKU: pallet.noSKU,
            createdAt: pallet.createdAt,
            tongPallet: 1,
            tongSlSp: sub.tongSoThung ?? 0,
            tongSoThung: sub.tongSoThung ?? 0,
            serialPallet: sub.maPallet,
            khachHang: pallet.khachHang,
            poNumber: pallet.poNumber,
            dateCode: pallet.dateCode,
            qdsx: pallet.qdsx,
            nguoiKiemTra: pallet.nguoiKiemTra,
            ketQuaKiemTra: pallet.ketQuaKiemTra,
          }),
        );
        return acc.concat(subData);
      },
      [],
    );

    const dialogData: MultiPalletDialogData = {
      mode: "multiple",
      multipleData: allPallets,
    };

    const dialogRef = this.dialog.open(PalletDetailDialogComponent, {
      width: "1200px",
      maxWidth: "100vw",
      maxHeight: "900px",
      data: dialogData,
      disableClose: true,
      autoFocus: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log("Package completed:", result);
        this.handlePackageComplete(result);
      }
    });
  }

  //lay thong tin tao bang tao tem
  generateReelData(): void {
    if (this.productionOrders.length === 0) {
      this.snackBar.open("Chưa có thông tin lệnh sản xuất!", "Đóng", {
        duration: 3000,
        horizontalPosition: "center",
        verticalPosition: "bottom",
      });
      return;
    }

    if (this.boxItems.length === 0) {
      this.snackBar.open("Chưa có thông tin thùng!", "Đóng", {
        duration: 3000,
        horizontalPosition: "center",
        verticalPosition: "bottom",
      });
      return;
    }

    this.reelDataList = [];
    const now = new Date();
    let counter = 1;

    // Lấy thông tin từ production order
    const productionOrder = this.productionOrders[0];

    // Lặp qua từng box để tạo reel data
    this.boxItems.forEach((box) => {
      // Tạo số lượng reel dựa vào soLuongThung
      for (let i = 0; i < box.soLuongThung; i++) {
        const reelID = this.generateReelID(now, productionOrder.maSAP, counter);

        const reelData: ReelData = {
          // Dữ liệu có từ 2 bảng
          reelID: reelID,
          partNumber: ``,
          vendor: box.vendor || "",
          lot: box.lotNumber || "",
          userData5: productionOrder.maLenhSanXuat || "",
          initialQuantity: box.soLuongTrongThung || 0,
          sapCode: productionOrder.maSAP || "",
          trangThai: "Active",
          storageUnit: box.kho || "",
          comments: box.ghiChu || "",

          // Dữ liệu để trống (chưa có từ 2 bảng)
          userData1: box.soLuongSp.toString() || "0",
          userData2: "",
          userData3: box.tenSanPham || "",
          userData4: productionOrder.maSAP || "",
          msdLevel: "1",
          msdInitialFloorTime: "",
          spMaterialName: "",
          msdBagSealDate: "",
          marketUsage: "",
          quantityOverride: "",
          shelfTime: "",
          warningLimit: "",
          maximumLimit: "",
          warmupTime: "",
          subStorageUnit: "",
          locationOverride: "",
          expirationDate: (() => {
            const d = new Date();
            return `${d.getFullYear() + 2}${String(d.getMonth() + 1).padStart(2, "0")}${String(d.getDate()).padStart(2, "0")}`;
          })(),
          manufacturingDate: new Date()
            .toISOString()
            .split("T")[0]
            .replace(/-/g, ""),
          partClass: productionOrder.loaiSanPham || "",
        };

        this.reelDataList.push(reelData);
        counter++;
      }
    });

    console.log(`Đã tạo ${this.reelDataList.length} reel items`);
    this.goToCreateTemTab();
  }

  //xoa trong bang box
  deleteBox(index: number): void {
    // Xoá  index
    this.boxItems = this.boxItems.filter((_, i) => i !== index);

    //cập nhật lại STT
    this.boxItems = this.boxItems.map((box, idx) => ({
      ...box,
      stt: idx + 1,
    }));

    // Cập nhật lại tổng số lượng ở bảng 1
    this.updateProductionOrderTotal();

    console.log(`Đã xoá thùng tại index ${index}`);
  }

  //lưu bảng 1
  saveWarehouseNotes(): void {
    const currentUser = this.accountService.isAuthenticated()
      ? this.accountService["userIdentity"]?.login
      : "unknown";

    this.productionOrders.forEach((order) => {
      const payload: WarehouseNotePayload = {
        ma_lenh_san_xuat: order.maLenhSanXuat,
        sap_code: order.maSAP,
        sap_name: order.tenHangHoa,
        work_order_code: order.maWO,
        version: order.version,
        storage_code: order.maKhoNhap,
        total_quantity: order.tongSoLuong,
        create_by: currentUser ?? "",
        trang_thai: order.trangThai,
        group_name: order.nganh,
        comment_2: "",
        approver_by: "",
        branch: order.to,
        product_type: order.loaiSanPham,
        destination_warehouse: 1,
      };

      this.planningService.createWarehouseNote(payload).subscribe({
        next: (res) => {
          const newId = res.id; // lấy id từ response
          this.snackBar.open(`Lưu thành công!`, "Đóng", {
            duration: 3000,
            panelClass: ["snackbar-success"],
          });

          // this.loadProductionOrders();

          this.router.navigate([`/lenh-san-xuat/${newId}/add-new`]);
        },
        error: (err) => {
          console.error("Lỗi khi lưu:", err);
          this.snackBar.open(`Lưu thất bại!`, "Đóng", {
            duration: 4000,
            panelClass: ["snackbar-error"],
          });
        },
      });
    });
  }

  //lưu dữ liệu box và pallet
  saveCombined(maLenhSanXuatId: number): void {
    const currentUser = this.accountService.isAuthenticated()
      ? this.accountService["userIdentity"]?.login
      : "unknown";
    const version = this.productionOrders[0]?.version ?? "1.0";
    const loaiSanPham = this.productionOrders[0]?.loaiSanPham;

    // Tạo Set serial đã tồn tại
    const existingPalletSerials: Set<string> = new Set<string>();
    for (let i = 0; i < (this.pallets?.length ?? 0); i++) {
      const serial = this.getPalletSerialSafe(this.pallets![i]);
      if (serial) {
        existingPalletSerials.add(serial);
      }
    }

    const existingBoxSerials: Set<string> = new Set<string>();
    for (let i = 0; i < (this.details?.length ?? 0); i++) {
      const serial = this.getBoxSerialSafe(this.details![i]);
      if (serial) {
        existingBoxSerials.add(serial);
      }
    }

    // Pallet payload
    const pallet_infor_detail: any[] = [];
    for (const p of this.palletItems) {
      for (const sub of p.subItems) {
        if (!existingPalletSerials.has(sub.maPallet)) {
          pallet_infor_detail.push({
            id: null,
            serial_pallet: sub.maPallet,
            ma_lenh_san_xuat_id: maLenhSanXuatId,
            customer_name: p.khachHang ?? null,
            po_number: p.poNumber ?? null,
            date_code: p.dateCode ?? null,
            item_no_sku: p.noSKU ?? null,
            qdsx_no: p.qdsx ?? null,
            production_date: p.createdAt ?? null,
            inspector_name: p.nguoiKiemTra ?? null,
            inspection_result: p.ketQuaKiemTra ?? null,
            scan_progress: sub.tienDoScan ?? 0,
            num_box_actual: sub.tongSoThung,
            updated_at: new Date().toISOString(),
            updated_by: currentUser,
          });
        }
      }
    }

    // Box/Tem payload
    let warehouse_note_info_detail: any[] = [];

    if (loaiSanPham === "Thành phẩm") {
      // Thành phẩm: flatten boxItems
      for (const b of this.boxItems) {
        if (!b.subItems || b.subItems.length === 0) {
          if (!existingBoxSerials.has(b.serialBox)) {
            warehouse_note_info_detail.push({
              id: null,
              reel_id: b.serialBox,
              part_number: `${b.maSanPham}V_${version}`,
              vendor: b.vendor,
              lot: b.lotNumber,
              user_data1: "",
              user_data2: "",
              user_data3: "",
              user_data4: "",
              user_data5: "",
              initial_quantity: b.soLuongSp,
              msd_level: "1",
              msd_initial_floor_time: "",
              msd_bag_seal_date: new Date().toISOString().split("T")[0],
              sp_material_name: b.tenSanPham,
              comments: b.ghiChu,
              storage_unit: "RD",
              sap_code: b.maSanPham,
              ma_lenh_san_xuat_id: maLenhSanXuatId,
              trang_thai: "ACTIVE",
              checked: 1,
            });
          }
          continue;
        }

        for (const sub of b.subItems) {
          if (!existingBoxSerials.has(sub.maThung)) {
            warehouse_note_info_detail.push({
              id: null,
              reel_id: sub.maThung,
              part_number: `${b.maSanPham}V_${version}`,
              vendor: b.vendor,
              lot: b.lotNumber,
              initial_quantity: sub.soLuong,
              msd_level: "1",
              msd_bag_seal_date: new Date().toISOString().split("T")[0],
              sp_material_name: b.tenSanPham,
              comments: b.ghiChu,
              storage_unit: "RD",
              sap_code: b.maSanPham,
              ma_lenh_san_xuat_id: maLenhSanXuatId,
              trang_thai: "ACTIVE",
              checked: 1,
            });
          }
        }
      }
    } else if (loaiSanPham === "Bán thành phẩm") {
      const reelMap = new Map<string, any>();

      for (const reel of this.reelDataList) {
        const key = reel.reelID;
        if (reelMap.has(key)) {
          // Nếu đã có reelID thì cộng dồn số lượng
          const existing = reelMap.get(key);
          existing.initial_quantity += reel.initialQuantity ?? 0;

          // Nếu cần cộng thêm userData1 (nếu là số)
          const existingUserData1 = parseInt(existing.user_data1 || "0", 10);
          const newUserData1 = parseInt(reel.userData1 || "0", 10);
          existing.user_data1 = (existingUserData1 + newUserData1).toString();
        } else {
          // Nếu chưa có thì tạo mới record
          reelMap.set(key, {
            id: null,
            reel_id: reel.reelID,
            part_number: `${reel.sapCode}V_${version}`,
            vendor: reel.vendor ?? "",
            lot: reel.lot ?? "",
            user_data1: reel.userData1 ?? "",
            user_data2: reel.userData2 ?? "",
            user_data3: reel.userData3 ?? "",
            user_data4: reel.userData4 ?? "",
            user_data5: reel.userData5 ?? "",
            initial_quantity: reel.initialQuantity ?? 0,
            msd_level: reel.msdLevel ?? "1",
            msd_initial_floor_time: reel.msdInitialFloorTime ?? "",
            msd_bag_seal_date: reel.msdBagSealDate ?? "",
            sp_material_name: reel.spMaterialName ?? "",
            comments: reel.comments ?? "",
            storage_unit: reel.storageUnit ?? "RD",
            sub_storage_unit: reel.subStorageUnit ?? "",
            location_override: reel.locationOverride ?? "",
            expiration_date: reel.expirationDate ?? "",
            manufacturing_date: reel.manufacturingDate ?? "",
            part_class: reel.partClass ?? "",
            sap_code: reel.sapCode ?? "",
            ma_lenh_san_xuat_id: maLenhSanXuatId,
            lenh_san_xuat_id: null,
            trang_thai: reel.trangThai ?? "ACTIVE",
            checked: 1,
          });
        }
      }

      warehouse_note_info_detail = Array.from(reelMap.values());
    }

    // Payload tổng
    const payload = {
      pallet_infor_detail,
      warehouse_note_info_detail,
    };

    this.planningService.saveCombined(maLenhSanXuatId, payload).subscribe({
      next: (res) => {
        console.log("Lưu thành công:", res);
        this.snackBar.open("Lưu thông tin thành công", "Đóng", {
          duration: 3000,
        });
      },
      error: (err) => {
        console.error("Lỗi khi lưu:", err);
        this.snackBar.open("Lỗi khi lưu thông tin thùng hoặc pallet", "Đóng", {
          duration: 4000,
        });
      },
    });
  }

  //xuất csv bán thành phẩm
  public exportTemBtpCsv(): void {
    // 1. Khai báo header theo đúng thứ tự bạn muốn
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

    // 2. Map reelDataList thành rows
    const rows = this.reelDataList.map((item: ReelData) => [
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
      item.expirationDate?.replace(/-/g, "") ?? "",
      item.manufacturingDate?.replace(/-/g, "") ?? "",
      "",
      item.sapCode,
    ]);

    // 3. Ghép header + rows thành CSV string
    const csvRows = [headers, ...rows]
      .map((row) => row.map((cell) => cell ?? "").join(","))
      .join("\n");

    const csvContent = "\ufeff" + csvRows;

    // 4. Xuất file CSV
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    const timestamp = new Date()
      .toISOString()
      .replace(/[:.]/g, "-")
      .slice(0, -5);
    link.download = `TemBTP_${timestamp}.csv`;
    link.click();

    this.snackBar.open("Xuất CSV thành công", "Đóng", {
      duration: 3000,
    });
  }
  // Helper: đọc serial pallet từ cả PalletItem (serialPallet) lẫn raw API (serial_pallet)
  private getPalletSerialSafe(p: any): string | undefined {
    const s = p?.serialPallet ?? p?.serial_pallet;
    return typeof s === "string" && s.length ? s : undefined;
  }

  // Helper: đọc serial box từ cả ReelData (reelID) lẫn raw API (reel_id)
  private getBoxSerialSafe(d: any): string | undefined {
    const s = d?.reelID ?? d?.reel_id;
    return typeof s === "string" && s.length ? s : undefined;
  }
  private handlePackageComplete(result: any): void {
    // console.log("Pallet:", result.pallet);
    // console.log("Số thùng đã scan:", result.soThungDaScan);
    // console.log("Danh sách thùng:", result.scannedBoxes);

    // Update pallet status in database
    // Update box status to "packaged"
    // Show success notification

    this.snackBar.open(
      `Đã hoàn thành package ${result.soThungDaScan} thùng vào pallet ${result.pallet.maPallet}`,
      "Đóng",
      {
        duration: 3000,
        horizontalPosition: "center",
        verticalPosition: "bottom",
      },
    );
  }
  // map mã ngành tổ
  private resolveCodesFromHierarchy(
    workshops: any[],
    branchCodeRaw?: string,
    teamCodeRaw?: string,
  ): { xuong?: string; nganh?: string; to?: string } {
    for (const ws of workshops) {
      for (const br of ws.branchs) {
        if (br.branch_code === branchCodeRaw) {
          // tìm đúng ngành
          let toCode: string | undefined;
          if (teamCodeRaw) {
            const team = br.production_teams.find(
              (t: any) =>
                t.production_team_name === teamCodeRaw ||
                t.production_team_code === teamCodeRaw,
            );
            if (team) {
              toCode = team.production_team_code;
            }
          }
          return {
            xuong: ws.workshop_code, // ví dụ "01"
            nganh: br.branch_code, // ví dụ "TBCS"
            to: toCode, // ví dụ "SMT"
          };
        }
      }
    }
    return {};
  }

  // Xử lý tạo thùng
  private handleBoxCreation(data: BoxFormData, woId: string): void {
    this.loadingBox = true;

    this.planningService.search(woId).subscribe({
      next: (res) => {
        const order = res.content[0];
        if (!order) {
          this.loadingBox = false;
          return;
        }

        const tongSoLuongSp = data.soLuongTrongThung * data.soLuongThung;
        const prodOrder = this.productionOrders.find(
          (po) => po.maLenhSanXuat === woId,
        );

        let maSanPham = order.productCode;
        if (prodOrder?.loaiSanPham === "Thành phẩm") {
          maSanPham = `LED${order.productCode}`;
        }

        const baseIndex = this.boxItems.length + 1;
        const xuong = prodOrder?.xuong ?? "01";
        const nganh = prodOrder?.nganh ?? "01";
        const to = prodOrder?.to ?? "";

        // Tạo subItems với serial unique cho từng thùng
        const subItems: BoxSubItem[] = [];
        for (let i = 1; i <= data.soLuongThung; i++) {
          const boxIndex = baseIndex + (i - 1);
          const maThung = this.generateBoxCode(boxIndex, xuong, nganh, to);

          subItems.push({
            stt: i,
            maThung: maThung,
            soLuong: data.soLuongTrongThung,
          });
        }

        const newBox: BoxItem = {
          stt: baseIndex,
          maSanPham,
          lotNumber: order.lotNumber,
          version: order.bomVersion,
          vendor: order.productOrderId,
          tenSanPham: order.productName,
          soLuongThung: data.soLuongThung,
          soLuongSp: tongSoLuongSp,
          soLuongTrongThung: data.soLuongTrongThung,
          ghiChu: order.note ?? "",
          kho: "RD",
          trangThaiIn: false,
          serialBox: this.generateBoxCode(baseIndex, xuong, nganh, to),
          subItems,
        };

        this.boxItems = [...this.boxItems, newBox];

        this.updateProductionOrderTotal();
        this.goToBoxTab();
      },
      error: (err) => {
        console.error(err);
        this.loadingBox = false;
      },
      complete: () => {
        this.loadingBox = false;
      },
    });
  }

  private generateBoxCode(
    index: number,
    xuong?: string,
    nganh?: string,
    to?: string,
  ): string {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");
    const sequence = String(index).padStart(4, "0");

    // Chỉ thêm phần mã khi có dữ liệu, tránh nhét '00' sai
    const xuongPart = xuong ? xuong : "01"; // default xưởng nếu cần
    const nganhPart = nganh ? nganh : ""; // để trống nếu chưa map
    const toPart = to ? to : "";

    return `B${xuongPart}${nganhPart}${toPart}${year}${month}${day}${hours}${minutes}${seconds}${sequence}`;
  }

  // Xử lý tạo tem BTP
  private handleTemBTPCreation(data: BoxFormData, woId: string): void {
    if (this.productionOrders.length === 0) {
      this.snackBar.open("Chưa có thông tin lệnh sản xuất!", "Đóng", {
        duration: 3000,
        horizontalPosition: "center",
        verticalPosition: "bottom",
      });
      return;
    }
    const version = this.productionOrders[0]?.version ?? "";
    const now = new Date();
    let counter = this.reelDataList.length + 1;

    const productionOrder =
      this.productionOrders.find((po) => po.maWO === woId) ??
      this.productionOrders[0];

    for (let i = 0; i < data.soLuongThung; i++) {
      const reelID = this.generateReelID(now, productionOrder.maSAP, counter);

      //  check reelID trùng
      const existing = this.reelDataList.find((r) => r.reelID === reelID);

      if (existing) {
        // Nếu reelID đã tồn tại thì cộng thêm số lượng
        existing.initialQuantity += data.soLuongTrongThung;
        existing.userData1 = (
          parseInt(existing.userData1, 10) + data.soLuongTrongThung
        ).toString();
        console.log(`Cộng dồn vào reelID ${reelID}:`, existing);
      } else {
        // Nếu reelID chưa có thì tạo mới
        const reelData: ReelData = {
          reelID,
          partNumber: `${productionOrder.maSAP}V_${version}`,
          vendor: productionOrder.maLenhSanXuat || "",
          lot: productionOrder.maWO || "",
          userData5: productionOrder.maLenhSanXuat || "",
          initialQuantity: data.soLuongTrongThung,
          sapCode: productionOrder.maSAP || "",
          trangThai: "Active",
          storageUnit: "",
          comments: "",
          userData1: "NO",
          userData2: "NO",
          userData3: "NO",
          userData4: productionOrder.maSAP || "",
          msdLevel: "1",
          msdInitialFloorTime: "",
          spMaterialName: "",
          msdBagSealDate: "",
          marketUsage: "",
          quantityOverride: "",
          shelfTime: "",
          warningLimit: "",
          maximumLimit: "",
          warmupTime: "",
          subStorageUnit: "",
          locationOverride: "",
          expirationDate: (() => {
            const d = new Date();
            return `${d.getFullYear() + 2}${String(d.getMonth() + 1).padStart(2, "0")}${String(d.getDate()).padStart(2, "0")}`;
          })(),
          manufacturingDate: new Date()
            .toISOString()
            .split("T")[0]
            .replace(/-/g, ""),
          partClass: productionOrder.loaiSanPham || "",
        };

        this.reelDataList = [...this.reelDataList, reelData];
        console.log("Thêm mới reelData:", reelData);
      }

      counter++;
    }

    console.log("Final reelDataList:", this.reelDataList);
    console.log(`Đã tạo ${this.reelDataList.length} reel items cho Tem BTP`);
    this.goToCreateTemTab();
  }

  private updateProductionOrderTotal(): void {
    const total = this.boxItems.reduce((sum, box) => sum + box.soLuongSp, 0);

    if (this.productionOrders.length > 0) {
      this.productionOrders[0].tongSoLuong = total;
    }
  }

  // Xử lý tạo pallet
  private handlePalletCreation(data: PalletFormData): void {
    const index = this.palletItems.length + 1;

    //  Tạo danh sách pallet con với đầy đủ thông tin
    const subItems: PalletBoxItem[] = [];
    for (let i = 1; i <= data.soLuongPallet; i++) {
      const palletCode = this.generatePalletCode(index, i);
      subItems.push({
        stt: i,
        maPallet: palletCode,
        tongSoThung: data.soLuongThungTrongPallet,
        qrCode: palletCode,
        tienDoScan: 0,
        sucChua: `${data.soLuongThungTrongPallet} thùng`,
      });
    }

    const newPallet: PalletItem = {
      stt: index,
      tenSanPham: data.tenSanPham,
      noSKU: data.noSKU,
      createdAt: new Date().toISOString(),
      tongPallet: data.soLuongPallet,
      tongSlSp: data.soLuongThungTrongPallet * data.soLuongPallet,
      tongSoThung: data.soLuongThungTrongPallet,
      khachHang: data.khachHang,
      poNumber: data.poNumber,
      dateCode: data.dateCode,
      qdsx: data.qdsx,
      nguoiKiemTra: data.nguoiKiemTra,
      ketQuaKiemTra: data.ketQuaKiemTra,
      trangThaiIn: false,
      serialPallet: this.generatePalletCode(index, 1),
      subItems,
    };

    this.palletItems = [...this.palletItems, newPallet];

    console.log(" Đã tạo Pallet với", data.soLuongPallet, "pallet con");
    console.log(" SubItems:", subItems);

    this.goToPalletTab();
  }

  private generatePalletCode(sourceIndex: number, palletIndex: number): string {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");
    const sequence = String(palletIndex).padStart(4, "0");

    // Format: PYYYYMMDDHHMMSS + sourceIndex + sequence
    return `P${year}${month}${day}${hours}${minutes}${seconds}${sequence}`;
  }

  //generate realID
  private generateReelID(date: Date, sapCode: string, counter: number): string {
    // Format: YYMMDDHHMMXX00001
    // VD: 25112408082100001

    const year = date.getFullYear().toString().slice(-2); // 25
    const month = String(date.getMonth() + 1).padStart(2, "0"); // 11
    const day = String(date.getDate()).padStart(2, "0"); // 24
    const hours = String(date.getHours()).padStart(2, "0"); // 08
    const minutes = String(date.getMinutes()).padStart(2, "0"); // 08

    // Lấy 2 số cuối của sapCode
    const sapCodeLast2 = sapCode.slice(-2); // 21 (từ maSAP)

    // Counter tự nhiên tăng dần (5 chữ số)
    const counterStr = String(counter).padStart(5, "0"); // 00001

    return `${year}${month}${day}${hours}${minutes}${sapCodeLast2}${counterStr}`;
  }

  //map data detail
  private mapDetailsToBoxItems(details: any[]): BoxItem[] {
    console.log(" mapDetailsToBoxItems - Input:", details);

    //  Group theo: sapCode + lot + vendor + sp_material_name
    const grouped: { [key: string]: BoxItem } = {};

    for (const d of details) {
      //  Parse qty an toàn
      const qty =
        typeof d.initial_quantity === "number"
          ? d.initial_quantity
          : Number(d.initial_quantity) || 0;

      //  Key group theo thông tin sản phẩm
      const key = `${d.sap_code || ""}|${d.lot || ""}|${d.vendor || ""}|${d.sp_material_name || ""}`;

      if (!grouped[key]) {
        //  Tạo BoxItem cha mới
        grouped[key] = {
          stt: Object.keys(grouped).length + 1,
          maSanPham: d.sap_code || "", //  Lấy từ sap_code
          lotNumber: d.lot || "",
          vendor: d.vendor || "",
          version: this.productionOrders[0]?.version ?? "",
          tenSanPham: d.sp_material_name || "", //  Lấy từ sp_material_name
          soLuongThung: 0, // Sẽ tính = số subItems
          soLuongSp: 0, // Sẽ tính = tổng qty
          soLuongTrongThung: 0, // Sẽ tính = trung bình
          ghiChu: d.comments ?? "",
          kho: d.storage_unit ?? "RD",
          trangThaiIn: false,
          serialBox: "", // Box cha không có serial riêng
          subItems: [],
        };
      }

      //  Thêm box con vào subItems
      grouped[key].subItems.push({
        stt: grouped[key].subItems.length + 1,
        maThung: d.reel_id || "", //  Serial từ reel_id
        soLuong: qty,
      });

      //  Cập nhật tổng số
      grouped[key].soLuongThung = grouped[key].subItems.length;
      grouped[key].soLuongSp += qty;

      //  Tính số lượng trung bình trong 1 thùng
      grouped[key].soLuongTrongThung = Math.round(
        grouped[key].soLuongSp / grouped[key].soLuongThung,
      );
    }

    const result = Object.values(grouped);

    console.log(" mapDetailsToBoxItems - Output:", result);
    console.log(" Total BoxItems:", result.length);
    result.forEach((box, idx) => {
      console.log(` BoxItem ${idx + 1}:`, {
        maSanPham: box.maSanPham,
        tenSanPham: box.tenSanPham,
        soLuongThung: box.soLuongThung,
        soLuongSp: box.soLuongSp,
        soLuongTrongThung: box.soLuongTrongThung,
        subItemsCount: box.subItems.length,
        subItems: box.subItems,
      });
    });

    return result;
  }
  //  THÊM HÀM MAP CHO PALLET ITEMS
  private mapPalletsToPalletItems(palletDetails: any[]): PalletItem[] {
    const grouped: { [key: string]: PalletItem } = {};

    for (const detail of palletDetails) {
      // Tạo key nhóm theo các thông tin chung của pallet cha
      const key = `${detail.customer_name}|${detail.po_number}|${detail.date_code}|${detail.qdsx_no}`;

      if (!grouped[key]) {
        grouped[key] = {
          stt: Object.keys(grouped).length + 1,
          tenSanPham: this.productionOrders[0]?.tenHangHoa ?? "",
          noSKU: detail.item_no_sku ?? "",
          createdAt: detail.production_date ?? "",
          tongPallet: 0,
          tongSlSp: 0,
          tongSoThung: 0,
          khachHang: detail.customer_name ?? "",
          poNumber: detail.po_number ?? "",
          dateCode: detail.date_code ?? "",
          qdsx: detail.qdsx_no ?? "",
          nguoiKiemTra: detail.inspector_name ?? "",
          ketQuaKiemTra: detail.inspection_result ?? "",
          trangThaiIn: false,
          serialPallet: "", // pallet cha không cần serial
          subItems: [],
          tienDoScan: 0,
        };
      }

      // Thêm pallet con vào subItems
      grouped[key].subItems.push({
        stt: grouped[key].subItems.length + 1,
        maPallet: detail.serial_pallet,
        tongSoThung: detail.num_box_actual ?? 0,
        qrCode: detail.serial_pallet,
        tienDoScan: detail.scan_progress ?? 0,
        sucChua: `${detail.num_box_actual ?? 0} thùng`,
      });

      // Cập nhật tổng số liệu
      grouped[key].tongPallet++;
      grouped[key].tongSoThung += detail.num_box_actual ?? 0;
      grouped[key].tongSlSp += detail.num_box_actual ?? 0;
    }

    return Object.values(grouped);
  }

  //  THÊM HÀM MAP CHO REEL DATA (Tem BTP)
  private mapDetailsToReelData(details: any[]): ReelData[] {
    return details.map((detail) => ({
      reelID: detail.reel_id || "",
      partNumber: detail.part_number || "",
      vendor: detail.vendor || "",
      lot: detail.lot || "",
      userData1: detail.user_data1 || "",
      userData2: detail.user_data2 || "",
      userData3: detail.user_data3 || "",
      userData4: detail.user_data4 || "",
      userData5: detail.user_data5 || "",
      initialQuantity: detail.initial_quantity || 0,
      msdLevel: detail.msd_level || "MSL3",
      msdInitialFloorTime: detail.msd_initial_floor_time || "",
      msdBagSealDate: detail.msd_bag_seal_date || "",
      marketUsage: detail.market_usage || "",
      quantityOverride: detail.quantity_override || "",
      shelfTime: detail.shelf_time || "",
      spMaterialName: detail.sp_material_name || "",
      warningLimit: detail.warning_limit || "",
      maximumLimit: detail.maximum_limit || "",
      comments: detail.comments || "",
      warmupTime: detail.warmup_time || "",
      storageUnit: detail.storage_unit || "",
      subStorageUnit: detail.sub_storage_unit || "",
      locationOverride: detail.location_override || "",
      expirationDate: detail.expiration_date || "",
      manufacturingDate: detail.manufacturing_date || "",
      partClass: detail.part_class || "",
      sapCode: detail.sap_code || "",
      trangThai: detail.trang_thai || "Active",
    }));
  }

  private sendWmsApproval(): void {
    // logic gửi phê duyệt WMS
    // Ví dụ gọi service hoặc hiển thị dialog xác nhận
  }
  private sendApproval(): void {
    // logic gửi phê duyệt WMS
    // Ví dụ gọi service hoặc hiển thị dialog xác nhận
  }
}
