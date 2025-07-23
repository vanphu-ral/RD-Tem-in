import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ViewChildren,
  QueryList,
  ElementRef,
  OnDestroy,
  ChangeDetectorRef,
} from "@angular/core";
import { Router, ActivatedRoute, Params } from "@angular/router";
import { MatTableDataSource } from "@angular/material/table";
import { ChangeDetectionStrategy, signal } from "@angular/core";
import { FormBuilder, FormGroup, FormControl } from "@angular/forms";
import { MatMenuTrigger } from "@angular/material/menu";
import { MatSort } from "@angular/material/sort";
import { PageEvent, MatPaginator } from "@angular/material/paginator";
import { MatDialog } from "@angular/material/dialog";
import { Subscription, Subject } from "rxjs";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import { forkJoin } from "rxjs";
import {
  filter,
  takeUntil,
  map,
  catchError,
  distinctUntilChanged,
  first,
  debounceTime,
  finalize,
  switchMap,
} from "rxjs/operators";
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import { MatDatepickerInputEvent } from "@angular/material/datepicker";
import {
  ListMaterialService,
  RawGraphQLMaterial,
  DataSumary,
} from "../services/list-material.service";
import { Observable } from "rxjs";
import { APISumaryResponse } from "../services/list-material.service";
import { HttpParams } from "@angular/common/http";
import { HttpClient } from "@angular/common/http";

interface sumary_mode {
  value: string;
  name: string;
  link: string;
}
interface ExportDetailRow {
  [key: string]: string | number | null | undefined;
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

// export interface AggregatedDetailData {
//   [key: string]: any;
//   totalQuantity: number;
//   totalAvailableQuantity: number;
//   count: number;
//   details: RawGraphQLMaterial[];
//   detailDataSource?: MatTableDataSource<RawGraphQLMaterial>;
// }
export interface AggregatedDetailData {
  partNumber: string;
  locationName: string;
  userData4: string;
  lotNumber: string;

  detailDataSource?: MatTableDataSource<RawGraphQLMaterial>;
  detailTotalItems?: number;
  detailPageIndex?: number;
  detailPageSize?: number;
  isLoadingDetails?: boolean;
}

@Component({
  selector: "jhi-list-material-sumary",
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormBuilder],
  templateUrl: "./list-material-sumary.component.html",
  styleUrls: ["./list-material-sumary.component.scss"],
  animations: [
    trigger("detailExpand", [
      state(
        "collapsed",
        style({
          height: "0px",
          minHeight: "0",
          visibility: "hidden",
          display: "none",
        }),
      ),
      state("expanded", style({ height: "*", visibility: "visible" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)"),
      ),
    ]),
  ],
})
export class ListMaterialSumaryComponent implements OnInit, AfterViewInit {
  // #region Public properties
  // public summaryData?: APISumaryResponse;

  tableWidth: string = "100%";
  value = "";
  groupingFields: string[] = [
    "partNumber",
    "locationName",
    "userData4",
    "lotNumber",
  ];
  selectedGroupingField = this.groupingFields[0];
  checkedCount = signal(0);
  displayedColumns: string[] = [];

  // dataSource = new MatTableDataSource<AggregatedDetailData>();
  dataSource: MatTableDataSource<DataSumary>;
  length = 0;
  pageSize = 50;
  pageIndex = 1;
  pageSizeOptions = [15, 25, 50, 100, 150];
  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = true;
  disabled = false;
  pageEvent: PageEvent | undefined;
  groupedData: AggregatedDetailData[] = [];
  groupingField: string = "groupingKey";
  expandedElement: AggregatedDetailData | null = null;
  form!: FormGroup;
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
  public searchTerms: { [columnDef: string]: { value: string } } = {};
  public activeFilters: { [columnDef: string]: any[] } = {};
  public filterModes: { [columnDef: string]: string } = {};
  filterValues: Record<string, string> = {};
  public searchTermsDetail: {
    [colDetail: string]: { mode: string; value: string };
  } = {};
  public fieldLabels: Record<string, string> = {
    partNumber: "Part Number",
    locationName: "Location Name",
    userData4: "User Data 4",
    lotNumber: "Lot Number",
  };
  public activeFiltersDetail: { [colDetail: string]: any[] } = {};
  public filterModesDetail: { [colDetail: string]: string } = {};
  statusOptions = [
    { value: "", view: "-- All --" },
    { value: "available", view: "Available" },
    { value: "consumed", view: "Consumed" },
    { value: "expired", view: "Expired" },
  ];
  aggregatedParts: AggregatedDetailData[] = [];
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChildren("detailPaginator") detailPaginators!: QueryList<MatPaginator>;
  @ViewChild("menuTrigger") menuTrigger!: MatMenuTrigger;
  @ViewChild("detailPaginator") detailPaginator!: MatPaginator;
  sumaryData$: Observable<DataSumary[]> | undefined;
  private ngUnsubscribe = new Subject<void>();
  private destroy$ = new Subject<void>();
  private filterInput$ = new Subject<{ col: string; value: string }>();

