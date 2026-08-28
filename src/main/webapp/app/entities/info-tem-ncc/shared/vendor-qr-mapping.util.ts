export interface VendorQrFieldMapping {
  position: number;
  nccFieldDesc: string;
  dataField: string;
  /**
   * Trường dữ liệu thứ hai được cắt ra từ chính chuỗi của dòng này
   * (vd: Part number nằm bên trong chuỗi ReelID). Không chiếm đoạn riêng trong QR.
   */
  derivedDataField?: string | null;
  /** Ký tự bắt đầu cắt cho derivedDataField, đếm từ 1; lấy đến hết chuỗi. */
  derivedStartIndex?: number | null;
  /**
   * @deprecated Cấu hình cũ: dòng riêng lấy giá trị cắt từ segment khác.
   * Vẫn đọc được để không vỡ kịch bản đã lưu.
   */
  sourcePosition?: number | null;
  /** @deprecated Đi kèm sourcePosition. */
  startIndex?: number | null;
}

export interface VendorQrMappingConfig {
  separator: string;
  fieldMappings: VendorQrFieldMapping[];
}

/** Dòng cấu hình kiểu cũ: bản thân dòng lấy giá trị cắt từ segment khác. */
export function isLegacyDerivedVendorQrField(
  fm: VendorQrFieldMapping | null | undefined,
): boolean {
  return fm?.sourcePosition !== undefined && fm?.sourcePosition !== null;
}

/** Dòng có cấu hình cắt thêm một trường từ chính chuỗi của nó. */
export function hasVendorQrDerivedField(
  fm: VendorQrFieldMapping | null | undefined,
): boolean {
  return !!fm?.derivedDataField && fm.derivedDataField !== "Không lấy";
}

function sliceFrom(
  source: string,
  startIndex: number | null | undefined,
): string {
  return source.slice(Math.max(1, startIndex ?? 1) - 1);
}

/** Giá trị thô của một dòng cấu hình. */
export function extractVendorQrRawValue(
  parts: string[],
  fm: VendorQrFieldMapping,
): string {
  if (isLegacyDerivedVendorQrField(fm)) {
    return sliceFrom(parts[fm.sourcePosition!] ?? "", fm.startIndex);
  }
  return parts[fm.position] ?? "";
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
      (!!fm.dataField &&
        fm.dataField !== "Không lấy" &&
        toVendorQrFieldKey(fm.dataField) === fieldKey) ||
      (hasVendorQrDerivedField(fm) &&
        toVendorQrFieldKey(fm.derivedDataField!) === fieldKey),
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

  const assign = (dataField: string, rawValue: string): void => {
    const key = toVendorQrFieldKey(dataField);
    fieldMap[key] =
      key === "manufacturingDate" || key === "expirationDate"
        ? normalizeVendorDateToYyyyMmDd(rawValue)
        : rawValue;
  };

  mappingConfig.fieldMappings.forEach((fm) => {
    if (fm.dataField && fm.dataField !== "Không lấy") {
      assign(fm.dataField, extractVendorQrRawValue(parts, fm));
    }
    if (hasVendorQrDerivedField(fm)) {
      assign(
        fm.derivedDataField!,
        sliceFrom(parts[fm.position] ?? "", fm.derivedStartIndex),
      );
    }
  });
  return fieldMap;
}
