import { Injectable } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog"; // Import MatDialogRef
import { Observable } from "rxjs";
import { map, take } from "rxjs/operators";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
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
    private breakpointObserver: BreakpointObserver,
  ) {}
  public isMobileScreen(): boolean {
    return this.breakpointObserver.isMatched(Breakpoints.Handset);
  }

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
          approved?: boolean;
        }
      >
    | undefined {
    if (itemsToUpdate && itemsToUpdate.length > 0) {
      const isMobile = this.isMobileScreen();

      return this.dialog.open<
        ListMaterialUpdateDialogComponent,
        { items: RawGraphQLMaterial[] },
        {
          updatedItems: MaterialItem[];
          selectedWarehouse: any;
          approvers: string[];
          approved?: boolean;
        }
      >(ListMaterialUpdateDialogComponent, {
        minWidth: isMobile ? "100vw" : "320px",
        maxWidth: isMobile ? "100vw" : "100vw",
        width: isMobile ? "100vw" : "100vw",
        height: isMobile ? "100vh" : "100vh",
        panelClass: isMobile ? "dialog-fullscreen-pane" : "dialog-center-pane",
        data: { items: itemsToUpdate },
        autoFocus: false,
        scrollStrategy: this.overlay.scrollStrategies.noop(),
        disableClose: true,
      });
    }
    return undefined;
  }
}
