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
  faCompress,
  faExpand,
  faMapMarkerAlt,
  faRotateRight,
  faWarehouse,
} from "@fortawesome/free-solid-svg-icons";
import {
  ListMaterialService,
  WarehouseAreaInventoryItem,
  WarehouseAreaLocation,
  WarehouseAreaMaterialGroup,
  WarehouseAreaOption,
  WarehouseAreaSummaryRow,
  WarehouseFloorPlanContext,
  WarehouseLocationInventoryRow,
  WarehouseLocationMaterialFilters,
  WarehouseMaterialSearchField,
  WarehouseOverviewArea,
  WarehouseOverviewMaterialType,
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
  faCompress = faCompress;
  faExpand = faExpand;
  faRotateRight = faRotateRight;
  faWarehouse = faWarehouse;
  faMapLocationDot = faMapMarkerAlt;

  filterForm!: FormGroup;
  locationSearchForm!: FormGroup;
  materialSearchForm!: FormGroup;
  overviewMaterialSearchForm!: FormGroup;
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
    "locationFullName",
    "quantity",
    "lotNumber",
    "materialType",
    "status",
  ];

  @HostBinding("class.scrollable-tab-active")
  get isScrollableTabActive(): boolean {
    return this.selectedTabIndex === 0 || this.selectedTabIndex === 1;
  }

  @HostBinding("class.is-page-fullscreen")
  isPageFullscreen = false;

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

  overviewAreas: WarehouseOverviewArea[] = [];
  overviewMaterialTypes: WarehouseOverviewMaterialType[] = [];
  selectedOverviewMaterialType: string | null = null;
  selectedOverviewArea: WarehouseOverviewArea | null = null;

  floorPlanContext: WarehouseFloorPlanContext | null = null;
  floorPlanLocations: WarehouseAreaLocation[] = [];
  groupedAreaMaterials: WarehouseAreaMaterialGroup[] = [];
  areaMaterialsTotalCount = 0;
  areaMaterialsPreviewLimited = false;
  readonly areaMaterialsPreviewLimit = 100;
  selectedMaterialOccurrences: WarehouseAreaInventoryItem[] = [];
  legendMaterialTypes: string[] = [];
  selectedLocation: WarehouseAreaLocation | null = null;
  selectedAreaMaterial: WarehouseAreaInventoryItem | null = null;
  selectedMaterialGroupKey: string | null = null;
  selectedSummaryRow: WarehouseAreaSummaryRow | null = null;
  highlightedLocationIds = new Set<number>();

  isLoading = true;
  isFloorPlanLoading = false;
  isMaterialsLoading = false;
  isAreaMaterialsLoading = false;
  length = 0;
  pageIndex = 0;
  pageSize = 50;
  pageSizeOptions = [25, 50, 100, 200];
  floorPlanLocationCount = 0;

  locationMaterialsPageIndex = 0;
  locationMaterialsPageSize = 25;
  readonly locationMaterialsPageSizeOptions = [10, 25, 50, 100];
  locationMaterialsTotalCount = 0;
  locationMaterialFiltersDraft: WarehouseLocationMaterialFilters =
    this.createEmptyLocationMaterialFilters();
  locationMaterialFiltersApplied: WarehouseLocationMaterialFilters =
    this.createEmptyLocationMaterialFilters();
  filteredLocationMaterialTypes: string[] = [];

  readonly skeletonKpiItems = [1, 2, 3, 4, 5, 6];
  readonly skeletonTableRows = [1, 2, 3, 4, 5, 6, 7, 8];
  readonly skeletonTableCols = [1, 2, 3, 4, 5];
  readonly skeletonOverviewCards = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  readonly skeletonSidebarItems = [1, 2, 3, 4, 5, 6];
  readonly skeletonMaterialRows = [1, 2, 3, 4, 5, 6, 7, 8];
  readonly skeletonMaterialCols = [1, 2, 3, 4, 5, 6, 7, 8, 9];

  private readonly fullscreenBodyClass = "warehouse-summary-fullscreen-active";

  private readonly destroy$ = new Subject<void>();
  private readonly locationSearch$ = new Subject<string>();
  private readonly overviewMaterialSearch$ = new Subject<string>();
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
    this.materialSearchForm = this.fb.group({
      materialSearch: [""],
      materialSearchField: ["sap" as WarehouseMaterialSearchField],
    });
    this.overviewMaterialSearchForm = this.fb.group({
      overviewMaterialSearch: [""],
    });
    this.initFilterAutocomplete();
    this.initLocationSearch();
    this.initOverviewMaterialSearch();
    this.loadData();
  }

  ngOnDestroy(): void {
    this.setPageFullscreenState(false);
    this.destroy$.next();
    this.destroy$.complete();
  }

  togglePageFullscreen(): void {
    this.setPageFullscreenState(!this.isPageFullscreen);
  }

  hasFloorPlanHighlightFocus(): boolean {
    if (this.highlightedLocationIds.size > 0) {
      return true;
    }
    return !!(this.floorPlanContext?.materialType ?? "").trim();
  }

  hasOverviewHighlightFocus(): boolean {
    return !!this.selectedOverviewMaterialType || !!this.selectedOverviewArea;
  }

  onSearch(): void {
    this.pageIndex = 0;
    this.selectedOverviewMaterialType = null;
    this.selectedOverviewArea = null;
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
    this.openFloorPlanForRow(row);
  }

  onOverviewAreaClick(area: WarehouseOverviewArea): void {
    const isSameAreaSelected =
      this.selectedOverviewArea?.areaCode === area.areaCode &&
      this.selectedOverviewArea?.areaName === area.areaName;
    const nextWarehouseFilter = isSameAreaSelected ? "" : area.areaCode;

    this.filterForm.patchValue(
      {
        warehouseName: nextWarehouseFilter,
      },
      { emitEvent: false },
    );
    this.pageIndex = 0;

    if (isSameAreaSelected) {
      this.selectedOverviewArea = null;
      this.loadData();
      this.cdr.markForCheck();
      return;
    }

    this.selectedOverviewArea = area;
    this.openFloorPlanForRow({
      areaCode: area.areaCode,
      areaName: area.areaName,
      materialType: this.selectedOverviewMaterialType ?? "",
      quantity: area.quantity,
      materialIdentifierCount: area.materialIdentifierCount,
    });
    this.loadData();
    this.cdr.markForCheck();
  }

  onOverviewMaterialTypeClick(type: WarehouseOverviewMaterialType): void {
    const next =
      this.selectedOverviewMaterialType === type.materialType
        ? null
        : type.materialType;
    this.selectedOverviewMaterialType = next;
    this.selectedOverviewArea = null;
    this.cdr.markForCheck();
  }

  onLocationSelect(location: WarehouseAreaLocation): void {
    const locationChanged =
      this.selectedLocation?.locationId !== location.locationId;
    this.selectedLocation = location;
    this.selectedAreaMaterial = null;
    this.selectedMaterialGroupKey = null;
    this.selectedMaterialOccurrences = [];
    this.highlightedLocationIds = new Set([location.locationId]);
    if (locationChanged) {
      this.resetLocationMaterialTableState();
    }
    this.loadLocationMaterials(location);
    this.cdr.markForCheck();
  }

  onAreaMaterialGroupSelect(group: WarehouseAreaMaterialGroup): void {
    this.selectedMaterialGroupKey = group.groupKey;
    this.selectedAreaMaterial = this.buildRepresentativeMaterial(group);
    this.selectedLocation = null;
    this.locationMaterialsSource.data = [];
    this.locationMaterialsTotalCount = 0;
    this.resetLocationMaterialTableState();
    this.loadMaterialOccurrences(this.selectedAreaMaterial);
    this.cdr.markForCheck();
  }

  onMaterialSearchSubmit(): void {
    const term = (
      this.materialSearchForm.get("materialSearch")?.value ?? ""
    ).trim();
    this.loadAreaMaterials(term);
  }

  trackAreaMaterialGroup(
    _index: number,
    group: WarehouseAreaMaterialGroup,
  ): string {
    return group.groupKey;
  }

  isAreaMaterialGroupActive(group: WarehouseAreaMaterialGroup): boolean {
    return this.selectedMaterialGroupKey === group.groupKey;
  }

  getMaterialGroupTitle(group: WarehouseAreaMaterialGroup): string {
    const code = (group.itemCode ?? "").trim();
    const name = (group.materialName ?? "").trim();
    if (code && name) {
      return `${code} - ${name}`;
    }
    return code || name || "—";
  }

  getMaterialGroupMeta(group: WarehouseAreaMaterialGroup): string {
    if (!group.locationSummaries.length) {
      return `Tổng SL ${this.formatNumber(group.totalQuantity)}`;
    }
    return group.locationSummaries
      .map((loc) => `${loc.locationName} SL ${this.formatNumber(loc.quantity)}`)
      .join(" · ");
  }

  getMaterialListTitle(item: WarehouseAreaInventoryItem): string {
    const code = (item.itemCode ?? "").trim();
    const name = (item.materialName ?? "").trim();
    if (code && name) {
      return `${code} - ${name}`;
    }
    return code || name || "—";
  }

  getMaterialListMeta(item: WarehouseAreaInventoryItem): string {
    const location = item.locationName || item.locationFullName || "—";
    return `${location} · SL ${this.formatNumber(item.quantity)}`;
  }

  getSelectedMaterialOccurrences(): WarehouseAreaInventoryItem[] {
    return this.selectedMaterialOccurrences;
  }

  getHighlightStatusForLocation(
    location: WarehouseAreaLocation,
  ): string | number | null {
    const occurrence = this.selectedMaterialOccurrences.find(
      (item) => item.locationId === location.locationId,
    );
    return occurrence?.status ?? null;
  }

  getInventoryStatusClass(status: string | number | null | undefined): string {
    const code = String(status ?? "").trim();
    switch (code) {
      case "3":
        return "status-ready";
      case "6":
        return "status-empty";
      case "19":
        return "status-expired";
      default:
        return "";
    }
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

  isLocationHighlighted(location: WarehouseAreaLocation): boolean {
    if (this.highlightedLocationIds.has(location.locationId)) {
      return true;
    }
    const materialType = (this.floorPlanContext?.materialType ?? "").trim();
    if (!materialType) {
      return false;
    }
    return location.containsSelectedMaterialType;
  }

  isOverviewAreaHighlighted(area: WarehouseOverviewArea): boolean {
    if (this.selectedOverviewArea?.areaCode === area.areaCode) {
      return true;
    }
    if (!this.selectedOverviewMaterialType) {
      return false;
    }
    return area.materialTypes.some(
      (type) =>
        type.toLowerCase() === this.selectedOverviewMaterialType?.toLowerCase(),
    );
  }

  isOverviewMaterialTypeActive(type: WarehouseOverviewMaterialType): boolean {
    return this.selectedOverviewMaterialType === type.materialType;
  }

  getFilteredOverviewMaterialTypes(): WarehouseOverviewMaterialType[] {
    const term = (
      this.overviewMaterialSearchForm.get("overviewMaterialSearch")?.value ?? ""
    )
      .trim()
      .toLowerCase();
    if (!term) {
      return this.overviewMaterialTypes;
    }
    return this.overviewMaterialTypes.filter((item) =>
      item.materialType.toLowerCase().includes(term),
    );
  }

  getMaterialTypeColor(materialType: string | null | undefined): string {
    const type = (materialType ?? "").trim();
    if (!type || type === "-") {
      return "#64748b";
    }
    const index = this.legendMaterialTypes.findIndex(
      (item) => item.toLowerCase() === type.toLowerCase(),
    );
    if (index < 0) {
      const overviewIndex = this.overviewMaterialTypes.findIndex(
        (item) => item.materialType.toLowerCase() === type.toLowerCase(),
      );
      if (overviewIndex >= 0) {
        return this.materialTypeColors[
          overviewIndex % this.materialTypeColors.length
        ];
      }
      return "#94a3b8";
    }
    return this.materialTypeColors[index % this.materialTypeColors.length];
  }

  getLocationCardColor(location: WarehouseAreaLocation): string {
    if (this.isLocationHighlighted(location)) {
      return "#38bdf8";
    }
    if (location.empty) {
      return "#334155";
    }
    if (
      this.floorPlanContext?.materialType &&
      location.containsSelectedMaterialType
    ) {
      return this.getMaterialTypeColor(this.floorPlanContext.materialType);
    }
    return this.getMaterialTypeColor(location.dominantMaterialType);
  }

  getOverviewAreaCardColor(area: WarehouseOverviewArea): string {
    if (this.isOverviewAreaHighlighted(area)) {
      if (this.selectedOverviewMaterialType) {
        return this.getMaterialTypeColor(this.selectedOverviewMaterialType);
      }
      return "#38bdf8";
    }
    return "#475569";
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
        return "Hết hàng";
      case "19":
        return "Hết hạn";
      default:
        return code || "-";
    }
  }

  onLocationMaterialFilterSubmit(): void {
    this.locationMaterialFiltersApplied = {
      ...this.locationMaterialFiltersDraft,
      qrCode: this.materialService.extractQrSearchToken(
        this.locationMaterialFiltersDraft.qrCode,
      ),
    };
    this.locationMaterialsPageIndex = 0;
    if (this.selectedLocation) {
      this.loadLocationMaterials(this.selectedLocation);
    }
  }

  onLocationMaterialHeaderKeydown(event: KeyboardEvent): void {
    if (event.key !== "Enter") {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    (event.target as HTMLElement)?.blur();
    this.onLocationMaterialFilterSubmit();
  }

  onLocationMaterialStatusFilter(): void {
    this.onLocationMaterialFilterSubmit();
  }

  onLocationMaterialTypeInput(value: string): void {
    this.filteredLocationMaterialTypes =
      this.filterLocationMaterialTypeOptions(value);
  }

  onLocationMaterialTypeSelected(value: string): void {
    this.locationMaterialFiltersDraft.materialType = value;
    this.onLocationMaterialFilterSubmit();
  }

  onLocationMaterialsPageChange(event: PageEvent): void {
    this.locationMaterialsPageIndex = event.pageIndex;
    this.locationMaterialsPageSize = event.pageSize;
    if (this.selectedLocation) {
      this.loadLocationMaterials(this.selectedLocation);
    }
  }

  filterLocationMaterialTypeOptions(value: string): string[] {
    const needle = (value ?? "").trim().toLowerCase();
    if (!needle) {
      return [...this.materialTypeOptions];
    }
    return this.materialTypeOptions.filter((type) =>
      type.toLowerCase().includes(needle),
    );
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

  private setPageFullscreenState(active: boolean): void {
    this.isPageFullscreen = active;
    document.body.classList.toggle(this.fullscreenBodyClass, active);
    this.cdr.markForCheck();
  }

  private openFloorPlanForRow(row: WarehouseAreaSummaryRow): void {
    this.selectedSummaryRow = row;
    this.floorPlanContext = {
      areaCode: row.areaCode ?? "",
      areaName: row.areaName ?? "",
      materialType: row.materialType ?? "",
    };
    this.selectedLocation = null;
    this.selectedAreaMaterial = null;
    this.selectedMaterialGroupKey = null;
    this.selectedMaterialOccurrences = [];
    this.highlightedLocationIds = new Set();
    this.locationMaterialsSource.data = [];
    this.locationMaterialsTotalCount = 0;
    this.resetLocationMaterialTableState();
    this.locationSearchForm.patchValue(
      { locationSearch: "" },
      { emitEvent: false },
    );
    this.materialSearchForm.patchValue(
      { materialSearch: "" },
      { emitEvent: false },
    );
    this.selectedTabIndex = 1;
    this.loadFloorPlan("");
    this.loadAreaMaterials("");
    this.cdr.markForCheck();
    requestAnimationFrame(() => this.scrollToWorkspace());
  }

  private scrollToWorkspace(): void {
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

  private initOverviewMaterialSearch(): void {
    this.overviewMaterialSearch$
      .pipe(debounceTime(250), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => this.cdr.markForCheck());

    this.overviewMaterialSearchForm
      .get("overviewMaterialSearch")
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value: string | null | undefined) => {
        this.overviewMaterialSearch$.next((value ?? "").trim());
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
          this.filteredLocationMaterialTypes = [...this.materialTypeOptions];

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

        if (this.selectedAreaMaterial) {
          this.loadMaterialOccurrences(this.selectedAreaMaterial);
        } else if (this.selectedLocation) {
          const refreshed = this.floorPlanLocations.find(
            (item) => item.locationId === this.selectedLocation?.locationId,
          );
          this.selectedLocation = refreshed ?? null;
          if (!this.selectedLocation) {
            this.locationMaterialsSource.data = [];
            this.highlightedLocationIds = new Set();
          } else {
            this.highlightedLocationIds = new Set([
              this.selectedLocation.locationId,
            ]);
          }
        }

        this.cdr.markForCheck();
      });
  }

  private loadAreaMaterials(materialSearch: string): void {
    if (!this.floorPlanContext?.areaCode) {
      this.groupedAreaMaterials = [];
      this.areaMaterialsTotalCount = 0;
      this.areaMaterialsPreviewLimited = false;
      return;
    }

    const searchField =
      (this.materialSearchForm.get("materialSearchField")
        ?.value as WarehouseMaterialSearchField) ?? "sap";
    const normalizedSearch = materialSearch.trim();
    const isPreview = !normalizedSearch;

    this.isAreaMaterialsLoading = true;
    this.cdr.markForCheck();

    this.materialService
      .fetchWarehouseAreaMaterialGroups({
        areaCode: this.floorPlanContext.areaCode,
        areaName: this.floorPlanContext.areaName,
        materialSearch: normalizedSearch,
        searchField,
        previewLimit: isPreview ? this.areaMaterialsPreviewLimit : undefined,
      })
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isAreaMaterialsLoading = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe((response) => {
        this.groupedAreaMaterials = response.groups ?? [];
        this.areaMaterialsTotalCount = response.totalCount ?? 0;
        this.areaMaterialsPreviewLimited = response.previewLimited ?? false;

        if (this.selectedMaterialGroupKey) {
          const refreshedGroup = this.groupedAreaMaterials.find(
            (group) => group.groupKey === this.selectedMaterialGroupKey,
          );
          if (refreshedGroup) {
            this.selectedAreaMaterial =
              this.buildRepresentativeMaterial(refreshedGroup);
          } else {
            this.selectedMaterialGroupKey = null;
            this.selectedAreaMaterial = null;
            this.selectedMaterialOccurrences = [];
            this.highlightedLocationIds = new Set();
          }
        }
        this.cdr.markForCheck();
      });
  }

  private buildRepresentativeMaterial(
    group: WarehouseAreaMaterialGroup,
  ): WarehouseAreaInventoryItem {
    const firstLocation = group.locationSummaries[0];
    return {
      locationId: firstLocation?.locationId ?? 0,
      locationName: firstLocation?.locationName ?? "",
      locationFullName: firstLocation?.locationName ?? "",
      materialIdentifier: "",
      itemCode: group.itemCode,
      materialName: group.materialName,
      partNumber: "",
      quantity: group.totalQuantity,
      status: "",
      lotNumber: "",
      materialType: group.materialType,
    };
  }

  private loadMaterialOccurrences(item: WarehouseAreaInventoryItem): void {
    if (!this.floorPlanContext?.areaCode) {
      this.selectedMaterialOccurrences = [item];
      this.highlightedLocationIds = new Set(
        item.locationId != null ? [item.locationId] : [],
      );
      return;
    }

    const itemCode = (item.itemCode ?? "").trim();
    const materialId = (item.materialIdentifier ?? "").trim();
    const searchTerm = itemCode || materialId;
    const searchField: WarehouseMaterialSearchField = itemCode ? "sap" : "name";

    if (!searchTerm) {
      this.selectedMaterialOccurrences = [item];
      this.highlightedLocationIds = new Set(
        item.locationId != null ? [item.locationId] : [],
      );
      this.cdr.markForCheck();
      return;
    }

    this.materialService
      .fetchWarehouseAreaMaterials({
        areaCode: this.floorPlanContext.areaCode,
        areaName: this.floorPlanContext.areaName,
        materialSearch: searchTerm,
        searchField,
        previewLimit: 50000,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe((response) => {
        const rows = (response.items ?? []).filter((row) => {
          if (itemCode) {
            return (row.itemCode ?? "").trim() === itemCode;
          }
          return row.materialIdentifier === materialId;
        });
        this.selectedMaterialOccurrences = rows.length ? rows : [item];
        this.highlightedLocationIds = new Set(
          this.selectedMaterialOccurrences
            .map((row) => row.locationId)
            .filter((id): id is number => id != null),
        );
        this.cdr.markForCheck();
      });
  }

  private loadLocationMaterials(location: WarehouseAreaLocation): void {
    const locationKey = location.locationFullName || location.locationName;
    if (!locationKey) {
      this.locationMaterialsSource.data = [];
      this.locationMaterialsTotalCount = 0;
      return;
    }

    this.isMaterialsLoading = true;
    this.cdr.markForCheck();

    this.materialService
      .fetchLocationInventoryMaterialsPage(
        locationKey,
        this.locationMaterialsPageIndex,
        this.locationMaterialsPageSize,
        this.locationMaterialFiltersApplied,
      )
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isMaterialsLoading = false;
          this.cdr.markForCheck();
        }),
      )
      .subscribe((response) => {
        this.locationMaterialsSource.data = response.items ?? [];
        this.locationMaterialsTotalCount = response.totalCount ?? 0;
        this.locationMaterialsPageIndex = response.page ?? 0;
        this.locationMaterialsPageSize =
          response.size ?? this.locationMaterialsPageSize;
        this.cdr.markForCheck();
      });
  }

  private createEmptyLocationMaterialFilters(): WarehouseLocationMaterialFilters {
    return {
      qrCode: "",
      materialName: "",
      itemCode: "",
      partNumber: "",
      lotNumber: "",
      materialType: "",
      status: "",
      locationFullName: "",
    };
  }

  private resetLocationMaterialTableState(): void {
    this.locationMaterialsPageIndex = 0;
    this.locationMaterialFiltersDraft =
      this.createEmptyLocationMaterialFilters();
    this.locationMaterialFiltersApplied =
      this.createEmptyLocationMaterialFilters();
    this.filteredLocationMaterialTypes = [...this.materialTypeOptions];
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
        includeOverview: true,
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
        this.overviewAreas = response.overviewAreas ?? [];
        this.overviewMaterialTypes = response.overviewMaterialTypes ?? [];
        if (this.selectedOverviewArea) {
          const refreshedOverviewArea = this.overviewAreas.find(
            (area) =>
              area.areaCode === this.selectedOverviewArea?.areaCode &&
              area.areaName === this.selectedOverviewArea?.areaName,
          );
          this.selectedOverviewArea = refreshedOverviewArea ?? null;
        }
        this.cdr.markForCheck();
      });
  }
}
