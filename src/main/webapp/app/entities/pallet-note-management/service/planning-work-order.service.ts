import { Injectable } from "@angular/core";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "app/environments/environment";
import { SerialBoxPalletMapping } from "../scan-pallet-dialog/scan-pallet-dialog.component";

// Interface cho dữ liệu trả về từ API
export interface PlanningWorkOrder {
  id: string;
  sapWoId: string;
  productCode: string;
  productName: string;
  woId: string;
  bomVersion: string;
  quantityPlan: number;
  status: string;
  processStatus: string;
  customerName: string;
  lotNumber: string;
  productOrderId: string;
  note?: string;
  productType: string;
  branchCode: string;
  groupCode: string;
  groupName: string;
}
export interface WarehouseNotePayload {
  //tạo đơn chính
  id?: number;
  ma_lenh_san_xuat: string;
  sap_code: string;
  sap_name: string;
  work_order_code: string;
  version: string;
  storage_code: string;
  total_quantity: number;
  create_by: string;
  trang_thai: string;
  comment?: string;
  group_name?: string;
  comment_2?: string;
  approver_by?: string;
  branch?: string;
  lot_number?: string;
  product_type: string;
  destination_warehouse: number;
}
//ngành tổ
export interface WorkshopHierarchy {
  id: number;
  workshop_code: string;
  work_shop_name: string;
  description?: string;
  branchs: Branch[];
}

export interface Branch {
  id: number;
  workshop_code: string;
  branch_code: string;
  branch_name: string;
  production_teams: ProductionTeam[];
}

export interface ProductionTeam {
  id: number;
  branch_code: string;
  production_team_code: string;
  production_team_name: string;
}
// Interface cho response tổng thể
export interface PlanningWorkOrderResponse {
  content: PlanningWorkOrder[];
}
export interface Area {
  id: number;
  code: string;
  name: string;
  thuKho?: string | null;
  description?: string | null;
  address?: string | null;
  isActive: boolean;
  updatedBy?: string | null;
  updatedDate?: string; // ISO date string
}

@Injectable({
  providedIn: "root",
})
export class PlanningWorkOrderService {
  private baseUrl = environment.baseInTemApiUrl;
  private apiUrl = `${this.baseUrl}/item-data/planning-work-orders`;
  private WMSUrl = "https://ral-wms-logistic.rangdong.com.vn:9004/api";
  private apiCheckIdentifyWo = `${this.baseUrl}/warehouse-note-infos/work-order-code`;

  private testUrl = "http://192.168.68.77:9030/api";

  constructor(private http: HttpClient) {}

