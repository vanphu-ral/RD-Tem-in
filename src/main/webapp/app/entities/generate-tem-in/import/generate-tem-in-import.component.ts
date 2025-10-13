// generate-tem-in-import.component.ts
import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { FormControl } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { GenerateTemInImportEditDialogComponent } from "./generate-tem-in-import-edit-dialog.component";

// Models and Services
import {
  ListProductOfRequest,
  ExcelImportData,
} from "../models/list-product-of-request.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { ExcelParserService } from "../service/excel-parser.service";

export interface VatTuRow {
  stt: number;
  sapCode: string;
  tenSp: string;
  partNumber: string;
  lot: string | number;
  storageUnit: string;
  soTem: number;
  initialQuantity: number;
  vendor: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
}
// , 'userData1',  'UserData2', 'UserData3', 'UserData4', 'UserData5', 'ExpirationDate', 'ManufacturingDate', 'ngayVe'
type VatTuKeys = keyof VatTuRow;

@Component({
  selector: "jhi-generate-tem-in-import",
  templateUrl: "./generate-tem-in-import.component.html",
  styleUrls: ["./generate-tem-in-import.component.scss"],
})
export class GenerateTemInImportComponent implements OnInit, AfterViewInit {
  khoControl = new FormControl<string | null>(null);
  allowSplitControl = new FormControl<boolean>(false);

  displayedColumns: string[] = [
    "stt",
    "actions",
    "sapCode",
    "tenSp",
    "partNumber",
    "lot",
    "storageUnit",
    "soTem",
    "initialQuantity",
    "vendor",
    "userData1",
    "userData2",
    "userData3",
    "userData4",
    "userData5",
    "expirationDate",
    "manufacturingDate",
    "ngayVe",
  ];

  dataSource = new MatTableDataSource<VatTuRow>([]);
  isLoading = false;
  importProgress = 0;
  selectedFile: File | null = null;

  // Dùng string | null để an toàn với Typed Forms và template
  filters: Partial<Record<VatTuKeys, FormControl<string | null>>> = {};

