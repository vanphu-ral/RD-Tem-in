import { MatDialog } from "@angular/material/dialog";
import { forkJoin, Observable, of } from "rxjs";
import { catchError, map, switchMap } from "rxjs/operators";
import { NotificationService } from "app/entities/list-material/services/notification.service";
import { InfoTemNccService } from "../services/info-tem-ncc.service";
import { openChoiceDialog } from "./choice-dialog/choice-dialog.component";
import {
  buildCompletedOrderPayload,
  buildOrderStatusPayload,
  DELIVERY_STATUS_SCANNING,
  isOrderCompleted,
  isOrderNew,
  orderCompletionBlockers,
} from "./order-completion.util";

export interface CompleteOrderDeps {
  dialog: MatDialog;
  infoTemNccService: InfoTemNccService;
  notificationService: NotificationService;
}

/**
 * Nút "Hoàn thành" đơn (desktop + mobile): tải chi tiết đơn + toàn bộ thùng →
 *  - chưa đủ điều kiện (thiếu SL, chưa gửi SAP / PanaCIM, còn hàng chờ) → popup đủ lý do, không gửi
 *  - đủ → hỏi xác nhận → PUT /delivery-notifications/{id} với status COMPLETED → onCompleted()
 */
export function completeOrderFlow(
  deps: CompleteOrderDeps,
  deliveryId: number | null | undefined,
  onCompleted: () => void,
  setBusy?: (busy: boolean) => void,
): void {
  const { dialog, infoTemNccService, notificationService } = deps;
  if (deliveryId === null || deliveryId === undefined) {
    notificationService.warning("Chưa có đơn giao hàng — lưu đơn trước.");
    return;
  }
  setBusy?.(true);
  forkJoin({
    detail: infoTemNccService.getDeliveryNotificationDetail(deliveryId),
    boxes: infoTemNccService.getVendorLabelInfosByDelivery(deliveryId),
  }).subscribe({
    next: ({ detail, boxes }) => {
      setBusy?.(false);
      if (isOrderCompleted(detail)) {
        notificationService.info("Đơn đã ở trạng thái hoàn thành.");
        return;
      }
      const reasons = orderCompletionBlockers(detail, boxes);
      if (reasons.length) {
        openChoiceDialog(dialog, {
          title: "Chưa thể hoàn thành đơn",
          message: "Đơn chưa đủ điều kiện hoàn thành:",
          items: reasons,
          confirmText: "Đã hiểu",
          cancelText: "",
          tone: "warning",
        }).subscribe();
        return;
      }
      openChoiceDialog(dialog, {
        title: "Hoàn thành đơn",
        highlight: detail.deliveryNotificationCode ?? undefined,
        message:
          "Mọi vật tư đã nhận đủ và đã gửi SAP, PanaCIM. Xác nhận hoàn thành đơn?",
        confirmText: "Hoàn thành",
        cancelText: "Hủy",
        tone: "info",
      }).subscribe((ok) => {
        if (!ok) {
          return;
        }
        setBusy?.(true);
        infoTemNccService
          .updateDeliveryNotification(buildCompletedOrderPayload(detail))
          .subscribe({
            next: () => {
              setBusy?.(false);
              notificationService.success("Đã hoàn thành đơn.");
              // Gửi ngay trạng thái COMPLETED + tổng SL thực nhận sang gateway
              infoTemNccService.syncScanTotal(deliveryId, "FE_COMPLETED", true);
              onCompleted();
            },
            error: () => {
              setBusy?.(false);
              notificationService.error(
                "Cập nhật trạng thái hoàn thành thất bại.",
              );
            },
          });
      });
    },
    error: () => {
      setBusy?.(false);
      notificationService.error("Không tải được thông tin đơn để kiểm tra.");
    },
  });
}

/**
 * Đã scan thùng vào đơn → đơn đang New thì PUT trạng thái SCANNING (đơn đã SCANNING / COMPLETED giữ nguyên).
 * Phát true khi không cần gọi lại (đã đổi / không phải đơn New); lỗi → false (không chặn luồng scan).
 */
export function markOrderScanning(
  infoTemNccService: InfoTemNccService,
  deliveryId: number,
): Observable<boolean> {
  return infoTemNccService.getDeliveryNotificationDetail(deliveryId).pipe(
    switchMap((detail): Observable<boolean> => {
      if (!isOrderNew(detail)) {
        return of(true);
      }
      return infoTemNccService
        .updateDeliveryNotification(
          buildOrderStatusPayload(detail, DELIVERY_STATUS_SCANNING),
        )
        .pipe(map((): boolean => true));
    }),
    catchError(() => of(false)),
  );
}
