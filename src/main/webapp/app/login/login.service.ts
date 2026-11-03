import { Injectable } from "@angular/core";
import { Location } from "@angular/common";
import { Router } from "@angular/router";

import { AuthServerProvider } from "app/core/auth/auth-session.service";
import { StateStorageService } from "app/core/auth/state-storage.service";
import { Logout } from "./logout.model";
import { PendingMaterialService } from "app/entities/list-material/services/pending-material.service";
import { ListMaterialService } from "app/entities/list-material/services/list-material.service";

/** Đã kiểm tra phiên SSO trong tab này → không chuyển trang kiểm tra lại (tránh lặp) */
const SSO_CHECKED_KEY = "ssoChecked";

@Injectable({ providedIn: "root" })
export class LoginService {
  constructor(
    private location: Location,
    private authServerProvider: AuthServerProvider,
    private pendingMaterialService: PendingMaterialService,
    private materialService: ListMaterialService,
    private router: Router,
    private stateStorageService: StateStorageService,
  ) {}

  login(): void {
    // If you have configured multiple OIDC providers, then, you can update this URL to /login.
    // It will show a Spring Security generated login page with links to configured OIDC providers.
    const currentItems = this.materialService.getCurrentItems();
    this.pendingMaterialService.save(currentItems);
    location.href = `${location.origin}${this.location.prepareExternalUrl("oauth2/authorization/oidc")}`;
  }

  /**
   * Vào web chưa đăng nhập → kiểm tra ngầm phiên SSO 1 lần (BE gửi prompt=none):
   * còn phiên thì tự đăng nhập, chưa có thì BE trả về /?sso=none, ở lại trang với nút Đăng nhập.
   */
  checkSsoSession(loggedIn: boolean): void {
    if (loggedIn) {
      sessionStorage.removeItem(SSO_CHECKED_KEY);
      return;
    }
    if (new URLSearchParams(location.search).get("sso") === "none") {
      sessionStorage.setItem(SSO_CHECKED_KEY, "true");
      // Quay lại trang đang mở trước khi kiểm tra (trang cần đăng nhập sẽ tự mở form đăng nhập)
      const previousUrl = this.stateStorageService.getUrl();
      this.stateStorageService.clearUrl();
      void this.router.navigateByUrl(previousUrl ?? "/", { replaceUrl: true });
      return;
    }
    if (sessionStorage.getItem(SSO_CHECKED_KEY)) {
      return;
    }
    sessionStorage.setItem(SSO_CHECKED_KEY, "true");
    const currentUrl = `${location.pathname}${location.search}`;
    if (currentUrl !== "/" && !this.stateStorageService.getUrl()) {
      this.stateStorageService.storeUrl(currentUrl);
    }
    location.href = `${location.origin}${this.location.prepareExternalUrl("oauth2/authorization/oidc")}?silent=true`;
  }

  logout(): void {
    this.authServerProvider.logout().subscribe((logout: Logout) => {
      window.location.href = logout.logoutUrl;
    });
  }
}
