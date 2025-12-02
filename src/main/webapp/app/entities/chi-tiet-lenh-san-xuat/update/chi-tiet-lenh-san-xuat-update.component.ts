import { DATE_TIME_FORMAT } from "app/config/input.constants";
import dayjs from "dayjs/esm";
import { ApplicationConfigService } from "./../../../core/config/application-config.service";
import { Component, Input, OnInit } from "@angular/core";
import { HttpResponse, HttpClient } from "@angular/common/http";
import { UntypedFormBuilder, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { Observable } from "rxjs";
import { finalize } from "rxjs/operators";
import { IChiTietLenhSanXuat } from "../chi-tiet-lenh-san-xuat.model";
import { ChiTietLenhSanXuatService } from "../service/chi-tiet-lenh-san-xuat.service";
import {
  ILenhSanXuat,
  LenhSanXuat,
} from "app/entities/lenh-san-xuat/lenh-san-xuat.model";
import { LenhSanXuatService } from "app/entities/lenh-san-xuat/service/lenh-san-xuat.service";
import { AccountService } from "app/core/auth/account.service";
import { Account } from "app/core/auth/account.model";
import { PageEvent } from "@angular/material/paginator";
@Component({
  selector: "jhi-chi-tiet-lenh-san-xuat-update",
  templateUrl: "./chi-tiet-lenh-san-xuat-update.component.html",
  styleUrls: ["./chi-tiet-lenh-san-xuat-update.component.css"],
  standalone: false,
})
export class ChiTietLenhSanXuatUpdateComponent implements OnInit {
  resourceUrl = this.applicationConfigService.getEndpointFor(
    "/api/warehouse-stamp-info-details",
  );
  resourceUrlUpdate = this.applicationConfigService.getEndpointFor(
    "/api/warehouse-note-info-details",
  );
  resourceUrlWarehouseNoteInfo = this.applicationConfigService.getEndpointFor(
    "/api/warehouse-note-infos",
  );
  selectedAllResult?: boolean;
  selectedAll = 1;
  checkedList: any;
  @Input() selectedItems: { checked: string }[] = [];

  @Input() itemPerPage = 10;
  @Input() itemPerPage3 = 5;
  @Input() itemPerPage2 = 10;

  page?: number;
  page2?: number;
  page3?: number;
  showScanInput = false;

  totalItems = 0;
  pageSize = 10;
  pageIndex = 0;
  sum = 0;

  isSaving = false;
  predicate!: string;
  ascending!: boolean;

  selectedStatus = "";

  changeStatus: {
    id: number;
    totalQuantity: string;
    timeUpdate: dayjs.Dayjs;
    trangThai: string;
  } = {
    id: 0,
    totalQuantity: "",
    timeUpdate: dayjs().startOf("second"),
    trangThai: "",
  };
  isAllChecked: boolean = false;
  chiTietLenhSanXuats: any[] = [];
  //tạo danh sách lệnh sản xuất ở trạng thái active
  chiTietLenhSanXuatActive: any[] = [];
  //tạo danh sách lệnh sản xuất không có trong danh sách
  chiTietLenhSanXuatNotList: any[] = [];
  // tạo biến lưu trữ giá trị scan
  @Input() scanResults = "";
  @Input() reelID = "";
  @Input() storageUnit = "";
  index = 0; // lưu thông tin index của phần tử vừa scan được
  // tạo biến check sự tồn tại trong danh sách
  isExisted = false;
  //Đóng mở popup
  popupMove = false;
  scanValue: any = {
    id: 0,
    reelID: "null",
    partNumber: "null",
    vendor: "null",
    lot: "null",
    userData1: "null",
    userData2: "null",
    userData3: "null",
    userData4: "null",
    userData5: 0,
    initialQuantity: 0,
    msdLevel: "",
    msdInitialFloorTime: "",
    msdBagSealDate: "",
    marketUsage: "",
    quantityOverride: 0,
    shelfTime: "",
    spMaterialName: "",
    warningLimit: "",
    maximumLimit: "",
    comments: "",
    warmupTime: "",
    storageUnit: "null",
    subStorageUnit: "",
    locationOverride: "",
    expirationDate: "null",
    manufacturingDate: "null",
    partClass: "",
    sapCode: "null",
    trangThai: "Inactive",
    checked: 1,
  };
  lenhSanXuatsSharedCollection: ILenhSanXuat[] = [];
  scanResult = this.fb.group({
    result: [],
  });

  countScan = 0;
  tienDoScan = 0;
  resultScanPerCent = "";
  //test popup move
  dataMove: {
    id: number;
    reelID: string;
    storageUnit: string;
    subStorageUnit: string;
  }[] = [];
  count = 0;
  //----------------
  editForm = this.fb.group({
    id: [],
    maLenhSanXuat: [null, [Validators.required]],
    sapCode: [],
    sapName: [],
    workOrderCode: [],
    version: [],
    storageCode: [],
    totalQuantity: [],
    createBy: [],
    entryTime: [],
    timeUpdate: [],
    trangThai: [],
    comment: [],
    groupName: [],
    comment2: [],
  });

  initialQuantity: any;
  account: Account | null = null;
  constructor(
    protected chiTietLenhSanXuatService: ChiTietLenhSanXuatService,
    protected lenhSanXuatService: LenhSanXuatService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder,
    protected applicationConfigService: ApplicationConfigService,
    protected http: HttpClient,
    protected accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe((account) => {
      this.account = account;
    });
    // bắt sự kiện scan
    this.scanResult.valueChanges.subscribe((data) => {
      // console.log('data: ');
    });

    this.activatedRoute.data.subscribe(({ lenhSanXuat }) => {
      console.log("[DEBUG UPDATE] Route data received:", lenhSanXuat);
      console.log(
        "[DEBUG UPDATE] lenhSanXuat keys:",
        lenhSanXuat ? Object.keys(lenhSanXuat) : "null",
      );

      // Check if we received data from the resolver with warehouse_note_info structure
      if (!lenhSanXuat || !lenhSanXuat.warehouse_note_info) {
        console.error("[ERROR UPDATE] Invalid data structure from resolver");
        alert(
          "Lỗi: Không tìm thấy dữ liệu lệnh sản xuất. Vui lòng kiểm tra lại đường dẫn.",
        );
        return;
      }

      const warehouseNoteInfo = lenhSanXuat.warehouse_note_info;
      const warehouseNoteInfoDetails =
        lenhSanXuat.warehouse_note_info_details || [];

      console.log("[DEBUG UPDATE] warehouseNoteInfo:", warehouseNoteInfo);
      console.log(
        "[DEBUG UPDATE] warehouseNoteInfoDetails count:",
        warehouseNoteInfoDetails.length,
      );

      const today = dayjs().startOf("second");

      // Map warehouse_note_info to lenhSanXuat structure
      const mappedLenhSanXuat: ILenhSanXuat = {
        id: warehouseNoteInfo.id,
        maLenhSanXuat: warehouseNoteInfo.ma_lenh_san_xuat,
        sapCode: warehouseNoteInfo.sap_code,
        sapName: warehouseNoteInfo.sap_name,
        workOrderCode: warehouseNoteInfo.work_order_code,
        version: warehouseNoteInfo.version,
        storageCode: warehouseNoteInfo.storage_code,
        totalQuantity: warehouseNoteInfo.total_quantity,
        createBy: warehouseNoteInfo.create_by,
        entryTime: warehouseNoteInfo.entry_time
          ? dayjs(warehouseNoteInfo.entry_time)
          : undefined,
        timeUpdate: today,
        trangThai: warehouseNoteInfo.trang_thai,
        comment: warehouseNoteInfo.comment,
        groupName: warehouseNoteInfo.group_name,
        comment2: warehouseNoteInfo.comment2,
      };

      console.log("[DEBUG UPDATE] mappedLenhSanXuat:", mappedLenhSanXuat);

      this.changeStatus.id = mappedLenhSanXuat.id!;
      this.changeStatus.totalQuantity = String(
        mappedLenhSanXuat.totalQuantity ?? 0,
      );

      // Update form with mapped data
      this.updateForm(mappedLenhSanXuat);

      console.log(
        "[DEBUG UPDATE] Form values after updateForm:",
        this.editForm.value,
      );

      // Use warehouse_note_info_details directly
      this.chiTietLenhSanXuats = warehouseNoteInfoDetails;

      //lấy danh sách chi tiết lsx ở trạng thái active
      this.chiTietLenhSanXuatActive = this.chiTietLenhSanXuats.filter(
        (a) => a.trang_thai === "ACTIVE",
      );
      this.itemPerPage = this.chiTietLenhSanXuatActive.length;

      console.log(
        "[DEBUG UPDATE] chiTietLenhSanXuatActive count:",
        this.chiTietLenhSanXuatActive.length,
      );

      // sắp xếp danh sách
      this.chiTietLenhSanXuatActive.sort(function (a, b) {
        if (
          a.checked !== undefined &&
          a.checked !== null &&
          b.checked !== undefined &&
          b.checked !== null
        ) {
          return a.checked - b.checked;
        }
        return 0;
      });

      //lấy danh sách chi tiết lsx không có trong danh sách
      this.chiTietLenhSanXuatNotList = this.chiTietLenhSanXuats.filter(
        (a) => a.trang_thai === "not list",
      );

      console.log(
        "[DEBUG UPDATE] chiTietLenhSanXuatNotList count:",
        this.chiTietLenhSanXuatNotList.length,
      );

      // this.loadRelationshipsOptions();
      setTimeout(() => {
        const input = document.getElementById("scan");
        // // console.log('test: ');
        if (input) {
          input.focus();
        }
      }, 100);
    });
  }
  //cập nhật từng mã kho panacim
  test(test: string): void {
    test = "";
    // console.log('test:', test);
  }
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  //Cập nhật tất cả mã kho panacim
  changeAllStorageUnit(): void {
    const positionSL = this.storageUnit.indexOf("-SL");
    // console.log('check SL', positionSL);
    const storageUnit = this.storageUnit.slice(2, positionSL);
    const subsStorageUnit = this.storageUnit.slice(positionSL + 3);
    // // console.log("test: ", this.storageUnit)
    for (let i = 0; i < this.chiTietLenhSanXuats.length; i++) {
      if (positionSL === -1) {
        this.chiTietLenhSanXuats[i].storageUnit = this.storageUnit.slice(2);
      } else {
        this.chiTietLenhSanXuats[i].storageUnit = storageUnit;
        this.chiTietLenhSanXuats[i].subStorageUnit = subsStorageUnit;
      }
    }
    this.storageUnit = "";
  }

  onCheckUnCheckSelectAll(): void {
    if (this.selectedAllResult === true) {
      this.selectedAll = 1;
      for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
        if (this.chiTietLenhSanXuatActive[i].trang_thai === "ACTIVE") {
          this.chiTietLenhSanXuatActive[i].checked = this.selectedAll;
        } else {
          this.chiTietLenhSanXuatActive[i].checked = 0;
        }
      }
    } else {
      this.selectedAll = 0;
      for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
        this.chiTietLenhSanXuatActive[i].checked = this.selectedAll;
      }
    }
    // // duyet qua mang chiTietLenhSanXuatActive va cap nhat checked cua moi ptu trong mang checkedList dua tren onSelected()
    // for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
    //   this.chiTietLenhSanXuatActive[i].checked = this.selectedAll;
    // }
    // sau khi cap nhat checkedList goi ham getCheckItemList
    // this.getCheckItemList();
  }

  onCheckAll(): void {
    this.selectedAll = 1;
    for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
      if (this.chiTietLenhSanXuatActive[i].trang_thai === "ACTIVE") {
        this.chiTietLenhSanXuatActive[i].checked = this.selectedAll;
      } else {
        this.chiTietLenhSanXuatActive[i].checked = 0;
      }
    }
  }

  onUnCheckAll(): void {
    this.selectedAll = 0;
    for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
      this.chiTietLenhSanXuatActive[i].checked = this.selectedAll;
    }
  }
  // check tung tem 1
  onSelected(id: number, checker: boolean): void {
    //gán giá trị cho các phần tử đã chọn
    for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
      if (i === id) {
        if (checker === true) {
          this.chiTietLenhSanXuatActive[i].checked = 1;
        } else {
          this.chiTietLenhSanXuatActive[i].checked = 0;
        }
      }
    }
    // console.log('id: ', id);
    // console.log('check: ', checker);
    // console.log(this.chiTietLenhSanXuats);
  }

  // cap nhat vao mang checkedList cac ptu chiTietLenhSanXuatActive co thuoc tinh checked
  // duyet qua mang chiTietLenhSanXuatActive, neu 1 ptu duoc chon se them vao checkedList
  getCheckItemList(): void {
    this.checkedList = [];
    for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
      if (this.chiTietLenhSanXuatActive[i].checked) {
        this.checkedList.push(this.chiTietLenhSanXuatActive[i]);
      }
    }
  }

  previousState(): void {
    window.history.back();
  }

  changeQuantity(): void {
    this.sum = 0;
    for (let i = 0; i < this.chiTietLenhSanXuats.length; i++) {
      if (this.chiTietLenhSanXuats[i].trang_thai === "ACTIVE") {
        const result = this.chiTietLenhSanXuats[i].initial_quantity;
        if (result) {
          this.sum += Number(result);
        }
      }
    }
    this.editForm.patchValue({
      totalQuantity: this.sum,
    });
  }

  // ================================  các button ======================================================
  pheDuyetTem(): void {
    this.editForm.patchValue({
      trangThai: "Đã phê duyệt",
      timeUpdate: dayjs().startOf("second").format(DATE_TIME_FORMAT),
    });

    // Create DTO with only necessary fields to avoid data loss
    const updateDTO = {
      id: this.editForm.get(["id"])!.value,
      maLenhSanXuat: this.editForm.get(["maLenhSanXuat"])!.value,
      sapCode: this.editForm.get(["sapCode"])!.value,
      sapName: this.editForm.get(["sapName"])!.value,
      workOrderCode: this.editForm.get(["workOrderCode"])!.value,
      version: this.editForm.get(["version"])!.value,
      storageCode: this.editForm.get(["storageCode"])!.value,
      totalQuantity: this.editForm.get(["totalQuantity"])!.value,
      createBy: this.editForm.get(["createBy"])!.value,
      entryTime: this.editForm.get(["entryTime"])!.value,
      trangThai: this.editForm.get(["trangThai"])!.value,
      comment: this.editForm.get(["comment"])!.value,
      timeUpdate: dayjs().toISOString(),
      groupName: this.editForm.get(["groupName"])!.value,
      comment2: this.editForm.get(["comment2"])!.value,
    };

    // Update warehouse_note_info status
    this.http
      .put<any>(
        `${this.resourceUrlWarehouseNoteInfo}/${updateDTO.id}`,
        updateDTO,
      )
      .subscribe(() => {
        // Update warehouse_note_info_details
        this.http
          .post<any>(this.resourceUrlUpdate, this.chiTietLenhSanXuats)
          .subscribe(() => {
            alert("Phê duyệt thành công");
            this.previousState();
          });
      });
  }

  khoHuyStatus(): void {
    this.editForm.patchValue({
      trangThai: "Kho hủy",
      timeUpdate: dayjs().startOf("second").format(DATE_TIME_FORMAT),
    });

    // Create DTO with only necessary fields to avoid data loss
    const updateDTO = {
      id: this.editForm.get(["id"])!.value,
      maLenhSanXuat: this.editForm.get(["maLenhSanXuat"])!.value,
      sapCode: this.editForm.get(["sapCode"])!.value,
      sapName: this.editForm.get(["sapName"])!.value,
      workOrderCode: this.editForm.get(["workOrderCode"])!.value,
      version: this.editForm.get(["version"])!.value,
      storageCode: this.editForm.get(["storageCode"])!.value,
      totalQuantity: this.editForm.get(["totalQuantity"])!.value,
      createBy: this.editForm.get(["createBy"])!.value,
      entryTime: this.editForm.get(["entryTime"])!.value,
      trangThai: this.editForm.get(["trangThai"])!.value,
      comment: this.editForm.get(["comment"])!.value,
      timeUpdate: dayjs().toISOString(),
      groupName: this.editForm.get(["groupName"])!.value,
      comment2: this.editForm.get(["comment2"])!.value,
    };

    // Update warehouse_note_info status
    this.http
      .put<any>(
        `${this.resourceUrlWarehouseNoteInfo}/${updateDTO.id}`,
        updateDTO,
      )
      .subscribe(() => {
        // Update warehouse_note_info_details
        this.http
          .post<any>(this.resourceUrlUpdate, this.chiTietLenhSanXuats)
          .subscribe(() => {
            alert("Kho hủy thành công");
            this.previousState();
          });
      });
  }

  khoTuChoiStatus(): void {
    this.editForm.patchValue({
      trangThai: "Từ chối",
      timeUpdate: dayjs().startOf("second").format(DATE_TIME_FORMAT),
    });

    // Create DTO with only necessary fields to avoid data loss
    const updateDTO = {
      id: this.editForm.get(["id"])!.value,
      maLenhSanXuat: this.editForm.get(["maLenhSanXuat"])!.value,
      sapCode: this.editForm.get(["sapCode"])!.value,
      sapName: this.editForm.get(["sapName"])!.value,
      workOrderCode: this.editForm.get(["workOrderCode"])!.value,
      version: this.editForm.get(["version"])!.value,
      storageCode: this.editForm.get(["storageCode"])!.value,
      totalQuantity: this.editForm.get(["totalQuantity"])!.value,
      createBy: this.editForm.get(["createBy"])!.value,
      entryTime: this.editForm.get(["entryTime"])!.value,
      trangThai: this.editForm.get(["trangThai"])!.value,
      comment: this.editForm.get(["comment"])!.value,
      timeUpdate: dayjs().toISOString(),
      groupName: this.editForm.get(["groupName"])!.value,
      comment2: this.editForm.get(["comment2"])!.value,
    };

    // Update warehouse_note_info status
    this.http
      .put<any>(
        `${this.resourceUrlWarehouseNoteInfo}/${updateDTO.id}`,
        updateDTO,
      )
      .subscribe(() => {
        // Update warehouse_note_info_details
        this.http
          .post<any>(this.resourceUrlUpdate, this.chiTietLenhSanXuats)
          .subscribe(() => {
            alert("Từ chối thành công");
            this.previousState();
          });
      });
  }
  //================================ chức năng scan mã kho panacim =========================================
  setStorageUnit(storageUnit: string): void {
    storageUnit = storageUnit.slice(2);
    // console.log('test: ', storageUnit);
  }

  save(): void {
    this.isSaving = true;
    this.editForm.patchValue({
      timeUpdate: dayjs().startOf("second").format(DATE_TIME_FORMAT),
    });

    const id = this.editForm.get(["id"])!.value;
    if (id !== undefined && id !== null) {
      // Create DTO with only necessary fields to avoid data loss
      const updateDTO = {
        id: id,
        maLenhSanXuat: this.editForm.get(["maLenhSanXuat"])!.value,
        sapCode: this.editForm.get(["sapCode"])!.value,
        sapName: this.editForm.get(["sapName"])!.value,
        workOrderCode: this.editForm.get(["workOrderCode"])!.value,
        version: this.editForm.get(["version"])!.value,
        storageCode: this.editForm.get(["storageCode"])!.value,
        totalQuantity: this.editForm.get(["totalQuantity"])!.value,
        createBy: this.editForm.get(["createBy"])!.value,
        entryTime: this.editForm.get(["entryTime"])!.value,
        trangThai: this.editForm.get(["trangThai"])!.value,
        comment: this.editForm.get(["comment"])!.value,
        timeUpdate: dayjs().toISOString(),
        groupName: this.editForm.get(["groupName"])!.value,
        comment2: this.editForm.get(["comment2"])!.value,
      };

      // Update warehouse_note_info
      this.http
        .put<any>(`${this.resourceUrlWarehouseNoteInfo}/${id}`, updateDTO)
        .subscribe(() => {
          // Update warehouse_note_info_details
          this.http
            .post<any>(this.resourceUrlUpdate, this.chiTietLenhSanXuats)
            .subscribe(() => {
              this.isSaving = false;
              alert("cập nhật chi tiết lệnh sản xuất thành công!");
              window.location.reload();
            });
        });
    } else {
      const lenhSanXuat = this.createFromForm();
      this.subscribeToSaveResponse(this.lenhSanXuatService.create(lenhSanXuat));
    }
  }

  trackLenhSanXuatById(_index: number, item: ILenhSanXuat): number {
    return item.id!;
  }

  trackId(_index: number, item: IChiTietLenhSanXuat): number {
    return item.id!;
  }

  subscribeToSaveResponse(
    result: Observable<HttpResponse<ILenhSanXuat>>,
  ): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  onSaveSuccess(): void {
    // this.previousState();
  }

  onSaveError(): void {
    // Api for inheritance.
  }

  onSaveFinalize(): void {
    this.isSaving = false;
  }

  updateForm(lenhSanXuat: ILenhSanXuat): void {
    this.editForm.patchValue({
      id: lenhSanXuat.id,
      maLenhSanXuat: lenhSanXuat.maLenhSanXuat,
      sapCode: lenhSanXuat.sapCode,
      sapName: lenhSanXuat.sapName,
      workOrderCode: lenhSanXuat.workOrderCode,
      version: lenhSanXuat.version,
      storageCode: lenhSanXuat.storageCode,
      totalQuantity: lenhSanXuat.totalQuantity,
      createBy: lenhSanXuat.createBy,
      entryTime: lenhSanXuat.entryTime
        ? lenhSanXuat.entryTime.format(DATE_TIME_FORMAT)
        : null,
      timeUpdate: lenhSanXuat.timeUpdate
        ? lenhSanXuat.timeUpdate.format(DATE_TIME_FORMAT)
        : null,
      trangThai: lenhSanXuat.trangThai,
      comment: lenhSanXuat.comment,
      groupName: lenhSanXuat.groupName,
      comment2: lenhSanXuat.comment2,
    });
  }
  // chuyển hướng con trỏ
  setInputValue(): void {
    this.isExisted = false;
    const input = document.getElementById("scan");
    if (input) {
      input.focus();
    }
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
      setTimeout(() => {
        this.sortList();
      }, 1000);
    });
  }
  playAlertSoundSuccess(): any {
    return new Promise<void>((resolve) => {
      const audio = new Audio();
      audio.src = "../../../content/images/successed-295058.mp3";
      audio.load();
      audio.play();
      audio.onended = () => {
        resolve();
      };
      setTimeout(() => {
        this.sortList();
      }, 1000);
    });
  }
  // bắt sự kiện scan
  async catchScanEvent(): Promise<any> {
    this.scanValue = {};
    const test = this.scanResults.split("#");
    for (let i = 0; i < test.length; i++) {
      if (i === 0) {
        this.scanValue.reelID = test[0];
      }
      if (i === 1) {
        this.scanValue.partNumber = test[i];
      }
      if (i === 2) {
        this.scanValue.vendor = test[i];
      }
      if (i === 3) {
        this.scanValue.lot = test[i];
      }
      if (i === 4) {
        this.scanValue.userData1 = test[i];
      }
      if (i === 5) {
        this.scanValue.userData2 = test[i];
      }
      if (i === 6) {
        test[i] = "null";
        this.scanValue.userData3 = test[i];
      }
      if (i === 7) {
        this.scanValue.userData4 = test[i];
      }
      if (i === 8) {
        this.scanValue.userData5 = Number(test[i]);
      }
      if (i === 9) {
        this.scanValue.initialQuantity = Number(test[i]);
      }
      if (i === 10) {
        this.scanValue.quantityOverride = Number(test[i]);
      }
      if (i === 11) {
        this.scanValue.storageUnit = test[i];
      }
      if (i === 12) {
        this.scanValue.expirationDate = test[i];
      }
      if (i === 13) {
        this.scanValue.manufacturingDate = test[i];
      }
      if (i === 14) {
        this.scanValue.sapCode = test[i];
      }
    }

    this.isExisted = false;
    for (let i = 0; i < this.chiTietLenhSanXuats.length; i++) {
      if (
        this.scanValue.reelID === this.chiTietLenhSanXuats[i].reel_id &&
        this.chiTietLenhSanXuats[i].trang_thai === "ACTIVE"
      ) {
        this.isExisted = true;
        this.chiTietLenhSanXuats[i].checked = 1;
        this.countScan++;
        this.tienDoScan =
          (this.countScan / this.chiTietLenhSanXuatActive.length) * 100;
        this.resultScanPerCent = this.tienDoScan.toFixed(0);
        this.alertTimeout("Đã tìm thấy tem trong danh sách lệnh", 1000);
        await this.playAlertSoundSuccess();
        return; // Dừng tiến trình nếu đã xử lý
      } else if (
        this.scanValue.reelID === this.chiTietLenhSanXuats[i].reel_id &&
        this.chiTietLenhSanXuats[i].trang_thai === "INACTIVE"
      ) {
        this.isExisted = true;
        this.chiTietLenhSanXuats[i].checked = 1;
        this.alertTimeout("Tem đang ở trạng thái Inactive", 1000);
        await this.playAlertSound();
        return;
      } else if (
        this.scanValue.reelID === this.chiTietLenhSanXuats[i].reel_id &&
        this.chiTietLenhSanXuats[i].trang_thai === "not list"
      ) {
        this.isExisted = true;
        this.chiTietLenhSanXuats[i].checked = 1;
        this.alertTimeout("Tem đang ở trạng thái not list", 1000);
        await this.playAlertSound();
        return;
      }
    }

    // Nếu không nằm trong danh sách thì xử lý luôn và dừng tiến trình
    if (this.isExisted === false) {
      const item: any = {
        id: 0,
        reelID: this.scanValue.reelID,
        partNumber: this.scanValue.partNumber,
        vendor: this.scanValue.vendor,
        lot: this.scanValue.lot,
        userData1: this.scanValue.userData1,
        userData2: this.scanValue.userData2,
        userData3: this.scanValue.userData3,
        userData4: this.scanValue.userData4,
        userData5: this.scanValue.userData5,
        initialQuantity: this.scanValue.initialQuantity,
        msdLevel: "",
        msdInitialFloorTime: "",
        msdBagSealDate: "",
        marketUsage: "",
        quantityOverride: this.scanValue.quantityOverride,
        shelfTime: "",
        spMaterialName: "",
        warningLimit: "",
        maximumLimit: "",
        comments: "",
        warmupTime: "",
        storageUnit: this.scanValue.storageUnit,
        subStorageUnit: "",
        locationOverride: "",
        expirationDate: this.scanValue.expirationDate,
        manufacturingDate: this.scanValue.manufacturingDate,
        partClass: "",
        sapCode: this.scanValue.sapCode,
        trangThai: "not list",
        checked: 1,
        lenhSanXuat: this.createFromForm(),
      };
      this.chiTietLenhSanXuats.push(item);
      this.alertTimeout("Tem không nằm trong danh sách", 1000);
      await this.playAlertSound();

      // cập nhật lại danh sách chi tiết lsx ở trạng thái active
      return;
    }
  }
  sortList(): void {
    this.chiTietLenhSanXuatActive = this.chiTietLenhSanXuats.filter(
      (a) => a.trang_thai === "ACTIVE",
    );
    this.chiTietLenhSanXuatActive.sort(function (a, b) {
      if (
        a.checked !== undefined &&
        a.checked !== null &&
        b.checked !== undefined &&
        b.checked !== null
      ) {
        return a.checked - b.checked;
      }
      return 0;
    });
    // cập nhật lại danh sách chi tiết lsx không có trong danh sách
    this.chiTietLenhSanXuatNotList = this.chiTietLenhSanXuats.filter(
      (a) => a.trang_thai === "not list",
    );
    this.scanResults = "";
  }
  alertTimeout(mymsg: string, mymsecs: number): void {
    const myelement = document.createElement("div");
    myelement.setAttribute(
      "style",
      "background-color:white;color:Black; width: 300px;height: 70px;position: absolute;top:0;bottom:0;left:0;right:0;margin:auto;z-index:100;border: 1px solid black;font-family:arial;font-size:16px;display: flex; align-items: center; justify-content: center; text-align: center;border-radius:10px",
    );
    myelement.innerHTML = mymsg;
    setTimeout(function () {
      if (myelement.parentNode) {
        myelement.parentNode.removeChild(myelement);
      }
    }, mymsecs);
    document.body.appendChild(myelement);
  }
  createFromForm(): ILenhSanXuat {
    return {
      ...new LenhSanXuat(),
      id: this.editForm.get(["id"])!.value,
      maLenhSanXuat: this.editForm.get(["maLenhSanXuat"])!.value,
      sapCode: this.editForm.get(["sapCode"])!.value,
      sapName: this.editForm.get(["sapName"])!.value,
      workOrderCode: this.editForm.get(["workOrderCode"])!.value,
      version: this.editForm.get(["version"])!.value,
      storageCode: this.editForm.get(["storageCode"])!.value,
      totalQuantity: this.editForm.get(["totalQuantity"])!.value,
      createBy: this.account?.login,
      entryTime: this.editForm.get(["entryTime"])!.value
        ? dayjs(this.editForm.get(["entryTime"])!.value, DATE_TIME_FORMAT)
        : undefined,
      timeUpdate: this.editForm.get(["timeUpdate"])!.value
        ? dayjs(this.editForm.get(["timeUpdate"])!.value, DATE_TIME_FORMAT)
        : undefined,
      trangThai: this.editForm.get(["trangThai"])!.value,
      comment: this.editForm.get(["comment"])!.value,
      comment2: this.editForm.get(["comment2"])!.value,
      groupName: this.editForm.get(["groupName"])!.value,
    };
  }
  // bắt sự kiện scan
  catchEventScanReelId(): void {
    const positionSL = this.storageUnit.indexOf("-SL");
    if (positionSL === -1) {
      const item = {
        id: this.count,
        reelID: this.reelID,
        storageUnit: this.storageUnit.slice(2),
        subStorageUnit: "",
      };
      this.chiTietLenhSanXuatActive[this.index].storageUnit =
        this.storageUnit.slice(2);
      this.dataMove.push(item);
      this.dataMove.sort((a: any, b: any) => b.id - a.id);
      setTimeout(() => {
        this.count++;
        this.reelID = "";
        this.storageUnit = "";
        document.getElementById("reelID")!.focus();
      }, 150);
    } else {
      const item = {
        id: this.count,
        reelID: this.reelID,
        storageUnit: this.storageUnit.slice(2, positionSL),
        subStorageUnit: this.storageUnit.slice(positionSL + 3),
      };
      this.chiTietLenhSanXuatActive[this.index].storageUnit =
        this.storageUnit.slice(2, positionSL);
      this.chiTietLenhSanXuatActive[this.index].subStorageUnit =
        this.storageUnit.slice(positionSL + 3);
      this.dataMove.push(item);
      this.dataMove.sort((a: any, b: any) => b.id - a.id);
      setTimeout(() => {
        this.count++;
        this.reelID = "";
        this.storageUnit = "";
        document.getElementById("reelID")!.focus();
      }, 150);
    }
  }
  // delete thông tin  sau khi scan
  removeItem(id: any): void {
    this.dataMove = this.dataMove.filter((item) => item.id !== id);
  }
  //clear thông tin scan
  clearScanInFor(): void {
    this.reelID = "";
    document.getElementById("reelID")!.focus();
  }
  //scan reelID
  scanReelId(): void {
    let result = false;
    const reelID = this.reelID.substring(0, 17);
    for (let i = 0; i < this.chiTietLenhSanXuatActive.length; i++) {
      if (reelID === this.chiTietLenhSanXuatActive[i].reel_id) {
        this.reelID = reelID;
        this.index = i;
        document.getElementById("storageUnit")!.focus();
        result = true;
      }
    }
    setTimeout(() => {
      if (result === false) {
        this.alertTimeout("Tem không nằm trong danh sách", 3000);
        document.getElementById("reelID")!.focus();
        this.reelID = "";
      }
      // console.log(reelID);
    }, 300);
  }
  //cập nhật thông tin kho sau khi scan Move
  updateInfo(): void {
    this.popupMove = false;
    this.alertTimeout("Cập nhật vị trí kho thành công", 1000);
  }
  openPopupMove(): void {
    this.popupMove = true;
    setTimeout(() => {
      document.getElementById("reelID")!.focus();
    }, 100);
  }
  closePopupMove(): void {
    this.popupMove = false;
  }
  //upload
  UploadPanacim(): void {
    //code
  }
}
