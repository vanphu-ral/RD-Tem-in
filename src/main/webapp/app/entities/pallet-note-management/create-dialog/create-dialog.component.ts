import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
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
import { AccountService } from "app/core/auth/account.service";
import { MatOptionModule } from "@angular/material/core";
import { MatSelectModule } from "@angular/material/select";

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
  comments?: string;
  TPNK?: string;
  rank?: string;
  mfg?: string;
  note?: string;
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
  note: string;
  qtyPerBox: number;
  soLuongThungScan: number;
  soLuongPallet: number;
  soLuongThungThucTe: number;
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
    MatOptionModule,
    MatSelectModule,
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
    private accountService: AccountService,
  ) {
    this.isBoxType = data.type === "box";
    this.form = this.createForm();

    // Debug log để kiểm tra
    console.log("🔍 Dialog Data:", this.data);
    console.log("🔍 Expected tenSanPham:", this.data.tenSanPham);
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
    } else {
      // Mark all as touched để hiển thị lỗi
      this.form.markAllAsTouched();

      // Debug log để xem lỗi validation
      console.log("Form invalid:", this.form.errors);
      Object.keys(this.form.controls).forEach((key) => {
        const control = this.form.get(key);
        if (control?.invalid) {
          console.log(`Field "${key}" errors:`, control.errors);
        }
      });
    }
  }

  private createForm(): FormGroup {
    const currentUser = this.accountService.isAuthenticated()
      ? this.accountService["userIdentity"]?.login
      : "unknown";

    if (this.isBoxType) {
      const expectedMaSanPham = this.data.maSanPham ?? "";
      const formGroup = this.fb.group({
        maSanPham: [
          expectedMaSanPham, // Set initial value
          [
            Validators.required,
            this.matchTenSanPhamValidator(expectedMaSanPham),
          ],
        ],
        soLuongTrongThung: [
          24,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(99999),
            Validators.pattern(/^[1-9][0-9]{0,4}$/),
          ],
        ],
        soLuongThung: [
          1,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(99999),
            Validators.pattern(/^[1-9][0-9]{0,4}$/),
          ],
        ],
      }) as FormGroup;

      // Nếu là Bán thành phẩm thì thêm trường đặc thù
      if (this.data.loaiSanPham === "Bán thành phẩm") {
        formGroup.addControl(
          "TPNK",
          this.fb.control("", [Validators.maxLength(50)]),
        );
        formGroup.addControl(
          "rank",
          this.fb.control("", [Validators.maxLength(50)]),
        );
        formGroup.addControl(
          "mfg",
          this.fb.control("", [Validators.maxLength(50)]),
        );
        formGroup.addControl(
          "note",
          this.fb.control("", [Validators.maxLength(200)]),
        );
        formGroup.addControl(
          "comments",
          this.fb.control("", [Validators.maxLength(200)]),
        );
      }

      return formGroup;
    } else {
      // ===== PALLET FORM =====
      const expectedTenSanPham = this.data.tenSanPham ?? "";

      return this.fb.group({
        tenSanPham: [
          expectedTenSanPham, // Set initial value
          [
            Validators.required,
            this.matchTenSanPhamValidator(expectedTenSanPham),
          ],
        ],
        khachHang: [""],
        poNumber: [""],
        dateCode: [""],
        qdsx: [""],
        nguoiKiemTra: [currentUser],
        ketQuaKiemTra: ["Đạt"],
        note: [""],
        qtyPerBox: [
          1,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(99999),
            Validators.pattern(/^[1-9][0-9]{0,4}$/),
          ],
        ],
        soLuongThungScan: [
          1,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(99999),
            Validators.pattern(/^[1-9][0-9]{0,4}$/),
          ],
        ],
        soLuongThungThucTe: [
          1,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(99999),
            Validators.pattern(/^[1-9][0-9]{0,4}$/),
          ],
        ],
        soLuongPallet: [
          1,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(99999),
            Validators.pattern(/^[1-9][0-9]{0,4}$/),
          ],
        ],
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
        this.form.get("soLuongThungThucTe")?.value ?? 0;
      const soLuongPallet = this.form.get("soLuongPallet")?.value ?? 0;
      return soLuongThungTrongPallet * soLuongPallet;
    }
  }

  /**
   * Custom validator để kiểm tra tên sản phẩm khớp với giá trị ban đầu
   */
  private matchTenSanPhamValidator(expectedValue: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // Nếu chưa có giá trị, bỏ qua (để required validator xử lý)
      if (!control.value) {
        return null;
      }

      // Trim whitespace để tránh lỗi do khoảng trắng
      const currentValue = String(control.value).trim();
      const expected = String(expectedValue).trim();
      // So sánh chính xác
      if (currentValue !== expected) {
        return {
          notMatch: true,
          expectedValue: expected,
          currentValue: currentValue,
        };
      }

      return null;
    };
  }
}
