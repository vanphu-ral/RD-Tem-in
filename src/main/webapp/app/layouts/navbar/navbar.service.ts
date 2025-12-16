import { Injectable } from "@angular/core";
import {
  faHome,
  faTags,
  faCalendarDay,
  faTable,
  faInfoCircle,
  faNoteSticky,
} from "@fortawesome/free-solid-svg-icons";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class NavbarService {
  // Public properties
  menus: any[] = [
    {
      title: "Trang chủ",
      type: "home",
      link: "/home",
    },
    {
      title: "Profile Info",
      active: false,
      icon: faInfoCircle,
      type: "dropdown",
      submenus: [
        { title: "Quản lý thông số", link: "/quan-ly-thong-so" },
        { title: "Quản lý thiết bị", link: "/thiet-bi" },
        { title: "Quản lý kịch bản", link: "/kich-ban" },
        { title: "Sản xuất hàng ngày", link: "/san-xuat-hang-ngay" },
      ],
    },
    {
      title: "Quản lý Mã vạch - đóng gói",
      active: false,
      icon: faNoteSticky,
      type: "dropdown",
      submenus: [
        { title: "Quản lý mã - vạch đóng gói", link: "/warehouse-note-infos" },
        { title: "Quản lý phê duyệt", link: "/warehouse-inbound-approvals" },
      ],
    },
    {
      title: "Tem In",
      icon: faTags,
      active: false,
      type: "dropdown",
      submenus: [
        { title: "Thông tin tem sản xuất", link: "/lenh-san-xuat" },
        { title: "Quản lý phê duyệt", link: "/chi-tiet-lenh-san-xuat" },
        { title: "Quản lý TEM vật tư", link: "/generate-tem-in" },
      ],
    },
    {
      title: "Kiểm tra barcode",
      active: false,
      icon: faCalendarDay,
      type: "dropdown",
      submenus: [
        { title: " Danh sách thiết bị", link: "/quan-ly-thiet-bi" },
        { title: " Quy trình đối chiếu", link: "/profile-check" },
        { title: " Đối chiếu lệnh SX", link: "/doi-chieu-lenh-san-xuat" },
      ],
    },
    {
      title: "Quản lý vật tư",
      active: false,
      icon: faTable,
      type: "dropdown",
      submenus: [
        { title: "Danh sách vật tư", link: "/list-material" },
        { title: "Quản lý phê duyệt", link: "/approve-material-update" },
        { title: "Lịch sử phê duyệt", link: "/approve-material-history" },
      ],
    },
    // {
    //   title: 'Profile Info',
    //   active: false,
    //   icon: 'perm_device_information',
    //   type: 'dropdown',
    //   submenus: [
    //     { title: ' Quản lý thông số', link: '/quan-ly-thong-so' },
    //     { title: ' Quản lý thiết bị', link: '/thiet-bi' },
    //     { title: ' Quản lý kịch bản', link: '/kich-ban' },
    //     { title: ' Sản xuất hàng ngày', link: '/san-xuat-hang-ngay' },
    //   ],
    // },
    // {
    //   title: 'Tem In',
    //   icon: 'local_activity',
    //   active: false,
    //   type: 'dropdown',
    //   submenus: [
    //     { title: ' Thông tin tem sản xuất', link: '/lenh-san-xuat' },
    //     { title: ' Quản lý phê duyệt', link: '/chi-tiet-lenh-san-xuat' },
    //   ],
    // },
    // {
    //   title: 'Kiểm tra barcode',
    //   active: false,
    //   icon: 'calendar_view_day',
    //   type: 'dropdown',
    // submenus: [
    //   { title: ' Danh sách thiết bị', link: '/quan-ly-thiet-bi' },
    //   { title: ' Quy trình đối chiếu', link: '/profile-check' },
    //   { title: ' Đối chiếu lệnh SX', link: '/doi-chieu-lenh-san-xuat' },
    // ],
    // },
    // {
    //   title: 'Quản lý vật tư',
    //   active: false,
    //   icon: ' table_chart',
    //   type: 'dropdown',
    //   submenus: [
    //     { title: ' Danh sách vật tư', link: '/list-material' },
    //     { title: ' Quản lý phê duyệt', link: '/approve-material-update' },
    //     { title: ' Lịch sử phê duyệt', link: '/approve-material-history' },
    //   ],
    // },
  ];
  // Private properties
  // Public observable, khởi tạo trong constructor
  public sidebarToggled$: Observable<boolean>;

  private sidebarToggled = new BehaviorSubject<boolean>(false);

  constructor() {
    this.sidebarToggled$ = this.sidebarToggled.asObservable();
  }
  // Public methods
  toggle(): void {
    this.sidebarToggled.next(!this.sidebarToggled.value);
  }

  getSidebarState(): boolean {
    return this.sidebarToggled.value;
  }

  setSidebarState(state: boolean): void {
    this.sidebarToggled.next(state);
  }

  getMenuList(): any[] {
    return this.menus;
  }
}
