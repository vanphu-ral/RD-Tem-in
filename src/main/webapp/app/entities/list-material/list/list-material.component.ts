import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef,
  OnDestroy,
  ChangeDetectorRef,
  QueryList,
  ViewChildren,
} from "@angular/core";
import { Router } from "@angular/router";
import { MatTableDataSource } from "@angular/material/table";
import { ChangeDetectionStrategy, signal } from "@angular/core";
import { FormBuilder, FormGroup, FormControl } from "@angular/forms";
import { MatMenuTrigger } from "@angular/material/menu";
import { MatSort } from "@angular/material/sort";
import { PageEvent, MatPaginator } from "@angular/material/paginator";
import { MatDialog } from "@angular/material/dialog";
import { Subscription, Subject, of } from "rxjs";
import { formatDate } from "@angular/common";
import {
  takeUntil,
  map,
  catchError,
  distinctUntilChanged,
  first,
  debounceTime,
  skip,
  take,
  finalize,
} from "rxjs/operators";
import * as XLSX from "xlsx";
import { SelectionModel } from "@angular/cdk/collections";
import {
  APISumaryResponse,
  RawGraphQLMaterial,
  ListMaterialService,
  DataSumary,
} from "../services/list-material.service";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { AccountService } from "app/core/auth/account.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { ActivatedRoute } from "@angular/router";
import { HttpParams } from "@angular/common/http";
import { HttpClient } from "@angular/common/http";

interface sumary_mode {
  value: string;
  name: string;
  link: string;
}
export interface ColumnConfig {
  name: string;
  matColumnDef: string;
  completed: boolean;
}
export interface columnSelectionGroup {
  name: string;
  completed: boolean;
  subtasks?: ColumnConfig[];
}
export interface FilterDialogData {
  columnName: string;
  currentValues: any[];
  selectedValues: any[];
}

@Component({
  selector: "jhi-list-material",
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormBuilder],
  templateUrl: "./list-material.component.html",
  styleUrls: ["./list-material.component.scss"],
})
export class ListMaterialComponent implements OnInit, AfterViewInit, OnDestroy {
  // #region Public properties

