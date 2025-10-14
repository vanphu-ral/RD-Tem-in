import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

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

@Component({
  selector: "jhi-generate-tem-in-import-edit-dialog",
  templateUrl: "./generate-tem-in-import-edit-dialog.component.html",
  styleUrls: ["./generate-tem-in-import-edit-dialog.component.scss"],
})
export class GenerateTemInImportEditDialogComponent {
  editForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<GenerateTemInImportEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VatTuRow,
    private fb: FormBuilder,
  ) {
    this.editForm = this.fb.group({
      sapCode: [data.sapCode, Validators.required],
      tenSp: [data.tenSp, Validators.required],
      partNumber: [data.partNumber, Validators.required],
      lot: [data.lot, Validators.required],
      storageUnit: [data.storageUnit],
      soTem: [data.soTem, Validators.required],
      initialQuantity: [data.initialQuantity, Validators.required],
      vendor: [data.vendor],
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
      this.dialogRef.close(this.editForm.value);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
