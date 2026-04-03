import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ChangeDetectorRef,
  Inject,
} from "@angular/core";
import { CommonModule, DatePipe } from "@angular/common";
import { FormsModule } from "@angular/forms";

// Angular Material
import { MatTableModule, MatTableDataSource } from "@angular/material/table";
import {
  MatPaginator,
  MatPaginatorModule,
  PageEvent,
} from "@angular/material/paginator";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatChipsModule } from "@angular/material/chips";
import { MatDividerModule } from "@angular/material/divider";
import { MatIconModule } from "@angular/material/icon";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";

// Models and Services
import { AlertService } from "app/core/util/alert.service";
import { ConfigDialogComponent } from "../config-dialog/config-dialog.component";
import {
  ManagerTemNccService,
  TemScenarioResponse,
} from "app/entities/list-material/services/info-tem-ncc.service";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { ConfirmDialogComponent } from "app/entities/pallet-note-management/confirm-dialog/confirm-dialog.component";

export interface VendorConfig {
  id: string;
  vendorCode: string;
  vendorName: string;
  separator: string;
  fieldCount: number;
  fieldMappings: FieldMapping[];
}

export interface FieldMapping {
  position: number;
  nccFieldDesc: string;
  dataField: string;
}

const DATA_FIELDS = [
  "Mã ReelID",
  "Mã Part number",
  "SAP Code",
  "Initial quantity",
  "Vendor",
  "Quantity Override",
  "ManufacturingDate",
  "ExpirationDate",
  "UserData1",
  "UserData2",
  "UserData3",
  "UserData4",
  "UserData5",
  "Storage Unit",
  "Lot Number",
];

const UNMAPPED_SYSTEM_FIELDS = [
  "SAP Code",
  "Initial quantity",
  "Vendor",
  "Quantity Override",
  "ManufacturingDate",
  "ExpirationDate",
  "UserData1",
  "UserData2",
  "UserData3",
  "UserData4",
  "UserData5",
  "Storage Unit",
  "Lot Number",
];
@Component({
  selector: "jhi-config-tem-ncc",
  standalone: false,
  templateUrl: "./config-tem-ncc.component.html",
  styleUrls: ["./config-tem-ncc.component.scss"],
})
export class ConfigTemNccComponent {
  displayedColumns = [
    "vendorCode",
    "vendorName",
    "separator",
    "fieldCount",
    "actions",
  ];

  isLoading = false;

  allData: VendorConfig[] = [];

  filteredData: VendorConfig[] = [...this.allData];
  pagedData: VendorConfig[] = [];
  pageSize = 3;
  pageIndex = 0;

  filters = { vendorCode: "", vendorName: "" };

  constructor(
    private dialog: MatDialog,
    private managerTemNccService: ManagerTemNccService,
    private notificationService: NotificationService,
  ) {
    this.loadScenarios();
    this.updatePage();
  }

  applyFilter(): void {
    const code = this.filters.vendorCode.toLowerCase();
    const name = this.filters.vendorName.toLowerCase();
    this.filteredData = this.allData.filter(
      (v) =>
        v.vendorCode.toLowerCase().includes(code) &&
        v.vendorName.toLowerCase().includes(name),
    );
    this.pageIndex = 0;
    this.updatePage();
  }

  loadScenarios(): void {
    this.isLoading = true;
    this.managerTemNccService.getTemIdentificationScenarios().subscribe({
      next: (data) => {
        this.allData = data.map((item) => this.mapResponseToVendorConfig(item));
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.notificationService.error("Không thể tải danh sách kịch bản!");
        this.isLoading = false;
      },
    });
  }
  updatePage(): void {
    const start = this.pageIndex * this.pageSize;
    this.pagedData = this.filteredData.slice(start, start + this.pageSize);
  }

  onPage(e: PageEvent): void {
    this.pageIndex = e.pageIndex;
    this.pageSize = e.pageSize;
    this.updatePage();
  }

  openAddDialog(): void {
    const ref = this.dialog.open(ConfigDialogComponent, {
      width: "95vw",
      maxWidth: "95vw",
      height: "95vh",
      maxHeight: "95vh",
      data: null,
      panelClass: "vendor-dialog-panel",
    });
    ref.afterClosed().subscribe((result: VendorConfig | null) => {
      if (result) {
        this.loadScenarios();
        result.id = Date.now().toString();
        this.allData.push(result);
        this.applyFilter();
      }
    });
  }

  openEditDialog(vendor: VendorConfig): void {
    const ref = this.dialog.open(ConfigDialogComponent, {
      width: "95vw",
      maxWidth: "95vw",
      height: "95vh",
      maxHeight: "95vh",
      data: { ...vendor },
      panelClass: "vendor-dialog-panel",
    });
    ref.afterClosed().subscribe((result: VendorConfig | null) => {
      if (result) {
        this.loadScenarios();
        const idx = this.allData.findIndex((v) => v.id === result.id);
        if (idx >= 0) {
          this.allData[idx] = result;
        }
        this.applyFilter();
      }
    });
  }

  deleteVendor(vendor: VendorConfig): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: "400px",
      data: {
        title: "Xác nhận xóa",
        message: `Bạn có chắc chắn muốn xóa nhà cung cấp ${vendor.vendorName} không?`,
        confirmText: "Xóa",
        cancelText: "Hủy",
      },
    });

    ref.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) {
        return;
      }
      this.managerTemNccService
        .deleteTemIdentificationScenario(Number(vendor.id))
        .subscribe({
          next: () => {
            this.notificationService.success("Xóa kịch bản thành công!");
            this.loadScenarios();
          },
          error: () => {
            this.notificationService.error("Xóa kịch bản thất bại!");
          },
        });
    });
  }

  private mapResponseToVendorConfig(item: TemScenarioResponse): VendorConfig {
    let separator = "#";
    let fieldMappings: FieldMapping[] = [];

    try {
      const parsed = JSON.parse(item.mappingConfig);
      separator = parsed.separator ?? "#";
      fieldMappings = parsed.fieldMappings ?? [];
    } catch {
      // mappingConfig không parse được, giữ default
    }

    return {
      id: String(item.id),
      vendorCode: item.vendorCode,
      vendorName: item.vendorName,
      separator,
      fieldCount: fieldMappings.length,
      fieldMappings,
    };
  }
}
