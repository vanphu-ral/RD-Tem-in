import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef,
  OnDestroy,
  ChangeDetectorRef,
  Inject,
  Input,
} from "@angular/core";
import { Router } from "@angular/router";
import { MatTableDataSource } from "@angular/material/table";
import { ChangeDetectionStrategy, signal, WritableSignal } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  FormControl,
  FormArray,
  Validators,
} from "@angular/forms";
import { MatMenuTrigger } from "@angular/material/menu";
import { MatSort } from "@angular/material/sort";
import { PageEvent, MatPaginator } from "@angular/material/paginator";
// import { KeycloakApiService,UserDto  } from "../services/KeycloakApiService";
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { Subscription, Observable, Subject, of } from "rxjs";
import { SelectionModel } from "@angular/cdk/collections";
import {
  RawGraphQLMaterial,
  ListMaterialService,
  RawGraphQLLocation,
  UserSummary,
  inventory_update_requests_detail,
} from "../services/list-material.service";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MaterialUpdateService } from "../services/material-update.service";
import { startWith, map, takeUntil, take } from "rxjs/operators";
import {
  DialogContentExampleDialogComponent,
  ConfirmDialogData,
} from "../confirm-dialog/confirm-dialog.component";
import { MatRadioButton } from "@angular/material/radio";
import { MatAutocompleteTrigger } from "@angular/material/autocomplete";
import { AccountService } from "app/core/auth/account.service";

interface Warehouse {
  value: string;
  name: string;
}

export interface MaterialItem {
  enable_input_expirated?: boolean;
  inventoryId: string;
  materialIdentifier: string;
  partNumber: string;
  calculatedStatus: string;
  trackingType?: string;
  materialTraceId?: string;
  quantity: number;
  quantityChange: number;
  locationId: string | null;
  locationFullName?: string;
  locationName?: string;
  expirationDate?: string;
  receivedDate?: string;
  updatedDate?: string;
  updatedBy?: string;
  checkinDate?: string;
  extendExpiration?: boolean;
  selectedWarehouse?: Warehouse;
  _isChanged?: boolean;
}

export interface sub {
  name: string;
  completed: boolean;
}

export interface SelectApproverpprover {
  name: string;
  completed: boolean;
  sub: sub[];
}

@Component({
  selector: "jhi-list-material-update",
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormBuilder],
  templateUrl: "./list-material-update-dialog.html",
  styleUrls: ["./list-material-update-dialog.scss"],
})
export class ListMaterialUpdateDialogComponent implements OnInit {
  // #region Public properties
  dialogForm: FormGroup = new FormGroup({});
  locations$: Observable<RawGraphQLLocation[]>;
  @ViewChild("input") input!: ElementRef<HTMLInputElement>;
  @ViewChild("scanInput") scanInput!: ElementRef<HTMLInputElement>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @Input() selectedWarehouse: RawGraphQLLocation | string | null = null;
  @Input() requestId!: number;
  myControl = new FormControl("");
  headerQuantityChange: number | null = null;
  filteredOptions!: string[];
  approvers: UserSummary[] = [];
  approverCtrl = new FormControl<UserSummary | string>("", Validators.required);
  filteredApprovers!: Observable<UserSummary[]>;
  selectedApprover?: UserSummary;

  // approvers: WritableSignal<UserDto[]> = signal<UserDto[]>([]);
  // selectedApprover: WritableSignal<UserDto | null> = signal<UserDto | null>(null);

  // selectApprover: WritableSignal<SelectApproverpprover> =
  //   signal<SelectApproverpprover>({
  //     name: "Select all",
  //     completed: false,
  //     sub: [
  //       { name: "admin1", completed: false },
  //       { name: "admin2", completed: false },
  //       { name: "admin3", completed: false },
  //       { name: "admin4", completed: false },
  //       { name: "admin5", completed: false },
  //     ],
  //   });
  warehouseSelection: Array<{ value: string; name: string }> = [];
  filteredWarehouses!: Observable<Array<{ value: string; name: string }>>;
  displayableItemKeys: string[][] = [];
  itemsDataSource: MatTableDataSource<MaterialItem> =
    new MatTableDataSource<MaterialItem>();
  displayedColumns: string[] = [
    "materialIdentifier",
    "partNumber",
    "calculatedStatus",
    "expirationDate",
    "quantity",
    "quantityChange",
    // 'status',
    "locationId",
    "extendExpiration",
    "scanLocation",
  ];
  statusOptions = [
    { value: "", view: "-- All --" },
    { value: "available", view: "Available" },
    { value: "consumed", view: "Consumed" },
    { value: "expired", view: "Expired" },
  ];
  editableFields: string[] = ["quantity", "locationFullName"];
  headerEnableInputRenewal: boolean = false;
  headerInputRenewal: string = "";
  itemFormGroups: Map<any, FormGroup> = new Map();
  rowFilteredWarehouses: Map<
    string,
    Observable<Array<{ value: string; name: string }>>
  > = new Map();
  applyHeaderSelectNewlocation: string = "";
  filteredlocations: string[] = [];
  optionslocation: string[] = [];
  scanLoadingRow: { [materialIdentifier: string]: boolean } = {};
  scanLoadingAll = false;
  currentScanRow: MaterialItem | null = null;
  isScanAll = false;
  pageSize = 50;
  pageIndex = 0;
  isMoveChecked = false;
  autoApprove = false;
  buttonLabel = "Gửi đề nghị";
  isAutoApprove = false;
  selectedSubApproverIndex: number = -1;
  public searchTerms: { [columnDef: string]: { mode: string; value: string } } =
    {};
  public activeFilters: { [columnDef: string]: any[] } = {};
  public filterModes: { [columnDef: string]: string } = {};
  public isWarehouseReady = false;
  private bufferedScanValue: string | null = null;
  private isSelectHeader: boolean = false;
  private ngUnsubscribe = new Subject<void>();
  private allApprovers: UserSummary[] = [];
  private scanBuffer = "";
  private scanTimeoutId: any;
  private scanDelay = 250;

