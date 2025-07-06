import { Injectable } from "@angular/core";
import {
  BreakpointObserver,
  Breakpoints,
  BreakpointState,
} from "@angular/cdk/layout";
import { Observable } from "rxjs";
import { map, shareReplay } from "rxjs/operators";

@Injectable()
export class ResponsiveService {
  // Observable trả về true khi màn hình là handset (mobile)
  isHandset$: Observable<boolean> = this.bp.observe(Breakpoints.Handset).pipe(
    map((r: BreakpointState) => r.matches),
    shareReplay(1),
  );

  isTablet$: Observable<boolean> = this.bp.observe(Breakpoints.Tablet).pipe(
    map((r) => r.matches),
    shareReplay(1),
  );

  // Portrait / Landscape:
  isHandsetPortrait$: Observable<boolean> = this.bp
    .observe(Breakpoints.HandsetPortrait)
    .pipe(
      map((r) => r.matches),
      shareReplay(1),
    );
  constructor(private bp: BreakpointObserver) {}
}
