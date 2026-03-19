import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "app/environments/environment.development";

export interface RdMaterialAttribute {
  id: string;
  attributes: string;
  [key: string]: any;
}

export interface TemIdentificationScenarioPayload {
  vendorCode: string;
  vendorName: string;
  mappingConfig: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface TemScenarioResponse {
  id: number;
  vendorCode: string;
  vendorName: string;
  mappingConfig: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

@Injectable({
  providedIn: "root",
})
export class ManagerTemNccService {
  private baseUrl = environment.testApiUrl;

  constructor(private http: HttpClient) {}

  //kich ban tem
  getMaterialAttributes(): Observable<RdMaterialAttribute[]> {
    return this.http.get<RdMaterialAttribute[]>(
      `${this.baseUrl}/rd-material-attributes`,
    );
  }

  createTemIdentificationScenario(
    payload: TemIdentificationScenarioPayload,
  ): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/tem-identification-scenarios`,
      payload,
    );
  }

  deleteTemIdentificationScenario(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/tem-identification-scenarios/${id}`,
    );
  }

  //lay danh sach kich ban
  getTemIdentificationScenarios(): Observable<TemScenarioResponse[]> {
    return this.http.get<TemScenarioResponse[]>(
      `${this.baseUrl}/tem-identification-scenarios`,
    );
  }
}
