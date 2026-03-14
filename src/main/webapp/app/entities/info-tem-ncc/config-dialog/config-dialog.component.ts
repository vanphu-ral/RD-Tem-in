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
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
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
  "Không lấy",
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
  templateUrl: "./config-dialog.component.html",
  styleUrls: ["./config-dialog.component.scss"],
})
export class ConfigDialogComponent implements OnInit {
  form: VendorConfig = {
    id: "",
    vendorCode: "",
    vendorName: "",
    separator: "#",
    fieldCount: 0,
    fieldMappings: [
      { position: 0, nccFieldDesc: "Real ID", dataField: "Mã ReelID" },
      { position: 1, nccFieldDesc: "", dataField: "Mã Part number" },
    ],
  };

  dataFields = DATA_FIELDS;
  unmappedFields = UNMAPPED_SYSTEM_FIELDS;

  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    public dialogRef: MatDialogRef<ConfigDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VendorConfig | null,
  ) {}

  ngOnInit(): void {
    if (this.data) {
      this.form = {
        ...this.data,
        fieldMappings: [...(this.data.fieldMappings || [])],
      };
    }
  }

  addField(): void {
    const nextPos = this.form.fieldMappings.length;
    this.form.fieldMappings.push({
      position: nextPos,
      nccFieldDesc: "",
      dataField: "",
    });
  }

  removeField(index: number): void {
    this.form.fieldMappings.splice(index, 1);
    this.form.fieldMappings.forEach((f, i) => (f.position = i));
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onSave(): void {
    this.form.fieldCount = this.form.fieldMappings.length;
    this.dialogRef.close(this.form);
  }
}
