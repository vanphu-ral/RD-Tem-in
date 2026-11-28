/** Trạm tiếp nhận xe — mã vị trí + mô tả (Location_Description, bảng location PanaCIMMC) */
export interface ReceivingStation {
  code: string;
  name: string;
  /** Tên rút gọn — hiển thị ở autocomplete / ô nhập */
  shortName: string;
}

export const RECEIVING_STATIONS: ReceivingStation[] = [
  {
    code: "T-01-01",
    name: "Kho tạm 1 KV ngoài đường trục điện tử",
    shortName: "Khu vực trục điện tử",
  },
  {
    code: "T-02-01",
    name: "Kho tạm 2 KV ngoài đường trục bình GAS",
    shortName: "Khu vực trục bình GAS",
  },
  {
    code: "T-02-02",
    name: "Kho tạm 2 KV ngoài đường trục bình GAS",
    shortName: "Khu vực trục bình GAS",
  },
  {
    code: "T-03-01",
    name: "Kho tạm 3 KV ngoài đường trục XK",
    shortName: "Khu vực trục XK",
  },
  {
    code: "T-03-02",
    name: "Kho tạm 3 KV ngoài đường trục XK",
    shortName: "Khu vực trục XK",
  },
  {
    code: "T-04-01",
    name: "Kho tạm 4 KV để tôn thép trục nhà cân, LR1",
    shortName: "Khu vực tôn thép nhà cân, LR1",
  },
  {
    code: "T-05-01",
    name: "Kho tạm 5 KV ngoài đường trục LR1",
    shortName: "Khu vực trục LR1",
  },
  {
    code: "T-05-02",
    name: "Kho tạm 5 KV ngoài đường trục LR1",
    shortName: "Khu vực trục LR1",
  },
  {
    code: "T-05-03",
    name: "Kho tạm 5 KV ngoài đường trục LR1",
    shortName: "Khu vực trục LR1",
  },
  {
    code: "T-05-04",
    name: "Kho tạm 5 KV ngoài đường trục LR1",
    shortName: "Khu vực trục LR1",
  },
  {
    code: "T-06-01",
    name: "Kho tạm 6 KV ngoài đường trục nhà cân",
    shortName: "Khu vực trục nhà cân",
  },
  {
    code: "T-06-02",
    name: "Kho tạm 6 KV ngoài đường trục nhà cân",
    shortName: "Khu vực trục nhà cân",
  },
  {
    code: "T-07-01",
    name: "Kho tạm 7 KV ngoài đường trục sân bóng",
    shortName: "Khu vực trục sân bóng",
  },
  {
    code: "T-07-02",
    name: "Kho tạm 7 KV ngoài đường trục sân bóng",
    shortName: "Khu vực trục sân bóng",
  },
  {
    code: "T-08-01",
    name: "Kho tạm 8 KV T3 đèn tròn",
    shortName: "Khu vực T3 đèn tròn",
  },
];

/** Lọc trạm theo mã, tên đầy đủ hoặc tên rút gọn (trống → tất cả) */
export function searchReceivingStations(term: string): ReceivingStation[] {
  const keyword = (term ?? "").trim().toLowerCase();
  if (!keyword) {
    return RECEIVING_STATIONS;
  }
  return RECEIVING_STATIONS.filter(
    (s) =>
      s.code.toLowerCase().includes(keyword) ||
      s.name.toLowerCase().includes(keyword) ||
      s.shortName.toLowerCase().includes(keyword),
  );
}

/** "T-01-01 · Khu vực trục điện tử" — mã không có trong danh sách thì hiện nguyên mã */
export function receivingStationLabel(code: string | null | undefined): string {
  const value = (code ?? "").trim();
  const found = RECEIVING_STATIONS.find((s) => s.code === value);
  return found ? `${found.code} · ${found.shortName}` : value;
}
