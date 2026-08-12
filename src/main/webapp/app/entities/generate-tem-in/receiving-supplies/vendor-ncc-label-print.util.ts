import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { VendorImportedReelEntry } from "./vendor-reel-log-import.util";
import { VendorPrintSourceExportInput } from "./vendor-print-source-export.util";

/** Khổ tem NCC: 40×100mm (ngang), 100×100mm, A5. */
export type VendorNccPaperSize = "40x100" | "100x100" | "A5";

export type VendorNccHandlingIconId =
  | "handle_with_care"
  | "this_side_up"
  | "fragile"
  | "keep_dry";

export interface VendorNccHandlingIconOption {
  id: VendorNccHandlingIconId;
  label: string;
  matIcon: string;
}

export interface VendorNccLabelData {
  id: string;
  reelId: string;
  qrCode: string;
  sapCode: string;
  partNumber: string;
  /** Tên vật tư / Vendor item name */
  vendorItemName: string;
  quantity: number | string;
  /** Weight — Gross/Net Weigh */
  weight: string;
  meas: string;
  poNo: string;
  invoiceNo: string;
  boxNo: string;
  vendorName: string;
  madeIn: string;
  color: string;
  operator: string;
  /** Giữ field cũ để tương thích chỗ khác nếu còn dùng */
  grossNetWeight: string;
  contractNo: string;
  batchNo: string;
  mfgDate: string;
}

export interface VendorNccTemDetailInput {
  reelId: string;
  qrCode: string;
  sapCode: string;
  partNumber: string;
  productOfRequestId?: number | null;
  lot?: string;
  initialQuantity?: number | null;
  userData5?: string;
  manufacturingDate?: string;
}

export interface VendorNccImportedReelGroup {
  sourceRowIndex: number;
  reels: VendorImportedReelEntry[];
}

export interface VendorNccPrintBuildInput {
  sourceRows: VendorPrintSourceExportInput[];
  temDetails: VendorNccTemDetailInput[];
  importedReelGroups?: VendorNccImportedReelGroup[];
}

export const VENDOR_NCC_HANDLING_ICONS: VendorNccHandlingIconOption[] = [
  { id: "handle_with_care", label: "Handle with care", matIcon: "back_hand" },
  { id: "this_side_up", label: "This side up", matIcon: "vertical_align_top" },
  { id: "fragile", label: "Fragile", matIcon: "liquor" },
  { id: "keep_dry", label: "Keep dry", matIcon: "umbrella" },
];

export const DEFAULT_VENDOR_NCC_HANDLING_ICONS: VendorNccHandlingIconId[] = [
  "handle_with_care",
  "this_side_up",
  "fragile",
  "keep_dry",
];

export const VENDOR_NCC_SHIPPING_ICONS_SRC =
  "/content/images/shipping-icons.png";

const PAPER_SIZE_STORAGE_KEY = "vendor-ncc-print-paper-size";

export function loadVendorNccPaperSizePreference(): VendorNccPaperSize {
  const stored = localStorage.getItem(PAPER_SIZE_STORAGE_KEY);
  if (stored === "40x100" || stored === "100x100" || stored === "A5") {
    return stored;
  }
  // Migrate cũ A4 → A5
  return "A5";
}

export function saveVendorNccPaperSizePreference(
  size: VendorNccPaperSize,
): void {
  localStorage.setItem(PAPER_SIZE_STORAGE_KEY, size);
}

export function getVendorNccPaperDimensions(paperSize: VendorNccPaperSize): {
  widthMm: number;
  heightMm: number;
  orientation: "landscape" | "portrait";
} {
  switch (paperSize) {
    case "40x100":
      return { widthMm: 100, heightMm: 40, orientation: "landscape" };
    case "100x100":
      return { widthMm: 100, heightMm: 100, orientation: "portrait" };
    case "A5":
    default:
      // A5 ngang (landscape)
      return { widthMm: 210, heightMm: 148, orientation: "landscape" };
  }
}

