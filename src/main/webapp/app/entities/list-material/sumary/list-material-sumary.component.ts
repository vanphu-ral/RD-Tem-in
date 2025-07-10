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
import {
  filter,
  takeUntil,
  map,
  catchError,
  distinctUntilChanged,
  first,
  debounceTime,
  finalize,
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

  export(): void {}

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

  exportDetailExcel(row: AggregatedDetailData): void {
    if (!row.detailDataSource) {
      console.warn("Không có dữ liệu bảng con để export.");
      return;
    }
    const detailColumns = [
      "partNumber",
      "lotNumber",
      "receivedDate",
      "availableQuantity",
      "expirationDate",
      "status",
    ];
    const dataSource = row.detailDataSource;
    const pageIndex = dataSource.paginator?.pageIndex ?? 0;
    const pageSize =
      dataSource.paginator?.pageSize ?? dataSource.filteredData.length;
    const startIndex = pageIndex * pageSize;
    const endIndex = startIndex + pageSize;
    const currentPageData = dataSource.filteredData.slice(startIndex, endIndex);

    const formattedDetails: ExportDetailRow[] = currentPageData.map(
      (detail: RawGraphQLMaterial) => {
        const result: ExportDetailRow = {};
        this.groupingFields.forEach((f) => {});
        detailColumns.forEach((col) => {
          if (col === "receivedDate" || col === "expirationDate") {
            // Format ngày tháng
            result[col] = this.convertTimestampToDate((detail as any)[col]);
          } else if (col === "status") {
            result[col] = this.getStatusLabel((detail as any)[col]);
          } else {
            result[col] = (detail as any)[col];
          }
        });
        return result;
      },
    );

    const groupName = this.groupingFields.join("_");
    const fileName = `báo_cáo_tổng_hợp_chi_tiet_theo_${groupName}`;
    this.materialService.exportExcel(formattedDetails, fileName);
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

  applyFilter(col: string, e: KeyboardEvent): void {
    const value = (e.target as HTMLInputElement).value.trim();
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
    };

    this.materialService
      .getDetailSumary(initialPageIndex, initialPageSize, filters)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((response) => {
        element.detailTotalItems = response.totalItems;
        element.detailPageIndex = initialPageIndex;
        element.detailPageSize = initialPageSize;
        element.detailDataSource = new MatTableDataSource(response.inventories);

        element.isLoadingDetails = false;
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
        // Cập nhật dữ liệu cho DataSource đã có
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
    this.searchTermsDetail[col] = {
      mode,
      value: filterValue,
    };
  }
  applyDetailFilter(
    row: AggregatedDetailData,
    colDetail: string,
    event: Event,
  ): void {
    const filterValue = (event.target as HTMLInputElement).value
      .trim()
      .toLowerCase();
    const selectedMode = this.filterModesDetail[colDetail] || "contains";
    this.searchTermsDetail[colDetail] = {
      mode: selectedMode,
      value: filterValue,
    };
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

    this.isLoading = false;
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
