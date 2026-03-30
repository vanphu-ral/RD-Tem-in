import { Injectable } from "@angular/core";

export interface StatusBadgeStyle {
  backgroundColor: string;
  color: string;
  border: string;
  borderRadius: string;
  padding: string;
  fontWeight: string;
  fontSize: string;
  display: string;
  whiteSpace: string;
}

@Injectable({ providedIn: "root" })
export class StatusBadgeService {
  getStyle(status: string): StatusBadgeStyle {
    switch ((status ?? "").toUpperCase().trim()) {
      case "NEW":
        return {
          backgroundColor: "#E3F2FD",
          color: "#1565C0",
          border: "1px solid #90CAF9",
          borderRadius: "12px",
          padding: "2px 10px",
          fontWeight: "600",
          fontSize: "12px",
          display: "inline-block",
          whiteSpace: "nowrap",
        };
      case "PENDING":
        return {
          backgroundColor: "#FFF8E1",
          color: "#E65100",
          border: "1px solid #FFCC02",
          borderRadius: "12px",
          padding: "2px 10px",
          fontWeight: "600",
          fontSize: "12px",
          display: "inline-block",
          whiteSpace: "nowrap",
        };
      case "APPROVE":
      case "APPROVED":
        return {
          backgroundColor: "#E8F5E9",
          color: "#1B5E20",
          border: "1px solid #A5D6A7",
          borderRadius: "12px",
          padding: "2px 10px",
          fontWeight: "600",
          fontSize: "12px",
          display: "inline-block",
          whiteSpace: "nowrap",
        };
      case "REJECT":
      case "REJECTED":
        return {
          backgroundColor: "#FFEBEE",
          color: "#B71C1C",
          border: "1px solid #EF9A9A",
          borderRadius: "12px",
          padding: "2px 10px",
          fontWeight: "600",
          fontSize: "12px",
          display: "inline-block",
          whiteSpace: "nowrap",
        };
      default:
        return {
          backgroundColor: "#F5F5F5",
          color: "#424242",
          border: "1px solid #E0E0E0",
          borderRadius: "12px",
          padding: "2px 10px",
          fontWeight: "600",
          fontSize: "12px",
          display: "inline-block",
          whiteSpace: "nowrap",
        };
    }
  }

  getLabel(status: string): string {
    switch ((status ?? "").toUpperCase().trim()) {
      case "NEW":
        return "Mới";
      case "PENDING":
        return "Chờ duyệt";
      case "APPROVE":
      case "APPROVED":
        return "Đã duyệt";
      case "REJECT":
      case "REJECTED":
        return "Từ chối";
      default:
        return status ?? "";
    }
  }
}