function normalizeDigits(value: string | null | undefined): string {
  return (value ?? "").replace(/\D/g, "");
}

function formatMfgDate(value: string | null | undefined): string {
  const digits = normalizeDigits(value);
  if (digits.length === 8) {
    return digits;
  }
  if (!value) {
    return "";
  }
  const match = value.match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (match) {
    return `${match[1]}${match[2]}${match[3]}`;
  }
  return value.trim();
}

function buildContractNo(po: string, contractNo: string): string {
  const trimmed = contractNo.trim();
  if (trimmed) {
    return trimmed;
  }
  const poTrimmed = po.trim();
  if (!poTrimmed) {
    return "";
  }
  return `RD-JK26-${poTrimmed}`;
}

function toLabelFromSource(
  source: VendorPrintSourceExportInput,
  overrides: Partial<VendorNccLabelData> & { id: string },
): VendorNccLabelData {
  const po = (overrides.poNo ?? source.po).trim();
  const weight = (
    overrides.weight ??
    overrides.grossNetWeight ??
    source.grossNetWeight ??
    ""
  ).trim();
  return {
    id: overrides.id,
    reelId: overrides.reelId ?? "",
    qrCode: overrides.qrCode ?? "",
    sapCode: (overrides.sapCode ?? source.sapCode).trim(),
    partNumber: (overrides.partNumber ?? source.partNumber).trim(),
    vendorItemName: (
      overrides.vendorItemName ??
      source.productName ??
      ""
    ).trim(),
    quantity: overrides.quantity ?? source.quantity ?? "",
    weight,
    grossNetWeight: weight,
    meas: overrides.meas ?? source.meas ?? "",
    poNo: po,
    contractNo:
      overrides.contractNo ?? buildContractNo(po, source.contractNo ?? ""),
    invoiceNo: overrides.invoiceNo ?? source.invoiceNo ?? "",
    batchNo: (overrides.batchNo ?? source.lotBatchNo).trim(),
    mfgDate: overrides.mfgDate ?? formatMfgDate(source.mfgDate),
    boxNo: overrides.boxNo ?? "1 of 1",
    madeIn: (overrides.madeIn ?? source.madeIn ?? "").trim(),
    vendorName: (overrides.vendorName ?? source.vendorName ?? "").trim(),
    color: overrides.color ?? "",
    operator: overrides.operator ?? "",
  };
}

function findSourceRowIndex(
  sourceRows: VendorPrintSourceExportInput[],
  sapCode: string,
  lot?: string,
): number {
  const sap = sapCode.trim();
  const lotTrimmed = (lot ?? "").trim();
  const exact = sourceRows.findIndex(
    (row) =>
      row.sapCode.trim() === sap &&
      (lotTrimmed ? row.lotBatchNo.trim() === lotTrimmed : true),
  );
  if (exact >= 0) {
    return exact;
  }
  return sourceRows.findIndex((row) => row.sapCode.trim() === sap);
}

