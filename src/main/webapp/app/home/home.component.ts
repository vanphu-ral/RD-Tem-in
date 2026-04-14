import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import {
  faHome,
  faTags,
  faCalendarDay,
  faTable,
  faInfoCircle,
  faNoteSticky,
  IconDefinition,
} from "@fortawesome/free-solid-svg-icons";

import { LoginService } from "app/login/login.service";
import { AccountService } from "app/core/auth/account.service";
import { Account } from "app/core/auth/account.model";

export interface MenuGroupItem {
  title: string;
  link: string;
}

export interface MenuGroup {
  title: string;
  icon: IconDefinition;
  submenus: MenuGroupItem[];
}

@Component({
  selector: "jhi-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.scss"],
  standalone: false,
})
export class HomeComponent implements OnInit {
  account: Account | null = null;

  menuGroups: MenuGroup[] = [
    {
      title: "Profile Info",
      icon: faInfoCircle,
      submenus: [
        { title: "Quản lý thông số", link: "/quan-ly-thong-so" },
        { title: "Quản lý thiết bị", link: "/thiet-bi" },
        { title: "Quản lý kịch bản", link: "/kich-ban" },
        { title: "Sản xuất hàng ngày", link: "/san-xuat-hang-ngay" },
      ],
    },
    {
      title: "Quản lý Mã vạch - Đóng gói",
      icon: faNoteSticky,
      submenus: [
        { title: "Quản lý mã vạch - đóng gói", link: "/warehouse-note-infos" },
        { title: "Quản lý phê duyệt", link: "/warehouse-inbound-approvals" },
        { title: "Scan nhập WMS", link: "/scan-to-wms" },
      ],
    },
    {
      title: "Tem In",
      icon: faTags,
      submenus: [
        // { title: "Thông tin tem sản xuất", link: "/lenh-san-xuat" },
        // { title: "Quản lý phê duyệt", link: "/chi-tiet-lenh-san-xuat" },
        { title: "Quản lý TEM vật tư", link: "/generate-tem-in" },
        { title: "Nhập thông tin TEM NCC", link: "/info-tem-ncc" },
        { title: "Cấu hình TEM NCC", link: "/info-tem-ncc/config-tem-ncc" },
        { title: "Phê duyệt TEM NCC", link: "/approve-tem-ncc" },
      ],
    },
    {
      title: "Kiểm tra Barcode",
      icon: faCalendarDay,
      submenus: [
        { title: "Danh sách thiết bị", link: "/quan-ly-thiet-bi" },
        { title: "Quy trình đối chiếu", link: "/profile-check" },
        { title: "Đối chiếu lệnh SX", link: "/doi-chieu-lenh-san-xuat" },
      ],
    },
    {
      title: "Quản lý Vật tư",
      icon: faTable,
      submenus: [
        { title: "Danh sách vật tư", link: "/list-material" },
        { title: "Quản lý phê duyệt", link: "/approve-material-update" },
        { title: "Lịch sử phê duyệt", link: "/approve-material-history" },
      ],
    },
  ];

  constructor(
    private accountService: AccountService,
    private loginService: LoginService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe((account) => {
      this.account = account;
    });
  }

  login(): void {
    this.loginService.login();
  }
}
