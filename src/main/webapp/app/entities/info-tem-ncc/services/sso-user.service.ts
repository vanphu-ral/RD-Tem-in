import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { AuthServerProvider } from "app/core/auth/auth-session.service";

/** Thông tin gửi SAP của user đang đăng nhập — đọc từ claim trong access token */
export interface SapTokenProfile {
  /** Tài khoản SAP — claim "sapUser" ("" nếu chưa gán) */
  sapUser: string;
  /** Mã kho — claim "whsCode" ("" nếu chưa gán; 1 user 1 kho) */
  whsCode: string;
}

@Injectable({ providedIn: "root" })
export class SsoUserService {
  constructor(private authServer: AuthServerProvider) {}

  /** sapUser + whsCode của user đang đăng nhập (từ token, không gọi Keycloak Admin API) */
  getCurrentSapProfile(): Observable<SapTokenProfile> {
    return this.authServer.getToken().pipe(
      map((token) => {
        const claims = this.decodeToken(token);
        return {
          sapUser: claimText(claims["sapUser"]),
          whsCode: claimText(claims["whsCode"]),
        };
      }),
    );
  }

  /** Payload JWT (base64url, UTF-8) */
  private decodeToken(token: string): Record<string, unknown> {
    const part = token.split(".")[1];
    if (!part) {
      return {};
    }
    const json = decodeURIComponent(
      atob(part.replace(/-/g, "+").replace(/_/g, "/"))
        .split("")
        .map((c) => `%${c.charCodeAt(0).toString(16).padStart(2, "0")}`)
        .join(""),
    );
    return JSON.parse(json) as Record<string, unknown>;
  }
}

/** Claim dạng chuỗi hoặc mảng (lấy phần tử đầu) → chuỗi đã trim */
function claimText(claim: unknown): string {
  const value = Array.isArray(claim) ? (claim[0] as unknown) : claim;
  return typeof value === "string" ? value.trim() : "";
}
