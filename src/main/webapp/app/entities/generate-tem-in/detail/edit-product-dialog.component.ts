import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

@Component({
  selector: "jhi-edit-product-dialog",
  templateUrl: "./edit-product-dialog.component.html",
  styleUrls: ["./edit-product-dialog.component.scss"],
})
export class EditProductDialogComponent implements OnInit {
  editForm!: FormGroup;
  isSaving = false;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EditProductDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {}

  ngOnInit(): void {
    this.editForm = this.fb.group({
      sapCode: [this.data.item.sapCode, Validators.required],
      productName: [this.data.item.productName, Validators.required],
      partNumber: [this.data.item.partNumber, Validators.required],
      lot: [this.data.item.lot, Validators.required],
      storageUnit: [this.data.item.storageUnit],
      temQuantity: [this.data.item.temQuantity, Validators.required],
      initialQuantity: [this.data.item.initialQuantity, Validators.required],
      vendor: [this.data.item.vendor],
      vendorName: [this.data.item.vendorName],
      userData1: [this.data.item.userData1],
      userData2: [this.data.item.userData2],
      userData3: [this.data.item.userData3],
      userData4: [this.data.item.userData4],
      userData5: [this.data.item.userData5],
      expirationDate: [this.formatDateForInput(this.data.item.expirationDate)],
      manufacturingDate: [
        this.formatDateForInput(this.data.item.manufacturingDate),
      ],
      arrivalDate: [this.formatDateForInput(this.data.item.arrivalDate)],
    });
  }

  /**
   * Chuyển đổi date từ server về định dạng YYYY-MM-DD cho input type="date"
   */
  formatDateForInput(date: any): string {
    if (!date) {
      return "";
    }
    try {
      const d = new Date(date);
      if (isNaN(d.getTime())) {
        return "";
      }
      return d.toISOString().split("T")[0];
    } catch (e) {
      return "";
    }
  }
  /**
   * Chuyển đổi date từ input về định dạng ISO DateTime cho server
   * Thêm thời gian 00:00:00 nếu chỉ có date
   */
  formatDateForServer(dateString: string): string | null {
    if (!dateString) {
      return null;
    }

    try {
      if (dateString.includes("T")) {
        return dateString.replace("Z", ""); // Bỏ timezone
      }

      // Trả về: YYYY-MM-DDTHH:mm:ss
      return dateString + "T00:00:00";
    } catch (e) {
      console.error("Error formatting date:", e);
      return null;
    }
  }

  onSave(): void {
    if (this.editForm.valid && !this.isSaving) {
      this.isSaving = true;
      const formValue = { ...this.editForm.value };

      // Chuyển đổi các trường date sang định dạng đúng
      formValue.expirationDate = this.formatDateForServer(
        formValue.expirationDate,
      );
      formValue.manufacturingDate = this.formatDateForServer(
        formValue.manufacturingDate,
      );
      formValue.arrivalDate = this.formatDateForServer(formValue.arrivalDate);

      this.dialogRef.close({ action: "save", data: formValue });
    }
  }

  onCancel(): void {
    this.dialogRef.close({ action: "cancel" });
  }
}
