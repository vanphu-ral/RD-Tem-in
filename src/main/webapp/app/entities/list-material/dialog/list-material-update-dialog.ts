import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef,
  OnDestroy,
  ChangeDetectorRef,
  Inject,
} from "@angular/core";
import { Router } from "@angular/router";
import { MatTableDataSource } from "@angular/material/table";
import { ChangeDetectionStrategy, signal, WritableSignal } from "@angular/core";
import { FormBuilder, FormGroup, FormControl, FormArray } from "@angular/forms";
import { MatMenuTrigger } from "@angular/material/menu";
import { MatSort } from "@angular/material/sort";
import { PageEvent, MatPaginator } from "@angular/material/paginator";
// import { KeycloakApiService,UserDto  } from "../services/KeycloakApiService";
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { Subscription, Observable } from "rxjs";
import { SelectionModel } from "@angular/cdk/collections";
import {
  RawGraphQLMaterial,
  ListMaterialService,
  RawGraphQLLocation,
} from "../services/list-material.service";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MaterialUpdateService } from "../services/material-update.service";
import { startWith, map } from "rxjs/operators";
import {
  DialogContentExampleDialogComponent,
  ConfirmDialogData,
} from "../confirm-dialog/confirm-dialog.component";
import { MatRadioButton } from "@angular/material/radio";

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
  myControl = new FormControl("");
  headerQuantityChange: number | null = null;
  filteredOptions!: string[];
  // approvers: WritableSignal<UserDto[]> = signal<UserDto[]>([]);
  // selectedApprover: WritableSignal<UserDto | null> = signal<UserDto | null>(null);

  selectApprover: WritableSignal<SelectApproverpprover> =
    signal<SelectApproverpprover>({
      name: "Select all",
      completed: false,
      sub: [
        { name: "admin1", completed: false },
        { name: "admin2", completed: false },
        { name: "admin3", completed: false },
        { name: "admin4", completed: false },
        { name: "admin5", completed: false },
      ],
    });
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
  editableFields: string[] = ["quantity", "locationName"];
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
  selectedSubApproverIndex: number = -1;
  public searchTerms: { [columnDef: string]: { mode: string; value: string } } =
    {};
  public activeFilters: { [columnDef: string]: any[] } = {};
  public filterModes: { [columnDef: string]: string } = {};
  private isSelectHeader: boolean = false;
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
    // private kcApi: KeycloakApiService,
  ) {
    this.dialogForm = this.fb.group({
      selectedWarehouseControl: [null],
    });

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
          locationName: rawItem.locationName,
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
        value: loc.locationId,
        name: loc.locationName,
      }));

      this.itemsDataSource.data.forEach((item) => {
        const formGroup = this.getFormGroupForItem(item);
        const initialWarehouseObject = this.warehouseSelection.find(
          (w) => w.value === item.locationId,
        );
        formGroup
          .get("selectedWarehouseControl")
          ?.setValue(initialWarehouseObject ?? null, { emitEvent: false });
      });

      this.dialogForm
        .get("selectedWarehouseControl")
        ?.updateValueAndValidity({ emitEvent: true });
    });
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
        map((value: unknown) =>
          typeof value === "string"
            ? value
            : (value as { name: string })?.name || "",
        ),
        map((name: string) => {
          // Lọc theo tên
          const filtered = name
            ? this._filterWarehouses(name)
            : this.warehouseSelection.slice();

          // Sắp xếp ưu tiên tên ngắn lên trước
          return filtered.sort((a, b) => a.name.length - b.name.length);
        }),
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
  getFormGroupForItem(item: MaterialItem): FormGroup {
    const itemIdentifier = item.materialIdentifier;
    if (!this.itemFormGroups.has(itemIdentifier)) {
      const control = new FormControl();
      control.valueChanges.subscribe((selectedWarehouseObj) => {
        if (
          selectedWarehouseObj &&
          typeof selectedWarehouseObj === "object" &&
          selectedWarehouseObj.value !== undefined
        ) {
          if (item.locationId !== selectedWarehouseObj.value) {
            item.locationId = selectedWarehouseObj.value;
            console.log(
              `Row: Item ${item.materialIdentifier} locationId updated to: ${item.locationId}`,
            );
          }
        } else if (
          selectedWarehouseObj === null ||
          selectedWarehouseObj === ""
        ) {
          if (item.locationId !== "" && item.locationId !== null) {
            item.locationId = "";
            console.log(
              `Row: Item ${item.materialIdentifier} locationId cleared.`,
            );
          }
        }
      });
      const group = this.fb.group({ selectedWarehouseItem: control });
      this.itemFormGroups.set(itemIdentifier, group);
      this.rowFilteredWarehouses.set(
        itemIdentifier,
        control.valueChanges.pipe(
          startWith(""),
          map((value: string | { name: string } | null): string =>
            typeof value === "string" ? value : value ? value.name : "",
          ),
          map((filterValue: string) => {
            const unsorted = filterValue
              ? this._filterWarehouses(filterValue)
              : this.warehouseSelection.slice();
            const sorted = unsorted
              .concat()
              .sort((a, b) => a.name.length - b.name.length);
            return sorted;
          }),
        ),
      );
    }
    return this.itemFormGroups.get(itemIdentifier)!;
  }
  // onSelectApprover(user: UserDto): void {
  //   this.selectedApprover.set(user);
  // }
  rowWarehouseChanged(selectedWarehouse: Warehouse, item: MaterialItem): void {
    const formGroup = this.getFormGroupForItem(item);
    formGroup
      .get("selectedWarehouseItem")
      ?.setValue(selectedWarehouse, { emitEvent: true });
    item.selectedWarehouse = selectedWarehouse;
    item.extendExpiration = false;
    console.log(
      `Row change: Item ${item.materialIdentifier} locationId updated to: ${item.locationId}`,
    );
    this.isSelectHeader = false;
  }

  globalWarehouseChanged(selectedWarehouse: Warehouse): void {
    this.dialogForm
      .get("selectedWarehouseControl")
      ?.setValue(selectedWarehouse, { emitEvent: false });
    console.log(`Header: Warehouse selected: ${selectedWarehouse.value}`);
    if (this.itemsDataSource && this.itemsDataSource.data) {
      this.itemsDataSource.data.forEach((item) => {
        const formGroup = this.getFormGroupForItem(item);
        formGroup
          .get("selectedWarehouseItem")
          ?.setValue(selectedWarehouse, { emitEvent: true });
        item.selectedWarehouse = selectedWarehouse;
        console.log(
          `Header update: Item ${item.materialIdentifier} locationId updated to: ${item.locationId}`,
        );
      });
    }
    this.isSelectHeader = true;
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
  scanLocationForRow(item: MaterialItem): void {
    this.scanLoadingRow[item.materialIdentifier] = true;
    this.currentScanRow = item;
    setTimeout(() => this.scanInput.nativeElement.focus(), 0);
  }
  scanLocationForAll(): void {
    this.scanLoadingAll = true;
    this.isScanAll = true;
    setTimeout(() => this.scanInput.nativeElement.focus(), 0);
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
        const formGroup = this.getFormGroupForItem(item);
        formGroup
          .get("selectedWarehouseItem")
          ?.setValue(null, { emitEvent: true });
        this.scanLoadingRow[item.materialIdentifier] = false;
      });
      this.scanLoadingAll = false;
      this.cdr.markForCheck();
    }, 300);
  }

  onScanInputEnter(event: Event): void {
    const rawValue = (event.target as HTMLInputElement).value;
    const processed = this.processScanInput(rawValue);

    const matchedWarehouse = this.warehouseSelection.find(
      (w) => w.name.trim().toLowerCase() === processed.trim().toLowerCase(),
    );

    if (this.isScanAll) {
      if (matchedWarehouse) {
        this.globalWarehouseChanged(matchedWarehouse);
      }
      this.itemsDataSource.data.forEach((item) => {
        this.scanLoadingRow[item.materialIdentifier] = false;
      });
      this.isScanAll = false;
      this.scanLoadingAll = false;
    } else if (this.currentScanRow) {
      if (matchedWarehouse) {
        this.rowWarehouseChanged(matchedWarehouse, this.currentScanRow);
      } else {
        this.currentScanRow.locationId = "";
        this.currentScanRow.locationName = processed;
        const formGroup = this.getFormGroupForItem(this.currentScanRow);
        formGroup
          .get("selectedWarehouseItem")
          ?.setValue(null, { emitEvent: true });
      }
      this.scanLoadingRow[this.currentScanRow.materialIdentifier] = false;
      this.currentScanRow = null;
    }

    (event.target as HTMLInputElement).value = "";
    this.cdr.markForCheck();
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

  onSelectSubApprover(idx: number): void {
    this.selectedSubApproverIndex = idx;
    const current = this.selectApprover();
    current.sub.forEach((sub, i) => (sub.completed = i === idx));
    this.selectApprover.set({ ...current });
  }
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
    console.log(`[applyFilter] - Cột ${colDef}:`, this.searchTerms[colDef]);
    this.applyCombinedFilters();
  }

  setFilterMode(column: any, mode: string): void {
    this.filterModes[column] = mode;
  }

  displayWarehouseFn(warehouse: { value: string; name: string }): string {
    return warehouse && warehouse.name ? warehouse.name : "";
  }

  onSave(): void {
    const selectedWarehouseValue = this.dialogForm.get(
      "selectedWarehouseControl",
    )?.value;
    const selectedApprovers = this.selectApprover()
      .sub.filter((s) => s.completed)
      .map((s) => s.name);

    if (!selectedApprovers.length) {
      this.snackBar.open("Yêu cầu chọn người duyệt!", "Đóng", {
        duration: 3000,
        panelClass: ["snackbar-error"],
      });
      return;
    }
    const headerWarehouseValue = this.dialogForm.get("selectedWarehouseControl")
      ?.value?.value;
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
        { duration: 3000, panelClass: ["snackbar-error"] },
      );
      return;
    }

    const allRowsValid = this.itemsDataSource.data.every(
      (item) =>
        headerWarehouseSet ||
        !!item.selectedWarehouse?.value ||
        item.extendExpiration,
    );

    if (!allRowsValid) {
      this.snackBar.open(
        "Vui lòng với mỗi hàng chọn Chuyển kho hoặc Gia hạn",
        "Đóng",
        { duration: 3000, panelClass: ["snackbar-error"] },
      );
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
        if (this.isSelectHeader) {
          this.dialogRef.close({
            updatedItems: this.itemsDataSource.data,
            selectedWarehouse: selectedWarehouseValue,
            approvers: selectedApprovers,
          });
        } else {
          this.dialogRef.close({
            updatedItems: this.itemsDataSource.data,
            selectedWarehouse: null,
            approvers: selectedApprovers,
          });
        }
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onCellBlur(element: MaterialItem, columnName: keyof MaterialItem): void {}

  partiallyComplete(): boolean {
    const subtasks = this.selectApprover().sub;
    if (!subtasks) {
      return false;
    }
    const allCompleted = subtasks.every((sub) => sub.completed);
    const anyCompleted = subtasks.some((sub) => sub.completed);
    return anyCompleted && !allCompleted;
  }

  update(completed: boolean, index?: number): void {
    const currentGroup = this.selectApprover();

    if (index === undefined) {
      currentGroup.completed = completed;
      currentGroup.sub!.forEach((sub) => (sub.completed = completed));
    } else {
      currentGroup.sub![index].completed = completed;
    }

    this.selectApprover.set({ ...currentGroup });
  }

  toggleAllExtendExpiration(checked: boolean): void {
    if (this.itemsDataSource && this.itemsDataSource.data) {
      this.itemsDataSource.data.forEach((row) => {
        if (row.extendExpiration !== checked) {
          row.extendExpiration = checked;
        }
      });
    }
  }

  isAllExtendExpirationSelected(): boolean {
    if (
      !this.itemsDataSource ||
      !this.itemsDataSource.data ||
      this.itemsDataSource.data.length === 0
    ) {
      return false;
    }
    return this.itemsDataSource.data.every((row) => row.extendExpiration);
  }

  isSomeExtendExpirationSelected(): boolean {
    if (
      !this.itemsDataSource ||
      !this.itemsDataSource.data ||
      this.itemsDataSource.data.length === 0
    ) {
      return false;
    }
    const numSelected = this.itemsDataSource.data.filter(
      (row) => row.extendExpiration,
    ).length;
    return numSelected > 0 && numSelected < this.itemsDataSource.data.length;
  }
  // #endregion

  // #region Private methods
  private processScanInput(scanValue: string): string {
    let result = scanValue;
    if (result.startsWith("LO")) {
      result = result.slice(2);
    }
    const slIndex = result.search(/-SL/i);
    if (slIndex > -1) {
      result = result.substring(0, slIndex);
    }
    result = result.replace(/[^A-Za-z0-9-]+$/g, "");

    return result.trim();
  }
  private _filterWarehouses(
    name: string,
  ): Array<{ value: string; name: string }> {
    const filterValue = name.toLowerCase();
    return this.warehouseSelection.filter((warehouse) =>
      warehouse.name.toLowerCase().includes(filterValue),
    );
  }
  private applyCombinedFilters(): void {
    const combinedFilterData = {
      textFilters: this.searchTerms,
      dialogFilters: this.activeFilters,
    };
    console.log(
      "[applyCombinedFilters] - combinedFilterData:",
      combinedFilterData,
    );
    this.itemsDataSource.filter = JSON.stringify(combinedFilterData);

    if (this.itemsDataSource.paginator) {
      this.itemsDataSource.paginator.firstPage();
    }
  }
}
