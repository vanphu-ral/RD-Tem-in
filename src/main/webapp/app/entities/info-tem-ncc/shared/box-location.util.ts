/**
 * Vị trí kho của thùng (vendor-label-info).
 *
 * Hiện tại: storageUnit = vị trí kho (vd T-05-01), subStorageUnit để trống.
 * Mã kho SAP (vd 02) là whsCode của dòng vật tư trong PO, không lưu theo thùng.
 *
 * Dữ liệu cũ: vị trí nằm ở subStorageUnit, storageUnit là mã kho → ưu tiên subStorageUnit
 * nếu còn giá trị. Ghi lại vị trí mới sẽ chuyển thùng sang cách lưu hiện tại.
 */
export function boxLocation(rec: {
  storageUnit?: string | null;
  subStorageUnit?: string | null;
}): string {
  return (rec.subStorageUnit ?? "").trim() || (rec.storageUnit ?? "").trim();
}

/** Trường gửi lên khi gán vị trí cho thùng */
export function locationFields(location: string | null | undefined): {
  storageUnit: string | null;
  subStorageUnit: null;
} {
  const value = (location ?? "").trim();
  return { storageUnit: value || null, subStorageUnit: null };
}