  constructor(
    public materialService: ListMaterialService,
    private dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
  ) {
    this.dataSource = new MatTableDataSource<DataSumary>();
  }

  ngOnInit(): void {
    this.buildDisplayedColumns();
    this.filterInput$
      .pipe(
        debounceTime(300),
        distinctUntilChanged((a, b) => a.col === b.col && a.value === b.value),
        takeUntil(this.destroy$),
      )
      .subscribe(({ col, value }) => {
        this.filterValues[col] = value;
        const newParams = { ...this.route.snapshot.queryParams };
        if (value) {
          newParams[col] = value;
        } else {
          delete newParams[col];
        }
        newParams["page"] = 1;

        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: newParams,
          replaceUrl: true,
        });
      });

    this.form = new FormGroup({
      sumary_modeControl: new FormControl<string[]>([]),
    });

    // this.sumaryData$ = this.materialService.sumaryData$;

    this.route.queryParams
      .pipe(
        filter((params) => typeof params["mode"] === "string"),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        takeUntil(this.destroy$),
      )
      .subscribe((params) => {
        const mode = params["mode"] as string;

        if (this.form.get("sumary_modeControl")!.value !== mode) {
          this.form
            .get("sumary_modeControl")!
            .setValue([mode], { emitEvent: false });
        }

        if (this.selectedGroupingField !== mode) {
          this.selectedGroupingField = mode;
          this.buildDisplayedColumns();
        }
        this.fetchDataAndUpdateUISumary(params);
      });

