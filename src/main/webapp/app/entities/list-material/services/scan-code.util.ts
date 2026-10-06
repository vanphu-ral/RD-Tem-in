/**
 * Lấy ReelID (mã vật tư) từ chuỗi QR vừa scan. Hỗ trợ 2 kiểu mã:
 *  - "SAP;ReelID;..." : trường đầu (trước dấu ;) đúng 8 ký tự (mã SAP) → lấy trường thứ 2
 *  - còn lại (kiểu cũ "ReelID#Part#...") → lấy phần trước dấu # đầu tiên
 */
export function extractReelIdFromScan(raw: string | null | undefined): string {
  const value = (raw ?? "").trim();
  const parts = value.split(";");
  if (parts.length > 1 && parts[0].trim().length === 8) {
    return parts[1].trim();
  }
  return value.split("#")[0].trim();
}
