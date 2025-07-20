import { Injectable } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog"; // Import MatDialogRef
import { Observable } from "rxjs";
import { map, take } from "rxjs/operators";
import {
  ListMaterialService,
  RawGraphQLMaterial,
} from "./list-material.service";
import {
  ListMaterialUpdateDialogComponent,
  MaterialItem,
} from "../dialog/list-material-update-dialog";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { Overlay } from "@angular/cdk/overlay";

@Injectable({
  providedIn: "root",
})
export class MaterialUpdateService {
  constructor(
    private materialService: ListMaterialService,
    private dialog: MatDialog,
    private http: HttpClient,
    private overlay: Overlay,
  ) {}

  getCheckedCount$(): Observable<number> {
    return this.materialService.selectedIds$.pipe(map((ids) => ids.length));
  }

  openEditSelectedDialog(itemsToUpdate: RawGraphQLMaterial[]):
    | MatDialogRef<
        ListMaterialUpdateDialogComponent,
        {
          updatedItems: MaterialItem[];
          selectedWarehouse: any;
          approvers: string[];
        }
      >
    | undefined {
    if (itemsToUpdate && itemsToUpdate.length > 0) {
      return this.dialog.open<
        ListMaterialUpdateDialogComponent,
        { items: RawGraphQLMaterial[] },
        {
          updatedItems: MaterialItem[];
          selectedWarehouse: any;
          approvers: string[];
        }
      >(ListMaterialUpdateDialogComponent, {
        minWidth: "320px",
        maxWidth: "1400px",
        width: "90vw",
        height: "auto",
        panelClass: "dialog-center-pane",
        data: { items: itemsToUpdate },
        autoFocus: false,
        scrollStrategy: this.overlay.scrollStrategies.noop(),
        disableClose: true,
      });
    }
    return undefined;
  }
}
