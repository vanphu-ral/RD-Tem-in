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
}
export interface PalletBoxItem {
  stt: number;
  maPallet: string;
  tongSoThung: number;
  qrCode?: string;
}

@Component({
  selector: "jhi-lenh-san-xuat-detail",
  templateUrl: "./add-new-lenh-san-xuat.component.html",
  styleUrls: ["./add-new-lenh-san-xuat.component.scss"],
  standalone: false,
})
export class AddNewLenhSanXuatComponent implements OnInit {
  woId = "";
  maLenhSanXuatId = 0;

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
  boxQuantity = 3;
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

  //log
  constructor(
    private dialog: MatDialog,
    private planningService: PlanningWorkOrderService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private accountService: AccountService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.selectedTabIndex = 0;
    this.isMobile = window.innerWidth < 768;
    this.maLenhSanXuatId = 10;

    window.addEventListener("resize", () => {
      this.isMobile = window.innerWidth < 768;
    });
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
    // Chuyển đổi tất cả pallets thành PalletDetailData
    const allPallets: PalletDetailData[] = this.palletItems.map((pallet) => ({
      stt: pallet.stt,
      tenSanPham: pallet.tenSanPham,
      noSKU: pallet.noSKU,
      createdAt: pallet.createdAt,
      tongPallet: pallet.tongPallet,
      tongSlSp: pallet.tongSlSp,
      tongSoThung: pallet.tongSoThung,
      serialPallet: pallet.serialPallet,
    }));

    const dialogData: MultiPalletDialogData = {
      mode: "multiple",
      multipleData: allPallets,
    };

    const dialogRef = this.dialog.open(PalletDetailDialogComponent, {
      width: "100vw",
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

          this.router.navigate([`${newId}/add-new`]);
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

    const pallet_infor_detail = this.palletItems.map((p) => ({
      id: null,
      serial_pallet: p.serialPallet,
      ma_lenh_san_xuat_id: maLenhSanXuatId,
      quantity_per_box: null,
      note: null,
      num_box_per_pallet: null,
      customer_name: p.khachHang ?? null,
      po_number: p.poNumber ?? null,
      date_code: p.dateCode ?? null,
      item_no_sku: p.noSKU ?? null,
      qdsx_no: p.qdsx ?? null,
      production_date: p.createdAt ?? null,
      inspector_name: p.nguoiKiemTra ?? null,
      inspection_result: p.ketQuaKiemTra ?? null,
      scan_progress: 100,
      num_box_actual: p.tongSoThung,
      updated_at: new Date().toISOString(),
      updated_by: currentUser,
    }));

    const warehouse_note_info_detail = this.boxItems.map((b) => ({
      id: null,
      reel_id: b.lotNumber,
      part_number: b.maSanPham,
      vendor: b.vendor,
      lot: b.lotNumber,
      user_data1: "",
      user_data2: "",
      user_data3: "",
      user_data4: "",
      user_data5: "",
      initial_quantity: b.soLuongSp,
      msd_level: "MSL3",
      msd_initial_floor_time: "",
      msd_bag_seal_date: new Date().toISOString().split("T")[0],
      market_usage: null,
      quantity_override: b.soLuongSp,
      shelf_time: "",
      sp_material_name: b.tenSanPham,
      warning_limit: "",
      maximum_limit: "",
      comments: b.ghiChu,
      warmup_time: "",
      storage_unit: "BOX",
      sub_storage_unit: "",
      location_override: "",
      expiration_date: "",
      manufacturing_date: "",
      part_class: "",
      sap_code: b.maSanPham,
      ma_lenh_san_xuat_id: maLenhSanXuatId,
      lenh_san_xuat_id: null,
      trang_thai: "ACTIVE",
      checked: 1,
    }));

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

  private handlePackageComplete(result: any): void {
    console.log("Pallet:", result.pallet);
    console.log("Số thùng đã scan:", result.soThungDaScan);
    console.log("Danh sách thùng:", result.scannedBoxes);

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
    workshops: WorkshopHierarchy[],
    branchRaw?: string,
    teamRaw?: string,
  ): { xuong?: string; nganh?: string; to?: string } {
    // Tìm branch theo branch_code hoặc theo tên gần đúng
    for (const ws of workshops) {
      for (const br of ws.branchs) {
        const branchMatched =
          br.branch_code === branchRaw ||
          br.branch_name?.toLowerCase() === branchRaw?.toLowerCase();
        if (branchMatched) {
          let teamCode: string | undefined = undefined;

          if (teamRaw) {
            const team = br.production_teams.find(
              (t: {
                production_team_code: string;
                production_team_name: string;
              }) =>
                t.production_team_code === teamRaw ||
                t.production_team_name?.toLowerCase() ===
                  teamRaw?.toLowerCase(),
            );
            teamCode = team?.production_team_code;
          }

          return {
            xuong: ws.workshop_code, // VD: '01'
            nganh: br.branch_code, // VD: 'DTTD' hoặc 'TBCS'
            to: teamCode, // VD: 'Tổ SMT'
          };
        }
      }
    }

    // Không match được → giữ undefined để sau generateBoxCode không nhảy '00' mặc định
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

        // Tạo subItems cho từng thùng
        const subItems: BoxSubItem[] = [];
        for (let i = 1; i <= data.soLuongThung; i++) {
          subItems.push({
            stt: i,
            maThung: this.generateBoxCode(
              i,
              prodOrder?.xuong ?? "01",
              prodOrder?.nganh ?? "00",
              prodOrder?.to ?? "",
            ),
            soLuong: data.soLuongTrongThung,
          });
        }

        const newBox: BoxItem = {
          stt: this.boxItems.length + 1,
          maSanPham,
          lotNumber: order.lotNumber,
          vendor: order.productOrderId,
          tenSanPham: order.productName,
          soLuongThung: data.soLuongThung,
          soLuongSp: tongSoLuongSp,
          soLuongTrongThung: data.soLuongTrongThung,
          ghiChu: order.note ?? "",
          kho: "RD",
          trangThaiIn: false,
          serialBox: this.generateBoxCode(
            this.boxItems.length + 1,
            prodOrder?.xuong ?? "01",
            prodOrder?.nganh ?? "00",
            prodOrder?.to ?? "",
          ),
          subItems,
        };

        this.boxItems = [...this.boxItems, newBox];

        console.log(
          `Đã tạo Box với ${data.soLuongThung} thùng, tổng số lượng: ${tongSoLuongSp}`,
        );
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
          partNumber: "",
          vendor: productionOrder.maLenhSanXuat || "",
          lot: productionOrder.maWO || "",
          userData5: productionOrder.maLenhSanXuat || "",
          initialQuantity: data.soLuongTrongThung,
          sapCode: productionOrder.maSAP || "",
          trangThai: "Active",
          storageUnit: "",
          comments: "",
          userData1: data.soLuongTrongThung.toString(),
          userData2: "",
          userData3: productionOrder.tenHangHoa || "",
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

    // Tạo danh sách pallet con
    const subItems: PalletBoxItem[] = [];
    for (let i = 1; i <= data.soLuongPallet; i++) {
      subItems.push({
        stt: i,
        maPallet: this.generatePalletCode(index, i),
        tongSoThung: data.soLuongThungTrongPallet,
        qrCode: this.generatePalletCode(index, i),
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
      serialPallet: this.generatePalletCode(index, 0),
      subItems,
    };

    this.palletItems = [...this.palletItems, newPallet];
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
}
