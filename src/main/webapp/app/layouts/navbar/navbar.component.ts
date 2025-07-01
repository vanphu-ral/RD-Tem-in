import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { VERSION } from 'app/app.constants';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { EntityNavbarItems } from 'app/entities/entity-navbar-items';
import { MainComponent } from '../main/main.component';
import { NavbarService } from './navbar.service';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: false,
  animations: [
    trigger('slide', [
      state('up', style({ height: 0, overflow: 'hidden' })),
      state('down', style({ height: '*', overflow: 'hidden' })),
      transition('up  <=> down', animate('200ms ease-in-out')),
    ]),
  ],
})
export class NavbarComponent implements OnInit {
  inProduction?: boolean;
  isNavbarCollapsed = true;
  openAPIEnabled?: boolean;
  version = '';
  account: Account | null = null;
  entitiesNavbarItems: any[] = [];
  isOpenMenu = false;
  showLogo = 'false';
  menus: any[] = [];
  isAccountOpen = false;
  accountMenu = {
    type: 'dropdown',
    active: this.isAccountOpen,
    title: 'Tài khoản',
    icon: 'account_box',
    submenus: [],
  };

  public isSidebarCollapsed: boolean = false;
  @Input() isCollapsed: boolean = false;

  constructor(
    private loginService: LoginService,
    private accountService: AccountService,
    private profileService: ProfileService,
    private router: Router,
    private mainComponent: MainComponent,
    public navbarservice: NavbarService,
  ) {
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`;
    }
  }

  ngOnInit(): void {
    this.navbarservice.setSidebarState(true);
    this.entitiesNavbarItems = EntityNavbarItems;
    this.menus = this.navbarservice.getMenuList();
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
      this.openAPIEnabled = profileInfo.openAPIEnabled;
    });

    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }
  getSideBarState(): Observable<boolean> {
    return of(this.navbarservice.getSidebarState());
  }
  toggle(currentMenu: any): void {
    if (currentMenu.type === 'dropdown') {
      this.menus.forEach(element => {
        if (element === currentMenu) {
          currentMenu.active = !currentMenu.active;
        } else {
          element.active = false;
        }
      });
    }
  }

  getState(currentMenu: any): string {
    if (currentMenu.active) {
      return 'down';
    } else {
      return 'up';
    }
  }
  toggleNavbar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
    if (this.isSidebarCollapsed) {
      // Optionally, collapse all active submenus when the sidebar collapses
      this.menus.forEach(menu => {
        if (menu.type === 'dropdown') {
          menu.active = false;
        }
      });
    }
  }
  toggleDropDown(): void {
    this.isAccountOpen = !this.isAccountOpen;
    this.accountMenu.active = this.isAccountOpen;
  }
  toggleDropDownProfile(): void {
    const ul = document.querySelector('.nav-item-profile.dropdown ul') as HTMLElement;

    const displayValue = ul.style.display;
    if (displayValue === 'block') {
      ul.style.display = 'none';
    } else {
      ul.style.display = 'block';
    }
  }

  toggleDropDownStamp(): void {
    const ul = document.querySelector('.nav-item-stamp.dropdown ul') as HTMLElement;

    const displayValue = ul.style.display;
    if (displayValue === 'block') {
      ul.style.display = 'none';
    } else {
      ul.style.display = 'block';
    }
  }

  toggleDropDownStamp2(): void {
    const ul = document.querySelector('.nav-item-stamp2.dropdown ul') as HTMLElement;

    const displayValue = ul.style.display;
    if (displayValue === 'block') {
      ul.style.display = 'none';
    } else {
      ul.style.display = 'block';
    }
  }
  toggleDropDownMaterial(): void {
    const ul = document.querySelector('.nav-item-material.dropdown ul') as HTMLElement;

    const displayValue = ul.style.display;
    if (displayValue === 'block') {
      ul.style.display = 'none';
    } else {
      ul.style.display = 'block';
    }
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
    document.getElementById('sidebar-id')!.style.width = '60px';
    this.mainComponent.closeNav();
  }

  login(): void {
    this.loginService.login();
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }
  closeAccountDropdown(): void {
    this.isAccountOpen = false;
  }

  toggleSidebar(): void {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
    if (this.isNavbarCollapsed === true) {
      document.getElementById('sidebar-id')!.style.width = '60px';
      this.mainComponent.closeNav();
    } else {
      document.getElementById('sidebar-id')!.style.width = '250px';
      this.mainComponent.openNav();
    }
  }
}