function buildFromTemDetails(
  input: VendorNccPrintBuildInput,
): VendorNccLabelData[] {
  const grouped = new Map<string, VendorNccTemDetailInput[]>();
  input.temDetails.forEach((tem) => {
    const key =
      tem.productOfRequestId != null
        ? `p:${tem.productOfRequestId}`
        : `s:${tem.sapCode?.trim() ?? ""}|l:${tem.lot?.trim() ?? ""}`;
    const list = grouped.get(key) ?? [];
    list.push(tem);
    grouped.set(key, list);
  });

  const labels: VendorNccLabelData[] = [];
  grouped.forEach((tems) => {
    const sorted = [...tems].sort((a, b) =>
      a.reelId.localeCompare(b.reelId, undefined, { numeric: true }),
    );
    const total = sorted.length;
    sorted.forEach((tem, index) => {
      const sourceIndex = findSourceRowIndex(
        input.sourceRows,
        tem.sapCode,
        tem.lot,
      );
      const source =
        sourceIndex >= 0
          ? input.sourceRows[sourceIndex]
          : ({
              sapCode: tem.sapCode,
              productName: "",
              partNumber: tem.partNumber,
              quantity: tem.initialQuantity ?? 0,
              grossNetWeight: "",
              meas: "",
              po: tem.userData5 ?? "",
              contractNo: "",
              invoiceNo: "",
              lotBatchNo: tem.lot ?? "",
              mfgDate: tem.manufacturingDate ?? "",
              madeIn: "",
              vendorName: "",
            } as VendorPrintSourceExportInput);

      labels.push(
        toLabelFromSource(source, {
          id: tem.reelId || `tem-${index}`,
          reelId: tem.reelId,
          qrCode: tem.qrCode,
          sapCode: tem.sapCode,
          partNumber: tem.partNumber,
          quantity: tem.initialQuantity ?? source.quantity ?? "",
          poNo: tem.userData5 ?? source.po,
          batchNo: tem.lot ?? source.lotBatchNo,
          mfgDate: formatMfgDate(tem.manufacturingDate ?? source.mfgDate),
          boxNo: `${index + 1} of ${total}`,
        }),
      );
    });
  });

  return labels.sort((a, b) =>
    a.reelId.localeCompare(b.reelId, undefined, { numeric: true }),
  );
}

function buildFromImportedReels(
  input: VendorNccPrintBuildInput,
): VendorNccLabelData[] {
  const labels: VendorNccLabelData[] = [];
  (input.importedReelGroups ?? []).forEach((group) => {
    const source = input.sourceRows[group.sourceRowIndex];
    if (!source) {
      return;
    }
    const total = group.reels.length;
    group.reels.forEach((reel, index) => {
      labels.push(
        toLabelFromSource(source, {
          id: reel.reelId,
          reelId: reel.reelId,
          qrCode: reel.qrCode,
          partNumber: reel.partNumber,
          vendorItemName: source.productName,
          quantity: reel.quantity,
          poNo: reel.poNumber || source.po,
          batchNo: reel.lotNumber || source.lotBatchNo,
          mfgDate: formatMfgDate(reel.mfgDate || source.mfgDate),
          boxNo: `${index + 1} of ${total}`,
        }),
      );
    });
  });
  return labels.sort((a, b) =>
    a.reelId.localeCompare(b.reelId, undefined, { numeric: true }),
  );
}

function buildFromSourceCounts(
  sourceRows: VendorPrintSourceExportInput[],
): VendorNccLabelData[] {
  const labels: VendorNccLabelData[] = [];
  sourceRows.forEach((source, rowIndex) => {
    const total = Math.max(1, source.temCount ?? 1);
    for (let i = 0; i < total; i += 1) {
      labels.push(
        toLabelFromSource(source, {
          id: `source-${rowIndex}-${i}`,
          boxNo: `${i + 1} of ${total}`,
        }),
      );
    }
  });
  return labels;
}

function applyBoxNoByGroup(labels: VendorNccLabelData[]): VendorNccLabelData[] {
  const grouped = new Map<string, VendorNccLabelData[]>();
  labels.forEach((label) => {
    const key = [
      label.sapCode.trim(),
      label.partNumber.trim(),
      label.batchNo.trim(),
      label.poNo.trim(),
    ].join("|");
    const list = grouped.get(key) ?? [];
    list.push(label);
    grouped.set(key, list);
  });

  const normalized: VendorNccLabelData[] = [];
  grouped.forEach((items) => {
    const sorted = [...items].sort((a, b) =>
      a.reelId.localeCompare(b.reelId, undefined, { numeric: true }),
    );
    const total = sorted.length;
    sorted.forEach((item, index) => {
      normalized.push({
        ...item,
        boxNo: `${index + 1} of ${total}`,
      });
    });
  });

  return normalized.sort((a, b) =>
    a.reelId.localeCompare(b.reelId, undefined, { numeric: true }),
  );
}

