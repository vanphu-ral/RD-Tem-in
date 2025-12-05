import { Component } from "@angular/core";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { HttpClient } from "@angular/common/http";

import { ILenhSanXuat } from "../lenh-san-xuat.model";
import { LenhSanXuatService } from "../service/lenh-san-xuat.service";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { AccountService } from "app/core/auth/account.service";
import { Account } from "app/core/auth/account.model";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  templateUrl: "./lenh-san-xuat-delete-dialog.component.html",
  standalone: false,
})
export class LenhSanXuatDeleteDialogComponent {
  lenhSanXuat?: ILenhSanXuat;

  constructor(
    protected lenhSanXuatService: LenhSanXuatService,
    protected activeModal: NgbActiveModal,
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    protected accountService: AccountService,
    protected snackBar: MatSnackBar,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.accountService.getAuthenticationState().subscribe((account) => {
      if (account) {
        const currentTime = new Date().toISOString();
        const requestBody = {
          deleted_at: currentTime,
          deleted_by: account.login,
        };
        const resourceUrl = this.applicationConfigService.getEndpointFor(
          "api/warehouse-note-infos",
        );
        this.http.patch(`${resourceUrl}/${id}`, requestBody).subscribe({
          next: () => {
            this.snackBar.open(`Xóa thành công`, "Đóng", {
              duration: 3000,
              panelClass: ["snackbar-success"],
            });
            this.activeModal.close(id);
          },
          error: (err) => {
            this.snackBar.open(
              `Lỗi khi xóa: ${err.error?.message || err.message}`,
              "Đóng",
              {
                duration: 4000,
                panelClass: ["snackbar-error"],
              },
            );
          },
        });
      }
    });
  }
}
