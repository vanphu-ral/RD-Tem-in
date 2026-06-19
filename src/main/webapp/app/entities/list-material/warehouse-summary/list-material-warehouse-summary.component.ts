import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostBinding,
  OnDestroy,
  OnInit,
} from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { Observable, Subject, forkJoin, of } from "rxjs";
import {
  debounceTime,
  distinctUntilChanged,
  finalize,
  map,
  startWith,
  takeUntil,
} from "rxjs/operators";
import {
  faArrowLeft,
  faMapMarkerAlt,
  faRotateRight,
  faWarehouse,
} from "@fortawesome/free-solid-svg-icons";
import {
  ListMaterialService,
  WarehouseAreaLocation,
  WarehouseAreaOption,
  WarehouseAreaSummaryRow,
  WarehouseFloorPlanContext,
  WarehouseLocationInventoryRow,
  WarehouseSummaryStats,
} from "../services/list-material.service";

@Component({
  selector: "jhi-list-material-warehouse-summary",
  templateUrl: "./list-material-warehouse-summary.component.html",
  styleUrls: ["./list-material-warehouse-summary.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListMaterialWarehouseSummaryComponent
  implements OnInit, OnDestroy
{
  faArrowLeft = faArrowLeft;
  faRotateRight = faRotateRight;
  faWarehouse = faWarehouse;
  faMapLocationDot = faMapMarkerAlt;

  filterForm!: FormGroup;
  locationSearchForm!: FormGroup;
  selectedTabIndex = 0;

  displayedColumns = [
    "areaCode",
    "areaName",
    "materialType",
    "materialIdentifierCount",
    "quantity",
  ];
  locationMaterialColumns = [
    "materialIdentifier",
    "materialName",
    "itemCode",
    "partNumber",
    "quantity",
    "lotNumber",
    "materialType",
    "status",
  ];

  @HostBinding("class.floor-plan-tab-active")
  get isFloorPlanTabActive(): boolean {
    return this.selectedTabIndex === 1;
  }

  dataSource = new MatTableDataSource<WarehouseAreaSummaryRow>([]);
  locationMaterialsSource =
    new MatTableDataSource<WarehouseLocationInventoryRow>([]);

  stats: WarehouseSummaryStats = {
    warehouseCount: 0,
    locationCount: 0,
    emptyLocationCount: 0,
    availableQuantity: 0,
    materialTypeCount: 0,
    unavailableQuantity: 0,
  };

  warehouseOptions: WarehouseAreaOption[] = [];
  warehouseAreaDescriptions: string[] = [];
  materialTypeOptions: string[] = [];
  filteredWarehouses$: Observable<WarehouseAreaOption[]> = of([]);
  filteredWarehouseAreaDescriptions$: Observable<string[]> = of([]);
  filteredMaterialTypes$: Observable<string[]> = of([]);

  floorPlanContext: WarehouseFloorPlanContext | null = null;
  floorPlanLocations: WarehouseAreaLocation[] = [];
  legendMaterialTypes: string[] = [];
  selectedLocation: WarehouseAreaLocation | null = null;
  selectedSummaryRow: WarehouseAreaSummaryRow | null = null;

  isLoading = false;
  isFloorPlanLoading = false;
  isMaterialsLoading = false;
  length = 0;
  pageIndex = 0;
  pageSize = 50;
  pageSizeOptions = [25, 50, 100, 200];
  floorPlanLocationCount = 0;

  private readonly destroy$ = new Subject<void>();
  private readonly locationSearch$ = new Subject<string>();
  private readonly materialTypeColors = [
    "#3b82f6",
    "#22c55e",
    "#f59e0b",
    "#ef4444",
    "#8b5cf6",
    "#06b6d4",
    "#ec4899",
    "#84cc16",
  ];

  constructor(
    private fb: FormBuilder,
    private materialService: ListMaterialService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private hostElement: ElementRef<HTMLElement>,
  ) {}

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      warehouseName: [""],
      warehouseAreaName: [""],
      materialType: [""],
    });
    this.locationSearchForm = this.fb.group({
      locationSearch: [""],
    });
    this.initFilterAutocomplete();
    this.initLocationSearch();
    this.loadData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearch(): void {
    this.pageIndex = 0;
    this.loadData();
  }

  onRefresh(): void {
    this.loadData(true);
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  onSummaryRowClick(row: WarehouseAreaSummaryRow): void {
    this.selectedSummaryRow = row;
    this.floorPlanContext = {
      areaCode: row.areaCode ?? "",
      areaName: row.areaName ?? "",
      materialType: row.materialType ?? "",
    };
    this.selectedLocation = null;
    this.locationMaterialsSource.data = [];
    this.locationSearchForm.patchValue(
      { locationSearch: "" },
      { emitEvent: false },
    );
    this.selectedTabIndex = 1;
    this.loadFloorPlan("");
    this.cdr.markForCheck();
    requestAnimationFrame(() => this.scrollToFloorPlanWorkspace());
  }

  onLocationSelect(location: WarehouseAreaLocation): void {
    this.selectedLocation = location;
    this.loadLocationMaterials(location);
    this.cdr.markForCheck();
  }

  isSummaryRowActive(row: WarehouseAreaSummaryRow): boolean {
    if (!this.selectedSummaryRow) {
      return false;
    }
    return (
      this.selectedSummaryRow.areaCode === row.areaCode &&
      this.selectedSummaryRow.areaName === row.areaName &&
      this.selectedSummaryRow.materialType === row.materialType
    );
  }

  isLocationSelected(location: WarehouseAreaLocation): boolean {
    return this.selectedLocation?.locationId === location.locationId;
  }

  getMaterialTypeColor(materialType: string | null | undefined): string {
    const type = (materialType ?? "").trim();
    if (!type || type === "-") {
      return "#d1d5db";
    }
    const index = this.legendMaterialTypes.findIndex(
      (item) => item.toLowerCase() === type.toLowerCase(),
    );
    if (index < 0) {
      return "#9ca3af";
    }
    return this.materialTypeColors[index % this.materialTypeColors.length];
  }

  getLocationCardColor(location: WarehouseAreaLocation): string {
    if (location.empty) {
      return "#f3f4f6";
    }
    if (
      this.floorPlanContext?.materialType &&
      location.containsSelectedMaterialType
    ) {
      return this.getMaterialTypeColor(this.floorPlanContext.materialType);
    }
    return this.getMaterialTypeColor(location.dominantMaterialType);
  }

  getFillPercent(location: WarehouseAreaLocation): number {
    const total = location.totalQuantity ?? 0;
    const limit = location.productLimit ?? 0;
    if (limit <= 0 || total <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((total / limit) * 100));
  }

  hasProductCapacity(location: WarehouseAreaLocation): boolean {
    return (location.productLimit ?? 0) > 0;
  }

  getFillMaxLabel(location: WarehouseAreaLocation): string {
    if (location.productLimit && location.productLimit > 0) {
      return this.formatNumber(location.productLimit);
    }
    return "—";
  }

  formatInventoryStatus(status: string | number | null | undefined): string {
    const code = String(status ?? "").trim();
    switch (code) {
      case "3":
        return "Sẵn sàng";
      case "6":
        return "Đã hết";
      case "19":
        return "Đã hết hạn";
      default:
        return code || "-";
    }
  }

  goBack(): void {
    void this.router.navigate(["/list-material"]);
  }

  formatNumber(value: number | null | undefined): string {
    if (value == null || Number.isNaN(value)) {
      return "0";
    }
    return new Intl.NumberFormat("vi-VN").format(value);
  }

  displayWarehouseName(areaName: string): string {
    return areaName ?? "";
  }

  displayWarehouseAreaDescription(description: string): string {
    return description ?? "";
  }

  displayMaterialType(materialType: string): string {
    return materialType ?? "";
  }

  private scrollToFloorPlanWorkspace(): void {
    const filterBar =
      this.hostElement.nativeElement.querySelector(".filter-bar");
    filterBar?.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  private initLocationSearch(): void {
    this.locationSearch$
      .pipe(debounceTime(350), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe((term) => {
        if (this.floorPlanContext) {
          this.loadFloorPlan(term);
        }
      });

    this.locationSearchForm
      .get("locationSearch")
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value: string | null | undefined) => {
        this.locationSearch$.next((value ?? "").trim());
      });
  }

  private initFilterAutocomplete(): void {
    forkJoin({
      areas: this.materialService.fetchWarehouseAreas(),
      materialTypes: this.materialService.fetchWarehouseMaterialTypes(),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        ({
          areas,
          materialTypes,
        }: {
          areas: WarehouseAreaOption[];
          materialTypes: string[];
        }) => {
          this.warehouseOptions = [...areas].sort((a, b) =>
            (a.areaName ?? "").localeCompare(b.areaName ?? "", "vi"),
          );
          this.warehouseAreaDescriptions =
            this.buildWarehouseAreaDescriptions(areas);
          this.materialTypeOptions = [...materialTypes].sort(
            (a: string, b: string) => a.localeCompare(b, "vi"),
          );

          const warehouseControl = this.filterForm.get("warehouseName");
          if (warehouseControl) {
            this.filteredWarehouses$ = warehouseControl.valueChanges.pipe(
              startWith(warehouseControl.value ?? ""),
              map((value) => this.filterWarehouseOptions(value)),
            );
          }

          const warehouseAreaControl = this.filterForm.get("warehouseAreaName");
          if (warehouseAreaControl) {
            this.filteredWarehouseAreaDescriptions$ =
              warehouseAreaControl.valueChanges.pipe(
                startWith(warehouseAreaControl.value ?? ""),
                map((value) => this.filterWarehouseAreaDescriptions(value)),
              );
          }

          const materialTypeControl = this.filterForm.get("materialType");
          if (materialTypeControl) {
            this.filteredMaterialTypes$ = materialTypeControl.valueChanges.pipe(
              startWith(materialTypeControl.value ?? ""),
              map((value) => this.filterMaterialTypeOptions(value)),
            );
          }

          this.cdr.markForCheck();
        },
      );
  }

  private loadFloorPlan(locationSearch: string): void {
    if (!this.floorPlanContext?.areaCode) {
      return;
    }

    this.isFloorPlanLoading = true;
    this.cdr.markForCheck();

    this.materialService
      .fetchWarehouseAreaLocations({
        areaCode: this.floorPlanContext.areaCode,
        areaName: this.floorPlanContext.areaName,
        materialType: this.floorPlanContext.materialType,
        locationSearch,
      })
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isFloorPlanLoading = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe((response) => {
        this.floorPlanLocations = response.locations ?? [];
        this.legendMaterialTypes = response.legendMaterialTypes ?? [];
        this.floorPlanLocationCount = response.totalCount ?? 0;

        if (this.selectedLocation) {
          const refreshed = this.floorPlanLocations.find(
            (item) => item.locationId === this.selectedLocation?.locationId,
          );
          this.selectedLocation = refreshed ?? null;
          if (!this.selectedLocation) {
            this.locationMaterialsSource.data = [];
          }
        }

        this.cdr.markForCheck();
      });
  }

  private loadLocationMaterials(location: WarehouseAreaLocation): void {
    const locationKey = location.locationFullName || location.locationName;
    if (!locationKey) {
      this.locationMaterialsSource.data = [];
      return;
    }

    this.isMaterialsLoading = true;
    this.cdr.markForCheck();

    this.materialService
      .fetchLocationInventoryMaterials(locationKey)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isMaterialsLoading = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe((rows) => {
        this.locationMaterialsSource.data = rows ?? [];
        this.cdr.markForCheck();
      });
  }

  private buildWarehouseAreaDescriptions(
    areas: WarehouseAreaOption[],
  ): string[] {
    const seen = new Set<string>();
    const descriptions: string[] = [];
    for (const area of areas) {
      const description = (area.areaDescription ?? "").trim();
      if (!description) {
        continue;
      }
      const key = description.toLowerCase();
      if (seen.has(key)) {
        continue;
      }
      seen.add(key);
      descriptions.push(description);
    }
    return descriptions.sort((a, b) => a.localeCompare(b, "vi"));
  }

  private filterWarehouseAreaDescriptions(
    value: string | null | undefined,
  ): string[] {
    const term = (value ?? "").trim().toLowerCase();
    if (!term) {
      return this.warehouseAreaDescriptions;
    }
    return this.warehouseAreaDescriptions.filter((description) =>
      description.toLowerCase().includes(term),
    );
  }

  private filterMaterialTypeOptions(
    value: string | null | undefined,
  ): string[] {
    const term = (value ?? "").trim().toLowerCase();
    if (!term) {
      return this.materialTypeOptions;
    }
    return this.materialTypeOptions.filter((materialType) =>
      materialType.toLowerCase().includes(term),
    );
  }

  private filterWarehouseOptions(
    value: string | null | undefined,
  ): WarehouseAreaOption[] {
    const term = (value ?? "").trim().toLowerCase();
    if (!term) {
      return this.warehouseOptions;
    }
    return this.warehouseOptions.filter(
      (area) =>
        (area.areaName ?? "").toLowerCase().includes(term) ||
        (area.areaDescription ?? "").toLowerCase().includes(term),
    );
  }

  private loadData(refreshCache = false): void {
    const filters = this.filterForm.getRawValue();
    this.isLoading = true;
    this.cdr.markForCheck();

    this.materialService
      .fetchWarehouseSummary({
        warehouseName: filters.warehouseName,
        warehouseAreaName: filters.warehouseAreaName,
        materialType: filters.materialType,
        pageNumber: this.pageIndex + 1,
        itemPerPage: this.pageSize,
        refreshCache,
      })
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe((response) => {
        this.length = response.totalItems ?? 0;
        this.stats = response.stats ?? this.stats;
        this.dataSource.data = response.inventories ?? [];
        this.cdr.markForCheck();
      });
  }
}
