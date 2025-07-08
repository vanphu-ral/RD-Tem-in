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
import { Router, ActivatedRoute } from "@angular/router";
import { MatTableDataSource } from "@angular/material/table";
import { ChangeDetectionStrategy, signal } from "@angular/core";
import { FormBuilder, FormGroup, FormControl } from "@angular/forms";
import { MatMenuTrigger } from "@angular/material/menu";
import { MatSort } from "@angular/material/sort";
import { PageEvent, MatPaginator } from "@angular/material/paginator";
import { MatDialog } from "@angular/material/dialog";
import { Subscription, Subject } from "rxjs";
import { takeUntil, startWith } from "rxjs/operators";
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
} from "../services/list-material.service";

interface sumary_mode {
  value: string;
  name: string;
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
  tableWidth: string = "100%";
  value = "";
  groupingFields: string[] = ["partNumber", "locationName"]; // mặc định
  checkedCount = signal(0);
  displayedColumns: string[] = [
    "expand",
    ...this.groupingFields,
    "totalQuantity",
    "totalAvailableQuantity",
    "count",
  ];
  dataSource = new MatTableDataSource<AggregatedPartData>();
  length = 0;
  pageSize = 15;
  pageIndex = 0;
  pageSizeOptions = [10, 15, 25, 50, 100];
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
  // #endregion

  // #region ViewChild
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChildren("detailPaginator") detailPaginators!: QueryList<MatPaginator>;
  @ViewChild("menuTrigger") menuTrigger!: MatMenuTrigger;
  @ViewChild("detailPaginator") detailPaginator!: MatPaginator;
  // #endregion

  // #region Constructor
  constructor(
    private MaterialService: ListMaterialService,
    private dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
  ) {
    this.form = new FormGroup({
      sumary_modeControl: new FormControl(null),
    });
  }
  // #endregion

