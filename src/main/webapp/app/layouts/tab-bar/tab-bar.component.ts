import { Component, OnInit } from "@angular/core";
import { Observable } from "rxjs";
import { Tab, TabService } from "./tab.service";

@Component({
  selector: "jhi-tab-bar",
  templateUrl: "./tab-bar.component.html",
  styleUrls: ["./tab-bar.component.scss"],
  standalone: false,
})
export class TabBarComponent {
  tabs$: Observable<Tab[]>;

  constructor(private tabService: TabService) {
    this.tabs$ = this.tabService.tabs$;
  }

  activateTab(tab: Tab): void {
    this.tabService.openTab(tab.title, tab.route);
  }

  closeTab(tabId: string, event: MouseEvent): void {
    this.tabService.closeTab(tabId, event);
  }
}