  value = "";
  tableMaxWidth: string = "100%";
  displayedColumns: string[] = [
    "select",
    "userData4",
    "lotNumber",
    "materialTraceId",
    "inventoryId",
    "partId",
    "partNumber",
    "calculatedStatus",
    "trackingType",
    "quantity",
    "availableQuantity",
    "locationName",
    "parentLocationId",
    "lastLocationId",
    "expirationDate",
    "receivedDate",
    "updatedDate",
    "status",
    "updatedBy",
    "manufacturingDate",
    "materialType",
    "checkinDate",
  ];
  columnSelectionGroup = signal<columnSelectionGroup>({
    name: "Select all",
    completed: false,
    subtasks: [
      { name: "Add to list", matColumnDef: "select", completed: true },
      {
        name: "Material Identifier",
        matColumnDef: "materialIdentifier",
        completed: true,
      },
      { name: "PartId", matColumnDef: "partId", completed: false },
      { name: "Status", matColumnDef: "status", completed: true },
      { name: "Part Number", matColumnDef: "partNumber", completed: true },
      {
        name: "Calculated Status",
        matColumnDef: "calculatedStatus",
        completed: false,
      },
      { name: "Tracking Type", matColumnDef: "trackingType", completed: false },
      { name: "InventoryId", matColumnDef: "inventoryId", completed: false },
      { name: "Quantity", matColumnDef: "quantity", completed: false },
      {
        name: "Available Quantity",
        matColumnDef: "availableQuantity",
        completed: true,
      },
      {
        name: "Material TraceId",
        matColumnDef: "materialTraceId",
        completed: false,
      },
      { name: "Lot Number", matColumnDef: "lotNumber", completed: true },
      { name: "User Data 4", matColumnDef: "userData4", completed: true },
      { name: "Location Name", matColumnDef: "locationName", completed: true },
      {
        name: "Parent LocationId",
        matColumnDef: "parentLocationId",
        completed: false,
      },
      {
        name: "Last LocationId",
        matColumnDef: "lastLocationId",
        completed: false,
      },
      {
        name: "Expiration Date",
        matColumnDef: "expirationDate",
        completed: true,
      },
      { name: "Received Date", matColumnDef: "receivedDate", completed: false },
      { name: "Updated Date", matColumnDef: "updatedDate", completed: false },
      { name: "Updated By", matColumnDef: "updatedBy", completed: false },
      {
        name: "Manufacturing Date",
        matColumnDef: "manufacturingDate",
        completed: false,
      },
      { name: "Material Type", matColumnDef: "materialType", completed: false },
      { name: "Checkin Date", matColumnDef: "checkinDate", completed: false },
    ],
  });
  statusOptions = [
    { value: "", view: "-- All --" },
    { value: "3", view: "Available" },
    { value: "6", view: "Consumed" },
    { value: "19", view: "Expired" },
  ];
  dataSource = new MatTableDataSource<RawGraphQLMaterial>();
  length = 0;
  pageSize = 50;
  pageIndex = 1;
  pageSizeOptions = [15, 25, 50, 100, 150];
  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  disabled = false;
  pageEvent: PageEvent | undefined;
  selection = new SelectionModel<RawGraphQLMaterial>(true, []);
  checkedCount = signal(0);
  form!: FormGroup;
  locale = "en-GB";
  sumary_modes: sumary_mode[] = [
    { value: "partNumber", name: "Part Number", link: "/list-material/sumary" },
    {
      value: "locationName",
      name: "Location Name",
      link: "/list-material/sumary",
    },
    { value: "userData4", name: "User Data 4", link: "/list-material/sumary" },
    { value: "lotNumber", name: "Lot Number", link: "/list-material/sumary" },
  ];
  sumary_modeControl = new FormControl();
  selectedAggregated: string = "";

  // đối tượng chứa cột  thông tin tìm kiếm
  public scanPending = false;
  public activeFilters: { [columnDef: string]: any[] } = {};
  searchTerms: Record<string, any> = {};
  filterModes: Record<string, string> = {};
  isLoading = false;
  filterForm!: FormGroup;

  filteredValues: any = {
    materialIdentifier: "",
  };
  isScanMode = false;
  scanResult = "";
  scanError = "";
  filterApplied: boolean = false;
  filterInputs!: QueryList<ElementRef<HTMLInputElement | HTMLSelectElement>>;
  dateInputs: Record<string, string> = {};

  // #endregion

  // #region ViewChild
  @ViewChild(MatPaginator, { static: false }) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild("menuTrigger") menuTrigger!: MatMenuTrigger;
  @ViewChild("scanInput") scanInput!: ElementRef<HTMLInputElement>;
  @ViewChildren("filterInput", { read: ElementRef })
  // #endregion

  // #region Private properties
  private sidebarSubscription!: Subscription;
  private ngUnsubscribe = new Subject<void>();
  private filterChange$ = new Subject<void>();
  private dateFilters: Record<string, string> = {};
  private readonly FULL_DATE_REGEX = /^\d{2}\/\d{2}\/\d{4}$/;

  // #endregion

  // #region Constructor
  constructor(
    public materialService: ListMaterialService,
    private dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private accountService: AccountService,
    private snackBar: MatSnackBar,
    private http: HttpClient,
    private fb: FormBuilder,
  ) {
    this.form = new FormGroup({
      sumary_modeControl: new FormControl(null),
    });
  }
  // #endregion

