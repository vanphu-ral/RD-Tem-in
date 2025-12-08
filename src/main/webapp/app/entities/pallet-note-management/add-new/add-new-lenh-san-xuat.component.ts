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
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../confirm-dialog/confirm-dialog.component";
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
import { catchError, finalize, of, timeout } from "rxjs";
import { LenhSanXuatService } from "../service/lenh-san-xuat.service";
import {
  PrintPalletData,
  PrintPalletDialogComponent,
} from "../print-pallet-dialog/print-pallet-dialog.component";
import { SerialBoxPalletMapping } from "../scan-pallet-dialog/scan-pallet-dialog.component";

interface ProductionOrder {
  id?: number;
  maLenhSanXuat: string;
  maSAP: string;
  tenHangHoa: string;
  maWO: string;
  version: string;
  maKhoNhap: string;
  lotNumber?: string;
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
  lotNumber?: string;
  product_type: string;
}
export interface ReelData {
  id?: number;
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
  TPNK?: string;
}
interface BoxItem {
  stt: number;
  id?: number;
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
  id?: number;
  stt: number;
  maThung: string;
  soLuong: number;
}
export interface PalletItem {
  stt: number;
  id?: number;
  maLenhSanXuatId?: number;
  tenSanPham: string;
  noSKU: string;
  createdAt: string;
  tongPallet: number;
  tongSlSp: number;
  tongSoThung: number;
  thungScan?: number;
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
  nganh?: string;
  note: string;
  to?: string;
}
export interface PalletBoxItem {
  stt: number;
  id?: number;
  maPallet: string;
  tongSoThung: number;
  thungScan?: number;
  qrCode?: string;
  tienDoScan?: number;
  sucChua?: string;
  scannedBoxes?: string[];
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
  resourceUrl1 = this.applicationConfigService.getEndpointFor(
    "/api/chi-tiet-lenh-san-xuat/update",
  );
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
    // "vendor",
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
    // "tongSlSp",
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
    // "vendor",
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
  // Danh sách cố định cho Bán thành phẩm
  maKhoNhapOptionsBTP = [
    { value: "", label: "--" },
    { value: "01", label: "01 - Kho linh kiện điện tử" },
    { value: "02", label: "02 - Kho vật tư LED" },
    { value: "03", label: "03 - Kho vật tư TBCS" },
    { value: "04", label: "04 - Kho vật tư CLC" },
    { value: "05", label: "05 - Kho ngành ĐTTĐ" },
    { value: "06", label: "06 - Kho ngành LRSP LED 1" },
    { value: "07", label: "07 - Kho ngành SMART" },
    { value: "08", label: "08 - Kho ngành LED2" },
    { value: "10", label: "10 - Kho cơ khí xưởng" },
    { value: "11", label: "11 - Kho ngành CNPT" },
    { value: "12", label: "12 - Kho vật tư Smart" },
    { value: "13", label: "13 - Kho ngành CNPT 2" },
    { value: "14", label: "14 - Kho chị Hòa 2" },
    { value: "15", label: "15 - Kho Pilot TTRD" },
    { value: "17", label: "17 - Kho vật tư SKD" },
    { value: "18", label: "18 - Kho trung tâm RD" },
    { value: "19", label: "19 - Kho LKDT thủ công" },
    { value: "20", label: "20 - Kho xuất khẩu" },
  ];

  // Danh sách động cho Thành phẩm
  maKhoNhapOptionsTP: { value: string; label: string }[] = [];

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
  isLoadingProgress: { [maPallet: string]: boolean } = {};

  isDetail = false;
  //log
  constructor(
    private dialog: MatDialog,
    private planningService: PlanningWorkOrderService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private accountService: AccountService,
    private router: Router,
    protected http: HttpClient,
    private route: ActivatedRoute,
    protected applicationConfigService: ApplicationConfigService,
    protected lenhSanXuatService: LenhSanXuatService,
  ) {}

  ngOnInit(): void {
    this.selectedTabIndex = 0;
    this.isMobile = window.innerWidth < 768;
    this.isDetail = false;

    const id = this.route.snapshot.params["id"];
    if (id) {
      this.isDetail = true;
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
              id: this.warehouseNoteInfo.id,
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
              lotNumber: this.warehouseNoteInfo.lotNumber,
            },
          ];
          this.isThanhPham =
            this.productionOrders[0]?.loaiSanPham === "Thành phẩm";

          if (this.details && this.details.length > 0) {
            if (this.warehouseNoteInfo.product_type === "Thành phẩm") {
              this.boxItems = this.mapDetailsToBoxItems(this.details);
              const total = this.boxItems.reduce(
                (sum, b) => sum + (b.soLuongSp ?? 0),
                0,
              );
              this.productionOrders[0].tongSoLuong = total;
            } else if (
              this.warehouseNoteInfo.product_type === "Bán thành phẩm"
            ) {
              this.reelDataList = this.mapDetailsToReelData(this.details);
              const total = this.reelDataList.reduce(
                (sum, r) => sum + (r.initialQuantity ?? 0),
                0,
              );
              this.productionOrders[0].tongSoLuong = total;
            }
          }

          if (this.pallets && this.pallets.length > 0) {
            this.palletItems = this.mapPalletsToPalletItems(this.pallets).map(
              (p) => ({
                ...p,
                maLenhSanXuatId: this.maLenhSanXuatId,
              }),
            );
          }

