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
  ManagerTemNccService,
  SapOcrd,
} from "app/entities/list-material/services/info-tem-ncc.service";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from "@angular/material/dialog";

// Models and Services
import { AlertService } from "app/core/util/alert.service";
import { AccountService } from "app/core/auth/account.service";
import { take } from "rxjs";
import { NotificationService } from "app/entities/list-material/services/notification.service";

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
@Component({
  selector: "jhi-config-tem-ncc",
  standalone: false,
  templateUrl: "./config-dialog.component.html",
  styleUrls: ["./config-dialog.component.scss"],
})
export class ConfigDialogComponent implements OnInit {
  allAttributes: string[] = [];
  unmappedFields: string[] = [];
  //dem vi tri
  nextPosition = 0;

  //danh sach nha cung cap
  vendorOptions: SapOcrd[] = [];
  filteredVendorOptions: SapOcrd[] = [];
  isLoadingVendors = false;

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

  dataFields: string[] = [];
  isLoadingFields = false;
  isSaving = false;
  filteredFieldOptions: string[][] = [];
  currentUser = "unknown";

  private readonly VENDOR_DISPLAY_LIMIT = 50;
  constructor(
    private dialog: MatDialog,
    private alertService: AlertService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    public dialogRef: MatDialogRef<ConfigDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VendorConfig | null,
    private managerTemNccService: ManagerTemNccService,
    private accountService: AccountService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    if (this.data) {
      this.form = {
        ...this.data,
        fieldMappings: [...(this.data.fieldMappings || [])],
      };
    }

    this.loadVendors();
    //tinh vi tri moi tu vi tri moi nhat
    this.nextPosition =
      this.form.fieldMappings.length > 0
        ? Math.max(...this.form.fieldMappings.map((f) => f.position)) + 1
        : 0;
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe((account) => {
        this.currentUser = account?.login ?? "unknown";
      });
    this.loadMaterialAttributes();
  }
  onVendorSearch(value: string): void {
    const lower = (value ?? "").toLowerCase().trim();
    const filtered = lower
      ? this.vendorOptions.filter(
          (v) =>
            v.cardName.toLowerCase().includes(lower) ||
            v.cardCode.toLowerCase().includes(lower),
        )
      : this.vendorOptions;

    this.filteredVendorOptions = filtered.slice(0, this.VENDOR_DISPLAY_LIMIT);
  }

  onVendorSelected(vendor: SapOcrd): void {
    this.form.vendorName = vendor.cardName;
    this.form.vendorCode = vendor.cardCode;
  }
  addField(): void {
    this.form.fieldMappings.push({
      position: this.nextPosition,
      nccFieldDesc: "",
      dataField: "",
    });
    this.nextPosition++;
    this.filteredFieldOptions.push([...this.dataFields]);
    this.updateUnmappedFields();
  }
  removeField(index: number): void {
    this.form.fieldMappings.splice(index, 1);
    // this.form.fieldMappings.forEach((f, i) => (f.position = i));
    this.filteredFieldOptions.splice(index, 1);
    this.updateUnmappedFields();
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onSave(): void {
    this.form.fieldCount = this.form.fieldMappings.length;

    const mappingConfig = JSON.stringify({
      separator: this.form.separator,
      fieldMappings: this.form.fieldMappings,
    });

    const now = new Date().toISOString();
    const currentUser = this.currentUser;

    const payload = {
      id: Number(this.form.id),
      vendorCode: this.form.vendorCode,
      vendorName: this.form.vendorName,
      mappingConfig,
      createdBy: currentUser,
      createdAt: now,
      updatedBy: currentUser,
      updatedAt: now,
    };

    this.isSaving = true;
    const isEdit = !!this.form.id;

    const request$ = isEdit
      ? this.managerTemNccService.updateTemIdentificationScenario(
          Number(this.form.id),
          payload,
        )
      : this.managerTemNccService.createTemIdentificationScenario(payload);

    request$.subscribe({
      next: () => {
        this.isSaving = false;
        this.notificationService.success(
          isEdit ? "Cập nhật cấu hình thành công!" : "Lưu cấu hình thành công!",
        );
        this.dialogRef.close(this.form);
      },
      error: () => {
        this.isSaving = false;
        this.notificationService.error(
          isEdit
            ? "Cập nhật cấu hình thất bại, vui lòng thử lại!"
            : "Lưu cấu hình thất bại, vui lòng thử lại!",
        );
      },
    });
  }

  updateUnmappedFields(): void {
    const mappedFields = new Set(
      this.form.fieldMappings
        .map((f) => f.dataField)
        .filter((f) => f && f !== "Không lấy"),
    );
    this.unmappedFields = this.allAttributes.filter(
      (f) => !mappedFields.has(f),
    );
  }

  onFieldInput(value: string, index: number): void {
    const lower = (value ?? "").toLowerCase().trim();
    this.filteredFieldOptions[index] = lower
      ? this.dataFields.filter((f) => f.toLowerCase().includes(lower))
      : [...this.dataFields];
  }

  private loadMaterialAttributes(): void {
    this.isLoadingFields = true;
    this.managerTemNccService.getMaterialAttributes().subscribe({
      next: (attrs) => {
        this.allAttributes = attrs.map((a) => a.attributes);
        this.dataFields = ["Không lấy", ...this.allAttributes];

        // khởi tạo đủ slot cho cả các row đang có khi edit
        this.filteredFieldOptions = this.form.fieldMappings.map(() => [
          ...this.dataFields,
        ]);

        this.isLoadingFields = false;
        this.updateUnmappedFields();
        this.cdr.markForCheck();
      },
      error: () => {
        this.dataFields = DATA_FIELDS;
        // tương tự fallback
        this.filteredFieldOptions = this.form.fieldMappings.map(() => [
          ...this.dataFields,
        ]);
        this.isLoadingFields = false;
      },
    });
  }
  private initFilteredOptions(): void {
    this.filteredFieldOptions = this.form.fieldMappings.map(() => [
      ...this.dataFields,
    ]);
  }

  private loadVendors(): void {
    this.isLoadingVendors = true;
    this.managerTemNccService.getSapOcrds().subscribe({
      next: (data) => {
        this.vendorOptions = data;
        this.filteredVendorOptions = data.slice(0, this.VENDOR_DISPLAY_LIMIT);
        this.isLoadingVendors = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoadingVendors = false;
      },
    });
  }
}
