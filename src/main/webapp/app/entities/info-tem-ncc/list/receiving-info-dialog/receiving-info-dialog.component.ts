import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import {
  ReceivingStation,
  receivingStationLabel,
  searchReceivingStations,
} from "../../shared/receiving-station.util";

export interface ReceivingInfo {
  receivingStation: string;
  assignee: string;
}

export interface ReceivingInfoDialogData extends ReceivingInfo {
  deliveryNotice: string;
}

/** Mobile: popup nhập trạm tiếp nhận + người tiếp nhận của 1 TBGH */
@Component({
  selector: "jhi-receiving-info-dialog",
  templateUrl: "./receiving-info-dialog.component.html",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatInputModule,
  ],
})
export class ReceivingInfoDialogComponent {
  receivingStation: string;
  assignee: string;
  stationOptions: ReceivingStation[] = searchReceivingStations("");
  readonly stationLabel = receivingStationLabel;
  /** Trạm phải chọn từ danh sách (hoặc để trống) */
  stationValid = true;

  constructor(
    public dialogRef: MatDialogRef<ReceivingInfoDialogComponent, ReceivingInfo>,
    @Inject(MAT_DIALOG_DATA) public data: ReceivingInfoDialogData,
  ) {
    this.receivingStation = data.receivingStation;
    this.assignee = data.assignee;
  }

  onStationInput(term: string): void {
    const keyword = (term ?? "").trim();
    this.stationValid =
      !keyword || keyword === this.data.receivingStation.trim();
    this.stationOptions = searchReceivingStations(keyword);
  }

  onStationSelected(): void {
    this.stationValid = true;
  }

  save(): void {
    if (!this.stationValid) {
      return;
    }
    this.dialogRef.close({
      receivingStation: this.receivingStation.trim(),
      assignee: this.assignee.trim(),
    });
  }
}
