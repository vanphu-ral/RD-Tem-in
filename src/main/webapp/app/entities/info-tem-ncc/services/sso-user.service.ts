import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError, map, shareReplay, switchMap, tap } from "rxjs/operators";
import { AuthServerProvider } from "app/core/auth/auth-session.service";
import { environment } from "app/environments/environment";

/** User SSO (Keycloak) */
export interface SsoUser {
  username: string;
  firstName?: string | null;
  lastName?: string | null;
  /** Tài khoản SAP — user attribute "sap_user" trên Keycloak */
  sapUser?: string | null;
}

/** User trả về từ Keycloak Admin API (briefRepresentation=false → có attributes) */
interface KeycloakUser {
  username: string;
  firstName?: string;
  lastName?: string;
  attributes?: Record<string, string[] | undefined>;
}

/** Thời gian giữ cache danh sách user */
const CACHE_TTL_MS = 10 * 60 * 1000;

/**
 * Danh sách toàn bộ user Keycloak (realm theo environment), cache dùng chung toàn app:
 * gọi Keycloak 1 lần, tự xóa cache sau 10 phút; lỗi → không cache (lần sau gọi lại).
 */
@Injectable({ providedIn: "root" })
export class SsoUserService {
  private cache$: Observable<SsoUser[]> | null = null;
  private clearTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private http: HttpClient,
    private authServer: AuthServerProvider,
  ) {}

  getAllUsers(): Observable<SsoUser[]> {
    if (!this.cache$) {
      this.cache$ = this.fetchAllUsers().pipe(
        tap(() => this.scheduleClear()),
        catchError((err: unknown) => {
          this.clearCache();
          return throwError(() => err);
        }),
        shareReplay(1),
      );
    }
    return this.cache$;
  }

  clearCache(): void {
    this.cache$ = null;
    if (this.clearTimer) {
      clearTimeout(this.clearTimer);
      this.clearTimer = null;
    }
  }

  private scheduleClear(): void {
    if (this.clearTimer) {
      clearTimeout(this.clearTimer);
    }
    this.clearTimer = setTimeout(() => this.clearCache(), CACHE_TTL_MS);
  }

  /** GET toàn bộ users (max=-1, kèm attributes để lấy sap_user) */
  private fetchAllUsers(): Observable<SsoUser[]> {
    const { serverUrl, realm } = environment.keycloak;
    return this.authServer.getToken().pipe(
      switchMap((token) =>
        this.http.get<KeycloakUser[]>(
          `${serverUrl}/admin/realms/${realm}/users`,
          {
            headers: new HttpHeaders().set("Authorization", `Bearer ${token}`),
            params: { max: -1, briefRepresentation: false },
          },
        ),
      ),
      map((list) =>
        list.map((u) => ({
          username: u.username,
          firstName: u.firstName,
          lastName: u.lastName,
          sapUser: u.attributes?.["sap_user"]?.[0] ?? null,
        })),
      ),
    );
  }
}
