import {
  AggregateEditRow,
  scannedItemToEditRow,
} from "./scan-item-columns.util";
import { ScannedItem } from "../scan-item-dialog/scan-item-dialog.component";

/** Trường cascade giữa lớp 1 → 2 → 3 (flatten xuống reel). */
export type AggregateCascadeField =
  | "locationOverride"
  | "storageUnit"
  | "manufacturingDate"
  | "expirationDate";

export const AGGREGATE_CASCADE_FIELDS: AggregateCascadeField[] = [
  "locationOverride",
  "storageUnit",
  "manufacturingDate",
  "expirationDate",
];

export interface AggregateLotGroup {
  key: string;
  lotNumber: string;
  poCode: string;
  vendor: string;
  rows: AggregateEditRow[];
  /** Highlight khi tìm ReelID khớp. */
  highlighted: boolean;
}

export interface AggregateMaterialGroup {
  key: string;
  sapCode: string;
  partNumber: string;
  itemName: string;
  msl: string;
  lots: AggregateLotGroup[];
  autoFillDefaults: boolean;
  expanded: boolean;
}

export function effectivePoOfRow(
  row: AggregateEditRow,
  dialogPoCode?: string,
): string {
  return (
    String(row.userData5 ?? "").trim() || String(dialogPoCode ?? "").trim()
  );
}

export function buildAggregateTree(
  items: ScannedItem[],
  options: { collapseByDefault: boolean; dialogPoCode?: string },
): AggregateMaterialGroup[] {
  const materialMap = new Map<string, AggregateMaterialGroup>();

  items.forEach((item) => {
    const row = scannedItemToEditRow(item);
    const sap = String(row.sapCode ?? "").trim();
    const part = String(row.partNumber ?? "").trim();
    const materialKey = `m:${sap || "_"}|${part || `id-${item.id}`}`;

    let material = materialMap.get(materialKey);
    if (!material) {
      material = {
        key: materialKey,
        sapCode: sap,
        partNumber: part,
        itemName: String(row.itemName ?? "").trim(),
        msl: String(row.msl ?? "").trim(),
        lots: [],
        autoFillDefaults: false,
        expanded: !options.collapseByDefault,
      };
      materialMap.set(materialKey, material);
    } else if (!material.itemName && row.itemName) {
      material.itemName = String(row.itemName).trim();
    } else if (!material.msl && row.msl) {
      material.msl = String(row.msl).trim();
    }

    const lotNumber = String(row.lotNumber ?? "").trim();
    const poCode = effectivePoOfRow(row, options.dialogPoCode);
    const lotKey = `l:${lotNumber || "_"}|${poCode || "_"}`;

    let lot = material.lots.find((l) => l.key === lotKey);
    if (!lot) {
      lot = {
        key: lotKey,
        lotNumber,
        poCode,
        vendor: String(row.vendor ?? "").trim(),
        rows: [],
        highlighted: false,
      };
      material.lots.push(lot);
    } else if (!lot.vendor && row.vendor) {
      lot.vendor = String(row.vendor).trim();
    }
    lot.rows.push(row);
  });

  return Array.from(materialMap.values());
}

export function flattenTreeRows(
  materials: AggregateMaterialGroup[],
): AggregateEditRow[] {
  const rows: AggregateEditRow[] = [];
  materials.forEach((m) => {
    m.lots.forEach((lot) => {
      lot.rows.forEach((row) => rows.push(row));
    });
  });
  return rows;
}

export function sumQty(rows: AggregateEditRow[]): number {
  return rows.reduce((sum, r) => sum + (Number(r.initialQuantity) || 0), 0);
}

export function materialRows(
  material: AggregateMaterialGroup,
): AggregateEditRow[] {
  return flattenTreeRows([material]);
}

/** Giá trị đồng nhất của field trên các row; null nếu lệch / rỗng hết. */
export function uniformFieldValue(
  rows: AggregateEditRow[],
  field: string,
): string | null {
  if (!rows.length) {
    return null;
  }
  const first = String(rows[0][field] ?? "").trim();
  for (let i = 1; i < rows.length; i++) {
    if (String(rows[i][field] ?? "").trim() !== first) {
      return null;
    }
  }
  return first;
}

export function isFieldMixed(rows: AggregateEditRow[], field: string): boolean {
  if (rows.length < 2) {
    return false;
  }
  const first = String(rows[0][field] ?? "").trim();
  return rows.some((r) => String(r[field] ?? "").trim() !== first);
}

export function writeFieldToRows(
  rows: AggregateEditRow[],
  field: string,
  value: string | number,
): void {
  rows.forEach((row) => {
    if (field === "initialQuantity") {
      const n = Number(value);
      row.initialQuantity = Number.isFinite(n) ? n : 0;
    } else {
      row[field] = value;
    }
  });
}

/** Phiếu chỉ có một PO hiệu lực trên mọi reel. */
export function isSinglePoMode(
  materials: AggregateMaterialGroup[],
  dialogPoCode?: string,
): boolean {
  const set = new Set<string>();
  flattenTreeRows(materials).forEach((row) => {
    const po = effectivePoOfRow(row, dialogPoCode);
    if (po) {
      set.add(po);
    }
  });
  if (set.size === 0 && dialogPoCode) {
    return true;
  }
  return set.size <= 1;
}
