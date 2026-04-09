import { ILenhSanXuat } from "app/entities/lenh-san-xuat/lenh-san-xuat.model";
import { Injectable } from "@angular/core";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import dayjs from "dayjs/esm";

import { isPresent } from "app/core/util/operators";
import { ApplicationConfigService } from "app/core/config/application-config.service";
import { createRequestOption } from "app/core/request/request-util";
import { environment } from "app/environments/environment.development";

export interface WarehouseNoteInfo {
  id: number;
  ma_lenh_san_xuat: string | null;
  sap_code: string | null;
  sap_name: string | null;
  work_order_code: string | null;
  total_quantity: number;
  lot_number: string | null;
  trang_thai: string | null;
}

export interface ListBox {
  note: string | null;
  boxCode: string;
  quantity: number;
}

export interface InboundWMSPallet {
  id: number;
  inboundWMSSessionId: number;
  warehouseNoteInfoId: number;
  warehouseNoteInfo: WarehouseNoteInfo | null;
  serialPallet: string;
  wmsSendStatus: string | null;
  createdBy: string | null;
  createdAt: string | null;
  listBox: ListBox[];
}

export interface InboundWMSSession {
  id: number;
  status: string | null;
  note: string | null;
  createdBy: string | null;
  createdAt: string | null;
  wmsSentAt: string | null;
  inboundWMSPallets: InboundWMSPallet[];
  numberOfPallets?: number;
  numberOfBox?: number;
  totalQuantity?: number;
}

export interface CreateSessionPayload {
  status: string;
  createdBy: string;
  note: string;
  createdAt: string;
}

export interface ScanPalletPayload {
  inbound_wms_session_id: number;
  serial_pallet: string;
  scaned_by: string;
  scaned_at: string;
}
export interface ScanPalletResponse {
  warehouseNoteInfo: WarehouseNoteInfo;
  inboundpalletInfo: InboundWMSPallet;
}
export interface InboundWMSSessionPage {
  data: InboundWMSSession[];
  totalCount: number;
}
@Injectable({ providedIn: "root" })
export class ScanWMSService {
  private baseUrl = environment.baseInTemApiUrl; //http://192.168.10.99:8085/api
  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  //lay danh sach
  getSessions(
    page: number,
    size: number,
    filters?: {
      createdAt?: string | null;
      note?: string | null;
      status?: string | null;
    },
  ): Observable<HttpResponse<InboundWMSSession[]>> {
    const query: any = { page, size, sort: ["createdAt,desc"] };

    if (filters?.createdAt) {
      const start = new Date(filters.createdAt);
      start.setHours(0, 0, 0, 0);
      const end = new Date(filters.createdAt);
      end.setHours(23, 59, 59, 999);
      query["createdAt.greaterThanOrEqual"] = start.toISOString();
      query["createdAt.lessThanOrEqual"] = end.toISOString();
    }
    if (filters?.note) {
      query["note.contains"] = filters.note;
    }
    if (filters?.status) {
      query["status.equals"] = filters.status;
    }

    console.log("Filter query:", query);
    const params = createRequestOption(query);
    console.log("HttpParams:", params.toString());
    return this.http.get<InboundWMSSession[]>(
      `${this.baseUrl}/inbound-wms-sessions`,
      { params, observe: "response" },
    );
  }

  //lay thong tin chi tiet don
  getSessionById(id: number): Observable<HttpResponse<InboundWMSSession>> {
    return this.http.get<InboundWMSSession>(
      `${this.baseUrl}/inbound-wms-sessions/detail/${id}`,
      { observe: "response" },
    );
  }

  //tao moi don
  createSession(
    payload: CreateSessionPayload,
  ): Observable<HttpResponse<InboundWMSSession>> {
    return this.http.post<InboundWMSSession>(
      `${this.baseUrl}/inbound-wms-sessions`,
      payload,
      { observe: "response" },
    );
  }

  //luu  thong tin scan
  scanPallet(
    payload: ScanPalletPayload,
  ): Observable<HttpResponse<ScanPalletResponse>> {
    return this.http.post<ScanPalletResponse>(
      `${this.baseUrl}/inbound-wms-pallets/scan`,
      payload,
      { observe: "response" },
    );
  }

  // submit approval
  submitWarehouseEntryApproval(
    sessionId: number,
  ): Observable<HttpResponse<any>> {
    return this.http.post<any>(
      `${this.baseUrl}/inbound-wms-sessions/${sessionId}/submit-warehouse-entry-approval`,
      null,
      { observe: "response" },
    );
  }

  // cập nhật trạng thái tất cả pallet
  updatePalletStatuses(payload: {
    serialPallets: string[];
    wmsSendStatus: boolean;
    updatedBy: string;
  }): Observable<HttpResponse<any>> {
    return this.http.put<any>(
      `${this.baseUrl}/pallet-infor-details/update-wms-status`,
      payload,
      {
        observe: "response",
      },
    );
  }

  // cập nhật session
  updateSession(payload: {
    id: number;
    status: string;
    note: string | null;
    createdBy: string | null;
    createdAt: string | null;
    wmsSentAt: string;
  }): Observable<HttpResponse<InboundWMSSession>> {
    return this.http.put<InboundWMSSession>(
      `${this.baseUrl}/inbound-wms-sessions/${payload.id}`,
      payload,
      { observe: "response" },
    );
  }

  //xoa 1 du lieu scan
  deleteScanItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/inbound-wms-pallets/${id}`);
  }
}
