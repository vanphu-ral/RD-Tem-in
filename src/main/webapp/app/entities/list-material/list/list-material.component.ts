import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef,
  OnDestroy,
  ChangeDetectorRef,
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
import {
  takeUntil,
  map,
  catchError,
  distinctUntilChanged,
  first,
} from "rxjs/operators";
import * as XLSX from "xlsx";
import { SelectionModel } from "@angular/cdk/collections";
import {
  RawGraphQLMaterial,
  ListMaterialService,
} from "../services/list-material.service";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { AccountService } from "app/core/auth/account.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { ActivatedRoute } from "@angular/router";
import { HttpParams } from "@angular/common/http";

interface sumary_mode {
  value: string;
  name: string;
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
    { value: "available", view: "Available" },
    { value: "consumed", view: "Consumed" },
    { value: "expired", view: "Expired" },
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
  sumary_modes: sumary_mode[] = [
    { value: "partNumber", name: "Part Number" },
    { value: "locationName", name: "Location Name" },
    { value: "userData4", name: "User Data 4" },
    { value: "lotNumber", name: "Lot Number" },
  ];
  sumary_modeControl = new FormControl();
  selectedAggregated: string = "";

  // đối tượng chứa cột  thông tin tìm kiếm
  public searchTerms: { [columnDef: string]: { mode: string; value: string } } =
    {};
  public activeFilters: { [columnDef: string]: any[] } = {};

  public filterModes: { [columnDef: string]: string } = {};

  filteredValues: any = {
    materialIdentifier: "",
  };
  isScanMode = false;
  scanResult = "";
  scanError = "";
  filterApplied: boolean = false;
  // #endregion

  // #region ViewChild
  @ViewChild(MatPaginator, { static: false }) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild("menuTrigger") menuTrigger!: MatMenuTrigger;
  @ViewChild("scanInput") scanInput!: ElementRef<HTMLInputElement>;
  // #endregion

