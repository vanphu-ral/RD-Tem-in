import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";

export interface DialogData {
  type: "box" | "pallet";
  maSanPham?: string;
  tenSanPham?: string;
  sapWoId?: string;
  productCode?: string;
  woId?: string;
  loaiSanPham?: string;
}

export interface BoxFormData {
  maSanPham: string;
  soLuongTrongThung: number;
  soLuongThung: number;
}

export interface PalletFormData {
  tenSanPham: string;
  khachHang: string;
  noSKU: string;
  poNumber: string;
  dateCode: string;
  qdsx: string;
  nguoiKiemTra: string;
  ketQuaKiemTra: string;
  soLuongThungTrongPallet: number;
  soLuongPallet: number;
}

export interface DialogResult {
  type: "box" | "pallet";
  data: BoxFormData | PalletFormData;
}

@Component({
  selector: "jhi-create-dialog",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: "./create-dialog.component.html",
  styleUrls: ["./create-dialog.component.scss"],
})
export class CreateDialogComponent {
  form: FormGroup;
  isBoxType: boolean;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<CreateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {
    this.isBoxType = data.type === "box";
    this.form = this.createForm();
  }
  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.form.valid) {
      const result: DialogResult = {
        type: this.data.type,
        data: this.form.value,
      };
      this.dialogRef.close(result);
    }
  }
  private createForm(): FormGroup {
    if (this.isBoxType) {
      return this.fb.group({
        maSanPham: [this.data.maSanPham ?? "", Validators.required],
        soLuongTrongThung: [5000, [Validators.required, Validators.min(1)]],
        soLuongThung: [3, [Validators.required, Validators.min(1)]],
      });
    } else {
      return this.fb.group({
        tenSanPham: [this.data.tenSanPham ?? "", Validators.required],
        khachHang: [""],
        poNumber: [""],
        dateCode: [""],
        qdsx: [""],
        nguoiKiemTra: [""],
        ketQuaKiemTra: [""],
        soLuongThungTrongPallet: [30, [Validators.required, Validators.min(1)]],
        soLuongPallet: [1, [Validators.required, Validators.min(1)]],
        noSKU: [""],
      });
    }
  }

  get title(): string {
    if (this.isBoxType) {
      return this.data.loaiSanPham === "Thành phẩm"
        ? "Tạo thùng sản phẩm"
        : "Tạo tem BTP";
    }
    return "Tạo pallet";
  }
  get icon(): string {
    if (this.isBoxType) {
      return this.data.loaiSanPham === "Thành phẩm" ? "view_in_ar" : "qr_code";
    }
    return "layers";
  }

  get totalQuantity(): number {
    if (this.isBoxType) {
      const soLuongTrongThung = this.form.get("soLuongTrongThung")?.value ?? 0;
      const soLuongThung = this.form.get("soLuongThung")?.value ?? 0;
      return soLuongTrongThung * soLuongThung;
    } else {
      const soLuongThungTrongPallet =
        this.form.get("soLuongThungTrongPallet")?.value ?? 0;
      const soLuongPallet = this.form.get("soLuongPallet")?.value ?? 0;
      return soLuongThungTrongPallet * soLuongPallet;
    }
  }
}