  // #region Lifecycle hooks
  ngOnInit(): void {
    this.isLoading = true;

    this.updateDisplayedColumns();

    const canUpdate = this.accountService.hasAnyAuthority([
      "ROLE_PANACIM_UPDATE",
      "ROLE_PANACIM_ADMIN",
    ]);
    this.materialService.selectedIds$
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((ids) => this.checkedCount.set(ids.length));

    //debounceTime
    this.filterChange$
      .pipe(debounceTime(300), takeUntil(this.ngUnsubscribe))
      .subscribe(() => {
        this.updateRouteWithFilters();
      });

    this.route.queryParams.pipe(first()).subscribe((params) => {
      const allowed = this.displayedColumns.filter((c) => c !== "select");
      for (const col of allowed) {
        const v = params[col];
        if (v != null && v !== "") {
          const m = params[col + "Mode"] || this.filterModes[col] || "contains";
          this.searchTerms[col] = { mode: m, value: v };
          this.filterModes[col] = m;
          if (this.isDateField(col)) {
            const ms = parseInt(v, 10) * 1000;
            this.dateInputs[col] = formatDate(
              new Date(ms),
              "dd/MM/yyyy",
              this.locale,
            );
          }
        }
      }
    });

    this.route.queryParams
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((params) => {
        console.log("[DBG] queryParams fired:", params);
        this.pageIndex = params.page ? +params.page - 1 : 0;
        this.pageSize = params.pageSize ? +params.pageSize : this.pageSize;
        this.isLoading = true;
        this.fetchPage(params);
      });

    this.displayedColumns = canUpdate
      ? [...this.displayedColumns]
      : this.displayedColumns.filter((c) => c !== "select");

    const allowedCols = this.displayedColumns.filter((c) => c !== "select");
  }
  onPageChange(event: PageEvent): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: event.pageIndex + 1,
        pageSize: event.pageSize,
      },
      queryParamsHandling: "merge",
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  onLoad(): void {
    const selectedMode: string = this.form.get("sumary_modeControl")?.value[0];
    let body: any;
    let apiUrl: string;
    console.log(selectedMode);
    switch (selectedMode) {
      case "partNumber":
        apiUrl = this.materialService.apiSumaryByPart;
        body = {
          partNumber: "",
          pageNumber: 1,
          itemPerPage: 50,
        };
        break;
      case "lotNumber":
        apiUrl = this.materialService.apiSumaryByLot;
        body = {
          partNumber: "",
          lotNumber: "",
          pageNumber: 1,
          itemPerPage: 50,
        };
        break;
      case "userData4":
        apiUrl = this.materialService.apiSumaryByUserData4;
        body = {
          partNumber: "",
          userData4: "",
          pageNumber: 1,
          itemPerPage: 50,
        };
        break;
      case "locationName":
        apiUrl = this.materialService.apiSumaryByLocation;
        body = {
          partNumber: "",
          locationName: "",
          pageNumber: 1,
          itemPerPage: 50,
        };
        break;
      default:
        console.warn("Chưa chọn chế độ tổng hợp.");
        return;
    }
    this.materialService.fetchDataSumary(apiUrl, body).subscribe({
      next: (response: APISumaryResponse) => {
        console.log("Dữ liệu nhận được từ API:", response);
        this.router.navigate(["/list-material/sumary"], {
          queryParams: { mode: selectedMode },
          state: { data: response.inventories },
        });
      },
      error: (err) => {
        console.error("Lỗi khi gọi API, không điều hướng", err);
      },
    });
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
    this.filterChange$.complete();
  }

  onFilterChange(col: string): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        ...this.searchTerms,
        page: 1,
        pageSize: this.pageSize,
      },
      queryParamsHandling: "merge",
    });
  }

  // #endregion

  // #region Public methods
  getColumnDisplayName(colDef: string): string {
    const columnNames: { [key: string]: string } = {
      select: "Add to list",
      materialIdentifier: "Material Identifier",
      inventoryId: "Inventory Id",
      partId: "PartId",
      lotNumber: "Lot Number",
      userData4: "User Data 4",
      calculatedStatus: "Calculated Status",
      partNumber: "Part Number",
      trackingType: "Tracking Type",
      materialTraceId: "Material TraceId",
      quantity: "Quantity",
      availableQuantity: "Available Quantity",
      status: "Status",
      locationId: "LocationId",
      parentLocationId: "Parent LocationId",
      lastLocationId: "Last LocationId",
      expirationDate: "Expiration Date",
      receivedDate: "Received Date",
      updatedDate: "Updated Date",
      updatedBy: "Updated By",
      manufacturingDate: "Manufacturing Date",
      materialType: "Material Type",
      checkinDate: "Checkin Date",
      locationName: "Location Name",
      locationFullName: "Location FullName",
      locationTypeId: "Location TypeId",
      locationTypeName: "Location TypeName",
      locationDescription: "Location Description",
    };
    return columnNames[colDef] || colDef;
  }

  getStatusLabel(code: number | string): string {
    const n = typeof code === "string" ? parseInt(code, 10) : code;
    switch (n) {
      case 3:
        return "available";
      case 6:
        return "consumed";
      case 19:
        return "expired";
      default:
        return "";
    }
  }
  setFilterMode(col: string, mode: string): void {
    this.filterModes[col] = mode;
    this.onFilterChange(col);
  }

  public applyDatePickerByPicker(col: string, date: Date): void {
    this.isLoading = true;
    this.setDateFilter(col, date);
  }
  public applyDatePickerByTyping(col: string, raw: string): void {
    console.log("Typing raw date:", col, raw);
    this.isLoading = true;
    if (raw === "") {
      this.setDateFilter(col, null);
      return;
    }
    raw = raw.trim();
    if (this.FULL_DATE_REGEX.test(raw)) {
      const [dd, mm, yyyy] = raw.split("/").map((n) => +n);
      if (
        mm >= 1 &&
        mm <= 12 &&
        dd >= 1 &&
        dd <= new Date(yyyy, mm, 0).getDate()
      ) {
        this.setDateFilter(col, new Date(yyyy, mm - 1, dd));
        return;
      }
    }
  }

  onDateInputChange(col: string, str: string): void {
    this.dateInputs[col] = str;

    if (!str) {
      delete this.searchTerms[col];
      delete this.filterModes[col];
      this.updateRouteWithFilters();
      return;
    }

    const parts = str.split("/");
    if (parts.length === 3) {
      const d = +parts[0],
        m = +parts[1] - 1,
        y = +parts[2];
      const date = new Date(y, m, d);
      if (
        date.getFullYear() === y &&
        date.getMonth() === m &&
        date.getDate() === d
      ) {
        const ts = Math.floor(date.getTime() / 1000).toString();
        this.filterModes[col] = "equals";
        this.searchTerms[col] = { mode: "equals", value: ts };
        this.updateRouteWithFilters();
      }
    }
  }

  public applySelectFilter(col: string, selected: string): void {
    if (selected) {
      this.filterModes[col] = "equals";
      this.searchTerms[col] = { mode: "equals", value: selected };
    } else {
      delete this.searchTerms[col];
      delete this.filterModes[col];
    }

    // const params: any = { page: "1", pageSize: this.pageSize.toString() };
    // Object.entries(this.searchTerms).forEach(([field, term]) => {
    //   params[field] = term.value;
    //   // nếu ẩy mode lên URL:
    //   // params[field+'Mode'] = term.mode;
    // });

    this.filterChange$.next();
  }

  handlePageEvent(e: PageEvent): void {
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
    // if (this.paginator) {
    //   this.paginator.pageIndex = this.pageIndex;
    // }
    // if (this.paginator) {
    //   this.paginator.pageIndex = this.pageIndex;
    // }

    this.fetchData();
  }

  setPageSizeOptions(setPageSizeOptionsInput: string): void {
    if (setPageSizeOptionsInput) {
      this.pageSizeOptions = setPageSizeOptionsInput
        .split(",")
        .map((str) => +str);
    }
  }

  handleRowToggle(row: RawGraphQLMaterial): void {
    const inventoryId = row.inventoryId;
    console.log(
      `[ListMaterialComponent] handleRowToggle for row. Material ID: ${inventoryId} (type: ${typeof inventoryId})`,
      JSON.stringify(row),
    );
    if (inventoryId === undefined || inventoryId === null) {
      console.error(
        "[ListMaterialComponent] Material ID is undefined or null for row:",
        JSON.stringify(row),
      );
      return;
    }
    this.materialService.toggleItemSelection(inventoryId);
  }

  public isAllSelected(): boolean {
    const page = this.dataSource.data;
    return page.length > 0 && page.every((r) => r.checked);
  }

  public isSomeSelected(): boolean {
    const page = this.dataSource.data;
    const sel = page.filter((r) => r.checked).length;
    return sel > 0 && sel < page.length;
  }
  onRowToggle(row: RawGraphQLMaterial): void {
    this.materialService.toggleItemSelection(row.inventoryId);
    row.checked = !row.checked;
  }

  updateChecked(element: any, isChecked: boolean): void {
    this.materialService.toggleItemSelection(element.inventoryId);

    console.log("Current checked count:", this.checkedCount);
    console.log("Updated dataSource:", this.dataSource.data);
  }

  isAllChecked(): boolean {
    return this.dataSource.data.every((item) => item.checked);
  }

  updateSelectedItemsCount(): void {
    this.checkedCount.set(
      this.dataSource.data.filter((item) => item.checked).length,
    );
  }

  toggleCheckbox(inventoryId: string): void {
    this.materialService.toggleItemSelection(inventoryId);
  }

  checkboxLabel(): string {
    return `${this.isAllSelected() ? "select" : "deselect"} all`;
  }

  openMenuManually(): void {
    this.menuTrigger.openMenu();
  }

  closeMenuManually(): void {
    this.menuTrigger.closeMenu();
  }

  allItemsSelected(): boolean {
    return this.dataSource.data.every((item) => item.checked);
  }

  someItemsSelected(): boolean {
    const numSelected = this.selection.selected.length;
    return numSelected > 0 && numSelected < this.dataSource.data.length;
  }

  public toggleAllRows(): void {
    const pageIds = this.dataSource.data.map((r) => r.inventoryId);
    if (this.isAllSelected()) {
      this.materialService.deselectItems(pageIds);
    } else {
      this.materialService.selectItems(pageIds);
    }
    // cập nhật UI ngay
    this.dataSource.data = this.materialService.mergeChecked(
      this.dataSource.data,
    );
  }

  startScan(): void {
    this.scanError = "";
    this.scanResult = "";
    this.isScanMode = true;
    setTimeout(() => {
      this.scanInput.nativeElement.value = "";
      this.scanInput.nativeElement.focus();
    }, 0);
  }

  exitScanMode(): void {
    this.isScanMode = false;
  }

  onScanEnter(rawValue: string): void {
    const scanString = this.scanResult.trim();
    if (!scanString) {
      this.openError("Không nhận diện được mã vật tư, vui lòng thử lại!");
      this.isScanMode = false;
      return;
    }

    const inventoryTerm = scanString.split("#")[0].trim().toLowerCase();
    if (!inventoryTerm) {
      this.openError("Không nhận diện được mã vật tư, vui lòng thử lại!");
      this.isScanMode = false;
      return;
    }
    this.filterModes["materialIdentifier"] = "equals";
    this.scanPending = true;
    this.searchTerms["materialIdentifier"] = {
      mode: "equals",
      value: inventoryTerm,
    };

    this.updateRouteWithFilters();

    this.isScanMode = false;
  }

  openError(message: string): void {
    this.snackBar.open(message, "Đóng", {
      duration: 3000,
      panelClass: ["snackbar-error"],
      horizontalPosition: "center",
      verticalPosition: "bottom",
    });
  }

  refreshScan(): void {
    this.scanResult = "";
    this.searchTerms = {};
    this.filterModes = {};

    this.materialService.clearSelection();

    this.checkedCount.set(0);

    this.dataSource.data = this.materialService.mergeChecked(
      this.dataSource.data,
    );

    this.updateRouteWithFilters();
    this.isScanMode = false;
  }

  updateDisplayedColumns(): void {
    this.displayedColumns = this.columnSelectionGroup()
      .subtasks!.filter((col) => col.completed)
      .map((col) => col.matColumnDef);
  }

  public applyFilter(colDef: string, event: Event): void {
    const raw = (event.target as HTMLInputElement).value.trim().toLowerCase();
    const mode = this.filterModes[colDef] || "contains";

    if (raw) {
      this.searchTerms[colDef] = { mode, value: raw };
    } else {
      delete this.searchTerms[colDef];
      delete this.filterModes[colDef];
    }

    this.filterChange$.next();
  }

  isDateField(colDef: string): boolean {
    const dateFields = [
      "expirationDate",
      "receivedDate",
      "updatedDate",
      "checkinDate",
    ];
    return dateFields.includes(colDef);
  }

  export(): void {
    const exportColumns = this.displayedColumns.filter(
      (col) => col !== "select" && col !== "checked",
    );

    const formattedData = this.dataSource.filteredData.map((row) => {
      const result: { [key: string]: string | number | null } = {};
      for (const col of exportColumns) {
        let value = (row as any)[col];
        if (
          [
            "expirationDate",
            "receivedDate",
            "updatedDate",
            "checkinDate",
          ].includes(col)
        ) {
          if (value) {
            let dt: Date;
            if (typeof value === "number") {
              dt = new Date(value * 1000);
            } else if (typeof value === "string" && /^\d+$/.test(value)) {
              dt = new Date(Number(value) * 1000);
            } else {
              dt = new Date(value);
            }
            value = isNaN(dt.getTime())
              ? ""
              : dt.toLocaleDateString("vi-VN", {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                });
          } else {
            value = "";
          }
        }
        // Format status
        else if (col === "status") {
          value = this.getStatusLabel(value);
        }
        result[col] = value;
      }
      return result;
    });

    const fileName =
      "Danh_sach_vat_tu_" + new Date().toISOString().slice(0, 10);
    this.materialService.exportExcel(formattedData, fileName);
  }
  // #endregion

  // #region Public methods for column selection group
  update(completed: boolean, index?: number): void {
    const currentGroup = this.columnSelectionGroup();

    if (index === undefined) {
      currentGroup.completed = completed;
      currentGroup.subtasks!.forEach(
        (subtask) => (subtask.completed = completed),
      );
    } else {
      currentGroup.subtasks![index].completed = completed;
      this.updateMasterCheckboxStatus();
    }

    this.columnSelectionGroup.set({ ...currentGroup });
    this.updateDisplayedColumns();
  }

  updateMasterCheckboxStatus(): void {
    const currentGroup = this.columnSelectionGroup();
    const allCompleted = currentGroup.subtasks!.every(
      (subtask) => subtask.completed,
    );
    const anyCompleted = currentGroup.subtasks!.some(
      (subtask) => subtask.completed,
    );
    currentGroup.completed = allCompleted;
    this.columnSelectionGroup.set({ ...currentGroup });
  }

  partiallyComplete(): boolean {
    const subtasks = this.columnSelectionGroup().subtasks;
    if (!subtasks) {
      return false;
    }
    const allCompleted = subtasks.every((subtask) => subtask.completed);
    const anyCompleted = subtasks.some((subtask) => subtask.completed);
    return anyCompleted && !allCompleted;
  }
  //   // #endregion

  public convertTimestampToDate(timestamp: number): string {
    if (!timestamp) {
      return "";
    }
    const date = new Date(timestamp * 1000);
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  }

  //   // #region Private methods
  private setDateFilter(col: string, date: Date | null): void {
    if (date) {
      // UTC-midnight → epoch seconds
      const d0 = new Date(
        Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0),
      );
      const ts = Math.floor(d0.getTime() / 1000).toString();

      this.searchTerms[col] = ts;
      this.dateInputs[col] = formatDate(d0, "dd/MM/yyyy", this.locale);
    } else {
      delete this.searchTerms[col];
      delete this.dateInputs[col];
    }

    this.filterChange$.next();
  }
  private fetchPage(params: any): void {
    const page = +params.page || 1;
    const pageSize = +params.pageSize || this.pageSize;
    const { page: _, pageSize: __, ...filters } = params;

    console.log("API filters:", filters);
    this.isLoading = true;
    this.materialService
      .fetchMaterialsData(page, pageSize, filters)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe((resp) => {
        this.length = resp.totalItems;
        this.dataSource.data = this.materialService.mergeChecked(
          resp.inventories,
        );
      });
  }

  private updateRouteWithFilters(): void {
    const params: any = { page: 1, pageSize: this.pageSize };

    Object.entries(this.searchTerms).forEach(([col, term]) => {
      if (this.isDateField(col)) {
        params[col] = term;
      } else {
        params[col] = term.value;
        params[col + "Mode"] = term.mode;
      }
    });

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: params,
      replaceUrl: true,
    });
  }

  private getCurrentPageRows(): RawGraphQLMaterial[] {
    const filtered = this.dataSource.filteredData || [];
    if (!this.paginator) {
      return filtered;
    }

    const pageIndex = this.paginator.pageIndex || 1;
    const pageSize = this.paginator.pageSize || filtered.length;
    const start = pageIndex * pageSize;
    return filtered.slice(start, start + pageSize);
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
    this.dataSource.filter = JSON.stringify(combinedFilterData);
    this.pageIndex = 1;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  private fetchData(): void {
    const params = this.route.snapshot.queryParams;
  }

  private fetchDataAndUpdateUI(params: { [key: string]: any }): void {
    const filters: any = {};
    Object.keys(params).forEach((k) => {
      if (["page", "pageSize"].includes(k)) {
        return;
      }
      if (params[k] !== "" && params[k] != null) {
        filters[k] = params[k];
      }
    });
    const page = +params.page || 1;
    const pageSize = +params.pageSize || this.pageSize;

    // const page = params["page"] ? +params["page"] : 1;
    // const pageSize = params["pageSize"] ? +params["pageSize"] : 50;

    this.materialService
      .fetchMaterialsData(page, pageSize, filters)
      .pipe(take(1))
      .subscribe((response) => {
        const items = response.inventories;
        this.materialService.updatePageData(items);
        const withCheck = this.materialService.getPageWithSelection(items);
        this.dataSource.data = withCheck;
        this.updateSelectedItemsCount();
        const totalItems = response.totalItems;
        this.length = totalItems;
        this.dataSource.data = response.inventories || [];
        console.log("trang hiện tại: ", page);
        console.log("số lượng bản ghi đọc từ api: ", totalItems);
        //scan
        if (this.scanPending) {
          if (!response.inventories.length) {
            this.openError(
              `Không tìm thấy vật tư "${this.searchTerms["materialIdentifier"].value}"`,
            );
          }
          this.scanPending = false;
          this.isScanMode = false;
        }
        //end scan
        if (this.paginator) {
          this.paginator.length = totalItems;
          this.paginator.pageIndex = page - 1;
          this.paginator.pageSize = pageSize;
          if (totalItems > 0 && this.pageIndex > 0) {
            const maxPageIndex = Math.ceil(totalItems / this.pageSize) - 1;
            if (page - 1 > maxPageIndex) {
              console.log("trang hiện tại: ", page);
              console.log("số lượng bản ghi đọc từ api: ", totalItems);
              // this.pageIndex = maxPageIndex;
              this.router.navigate([], {
                relativeTo: this.route,
                queryParams: { page: maxPageIndex + 1 },
                queryParamsHandling: "merge",
                replaceUrl: true,
              });
              return;
            }
          }
        }
        // this.checkedCount.set(
        //   response.inventories.filter((i) => i.checked).length,
        // );
        this.cdr.markForCheck();
      });
  }
}
