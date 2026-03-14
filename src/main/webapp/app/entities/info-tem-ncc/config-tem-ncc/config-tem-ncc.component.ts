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

  allData: VendorConfig[] = [
    {
      id: "1",
      vendorCode: "V900000367",
      vendorName: "SOSEN",
      separator: "#",
      fieldCount: 20,
      fieldMappings: [],
    },
    {
      id: "2",
      vendorCode: "V900000368",
      vendorName: "HUNAN AIHUA GROUP CO., LTD",
      separator: "#",
      fieldCount: 6,
      fieldMappings: [],
    },
    {
      id: "3",
      vendorCode: "V900000369",
      vendorName: "Zhejiang Yankon Mega Lighting Co., Ltd",
      separator: "#",
      fieldCount: 7,
      fieldMappings: [],
    },
  ];

  filteredData: VendorConfig[] = [...this.allData];
  pagedData: VendorConfig[] = [];
  pageSize = 3;
  pageIndex = 0;

  filters = { vendorCode: "", vendorName: "" };

  constructor(private dialog: MatDialog) {
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
      width: "760px",
      maxHeight: "90vh",
      data: null,
      panelClass: "vendor-dialog-panel",
    });
    ref.afterClosed().subscribe((result: VendorConfig | null) => {
      if (result) {
        result.id = Date.now().toString();
        this.allData.push(result);
        this.applyFilter();
      }
    });
  }

  openEditDialog(vendor: VendorConfig): void {
    const ref = this.dialog.open(ConfigDialogComponent, {
      width: "760px",
      maxHeight: "90vh",
      data: { ...vendor },
      panelClass: "vendor-dialog-panel",
    });
    ref.afterClosed().subscribe((result: VendorConfig | null) => {
      if (result) {
        const idx = this.allData.findIndex((v) => v.id === result.id);
        if (idx >= 0) {
          this.allData[idx] = result;
        }
        this.applyFilter();
      }
    });
  }

  deleteVendor(vendor: VendorConfig): void {
    this.allData = this.allData.filter((v) => v.id !== vendor.id);
    this.applyFilter();
  }
}