export function buildVendorNccLabels(
  input: VendorNccPrintBuildInput,
): VendorNccLabelData[] {
  if (input.temDetails.length > 0) {
    return applyBoxNoByGroup(buildFromTemDetails(input));
  }
  if ((input.importedReelGroups?.length ?? 0) > 0) {
    return applyBoxNoByGroup(buildFromImportedReels(input));
  }
  return applyBoxNoByGroup(buildFromSourceCounts(input.sourceRows));
}

export function filterVendorNccLabelsByReelId(
  labels: VendorNccLabelData[],
  fromReelId: string,
  toReelId: string,
): VendorNccLabelData[] {
  const from = fromReelId.trim();
  const to = toReelId.trim();
  if (!from && !to) {
    return labels;
  }
  return labels.filter((label) => {
    const reel = label.reelId.trim();
    if (!reel) {
      return !from && !to;
    }
    if (from && reel < from) {
      return false;
    }
    if (to && reel > to) {
      return false;
    }
    return true;
  });
}

export function getVendorNccPrintCss(paperSize: VendorNccPaperSize): string {
  const { widthMm, heightMm } = getVendorNccPaperDimensions(paperSize);

  return `
    @media print {
      @page {
        size: ${widthMm}mm ${heightMm}mm;
        margin: 0;
      }

      body {
        margin: 0 !important;
        padding: 0 !important;
      }

      body > *:not(#vendorNccPrintClone) {
        display: none !important;
      }

      #vendorNccPrintClone {
        display: block !important;
        visibility: visible !important;
      }

      #vendorNccPrintClone .vendor-ncc-label-page {
        width: ${widthMm}mm !important;
        height: ${heightMm}mm !important;
        min-height: ${heightMm}mm !important;
        max-height: ${heightMm}mm !important;
        page-break-after: always !important;
        page-break-inside: avoid !important;
        break-after: page !important;
        break-inside: avoid !important;
        margin: 0 !important;
        padding: 0 !important;
        box-sizing: border-box !important;
        background: #fff !important;
        overflow: hidden !important;
        box-shadow: none !important;
      }

      #vendorNccPrintClone .vendor-ncc-label-page:last-child {
        page-break-after: avoid !important;
        break-after: avoid !important;
      }

      #vendorNccPrintClone .label-sheet {
        width: 100% !important;
        height: 100% !important;
      }

      #vendorNccPrintClone .shipping-icons-img {
        print-color-adjust: exact !important;
        -webkit-print-color-adjust: exact !important;
      }
    }
  `;
}

export function runVendorNccLabelPrint(
  containerId: string,
  paperSize: VendorNccPaperSize,
  onAfterPrint?: () => void,
): void {
  const container = document.getElementById(containerId);
  if (!container) {
    return;
  }

  const printContainer = container.cloneNode(true) as HTMLElement;
  printContainer.id = "vendorNccPrintClone";

  const originalCanvases = container.querySelectorAll("canvas");
  const clonedCanvases = printContainer.querySelectorAll("canvas");
  originalCanvases.forEach((originalCanvas, index) => {
    const clonedCanvas = clonedCanvases[index] as HTMLCanvasElement;
    const ctx = clonedCanvas?.getContext("2d");
    if (ctx && originalCanvas) {
      clonedCanvas.width = originalCanvas.width;
      clonedCanvas.height = originalCanvas.height;
      ctx.drawImage(originalCanvas, 0, 0);
    }
  });

  printContainer.style.display = "none";
  document.body.appendChild(printContainer);

  const style = document.createElement("style");
  style.id = "vendor-ncc-print-style";
  style.textContent = getVendorNccPrintCss(paperSize);
  document.head.appendChild(style);

  setTimeout(() => {
    window.print();
    const cleanup = (): void => {
      printContainer.remove();
      style.remove();
      window.removeEventListener("afterprint", cleanup);
    };
    window.addEventListener("afterprint", cleanup);
    setTimeout(cleanup, 3000);
    onAfterPrint?.();
  }, 400);
}

