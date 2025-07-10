import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";

import { ApplicationConfigService } from "../config/application-config.service";
import { Logout } from "app/login/logout.model";

@Injectable({ providedIn: "root" })
export class AuthServerProvider {
  private tokenUrl = this.applicationConfigService.getEndpointFor("api/token");

  constructor(
    private http: HttpClient,
    private applicationConfigService: ApplicationConfigService,
  ) {}

  logout(): Observable<Logout> {
    return this.http.post<Logout>(
      this.applicationConfigService.getEndpointFor("api/logout"),
      {},
    );
  }
  public getToken(): Observable<string> {
    return this.http
      .get<{ access_token: string }>(this.tokenUrl)
      .pipe(map((response) => response.access_token));
  }
}
