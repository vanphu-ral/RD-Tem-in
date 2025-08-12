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
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { ChangeDetectionStrategy, signal, NgZone } from "@angular/core";
import { FormBuilder, FormGroup, FormControl } from "@angular/forms";
import { MatMenuTrigger } from "@angular/material/menu";
import { MatSort } from "@angular/material/sort";
import { PageEvent, MatPaginator } from "@angular/material/paginator";
import { MatDialog } from "@angular/material/dialog";
import {
  faDownload,
  faChevronDown,
  faChevronRight,
  faFilter,
  faCrosshairs,
  faRotateRight,
  faListCheck,
  faEye,
  faTableColumns,
  faArrowLeft,
  faTrashCan,
  faPenToSquare,
} from "@fortawesome/free-solid-svg-icons";
import {
  filter,
  finalize,
  forkJoin,
  map,
  Observable,
  Subject,
  Subscription,
  takeUntil,
} from "rxjs";
import { SelectionModel } from "@angular/cdk/collections";
import * as XLSX from "xlsx";
import {
  RawGraphQLMaterial,
  ListMaterialService,
} from "../services/list-material.service";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MaterialUpdateService } from "../services/material-update.service";
import { AccountService } from "app/core/auth/account.service";
import saveAs from "file-saver";
import { MaterialItem } from "../dialog/list-material-update-dialog";
import { BreakpointObserver } from "@angular/cdk/layout";
import { PendingMaterialService } from "../services/pending-material.service";
import { DialogContentExampleDialogComponent } from "../confirm-dialog/confirm-dialog.component";

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
  faDownload = faDownload;
  faFilter = faFilter;
  faCrosshairs = faCrosshairs;
  faRotateRight = faRotateRight;
  faListCheck = faListCheck;
  faEye = faEye;
  faTableColumns = faTableColumns;
  faChevronRight = faChevronRight;
  faChevronDown = faChevronDown;
  faArrowLeft = faArrowLeft;
  faTrashCan = faTrashCan;
  faPenToSquare = faPenToSquare;
  dataSource = new MatTableDataSource<RawGraphQLMaterial>([]);
  selection = new SelectionModel<RawGraphQLMaterial>(true, []);
  displayedColumns: string[] = [];
  tableMaxWidth: string = "100%";
  pageEvent: PageEvent | undefined;
  length = 0;
  pageSize = 50;
  pageIndex = 0;
  currentScanLocation!: string;
  pageSizeOptions = [10, 15, 25, 50, 100];
  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  isLoading = false;
  disabled = false;
  pendingWarehouseItems = new SelectionModel<RawGraphQLMaterial>(true, []);
  warehouseScanBuffer = "";
  warehouseScanDelay = 300;
  warehouseScanTimeoutId: any;
  checkedCount$ = this.materialService.selectedItems$.pipe(
    map((items) => items?.length ?? 0),
  );

  totalQuantityselect$ = this.materialService.selectedItems$.pipe(
    map((items) => items.reduce((sum, item) => sum + (item.quantity ?? 0), 0)),
  );
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
      { name: "Updated Date", matColumnDef: "updatedDate", completed: true },
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
  @ViewChild("warehouseScanInput", { static: false })
  warehouseScanInput!: ElementRef<HTMLInputElement>;
  // #endregion

  // #region Private properties
  private dataSubscription: Subscription | undefined;
  private sidebarSubscription!: Subscription;
  private ngUnsubscribe = new Subject<void>();
  private scanBuffer = "";
  private scanTimeoutId: any;
  private readonly scanTimeoutDelay = 300;
  private focusCheckId: number | null = null;

  // #endregion

  // #region Constructor
  constructor(
    public materialService: ListMaterialService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private materialUpdateService: MaterialUpdateService,
    private accountService: AccountService,
    private breakpointObserver: BreakpointObserver,
    private ngZone: NgZone,
    private pendingMaterialService: PendingMaterialService,
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
        this.setupResponsiveColumns();
      });
    const restoredItems = this.pendingMaterialService.load();
    this.materialService.updateCurrentItems(restoredItems);
    this.pendingMaterialService.clear();

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
    this.dataSource.paginator = this.paginator;
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
      materialIdentifier: "Material Identifier",
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
    // console.log(`[setFilterMode] - Cột ${colDef} đã chọn mode: ${mode}`);
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

  handlePageEvent(evt: PageEvent): void {
    this.pageIndex = evt.pageIndex;
    this.pageSize = evt.pageSize;

    this.materialService
      .fetchMaterialsData(evt.pageIndex + 1, this.pageSize, {
        locationName: this.currentScanLocation,
      })
      .pipe(
        takeUntil(this.ngUnsubscribe),
        finalize(() => this.cdr.detectChanges()),
      )
      .subscribe((resp) => {
        this.materialService.updatePageData(resp.inventories);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        const ids = resp.inventories.map((i) => i.inventoryId);
        this.materialService.selectItems(ids);
      });
  }

  startScan(): void {
    this.isScanMode = !this.isScanMode;

    if (this.isScanMode) {
      this.scanResult = "";
      setTimeout(() => {
        this.scanInput.nativeElement.value = "";
        this.scanInput.nativeElement.focus();
        this.checkFocusLoop(); // 🔁 Bắt đầu kiểm tra focus
      }, 0);
    } else {
      this.scanInput.nativeElement.blur();
      this.stopFocusLoop(); // 🛑 Dừng kiểm tra focus
    }
  }

  onWarehouseScanInput(event: Event): void {
    const inputEl = event.target as HTMLInputElement;
    const raw = inputEl.value.trim();
    const processed = this.processScanInput(raw);

    // console.log("[SCAN] Raw input:", raw);
    // console.log("[SCAN] Processed input:", processed);

    inputEl.value = processed;

    const isEnterEvent =
      "key" in event && (event as KeyboardEvent).key === "Enter";

    if (this.warehouseScanTimeoutId) {
      clearTimeout(this.warehouseScanTimeoutId);
    }

    if (isEnterEvent) {
      // console.log("[SCAN] Trigger by Enter, call API now");
      this.scanWarehouseCode(processed);
      this.warehouseScanBuffer = "";
    } else {
      this.warehouseScanBuffer = processed;
      // console.log("[SCAN] Waiting debounce...");

      this.warehouseScanTimeoutId = setTimeout(() => {
        // console.log(
        //   "[SCAN] Timeout reached, calling API with:",
        //   this.warehouseScanBuffer,
        // );
        this.scanWarehouseCode(this.warehouseScanBuffer);
        this.warehouseScanBuffer = "";
        this.warehouseScanTimeoutId = null;
      }, this.warehouseScanDelay);
    }
  }
  scanWarehouseCode(locationName: string): void {
    this.currentScanLocation = locationName;
    this.isLoading = true;

    this.materialService
      .getInventoryScanByLocation(locationName)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        }),
      )
      .subscribe((inventories) => {
        if (!inventories.length) {
          this.snackBar.open("Không có vật tư nào!", "Đóng", {
            duration: 2000,
          });
          return;
        }

        this.materialService.cachePage(inventories);
        this.materialService.updatePageData(inventories);

        const ids = inventories.map((i) => i.inventoryId);
        this.materialService.selectItems(ids);
      });
  }
  //   scanWarehouseCode(locationName: string): void {
  //   this.currentScanLocation = locationName;
  //   this.isLoading = true;

  //   this.materialService
  //     .fetchMaterialsData(1, 1, { locationName })
  //     .pipe(
  //       takeUntil(this.ngUnsubscribe),
  //       finalize(() => {
  //         this.isLoading = false;
  //         this.cdr.detectChanges();
  //       }),
  //     )
  //     .subscribe((meta) => {
  //       const totalItems = meta.totalItems ?? 0;
  //       if (totalItems === 0) {
  //         this.snackBar.open("Không có vật tư nào!", "Đóng", { duration: 2000 });
  //         return;
  //       }

  //       this.isLoading = true;
  //       this.materialService
  //         .fetchMaterialsData(1, totalItems, { locationName })
  //         .pipe(
  //           takeUntil(this.ngUnsubscribe),
  //           finalize(() => {
  //             this.isLoading = false;
  //             this.cdr.detectChanges();
  //           }),
  //         )
  //         .subscribe((resp) => {
  //           const inventories = resp.inventories ?? [];

  //           this.materialService.cachePage(inventories);
  //           this.materialService.updatePageData(inventories);

  //           // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  //           const ids = inventories.map((i) => i.inventoryId);
  //           this.materialService.selectItems(ids);
  //         });
  //     });
  // }

  exitScanMode(): void {
    this.isScanMode = false;
    this.stopFocusLoop();
  }
  onScanInput(raw: string): void {
    this.scanBuffer = raw;

    if (this.scanTimeoutId) {
      clearTimeout(this.scanTimeoutId);
    }

    this.scanTimeoutId = setTimeout(() => {
      this.onScanEnter(this.scanBuffer);
      this.scanBuffer = "";
      this.scanTimeoutId = null;
    }, this.scanTimeoutDelay);
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
    });
  }
  onScanEnter(rawValue: string): void {
    if (this.scanTimeoutId) {
      clearTimeout(this.scanTimeoutId);
      this.scanTimeoutId = null;
    }

    this.scanTimeoutId = setTimeout(() => {
      const code = rawValue.trim().split("#")[0];
      this.isLoading = true;

      this.ngZone.runOutsideAngular(() => {
        this.materialService
          .fetchMaterialById(code)
          .pipe(takeUntil(this.ngUnsubscribe))
          .subscribe({
            next: (raw) => {
              this.ngZone.run(() => {
                this.isLoading = false;
                this.scanInput.nativeElement.value = "";
                this.scanInput.nativeElement.focus();

                if (raw) {
                  this.materialService.selectItems([raw.inventoryId]);
                  this.playAlertSoundSuccess();
                } else {
                  this.openError(`Không tìm thấy vật tư: ${code}`);
                  this.playAlertSound();
                }

                this.cdr.detectChanges();
              });
            },
            error: () => {
              this.ngZone.run(() => {
                this.isLoading = false;
                this.scanInput.nativeElement.value = "";
                this.scanInput.nativeElement.focus();

                this.openError(`Lỗi khi tìm vật tư: ${code}`);
                this.playAlertSound();
                this.cdr.detectChanges();
              });
            },
          });
      });
    }, 50);
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
    // console.log(`[applyFilter] - Cột ${colDef}:`, this.searchTerms[colDef]);

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
  removeAllFromUpdate(): void {
    const dialogRef = this.dialog.open(DialogContentExampleDialogComponent, {
      width: "320px",
      data: {
        title: "Xác nhận",
        message: "Bạn có chắc muốn xóa tất cả vật tư khỏi danh sách cập nhật?",
        confirmText: "Xóa",
        cancelText: "Hủy",
      },
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        const idsToRemove = this.selection.selected.map(
          (item) => item.inventoryId,
        );
        this.selection.clear();
        this.materialService.removeItemsAfterUpdate(idsToRemove);
      }
    });
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

    this.exitScanMode();

    dialogRef.afterClosed().subscribe((result: any) => {
      if (!result) {
        return;
      }

      const updatedIds: string[] = result.updatedItems?.map(
        (item: { id?: string | number }) =>
          item.id != null ? item.id.toString() : "",
      );
      // .filter((id) => id !== "") ?? [];

      // console.log("Updated items:", result.updatedItems);
      // console.log("Mapped updatedIds:", updatedIds);

      if (result.approved === true && result.autoApprove === true) {
        this.accountService.getAuthenticationState().subscribe((account) => {
          const currentUser = account?.login ?? "unknown";

          this.materialService
            .postApproveInventoryUpdate(
              null,
              {
                updatedItems: result.updatedItems,
                selectedWarehouse: result.selectedWarehouse,
                approvers: result.approvers ?? [],
              },
              currentUser,
            )
            .subscribe({
              next: () => {
                this.snackBar.open(
                  "Đã tự phê duyệt và cập nhật danh sách",
                  "Đóng",
                  {
                    duration: 3000,
                  },
                );

                const requestedIds = result.updatedItems
                  .map((item: any) => {
                    const id = item.inventoryId ?? item.id;
                    return id != null ? Number(id) : null;
                  })
                  .filter((id: number | null) => typeof id === "number");

                this.dataSource.data = this.dataSource.data.filter(
                  (row) => !requestedIds.includes(row.inventoryId),
                );
                // console.log("requestedIds typeof:", typeof requestedIds[0]);
                // console.log("updatedItems:", result.updatedItems);
                // console.log(
                //   "row.inventoryId typeof:",
                //   typeof this.dataSource.data[0]?.inventoryId,
                // );
                this.materialService.removeItemsAfterUpdate(requestedIds);
                this.selection.clear();
                this.cdr.detectChanges();
              },
              error: (err) => {
                console.error("Lỗi khi cập nhật vật tư:", err);
                this.snackBar.open("Cập nhật thất bại", "Đóng", {
                  duration: 3000,
                  panelClass: ["snack-error"],
                });
              },
            });
        });

        return;
      }

      // ✅ Nếu không autoApprove thì gửi đề nghị
      if (!result.updatedItems?.length) {
        return;
      }

      this.accountService.getAuthenticationState().subscribe((account) => {
        const currentUser = account?.login ?? "unknown";

        this.materialService
          .postInventoryUpdateRequest(result, currentUser)
          .subscribe({
            next: () => {
              this.snackBar.open("Gửi yêu cầu thành công", "Đóng", {
                duration: 3000,
              });

              const requestedIds = result.updatedItems
                .map(
                  (item: { inventoryId: string | number }) => item.inventoryId,
                )
                .filter((id: string) => id !== "");

              this.dataSource.data = this.dataSource.data.filter(
                (row) => !requestedIds.includes(row.inventoryId),
              );

              this.materialService.removeItemsAfterUpdate(requestedIds);
              this.selection.clear();
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error("Lỗi khi gửi yêu cầu:", err);
              this.snackBar.open("Gửi yêu cầu thất bại", "Đóng", {
                duration: 3000,
              });
            },
          });
      });
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
  setupResponsiveColumns(): void {
    this.breakpointObserver.observe("(max-width: 481px)").subscribe((state) => {
      const isMobile = state.matches;
      const group = this.columnSelectionGroup();

      const updatedSubtasks = group.subtasks
        ? group.subtasks.map((task) => {
            const isMobileSensitive = [
              "userData4",
              "lotNumber",
              "partNumber",
              "updatedDate",
              "expirationDate",
            ].includes(task.matColumnDef);
            if (isMobile && isMobileSensitive) {
              return { ...task, completed: false };
            }
            return task;
          })
        : [];

      this.columnSelectionGroup.set({
        ...group,
        subtasks: updatedSubtasks,
      });

      this.updateDisplayedColumns();
    });
  }

  updateDisplayedColumns(): void {
    this.displayedColumns = this.columnSelectionGroup()
      .subtasks!.filter((col) => col.completed)
      .map((col) => col.matColumnDef);
    this.cdr.detectChanges();
  }

  exportUpdatedMaterials(): void {
    this.isLoading = true;

    const headers = [
      "STT",
      "Material Identifier",
      "Part Number",
      "Lot Number",
      "Location Name",
      "User Data 4",
      "Received Date",
      "Available Quantity",
      "Expiration Date",
      "Status",
      "Updated By",
    ];

    const statusMap: Record<string, string> = {
      "3": "Available",
      "6": "Consume",
      "19": "Expired",
    };

    const formatDate = (epoch: any): string => {
      if (!epoch || isNaN(+epoch)) {
        return "";
      }
      const date = new Date(Number(epoch) * 1000);
      return `${String(date.getDate()).padStart(2, "0")}/${String(date.getMonth() + 1).padStart(2, "0")}/${date.getFullYear()}`;
    };

    const rows = [...this.dataSource.data]
      .sort((a, b) => Number(b.availableQuantity) - Number(a.availableQuantity))
      .map((row, i) => ({
        STT: i + 1,
        "Material Identifier": row.materialIdentifier,
        "Part Number": row.partNumber,
        "Lot Number": row.lotNumber,
        "Location Name": row.locationName,
        "User Data 4": row.userData4,
        "Received Date": formatDate(row.receivedDate),
        "Available Quantity": row.availableQuantity,
        "Expiration Date": formatDate(row.expirationDate),
        Status: statusMap[String(row.status)] || row.status,
        "Updated By": row.updatedBy,
      }));

    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(rows, {
      header: headers,
    });
    ws["!cols"] = headers.map(() => ({ width: 25 }));
    ws["!freeze"] = { ySplit: 1 };

    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Vật Tư Đã Cập Nhật");

    const wbout = XLSX.write(wb, { bookType: "xlsx", type: "array" });

    saveAs(
      new Blob([wbout], { type: "application/octet-stream" }),
      "VatTu_CapNhat.xlsx",
    );
    this.isLoading = false;
    this.cdr.markForCheck();
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
  private processScanInput(scanValue: string): string {
    let result = scanValue;

    result = result.replace(/^LO/i, "");

    result = result.replace(/-SL(SLOT|RACK)/gi, "-$1");

    result = result.replace(/--+/g, "-");

    result = result.replace(/[^A-Za-z0-9-]+$/g, "");

    result = result.replace(/-(SLOT|RACK)/i, " - $1");

    return result.trim();
  }
  private checkFocusLoop(): void {
    if (
      this.isScanMode &&
      document.activeElement !== this.scanInput.nativeElement
    ) {
      this.scanInput.nativeElement.focus();
    }

    if (this.isScanMode) {
      this.focusCheckId = requestAnimationFrame(() => this.checkFocusLoop());
    }
  }
  private stopFocusLoop(): void {
    if (this.focusCheckId !== null) {
      cancelAnimationFrame(this.focusCheckId);
      this.focusCheckId = null;
    }
  }

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
    // console.log(
    //   "[applyCombinedFilters] - combinedFilterData:",
    //   combinedFilterData,
    // );
    this.dataSource.filter = JSON.stringify(combinedFilterData);

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
