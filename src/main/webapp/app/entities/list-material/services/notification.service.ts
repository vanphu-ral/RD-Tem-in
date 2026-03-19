import { Injectable } from "@angular/core";
import { MatSnackBar, MatSnackBarConfig } from "@angular/material/snack-bar";

export type NotificationType = "success" | "error" | "warning" | "info";

@Injectable({ providedIn: "root" })
export class NotificationService {
  private defaultConfig: MatSnackBarConfig = {
    duration: 3000,
    horizontalPosition: "right",
    verticalPosition: "top",
  };

  constructor(private snackBar: MatSnackBar) {}

  success(message: string): void {
    this.show(message, "snack-success");
  }

  error(message: string): void {
    this.show(message, "snack-error", 5000);
  }

  warning(message: string): void {
    this.show(message, "snack-warning");
  }

  info(message: string): void {
    this.show(message, "snack-info");
  }

  private show(message: string, panelClass: string, duration?: number): void {
    this.snackBar.open(message, "✕", {
      ...this.defaultConfig,
      duration: duration ?? this.defaultConfig.duration,
      panelClass: [panelClass],
    });
  }
}
