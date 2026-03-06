import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { Router } from "@angular/router";

export interface Tab {
  id: string;
  title: string;
  route: string;
  isActive: boolean;
  isNew?: boolean; // optional: highlight mới thêm
}

@Injectable({
  providedIn: "root",
})
export class TabService {
  tabs: Tab[] = [
    { id: "home", title: "Trang Chủ", route: "/", isActive: true },
  ];
  tabsSubject = new BehaviorSubject<Tab[]>(this.tabs);
  tabs$ = this.tabsSubject.asObservable();

  constructor(private router: Router) {}

  /**
   * Mở hoặc chuyển đến tab theo route và title.
   * Nếu tab đã tồn tại → chỉ activate.
   * Nếu chưa có → thêm mới.
   */
  openTab(title: string, route: string): void {
    const existingTab = this.tabs.find((t) => t.route === route);

    if (existingTab) {
      this.setActive(existingTab.id);
    } else {
      const newTab: Tab = {
        id: `tab_${Date.now()}`,
        title,
        route,
        isActive: false,
        isNew: true,
      };
      this.tabs.push(newTab);
      this.setActive(newTab.id);

      // Bỏ flag isNew sau animation
      setTimeout(() => {
        newTab.isNew = false;
        this.tabsSubject.next([...this.tabs]);
      }, 600);
    }

    this.router.navigate([route]);
  }

  setActive(tabId: string): void {
    this.tabs.forEach((t) => (t.isActive = t.id === tabId));
    this.tabsSubject.next([...this.tabs]);
  }

  closeTab(tabId: string, event?: MouseEvent): void {
    event?.stopPropagation();
    const idx = this.tabs.findIndex((t) => t.id === tabId);
    if (idx === -1) {
      return;
    }

    const wasActive = this.tabs[idx].isActive;
    this.tabs.splice(idx, 1);

    // Nếu đóng tab đang active → chuyển sang tab gần nhất
    if (wasActive && this.tabs.length > 0) {
      const newActiveIdx = Math.min(idx, this.tabs.length - 1);
      this.tabs[newActiveIdx].isActive = true;
      this.router.navigate([this.tabs[newActiveIdx].route]);
    }

    this.tabsSubject.next([...this.tabs]);
  }

  getTabs(): Tab[] {
    return this.tabs;
  }
}
