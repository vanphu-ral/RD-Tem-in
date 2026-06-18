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
import { Subject } from "rxjs";
import { finalize, takeUntil } from "rxjs/operators";
import {
  faArrowLeft,
  faRotateRight,
  faWarehouse,
} from "@fortawesome/free-solid-svg-icons";
import {
  ListMaterialService,
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
    availableQuantity: 0,
    materialTypeCount: 0,
    unavailableQuantity: 0,
  };

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
      locationName: [""],
      materialName: [""],
      materialCode: [""],
      materialType: [""],
    });
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
    this.loadData();
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

  private loadData(): void {
    const filters = this.filterForm.getRawValue();
    this.isLoading = true;

    this.materialService
      .fetchWarehouseSummary({
        warehouseName: filters.warehouseName,
        locationName: filters.locationName,
        materialName: filters.materialName,
        materialCode: filters.materialCode,
        materialType: filters.materialType,
        pageNumber: this.pageIndex + 1,
        itemPerPage: this.pageSize,
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