    this.form
      .get("sumary_modeControl")!
      .valueChanges.pipe(
        filter((arr): arr is string[] => Array.isArray(arr) && arr.length > 0),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        takeUntil(this.destroy$),
      )
      .subscribe((arr) => {
        const mode = arr[0];
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: { mode },
          queryParamsHandling: "merge",
          replaceUrl: true,
        });
      });
  }

  ngAfterViewInit(): void {
    if (this.paginator) {
      this.paginator.page
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe((pageEvent: PageEvent) => {
          this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
              page: pageEvent.pageIndex + 1,
              pageSize: pageEvent.pageSize,
            },
            queryParamsHandling: "merge",
          });
        });
    }

    // this.route.queryParams
    //   .pipe(takeUntil(this.ngUnsubscribe), first())
    //   .subscribe((params) => {
    //     if (this.paginator) {
    //       const page = +params["page"] || 1;
    //       const pageSize = +params["pageSize"] || 10;
    //       this.paginator.pageIndex = page - 1;
    //       this.paginator.pageSize = pageSize;
    //     }
    //   });
  }

  onLoad(): void {
    const selectedMode: string = this.form.get("sumary_modeControl")?.value[0];
    console.log("mode na: ", selectedMode);
    if (!selectedMode) {
      console.warn("Chưa chọn chế độ tổng hợp.");
      return;
    }

    this.router.navigate(["/list-material/sumary"], {
      queryParams: { mode: selectedMode },
    });
  }

  export(): void {
    this.isLoading = true;
    const groupColKey = this.selectedGroupingField;
    const groupColLabel = this.selectedGroupingField;

    const headers = [
      "Loại",
      "Part Number",
      groupColLabel,
      "Total Quantity",
      "Total Available Quantity",
      "Record Count",
      "Material Identifier",
      "Lot Number",
      "Location Name",
      "Received Date",
      "Status",
    ];

    const summaryRows = this.dataSource.data;
    const sheetData: any[] = [];

    const detailRequests = summaryRows.map((row) => {
      const filters = {
        partNumber: row.partNumber,
        locationName: row.locationName,
        userData4: row.userData4,
        lotNumber: row.lotNumber,
        materialIdentifier: "",
      };

      return this.materialService
        .getDetailSumary(0, Number(row.recordCount) || 50, filters)
        .pipe(
          map((response) => ({
            row,
            details: response.inventories || [],
          })),
        );
    });

    forkJoin(detailRequests).subscribe(
      (results) => {
        results.forEach(({ row, details }) => {
          sheetData.push({
            Loại: "Tổng hợp",
            "Part Number": row.partNumber,
            [groupColLabel]: (row as any)[groupColKey],
            "Total Quantity": row.quantity,
            "Total Available Quantity": row.availableQuantity,
            "Record Count": row.recordCount,
          });

          details.forEach((d) => {
            sheetData.push({
              Loại: "Chi tiết",
              "Part Number": row.partNumber,
              [groupColLabel]: (row as any)[groupColKey],
              "Total Quantity": row.quantity,
              "Total Available Quantity": row.availableQuantity,
              "Record Count": row.recordCount,
              "Material Identifier": d.materialIdentifier,
              "Lot Number": d.lotNumber,
              "Location Name": d.locationName,
              "Received Date": d.receivedDate,
              Status: d.status,
            });
          });
        });

        const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(sheetData, {
          header: headers,
        });

        ws["!cols"] = headers.map((h) => ({ width: 25 }));
        ws["!freeze"] = { ySplit: 1 };

        const wb: XLSX.WorkBook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, "Summary & Detail");

        const wbout: ArrayBuffer = XLSX.write(wb, {
          bookType: "xlsx",
          type: "array",
        });

        Promise.resolve().then(() => {
          saveAs(
            new Blob([wbout], { type: "application/octet-stream" }),
            "VậtTư_TổngHợp.xlsx",
          );
          this.isLoading = false;
          this.cdr.markForCheck();
        });
        saveAs(
          new Blob([wbout], { type: "application/octet-stream" }),
          "VậtTư_TổngHợp.xlsx",
        );
        this.isLoading = false;
      },
      (err) => {
        console.error("Lỗi khi export:", err);
        this.isLoading = false;
      },
    );
  }

  goBackToList(): void {
    this.router.navigate(["/list-material"]);
  }

  handlePageEvent(e: PageEvent): void {
    this.pageEvent = e;
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
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

  exportDetailExcel(element: AggregatedDetailData): void {
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
    ];

    const statusMap: Record<string, { text: string; color: string }> = {
      "3": { text: "Available", color: "C6EFCE" },
      "6": { text: "Consume", color: "F8CBAD" },
      "19": { text: "Expired", color: "FFE699" },
    };

    const formatDate = (epoch: any): string => {
      if (!epoch) {
        return "";
      }
      const d = new Date(Number(epoch) * 1000);
      return `${String(d.getDate()).padStart(2, "0")}/${String(d.getMonth() + 1).padStart(2, "0")}/${d.getFullYear()}`;
    };

    const rows = (element.detailDataSource?.data ?? []).map((row, i) => {
      const statusObj = statusMap[String(row.status)] ?? {
        text: row.status,
        color: "FFFFFF",
      };
      return {
        STT: i + 1,
        "Material Identifier": row.materialIdentifier,
        "Part Number": row.partNumber,
        "Lot Number": row.lotNumber,
        "Location Name": row.locationName,
        "User Data 4": row.userData4,
        "Received Date": formatDate(row.receivedDate),
        "Available Quantity": row.availableQuantity,
        "Expiration Date": formatDate(row.expirationDate),
        Status: statusObj.text,
        StatusColor: statusObj.color, // phụ để styling sau
      };
    });

    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(
      rows.map((r) => {
        const { StatusColor, ...rest } = r;
        return rest;
      }),
      { header: headers },
    );

    // Thiết lập độ rộng
    ws["!cols"] = headers.map(() => ({ width: 20 }));
    ws["!freeze"] = { ySplit: 1 };

    // Style màu cho từng dòng Status
    rows.forEach((r, i) => {
      const rowIndex = i + 1; // +1 vì header ở hàng 0
      const cellAddr = XLSX.utils.encode_cell({
        r: rowIndex,
        c: headers.indexOf("Status"),
      });

      if (ws[cellAddr]) {
        ws[cellAddr].s = {
          fill: { fgColor: { rgb: r.StatusColor } },
          font: { bold: true },
          alignment: { horizontal: "center" },
        };
      }
    });

    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Chi Tiết Vật Tư");

    const wbout: ArrayBuffer = XLSX.write(wb, {
      bookType: "xlsx",
      type: "array",
    });

    saveAs(
      new Blob([wbout], { type: "application/octet-stream" }),
      "ChiTiet_VatTu.xlsx",
    );
  }

  openMenuManually(): void {
    this.menuTrigger.openMenu();
  }

  closeMenuManually(): void {
    this.menuTrigger.closeMenu();
  }
  refreshFilters(): void {
    this.filterValues = {};

    const { mode, pageSize } = this.route.snapshot.queryParams;

    const newParams: any = { page: 1 };
    if (mode) {
      newParams.mode = mode;
    }
    if (pageSize) {
      newParams.pageSize = pageSize;
    }
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: newParams,
      replaceUrl: true,
    });
  }

  applyFilter(col: string, value: string): void {
    value = value.trim();
    if (!value) {
      return;
    }

    this.isLoading = true;
    this.filterInput$.next({ col, value });
  }

  public setFilterMode(colDef: string, mode: string): void {
    this.filterModes[colDef] = mode;
    this.selectedGroupingField = colDef;
    this.buildDisplayedColumns();
  }

  public applyDateFilter(
    row: AggregatedDetailData,
    colDetail: string,
    event: MatDatepickerInputEvent<Date>,
  ): void {
    const dateValue: Date | null = event.value;
    if (dateValue) {
      const formattedDate = dateValue
        .toLocaleDateString("vi-VN", {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
        })
        .toLowerCase();
      this.searchTermsDetail[colDetail] = {
        mode: "equals",
        value: formattedDate,
      };
    } else {
      this.searchTermsDetail[colDetail] = { mode: "contains", value: "" };
    }
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

  // loadDataByMode(mode: string): void {
  //   let body: any;
  //   let apiUrl: string;

  //   switch (mode) {
  //     case "partNumber":
  //       apiUrl = this.materialService.apiSumaryByPart;
  //       body = { partNumber: "", pageNumber: 1, itemPerPage: 50 };
  //       break;
  //     case "lotNumber":
  //       apiUrl = this.materialService.apiSumaryByLot;
  //       body = {
  //         partNumber: "",
  //         lotNumber: "",
  //         pageNumber: 1,
  //         itemPerPage: 50,
  //       };
  //       break;
  //     case "userData4":
  //       apiUrl = this.materialService.apiSumaryByUserData4;
  //       body = {
  //         partNumber: "",
  //         userData4: "",
  //         pageNumber: 1,
  //         itemPerPage: 50,
  //       };
  //       break;
  //     case "locationName":
  //       apiUrl = this.materialService.apiSumaryByLocation;
  //       body = {
  //         partNumber: "",
  //         locationName: "",
  //         pageNumber: 1,
  //         itemPerPage: 50,
  //       };
  //       break;
  //     default:
  //       console.warn("Chế độ không hợp lệ:", mode);
  //       return;
  //   }

  //   this.materialService.fetchDataSumary(apiUrl, body).subscribe({
  //     next: (response: APISumaryResponse) => {
  //       this.dataSource.data = response.inventories;
  //     },
  //     error: (err) => {
  //       console.error("Lỗi khi fetch dữ liệu theo mode:", err);
  //     },
  //   });
  // }

  toggleRow(element: AggregatedDetailData): void {
    // Nếu click vào dòng đang mở thì đóng lại
    if (this.expandedElement === element) {
      this.expandedElement = null;
      return;
    }

    // Mở dòng mới
    this.expandedElement = element;

    // Nếu dữ liệu đã được tải trước đó thì không làm gì cả
    if (element.detailDataSource) {
      return;
    }

    element.isLoadingDetails = true;
    const initialPageIndex = 0;
    const initialPageSize = 20;

    const filters = {
      partNumber: element.partNumber,
      locationName: element.locationName,
      userData4: element.userData4,
      lotNumber: element.lotNumber,
      materialIdentifier: "",
    };
    element.isLoadingDetails = true;
    this.materialService
      .getDetailSumary(initialPageIndex, initialPageSize, filters)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        finalize(() => (element.isLoadingDetails = false)),
      )
      .subscribe((response) => {
        element.detailTotalItems = response.totalItems;
        element.detailPageIndex = initialPageIndex;
        element.detailPageSize = initialPageSize;
        element.detailDataSource = new MatTableDataSource(response.inventories);
        console.log("Dữ liệu bảng con:", response.inventories);
        const ds = new MatTableDataSource(response.inventories);
        ds.filterPredicate = this.detailFilterPredicate;
        ds.filter = JSON.stringify({ textFilters: this.searchTermsDetail });
        element.detailDataSource = ds;

        this.cdr.markForCheck();
      });
  }

  handleDetailPaging(event: PageEvent, element: AggregatedDetailData): void {
    element.isLoadingDetails = true;

    const filters = {
      partNumber: element.partNumber,
      locationName: element.locationName,
      userData4: element.userData4,
      lotNumber: element.lotNumber,
    };

    this.materialService
      .getDetailSumary(event.pageIndex, event.pageSize, filters)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((response) => {
        element.detailTotalItems = response.totalItems;
        element.detailPageIndex = event.pageIndex;
        element.detailPageSize = event.pageSize;
        element.detailDataSource!.data = response.inventories;

        element.isLoadingDetails = false;
        this.cdr.markForCheck();
      });
  }
  applySelectFilterDetail(
    row: AggregatedDetailData,
    col: string,
    value: string,
  ): void {
    const filterValue = (value || "").trim().toLowerCase();
    const mode = this.filterModesDetail[col] || "equals";

    this.searchTermsDetail[col] = { mode, value: filterValue };

    if (row.detailDataSource) {
      row.detailDataSource.filter = JSON.stringify({
        textFilters: this.searchTermsDetail,
      });
    }
  }

  applyDetailFilter(
    element: AggregatedDetailData,
    colDetail: string,
    event: Event,
  ): void {
    const value = (event.target as HTMLInputElement).value.trim().toLowerCase();
    const mode = this.filterModesDetail[colDetail] || "contains";

    this.searchTermsDetail[colDetail] = { mode, value };

    const filters = {
      partNumber: element.partNumber,
      locationName: element.locationName,
      userData4: element.userData4,
      lotNumber: element.lotNumber,
      materialIdentifier: value,
    };

    element.isLoadingDetails = true;

    this.materialService
      .getDetailSumary(0, element.detailPageSize ?? 20, filters)
      .pipe(finalize(() => (element.isLoadingDetails = false)))
      .subscribe((response) => {
        element.detailTotalItems = response.totalItems;
        element.detailPageIndex = 0;
        element.detailPageSize = element.detailPageSize ?? 20;
        element.detailDataSource = new MatTableDataSource(response.inventories);
        this.cdr.markForCheck();
      });
  }

  public handleFatherPaging(event: PageEvent): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: event.pageIndex + 1,
        pageSize: event.pageSize,
      },
      queryParamsHandling: "merge",
    });
  }

  public setFilterModeDetail(
    row: AggregatedDetailData,
    colDt: string,
    mode: string,
  ): void {
    this.filterModesDetail[colDt] = mode;
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

  public detailFilterPredicate = (data: any, filterdetail: string): boolean => {
    let combined: { textFilters: any; dialogFilters: any };
    try {
      combined = JSON.parse(filterdetail);
    } catch {
      return true;
    }
    const tf = combined.textFilters || {};
    for (const col of Object.keys(tf)) {
      const { mode, value } = tf[col];
      const term = (value || "").trim().toLowerCase();
      if (!term) {
        continue;
      }
      let cellValue: string;
      if (col === "status") {
        cellValue = this.getStatusLabel(data.status).toLowerCase();
      } else if (
        [
          "expirationDate",
          "receivedDate",
          "updatedDate",
          "checkinDate",
        ].includes(col)
      ) {
        const ts = Number((data as any)[col] || 0) * 1000;
        const dt = new Date(ts);
        cellValue = isNaN(dt.getTime())
          ? ""
          : dt
              .toLocaleDateString("vi-VN", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
              })
              .toLowerCase();
      } else {
        cellValue = String((data as any)[col] ?? "")
          .trim()
          .toLowerCase();
      }
      switch (mode) {
        case "equals":
          if (cellValue !== term) {
            return false;
          }
          break;
        case "not_equals":
          if (cellValue === term) {
            return false;
          }
          break;
        case "contains":
          if (!cellValue.includes(term)) {
            return false;
          }
          break;
        case "not_contains":
          if (cellValue.includes(term)) {
            return false;
          }
          break;
      }
    }
    return true;
  };

  private buildDisplayedColumns(): void {
    const cols = ["expand"];
    cols.push("partNumber");
    if (this.selectedGroupingField !== "partNumber") {
      cols.push(this.selectedGroupingField);
    }
    cols.push("quantity", "availableQuantity", "recordCount");

    this.displayedColumns = cols;
  }

  private clearSummary(): void {
    this.length = 0;
    this.pageIndex = 0;
    this.dataSource.data = [];
    if (this.paginator) {
      this.paginator.length = 0;
      this.paginator.pageIndex = 0;
    }
    this.cdr.markForCheck();
  }

  private fetchDataAndUpdateUISumary(params: { [key: string]: any }): void {
    const mode = params["mode"] as string | undefined;

    if (!mode) {
      this.clearSummary();
      return;
    }

    const page = +params["page"] || 1;
    const pageSize = +params["pageSize"] || 50;

    const body: Record<string, any> = {
      pageNumber: page,
      itemPerPage: pageSize,
    };

    let apiUrl: string;
    switch (mode) {
      case "partNumber":
        apiUrl = this.materialService.apiSumaryByPart;
        body.partNumber = params.partNumber ?? "";
        break;
      case "lotNumber":
        apiUrl = this.materialService.apiSumaryByLot;
        body.partNumber = params.partNumber ?? "";
        body.lotNumber = params.lotNumber ?? "";
        break;
      case "userData4":
        apiUrl = this.materialService.apiSumaryByUserData4;
        body.partNumber = params.partNumber ?? "";
        body.userData4 = params.userData4 ?? "";
        break;
      case "locationName":
        apiUrl = this.materialService.apiSumaryByLocation;
        body.partNumber = params.partNumber ?? "";
        body.locationName = params.locationName ?? "";
        break;
      default:
        console.warn("Unknown summary mode:", mode);
        this.clearSummary();
        return;
    }

    this.isLoading = true;
    this.materialService
      .fetchDataSumary(apiUrl, body)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe((response) => {
        console.log("response  API:", response);
        console.log("response.totalItems:", response.totalItems);
        const totalItems = response.totalItems;
        this.length = totalItems;
        this.pageIndex = page - 1;
        this.dataSource.data = response.inventories || [];
        if (this.paginator) {
          this.paginator.length = totalItems;
          this.paginator.pageIndex = page - 1;
        }
        this.cdr.markForCheck();
      });
  }

  // private fetchDataAndUpdateUISumaryDetail(params: {
  //   [key: string]: any;
  // }): void {
  //   const allowedFilterKeys = [
  //     "partNumber",
  //     "locationName",
  //     "userDate4",
  //     "lotNumber",
  //   ];
  //   const filters: { [key: string]: any } = {};
  //   for (const key of allowedFilterKeys) {
  //     if (params[key] !== undefined) {
  //       filters[key] = params[key];
  //     }
  //   }
  //   const page = params["page"] ? +params["page"] : 1;
  //   const pageSize = params["pageSize"] ? +params["pageSize"] : 50;

  //   this.materialService
  //     .fetchMaterialsData(page, pageSize, filters)
  //     .pipe(takeUntil(this.ngUnsubscribe))
  //     .subscribe((response) => {
  //       const totalItems = response.totalItems;
  //       this.length = totalItems;
  //       this.dataSource.data = response.inventories || [];
  //       console.log("trang hiện tại: ", page);
  //       console.log("số lượng bản ghi đọc từ api: ", totalItems);
  //       if (this.paginator) {
  //         this.paginator.length = totalItems;
  //         this.paginator.pageIndex = page - 1;
  //         this.paginator.pageSize = pageSize;
  //         if (totalItems > 0 && this.pageIndex > 0) {
  //           const maxPageIndex = Math.ceil(totalItems / this.pageSize) - 1;
  //           if (page - 1 > maxPageIndex) {
  //             console.log("trang hiện tại: ", page);
  //             console.log("số lượng bản ghi đọc từ api: ", totalItems);
  //             // this.pageIndex = maxPageIndex;
  //             this.router.navigate([], {
  //               relativeTo: this.route,
  //               queryParams: { page: maxPageIndex + 1 },
  //               queryParamsHandling: "merge",
  //               replaceUrl: true,
  //             });
  //             return;
  //           }
  //         }
  //       }
  //       this.checkedCount.set(
  //         response.inventories.filter((i) => i.checked).length,
  //       );
  //       this.cdr.markForCheck();
  //     });
  // }
}
