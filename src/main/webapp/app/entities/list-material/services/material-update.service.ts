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

@Injectable({
  providedIn: "root",
})
export class MaterialUpdateService {
  constructor(
    private materialService: ListMaterialService,
    private dialog: MatDialog,
    private http: HttpClient,
  ) {}
  //  private apiUrl = environment.restApiBaseUrl + '/api/post/request-update';

  // sendRequestUpdate(data: any): Observable<any> {
  //     const headers = new HttpHeaders({
  //       'Content-Type': 'application/json',
  //     });
  //     return this.http.post(`${this.apiUrl}`, data, { headers, responseType: 'text' });
  //   }

  getCheckedCount$(): Observable<number> {
    return this.materialService.selectedIds$.pipe(map((ids) => ids.length));
  }

  openEditSelectedDialog(
    itemsToUpdate: RawGraphQLMaterial[],
  ):
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
        width: "80%",
        maxWidth: "75vw",
        maxHeight: "90vh",
        panelClass: "dialog-center-pane",
        data: { items: itemsToUpdate },
        autoFocus: false, // Thêm autoFocus: false vào đây
      });
    }
    return undefined;
  }
}
