import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StateStorageService {
  private readonly previousUrlKey = 'previousUrl';

  /** Lưu URL vào sessionStorage */
  storeUrl(url: string): void {
    sessionStorage.setItem(this.previousUrlKey, url);
  }

  /** Lấy URL từ sessionStorage (hoặc null nếu không có) */
  getUrl(): string | null {
    return sessionStorage.getItem(this.previousUrlKey);
  }

  /** Xóa URL đã lưu */
  clearUrl(): void {
    sessionStorage.removeItem(this.previousUrlKey);
  }
}
