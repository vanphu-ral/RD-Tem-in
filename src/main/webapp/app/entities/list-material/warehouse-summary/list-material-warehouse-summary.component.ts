import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { Observable, Subject, forkJoin, of } from "rxjs";
import { finalize, map, startWith, takeUntil } from "rxjs/operators";
import {
  faArrowLeft,
  faRotateRight,
  faWarehouse,
} from "@fortawesome/free-solid-svg-icons";
import {
  ListMaterialService,
  WarehouseAreaOption,
  WarehouseAreaSummaryRow,
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

  filterForm!: FormGroup;
  displayedColumns = [
    "areaCode",
    "areaName",
    "materialType",
    "materialIdentifierCount",
    "quantity",
  ];
  dataSource = new MatTableDataSource<WarehouseAreaSummaryRow>([]);

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

  isLoading = false;
  length = 0;
  pageIndex = 0;
  pageSize = 50;
  pageSizeOptions = [25, 50, 100, 200];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private materialService: ListMaterialService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      warehouseName: [""],
      warehouseAreaName: [""],
      materialType: [""],
    });
    this.initFilterAutocomplete();
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