function delay(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Xuất PDF tuần tự: mỗi lần chỉ render 1 tem (giảm tải UI khi nhiều tem).
 * `renderLabel` chuyển preview sang tem index i rồi chờ QR vẽ xong.
 */
export async function exportVendorNccLabelsToPdfSequential(
  paperSize: VendorNccPaperSize,
  totalLabels: number,
  fileName: string,
  renderLabel: (index: number) => Promise<HTMLElement | null>,
  onProgress?: (percent: number) => void,
): Promise<void> {
  if (totalLabels <= 0) {
    throw new Error("Không có tem để xuất PDF.");
  }

  const { widthMm, heightMm } = getVendorNccPaperDimensions(paperSize);
  const pdf = new jsPDF({
    orientation: widthMm >= heightMm ? "landscape" : "portrait",
    unit: "mm",
    format: [widthMm, heightMm],
  });

  for (let i = 0; i < totalLabels; i += 1) {
    if (i > 0) {
      pdf.addPage([widthMm, heightMm], widthMm >= heightMm ? "landscape" : "portrait");
    }

    const pageEl = await renderLabel(i);
    if (!pageEl) {
      throw new Error(`Không render được tem thứ ${i + 1}.`);
    }

    await delay(280);

    const canvas = await html2canvas(pageEl, {
      scale: 2,
      useCORS: true,
      logging: false,
      backgroundColor: "#ffffff",
    });

    const imgData = canvas.toDataURL("image/jpeg", 0.92);
    const ratio = Math.min(widthMm / canvas.width, heightMm / canvas.height);
    const pdfWidth = canvas.width * ratio;
    const pdfImgHeight = canvas.height * ratio;
    pdf.addImage(imgData, "JPEG", 0, 0, pdfWidth, pdfImgHeight);
    onProgress?.(Math.round(((i + 1) / totalLabels) * 100));
    canvas.remove();
  }

  pdf.save(fileName);
}

/** @deprecated Dùng exportVendorNccLabelsToPdfSequential khi preview 1 tem. */
export async function exportVendorNccLabelsToPdf(
  containerId: string,
  paperSize: VendorNccPaperSize,
  fileName: string,
  onProgress?: (percent: number) => void,
): Promise<void> {
  const container = document.getElementById(containerId);
  if (!container) {
    throw new Error("Không tìm thấy vùng preview in tem NCC.");
  }

  const pages = Array.from(
    container.querySelectorAll(".vendor-ncc-label-page"),
  ) as HTMLElement[];

  if (!pages.length) {
    throw new Error("Không có tem để xuất PDF.");
  }

  const { widthMm, heightMm } = getVendorNccPaperDimensions(paperSize);
  const pdf = new jsPDF({
    orientation: widthMm >= heightMm ? "landscape" : "portrait",
    unit: "mm",
    format: [widthMm, heightMm],
  });

  for (let i = 0; i < pages.length; i += 1) {
    if (i > 0) {
      pdf.addPage([widthMm, heightMm], widthMm >= heightMm ? "landscape" : "portrait");
    }

    const canvas = await html2canvas(pages[i], {
      scale: 2,
      useCORS: true,
      logging: false,
      backgroundColor: "#ffffff",
    });

    const imgData = canvas.toDataURL("image/jpeg", 0.92);
    const ratio = Math.min(widthMm / canvas.width, heightMm / canvas.height);
    const pdfWidth = canvas.width * ratio;
    const pdfImgHeight = canvas.height * ratio;
    pdf.addImage(imgData, "JPEG", 0, 0, pdfWidth, pdfImgHeight);
    onProgress?.(Math.round(((i + 1) / pages.length) * 100));
    canvas.remove();
  }

  pdf.save(fileName);
}
