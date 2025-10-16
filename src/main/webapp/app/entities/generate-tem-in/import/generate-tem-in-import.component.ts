// generate-tem-in-import.component.ts
import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { FormControl } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { GenerateTemInImportEditDialogComponent } from "./generate-tem-in-import-edit-dialog.component";

// Models and Services
import {
  ListProductOfRequest,
  ExcelImportData,
} from "../models/list-product-of-request.model";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import {
  ExcelParserService,
  GroupedExcelData,
} from "../service/excel-parser.service";
import { AlertService } from "app/core/util/alert.service";

export interface SaveStatus {
  groupIndex: number;
  groupName: string;
  status: "pending" | "saving" | "success" | "error";
  message?: string;
  timestamp?: Date;
}

export interface VatTuRow {
  stt: number;
  id?: number; // Product ID from database
  requestCreateTemId?: number; // Request ID from database
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

type VatTuKeys = keyof VatTuRow;
type DisplayColumnKeys = VatTuKeys | "ngayVe";

@Component({
  selector: "jhi-generate-tem-in-import",
  templateUrl: "./generate-tem-in-import.component.html",
  styleUrls: ["./generate-tem-in-import.component.scss"],
})
export class GenerateTemInImportComponent implements OnInit, AfterViewInit {
  khoControl = new FormControl<string | null>(null);
  allowSplitControl = new FormControl<boolean>(false);

  // Popup control
  showImportPopup = false;

  // Track saved request IDs for each group to prevent duplicate creation
  savedRequestIds: Map<number, number> = new Map();

  // Track if data has been modified after save
  dataModified: Map<number, boolean> = new Map();

  // Track save button state for each group
  isSaving: Map<number, boolean> = new Map();

  // Track save status for notification panel
  saveStatuses: SaveStatus[] = [];
  showSaveStatusPanel = false;

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

  // Grouped data support
  groupedData: GroupedExcelData[] = [];
  selectedTabIndex = 0;
  tabLabels: string[] = [];

  dataSource = new MatTableDataSource<VatTuRow>([]);
  groupDataSources: Map<number, MatTableDataSource<VatTuRow>> = new Map();
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
    private alertService: AlertService,
    private router: Router,
  ) {}

  fc(k: DisplayColumnKeys): FormControl<string | null> {
    // Map 'ngayVe' display column to 'arrivalDate' data property
    const actualKey = (k === "ngayVe" ? "arrivalDate" : k) as VatTuKeys;

    if (!this.filters[actualKey]) {
      this.filters[actualKey] = new FormControl<string | null>("");
      this.filters[actualKey]!.valueChanges.subscribe(() => {
        this.applyFilter();
      });
    }
    return this.filters[actualKey]!;
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
      this.alertService.addAlert({
        type: "warning",
        message: "Please select a valid Excel file (.xlsx or .xls)",
        timeout: 5000,
        toast: true,
      });
      this.selectedFile = null;
      return;
    }

