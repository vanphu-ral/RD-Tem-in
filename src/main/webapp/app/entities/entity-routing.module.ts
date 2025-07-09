import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: "thiet-bi",
        data: { pageTitle: "Thiết bị" },
        loadChildren: () =>
          import("./thiet-bi/thiet-bi.module").then((m) => m.ThietBiModule),
      },
      {
        path: "thong-so-may",
        data: { pageTitle: "Thông số máy" },
        loadChildren: () =>
          import("./thong-so-may/thong-so-may.module").then(
            (m) => m.ThongSoMayModule,
          ),
      },
      {
        path: "san-xuat-hang-ngay",
        data: { pageTitle: "Sản xuất hàng ngày" },
        loadChildren: () =>
          import("./san-xuat-hang-ngay/san-xuat-hang-ngay.module").then(
            (m) => m.SanXuatHangNgayModule,
          ),
      },
      {
        path: "chi-tiet-san-xuat",
        data: { pageTitle: "Chi tiết sản xuất" },
        loadChildren: () =>
          import("./chi-tiet-san-xuat/chi-tiet-san-xuat.module").then(
            (m) => m.ChiTietSanXuatModule,
          ),
      },
      {
        path: "lich-su-update",
        data: { pageTitle: "LichSuUpdates" },
        loadChildren: () =>
          import("./lich-su-update/lich-su-update.module").then(
            (m) => m.LichSuUpdateModule,
          ),
      },
      {
        path: "chi-tiet-lich-su-update",
        data: { pageTitle: "ChiTietLichSuUpdates" },
        loadChildren: () =>
          import(
            "./chi-tiet-lich-su-update/chi-tiet-lich-su-update.module"
          ).then((m) => m.ChiTietLichSuUpdateModule),
      },
      {
        path: "kich-ban",
        data: { pageTitle: "Kịch bản" },
        loadChildren: () =>
          import("./kich-ban/kich-ban.module").then((m) => m.KichBanModule),
      },
      {
        path: "chi-tiet-kich-ban",
        data: { pageTitle: "Chi tiết kịch bản" },
        loadChildren: () =>
          import("./chi-tiet-kich-ban/chi-tiet-kich-ban.module").then(
            (m) => m.ChiTietKichBanModule,
          ),
      },
      {
        path: "quan-ly-thong-so",
        data: { pageTitle: "Quản lý thông số" },
        loadChildren: () =>
          import("./quan-ly-thong-so/quan-ly-thong-so.module").then(
            (m) => m.QuanLyThongSoModule,
          ),
      },
      {
        path: "lenh-san-xuat",
        data: { pageTitle: "Lệnh sản xuất" },
        loadChildren: () =>
          import("./lenh-san-xuat/lenh-san-xuat.module").then(
            (m) => m.LenhSanXuatModule,
          ),
      },
      {
        path: "chi-tiet-lenh-san-xuat",
        data: { pageTitle: "Chi tiết lệnh sản xuất" },
        loadChildren: () =>
          import("./chi-tiet-lenh-san-xuat/chi-tiet-lenh-san-xuat.module").then(
            (m) => m.ChiTietLenhSanXuatModule,
          ),
      },

      {
        path: "quan-ly-thiet-bi",
        data: { pageTitle: "Quản lý thiết bị" },
        loadChildren: () =>
          import("./quan-ly-thiet-bi/quan-ly-thiet-bi.module").then(
            (m) => m.QuanLyThietBiModule,
          ),
      },

      {
        path: "doi-chieu-lenh-san-xuat",
        data: { pageTitle: "Giám sát - đối chiếu lệnh sản xuất" },
        loadChildren: () =>
          import(
            "./doi-chieu-lenh-san-xuat/doi-chieu-lenh-san-xuat.module"
          ).then((m) => m.DoiChieuLenhSanXuatModule),
      },
      {
        path: "profile-check",
        data: { pageTitle: "Profile check" },
        loadChildren: () =>
          import("./profile-check/profile-check.module").then(
            (m) => m.ProfileCheckModule,
          ),
      },
      {
        path: "list-material",
        data: { pageTitle: "Danh sách vật tư" },
        loadChildren: () =>
          import("./list-material/list-material.module").then(
            (m) => m.ListMaterialModule,
          ),
      },
      {
        path: "approve-material-update",
        data: { pageTitle: "Quản lí đề nghị câp nhật" },
        loadChildren: () =>
          import(
            "./approve-material-update/approve-material-update.module"
          ).then((m) => m.ApproveMaterialUpdateModule),
      },
      {
        path: "approve-material-history",
        data: { pageTitle: "Lịch sử đề nghị câp nhật" },
        loadChildren: () =>
          import(
            "./approve-material-history/approve-material-history.module"
          ).then((m) => m.ApproveMaterialHistoryModule),
      },
      {
        path: "list-material/sumary",
        data: { pageTitle: "Tổng hợp" },
        loadChildren: () =>
          import("./list-material/sumary/list-material-sumary.module").then(
            (m) => m.ListMaterialSumaryModule,
          ),
      },
      // {
      //   path: 'scan-check',
      //   data: { pageTitle: 'Scan check' },
      //   loadChildren: () => import('./scan-check/scan-check.module').then(m => m.ScanCheckModule)
      // }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