  // #region Lifecycle hooks
  ngOnInit(): void {
    if (!sessionStorage.getItem("aggregatedPageReloaded")) {
      sessionStorage.setItem("aggregatedPageReloaded", "true");
      location.reload();
      return;
    } else {
      sessionStorage.removeItem("aggregatedPageReloaded");
    }
    this.route.queryParams.subscribe((params) => {
      const mainField = params["mode"] || "partNumber";
      this.groupingFields =
        mainField === "partNumber" ? ["partNumber"] : [mainField, "partNumber"];
      this.displayedColumns = [
        "expand",
        ...this.groupingFields,
        "totalQuantity",
        "totalAvailableQuantity",
        "count",
      ];
      this.MaterialService.materialsData$.subscribe(
        (data: RawGraphQLMaterial[]) => {
          console.log("[ngOnInit] Received materialsData:", data);
          this.groupData(data);
          console.log("[ngOnInit] groupedData:", this.groupedData);
          this.applyCombinedFilters();
        },
      );
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    // Khởi tạo filter predicate sau khi dữ liệu đã được gán type AggregatedPartData
    this.detailPaginators.changes
      .pipe(startWith(this.detailPaginators))
      .subscribe(() => this.bindDetailPaginators());
    this.ngOnInitFilterPredicate();
  }

  // #endregion

  // #region Public methods
  onLoad(): void {
    const selectedMode = this.form.get("sumary_modeControl")?.value;
    if (selectedMode) {
      sessionStorage.setItem("aggregatedPageReloaded", "true");
      this.router
        .navigate(["/list-material/sumary"], {
          queryParams: { mode: selectedMode, groupBy: "partNumber" },
        })
        .then(() => {
          location.reload();
        });
    }
  }

  goBackToList(): void {
    this.router.navigate(["/list-material"]);
  }

  groupData(data: RawGraphQLMaterial[]): void {
    if (!data || data.length === 0) {
      console.warn("Không có dữ liệu để nhóm.");
      return;
    }
    const groupingFields = Array.isArray(this.groupingFields)
      ? this.groupingFields
      : [];
    if (groupingFields.length === 0) {
      console.warn("Không có trường group hợp lệ!");
      return;
    }
    const groups = new Map<string, AggregatedPartData>();
    data.forEach((item) => {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-return
      const key = this.groupingFields.map((f) => (item as any)[f]).join("|");
      if (!key) {
        console.warn(`Item không có đủ trường group:`, item);
        return;
      }
      if (!groups.has(key)) {
        const group: AggregatedPartData = {
          totalQuantity: 0,
          totalAvailableQuantity: 0,
          count: 0,
          details: [],
        };
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        this.groupingFields.forEach((f) => (group[f] = (item as any)[f]));
        groups.set(key, group);
      }
      const group = groups.get(key)!;
      group.totalQuantity += Number(item.quantity) || 0;
      group.totalAvailableQuantity += Number(item.availableQuantity) || 0;
      group.count++;
      group.details.push(item);
    });
    this.groupedData = Array.from(groups.values()).filter(
      (group) => group.totalQuantity !== 0,
    );
    this.groupedData.forEach((row) => {
      row.detailDataSource = new MatTableDataSource(row.details);
    });
    this.dataSource.data = this.groupedData;
    this.length = this.groupedData.length;
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
    this.MaterialService.toggleItemSelection(inventoryId);
  }

  export(): void {
    const formattedData = this.dataSource.filteredData.map((row) => {
      const { details, detailDataSource, ...rest } = row;
      return rest;
    });
    const fileName = "báo_cáo_tổng_hợp_theo_" + this.groupingFields.join("_");
    this.MaterialService.exportExcel(formattedData, fileName);
  }

  // exportDetailExcel(row: AggregatedPartData): void {
  //   if (!row.detailDataSource) {
  //     console.warn('Không có dữ liệu bảng con để export.');
  //     return;
  //   }
  //   const detailColumns = ['partNumber', 'lotNumber', 'receivedDate', 'availableQuantity', 'expirationDate', 'status'];

  //   const groupValues: { [key: string]: any } = {};
  //   this.groupingFields.forEach(f => {
  //     groupValues[f] = row[f];
  //   });

  //   const formattedDetails: ExportDetailRow[] = row.detailDataSource.filteredData.map((detail: RawGraphQLMaterial) => {
  //     const result: ExportDetailRow = {};
  //     this.groupingFields.forEach(f => {
  //       result[f] = row[f];
  //     });
  //     detailColumns.forEach(col => {
  //       result[col] = (detail as any)[col];
  //     });
  //     return result;
  //   });

  //   const groupName = this.groupingFields.map(f => `${f}_${row[f]}`).join('_');
  //   const fileName = `báo_cáo_tổng_hợp_chi_tiet_theo_${groupName}`;
  //   this.MaterialService.exportExcel(formattedDetails, fileName);
  // }
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
            // Format status
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
    this.MaterialService.exportExcel(formattedDetails, fileName);
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
        mode: selectedMode,
        value: filterValue,
      };
    } else {
      this.searchTerms[filterKey].value = filterValue;
    }
    console.log(
      `[applyFilter] - Cột ${filterKey}:`,
      this.searchTerms[filterKey],
    );
    this.applyCombinedFilters();
  }

  public setFilterMode(colDef: string, mode: string): void {
    this.filterModes[colDef] = mode;
    this.applyCombinedFilters();
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
    this.applyCombinedFiltersDetail(row);
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
    this.applyCombinedFiltersDetail(row);
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
    // console.log(
    //   `[applyDetailFilter] - Cột ${colDetail}:`,
    //   this.searchTermsDetail[colDetail]
    // );

    // Áp dụng filter lên detailDataSource của row đó
    this.applyCombinedFiltersDetail(row);
  }
  public setFilterModeDetail(
    row: AggregatedPartData,
    colDt: string,
    mode: string,
  ): void {
    this.filterModesDetail[colDt] = mode;
    this.applyCombinedFiltersDetail(row);
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
  ngOnInitFilterPredicate(): void {
    this.dataSource.filterPredicate = (
      data: AggregatedPartData,
      filter: string,
    ): boolean => {
      const combinedFilters = JSON.parse(filter) as {
        textFilters: { [columnDef: string]: { mode: string; value: string } };
        dialogFilters: { [columnDef: string]: any[] };
      };
      if (combinedFilters.textFilters) {
        for (const colDef in combinedFilters.textFilters) {
          if (
            Object.prototype.hasOwnProperty.call(
              combinedFilters.textFilters,
              colDef,
            )
          ) {
            const filterObj = combinedFilters.textFilters[colDef];
            const searchMode = filterObj.mode;
            const searchTerm = filterObj.value.trim().toLowerCase();
            if (searchTerm === "") {
              continue;
            }
            let cellValue = (data as any)[colDef];
            cellValue = cellValue ? String(cellValue).trim().toLowerCase() : "";
            if (searchMode === "contains") {
              if (!cellValue.includes(searchTerm)) {
                return false;
              }
            } else if (searchMode === "equals") {
              if (cellValue !== searchTerm) {
                return false;
              }
            } else {
              if (!cellValue.includes(searchTerm)) {
                return false;
              }
            }
          }
        }
      }
      if (combinedFilters.dialogFilters) {
        for (const colDef in combinedFilters.dialogFilters) {
          if (
            Object.prototype.hasOwnProperty.call(
              combinedFilters.dialogFilters,
              colDef,
            )
          ) {
            const selectedValuesFromDialog =
              combinedFilters.dialogFilters[colDef];
            if (
              !selectedValuesFromDialog ||
              selectedValuesFromDialog.length === 0
            ) {
              continue;
            }
            const normalizedSelectedValues = selectedValuesFromDialog.map(
              (val) => String(val).trim().toLowerCase(),
            );
            const cellValue = (data as any)[colDef]
              ? String((data as any)[colDef])
                  .trim()
                  .toLowerCase()
              : "";
            if (!normalizedSelectedValues.includes(cellValue)) {
              return false;
            }
          }
        }
      }
      return true;
    };
  }

  // #endregion

  // #region Private methods
  public detailFilterPredicate = (data: any, filter: string): boolean => {
    let combined: { textFilters: any; dialogFilters: any };
    try {
      combined = JSON.parse(filter);
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
        case "contains":
          if (!cellValue.includes(term)) {
            return false;
          }
          break;
      }
    }
    return true;
  };
  private applyCombinedFilters(): void {
    const combinedFilterData = {
      textFilters: this.searchTerms,
      dialogFilters: this.activeFilters,
      timestamp: new Date().getTime(),
    };
    this.dataSource.filter = JSON.stringify(combinedFilterData);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  private applyCombinedFiltersDetail(row: AggregatedPartData): void {
    if (!row.detailDataSource) {
      row.detailDataSource = new MatTableDataSource(row.details);
      row.detailDataSource.filterPredicate = this.detailFilterPredicate;
    }
    this.ensureDetailDataSource(row);
    const combinedFilterData = {
      textFilters: this.searchTermsDetail,
      dialogFilters: this.activeFiltersDetail,
      timestamp: Date.now(),
    };
    row.detailDataSource.filter = JSON.stringify(combinedFilterData);
    if (row.detailDataSource.paginator) {
      row.detailDataSource.paginator.firstPage();
    }
  }

  private ensureDetailDataSource(row: AggregatedPartData): void {
    if (!row.detailDataSource) {
      row.detailDataSource = new MatTableDataSource(row.details);
    }
    row.detailDataSource.filterPredicate =
      this.detailFilterPredicate.bind(this);
  }

  private bindDetailPaginators(): void {
    const pagArr = this.detailPaginators.toArray();
    this.dataSource.data.forEach((row, idx) => {
      const ds = row.detailDataSource;
      const pag = pagArr[idx];
      if (ds && pag) {
        ds.paginator = pag;
      }
    });
    this.cdr.markForCheck();
  }

  // #endregion
}
