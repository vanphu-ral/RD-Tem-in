import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { GenerateTemInService } from "../service/generate-tem-in.service";
import { AlertService } from "app/core/util/alert.service";

export interface VatTuRow {
  stt: number;
  id?: number;
  requestCreateTemId?: number;
  sapCode: string;
  tenSp: string;
  partNumber: string;
  lot: string | number;
  storageUnit: string;
  soTem: number;
  initialQuantity: number;
  vendor: string;
  tenNCC: string;
  userData1: string;
  userData2: string;
  userData3: string;
  userData4: string;
  userData5: string;
  expirationDate: string;
  manufacturingDate: string;
  arrivalDate: string;
}

export interface EditDialogData extends VatTuRow {
  isSaved?: boolean;
  groupIndex?: number;
}

@Component({
  selector: "jhi-generate-tem-in-import-edit-dialog",
  templateUrl: "./generate-tem-in-import-edit-dialog.component.html",
  styleUrls: ["./generate-tem-in-import-edit-dialog.component.scss"],
})
export class GenerateTemInImportEditDialogComponent {
  editForm: FormGroup;
  isSaved: boolean = false;
  isSaving: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<GenerateTemInImportEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditDialogData,
    private fb: FormBuilder,
    private generateTemInService: GenerateTemInService,
    private alertService: AlertService,
  ) {
    this.isSaved = data.isSaved ?? false;

    this.editForm = this.fb.group({
      sapCode: [data.sapCode, Validators.required],
      tenSp: [data.tenSp, Validators.required],
      partNumber: [data.partNumber, Validators.required],
      lot: [data.lot, Validators.required],
      storageUnit: [data.storageUnit],
      soTem: [data.soTem, Validators.required],
      initialQuantity: [data.initialQuantity, Validators.required],
      vendor: [data.vendor],
      tenNCC: [data.tenNCC],
      userData1: [data.userData1],
      userData2: [data.userData2],
      userData3: [data.userData3],
      userData4: [data.userData4],
      userData5: [data.userData5],
      expirationDate: [data.expirationDate],
      manufacturingDate: [data.manufacturingDate],
      arrivalDate: [data.arrivalDate],
    });
  }

  onSave(): void {
    if (this.editForm.valid) {
      const result = {
        ...this.editForm.value,
        id: this.data.id,
        requestCreateTemId: this.data.requestCreateTemId,
        stt: this.data.stt,
        isSaved: this.isSaved,
      };

      // If row has ID and requestCreateTemId, save to database
      if (this.data.id && this.data.requestCreateTemId) {
        this.isSaving = true;

        // Convert date strings to LocalDateTime format (YYYY-MM-DDTHH:mm:ss)
        const formatDateForBackend = (dateStr: string): string => {
          if (!dateStr) {
            return "";
          }
          // If already in datetime format, return as is
          if (dateStr.includes("T")) {
            return dateStr;
          }
          // Otherwise append time component
          return `${dateStr}T00:00:00`;
        };

        const productData = {
          id: this.data.id,
          requestCreateTemId: this.data.requestCreateTemId,
          sapCode: result.sapCode,
          partNumber: result.partNumber,
          lot: result.lot,
          temQuantity: result.soTem,
          initialQuantity: result.initialQuantity,
          vendor: result.vendor,
          userData1: result.userData1,
          userData2: result.userData2,
          userData3: result.userData3,
          userData4: result.userData4,
          userData5: result.userData5,
          storageUnit: result.storageUnit,
          expirationDate: formatDateForBackend(result.expirationDate),
          manufacturingDate: formatDateForBackend(result.manufacturingDate),
          arrivalDate: formatDateForBackend(result.arrivalDate),
          numberOfPrints: 0,
        };

        this.generateTemInService
          .updateProductOfRequest(productData)
          .subscribe({
            next: (response: any) => {
              this.isSaving = false;
              this.alertService.addAlert({
                type: "success",
                message: "Cập nhật sản phẩm thành công",
                timeout: 3000,
                toast: true,
              });
              this.dialogRef.close(result);
            },
            error: (error: any) => {
              this.isSaving = false;
              console.error("Error updating product:", error);
              this.alertService.addAlert({
                type: "danger",
                message: `Lỗi khi cập nhật sản phẩm: ${error.message}`,
                timeout: 5000,
                toast: true,
              });
            },
          });
      } else {
        // No ID, just update frontend table
        this.dialogRef.close(result);
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
