import {
  Directive,
  ElementRef,
  Input,
  NgZone,
  OnDestroy,
  OnInit,
  Renderer2,
} from "@angular/core";

/** Độ rộng tối thiểu khi kéo (px) */
const MIN_WIDTH = 48;

/**
 * Kéo cạnh phải của ô tiêu đề (th) để đổi độ rộng cột. Mặc định giữ nguyên độ rộng hiện tại;
 * nhấp đúp vào tay kéo → về mặc định. Độ rộng đã kéo lưu theo trình duyệt (localStorage) nếu có
 * `resizeStorageKey`. Đặt biến CSS `--col-w-<tên cột>` trên bảng để ô dữ liệu (vd max-width) theo kịp.
 *
 * `<th mat-header-cell jhiResizableColumn="vendorName" resizeStorageKey="info-tem-ncc">`
 */
@Directive({
  selector: "[jhiResizableColumn]",
  standalone: true,
})
export class ResizableColumnDirective implements OnInit, OnDestroy {
  @Input("jhiResizableColumn") columnName = "";
  @Input() resizeStorageKey = "";

  private handle?: HTMLElement;
  private cleanups: Array<() => void> = [];

  constructor(
    private el: ElementRef<HTMLElement>,
    private renderer: Renderer2,
    private zone: NgZone,
  ) {}

  ngOnInit(): void {
    const th = this.el.nativeElement;
    if (getComputedStyle(th).position === "static") {
      this.renderer.setStyle(th, "position", "relative");
    }
    const handle: HTMLElement = this.renderer.createElement("div");
    handle.title = "Kéo để đổi độ rộng cột · nhấp đúp để về mặc định";
    Object.assign(handle.style, {
      position: "absolute",
      top: "0",
      right: "-3px",
      width: "7px",
      height: "100%",
      cursor: "col-resize",
      zIndex: "2",
      touchAction: "none",
    });
    this.renderer.appendChild(th, handle);
    this.handle = handle;

    const saved = this.loadWidth();
    if (saved) {
      this.applyWidth(saved);
    }

    this.zone.runOutsideAngular(() => {
      this.cleanups.push(
        this.renderer.listen(handle, "pointerdown", (e: PointerEvent) =>
          this.startResize(e),
        ),
        // Không để thao tác kéo kích hoạt sắp xếp cột
        this.renderer.listen(handle, "click", (e: Event) =>
          e.stopPropagation(),
        ),
        this.renderer.listen(handle, "dblclick", (e: Event) => {
          e.stopPropagation();
          this.resetWidth();
        }),
        this.renderer.listen(handle, "mouseenter", () =>
          this.renderer.setStyle(
            handle,
            "background",
            "rgba(255,255,255,0.45)",
          ),
        ),
        this.renderer.listen(handle, "mouseleave", () =>
          this.renderer.removeStyle(handle, "background"),
        ),
      );
    });
  }

  ngOnDestroy(): void {
    this.cleanups.forEach((fn) => fn());
    this.cleanups = [];
  }

  private startResize(e: PointerEvent): void {
    e.preventDefault();
    e.stopPropagation();
    const th = this.el.nativeElement;
    const startX = e.clientX;
    const startWidth = th.getBoundingClientRect().width;
    const prevCursor = document.body.style.cursor;
    const prevSelect = document.body.style.userSelect;
    document.body.style.cursor = "col-resize";
    document.body.style.userSelect = "none";

    const move = (ev: PointerEvent): void => {
      this.applyWidth(
        Math.max(MIN_WIDTH, Math.round(startWidth + ev.clientX - startX)),
      );
    };
    const up = (): void => {
      document.removeEventListener("pointermove", move);
      document.removeEventListener("pointerup", up);
      document.body.style.cursor = prevCursor;
      document.body.style.userSelect = prevSelect;
      this.saveWidth(Math.round(th.getBoundingClientRect().width));
    };
    document.addEventListener("pointermove", move);
    document.addEventListener("pointerup", up);
  }

  private applyWidth(width: number): void {
    const th = this.el.nativeElement;
    const px = `${width}px`;
    this.renderer.setStyle(th, "width", px);
    this.renderer.setStyle(th, "min-width", px);
    this.renderer.setStyle(th, "max-width", px);
    const table = th.closest("table");
    if (table && this.columnName) {
      table.style.setProperty(`--col-w-${this.columnName}`, px);
    }
  }

  private resetWidth(): void {
    const th = this.el.nativeElement;
    this.renderer.removeStyle(th, "width");
    this.renderer.removeStyle(th, "min-width");
    this.renderer.removeStyle(th, "max-width");
    const table = th.closest("table");
    if (table && this.columnName) {
      table.style.removeProperty(`--col-w-${this.columnName}`);
    }
    this.saveWidth(null);
  }

  private storageId(): string | null {
    return this.resizeStorageKey && this.columnName
      ? `col-widths.${this.resizeStorageKey}`
      : null;
  }

  private loadWidth(): number | null {
    const id = this.storageId();
    if (!id) {
      return null;
    }
    try {
      const all = JSON.parse(localStorage.getItem(id) ?? "{}") as Record<
        string,
        number
      >;
      const w = Number(all[this.columnName]);
      return w >= MIN_WIDTH ? w : null;
    } catch {
      return null;
    }
  }

  private saveWidth(width: number | null): void {
    const id = this.storageId();
    if (!id) {
      return;
    }
    try {
      const all = JSON.parse(localStorage.getItem(id) ?? "{}") as Record<
        string,
        number
      >;
      if (width === null) {
        delete all[this.columnName];
      } else {
        all[this.columnName] = width;
      }
      localStorage.setItem(id, JSON.stringify(all));
    } catch {
      // trình duyệt chặn localStorage → chỉ giữ trong phiên hiện tại
    }
  }
}