          const loai = this.productionOrders[0]?.loaiSanPham;
          if (loai === "Thành phẩm") {
            this.showThungTab = true;
            this.showPalletTab = true;
            this.showTemBtpTab = false;
            this.loadMaKhoNhapOptionsTP();
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
  removeSpaces(): void {
    const control = this.form.get("woId");
    if (control) {
      const value = control.value ?? ""; // tránh null
      control.setValue(value.replace(/\s/g, ""), { emitEvent: false });
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
        timeout(10000),
        catchError((err) => {
          console.error("Lỗi hoặc timeout:", err);
          this.snackBar.open("Không tìm thấy thông tin đơn sản xuất", "Đóng", {
            duration: 3000,
          });
          this.loading = false;
          return of({ content: [] });
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

          // ===== FIX: Map đúng field name =====
          this.productionOrders = filtered.map((item) => ({
            maLenhSanXuat: item.sapWoId,
            maSAP: item.productCode,
            tenHangHoa: item.productName,
            maWO: item.woId,
            version: item.bomVersion,
            maKhoNhap: "",
            tongSoLuong: 0,
            trangThai: "Bản nháp",
            // loaiSanPham:
            //   item.productType === "1" || item.productType == null
            //     ? "Thành phẩm"
            //     : "Bán thành phẩm",
            loaiSanPham: item.productType,
            lotNumber: item.lotNumber,

            // ===== LƯU DỮ LIỆU THÔ =====
            nganhRaw: item.branchCode, // "LR LED"
            toRaw: item.groupCode, // "LR LED 5"

            // ===== KHỞI TẠO CÁC FIELD ĐÃ MAP (sẽ được update sau) =====
            xuong: "",
            nganh: "",
            to: "",
          }));

          console.log(" Before hierarchy mapping:", this.productionOrders);

          // ===== GỌI API LẤY HIERARCHY =====
          this.planningService.getHierarchy().subscribe({
            next: (workshops) => {
              console.log(" Hierarchy data:", workshops);

              // ===== MAP TỪ HIERARCHY =====
              this.productionOrders = this.productionOrders.map((order) => {
                const mapped = this.resolveCodesFromHierarchy(
                  workshops,
                  order.nganhRaw,
                  order.toRaw,
                );

                console.log(" Mapped result for order:", {
                  nganhRaw: order.nganhRaw,
                  toRaw: order.toRaw,
                  mapped,
                });

                // ===== MER GE KẾT QUẢ MAP VÀO ORDER =====
                return {
                  ...order,
                  xuong: mapped.xuong ?? order.xuong,
                  nganh: mapped.nganh ?? order.nganh,
                  to: mapped.to ?? order.to,
                };
              });

              console.log(" After hierarchy mapping:", this.productionOrders);
            },
            error: (err) => {
              console.error(" Lỗi lấy hierarchy:", err);
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
            this.loadMaKhoNhapOptionsTP();
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
  printAllPallets(): void {
    if (!this.palletItems || this.palletItems.length === 0) {
      this.snackBar.open("Chưa có pallet nào để in", "Đóng", {
        duration: 3000,
      });
      return;
    }

    const allPrintData: PrintPalletData[] = [];
    console.log("Starting print preparation...");
    this.refreshPalletItemsBeforePrint().then(() => {
      this.palletItems.forEach((palletGroup, groupIndex) => {
        console.log(
          "\n=== Processing Pallet Group " + (groupIndex + 1) + " ===",
        );

        palletGroup.subItems.forEach((palletSub, subIndex) => {
          console.log(
            "Processing sub-pallet " +
              (subIndex + 1) +
              "/" +
              palletGroup.subItems.length,
          );

          // QUAN TRỌNG: Lấy scannedBoxes từ palletSub (đã có sẵn từ loadPalletProgress)
          const scannedBoxes: string[] = palletSub.scannedBoxes ?? [];

          console.log("  scannedBoxes from palletSub:", scannedBoxes);
          console.log("  scannedBoxes count:", scannedBoxes.length);

          let tongSoSanPhamDaScan = 0;
          let soLuongSpTrong1ThungThucTe = 0;

          // Nếu đã có dữ liệu scan
          if (scannedBoxes.length > 0) {
            const uniqueScanned = [...new Set(scannedBoxes)];

            uniqueScanned.forEach((serial, idx) => {
              const trimmed = serial.trim();
              let qtyFound = 0;
              let found = false;

              if (this.warehouseNoteInfo?.product_type === "Thành phẩm") {
                // Ưu tiên 1: Tìm trong subItems trước
                for (const box of this.boxItems) {
                  if (found) {
                    break;
                  }

                  const subItem = box.subItems?.find(
                    (sub: any) => sub.maThung === trimmed,
                  );

                  if (subItem) {
                    qtyFound = subItem.soLuong || 0;
                    tongSoSanPhamDaScan += qtyFound;

                    if (idx === 0) {
                      soLuongSpTrong1ThungThucTe = qtyFound;
                    }

                    found = true;
                    console.log(
                      "    Found in subItems: " +
                        trimmed +
                        " -> " +
                        qtyFound +
                        " SP",
                    );
                    break;
                  }
                }

                // Ưu tiên 2: Nếu không tìm thấy trong subItems, check box cha
                if (!found) {
                  for (const box of this.boxItems) {
                    if (box.serialBox === trimmed) {
                      qtyFound = box.soLuongTrongThung || 0;
                      tongSoSanPhamDaScan += qtyFound;

                      if (idx === 0) {
                        soLuongSpTrong1ThungThucTe = qtyFound;
                      }

                      found = true;
                      console.log(
                        "    Found as parent box: " +
                          trimmed +
                          " -> " +
                          qtyFound +
                          " SP",
                      );
                      break;
                    }
                  }
                }

                if (!found) {
                  console.warn("    Box not found: " + trimmed);
                }
              }

              if (this.warehouseNoteInfo?.product_type === "Bán thành phẩm") {
                const reel = this.reelDataList.find(
                  (r: any) => r.reelID === trimmed,
                );
                if (reel) {
                  qtyFound = reel.initialQuantity || 0;
                  tongSoSanPhamDaScan += qtyFound;

                  if (idx === 0) {
                    soLuongSpTrong1ThungThucTe = qtyFound;
                  }

                  console.log(
                    "    Found reel: " + trimmed + " -> " + qtyFound + " SP",
                  );
                } else {
                  console.warn("    Reel not found: " + trimmed);
                }
              }
            });
          }

          const soThungDuKien = palletSub.tongSoThung;

          // Số lượng SP trong 1 thùng
          const soLuongSpTrong1Thung =
            scannedBoxes.length > 0
              ? soLuongSpTrong1ThungThucTe
              : this.warehouseNoteInfo?.product_type === "Thành phẩm"
                ? this.boxItems[0]?.soLuongTrongThung || 0
                : this.reelDataList[0]?.initialQuantity || 0;

          // soLuongCaiDatPallet chỉ là tổng số sản phẩm đã scan
          const soLuongCaiDatPallet = tongSoSanPhamDaScan;

          console.log("  Summary:", {
            scannedBoxesCount: scannedBoxes.length,
            tongSoSanPhamDaScan: tongSoSanPhamDaScan,
            soLuongSpTrong1Thung: soLuongSpTrong1Thung,
            soThungDuKien: soThungDuKien,
            soLuongCaiDatPallet: soLuongCaiDatPallet,
          });

          // Lấy thông tin box đầu tiên
          let firstBoxSerial = "";
          let lotNumber = "";

          if (scannedBoxes.length > 0) {
            firstBoxSerial = scannedBoxes[0];

            // Tìm lot number
            for (const box of this.boxItems) {
              if (box.serialBox === firstBoxSerial) {
                lotNumber = box.lotNumber || "";
                break;
              }

              const subItem = box.subItems?.find(
                (sub: any) => sub.maThung === firstBoxSerial,
              );
              if (subItem) {
                lotNumber = box.lotNumber || "";
                break;
              }
            }

            if (!lotNumber && this.reelDataList) {
              const reel = this.reelDataList.find(
                (r: any) => r.reelID === firstBoxSerial,
              );
              if (reel) {
                lotNumber = reel.lot || "";
              }
            }
          } else {
            firstBoxSerial = palletSub.maPallet;
            lotNumber = this.boxItems[0]?.lotNumber || "";
          }

          const printData: PrintPalletData = {
            khachHang: palletGroup.khachHang ?? "N/A",
            serialPallet: palletSub.maPallet,
            tenSanPham: palletGroup.tenSanPham,
            poNumber: palletGroup.poNumber ?? "",
            itemNoSku: palletGroup.noSKU ?? "",
            dateCode: palletGroup.dateCode ?? "",
            soQdsx: palletGroup.qdsx ?? "",
            ngaySanXuat: palletGroup.createdAt
              ? new Date(palletGroup.createdAt).toLocaleDateString("vi-VN")
              : new Date().toLocaleDateString("vi-VN"),
            nguoiKiemTra: palletGroup.nguoiKiemTra ?? "",
            ketQuaKiemTra: palletGroup.ketQuaKiemTra ?? "",
            nganh: palletGroup.nganh ?? this.productionOrders[0]?.nganh ?? "",
            led2: palletGroup.nganh ?? this.productionOrders[0]?.nganh ?? "",
            to: palletGroup.to ?? this.productionOrders[0]?.to ?? "",
            lpl2: palletGroup.to ?? this.productionOrders[0]?.to ?? "",

            soLuongCaiDatPallet: soLuongCaiDatPallet,
            thuTuGiaPallet: palletSub.stt,
            soLuongBaoNgoaiThungGiaPallet: soThungDuKien.toString(),
            slThung: palletGroup.tongSlSp,

            note: palletGroup.note ?? "",
            productCode: palletGroup.tenSanPham,
            serialBox: firstBoxSerial,
            qty: soLuongCaiDatPallet,
            lot: lotNumber,
            date: new Date().toLocaleDateString("vi-VN"),
            scannedBoxes: scannedBoxes,
          };

          console.log("  Print data for pallet " + palletSub.maPallet + ":", {
            soLuongCaiDatPallet: printData.soLuongCaiDatPallet,
            slThung: printData.slThung,
            qty: printData.qty,
            scannedBoxesFromPallet: scannedBoxes.length,
          });

          allPrintData.push(printData);
        });
      });

      if (allPrintData.length === 0) {
        this.snackBar.open("Không có dữ liệu để in", "Đóng", {
          duration: 3000,
        });
        return;
      }

      console.log("Prepared " + allPrintData.length + " print data items");
      this.openPrintDialogFromAddNew(allPrintData);
    });
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
    // Extract valid reel IDs from box items (the actual box serials)
    const validReelIds: string[] = [];

    // For Thành phẩm: extract from boxItems subItems
    if (this.warehouseNoteInfo?.product_type === "Thành phẩm") {
      this.boxItems.forEach((box) => {
        box.subItems.forEach((subItem) => {
          if (subItem.maThung && subItem.maThung.trim()) {
            validReelIds.push(subItem.maThung.trim());
          }
        });
      });
    }
    // For Bán thành phẩm: extract from reelDataList
    else if (this.warehouseNoteInfo?.product_type === "Bán thành phẩm") {
      this.reelDataList.forEach((reel) => {
        if (reel.reelID && reel.reelID.trim()) {
          validReelIds.push(reel.reelID.trim());
        }
      });
    }

    console.log("Product type:", this.warehouseNoteInfo?.product_type);
    console.log("Box items:", this.boxItems);
    console.log("Reel data list:", this.reelDataList);
    console.log("Extracted valid reel IDs:", validReelIds);
    console.log("maLenhSanXuatId:", this.maLenhSanXuatId);

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
        thungScan: pallet.thungScan,
        serialPallet: pallet.serialPallet,
        //  THÊM CÁC FIELD CẦN CHO PRINT
        khachHang: pallet.khachHang,
        poNumber: pallet.poNumber,
        dateCode: pallet.dateCode,
        qdsx: pallet.qdsx,
        nguoiKiemTra: pallet.nguoiKiemTra,
        ketQuaKiemTra: pallet.ketQuaKiemTra,
        subItems: pallet.subItems || [],
        branch: this.productionOrders[0]?.nganh ?? "",
        team: this.productionOrders[0]?.to ?? "",
        note: pallet.note ?? "",
        // Add box data for print dialog
        boxItems: this.boxItems,
        maLenhSanXuatId: this.maLenhSanXuatId,
        validReelIds: validReelIds,
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
      subItems: box.subItems || [],
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
    // Prepare valid reel IDs (whitelist) from current data so package flow can pass it to scan dialog
    const validReelIds: string[] = [];
    if (this.warehouseNoteInfo?.product_type === "Thành phẩm") {
      // collect all box serials (subItems.maThung)
      this.boxItems.forEach((box) => {
        (box.subItems || []).forEach((sub) => {
          if (sub.maThung && sub.maThung.trim()) {
            validReelIds.push(sub.maThung.trim());
          }
        });
      });
    } else if (this.warehouseNoteInfo?.product_type === "Bán thành phẩm") {
      // collect reel IDs
      this.reelDataList.forEach((r) => {
        if (r.reelID && r.reelID.trim()) {
          validReelIds.push(r.reelID.trim());
        }
      });
    }

    // Flatten tất cả pallet con từ palletItems
    const allPallets: PalletDetailData[] = this.palletItems.reduce(
      (acc: PalletDetailData[], pallet: PalletItem, parentIndex: number) => {
        const subData = pallet.subItems.map(
          (sub: PalletBoxItem, index: number) => ({
            stt: index + 1,
            tenSanPham: pallet.tenSanPham,
            noSKU: pallet.noSKU,
            createdAt: pallet.createdAt,
            tongPallet: 1,
            // Đừng nhồi số thùng vào tongSlSp; nếu không dùng, set 0 cho an toàn
            tongSlSp: pallet.tongSlSp,
            tongSoThung: sub.tongSoThung ?? 0,
            thungScan: sub.thungScan ?? 1,
            serialPallet: sub.maPallet,
            khachHang: pallet.khachHang,
            poNumber: pallet.poNumber,
            dateCode: pallet.dateCode,
            qdsx: pallet.qdsx,
            note: pallet.note,
            nguoiKiemTra: pallet.nguoiKiemTra,
            ketQuaKiemTra: pallet.ketQuaKiemTra,
            branch: pallet.nganh ?? this.productionOrders[0]?.nganh ?? "",
            team: pallet.to ?? this.productionOrders[0]?.to ?? "",
            maLenhSanXuatId: pallet.maLenhSanXuatId,
            validReelIds: validReelIds,
            tienDoScan: 0,
            scannedBoxes: [],

            // Quan trọng: cung cấp index của pallet cha để onPrintAll lấy đúng source
            parentPalletIndex: parentIndex,
            boxItems: this.boxItems,
          }),
        );
        return acc.concat(subData);
      },
      [],
    );

    // ===== LOADING INDICATOR =====
    this.snackBar.open("Đang tải tiến độ scan...", "", {
      duration: 0, // Không tự đóng
    });

    // ===== ĐỢI LOAD XONG MỚI MỞ DIALOG =====
    this.loadAllPalletProgressAsync(allPallets).then(() => {
      // Đóng loading snackbar
      this.snackBar.dismiss();

      console.log(" Đã load xong tiến độ, mở dialog:", allPallets);

      const dialogData: MultiPalletDialogData = {
        mode: "multiple",
        multipleData: allPallets,
        boxItems: this.boxItems,
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
  // deleteBox(index: number): void {
  //   // Xoá  index
  //   this.boxItems = this.boxItems.filter((_, i) => i !== index);

  //   //cập nhật lại STT
  //   this.boxItems = this.boxItems.map((box, idx) => ({
  //     ...box,
  //     stt: idx + 1,
  //   }));

  //   // Cập nhật lại tổng số lượng ở bảng 1
  //   this.updateProductionOrderTotal();

  //   console.log(`Đã xoá thùng tại index ${index}`);
  // }

  //lưu bảng 1
  saveWarehouseNotes(): void {
    const currentUser = this.accountService.isAuthenticated()
      ? this.accountService["userIdentity"]?.login
      : "unknown";
    let comment2 = "";
    this.productionOrders.forEach((order) => {
      if (!order.maKhoNhap || order.maKhoNhap.trim() === "") {
        this.snackBar.open("Vui lòng chọn mã kho nhập trước khi lưu!", "Đóng", {
          duration: 4000,
          panelClass: ["snackbar-error"],
        });
        return; // dừng luôn, không gọi API cho order này
      }
      if (
        order.loaiSanPham === "Bán thành phẩm" &&
        this.reelDataList.length > 0
      ) {
        // Lấy comments từ reel đầu tiên (hoặc merge tất cả nếu cần)
        const allComments = this.reelDataList
          .map((r) => r.comments)
          .filter((c) => c && c.trim() !== "")
          .join("; ");
        comment2 = allComments || "";
      }
      const payload: WarehouseNotePayload = {
        id: order.id,
        ma_lenh_san_xuat: order.maLenhSanXuat,
        sap_code: order.maSAP,
        sap_name: order.tenHangHoa,
        work_order_code: order.maWO,
        version: order.version,
        storage_code: order.maKhoNhap,
        total_quantity: order.tongSoLuong,
        create_by: currentUser ?? "",
        trang_thai: order.trangThai,
        group_name: order.to,
        comment_2: comment2,
        approver_by: currentUser ?? "",
        branch: order.nganh,
        product_type: order.loaiSanPham,
        destination_warehouse: 1,
      };

      // Nếu có id thì gọi update, nếu chưa có thì gọi create
      const request$ = order.id
        ? this.planningService.updateWarehouseNote(order.id, payload)
        : this.planningService.createWarehouseNote(payload);

      request$.subscribe({
        next: (res) => {
          // ===== FIX: Extract ID from nested warehouse_note_info object =====
          const newId = res.warehouse_note_info?.id ?? res.id ?? order.id;

          console.log(" Full response:", res);
          console.log(" Extracted ID:", newId);

          // Update maLenhSanXuatId and order.id
          this.maLenhSanXuatId = newId;
          order.id = newId;

          console.log("maLenhSanXuatId updated to:", this.maLenhSanXuatId);

          this.snackBar.open(`Lưu thành công!`, "Đóng", {
            duration: 3000,
            panelClass: ["snackbar-success"],
          });

          this.router.navigate([`/warehouse-note-infos/${newId}/add-new`]);
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
  // Xóa Box
  deleteBox(index: number): void {
    const box = this.boxItems[index];

    // ===== MỞ CONFIRM DIALOG =====
    const dialogData: ConfirmDialogData = {
      title: "Xác nhận xóa thùng",
      message: `Bạn có chắc muốn xóa thùng "${box.maSanPham}" với ${box.subItems.length} thùng con?\n\nHành động này không thể hoàn tác!`,
      confirmText: "Xóa",
      cancelText: "Hủy",
      type: "danger",
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "500px",
      data: dialogData,
      disableClose: false,
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) {
        return; // User clicked "Hủy"
      }

      // ===== USER CONFIRMED → PROCEED WITH DELETE =====

      // Check nếu có subItems với ID (đã lưu DB)
      const subItemsWithId = box.subItems.filter((sub) => sub.id);

      if (subItemsWithId.length > 0) {
        // ===== XÓA TRONG DB =====
        const deletePromises: Promise<void>[] = [];

        subItemsWithId.forEach((subItem) => {
          const promise = new Promise<void>((resolve, reject) => {
            this.planningService.deleteBoxDetail(subItem.id!).subscribe({
              next: () => {
                console.log(`Đã xóa box detail ID ${subItem.id} khỏi DB`);
                resolve();
              },
              error: (err) => {
                console.error(`Lỗi xóa box detail ID ${subItem.id}:`, err);
                reject(err);
              },
            });
          });
          deletePromises.push(promise);
        });

        // Đợi tất cả API xóa hoàn thành
        Promise.all(deletePromises)
          .then(() => {
            // Xóa khỏi FE sau khi xóa DB thành công
            this.removeBoxFromFE(index);

            this.snackBar.open("Đã xóa thùng khỏi cơ sở dữ liệu", "Đóng", {
              duration: 3000,
              panelClass: ["snackbar-success"],
            });
          })
          .catch((err) => {
            console.error("Lỗi khi xóa box:", err);
            this.snackBar.open("Lỗi khi xóa thùng khỏi cơ sở dữ liệu", "Đóng", {
              duration: 4000,
              panelClass: ["snackbar-error"],
            });
          });
      } else {
        // ===== CHƯA LƯU DB → XÓA TRỰC TIẾP FE =====
        this.removeBoxFromFE(index);

        this.snackBar.open("Đã xóa thùng", "Đóng", {
          duration: 2000,
        });
      }
    });
  }

  // Xóa Pallet
  deletePallet(palletItem: PalletItem, subIndex?: number): void {
    // ===== CASE 1: XÓA PALLET CON CỤ THỂ =====
    if (subIndex !== undefined) {
      const palletSub = palletItem.subItems[subIndex];

      // ===== MỞ CONFIRM DIALOG =====
      const dialogData: ConfirmDialogData = {
        title: "Xác nhận xóa pallet",
        message: `Bạn có chắc muốn xóa pallet "${palletSub.maPallet}"?\n\nHành động này không thể hoàn tác!`,
        confirmText: "Xóa",
        cancelText: "Hủy",
        type: "danger",
      };

      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: "500px",
        data: dialogData,
      });

      dialogRef.afterClosed().subscribe((confirmed: boolean) => {
        if (!confirmed) {
          return;
        }

        // ===== USER CONFIRMED =====
        if (palletSub.id) {
          // Đã lưu DB → gọi API
          this.planningService.deletePalletDetail(palletSub.id).subscribe({
            next: () => {
              this.removePalletSubFromFE(palletItem, subIndex);

              this.snackBar.open("Đã xóa pallet khỏi cơ sở dữ liệu", "Đóng", {
                duration: 3000,
                panelClass: ["snackbar-success"],
              });
            },
            error: (err) => {
              console.error("Lỗi xóa pallet:", err);
              this.snackBar.open(
                "Lỗi khi xóa pallet khỏi cơ sở dữ liệu",
                "Đóng",
                {
                  duration: 4000,
                  panelClass: ["snackbar-error"],
                },
              );
            },
          });
        } else {
          // Chưa lưu DB → xóa trực tiếp FE
          this.removePalletSubFromFE(palletItem, subIndex);

          this.snackBar.open("Đã xóa pallet", "Đóng", {
            duration: 2000,
          });
        }
      });
    }
    // ===== CASE 2: XÓA TOÀN BỘ PALLET GROUP =====
    else {
      const dialogData: ConfirmDialogData = {
        title: "Xác nhận xóa tất cả pallet",
        message: `Bạn có chắc muốn xóa tất cả ${palletItem.subItems.length} pallet?\n\nHành động này không thể hoàn tác!`,
        confirmText: "Xóa tất cả",
        cancelText: "Hủy",
        type: "danger",
      };

      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: "500px",
        data: dialogData,
      });

      dialogRef.afterClosed().subscribe((confirmed: boolean) => {
        if (!confirmed) {
          return;
        }

        // ===== XÓA TẤT CẢ PALLET CON =====
        const deletePromises: Promise<void>[] = [];

        palletItem.subItems.forEach((sub) => {
          if (sub.id) {
            const promise = new Promise<void>((resolve, reject) => {
              this.planningService.deletePalletDetail(sub.id!).subscribe({
                next: () => {
                  console.log(`Đã xóa pallet detail ID ${sub.id} khỏi DB`);
                  resolve();
                },
                error: (err) => {
                  console.error(`Lỗi xóa pallet detail ID ${sub.id}:`, err);
                  reject(err);
                },
              });
            });
            deletePromises.push(promise);
          }
        });

        if (deletePromises.length > 0) {
          Promise.all(deletePromises)
            .then(() => {
              this.removePalletGroupFromFE(palletItem);

              this.snackBar.open(
                "Đã xóa tất cả pallet khỏi cơ sở dữ liệu",
                "Đóng",
                {
                  duration: 3000,
                  panelClass: ["snackbar-success"],
                },
              );
            })
            .catch((err) => {
              console.error("Lỗi khi xóa pallet:", err);
              this.snackBar.open(
                "Lỗi khi xóa pallet khỏi cơ sở dữ liệu",
                "Đóng",
                {
                  duration: 4000,
                  panelClass: ["snackbar-error"],
                },
              );
            });
        } else {
          // Tất cả chưa lưu DB → xóa trực tiếp
          this.removePalletGroupFromFE(palletItem);

          this.snackBar.open("Đã xóa tất cả pallet", "Đóng", {
            duration: 2000,
          });
        }
      });
    }
  }

  //export excel realdata
  exportExcelReelData(): void {
    if (!this.reelDataList || this.reelDataList.length === 0) {
      this.snackBar.open("Không có dữ liệu để xuất Excel", "Đóng", {
        duration: 3000,
      });
      return;
    }

    // Header cố định theo yêu cầu
    const headers = [
      "NumberOfPlanning",
      "ItemCode",
      "ProductName",
      "SapWo",
      "Version",
      "TimeRecieved",
      "ReelID",
      "PartNumber",
      "Vendor",
      "QuantityOfPackage",
      "MFGDate",
      "ProductionShilt",
      "OpName",
      "Comments",
    ];

    // Map dữ liệu theo đúng cột, không lấy thêm trường nào khác
    const rows = this.reelDataList.map((r) => [
      r.userData1 ?? "", // NumberOfPlanning
      r.userData4 ?? "", // ItemCode
      r.userData3 ?? "", // ProductName
      r.userData5 ?? "", // SapWo
      this.productionOrders[0]?.version ?? "", // Version
      r.manufacturingDate ?? "", // TimeRecieved
      r.reelID, // ReelID
      r.partNumber, // PartNumber
      r.vendor ?? "", // Vendor
      r.initialQuantity, // QuantityOfPackage
      r.manufacturingDate ?? "", // MFGDate
      this.productionOrders[0]?.to ?? "", // ProductionShilt
      r.TPNK ?? "", // OpName (TPNK bạn đã nhập)
      r.comments ?? "", // Comments
    ]);

    // Tạo worksheet từ AOA: chỉ có headers + rows
    const worksheet: XLSX.WorkSheet = XLSX.utils.aoa_to_sheet([
      headers,
      ...rows,
    ]);
    const workbook: XLSX.WorkBook = {
      Sheets: { ReelData: worksheet },
      SheetNames: ["ReelData"],
    };

    XLSX.writeFile(workbook, "ReelData.xlsx");
  }
  //lưu dữ liệu box và pallet
  saveCombined(maLenhSanXuatId: number): void {
    // Validate maLenhSanXuatId
    if (!maLenhSanXuatId || maLenhSanXuatId === undefined) {
      this.snackBar.open(
        "Lỗi: Chưa có ID lệnh sản xuất. Vui lòng lưu lệnh sản xuất trước.",
        "Đóng",
        {
          duration: 4000,
          panelClass: ["snackbar-error"],
        },
      );
      return;
    }

    const currentUser = this.accountService.isAuthenticated()
      ? this.accountService["userIdentity"]?.login
      : "unknown";
    const version = this.productionOrders[0]?.version ?? "1.0";
    const loaiSanPham = this.productionOrders[0]?.loaiSanPham;

    // Tạo Set serial đã tồn tại từ backend data
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

    // Log để debug
    console.log("=== DEBUG SAVE COMBINED ===");
    console.log("Existing Pallet Serials:", Array.from(existingPalletSerials));
    console.log("Existing Box Serials:", Array.from(existingBoxSerials));
    console.log("Current palletItems count:", this.palletItems?.length);
    console.log("Current boxItems count:", this.boxItems?.length);

    // Pallet payload - CHỈ lưu pallet CHƯA tồn tại
    const pallet_infor_detail: any[] = [];
    for (const p of this.palletItems) {
      for (const sub of p.subItems) {
        const isExisting = existingPalletSerials.has(sub.maPallet);
        console.log(
          `Pallet ${sub.maPallet}: ${isExisting ? "SKIP (đã tồn tại)" : "ADD (mới)"}`,
        );

        if (!isExisting) {
          pallet_infor_detail.push({
            id: null,
            serial_pallet: sub.maPallet,
            ma_lenh_san_xuat_id: maLenhSanXuatId,
            customer_name: p.khachHang ?? null,
            po_number: p.poNumber ?? null,
            date_code: p.dateCode ?? null,
            item_no_sku: p.noSKU ?? null,
            quantity_per_box: p.tongSlSp,
            qdsx_no: p.qdsx ?? null,
            production_date: p.createdAt ?? null,
            inspector_name: p.nguoiKiemTra ?? null,
            inspection_result: p.ketQuaKiemTra ?? null,
            scan_progress: sub.tienDoScan ?? 0,
            num_box_actual: sub.tongSoThung,
            num_box_config: sub.thungScan ?? 1,
            updated_at: new Date().toISOString(),
            updated_by: currentUser,
            note: p.note ?? null,
          });
        }
      }
    }

    // Box/Tem payload - CHỈ lưu box/tem CHƯA tồn tại
    let warehouse_note_info_detail: any[] = [];

    if (loaiSanPham === "Thành phẩm") {
      // Thành phẩm: flatten boxItems
      for (const b of this.boxItems) {
        // Trường hợp không có subItems (box không chia nhỏ)
        if (!b.subItems || b.subItems.length === 0) {
          const isExisting = existingBoxSerials.has(b.serialBox);
          console.log(
            `Box ${b.serialBox}: ${isExisting ? "SKIP (đã tồn tại)" : "ADD (mới)"}`,
          );

          if (!isExisting && b.serialBox) {
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

        // Trường hợp có subItems (box chia thành nhiều thùng con)
        for (const sub of b.subItems) {
          const isExisting = existingBoxSerials.has(sub.maThung);
          console.log(
            `Box ${sub.maThung}: ${isExisting ? "SKIP (đã tồn tại)" : "ADD (mới)"}`,
          );

          if (!isExisting && sub.maThung) {
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

        // SKIP nếu reel đã tồn tại trong backend
        if (existingBoxSerials.has(key)) {
          console.log(`Reel ${key}: SKIP (đã tồn tại)`);
          continue;
        }

        console.log(`Reel ${key}: PROCESS (mới hoặc merge)`);

        // Merge nếu trùng trong reelDataList
        if (reelMap.has(key)) {
          const existing = reelMap.get(key);
          existing.initial_quantity += reel.initialQuantity ?? 0;
          if (reel.userData1) {
            existing.user_data_1 = reel.userData1;
          }
          if (reel.comments) {
            existing.comments = reel.comments;
          }
        } else {
          reelMap.set(key, {
            id: null, // Luôn null cho record mới
            reel_id: reel.reelID,
            part_number: `${reel.sapCode}V_${version}`,
            vendor: reel.vendor ?? "",
            lot: reel.lot ?? "",
            user_data_1: reel.userData1 ?? "",
            user_data_2: reel.userData2 ?? "",
            user_data_3: reel.userData3 ?? "",
            user_data_4: reel.userData4 ?? "",
            user_data_5: reel.userData5 ?? "",
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

    console.log(`New Pallets to save: ${pallet_infor_detail.length}`);
    console.log(`New Boxes to save: ${warehouse_note_info_detail.length}`);
    console.log("=== END DEBUG ===");

    // Kiểm tra nếu không có gì mới để lưu
    if (
      pallet_infor_detail.length === 0 &&
      warehouse_note_info_detail.length === 0
    ) {
      this.snackBar.open(
        "Không có dữ liệu mới để lưu. Tất cả thùng và pallet đã tồn tại.",
        "Đóng",
        {
          duration: 3000,
          panelClass: ["snackbar-info"],
        },
      );
      return;
    }

    // Payload tổng
    const payload = {
      pallet_infor_detail,
      warehouse_note_info_detail,
    };

    console.log("Final Payload to save:", payload);

    this.planningService.saveCombined(maLenhSanXuatId, payload).subscribe({
      next: (res) => {
        console.log("Lưu thành công:", res);

        // CẬP NHẬT LẠI cache local sau khi lưu thành công
        // Thêm các serial mới vào existing sets
        pallet_infor_detail.forEach((p) => {
          if (p.serial_pallet) {
            existingPalletSerials.add(p.serial_pallet);
            // Cập nhật vào this.pallets để lần sau check đúng
            if (!this.pallets) {
              this.pallets = [];
            }
            this.pallets.push(p);
          }
        });

        warehouse_note_info_detail.forEach((b) => {
          if (b.reel_id) {
            existingBoxSerials.add(b.reel_id);
            // Cập nhật vào this.details để lần sau check đúng
            if (!this.details) {
              this.details = [];
            }
            this.details.push(b);
          }
        });
        this.saveWarehouseNotes();

        this.snackBar.open(
          `Lưu thành công: ${pallet_infor_detail.length} pallet, ${warehouse_note_info_detail.length} thùng/tem`,
          "Đóng",
          {
            duration: 3000,
            panelClass: ["snackbar-success"],
          },
        );
      },
      error: (err) => {
        console.error("Lỗi khi lưu:", err);
        this.snackBar.open(
          `Lỗi khi lưu: ${err.error?.message || err.message}`,
          "Đóng",
          {
            duration: 4000,
            panelClass: ["snackbar-error"],
          },
        );
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
    console.log(" Searching for:", {
      branchCodeRaw,
      teamCodeRaw,
    });

    // ===== DUYỆT QUA TẤT CẢ WORKSHOP =====
    for (const ws of workshops) {
      console.log("Checking workshop:", ws.workshop_code);

      for (const br of ws.branchs) {
        console.log("Checking branch:", br.branch_code, "vs", branchCodeRaw);

        // ===== SO SÁNH BRANCH CODE (trim để tránh lỗi space) =====
        if (br.branch_code?.trim() === branchCodeRaw?.trim()) {
          console.log("Found matching branch!");

          let toCode: string | undefined;

          // ===== TÌM TỔ TRONG BRANCH NÀY =====
          if (teamCodeRaw) {
            console.log(" Searching for team:", teamCodeRaw);
            console.log(" Available teams:", br.production_teams);

            const team = br.production_teams?.find((t: any) => {
              const nameMatch =
                t.production_team_name?.trim() === teamCodeRaw?.trim();
              const codeMatch =
                t.production_team_code?.trim() === teamCodeRaw?.trim();

              console.log("Team check:", {
                teamName: t.production_team_name,
                teamCode: t.production_team_code,
                nameMatch,
                codeMatch,
              });

              return nameMatch || codeMatch;
            });

            if (team) {
              toCode = team.production_team_code;
              console.log("Found matching team:", toCode);
            } else {
              console.log("Team not found, using raw value");
              toCode = teamCodeRaw; // Fallback: dùng giá trị thô
            }
          }

          // ===== RETURN KẾT QUẢ =====
          const result = {
            xuong: ws.workshop_code,
            nganh: br.branch_code,
            to: toCode ?? teamCodeRaw,
          };

          console.log("Returning:", result);
          return result;
        }
      }
    }

    console.log(" No match found in hierarchy, using raw values");

    return {
      xuong: undefined,
      nganh: branchCodeRaw,
      to: teamCodeRaw,
    };
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
  private async handleTemBTPCreation(
    data: BoxFormData,
    woId: string,
  ): Promise<void> {
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
    let lotNumber = "";
    try {
      const res: any = await this.planningService.search(woId).toPromise();

      const item =
        res?.content?.find((c: any) => c.woId === woId) ?? res?.content?.[0];

      lotNumber = item?.lotNumber ?? "";
    } catch (error) {
      console.error("Error fetching lotNumber:", error);
      lotNumber = "";
    }

    const productionOrder =
      this.productionOrders.find((po) => po.maWO === woId) ??
      this.productionOrders[0];

    // Tính tổng số lượng SAU KHI tạo xong tất cả thùng
    const totalQuantityAfterCreation =
      this.reelDataList.reduce((sum, r) => sum + r.initialQuantity, 0) +
      data.soLuongThung * data.soLuongTrongThung;

    for (let i = 0; i < data.soLuongThung; i++) {
      const reelID = this.generateReelID(now, productionOrder.maSAP, counter);

      const existing = this.reelDataList.find((r) => r.reelID === reelID);

      if (existing) {
        existing.initialQuantity += data.soLuongTrongThung;
        existing.userData1 = totalQuantityAfterCreation.toString(); // Dùng giá trị tính trước
        console.log(`Cộng dồn vào reelID ${reelID}:`, existing);
      } else {
        const reelData: ReelData = {
          reelID,
          partNumber: `${productionOrder.maSAP}V_${version}`,
          vendor: productionOrder.maLenhSanXuat || "",
          lot: lotNumber,
          userData5: productionOrder.maLenhSanXuat || "",
          initialQuantity: data.soLuongTrongThung,
          sapCode: productionOrder.maSAP || "",
          trangThai: "Active",
          storageUnit: "",
          comments: data.comments ?? "",
          userData1: totalQuantityAfterCreation.toString(), // Dùng giá trị tính trước
          userData2: data.rank ?? "",
          userData3: productionOrder.tenHangHoa,
          userData4: productionOrder.maSAP || "",
          TPNK: data.TPNK ?? "",
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

    // Cập nhật tổng số lượng sau khi tạo xong
    this.updateProductionOrderTotalBTP();

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
  private updateProductionOrderTotalBTP(): void {
    const total = this.reelDataList.reduce(
      (sum, box) => sum + box.initialQuantity,
      0,
    );

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
        tongSoThung: data.soLuongThungThucTe,
        thungScan: data.soLuongThungScan,
        qrCode: palletCode,
        tienDoScan: 0,
        sucChua: `${data.soLuongThungThucTe} thùng`,
      });
    }

    const newPallet: PalletItem = {
      stt: index,
      tenSanPham: data.tenSanPham,
      noSKU: data.noSKU,
      createdAt: new Date().toISOString(),
      tongPallet: data.soLuongPallet,
      tongSlSp: data.qtyPerBox,
      tongSoThung: data.soLuongThungThucTe,
      thungScan: data.soLuongThungScan,
      khachHang: data.khachHang,
      poNumber: data.poNumber,
      dateCode: data.dateCode,
      qdsx: data.qdsx,
      nguoiKiemTra: data.nguoiKiemTra,
      ketQuaKiemTra: data.ketQuaKiemTra,
      trangThaiIn: false,
      serialPallet: this.generatePalletCode(index, 1),
      subItems,
      nganh: this.productionOrders[0]?.nganh ?? "",
      to: this.productionOrders[0]?.to ?? "",
      note: data.note,
      maLenhSanXuatId: this.maLenhSanXuatId,
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

  private removeBoxFromFE(index: number): void {
    this.boxItems = this.boxItems.filter((_, i) => i !== index);

    // Cập nhật lại STT
    this.boxItems = this.boxItems.map((b, idx) => ({
      ...b,
      stt: idx + 1,
    }));

    // Cập nhật tổng số lượng
    this.updateProductionOrderTotal();

    console.log(`Đã xóa thùng tại index ${index} khỏi FE`);
  }

  private removePalletSubFromFE(
    palletItem: PalletItem,
    subIndex: number,
  ): void {
    palletItem.subItems = palletItem.subItems.filter((_, i) => i !== subIndex);

    // Cập nhật lại STT
    palletItem.subItems = palletItem.subItems.map((sub, idx) => ({
      ...sub,
      stt: idx + 1,
    }));

    // Cập nhật tổng pallet
    palletItem.tongPallet = palletItem.subItems.length;

    // Nếu không còn pallet con nào → xóa luôn palletItem cha
    if (palletItem.subItems.length === 0) {
      this.palletItems = this.palletItems.filter((p) => p !== palletItem);
    }

    console.log(`Đã xóa pallet con tại index ${subIndex} khỏi FE`);
  }
  private removePalletGroupFromFE(palletItem: PalletItem): void {
    this.palletItems = this.palletItems.filter((p) => p !== palletItem);
    console.log(`Đã xóa pallet group khỏi FE`);
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
      const key = d.created_at;

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
        id: d.id,
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
      const key = detail.updated_at;

      if (!grouped[key]) {
        grouped[key] = {
          stt: Object.keys(grouped).length + 1,
          tenSanPham: this.productionOrders[0]?.tenHangHoa ?? "",
          noSKU: detail.item_no_sku ?? "",
          createdAt: detail.production_date ?? "",
          tongPallet: 0,
          tongSlSp: detail.quantity_per_box,
          tongSoThung: 0,
          khachHang: detail.customer_name ?? "",
          poNumber: detail.po_number ?? "",
          dateCode: detail.date_code ?? "",
          qdsx: detail.qdsx_no ?? "",
          nguoiKiemTra: detail.inspector_name ?? "",
          ketQuaKiemTra: detail.inspection_result ?? "",
          trangThaiIn: false,
          note: detail.note ?? "",
          serialPallet: "", // pallet cha không cần serial
          subItems: [],
          tienDoScan: 0,
        };
      }

      // Thêm pallet con vào subItems
      grouped[key].subItems.push({
        id: detail.id,
        stt: grouped[key].subItems.length + 1,
        maPallet: detail.serial_pallet,
        tongSoThung: detail.num_box_actual ?? 0,
        thungScan: detail.num_box_config ?? 1,
        qrCode: detail.serial_pallet,
        tienDoScan: detail.scan_progress ?? 0,
        sucChua: `${detail.num_box_actual ?? 0} thùng`,
      });

      // Cập nhật tổng số liệu
      // grouped[key].tongPallet++;
      // grouped[key].tongSoThung += detail.num_box_actual ?? 0;
      // grouped[key].tongSlSp += detail.quantity_per_box ?? 0;
    }

    return Object.values(grouped);
  }

  //  THÊM HÀM MAP CHO REEL DATA (Tem BTP)
  private mapDetailsToReelData(details: any[]): ReelData[] {
    return details.map((detail) => ({
      id: detail.id,
      reelID: detail.reel_id || "",
      partNumber: detail.part_number || "",
      vendor: detail.vendor || "",
      lot: detail.lot || "",
      userData1: detail.user_data_1 || "",
      userData2: detail.user_data_2 || "",
      userData3: detail.user_data_3 || "",
      userData4: detail.user_data_4 || "",
      userData5: detail.user_data_5 || "",
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
    // Validate dữ liệu
    if (!this.productionOrders || this.productionOrders.length === 0) {
      this.snackBar.open("Không có thông tin lệnh sản xuất", "Đóng", {
        duration: 3000,
      });
      return;
    }

    if (!this.palletItems || this.palletItems.length === 0) {
      this.snackBar.open("Chưa có thông tin pallet", "Đóng", {
        duration: 3000,
      });
      return;
    }

    if (!this.boxItems || this.boxItems.length === 0) {
      this.snackBar.open("Chưa có thông tin thùng", "Đóng", {
        duration: 3000,
      });
      return;
    }

    // Lấy thông tin từ productionOrder
    const currentOrder = this.productionOrders[0];

    // Lấy username người đăng nhập
    const currentUser = this.accountService.isAuthenticated()
      ? this.accountService["userIdentity"]?.login
      : "unknown";

    // Format ngày sản xuất (từ ISO sang DD/MM/YYYY)
    const formatDate = (isoDate: string): string => {
      if (!isoDate) {
        return "";
      }
      const date = new Date(isoDate);
      const day = String(date.getDate()).padStart(2, "0");
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const year = date.getFullYear();
      return `${day}/${month}/${year}`;
    };

    // Lấy lot number từ boxItems (nếu có)
    const lotNumber = this.boxItems[0]?.lotNumber || "";

    // Build detail array: flatten tất cả pallet con và map với box
    const detail: any[] = [];

    this.palletItems.forEach((palletGroup) => {
      palletGroup.subItems.forEach((palletSub) => {
        // Lấy danh sách box thuộc pallet này (nếu có mapping)
        // Giả sử bạn cần map pallet với box dựa vào logic nghiệp vụ
        // Ở đây tôi sẽ flatten tất cả box items

        this.boxItems.forEach((box) => {
          box.subItems.forEach((boxSub) => {
            detail.push({
              serial_pallet: palletSub.maPallet,
              box_code: boxSub.maThung,
              quantity: boxSub.soLuong,
              list_serial_items: "", // Có thể thêm logic nếu cần
            });
          });
        });
      });
    });

    // Nếu không có mapping cụ thể, có thể dùng logic đơn giản hơn
    // Ví dụ: mỗi pallet map với tất cả box
    if (detail.length === 0) {
      // Fallback: tạo detail từ tất cả pallet và box
      this.palletItems.forEach((palletGroup) => {
        palletGroup.subItems.forEach((palletSub) => {
          const totalBoxQty = this.boxItems.reduce(
            (sum, box) => sum + box.soLuongSp,
            0,
          );

          detail.push({
            serial_pallet: palletSub.maPallet,
            box_code: this.boxItems[0]?.subItems[0]?.maThung || "",
            quantity: totalBoxQty,
            list_serial_items: "",
          });
        });
      });
    }

    // Build payload
    const payload = {
      general_info: {
        po_number: this.palletItems[0]?.poNumber || "",
        client_id: this.palletItems[0]?.khachHang || "",
        inventory_code: currentOrder.maSAP || "",
        inventory_name: currentOrder.tenHangHoa || "",
        wo_code: currentOrder.maWO || "",
        lot_number: lotNumber,
        production_date: formatDate(this.palletItems[0]?.createdAt || ""),
        note: this.palletItems[0].note ?? "",
        create_by: currentUser,
        branch: currentOrder.nganh ?? "",
        production_team: currentOrder.to ?? "",
        production_decision_number: this.palletItems[0]?.qdsx || "",
        item_no_sku: this.palletItems[0]?.noSKU || "",
        approver: currentUser,
        destination_warehouse: 1,
        pallet_note_creation_session_id: this.maLenhSanXuatId ?? 1,
      },
      detail: detail,
    };

    console.log(" WMS Approval Payload:", payload);

    // Gọi API
    this.planningService.sendWmsApproval(payload).subscribe({
      next: (response) => {
        console.log("WMS Approval Success:", response);

        // Cập nhật trạng thái sang "Chờ duyệt"
        currentOrder.trangThai = "Chờ duyệt";

        this.snackBar.open("Gửi phê duyệt WMS thành công", "Đóng", {
          duration: 3000,
          panelClass: ["snackbar-success"],
        });
      },
      error: (err) => {
        console.error("WMS Approval Error:", err);
        this.snackBar.open(
          `Lỗi gửi phê duyệt WMS: ${err.error?.message || err.message}`,
          "Đóng",
          {
            duration: 4000,
            panelClass: ["snackbar-error"],
          },
        );
      },
    });
  }

  private sendApproval(): void {
    const currentUser = this.accountService.isAuthenticated()
      ? this.accountService["userIdentity"]?.login
      : "unknown";

    const currentOrder = this.productionOrders[0];
    if (!currentOrder) {
      this.snackBar.open("Không có dữ liệu lệnh sản xuất", "Đóng", {
        duration: 3000,
      });
      return;
    }

    // Chỉ cho gửi nếu trạng thái là "Bản nháp"
    if (currentOrder.trangThai !== "Bản nháp") {
      this.snackBar.open("Chỉ có bản nháp mới được gửi phê duyệt", "Đóng", {
        duration: 3000,
      });
      return;
    }

    // Cập nhật trạng thái sang "Chờ duyệt"
    currentOrder.trangThai = "Chờ duyệt";

    const payload: WarehouseNotePayload = {
      id: currentOrder.id,
      ma_lenh_san_xuat: currentOrder.maLenhSanXuat,
      sap_code: currentOrder.maSAP,
      sap_name: currentOrder.tenHangHoa,
      work_order_code: currentOrder.maWO,
      version: currentOrder.version,
      storage_code: currentOrder.maKhoNhap,
      total_quantity: currentOrder.tongSoLuong,
      create_by: currentUser ?? "",
      trang_thai: "Chờ duyệt", // ép trạng thái
      group_name: currentOrder.to,
      comment_2: "",
      approver_by: "",
      branch: currentOrder.nganh,
      product_type: currentOrder.loaiSanPham,
      destination_warehouse: 1,
    };

    if (!currentOrder.id) {
      this.snackBar.open("Lỗi: Không có ID lệnh sản xuất", "Đóng", {
        duration: 3000,
      });
      return;
    }

    this.planningService
      .updateWarehouseNote(currentOrder.id, payload)
      .subscribe({
        next: (res) => {
          const newId =
            res.warehouse_note_info?.id ?? res.id ?? currentOrder.id;
          this.maLenhSanXuatId = newId;
          currentOrder.id = newId;

          this.snackBar.open("Gửi phê duyệt thành công", "Đóng", {
            duration: 3000,
            panelClass: ["snackbar-success"],
          });
          this.router.navigate([`/warehouse-note-infos`]);
        },
        error: (err) => {
          console.error("Lỗi khi cập nhật trạng thái:", err);
          this.snackBar.open("Không thể gửi phê duyệt", "Đóng", {
            duration: 4000,
            panelClass: ["snackbar-error"],
          });
        },
      });
  }

  private loadMaKhoNhapOptionsTP(): void {
    this.planningService.getAreas().subscribe({
      next: (res: any) => {
        const areas = res.data ?? [];
        this.maKhoNhapOptionsTP = areas.map((a: any) => ({
          value: a.code,
          label: `${a.code} - ${a.name}`,
        }));
      },
      error: (err) => {
        console.error("Lỗi lấy danh sách area:", err);
        this.snackBar.open("Không lấy được danh sách kho nhập", "Đóng", {
          duration: 3000,
        });
      },
    });
  }

  //in tat ca
  private openPrintDialogFromAddNew(data: PrintPalletData[]): void {
    this.dialog.open(PrintPalletDialogComponent, {
      width: "100vw",
      height: "100vh",
      maxWidth: "100vw",
      maxHeight: "100vh",
      panelClass: "print-dialog-fullscreen",
      data: data, // Truyền array
      disableClose: false,
    });
  }
  //load tien do trong package
  private loadAllPalletProgressAsync(
    allPallets: PalletDetailData[],
  ): Promise<void> {
    const promises = allPallets.map((pallet) => {
      if (!pallet.serialPallet) {
        return Promise.resolve(); // Skip nếu không có serial
      }

      return new Promise<void>((resolve) => {
        this.planningService.getMappings(pallet.serialPallet).subscribe({
          next: (mappings) => {
            const successMappings = mappings.filter((m) => m.status === 1);

            // Chuẩn hóa scannedBoxes: chỉ lấy string hợp lệ, trim và loại trùng
            const scanned = successMappings
              .map((m) =>
                typeof m.serial_box === "string" ? m.serial_box.trim() : "",
              )
              .filter((s) => !!s); // bỏ rỗng/null

            pallet.tienDoScan = scanned.length;
            pallet.scannedBoxes = [...new Set(scanned)]; // loại trùng

            resolve();
          },
          error: (err) => {
            if (err.status === 404) {
              // 404 = chưa scan → OK
              pallet.tienDoScan = 0;
              pallet.scannedBoxes = [];
              console.log(` Pallet ${pallet.serialPallet}: chưa scan`);
            } else {
              console.error(` Error loading ${pallet.serialPallet}:`, err);
              pallet.tienDoScan = 0;
              pallet.scannedBoxes = [];
            }
            resolve(); // Vẫn resolve để không block
          },
        });
      });
    });

    // Đợi TẤT CẢ promises hoàn thành
    return Promise.all(promises).then(() => {
      console.log(" Đã load xong tiến độ cho", allPallets.length, "pallet");
    });
  }
  private loadPalletProgress(
    item: PalletBoxItem,
    maLenhSanXuatId: number,
  ): void {
    if (!item.maPallet) {
      console.warn(" Missing maPallet, cannot load progress");
      return;
    }

    // Set loading state
    this.isLoadingProgress[item.maPallet] = true;

    // Gọi API lấy danh sách box đã scan cho pallet này
    this.planningService
      .getMappings(item.maPallet)
      .pipe(
        finalize(() => {
          this.isLoadingProgress[item.maPallet] = false;
        }),
      )
      .subscribe({
        next: (mappings) => {
          // Lọc ra các box có status = 1 (success)
          const successMappings = mappings.filter((m) => m.status === 1);

          // Update tiến độ
          item.tienDoScan = successMappings.length;
          item.scannedBoxes = successMappings.map((m) => m.serial_box);
        },
        error: (error) => {
          // 404 = chưa có box nào được scan → OK
          if (error.status === 404) {
            console.log(` Pallet ${item.maPallet}: Chưa có box nào được scan`);
            item.tienDoScan = 0;
            item.scannedBoxes = [];
          } else {
            console.error(
              `Error loading progress for ${item.maPallet}:`,
              error,
            );
            item.tienDoScan = 0;
          }
        },
      });
  }
  private async refreshPalletItemsBeforePrint(): Promise<void> {
    console.log("Refreshing pallet items before print...");

    const promises = this.palletItems.map(async (pallet) => {
      const subPromises = (pallet.subItems ?? []).map(async (sub) => {
        try {
          // getMappings có thể trả về undefined → fallback []
          const mappings: SerialBoxPalletMapping[] =
            (await this.planningService
              .getMappings(sub.maPallet)
              .toPromise()) ?? [];

          const successMappings = mappings.filter((m) => m.status === 1);

          sub.scannedBoxes = successMappings.map((m) => m.serial_box);

          console.log(
            `Refreshed ${sub.maPallet}: ${sub.scannedBoxes?.length ?? 0} boxes`,
          );
        } catch (error) {
          console.error(`Error refreshing ${sub.maPallet}`, error);
          sub.scannedBoxes = [];
        }
      });

      await Promise.all(subPromises);
    });

    await Promise.all(promises);
    console.log("All pallet items refreshed");
  }
}