  // Lấy tất cả work order
  getAll(): Observable<PlanningWorkOrderResponse> {
    return this.http.get<PlanningWorkOrderResponse>(this.apiUrl);
  }
  //laays area
  getArea(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/location/area`);
  }

  //hoan thanh lenh san xuat
  completeWo(id: number, payload: any): Observable<any> {
    return this.http.patch(
      `${this.baseUrl}/warehouse-note-infos-approval/${id}`,
      payload,
    );
  }

  // Lọc theo sapWoId và productCode
  search(woId?: string): Observable<PlanningWorkOrderResponse> {
    return this.http.get<PlanningWorkOrderResponse>(`${this.apiUrl}/${woId}`);
  }

  //chi tiet
  findWarehouseNote(id: number): Observable<HttpResponse<any>> {
    return this.http.get<any>(`${this.baseUrl}/warehouse-note-infos/${id}`, {
      observe: "response",
    });
  }

  findWarehouseNoteWithChildren(
    maLenhSanXuatId: number,
  ): Observable<HttpResponse<any>> {
    return this.http.get<any>(
      `${this.baseUrl}/warehouse-note-infos/${maLenhSanXuatId}/with-children`,
      { observe: "response" },
    );
  }
  findWarehouseNoteApprovalWithChildren(
    maLenhSanXuatId: number,
  ): Observable<HttpResponse<any>> {
    return this.http.get<any>(
      `${this.baseUrl}/warehouse-note-infos-approval/${maLenhSanXuatId}/with-children`,
      { observe: "response" },
    );
  }
  //tạo đơn chính
  createWarehouseNote(payload: WarehouseNotePayload): Observable<any> {
    return this.http.post(`${this.baseUrl}/warehouse-note-infos`, payload);
  }

  updateWarehouseNote(
    id: number,
    payload: WarehouseNotePayload,
  ): Observable<any> {
    return this.http.patch(
      `${this.baseUrl}/warehouse-note-infos/${id}`,
      payload,
    );
  }

  //save combine pallet / box
  saveCombined(maLenhSanXuatId: number, payload: any): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/pallet-infor-details/combined/${maLenhSanXuatId}`,
      payload,
    );
  }

  //lấy mã ngành, tổ:
  getHierarchy(): Observable<WorkshopHierarchy[]> {
    return this.http.get<WorkshopHierarchy[]>(
      `${this.baseUrl}/workshops/hierarchy`,
    );
  }

  //mapping box/pallet
  sendMappingRequest(
    maLenhSanXuatId: number,
    serialBox: string,
    serialPallet: string,
    status: number,
  ): Observable<any> {
    const payload = {
      serial_box: serialBox,
      serial_pallet: serialPallet,
      status,
    };

    return this.http.post(
      `${this.baseUrl}/serial-box-pallet-mappings/ma-lenh-san-xuat/${maLenhSanXuatId}`,
      payload,
    );
  }

  //get mapping
  getMappings(serial_pallet: string): Observable<SerialBoxPalletMapping[]> {
    return this.http.get<SerialBoxPalletMapping[]>(
      `${this.baseUrl}/serial-box-pallet-mappings/serial-pallet/${serial_pallet}`,
    );
  }

  //get mappings by maLenhSanXuatId
  getMappingsByMaLenhSanXuatId(
    maLenhSanXuatId: number,
  ): Observable<SerialBoxPalletMapping[]> {
    return this.http.get<SerialBoxPalletMapping[]>(
      `${this.baseUrl}/serial-box-pallet-mappings/ma-lenh-san-xuat/${maLenhSanXuatId}`,
    );
  }

  //xoá box
  deleteBoxDetail(id: number): Observable<any> {
    return this.http.delete(
      `${this.baseUrl}/warehouse-note-info-details/${id}`,
    );
  }

  //xoá pallet
  deletePalletDetail(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/pallet-infor-details/${id}`);
  }

  // Xóa mapping theo serialBox + serialPallet + mã lệnh sản xuất
  removeMapping(
    serialBox: string,
    serialPallet: string,
    maLenhSanXuatId: number,
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/serial-box/${serialBox}/serial-pallet/${serialPallet}/ma-lenh-san-xuat/${maLenhSanXuatId}`,
    );
  }

  //send WMS
  //lay ma kho area
  getAreas(): Observable<Area[]> {
    return this.http.get<Area[]>(`${this.WMSUrl}/areas/`);
  }

  sendWmsApproval(payload: any): Observable<any> {
    return this.http.post<any>(
      `${this.WMSUrl}/import-requirements/wms`,
      payload,
    );
  }
  // Cập nhật trạng thái thùng wwms (mảng)
  updateStampDetails(
    payloadArray: { id: number; wms_send_status: boolean }[],
  ): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/warehouse-stamp-info-details`,
      payloadArray,
    );
  }

  //caap nhat trang thai in thung
  updatePrintBtpDetails(
    payloadArray: { id: number; print_status: boolean }[],
  ): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/warehouse-stamp-info-details`,
      payloadArray,
    );
  }
  //gui duyet btp
  sendBTPApproval(payload: any): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/warehouse-note-infos-approval`,
      payload,
    );
  }
  //check identify
  checkIdentifyWo(woId: string): Observable<any[]> {
    // endpoint trả mảng ([]) nếu chưa tồn tại, hoặc mảng có object nếu đã tồn tại
    return this.http.get<any[]>(
      `${this.apiCheckIdentifyWo}/${encodeURIComponent(woId)}`,
    );
  }

  //update status wms
  updatePalletWmsStatus(
    payload: Array<{
      id: string | number;
      wms_send_status: boolean;
      updated_by: string;
    }>,
  ): Observable<any> {
    return this.http.put(`${this.baseUrl}/pallet-infor-details`, payload);
  }

  //cập nhật trạng thái in
  updatePalletPrintStatus(
    payload: Array<{
      id: number;
      print_status: boolean;
      updated_by: string;
    }>,
  ): Observable<any> {
    const url = `${this.baseUrl}/pallet-infor-details`;
    return this.http.put(url, payload);
  }
}
