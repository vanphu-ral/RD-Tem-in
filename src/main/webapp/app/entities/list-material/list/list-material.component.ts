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
import { Subscription, Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import * as XLSX from "xlsx";
import { SelectionModel } from "@angular/cdk/collections";
import {
  RawGraphQLMaterial,
  ListMaterialService,
} from "../services/list-material.service";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { AccountService } from "app/core/auth/account.service";
import { MatSnackBar } from "@angular/material/snack-bar";

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
  pageSize = 15;
  pageIndex = 0;
  pageSizeOptions = [10, 15, 25, 50, 100];
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

    this.materialService.materialsData$
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((data) => {
        this.dataSource.data = data;
        this.checkedCount.set(data.filter((i) => i.checked).length);
        this.cdr.markForCheck();
      });
    this.materialService.totalCount$
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((total) => {
        this.length = total;
        this.cdr.markForCheck();
      });

    this.fetchData();

    this.materialService.selectedIds$
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((ids) => {
        this.checkedCount.set(ids.length);
      });

    // this.sidebarSubscription = this.sidebarService.sidebarToggled$.pipe(takeUntil(this.ngUnsubscribe)).subscribe(isSidebarOpen => {
    //   this.updateTableMaxWidth(isSidebarOpen);
    // });

    this.materialService.totalCount$
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((count) => {
        this.length = count;
        this.cdr.markForCheck();
      });

    this.dataSource.filterPredicate = (
      data: RawGraphQLMaterial,
      filter: string,
    ): boolean => {
      const { textFilters, dialogFilters } = JSON.parse(filter) as {
        textFilters: { [col: string]: { mode: string; value: string } };
        dialogFilters: { [col: string]: any[] };
      };

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

        // 1) Cột status: chuyển number → label
        if (colDef === "status") {
          const n =
            typeof cellValueRaw === "string"
              ? parseInt(cellValueRaw, 10)
              : cellValueRaw;
          cellValue = this.getStatusLabel(n).toLowerCase();
        }
        // 2) Các cột date
        else if (
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
        }
        // 3) Các cột còn lại: normal text
        else {
          cellValue = String(cellValueRaw).trim().toLowerCase();
        }

        // So sánh theo mode
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
          case "not_contains":
            if (cellValue.includes(searchTerm)) {
              return false;
            }
            break;
          case "not_equals":
            if (cellValue === searchTerm) {
              return false;
            }
            break;
        }
      }

      return true;
    };
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
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

  public applyFilter(colDef: string, event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    const selectedMode = this.filterModes[colDef] || "contains";
    this.searchTerms[colDef] = {
      mode: selectedMode,
      value: filterValue.trim().toLowerCase(),
    };
    console.log(`[applyFilter] - Cột ${colDef}:`, this.searchTerms[colDef]);
    this.filterApplied = Object.values(this.searchTerms).some(
      (term) => !!term.value,
    );
    this.applyCombinedFilters();
    this.pageIndex = 0;
    this.fetchData();
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

    const pageIndex = this.paginator.pageIndex || 0;
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

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  private fetchData(): void {
    const filtersObject = Object.entries(this.searchTerms)
      .filter(([_, t]) => !!t.value)
      .reduce(
        (acc, [key, t]) => ({
          ...acc,
          [`filter.${key}.mode`]: t.mode,
          [`filter.${key}.value`]: t.value,
        }),
        {},
      );
    const filterString = JSON.stringify(filtersObject);

    this.materialService.fetchMaterialsData(
      this.pageIndex * this.pageSize,
      this.pageSize,
      filterString,
    );
  }

  //   private updateTableMaxWidth(isSidebarOpen: boolean): void {
  //     if (isSidebarOpen) {
  //       this.tableMaxWidth = '1880px';
  //     } else {
  //       this.tableMaxWidth = '1670px';
  //     }
  //     this.cdr.markForCheck();
  //   }
  //   public exportExcel(jsonData: any[], fileName: string): void {
  //     const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(jsonData);
  //     const wb: XLSX.WorkBook = { Sheets: { 'data': ws }, SheetNames: ['data'] };
  //     const excelBuffer: any = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
  //     this.saveExcelFile(excelBuffer, fileName);
  //   }
  //   private saveExcelFile(buffer: any, fileName: string): void {
  //     const data: Blob = new Blob([buffer], { type: this.fileType });
  //     FileSaver.saveAs(data, fileName + this.fileExtension);
  //   }
  // #endregion
}
