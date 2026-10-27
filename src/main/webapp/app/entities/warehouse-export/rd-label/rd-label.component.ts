import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnChanges,
  ViewChild,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { QRCodeComponent } from "angularx-qrcode";
import JsBarcode from "jsbarcode";
import { SplitPrintLabel } from "./rd-label.model";

/**
 * 1 tem mẫu Rạng Đông — markup / class giống Preview In Tem ở receiving-supplies để
 * runLabelPrint (CSS #printPreviewClone .label …) in đúng khổ 102.5 × 35 mm.
 */
@Component({
  selector: "jhi-rd-label",
  standalone: true,
  imports: [CommonModule, QRCodeComponent],
  templateUrl: "./rd-label.component.html",
  styleUrls: ["./rd-label.component.scss"],
})
export class RdLabelComponent implements AfterViewInit, OnChanges {
  @Input({ required: true }) label!: SplitPrintLabel;

  @ViewChild("nnSvg") nnSvg?: ElementRef<SVGSVGElement>;
  @ViewChild("poSvg") poSvg?: ElementRef<SVGSVGElement>;

  ngAfterViewInit(): void {
    this.renderBarcodes();
  }

  ngOnChanges(): void {
    this.renderBarcodes();
  }

  /** yyyy-MM-dd → yyMMdd (dòng NN trên tem) */
  formatArrivalDate(dateStr: string): string {
    const parts = (dateStr || "").split("-");
    return parts.length === 3
      ? parts[0].slice(2) + parts[1] + parts[2]
      : dateStr;
  }

  /** Mã vạch NN (ngày nhận yyMMdd) + Po (userData5) — cùng thông số generateLabelBarcodes */
  private renderBarcodes(): void {
    const draw = (target: SVGSVGElement | undefined, value: string): void => {
      if (!target) {
        return;
      }
      try {
        JsBarcode(target, value, {
          format: "CODE128",
          lineColor: "#000",
          width: 2,
          height: 25,
          displayValue: false,
          margin: 0,
        });
      } catch {
        target.innerHTML = "";
      }
    };
    const compactDate = (this.label.arrivalDate || "000000")
      .replace(/-/g, "")
      .slice(2);
    draw(this.nnSvg?.nativeElement, compactDate || "000000");
    draw(this.poSvg?.nativeElement, this.label.userData5 || "00000");
  }
}
