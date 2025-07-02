import { Component, OnInit } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { Router, ActivatedRouteSnapshot, NavigationEnd } from "@angular/router";
import { Account } from "app/core/auth/account.model";

import { AccountService } from "app/core/auth/account.service";
import { LoginService } from "app/login/login.service";

@Component({
  selector: "jhi-main",
  templateUrl: "./main.component.html",
  styleUrls: ["./main.component.scss"],
  standalone: false,
})
export class MainComponent implements OnInit {
  inProduction?: boolean;
  isNavbarCollapsed = true;
  openAPIEnabled?: boolean;
  version = "";
  account: Account | null = null;
  entitiesNavbarItems: any[] = [];
  isSidebarCollapsed = false;

  constructor(
    private accountService: AccountService,
    private titleService: Title,
    private router: Router,
    private loginService: LoginService,
  ) {}

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateTitle();
      }
    });
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
  }

  login(): void {
    this.loginService.login();
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate([""]);
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  closeNav(): void {
    document.getElementById("main")!.style.marginLeft = "50px";
  }

  openNav(): void {
    document.getElementById("main")!.style.marginLeft = "250px";
  }

  private getPageTitle(routeSnapshot: ActivatedRouteSnapshot): string {
    const title: string = routeSnapshot.data["pageTitle"] ?? "";
    if (routeSnapshot.firstChild) {
      return this.getPageTitle(routeSnapshot.firstChild) || title;
    }
    return title;
  }

  private updateTitle(): void {
    let pageTitle = this.getPageTitle(this.router.routerState.snapshot.root);
    if (!pageTitle) {
      pageTitle = "JHipster";
    }
    this.titleService.setTitle(pageTitle);
  }
}