  poCode = "12345";

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private generateTemInService: GenerateTemInService,
    private excelParserService: ExcelParserService,
    private dialog: MatDialog,
  ) {}

  //  Helper: luôn trả về FormControl tồn tại, nếu thiếu thì tạo và subscribe
  fc(k: VatTuKeys): FormControl<string | null> {
    if (!this.filters[k]) {
      this.filters[k] = new FormControl<string | null>("");
      this.filters[k]!.valueChanges.subscribe(() => {
        this.applyFilter();
      });
    }
    return this.filters[k]!;
  }

  ngOnInit(): void {
    (
      this.displayedColumns.filter((c) => c !== "actions") as VatTuKeys[]
    ).forEach((c) => {
      this.filters[c] = new FormControl<string | null>("");
    });

    Object.values(this.filters).forEach((ctrl) => {
      ctrl?.valueChanges.subscribe(() => {
        this.applyFilter();
      });
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.dataSource.filterPredicate = (
      data: VatTuRow,
      filterJson: string,
    ): boolean => {
      const f = JSON.parse(filterJson) as Partial<Record<VatTuKeys, string>>;
      return (Object.entries(f) as [VatTuKeys, string | undefined][]).every(
        ([key, val]) => {
          const needle = (val ?? "").toString().trim().toLowerCase();
          if (!needle) {
            return true;
          }
          const target = String((data as any)[key] ?? "").toLowerCase();
          return target.includes(needle);
        },
      );
    };
  }

  applyFilter(): void {
    const f: Partial<Record<VatTuKeys, string>> = {};
    (
      Object.entries(this.filters) as [
        VatTuKeys,
        FormControl<string | null> | undefined,
      ][]
    ).forEach(([k, ctrl]) => {
      f[k] = (ctrl?.value ?? "").toString();
    });
    this.dataSource.filter = JSON.stringify(f);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  onFileChange(ev: Event): void {
    const file = (ev.target as HTMLInputElement).files?.[0];
    if (!file) {
      this.selectedFile = null;
      return;
    }

    if (!file.name.endsWith(".xlsx") && !file.name.endsWith(".xls")) {
      alert("Please select a valid Excel file (.xlsx or .xls)");
      this.selectedFile = null;
      return;
    }

    this.selectedFile = file;
  }

  getPOCode(): void {
    return;
  }

  import(): void {
    if (!this.selectedFile) {
      alert("Vui lòng import file excel.");
      return;
    }

    this.parseExcelFile(this.selectedFile);
  }

  save(): void {
    if (this.dataSource.data.length === 0) {
      alert("Không có dữ liệu");
      return;
    }

    const requestId = 1;
    this.isLoading = true;
    this.importProgress = 0;

    // Convert VatTuRow data back to ExcelImportData format
    const importData: ExcelImportData[] = this.dataSource.data.map((row) => ({
      sapCode: row.sapCode,
      tenSP: row.tenSp,
      partNumber: row.partNumber,
      lot: String(row.lot),
      temQuantity: row.soTem,
      initialQuantity: row.initialQuantity,
      vendor: row.vendor,
      userData1: row.userData1,
      userData2: row.userData2,
      userData3: row.userData3,
      userData4: row.userData4,
      userData5: row.userData5,
      storageUnit: row.storageUnit,
      expirationDate: new Date().toISOString().split("T")[0],
      manufacturingDate: new Date().toISOString().split("T")[0],
      arrivalDate: new Date().toISOString().split("T")[0],
    }));

    this.generateTemInService
      .importProductsFromExcel(requestId, importData)
      .subscribe({
        next: (result) => {
          console.log("Save successful:", result);
          alert(`Lưu thành công thông tin tem của ${result.length} sản phẩm.`);
          this.isLoading = false;
          this.importProgress = 100;
        },
        error: (error) => {
          console.error("Save error:", error);
          alert("Error saving data: " + error.message);
          this.isLoading = false;
          this.importProgress = 0;
        },
      });
  }
  genQR(): void {
    console.log("Sang trang tạo mã chi tiết.");
  }

  editRow(row: VatTuRow): void {
    const dialogRef = this.dialog.open(GenerateTemInImportEditDialogComponent, {
      width: "600px",
      data: { ...row },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const index = this.dataSource.data.findIndex((r) => r.stt === row.stt);
        if (index > -1) {
          this.dataSource.data[index] = {
            ...this.dataSource.data[index],
            ...result,
          };
          this.dataSource._updateChangeSubscription();
        }
      }
    });
  }
  deleteRow(row: VatTuRow): void {
    const index = this.dataSource.data.findIndex((r) => r.stt === row.stt);
    if (index > -1) {
      this.dataSource.data.splice(index, 1);
      // Update STT for remaining rows
      this.dataSource.data.forEach((r, i) => (r.stt = i + 1));
      this.dataSource._updateChangeSubscription();
    }
  }

  private parseExcelFile(file: File): void {
    this.isLoading = true;

    this.excelParserService
      .parseExcelFile(file)
      .then((parsedData: ExcelImportData[]) => {
        if (parsedData.length === 0) {
          alert("Định dạng file không chính xác.");
          this.isLoading = false;
          return;
        }
        // , 'userData1',  'UserData2', 'UserData3', 'UserData4', 'UserData5', 'ExpirationDate', 'ManufacturingDate', 'ngayVe'
        const rows: VatTuRow[] = parsedData.map(
          (item: ExcelImportData, index: number) => ({
            stt: index + 1,
            sapCode: item.sapCode,
            tenSp: item.partNumber,
            partNumber: item.partNumber,
            lot: item.lot,
            storageUnit: item.storageUnit ?? "",
            soTem: item.temQuantity,
            initialQuantity: item.initialQuantity,
            vendor: item.vendor,
            userData1: item.userData1,
            userData2: item.userData2,
            userData3: item.userData3,
            userData4: item.userData4,
            userData5: item.userData5,
            expirationDate: item.expirationDate,
            manufacturingDate: item.manufacturingDate,
            arrivalDate: item.arrivalDate,
          }),
        );

        this.dataSource.data = rows;
        this.isLoading = false;

        console.log(
          `Successfully parsed ${parsedData.length} rows from Excel file`,
        );
      })
      .catch((error: Error) => {
        console.error("Lỗi:", error);
        alert("Lỗi:" + error.message);
        this.isLoading = false;
      });
  }
}
