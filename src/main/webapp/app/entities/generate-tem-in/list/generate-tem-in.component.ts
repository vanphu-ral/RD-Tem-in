import { Component } from "@angular/core";
import {
  faFileImport,
  faEye,
  faEdit,
  faTrash,
  faPrint,
} from "@fortawesome/free-solid-svg-icons";
interface LabelData {
  code: string;
  productName: string;
  orderCode: string;
  qty: string;
  vendor: string;
  lot: string;
  msd: string;
  exp: string;
  rankM: string;
  rankA: string;
  rankQ: string;
  lo: string;
  rd: string;
  nv: string;
  po: string;
  note: string;
}
@Component({
  selector: "jhi-generate-tem-in",
  standalone: false,
  templateUrl: "./generate-tem-in.component.html",
  styleUrl: "./generate-tem-in.component.scss",
})
export class GenerateTemInComponent {
  faFileImport = faFileImport;
  faEye = faEye;
  faEdit = faEdit;
  faTrash = faTrash;
  faPrint = faPrint;
  data = [
    {
      status: "Đã TEM",
      supplierCode: "NNC001",
      poCode: "PO123456",
      createdDate: "25/06/2025",
      createdBy: "Nguyễn Văn A",
      goodsNumber: "GH001",
      totalQuantity: 1000,
      temNumber: "TEM001",
    },
    // thêm dữ liệu mẫu khác nếu cần
  ];

  currentPage = 2;
  totalPages = 67;
  pages = [1, 2, 3];

  labelData: LabelData = {
    code: "00060812",
    productName: "NFSW757GT-V3-65K",
    orderCode: "000202507210951Z1",
    qty: "5.000",
    vendor: "V900000008",
    lot: "P79D1M-sm6550bP11R9050",
    msd: "20250709",
    exp: "20260709",
    rankM: "sm6550b",
    rankA: "NO",
    rankQ: "P11",
    lo: "250717110031",
    rd: "RD",
    nv: "160725",
    po: "36244",
    note: "Trở dẫn-1.2 MΩm-0.25W-5%-1206 (Yankon)",
  };
  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
  handlePrint(): void {
    window.print();
  }
}