  // #endregion

  // #region Constructor
  constructor(
    public dialogRef: MatDialogRef<ListMaterialUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      items: RawGraphQLMaterial[];
      warehouses: Array<{ value: string; name: string }>;
    },
    private dialog: MatDialog,
    private fb: FormBuilder,
    private materialUpdateService: MaterialUpdateService,
    private materialService: ListMaterialService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private accountService: AccountService,
    // private kcApi: KeycloakApiService,
  ) {
    this.dialogForm = this.fb.group({
      selectedWarehouseControl: [null],
    });
    this.materialService.fetchLocations();

    this.locations$ = this.materialService.locationsData$;

    const mappedItems: MaterialItem[] = data.items.map(
      (rawItem: RawGraphQLMaterial): MaterialItem => {
        const formatDate = (dateString: string | null | undefined): string => {
          if (!dateString) {
            return "N/A";
          }
          const date = new Date(dateString);
          return isNaN(date.getTime())
            ? "Invalid Date"
            : date.toLocaleDateString();
        };

        return {
          inventoryId: rawItem.inventoryId,
          materialIdentifier: rawItem.materialIdentifier,
          partNumber: rawItem.partNumber,
          calculatedStatus: this.getCalculatedStatus(rawItem.status),
          expirationDate: rawItem.expirationDate,
          quantity: Number(rawItem.quantity),
          quantityChange: 0,
          locationId: rawItem.locationId || null,
          locationFullName: rawItem.locationFullName,
          extendExpiration: false,
        };
      },
    );
    this.itemsDataSource.data = mappedItems;
  }
  // #endregion

  // #region Lifecycle hooks
  ngOnInit(): void {
    this.locations$.subscribe((locations) => {
      this.warehouseSelection = locations.map((loc) => ({
        value: loc.id.toString(),
        name: loc.locationFullName,
      }));
      // console.log("warehouseSelection:", this.warehouseSelection);

      const headerCtl = this.dialogForm.get("selectedWarehouseControl")!;
      const headerInit =
        this.warehouseSelection.find((w) => w.value === headerCtl.value) ??
        null;
      headerCtl.setValue(headerInit, { emitEvent: false });
      headerCtl.updateValueAndValidity({ emitEvent: true });

      this.itemsDataSource.data.forEach((item) => {
        const fg = this.getFormGroupForItem(item);
        const rowCtl = fg.get("selectedWarehouseItem")!;
        const initial =
          this.warehouseSelection.find((w) => w.value === item.locationId) ??
          null;
        rowCtl.setValue(initial, { emitEvent: false });
        rowCtl.updateValueAndValidity({ emitEvent: true });
      });
      this.isWarehouseReady = true;
      // console.log("[INIT] Danh sách kho đã sẵn sàng");

      if (this.bufferedScanValue) {
        const buffered = this.bufferedScanValue;
        const matched = this.warehouseSelection.find(
          (w) => w.name.trim().toLowerCase() === buffered.toLowerCase(),
        );

        if (matched) {
          if (this.isScanAll) {
            this.globalWarehouseChanged(matched);
            this.scanLoadingAll = false;
            this.isScanAll = false;
          } else if (this.currentScanRow) {
            this.rowWarehouseChanged(matched, this.currentScanRow);
            this.scanLoadingRow[this.currentScanRow.materialIdentifier] = false;
            this.currentScanRow = null;
          }
        } else {
          console.warn("[SCAN] fallback header scan → gán chuỗi vào input");
          if (headerCtl) {
            headerCtl.setValue(buffered, { emitEvent: true });
          }

          this.itemsDataSource.data.forEach((item) => {
            item.locationId = "";
            item.locationFullName = buffered;

            const formGroup = this.getFormGroupForItem(item);
            formGroup
              .get("selectedWarehouseItem")
              ?.setValue(null, { emitEvent: true });
          });

          this.scanLoadingAll = false;
          this.isScanAll = false;

          this.snackBar.open(
            "Giá trị scan đã lưu không khớp với danh sách.",
            "Đóng",
            {
              duration: 3000,
              panelClass: ["snack-info"],
              horizontalPosition: "center",
              verticalPosition: "top",
            },
          );
        }
        this.bufferedScanValue = null;
      }
    });
    this.materialService.getApprovers().subscribe((list) => {
      this.allApprovers = list;
    });
    this.filteredApprovers = this.approverCtrl.valueChanges.pipe(
      startWith(""),
      map((val) => val ?? ""),
      map((val) => (typeof val === "string" ? val : val.username)),
      map((name) => this.filterByName(name)),
    );

    this.itemsDataSource.data.forEach((item) => {
      item.quantityChange = item.quantity;
    });
    // this.kcApi.getUsersByRole('ROLE_PANACIM_ADMIN', 0, 100).subscribe({
    //   next: (list) => {
    //     console.log('[ApproverSelect] Fetched users:', list);
    //     if (Array.isArray(list) && list.length > 0) {
    //       this.approvers.set(list);
    //     } else {
    //       console.warn('[ApproverSelect] API trả về mảng rỗng hoặc không phải mảng:', list);
    //       this.approvers.set([]);
    //     }
    //   },
    //   error: (err) => {
    //     console.error('[ApproverSelect] Lỗi fetch users Keycloak:', err);
    //     this.approvers.set([]);
    //   }
    // });

    this.filteredWarehouses = this.dialogForm
      .get("selectedWarehouseControl")!
      .valueChanges.pipe(
        startWith(this.dialogForm.get("selectedWarehouseControl")?.value || ""),
        map((value: string | Warehouse) =>
          typeof value === "string" ? value : value?.name || "",
        ),
        map((name: string) => this._filterWarehouses(name)),
        map((list) => list.sort((a, b) => a.name.length - b.name.length)),
      );

    // Định nghĩa interface cho filter được parse từ JSON
    interface FilterData {
      textFilters: { [col: string]: { mode: string; value: string } };
      dialogFilters: { [col: string]: any[] };
    }

    this.itemsDataSource.filterPredicate = (
      data: MaterialItem,
      filter: string,
    ): boolean => {
      // Ép kiểu dữ liệu filter theo interface đã định nghĩa
      const { textFilters, dialogFilters } = JSON.parse(filter) as FilterData;

      for (const colDef in textFilters) {
        if (!Object.prototype.hasOwnProperty.call(textFilters, colDef)) {
          continue;
        }

        const { mode: searchMode, value } = textFilters[colDef];
        const searchTerm = value.trim().toLowerCase();
        if (!searchTerm) {
          continue;
        }

        const cellValueRaw = (data as any)[colDef];
        let cellValue: string;

        if (colDef === "calculatedStatus") {
          const n =
            typeof cellValueRaw === "string"
              ? parseInt(cellValueRaw, 10)
              : cellValueRaw;
          cellValue = this.getCalculatedStatus(n).toLowerCase();
        } else if (
          [
            "expirationDate",
            "receivedDate",
            "updatedDate",
            "checkinDate",
          ].includes(colDef)
        ) {
          let dt: Date;
          if (typeof cellValueRaw === "number") {
            dt = new Date(cellValueRaw * 1000);
          } else if (
            typeof cellValueRaw === "string" &&
            /^\d+$/.test(cellValueRaw)
          ) {
            dt = new Date(Number(cellValueRaw) * 1000);
          } else {
            dt = new Date(cellValueRaw);
          }
          if (isNaN(dt.getTime())) {
            return false;
          }
          cellValue = dt
            .toLocaleDateString("vi-VN", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
            })
            .toLowerCase();
        } else {
          cellValue = String(cellValueRaw).trim().toLowerCase();
        }

        switch (searchMode) {
          case "equals":
            if (cellValue !== searchTerm) {
              return false;
            }
            break;
          case "contains":
            if (!cellValue.includes(searchTerm)) {
              return false;
            }
            break;
        }
      }

      return true;
    };
  }
  // #endregion

  // #region Public methods
  totalQuantityselect(): number {
    return this.itemsDataSource.data
      .map((row) => row.quantity ?? 0)
      .reduce((sum, val) => sum + val, 0);
  }
  getFormGroupForItem(item: MaterialItem): FormGroup {
    const key = item.materialIdentifier;
    if (!this.itemFormGroups.has(key)) {
      const control = new FormControl<Warehouse | null>(null);

      const group = this.fb.group({ selectedWarehouseItem: control });
      this.itemFormGroups.set(key, group);

      this.rowFilteredWarehouses.set(
        key,
        control.valueChanges.pipe(
          map((v) => (typeof v === "string" ? v : (v?.name ?? ""))),
          map((name) => this._filterWarehouses(name)),
        ),
      );
    }
    return this.itemFormGroups.get(key)!;
  }
  // onSelectApprover(user: UserDto): void {
  //   this.selectedApprover.set(user);
  // }
  rowWarehouseChanged(selected: Warehouse, item: MaterialItem): void {
    const formGroup = this.getFormGroupForItem(item);

    formGroup
      .get("selectedWarehouseItem")
      ?.setValue(selected, { emitEvent: true });

    item.locationId = selected.value;
    item.locationFullName = selected.name;
    item.selectedWarehouse = selected;
    // item.extendExpiration = false;

    // console.log(
    //   `Row change: Item ${item.materialIdentifier} new locationId =`,
    //   selected.value,
    // );

    this.isSelectHeader = false;
  }

  globalWarehouseChanged(selectedWarehouse: Warehouse): void {
    this.dialogForm
      .get("selectedWarehouseControl")
      ?.setValue(selectedWarehouse, { emitEvent: false });

    // console.log(`Header: Warehouse selected: ${selectedWarehouse.value}`);

    if (this.itemsDataSource && this.itemsDataSource.data) {
      this.itemsDataSource.data.forEach((item) => {
        const formGroup = this.getFormGroupForItem(item);
        formGroup
          .get("selectedWarehouseItem")
          ?.setValue(selectedWarehouse, { emitEvent: true });

        item.selectedWarehouse = selectedWarehouse;
        item.locationId = selectedWarehouse.value;
        item.locationFullName = selectedWarehouse.name;

        // console.log(
        //   `Header update: Item ${item.materialIdentifier} locationId updated to: ${item.locationId}`,
        // );
      });
    }

    this.isSelectHeader = true;
  }

  onMoveCheckboxChange(checked: boolean): void {
    if (!checked) {
      return;
    }

    const expiredItems = this.itemsDataSource.data.filter(
      (item: MaterialItem) => item.calculatedStatus === "Expired",
    );

    const expiredCount = expiredItems.length;

    if (expiredCount > 0) {
      const confirm = window.confirm(
        `Có ${expiredCount} vật tư đã hết hạn. Bạn có muốn chọn "Gia hạn" cho chúng không?`,
      );

      if (confirm) {
        expiredItems.forEach((item) => {
          item.extendExpiration = true;
        });

        const allExtended = this.isAllExtendExpirationSelected();
        // console.log("Tất cả expired đã được chọn gia hạn?", allExtended);
      }
    }
  }

  onAutoApproved(checked: boolean): void {
    this.isAutoApprove = checked;
    this.buttonLabel = checked ? "Cập nhật" : "Gửi đề nghị";
  }
  onSubmit(): void {
    if (this.autoApprove) {
      this.instantApprove();
    } else {
      this.onSave();
    }
  }

  getCalculatedStatus(status: number | string): string {
    const s = typeof status === "string" ? parseInt(status, 10) : status;
    switch (s) {
      case 3:
        return "Available";
      case 6:
        return "Consumed";
      case 19:
        return "Expired";
      default:
        return "N/A";
    }
  }
  // applyHeaderQuantityChange(): void {
  //   const qty = Number(this.headerQuantityChange);
  //   const max = this.minQuantityAcrossRows;
  //   const safeQty = qty > max ? max : qty;
  //   this.headerQuantityChange = safeQty;

  //   this.itemsDataSource.data.forEach((item) => {
  //     item.quantityChange = safeQty;
  //     item._isChanged = true;
  //   });
  //   this.itemsDataSource._updateChangeSubscription();
  // }
  displayApprover(user: UserSummary): string {
    return user ? user.username : "";
  }
  onApproverSelected(user: UserSummary): void {
    this.selectedApprover = user;
    // console.log("Chọn người duyệt:", user);
  }

  applyHeaderQuantityChange(): void {
    if (
      this.headerQuantityChange !== null &&
      this.headerQuantityChange !== undefined &&
      this.itemsDataSource
    ) {
      this.itemsDataSource.data.forEach((item) => {
        item.quantityChange = this.headerQuantityChange as number;
        item._isChanged = true;
      });
    }
  }

  toggleAllRenewal(): void {
    this.itemsDataSource.data.forEach(
      (row) => (row.enable_input_expirated = this.headerEnableInputRenewal),
    );
    if (this.headerEnableInputRenewal && this.headerInputRenewal) {
      this.applyHeaderInputRenewal();
    }
  }

  applyHeaderInputRenewal(): void {
    if (this.headerEnableInputRenewal && this.headerInputRenewal) {
      this.itemsDataSource.data.forEach((element) => {
        if (element.enable_input_expirated) {
          element.expirationDate = this.headerInputRenewal;
        }
      });
    }
  }
  clearScanState(): void {
    this.scanLoadingAll = false;
    this.currentScanRow = null;

    Object.keys(this.scanLoadingRow).forEach((key) => {
      this.scanLoadingRow[key] = false;
    });

    this.isScanAll = false;
    this.scanInput?.nativeElement?.blur();
  }

  scanLocationForRow(item: MaterialItem): void {
    const isSameRow = this.currentScanRow?.inventoryId === item.inventoryId;

    if (isSameRow) {
      this.clearScanState();
      return;
    }

    this.clearScanState();

    this.currentScanRow = item;
    this.scanLoadingRow[item.materialIdentifier] = true;

    setTimeout(() => this.scanInput?.nativeElement?.focus(), 0);
  }

  scanLocationForAll(): void {
    this.clearScanState();
    const inputEl = this.scanInput?.nativeElement;

    this.isScanAll = !this.isScanAll; // Toggle

    Object.keys(this.scanLoadingRow).forEach(
      (key) => (this.scanLoadingRow[key] = false),
    );

    this.scanLoadingAll = this.isScanAll;
    this.currentScanRow = null;

    if (this.isScanAll) {
      setTimeout(() => inputEl?.focus(), 0);
    } else {
      inputEl?.blur();
    }
  }

  refreshForRow(item: MaterialItem): void {
    this.scanLoadingRow[item.materialIdentifier] = true;
    setTimeout(() => {
      item.locationId = "";
      // Không động vào item.locationName để giữ placeholder
      const formGroup = this.getFormGroupForItem(item);
      formGroup
        .get("selectedWarehouseItem")
        ?.setValue(null, { emitEvent: true });
      this.scanLoadingRow[item.materialIdentifier] = false;
      this.cdr.markForCheck();
    }, 300);
  }
  refreshForAll(): void {
    this.scanLoadingAll = true;

    setTimeout(() => {
      this.itemsDataSource.data.forEach((item) => {
        item.locationId = "";
        item.locationFullName = "";

        const formGroup = this.getFormGroupForItem(item);
        formGroup
          .get("selectedWarehouseItem")
          ?.setValue(null, { emitEvent: true });

        this.scanLoadingRow[item.materialIdentifier] = false;
      });

      const headerControl = this.dialogForm.get("selectedWarehouseControl");
      if (headerControl) {
        headerControl.setValue(null, { emitEvent: true });
        headerControl.updateValueAndValidity({ emitEvent: true });
      }

      this.scanLoadingAll = false;
      this.cdr.markForCheck();
    }, 300);
  }
  onScanLocationInput(event: Event): void {
    this.scanBuffer = (event.target as HTMLInputElement).value;

    if (this.scanTimeoutId) {
      clearTimeout(this.scanTimeoutId);
    }
    this.scanTimeoutId = setTimeout(() => {
      this.handleScan(this.scanBuffer);
    }, this.scanDelay);
  }
  onScanInputEnter(): void {
    if (this.scanTimeoutId) {
      clearTimeout(this.scanTimeoutId);
      this.scanTimeoutId = null;
    }
    this.handleScan(this.scanBuffer);
  }

  filterlocations(value: string): void {
    const filterValue = value ? value.toLowerCase() : "";
    this.filteredlocations = this.optionslocation.filter((location) =>
      location.toLowerCase().includes(filterValue),
    );
    if (this.applyHeaderSelectNewlocation !== value) {
      this.applyHeaderSelectNewlocation = value;
      this.isSelectHeader = true;
    }
  }
  get minQuantityAcrossRows(): number {
    const q = this.itemsDataSource?.data.map((d) => d.quantity) || [];
    return q.length ? Math.min(...q) : 0;
  }
  openRowPanel(trigger: MatAutocompleteTrigger): void {
    Promise.resolve().then(() => trigger.openPanel());
  }

  clampHeaderQuantity(input: HTMLInputElement): void {
    const val = Number(input.value);
    const max = this.minQuantityAcrossRows;
    // if (val > max) {
    //   this.snackBar.open(
    //     `Không thể nhập lớn hơn ${max} (số nhỏ nhất trong các dòng)`,
    //     "Đóng",
    //     { duration: 2000, panelClass: ["snackbar-error"] },
    //   );
    //   input.value = String(max);
    //   this.headerQuantityChange = max;
    // }
  }

  clampQuantity(event: Event, item: MaterialItem): void {
    const input = event.target as HTMLInputElement;
    const val = Number(input.value);
    if (val > item.quantity) {
      input.value = String(item.quantity);
    }
  }

  onQuantityChange(element: MaterialItem): void {
    // if (element.quantityChange > element.quantity) {
    //   this.snackBar.open(
    //     "Không thể chuyển giá trị lớn hơn số lượng hiện tại",
    //     "Đóng",
    //     {
    //       duration: 3000,
    //       panelClass: ["snackbar-error"],
    //     },
    //   );
    //   element.quantityChange = element.quantity;
    // }
    element._isChanged = true;
  }

  // onSelectSubApprover(idx: number): void {
  //   this.selectedSubApproverIndex = idx;
  //   const current = this.selectApprover();
  //   current.sub.forEach((sub, i) => (sub.completed = i === idx));
  //   this.selectApprover.set({ ...current });
  // }
  // get selectedSubApproverIndex(): number {
  //   return this.selectApprover().sub.findIndex(sub => sub.completed);
  // }
  applySelectFilter(col: string, value: string): void {
    const mode = this.filterModes[col] || "equals";
    this.searchTerms[col] = { mode, value };
    this.applyCombinedFilters();
  }

  public applyFilter(colDef: string, event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    const selectedMode = this.filterModes[colDef] || "contains";
    this.searchTerms[colDef] = {
      mode: selectedMode,
      value: filterValue.trim().toLowerCase(),
    };
    // console.log(`[applyFilter] - Cột ${colDef}:`, this.searchTerms[colDef]);
    this.applyCombinedFilters();
  }

  setFilterMode(column: any, mode: string): void {
    this.filterModes[column] = mode;
  }
  hasExpiredRows(): boolean {
    return this.expiredRows.length > 0;
  }

  displayWarehouseFn(warehouse: { value: string; name: string }): string {
    return warehouse && warehouse.name ? warehouse.name : "";
  }
  getLocationNameById(locationId: string | null | undefined): string | null {
    if (!locationId) {
      return null;
    }
    const found = this.warehouseSelection.find((w) => w.value === locationId);
    return found ? found.name : null;
  }
  onSave(): void {
    const selectedWarehouseObj = this.dialogForm.get("selectedWarehouseControl")
      ?.value as Warehouse | null;
    const headerWarehouseValue = selectedWarehouseObj?.value ?? null;
    const approver = this.approverCtrl.value as UserSummary | null;

    if (!approver) {
      this.snackBar.open("Yêu cầu chọn người duyệt!", "Đóng", {
        duration: 3000,
        panelClass: ["snackbar-error"],
        horizontalPosition: "center",
        verticalPosition: "top",
      });
      return;
    }
    const headerWarehouseSet = !!headerWarehouseValue;
    const allQtyProvided = this.itemsDataSource.data.every(
      (item) =>
        item.quantityChange !== null &&
        item.quantityChange !== undefined &&
        item.quantityChange !== 0,
    );
    if (!allQtyProvided) {
      this.snackBar.open(
        "Số lượng không được để trống và phải lớn hơn 0!",
        "Đóng",
        {
          duration: 3000,
          panelClass: ["snackbar-error"],
          horizontalPosition: "center",
          verticalPosition: "top",
        },
      );
      return;
    }

    const invalidRows: MaterialItem[] = this.itemsDataSource.data.filter(
      (item) => {
        const hasWarehouse =
          headerWarehouseSet || !!item.selectedWarehouse?.value;
        const hasQuantity =
          item.quantityChange !== null &&
          item.quantityChange !== undefined &&
          item.quantityChange > 0;
        const hasExtension = item.extendExpiration;
        const isQuantityChanged =
          item.quantityChange !== null &&
          item.quantityChange !== undefined &&
          item.quantityChange !== item.quantity;

        if (item.calculatedStatus === "Expired") {
          return !(hasQuantity && (hasWarehouse || hasExtension));
        }

        if (item.calculatedStatus === "Available") {
          return !hasWarehouse && !(isQuantityChanged && hasQuantity);
        }

        return !(hasQuantity || hasWarehouse || hasExtension);
      },
    );

    if (invalidRows.length > 0) {
      invalidRows.forEach((item) => {
        const id = item.materialIdentifier;
        const status = item.calculatedStatus;

        let reason = "";

        if (status === "Expired") {
          reason = "Hết hạn cần chọn Chuyển kho hoặc Gia hạn + nhập số lượng";
        } else if (status === "Available") {
          reason = "Cần chọn kho hoặc nhập số lượng thay đổi";
        } else {
          reason = "Thiếu thông tin chuyển hoặc số lượng";
        }

        this.snackBar.open(`Hàng ${id}: ${reason}`, "Đóng", {
          duration: 3000,
          panelClass: ["snackbar-error"],
          horizontalPosition: "center",
          verticalPosition: "top",
        });
      });
      return;
    }

    const dialogData: ConfirmDialogData = {
      message: "Bạn có muốn gửi đề nghị cập nhật cho các vật tư này không?",
      confirmText: "Xác nhận",
      cancelText: "Hủy",
    };

    const confirmDialogRef = this.dialog.open(
      DialogContentExampleDialogComponent,
      {
        width: "450px",
        data: dialogData,
        panelClass: "dialog-center-pane",
        disableClose: true,
        autoFocus: false,
      },
    );

    confirmDialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.dialogRef.close({
          updatedItems: this.itemsDataSource.data,
          selectedWarehouse: headerWarehouseValue,
          approvers: [approver.username],
        });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onCellBlur(element: MaterialItem, columnName: keyof MaterialItem): void {}

  // partiallyComplete(): boolean {
  //   const subtasks = this.selectApprover().sub;
  //   if (!subtasks) {
  //     return false;
  //   }
  //   const allCompleted = subtasks.every((sub) => sub.completed);
  //   const anyCompleted = subtasks.some((sub) => sub.completed);
  //   return anyCompleted && !allCompleted;
  // }

  // update(completed: boolean, index?: number): void {
  //   const currentGroup = this.selectApprover();

  //   if (index === undefined) {
  //     currentGroup.completed = completed;
  //     currentGroup.sub!.forEach((sub) => (sub.completed = completed));
  //   } else {
  //     currentGroup.sub![index].completed = completed;
  //   }

  //   this.selectApprover.set({ ...currentGroup });
  // }

  toggleAllExtendExpiration(checked: boolean): void {
    this.itemsDataSource.data.forEach((row) => {
      if (row.calculatedStatus === "Expired") {
        row.extendExpiration = checked;
        row._isChanged = true;
      }
    });
  }

  isAllExtendExpirationSelected(): boolean {
    if (
      !this.itemsDataSource ||
      !this.itemsDataSource.data ||
      this.itemsDataSource.data.length === 0
    ) {
      return false;
    }
    const rows = this.expiredRows;
    return rows.length > 0 && rows.every((r) => r.extendExpiration);
  }

  isSomeExtendExpirationSelected(): boolean {
    if (
      !this.itemsDataSource ||
      !this.itemsDataSource.data ||
      this.itemsDataSource.data.length === 0
    ) {
      return false;
    }
    const rows = this.expiredRows;
    const cnt = rows.filter((r) => r.extendExpiration).length;
    return cnt > 0 && cnt < rows.length;
  }
  // #endregion

  // #region Private methods
  private instantApprove(): void {
    const selectedWarehouseObj = this.dialogForm.get("selectedWarehouseControl")
      ?.value as Warehouse | null;
    const headerWarehouseValue = selectedWarehouseObj?.value ?? null;
    const approver = this.approverCtrl.value as UserSummary | null;

    if (!approver) {
      this.snackBar.open("Yêu cầu chọn người duyệt!", "Đóng", {
        duration: 3000,
        panelClass: ["snackbar-error"],
        horizontalPosition: "center",
        verticalPosition: "top",
      });
      return;
    }

    const headerWarehouseSet = !!headerWarehouseValue;
    const allQtyProvided = this.itemsDataSource.data.every(
      (item) =>
        item.quantityChange !== null &&
        item.quantityChange !== undefined &&
        item.quantityChange !== 0,
    );

    if (!allQtyProvided) {
      this.snackBar.open(
        "Số lượng không được để trống và phải lớn hơn 0!",
        "Đóng",
        {
          duration: 3000,
          panelClass: ["snackbar-error"],
          horizontalPosition: "center",
          verticalPosition: "top",
        },
      );
      return;
    }

    const invalidRows: MaterialItem[] = this.itemsDataSource.data.filter(
      (item) => {
        const hasWarehouse =
          headerWarehouseSet || !!item.selectedWarehouse?.value;
        const hasQuantity =
          item.quantityChange !== null &&
          item.quantityChange !== undefined &&
          item.quantityChange > 0;
        const hasExtension = item.extendExpiration;
        const isQuantityChanged =
          item.quantityChange !== null &&
          item.quantityChange !== undefined &&
          item.quantityChange !== item.quantity;

        if (item.calculatedStatus === "Expired") {
          return !(hasQuantity && (hasWarehouse || hasExtension));
        }

        if (item.calculatedStatus === "Available") {
          return !hasWarehouse && !(isQuantityChanged && hasQuantity);
        }

        return !(hasQuantity || hasWarehouse || hasExtension);
      },
    );

    if (invalidRows.length > 0) {
      invalidRows.forEach((item) => {
        const id = item.materialIdentifier;
        const status = item.calculatedStatus;

        let reason = "";
        if (status === "Expired") {
          reason = "Hết hạn cần chọn Chuyển kho hoặc Gia hạn + nhập số lượng";
        } else if (status === "Available") {
          reason = "Cần chọn kho hoặc nhập số lượng thay đổi";
        } else {
          reason = "Thiếu thông tin chuyển hoặc số lượng";
        }

        this.snackBar.open(`Hàng ${id}: ${reason}`, "Đóng", {
          duration: 3000,
          panelClass: ["snackbar-error"],
          horizontalPosition: "center",
          verticalPosition: "top",
        });
      });
      return;
    }

    const dialogData: ConfirmDialogData = {
      message: "Bạn có muốn cập nhật tự động các vật tư này không?",
      confirmText: "Cập nhật",
      cancelText: "Hủy",
    };

    const confirmDialogRef = this.dialog.open(
      DialogContentExampleDialogComponent,
      {
        width: "450px",
        data: dialogData,
        panelClass: "dialog-center-pane",
        disableClose: true,
        autoFocus: false,
      },
    );

    confirmDialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        const now = new Date().toISOString();
        const currentUser = approver.username;

        const rawItems = this.itemsDataSource?.data ?? [];
        const detail: inventory_update_requests_detail[] = rawItems
          .filter((item) => item?.inventoryId != null)
          .map((item) => {
            const finalLocationName =
              item.locationName ??
              item.locationFullName ??
              this.getLocationNameById(item.locationId) ??
              "";

            const type = item.extendExpiration ? "EXTEND" : "MOVE";

            return {
              id: item.inventoryId != null ? Number(item.inventoryId) : null,
              inventoryId: String(item.inventoryId ?? ""),
              materialId: String(item.materialIdentifier ?? ""),
              updatedBy: currentUser,
              createdTime: now,
              updatedTime: now,
              productCode: item.partNumber ?? "",
              productName: item.partNumber ?? "",
              quantity: String(item.quantity ?? ""),
              type,
              locationId: item.locationId ?? "",
              locationName: finalLocationName,
              status: "APPROVE",
              requestId: null,
              quantityChange: String(item.quantityChange ?? ""),
              expiredTime:
                type === "EXTEND"
                  ? (item.expirationDate ?? "")
                  : (item.expirationDate ?? ""),
            };
          });

        this.dialogRef.close({
          updatedItems: detail,
          updatedInventoryIds: detail.map((i) => i.id),
          selectedWarehouse: headerWarehouseValue,
          approvers: [approver.username],
          approved: true,
          autoApprove: true,
        });
      }
    });
  }

  private handleScan(raw: string): void {
    // xoá timer
    if (this.scanTimeoutId) {
      clearTimeout(this.scanTimeoutId);
      this.scanTimeoutId = null;
    }

    // trim & process
    const processed = this.processScanInput(raw || "").trim();
    if (!processed) {
      return;
    }

    // nếu kho chưa load xong, buffer lại
    if (!this.warehouseSelection?.length) {
      this.bufferedScanValue = processed;
      this.snackBar.open(
        "Danh sách kho chưa sẵn sàng. Vui lòng đợi giây lát.",
        "Đóng",
        {
          duration: 3000,
          panelClass: ["snack-info"],
          horizontalPosition: "center",
          verticalPosition: "top",
        },
      );
      return;
    }

    // tìm kho match
    const matched = this.warehouseSelection.find(
      (w) => w.name.trim().toLowerCase() === processed.toLowerCase(),
    );

    // nếu đang scan cả list
    if (this.isScanAll) {
      matched
        ? this.globalWarehouseChanged(matched)
        : this.fallbackApplyHeader(processed);
      this.isScanAll = false;
    }

    // nếu đang scan 1 dòng
    if (this.currentScanRow) {
      matched
        ? this.rowWarehouseChanged(matched, this.currentScanRow)
        : this.fallbackApplyRow(this.currentScanRow, processed);
      this.scanLoadingRow[this.currentScanRow.materialIdentifier] = false;
      this.currentScanRow = null;
    }

    // Xoá input & tập trung lại để lần scan sau không bị out-focus
    this.clearScanInput();
    this.cdr.markForCheck();
  }
  private clearScanInput(): void {
    this.scanBuffer = "";
    const inp = this.scanInput.nativeElement;
    inp.value = "";
    setTimeout(() => inp.focus(), 0);
  }

  private fallbackApplyHeader(processed: string): void {
    this.dialogForm
      .get("selectedWarehouseControl")
      ?.setValue(processed, { emitEvent: true });
    this.itemsDataSource.data.forEach((item) => {
      item.locationId = "";
      item.locationFullName = processed;
      this.getFormGroupForItem(item)
        .get("selectedWarehouseItem")
        ?.setValue(null, { emitEvent: true });
    });
    this.scanLoadingAll = false;
  }

  private fallbackApplyRow(row: MaterialItem, processed: string): void {
    row.locationId = "";
    row.locationFullName = processed;
    this.getFormGroupForItem(row)
      .get("selectedWarehouseItem")
      ?.setValue(null, { emitEvent: true });
  }

  private get expiredRows(): MaterialItem[] {
    return this.itemsDataSource.data.filter(
      (r) => r.calculatedStatus === "Expired",
    );
  }
  private processScanInput(scanValue: string): string {
    let result = scanValue;

    result = result.replace(/^LO/i, "");

    result = result.replace(/-SL(SLOT|RACK)/gi, "-$1");

    result = result.replace(/--+/g, "-");

    result = result.replace(/[^A-Za-z0-9-]+$/g, "");

    result = result.replace(/-(SLOT|RACK)/i, " - $1");

    return result.trim();
  }

  private _filterWarehouses(
    name: string,
  ): Array<{ value: string; name: string }> {
    const filterValue = name.toLowerCase();
    return this.warehouseSelection
      .filter((w) => w.name.toLowerCase().includes(filterValue))
      .slice(0, 50);
  }
  private filterByName(name: string): UserSummary[] {
    const filter = name.toLowerCase();
    return this.allApprovers.filter((u) =>
      u.username.toLowerCase().includes(filter),
    );
  }

  private applyCombinedFilters(): void {
    const combinedFilterData = {
      textFilters: this.searchTerms,
      dialogFilters: this.activeFilters,
    };
    // console.log(
    //   "[applyCombinedFilters] - combinedFilterData:",
    //   combinedFilterData,
    // );
    this.itemsDataSource.filter = JSON.stringify(combinedFilterData);

    if (this.itemsDataSource.paginator) {
      this.itemsDataSource.paginator.firstPage();
    }
  }
}
