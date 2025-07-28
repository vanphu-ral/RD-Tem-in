import { Injectable } from "@angular/core";

@Injectable({ providedIn: "root" })
export class PendingMaterialService {
  private readonly storageKey = "pendingMaterials";

  save(materials: any[]): void {
    localStorage.setItem(this.storageKey, JSON.stringify(materials));
  }

  load(): any[] {
    const raw = localStorage.getItem(this.storageKey);
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return raw ? JSON.parse(raw) : [];
  }

  clear(): void {
    localStorage.removeItem(this.storageKey);
  }
}