  // #region Private properties
  private sidebarSubscription!: Subscription;
  private ngUnsubscribe = new Subject<void>();
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
  ) {
    this.form = new FormGroup({
      sumary_modeControl: new FormControl(null),
    });
  }
  // #endregion

  // #region Lifecycle hooks
  ngOnInit(): void {
    this.updateDisplayedColumns();
    const canUpdate = this.accountService.hasAnyAuthority([
      "ROLE_PANACIM_UPDATE",
      "ROLE_PANACIM_ADMIN",
    ]);
    this.displayedColumns = canUpdate
      ? [...this.displayedColumns]
      : this.displayedColumns.filter((c) => c !== "select");

    // this.materialService.materialsData$
    //   .pipe(
    //     takeUntil(this.ngUnsubscribe),
    //     distinctUntilChanged(),
    //   )
    //   .subscribe((data) => {
    //     this.dataSource.data = data;
    //     this.checkedCount.set(data.filter((i) => i.checked).length);
    //     this.cdr.markForCheck();
    //   });

    // this.materialService.totalCount$
    //   .pipe(
    //     takeUntil(this.ngUnsubscribe),
    //     distinctUntilChanged(),
    //   )
    //   .subscribe((total) => {
    //     this.length = total;
    //     if (this.paginator) {
    //       this.paginator.length = total;

    //       if (total > 0 && this.pageIndex > 0) {
    //         const maxPageIndex = Math.ceil(total / this.pageSize) - 1;
    //         this.pageIndex = Math.min(this.pageIndex, maxPageIndex);
    //         this.paginator.pageIndex = this.pageIndex;
    //       }
    //     }
    //     this.cdr.markForCheck();
    //   });

    this.route.queryParams
      .pipe(
        takeUntil(this.ngUnsubscribe),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
      )
      .subscribe((params) => {
        // Gọi hàm đã sửa và truyền params mới nhất vào
        this.fetchDataAndUpdateUI(params);
      });
  }

  ngAfterViewInit(): void {
    // this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.route.queryParams
      .pipe(takeUntil(this.ngUnsubscribe), first())
      .subscribe((params) => {
        if (this.paginator) {
          const page = +params["page"] || 1;
          const pageSize = +params["pageSize"] || 10;
          this.paginator.pageIndex = page - 1;
          this.paginator.pageSize = pageSize;
        }
      });

    // Xử lý khi người dùng thay đổi trang
    this.paginator.page
      .pipe(
        takeUntil(this.ngUnsubscribe),
        distinctUntilChanged(), // Tránh trigger nhiều lần
      )
      .subscribe(() => {
        this.router.navigate(["/list-material"], {
          queryParams: {
            page: this.paginator.pageIndex + 1,
            pageSize: this.paginator.pageSize,
          },
          queryParamsHandling: "merge",
        });
      });
  }

  onLoad(): void {
    const selectedMode: string = this.form.get("sumary_modeControl")?.value;
    if (selectedMode) {
      this.router.navigate(["/list-material/sumary"], {
        queryParams: { mode: selectedMode },
      });
    } else {
      console.warn("Chưa chọn chế độ tổng hợp.");
    }
  }
  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
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
  public setFilterMode(colDef: string, mode: string): void {
    this.filterModes[colDef] = mode;
    console.log(`[setFilterMode] - Cột ${colDef} đã chọn mode: ${mode}`);
  }

  public applyDateFilter(
    colDef: string,
    event: MatDatepickerInputEvent<Date>,
  ): void {
    const dateValue: Date | null = event.value;
    if (dateValue) {
      // Chuyển đổi ngày chọn từ picker thành chuỗi theo định dạng dd/MM/yyyy
      const formattedDate = dateValue
        .toLocaleDateString("vi-VN", {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
        })
        .toLowerCase();

      console.log(
        `[applyDateFilter] Ngày được chọn từ DatePicker ở cột ${colDef}: ${formattedDate}`,
      );

      // Lưu vào searchTerms với mode "equals"
      this.searchTerms[colDef] = {
        mode: "equals",
        value: formattedDate,
      };
    } else {
      this.searchTerms[colDef] = { mode: "contains", value: "" };
    }
    this.applyCombinedFilters();
  }

  applySelectFilter(col: string, value: string): void {
    const mode = this.filterModes[col] || "equals";
    this.searchTerms[col] = { mode, value };
    this.applyCombinedFilters();
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
    const page = this.getCurrentPageRows();
    return page.length > 0 && page.every((r) => r.checked);
  }
  public isSomeSelected(): boolean {
    const page = this.getCurrentPageRows();
    const sel = page.filter((r) => r.checked).length;
    return sel > 0 && sel < page.length;
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
    const page = this.getCurrentPageRows();

    if (this.isAllSelected()) {
      page.forEach(
        (r) =>
          r.checked && this.materialService.toggleItemSelection(r.inventoryId),
      );
    } else {
      page.forEach(
        (r) =>
          !r.checked && this.materialService.toggleItemSelection(r.inventoryId),
      );
    }
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
    this.scanError = "";

    const scanString = rawValue.trim();
    if (!scanString) {
      // this.scanError = 'Không nhận diện được mã vật tư, vui lòng thử lại!';
      this.isScanMode = false;
      return;
    }

    const inventoryTerm = scanString.split("#")[0].trim().toLowerCase();
    if (!inventoryTerm) {
      this.openError("Không nhận diện được mã vật tư, vui lòng thử lại!");
      this.isScanMode = false;
      return;
    }
    this.scanResult = inventoryTerm.trim().toLowerCase();
    const mode = this.filterModes["materialIdentifier"] || "contains";
    this.searchTerms["materialIdentifier"] = {
      mode: this.filterModes["materialIdentifier"] || "equals",
      value: inventoryTerm,
    };
    this.dataSource.filter = JSON.stringify({
      textFilters: this.searchTerms,
      dialogFilters: {},
    });

    const matchCount = this.dataSource.filteredData.length;
    if (matchCount === 0) {
      this.openError(`Không tìm thấy vật tư "${inventoryTerm}"`);
      this.isScanMode = false;
      return;
    }
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
    this.scanError = "";
    this.scanResult = "";
    delete this.searchTerms["materialIdentifier"];
    this.dataSource.filter = JSON.stringify({
      textFilters: this.searchTerms,
      dialogFilters: {},
    });
    this.dataSource.paginator?.firstPage();
    this.isScanMode = false;
  }

  updateDisplayedColumns(): void {
    this.displayedColumns = this.columnSelectionGroup()
      .subtasks!.filter((col) => col.completed)
      .map((col) => col.matColumnDef);
  }

  // public applyFilter(colDef: string, event: Event): void {
  //   const filterValue = (event.target as HTMLInputElement).value;
  //   const selectedMode = this.filterModes[colDef] || "contains";
  //   this.searchTerms[colDef] = {
  //     mode: selectedMode,
  //     value: filterValue.trim().toLowerCase(),
  //   };
  //   console.log(`[applyFilter] - Cột ${colDef}:`, this.searchTerms[colDef]);
  //   this.filterApplied = Object.values(this.searchTerms).some(
  //     (term) => !!term.value,
  //   );
  //   this.applyCombinedFilters();
  //   this.pageIndex = 1;
  //   if (this.paginator) {
  //     this.paginator.pageIndex = 1;
  //   }
  //   this.fetchData();
  // }
  public applyFilter(colDef: string, event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    const selectedMode = this.filterModes[colDef] || "contains";

    this.searchTerms[colDef] = {
      mode: selectedMode,
      value: filterValue.trim().toLowerCase(),
    };

    const filters: { [key: string]: string } = {};
    Object.entries(this.searchTerms).forEach(([field, term]) => {
      if (term.value) {
        filters[field] = term.value;
      }
    });
    filters["page"] = "1";

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: filters,
      queryParamsHandling: "merge",
    });
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

  //   public applyDateFilter(colDef: string, event: MatDatepickerInputEvent<Date>): void {
  //     const dateValue: Date | null = event.value;
  //     if (dateValue) {
  //       const formattedDate = dateValue
  //         .toLocaleDateString('vi-VN', {
  //           day: '2-digit',
  //           month: '2-digit',
  //           year: 'numeric',
  //         })
  //         .toLowerCase();

  //       console.log(`[applyDateFilter] Ngày được chọn từ DatePicker ở cột ${colDef}: ${formattedDate}`);

  //       this.searchTerms[colDef] = {
  //         mode: 'equals',
  //         value: formattedDate,
  //       };
  //     } else {
  //       this.searchTerms[colDef] = { mode: 'contains', value: '' };
  //     }
  //     this.applyCombinedFilters();
  //   }

  //   openFilterDialog(colDef: string): void {
  //     const currentColumnValues = this.dataSource.data.map(
  //       // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  //       item => (item as any)[colDef],
  //     );
  //     const selectedValuesForColumn = this.activeFilters[colDef] || [];
  //     // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  //     const dialogRef = this.dialog.open(FilterDialogComponent, {
  //       width: 'auto',
  //       data: {
  //         columnName: this.getColumnDisplayName(colDef),
  //         currentValues: currentColumnValues,
  //         selectedValues: selectedValuesForColumn,
  //       },
  //     });

  //     dialogRef.afterClosed().subscribe((result: string[] | undefined) => {
  //       if (result) {
  //         this.activeFilters[colDef] = result;
  //         this.applyCombinedFilters();
  //       }
  //     });
  //   }

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

  //   // #region Private methods
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
  // .filter(([_, t]) => !!t.value)

  // private fetchData(): void {

  //   this.materialService.fetchMaterialsData(
  //     this.paginator?.pageIndex || 1, // pageIndex
  //     this.paginator?.pageSize || 10, // limit

  //   ).pipe(
  //     takeUntil(this.ngUnsubscribe)
  //   ).subscribe({
  //     next: (response) => {
  //       this.length = response.totalItems;
  //       this.dataSource.data = response.inventories || [];

  //       // Cập nhật thông tin paginator
  //       if (this.paginator) {
  //         this.paginator.length = response.totalItems;
  //       }
  //     },
  //     error: (error) => {
  //       console.error('Error fetching materials:', error);
  //       this.length = 0;
  //       this.dataSource.data = [];
  //       if (this.paginator) {
  //         this.paginator.length = 0;
  //       }
  //     }
  //   });
  // }
  // private fetchData(): void {
  //   const params = this.route.snapshot.queryParams;

  //   // Build filters object only with allowed properties
  //   const allowedFilterKeys = [
  //     'materialIdentifier', 'status', 'partNumber', 'quantity', 'availableQuantity',
  //     'lotNumber', 'userData4', 'locationName', 'expirationDate'];

  //   const filters: { [key: string]: any } = {};
  //   for (const key of allowedFilterKeys) {
  //     if (params[key] !== undefined) {
  //       filters[key] = params[key];
  //     }
  //   }
  //   const page = params.page ? +params.page : (this.paginator?.pageIndex ?? 0) + 1;

  //   this.materialService.fetchMaterialsData(
  //     page,
  //     this.paginator?.pageSize || 10,
  //     filters
  //   ).pipe(
  //     takeUntil(this.ngUnsubscribe)
  //   ).subscribe({
  //     next: (response) => {
  //       this.length = response.totalItems;
  //       this.dataSource.data = response.inventories || [];

  //       // Cập nhật paginator nhưng không thay đổi trang hiện tại
  //       if (this.paginator) {
  //         this.paginator.length = response.totalItems;
  //       }
  //     },
  //     error: (error) => {
  //       console.error('Error fetching materials:', error);
  //       this.length = 0;
  //       this.dataSource.data = [];
  //       if (this.paginator) {
  //         this.paginator.length = 0;
  //       }
  //     }
  //   });
  // }

  private fetchData(): void {
    const params = this.route.snapshot.queryParams;
  }

  private fetchDataAndUpdateUI(params: { [key: string]: any }): void {
    const allowedFilterKeys = [
      "materialIdentifier",
      "status",
      "partNumber",
      "quantity",
      "availableQuantity",
      "lotNumber",
      "userData4",
      "locationName",
      "expirationDate",
    ];
    const filters: { [key: string]: any } = {};
    for (const key of allowedFilterKeys) {
      if (params[key] !== undefined) {
        filters[key] = params[key];
      }
    }

    const page = params["page"] ? +params["page"] : 1;
    const pageSize = params["pageSize"] ? +params["pageSize"] : 50;

    this.materialService
      .fetchMaterialsData(page, pageSize, filters)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((response) => {
        const totalItems = response.totalItems;
        this.length = totalItems;
        this.dataSource.data = response.inventories || [];
        console.log("trang hiện tại: ", page);
        console.log("số lượng bản ghi đọc từ api: ", totalItems);
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
        this.checkedCount.set(
          response.inventories.filter((i) => i.checked).length,
        );
        this.cdr.markForCheck();
      });
  }
}
