export interface VendorQrMappingConfig {
  separator: string;
  fieldMappings: {
    position: number;
    nccFieldDesc: string;
    dataField: string;
  }[];
}

/** Map nhãn dataField trong kịch bản tem → key nội bộ. */
export function toVendorQrFieldKey(dataField: string): string {
  const mapping: Record<string, string> = {
    ReelID: "reelId",
    PartNumber: "partNumber",
    Lot: "lotNumber",
    Vendor: "vendor",
    InitialQuantity: "initialQuantity",
    UserData1: "userData1",
    UserData2: "userData2",
    UserData3: "userData3",
    UserData4: "userData4",
    UserData5: "userData5",
    MSDLevel: "msl",
    MSL: "msl",
    StorageUnit: "storageUnit",
    ManufacturingDate: "manufacturingDate",
    ExpirationDate: "expirationDate",
    QuantityOverride: "totalQty",
    "Mã ReelID": "reelId",
    "Mã Part number": "partNumber",
    "SAP Code": "storageUnit",
    "Initial quantity": "initialQuantity",
    "Quantity Override": "totalQty",
    "Storage Unit": "storageUnit",
    "Lot Number": "lotNumber",
  };
  return mapping[dataField] ?? dataField;
}

/** Field có được map trong kịch bản tem (không phải "Không lấy"). */
export function isVendorQrFieldMapped(
  mappingConfig: VendorQrMappingConfig | null | undefined,
  fieldKey: string,
): boolean {
  if (!mappingConfig?.fieldMappings?.length) {
    return false;
  }
  return mappingConfig.fieldMappings.some(
    (fm) =>
      !!fm.dataField &&
      fm.dataField !== "Không lấy" &&
      toVendorQrFieldKey(fm.dataField) === fieldKey,
  );
}

/** Chuẩn hoá ngày về yyyyMMdd (bỏ `-` / ký tự khác). Ví dụ: 2026-08-09 → 20260809. */
export function normalizeVendorDateToYyyyMmDd(
  value: string | null | undefined,
): string {
  const digits = String(value ?? "").replace(/\D/g, "");
  if (digits.length >= 8) {
    return digits.slice(0, 8);
  }
  return "";
}

/** Tách mã QR theo separator + fieldMappings của kịch bản tem đang chọn. */
export function parseVendorQrByMappingConfig(
  rawCode: string,
  mappingConfig: VendorQrMappingConfig | null | undefined,
): Record<string, string> {
  const fieldMap: Record<string, string> = {};
  if (!mappingConfig) {
    return fieldMap;
  }
  const separator = mappingConfig.separator ?? "|";
  const parts = rawCode.split(separator);
  mappingConfig.fieldMappings
    .filter((fm) => fm.dataField && fm.dataField !== "Không lấy")
    .forEach((fm) => {
      const key = toVendorQrFieldKey(fm.dataField);
      let value = parts[fm.position] ?? "";
      if (key === "manufacturingDate" || key === "expirationDate") {
        value = normalizeVendorDateToYyyyMmDd(value);
      }
      fieldMap[key] = value;
    });
  return fieldMap;
}