    this.selectedFile = file;
  }

  getPOCode(): void {
    return;
  }

  onTabChange(event: any): void {
    this.selectedTabIndex = event;
    this.updateDataSourceForSelectedTab();
  }

  getDataSourceForGroup(groupIndex: number): MatTableDataSource<VatTuRow> {
    if (!this.groupDataSources.has(groupIndex)) {
      if (this.groupedData.length > groupIndex) {
        const group = this.groupedData[groupIndex];
        const rows: VatTuRow[] = group.products.map(
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
        this.groupDataSources.set(
          groupIndex,
          new MatTableDataSource<VatTuRow>(rows),
        );
      }
    }
    return this.groupDataSources.get(groupIndex)!;
  }

  // Open import popup
  openImportPopup(): void {
    this.showImportPopup = true;
  }

  // Close import popup
  closeImportPopup(): void {
    this.showImportPopup = false;
  }

  import(): void {
    if (!this.selectedFile) {
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng import file excel.",
        timeout: 5000,
        toast: true,
      });
      return;
    }

    // Reset saved request IDs when importing new data
    this.savedRequestIds.clear();
    this.dataModified.clear();
    this.isSaving.clear();

    this.parseExcelFile(this.selectedFile);

    // Close popup after successful import
    this.closeImportPopup();
  }

  save(): void {
    if (this.groupedData.length === 0) {
      this.alertService.addAlert({
        type: "warning",
        message: "Không có dữ liệu để lưu",
        timeout: 5000,
        toast: true,
      });
      return;
    }

    this.isLoading = true;
    this.importProgress = 0;

    // Save each group as a separate request
    const savePromises = this.groupedData.map((group, index) => {
      const currentIndex = index; // Capture index for progress tracking
      return this.generateTemInService
        .createRequestAndProducts(
          group.vendor,
          group.poCode,
          "system", // You might want to get this from user context
          group.products,
        )
        .toPromise()
        .then((result) => {
          // Update progress
          this.importProgress = Math.round(
            ((currentIndex + 1) / this.groupedData.length) * 100,
          );
          return result;
        })
        .catch((error) => {
          console.error(`Error saving group ${currentIndex + 1}:`, error);
          throw error;
        });
    });

    // Execute all save operations
    Promise.all(savePromises)
      .then((results) => {
        console.log("All groups saved successfully:", results);
        const totalProducts = results.reduce(
          (sum, result) => sum + (result?.products?.length ?? 0),
          0,
        );
        this.alertService.addAlert({
          type: "success",
          message: `Lưu thành công ${this.groupedData.length} yêu cầu với tổng cộng ${totalProducts} sản phẩm.`,
          timeout: 5000,
          toast: true,
        });
        this.isLoading = false;
        this.importProgress = 100;
      })
      .catch((error) => {
        console.error("Save error:", error);
        this.alertService.addAlert({
          type: "danger",
          message: `Lỗi khi lưu dữ liệu: ${error.message}`,
          timeout: 5000,
          toast: true,
        });
        this.isLoading = false;
        this.importProgress = 0;
      });
  }

  saveGroup(groupIndex: number): void {
    if (this.groupedData.length === 0 || !this.groupedData[groupIndex]) {
      this.alertService.addAlert({
        type: "warning",
        message: "Không có dữ liệu để lưu",
        timeout: 5000,
        toast: true,
      });
      return;
    }

    const group = this.groupedData[groupIndex];
    this.isSaving.set(groupIndex, true);

    // Show save status panel and add/update status
    this.showSaveStatusPanel = true;
    const groupName = `${group.poCode} - ${group.vendor}`;
    this.updateSaveStatus(groupIndex, groupName, "saving");

    // Get current data from the data source (includes any edits)
    const dataSource = this.getDataSourceForGroup(groupIndex);
    const currentData = dataSource.data;

    // Update group.products with current table data
    group.products = currentData.map((row) => ({
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
      expirationDate: row.expirationDate,
      manufacturingDate: row.manufacturingDate,
      arrivalDate: row.arrivalDate,
    }));

    // Check if this group has already been saved
    const existingRequestId = this.savedRequestIds.get(groupIndex);

    if (existingRequestId) {
      // Update existing request - use the existing requestId
      this.alertService.addAlert({
        type: "info",
        message: `Đang cập nhật nhóm ${groupIndex + 1}...`,
        timeout: 3000,
        toast: true,
      });

      // Update products for the existing request
      this.generateTemInService
        .updateRequestProducts(existingRequestId, group.products)
        .toPromise()
        .then((result: any) => {
          console.log(`Group ${groupIndex + 1} updated successfully:`, result);
          const totalProducts = result?.length ?? 0;

          // Update the data source with the returned product IDs
          if (result && Array.isArray(result)) {
            const updatedDataSource = this.getDataSourceForGroup(groupIndex);
            updatedDataSource.data = updatedDataSource.data.map((row, idx) => ({
              ...row,
              id: result[idx]?.id,
              requestCreateTemId: existingRequestId,
            }));
            updatedDataSource._updateChangeSubscription();
          }

          // Mark as not modified after successful save
          this.dataModified.set(groupIndex, false);

          this.alertService.addAlert({
            type: "success",
            message: `Cập nhật thành công nhóm ${groupIndex + 1} (${group.poCode} - ${group.vendor}) với ${totalProducts} sản phẩm.`,
            timeout: 5000,
            toast: true,
          });
          this.isSaving.set(groupIndex, false);

          // Update save status
          this.updateSaveStatus(
            groupIndex,
            groupName,
            "success",
            `Cập nhật thành công ${totalProducts} sản phẩm`,
          );
        })
        .catch((error: any) => {
          console.error(`Error updating group ${groupIndex + 1}:`, error);
          this.alertService.addAlert({
            type: "danger",
            message: `Lỗi khi cập nhật nhóm ${groupIndex + 1}: ${error.message}`,
            timeout: 5000,
            toast: true,
          });
          this.isSaving.set(groupIndex, false);

          // Update save status
          this.updateSaveStatus(groupIndex, groupName, "error", error.message);
        });
    } else {
      // Create new request
      this.generateTemInService
        .createRequestAndProducts(
          group.vendor,
          group.poCode,
          "system",
          group.products,
        )
        .toPromise()
        .then((result) => {
          console.log(`Group ${groupIndex + 1} saved successfully:`, result);
          const totalProducts = result?.products?.length ?? 0;

          // Store the request ID for future saves
          if (result?.requestId) {
            this.savedRequestIds.set(groupIndex, result.requestId);
          }

          // Update the data source with the returned product IDs and requestId
          if (result && result.products && result.requestId) {
            const updatedDataSource = this.getDataSourceForGroup(groupIndex);
            updatedDataSource.data = updatedDataSource.data.map((row, idx) => ({
              ...row,
              id: result.products[idx]?.id,
              requestCreateTemId: result.requestId,
            }));
            updatedDataSource._updateChangeSubscription();
          }

          // Mark as not modified after successful save
          this.dataModified.set(groupIndex, false);

          this.alertService.addAlert({
            type: "success",
            message: `Lưu thành công nhóm ${groupIndex + 1} (${group.poCode} - ${group.vendor})`,
            timeout: 5000,
            toast: true,
          });
          this.isSaving.set(groupIndex, false);

          this.updateSaveStatus(
            groupIndex,
            groupName,
            "success",
            `Lưu thành công`,
          );
        })
        .catch((error) => {
          console.error(`Error saving group ${groupIndex + 1}:`, error);
          this.alertService.addAlert({
            type: "danger",
            message: `Lỗi khi lưu nhóm ${groupIndex + 1}: ${error.message}`,
            timeout: 5000,
            toast: true,
          });
          this.isSaving.set(groupIndex, false);

          // Update save status
          this.updateSaveStatus(groupIndex, groupName, "error", error.message);
        });
    }
  }

  // Update save status for notification panel with auto-close
  updateSaveStatus(
    groupIndex: number,
    groupName: string,
    status: "pending" | "saving" | "success" | "error",
    message?: string,
  ): void {
    const existingIndex = this.saveStatuses.findIndex(
      (s) => s.groupIndex === groupIndex,
    );
    const newStatus: SaveStatus = {
      groupIndex,
      groupName,
      status,
      message,
      timestamp: new Date(),
    };

    if (existingIndex >= 0) {
      this.saveStatuses[existingIndex] = newStatus;
    } else {
      this.saveStatuses.push(newStatus);
    }

    // Auto-close after 3 seconds for success or error status
    if (status === "success" || status === "error") {
      setTimeout(() => {
        this.clearSaveStatuses();
      }, 3000);
    }
  }

  // Clear all save statuses
  clearSaveStatuses(): void {
    this.saveStatuses = [];
    this.showSaveStatusPanel = false;
  }

  // Get status icon
  getStatusIcon(status: string): string {
    switch (status) {
      case "saving":
        return "hourglass_empty";
      case "success":
        return "check_circle";
      case "error":
        return "error";
      default:
        return "pending";
    }
  }

  // Get status color class
  getStatusClass(status: string): string {
    switch (status) {
      case "saving":
        return "status-saving";
      case "success":
        return "status-success";
      case "error":
        return "status-error";
      default:
        return "status-pending";
    }
  }

  genQR(): void {
    console.log("Sang trang tạo mã chi tiết.");
  }

  genQRForGroup(groupIndex: number): void {
    if (this.groupedData.length === 0 || !this.groupedData[groupIndex]) {
      this.alertService.addAlert({
        type: "warning",
        message: "Không có dữ liệu để tạo mã QR",
        timeout: 5000,
        toast: true,
      });
      return;
    }

    // Check if this group has been saved
    const requestId = this.savedRequestIds.get(groupIndex);
    if (!requestId) {
      this.alertService.addAlert({
        type: "warning",
        message: "Vui lòng lưu dữ liệu trước khi tạo mã QR",
        timeout: 5000,
        toast: true,
      });
      return;
    }

    const group = this.groupedData[groupIndex];
    console.log(
      `Chuyển sang trang chi tiết để tạo mã QR cho nhóm ${groupIndex + 1}: PO ${group.poCode} - Vendor ${group.vendor}, Request ID: ${requestId}`,
    );

    // Navigate to detail page with the requestId
    this.router.navigate(["/generate-tem-in/detail", requestId]);
  }

  // Debug method to check data loading
  checkDataLoading(): void {
    console.log("=== DATA LOADING DEBUG ===");
    console.log(`Grouped data length: ${this.groupedData.length}`);
    console.log(`Group data sources size: ${this.groupDataSources.size}`);

    this.groupedData.forEach((group, index) => {
      console.log(
        `Group ${index}: PO ${group.poCode}, Vendor: ${group.vendor}, Products: ${group.products.length}`,
      );
      const dataSource = this.getDataSourceForGroup(index);
      console.log(
        `Data source for group ${index}: ${dataSource?.data?.length || 0} rows`,
      );
    });
  }

  editRow(row: VatTuRow, groupIndex?: number): void {
    const dialogRef = this.dialog.open(GenerateTemInImportEditDialogComponent, {
      width: "600px",
      data: { ...row },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (groupIndex !== undefined) {
          // Edit row in specific group
          const dataSource = this.getDataSourceForGroup(groupIndex);
          const index = dataSource.data.findIndex((r) => r.stt === row.stt);
          if (index > -1) {
            dataSource.data[index] = {
              ...dataSource.data[index],
              ...result,
            };
            dataSource._updateChangeSubscription();

            // Mark data as modified
            this.dataModified.set(groupIndex, true);
          }
        } else {
          // Fallback to old method for backward compatibility
          const index = this.dataSource.data.findIndex(
            (r) => r.stt === row.stt,
          );
          if (index > -1) {
            this.dataSource.data[index] = {
              ...this.dataSource.data[index],
              ...result,
            };
            this.dataSource._updateChangeSubscription();
          }
        }
      }
    });
  }

  deleteRow(row: VatTuRow, groupIndex?: number): void {
    if (groupIndex !== undefined) {
      // Delete row from specific group
      const dataSource = this.getDataSourceForGroup(groupIndex);
      const index = dataSource.data.findIndex((r) => r.stt === row.stt);
      if (index > -1) {
        dataSource.data.splice(index, 1);
        // Update STT for remaining rows
        dataSource.data.forEach((r, i) => (r.stt = i + 1));
        dataSource._updateChangeSubscription();

        // Mark data as modified
        this.dataModified.set(groupIndex, true);
      }
    } else {
      // Fallback to old method for backward compatibility
      const index = this.dataSource.data.findIndex((r) => r.stt === row.stt);
      if (index > -1) {
        this.dataSource.data.splice(index, 1);
        // Update STT for remaining rows
        this.dataSource.data.forEach((r, i) => (r.stt = i + 1));
        this.dataSource._updateChangeSubscription();
      }
    }
  }

  // Check if save button should be disabled for a group
  isSaveDisabled(groupIndex: number): boolean {
    // Disable if currently saving
    if (this.isSaving.get(groupIndex)) {
      return true;
    }

    // Disable if already saved and no modifications made
    if (
      this.savedRequestIds.has(groupIndex) &&
      !this.dataModified.get(groupIndex)
    ) {
      return true;
    }

    return false;
  }

  // Get save button text based on state
  getSaveButtonText(groupIndex: number): string {
    if (this.isSaving.get(groupIndex)) {
      return "Đang lưu...";
    }

    if (this.savedRequestIds.has(groupIndex)) {
      return this.dataModified.get(groupIndex) ? "Cập nhật" : "Đã lưu";
    }

    return "Lưu";
  }

  private updateDataSourceForSelectedTab(): void {
    if (this.selectedTabIndex < this.groupedData.length) {
      const selectedGroup = this.groupedData[this.selectedTabIndex];
      const rows: VatTuRow[] = selectedGroup.products.map(
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
    }
  }

  private parseExcelFile(file: File): void {
    this.isLoading = true;

    this.excelParserService
      .parseExcelFile(file)
      .then((parsedData: ExcelImportData[]) => {
        if (parsedData.length === 0) {
          this.alertService.addAlert({
            type: "danger",
            message: "Định dạng file không chính xác.",
            timeout: 5000,
            toast: true,
          });
          this.isLoading = false;
          return;
        }

        // Group data by PO Code and Vendor
        this.groupedData =
          this.excelParserService.groupDataByPOAndVendor(parsedData);

        if (this.groupedData.length === 0) {
          this.alertService.addAlert({
            type: "warning",
            message: "Không có dữ liệu hợp lệ để nhóm.",
            timeout: 5000,
            toast: true,
          });
          this.isLoading = false;
          return;
        }

        // Create tab labels for each group
        this.tabLabels = this.groupedData.map(
          (group) =>
            `${group.poCode} - ${group.vendor} (${group.products.length} sản phẩm)`,
        );

        // Create data sources for all groups
        this.groupDataSources.clear();
        this.groupedData.forEach((_, index) => {
          this.getDataSourceForGroup(index);
        });

        // Update data source with first group for backward compatibility
        this.updateDataSourceForSelectedTab();
        this.isLoading = false;

        console.log(
          `Successfully parsed ${parsedData.length} rows into ${this.groupedData.length} groups`,
        );

        console.log(
          `Successfully parsed and grouped ${parsedData.length} rows into ${this.groupedData.length} groups`,
        );
      })
      .catch((error: Error) => {
        console.error("Lỗi:", error);
        this.alertService.addAlert({
          type: "danger",
          message: `Lỗi: ${error.message}`,
          timeout: 5000,
          toast: true,
        });
        this.isLoading = false;
      });
  }
}
