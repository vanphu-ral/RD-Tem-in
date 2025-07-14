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
import { filter, finalize, Subject, Subscription, takeUntil } from "rxjs";
import { SelectionModel } from "@angular/cdk/collections";
import {
  RawGraphQLMaterial,
  ListMaterialService,
} from "../services/list-material.service";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MaterialUpdateService } from "../services/material-update.service";
import { AccountService } from "app/core/auth/account.service";

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
  selector: "jhi-list-material-update",
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormBuilder],
  templateUrl: "./list-material-update.conponent.html",
  styleUrls: ["./list-material-update.component.scss"],
})
export class ListMaterialUpdateComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  // #region Public properties
  dataSource = new MatTableDataSource<RawGraphQLMaterial>([]);
  selection = new SelectionModel<RawGraphQLMaterial>(true, []);
  displayedColumns: string[] = [];
  tableMaxWidth: string = "100%";
  pageEvent: PageEvent | undefined;
  length = 0;
  pageSize = 15;
  pageIndex = 0;
  pageSizeOptions = [10, 15, 25, 50, 100];
  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  isLoading = false;
  disabled = false;
  statusOptions = [
    { value: "", view: "-- All --" },
    { value: "available", view: "Available" },
    { value: "consumed", view: "Consumed" },
    { value: "expired", view: "Expired" },
  ];
  columnSelectionGroup = signal<columnSelectionGroup>({
    name: "Select all",
    completed: false,
    subtasks: [
      { name: "Chọn", matColumnDef: "select_update", completed: true },
      {
        name: "Material Identifier",
        matColumnDef: "materialIdentifier",
        completed: true,
      },
      { name: "PartId", matColumnDef: "partId", completed: false },
      { name: "Part Number", matColumnDef: "partNumber", completed: true },
      {
        name: "Calculated Status",
        matColumnDef: "calculatedStatus",
        completed: false,
      },
      { name: "Tracking Type", matColumnDef: "trackingType", completed: false },
      { name: "InventoryId", matColumnDef: "inventoryId", completed: false },
      { name: "Quantity", matColumnDef: "quantity", completed: true },
      {
        name: "Available Quantity",
        matColumnDef: "availableQuantity",
        completed: false,
      },
      { name: "User Data 4", matColumnDef: "userData4", completed: true },
      { name: "LOT Number", matColumnDef: "lotNumber", completed: true },
      {
        name: "Material TraceId",
        matColumnDef: "materialTraceId",
        completed: false,
      },
      { name: "LocationId", matColumnDef: "locationId", completed: false },
      { name: "Location Name", matColumnDef: "locationName", completed: true },
      { name: "Status", matColumnDef: "status", completed: true },
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
  public searchTerms: { [columnDef: string]: { mode: string; value: string } } =
    {};
  public activeFilters: { [columnDef: string]: any[] } = {};
  public filterModes: { [columnDef: string]: string } = {};
  filteredValues: any = { materialIdentifier: "" };
  isScanMode = false;
  public scanResult: string = "";
  // #endregion

  // #region ViewChild
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild("menuTrigger") menuTrigger!: MatMenuTrigger;
  @ViewChild("scanInput") scanInput!: ElementRef;
  // #endregion

  // #region Private properties
  private dataSubscription: Subscription | undefined;
  private sidebarSubscription!: Subscription;
  private ngUnsubscribe = new Subject<void>();

  // #endregion

  // #region Constructor
  constructor(
    private materialService: ListMaterialService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private materialUpdateService: MaterialUpdateService,
    private accountService: AccountService,
  ) {}
  // #endregion

  // #region Lifecycle hooks
  ngOnInit(): void {
    this.materialService.selectedItems$
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((items) => {
        const rows = items.map((item) => ({
          ...item,
          select_update: true,
        }));
        this.dataSource.data = rows;
        this.selection.clear();
        rows.forEach((r) => this.selection.select(r));
        this.initializeColumnSelection(rows);
        this.updateDisplayedColumns();
      });

    // Nếu có sidebarService thì lắng nghe trạng thái sidebar ở đây

    this.dataSource.filterPredicate = (
      data: RawGraphQLMaterial,
      // eslint-disable-next-line @typescript-eslint/no-shadow
      filter: string,
    ): boolean => {
      const combinedFilters = JSON.parse(filter) as {
        textFilters: { [colDef: string]: { mode: string; value: string } };
        dialogFilters: { [colDef: string]: any[] };
      };

      for (const colDef in combinedFilters.textFilters) {
        if (
          !Object.prototype.hasOwnProperty.call(
            combinedFilters.textFilters,
            colDef,
          )
        ) {
          continue;
        }

        const { mode: searchMode, value } = combinedFilters.textFilters[colDef];
        const searchTerm = value.trim().toLowerCase();
        if (!searchTerm) {
          continue;
        }

        const cellValueRaw: any = (data as any)[colDef];
        let cellValue: string;

        if (colDef === "status") {
          const n =
            typeof cellValueRaw === "string"
              ? parseInt(cellValueRaw, 10)
              : cellValueRaw;
          cellValue = this.getStatusLabel(n).toLowerCase();
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

  ngAfterViewInit(): void {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
    if (this.sort) {
      this.dataSource.sort = this.sort;
    }
  }

  ngOnDestroy(): void {
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    if (this.sidebarSubscription) {
      this.sidebarSubscription.unsubscribe();
    }
  }
  // #endregion

  // #region Public methods
  getColumnDisplayName(colDef: string): string {
    const columnNames: { [key: string]: string } = {
      select_update: "Chọn",
      materialTraceId: "Material TraceId",
      inventoryId: "InventoryId",
      partId: "PartId",
      partNumber: "Part Number",
      userData4: "User Data 4",
      lotNumber: "Lot Number",
      calculatedStatus: "Calculated Status",
      trackingType: "Tracking Type",
      quantity: "Quantity",
      availableQuantity: "Available Quantity",
      locationId: "LocationId",
      status: "Status",
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
      // locationFullName: "Location FullName",
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

  setFilterMode(colDef: string, mode: string): void {
    this.filterModes[colDef] = mode;
    console.log(`[setFilterMode] - Cột ${colDef} đã chọn mode: ${mode}`);
  }

  applyDateFilter(colDef: string, event: MatDatepickerInputEvent<Date>): void {
    const dateValue: Date | null = event.value;
    if (dateValue) {
      const formattedDate = dateValue
        .toLocaleDateString("vi-VN", {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
        })
        .toLowerCase();
      this.searchTerms[colDef] = { mode: "equals", value: formattedDate };
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
    this.pageEvent = e;
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
  }
  startScan(): void {
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
    const code = rawValue.trim().split("#")[0];
    this.isScanMode = false;
    this.isLoading = true;

    this.materialService
      .fetchMaterialById(code)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        }),
      )
      .subscribe({
        next: (raw) => {
          if (raw) {
            this.materialService.selectItems([raw.inventoryId]);
          } else {
            this.openError(`Không tìm thấy vật tư: ${code}`);
          }
        },
        error: () => {
          this.openError(`Lỗi khi tìm vật tư: ${code}`);
        },
      });
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
    this.dataSource.data = this.materialService.mergeChecked(
      this.dataSource.data,
    );
    this.isScanMode = false;
  }
  setPageSizeOptions(setPageSizeOptionsInput: string): void {
    if (setPageSizeOptionsInput) {
      this.pageSizeOptions = setPageSizeOptionsInput
        .split(",")
        .map((str) => +str);
    }
  }

  handleRowToggle(row: RawGraphQLMaterial): void {
    this.selection.toggle(row);
    row.select_update = this.selection.isSelected(row);
    this.cdr.markForCheck();
  }

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows && numRows > 0;
  }

  someItemsSelected(): boolean {
    return this.selection.hasValue() && !this.isAllSelected();
  }

  partiallyComplete(): boolean {
    const subtasks = this.columnSelectionGroup().subtasks;
    if (!subtasks || subtasks.length === 0) {
      return false;
    }
    const numCompleted = subtasks.filter((t) => t.completed).length;
    return numCompleted > 0 && numCompleted < subtasks.length;
  }

  toggleAllRows(shouldSelectAll: boolean): void {
    if (shouldSelectAll) {
      this.dataSource.data.forEach((row) => {
        this.selection.select(row);
        row.select_update = true;
      });
    } else {
      this.dataSource.data.forEach((row) => {
        if (this.selection.isSelected(row)) {
          row.select_update = false;
        }
      });
      this.selection.clear();
      this.dataSource.data.forEach((row) => (row.select_update = false));
    }
    this.cdr.markForCheck();
  }

  checkedCount(): number {
    return this.selection.selected.length;
  }

  focusScanInput(): void {
    this.isScanMode = true;
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    setTimeout(() => this.scanInput?.nativeElement.focus(), 0);
  }

  handleScanInput(scanString: string, event?: KeyboardEvent): void {
    const parts = scanString.split("#");
    const inventoryTerm = parts[0] || "";
    if (!inventoryTerm) {
      console.warn("[handleScanInput] - Chuỗi scan không hợp lệ hoặc rỗng");
      return;
    }
    this.scanResult = inventoryTerm.trim().toLowerCase();
    const mode = this.filterModes["materialIdentifier"] || "contains";
    this.searchTerms["materialIdentifier"] = {
      mode: mode,
      value: inventoryTerm.trim().toLowerCase(),
    };
    const filterObject = {
      textFilters: this.searchTerms,
      dialogFilters: {},
    };
    this.dataSource.filter = JSON.stringify(filterObject);
    if (event != null) {
      event.stopPropagation();
      event.preventDefault();
    }
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
  public applyFilter(colDef: string, event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    // Nếu chưa chọn mode, mặc định là contains
    const selectedMode = this.filterModes[colDef] || "contains";

    // Cập nhật searchTerms với kiểu là object chứa { mode, value }
    this.searchTerms[colDef] = {
      mode: selectedMode,
      value: filterValue.trim().toLowerCase(),
    };
    console.log(`[applyFilter] - Cột ${colDef}:`, this.searchTerms[colDef]);

    this.applyCombinedFilters();
  }
  isRowSelectedForUpdate(row: RawGraphQLMaterial): boolean {
    return !!row.select_update;
  }
  update(completed: boolean, index?: number): void {
    const currentGroup = this.columnSelectionGroup();
    if (index === undefined) {
      // Checkbox "Select All Columns" thay đổi
      currentGroup.completed = completed;
      currentGroup.subtasks!.forEach(
        (subtask) => (subtask.completed = completed),
      );
    } else {
      // Một checkbox cột riêng lẻ thay đổi
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
    currentGroup.completed = allCompleted;
    // Không cần set indeterminate ở đây, template sẽ dùng partiallyComplete()
    this.columnSelectionGroup.set({ ...currentGroup });
  }
  removeRowFromUpdate(row: RawGraphQLMaterial): void {
    this.selection.deselect(row);
    this.materialService.removeItemFromUpdate(row.inventoryId);
  }
  openEditSelectedDialog(): void {
    const itemsToUpdate = this.selection.selected;

    if (itemsToUpdate.length === 0) {
      console.warn("UpdateListComponent: No items selected for update.");
      return;
    }

    const dialogRef =
      this.materialUpdateService.openEditSelectedDialog(itemsToUpdate);
    if (!dialogRef) {
      return;
    }

    dialogRef.afterClosed().subscribe((result) => {
      if (!result || !result.updatedItems?.length) {
        return;
      }

      this.accountService.getAuthenticationState().subscribe((account) => {
        const currentUser = account?.login ?? "unknown";
        this.materialService
          .postInventoryUpdateRequest(result, currentUser)
          .subscribe({
            next: (response) => {
              console.log(
                "UpdateSelectedDialog closed with data:",
                result,
                currentUser,
              );
              console.log(
                "Yêu cầu cập nhật thành công từ UpdateListComponent:",
                response,
              );
              this.snackBar.open("Gửi yêu cầu thành công", "Đóng", {
                duration: 3000,
              });
              const updatedIds = result.updatedItems.map(
                (item) => item.inventoryId,
              );
              this.dataSource.data = this.dataSource.data.filter(
                (row) => !updatedIds.includes(row.inventoryId),
              );
              this.materialService.removeItemsAfterUpdate(updatedIds);
              this.selection.clear();
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error(
                "Lỗi khi gửi yêu cầu cập nhật từ UpdateListComponent:",
                err,
              );
              this.snackBar.open("Gửi yêu cầu thất bại", "Đóng", {
                duration: 3000,
              });
            },
          });
      });

      // Gọi API cập nhật
    });
  }
  toggleRowSelectedForUpdate(
    row: RawGraphQLMaterial,
    isChecked: boolean,
  ): void {
    row.select_update = isChecked;
    if (isChecked) {
      this.selection.select(row);
    } else {
      this.selection.deselect(row);
    }
    // this.updateCheckedCount();
  }

  toggleAllRowsForUpdate(checked: boolean): void {
    if (checked) {
      this.selection.select(...this.dataSource.data);
    } else {
      this.selection.clear();
    }
    this.dataSource.data.forEach((row) => (row.select_update = checked));
  }

  goBackToList(): void {
    this.router.navigate(["/list-material"]);
  }
  updateDisplayedColumns(): void {
    this.displayedColumns = this.columnSelectionGroup()
      .subtasks!.filter((col) => col.completed)
      .map((col) => col.matColumnDef);
    this.cdr.detectChanges();
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
      "Danh_sach_vat_tu_cap_nhat_" + new Date().toISOString().slice(0, 10);
    this.materialService.exportExcel(formattedData, fileName);
  }

  initializeColumnSelection(dataItems: RawGraphQLMaterial[]): void {
    let availableColDefs: string[] = [];
    if (dataItems.length > 0) {
      availableColDefs = Object.keys(dataItems[0]).filter(
        (key) => !key.startsWith("_"),
      );
    } else {
      availableColDefs =
        this.columnSelectionGroup()
          .subtasks?.filter((st) => st.matColumnDef !== "select_update")
          .map((st) => st.matColumnDef) ?? [];
    }
  }
  // #endregion
  private onScannedCode(code: string): void {
    const cached = this.materialService.getCachedMaterial(code);
    if (cached) {
      this.materialService.toggleItemSelection(code);
      return;
    }

    this.materialService.fetchMaterialById(code).subscribe(
      (raw) => {
        if (!raw) {
          this.snackBar.open("Không tìm thấy vật tư: " + code, "Đóng", {
            duration: 2000,
          });
          return;
        }
        this.materialService.cacheMaterial(raw);
        this.materialService.toggleItemSelection(raw.inventoryId);
      },
      () => {
        this.snackBar.open("Lỗi khi fetch vật tư: " + code, "Đóng", {
          duration: 2000,
        });
      },
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
    this.dataSource.filter = JSON.stringify(combinedFilterData);

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
