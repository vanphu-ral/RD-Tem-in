import { Component, OnInit } from "@angular/core";
import { LoginService } from "app/login/login.service";
import { AccountService } from "app/core/auth/account.service";
import { Account } from "app/core/auth/account.model";
import { NavbarService } from "app/layouts/navbar/navbar.service";
import { faChevronRight } from "@fortawesome/free-solid-svg-icons";

interface MenuItem {
  title: string;
  type: string;
  link?: string;
  icon?: any;
  active?: boolean;
  submenus?: SubMenuItem[];
}

interface SubMenuItem {
  title: string;
  link: string;
}

@Component({
  selector: "jhi-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.scss"],
  standalone: false,
})
export class HomeComponent implements OnInit {
  account: Account | null = null;
  menuList: MenuItem[] = [];
  faChevronRight = faChevronRight;

  private allowedMenuTitles = [
    // 'Profile Info',
    "Quản lý Mã vạch - đóng gói",
    "Quản lý vật tư",
    // 'Tem In',
  ];

  private linkOverrides: { [key: string]: string } = {
    "/list-material": "/list-material/update-list",
  };
  private hiddenSubmenuLinks: string[] = [
    "/warehouse-inbound-approvals",
    "/approve-material-history",
    "/approve-material-update",
  ];
  private titleOverrides: { [key: string]: string } = {
    // Replace title của menu
    // 'Tem In': 'Quản lý Tem In',
    // 'Quản lý vật tư': 'Danh sách vật tư',

    // Replace title của submenu
    // 'Thông tin tem sản xuất': 'Danh sách tem sản xuất',
    "Quản lý mã - vạch đóng gói": "Mã vạch đóng gói",
    "Danh sách vật tư": "Scan vật tư",
  };
  constructor(
    private accountService: AccountService,
    private loginService: LoginService,
    private navbarService: NavbarService,
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe((account) => {
      this.account = account;
    });

    this.loadMenuList();
  }

  login(): void {
    this.loginService.login();
  }

  private loadMenuList(): void {
    const allMenus = this.navbarService.getMenuList();

    this.menuList = allMenus
      .filter((menu) => this.allowedMenuTitles.includes(menu.title))
      .map((menu: MenuItem) => ({
        ...menu,
        title: this.titleOverrides[menu.title] || menu.title,
        submenus: menu.submenus
          ?.filter((submenu) => !this.hiddenSubmenuLinks.includes(submenu.link))
          .map((submenu: SubMenuItem) => ({
            ...submenu,
            title: this.titleOverrides[submenu.title] || submenu.title,
            link: this.linkOverrides[submenu.link] || submenu.link,
          })),
      }));
  }
}
