import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { VendorImportedReelEntry } from "./vendor-reel-log-import.util";
import { VendorPrintSourceExportInput } from "./vendor-print-source-export.util";

export type VendorNccPaperSize = "A5" | "A4";
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
  quantity: number;
  grossNetWeight: string;
  meas: string;
  poNo: string;
  contractNo: string;
  invoiceNo: string;
  batchNo: string;
  mfgDate: string;
  boxNo: string;
  madeIn: string;
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

const PAPER_SIZE_STORAGE_KEY = "vendor-ncc-print-paper-size";

export function loadVendorNccPaperSizePreference(): VendorNccPaperSize {
  const stored = localStorage.getItem(PAPER_SIZE_STORAGE_KEY);
  return stored === "A4" ? "A4" : "A5";
}

export function saveVendorNccPaperSizePreference(
  size: VendorNccPaperSize,
): void {
  localStorage.setItem(PAPER_SIZE_STORAGE_KEY, size);
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
  return {
    id: overrides.id,
    reelId: overrides.reelId ?? "",
    qrCode: overrides.qrCode ?? "",
    sapCode: (overrides.sapCode ?? source.sapCode).trim(),
    partNumber: (overrides.partNumber ?? source.partNumber).trim(),
    quantity: overrides.quantity ?? source.quantity ?? 0,
    grossNetWeight: overrides.grossNetWeight ?? source.grossNetWeight ?? "",
    meas: overrides.meas ?? source.meas ?? "",
    poNo: po,
    contractNo:
      overrides.contractNo ?? buildContractNo(po, source.contractNo ?? ""),
    invoiceNo: overrides.invoiceNo ?? source.invoiceNo ?? "",
    batchNo: (overrides.batchNo ?? source.lotBatchNo).trim(),
    mfgDate: overrides.mfgDate ?? formatMfgDate(source.mfgDate),
    boxNo: overrides.boxNo ?? "1 of 1",
    madeIn: (overrides.madeIn ?? source.madeIn ?? "China").trim(),
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
              partNumber: tem.partNumber,
              quantity: tem.initialQuantity ?? 0,
              grossNetWeight: "",
              meas: "",
              po: tem.userData5 ?? "",
              contractNo: "",
              invoiceNo: "",
              lotBatchNo: tem.lot ?? "",
              mfgDate: tem.manufacturingDate ?? "",
              madeIn: "China",
            } as VendorPrintSourceExportInput);

      labels.push(
        toLabelFromSource(source, {
          id: tem.reelId || `tem-${index}`,
          reelId: tem.reelId,
          qrCode: tem.qrCode,
          sapCode: tem.sapCode,
          partNumber: tem.partNumber,
          quantity: tem.initialQuantity ?? source.quantity ?? 0,
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
  const pageWidth = paperSize === "A5" ? "148mm" : "210mm";
  const pageHeight = paperSize === "A5" ? "210mm" : "297mm";

  return `
    @media print {
      @page {
        size: ${pageWidth} ${pageHeight};
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
        width: ${pageWidth} !important;
        height: ${pageHeight} !important;
        page-break-after: always !important;
        page-break-inside: avoid !important;
        margin: 0 !important;
        padding: 6mm !important;
        box-sizing: border-box !important;
        background: #fff !important;
      }

      #vendorNccPrintClone .vendor-ncc-label-page:last-child {
        page-break-after: avoid !important;
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
    const ctx = clonedCanvas.getContext("2d");
    if (ctx) {
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

  const isA5 = paperSize === "A5";
  const pdf = new jsPDF("portrait", "mm", isA5 ? [148, 210] : [210, 297]);
  const pageWidth = pdf.internal.pageSize.getWidth();
  const pageHeight = pdf.internal.pageSize.getHeight();

  for (let i = 0; i < pages.length; i += 1) {
    if (i > 0) {
      pdf.addPage(isA5 ? [148, 210] : [210, 297], "portrait");
    }

    const canvas = await html2canvas(pages[i], {
      scale: 2,
      useCORS: true,
      logging: false,
      backgroundColor: "#ffffff",
    });

    const imgData = canvas.toDataURL("image/jpeg", 0.92);
    const ratio = Math.min(
      pageWidth / canvas.width,
      pageHeight / canvas.height,
    );
    const pdfWidth = canvas.width * ratio;
    const pdfImgHeight = canvas.height * ratio;
    pdf.addImage(imgData, "JPEG", 0, 0, pdfWidth, pdfImgHeight);
    onProgress?.(Math.round(((i + 1) / pages.length) * 100));
    canvas.remove();
  }

  pdf.save(fileName);
}
