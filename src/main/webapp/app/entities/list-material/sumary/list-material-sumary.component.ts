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

export interface AggregatedPartData {
  [key: string]: any;
  totalQuantity: number;
  totalAvailableQuantity: number;
  count: number;
  details: RawGraphQLMaterial[];
  detailDataSource?: MatTableDataSource<RawGraphQLMaterial>;
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
  checkedCount = signal(0);
  displayedColumns: string[] = [
    "expand",
    this.groupingFields[0],
    this.groupingFields[1],
    this.groupingFields[2],
    this.groupingFields[3],
    "quantity",
    "availableQuantity",
    "recordCount",
  ];
  // dataSource = new MatTableDataSource<AggregatedPartData>();
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
  groupedData: AggregatedPartData[] = [];
  groupingField: string = "groupingKey";
  expandedElement: AggregatedPartData | null = null;
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
  public filterField = this.groupingField || "groupingKey";
  public searchTermsDetail: {
    [colDetail: string]: { mode: string; value: string };
  } = {};
  public activeFiltersDetail: { [colDetail: string]: any[] } = {};
  public filterModesDetail: { [colDetail: string]: string } = {};
  statusOptions = [
    { value: "", view: "-- All --" },
    { value: "available", view: "Available" },
    { value: "consumed", view: "Consumed" },
    { value: "expired", view: "Expired" },
  ];
  aggregatedParts: AggregatedPartData[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChildren("detailPaginator") detailPaginators!: QueryList<MatPaginator>;
  @ViewChild("menuTrigger") menuTrigger!: MatMenuTrigger;
  @ViewChild("detailPaginator") detailPaginator!: MatPaginator;
  sumaryData$: Observable<DataSumary[]> | undefined;
  private ngUnsubscribe = new Subject<void>();
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
    this.form = new FormGroup({
      sumary_modeControl: new FormControl(null),
    });

    // this.sumaryData$ = this.materialService.sumaryData$;

    this.route.queryParams
      .pipe(
        filter((params: Params) => !!params["mode"]),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        takeUntil(this.ngUnsubscribe),
      )
      .subscribe((params) => {
        console.log("URL hợp lệ, đang fetch dữ liệu với params:", params);

        const modeFromUrl = params["mode"];
        if (this.form.get("sumary_modeControl")?.value?.[0] !== modeFromUrl) {
          this.form
            .get("sumary_modeControl")
            ?.setValue([modeFromUrl], { emitEvent: false });
        }
        this.fetchDataAndUpdateUISumary(params);
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

  toggleRow(element: AggregatedPartData): void {
    this.expandedElement = this.expandedElement === element ? null : element;
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

  exportDetailExcel(row: AggregatedPartData): void {
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
        this.groupingFields.forEach((f) => {
          result[f] = row[f];
        });
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

    const groupName = this.groupingFields
      .map((f) => `${f}_${row[f]}`)
      .join("_");
    const fileName = `báo_cáo_tổng_hợp_chi_tiet_theo_${groupName}`;
    this.materialService.exportExcel(formattedDetails, fileName);
  }

  openMenuManually(): void {
    this.menuTrigger.openMenu();
  }

  closeMenuManually(): void {
    this.menuTrigger.closeMenu();
  }

  public applyFilter(colDef: string, event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value
      .trim()
      .toLowerCase();
    const selectedMode = this.filterModes[colDef] || "contains";
    const filterKey = colDef === "groupingKey" ? this.groupingField : colDef;
    if (!this.searchTerms[filterKey]) {
      this.searchTerms[filterKey] = {
        value: filterValue,
      };
    } else {
      this.searchTerms[filterKey].value = filterValue;
    }
    console.log(
      `[applyFilter] - Cột ${filterKey}:`,
      this.searchTerms[filterKey],
    );
  }

  public setFilterMode(colDef: string, mode: string): void {
    this.filterModes[colDef] = mode;
  }

  public applyDateFilter(
    row: AggregatedPartData,
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

  applySelectFilterDetail(
    row: AggregatedPartData,
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
    row: AggregatedPartData,
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
  public setFilterModeDetail(
    row: AggregatedPartData,
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

  private ensureDetailDataSource(row: AggregatedPartData): void {
    if (!row.detailDataSource) {
      row.detailDataSource = new MatTableDataSource(row.details);
    }
    row.detailDataSource.filterPredicate =
      this.detailFilterPredicate.bind(this);
  }

  private fetchDataAndUpdateUISumary(params: { [key: string]: any }): void {
    console.log("log params: ", params);
    const mode = params["mode"];
    let apiUrl: string;
    let allowedFilterKeys: string[] = [];
    switch (mode) {
      case "partNumber":
        apiUrl = this.materialService.apiSumaryByPart;
        allowedFilterKeys = ["partNumber"];
        break;
      case "lotNumber":
        apiUrl = this.materialService.apiSumaryByLot;
        allowedFilterKeys = ["partNumber", "lotNumber"];
        break;
      case "userData4":
        apiUrl = this.materialService.apiSumaryByUserData4;
        allowedFilterKeys = ["partNumber", "userData4"];
        break;
      case "locationName":
        apiUrl = this.materialService.apiSumaryByLocation;
        allowedFilterKeys = ["partNumber", "locationName"];
        break;
      default:
        console.warn(
          "Chế độ không hợp lệ hoặc không có, không fetch dữ liệu:",
          mode,
        );
        this.dataSource.data = [];
        this.length = 0;
        return;
    }

    const filters: { [key: string]: any } = {};
    for (const key of allowedFilterKeys) {
      if (params[key] !== undefined && params[key] !== null) {
        filters[key] = params[key];
      } else {
        filters[key] = "";
      }
    }

    const page = params["page"] ? +params["page"] : 1;
    const pageSize = params["pageSize"] ? +params["pageSize"] : 50;

    const body = {
      filters,
      pageNumber: page,
      itemPerPage: pageSize,
    };
    console.log("day la body: ", body);
    this.materialService
      .fetchDataSumary(apiUrl, body) //
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((response) => {
        const inventories = response.inventories || [];
        console.log("response  API:", response);
        console.log("response.totalItems:", response.totalItems);
        const totalItems = response.totalItems;
        this.length = totalItems;
        this.pageIndex = page - 1;
        // this.dataSource.data = response.inventories || [];
        this.dataSource = new MatTableDataSource(inventories);
        if (this.paginator) {
          this.paginator.length = totalItems;
          this.paginator.pageIndex = page - 1;
        }
        this.cdr.markForCheck();
      });
  }

  private fetchDataAndUpdateUISumaryDetail(params: {
    [key: string]: any;
  }): void {
    const allowedFilterKeys = [
      "partNumber",
      "locationName",
      "userDate4",
      "lotNumber",
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
