import { Injectable } from "@angular/core";
import { Location } from "@angular/common";

import { AuthServerProvider } from "app/core/auth/auth-session.service";
import { Logout } from "./logout.model";
import { PendingMaterialService } from "app/entities/list-material/services/pending-material.service";
import { ListMaterialService } from "app/entities/list-material/services/list-material.service";

@Injectable({ providedIn: "root" })
export class LoginService {
  constructor(
    private location: Location,
    private authServerProvider: AuthServerProvider,
    private pendingMaterialService: PendingMaterialService,
    private materialService: ListMaterialService,
  ) {}

  login(): void {
    // If you have configured multiple OIDC providers, then, you can update this URL to /login.
    // It will show a Spring Security generated login page with links to configured OIDC providers.
    const currentItems = this.materialService.getCurrentItems();
    this.pendingMaterialService.save(currentItems);
    location.href = `${location.origin}${this.location.prepareExternalUrl("oauth2/authorization/oidc")}`;
  }

  logout(): void {
    this.authServerProvider.logout().subscribe((logout: Logout) => {
      window.location.href = logout.logoutUrl;
    });
  }
}
