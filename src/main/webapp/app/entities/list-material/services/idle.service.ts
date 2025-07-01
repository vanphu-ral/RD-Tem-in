import { Injectable } from '@angular/core';
import { fromEvent, merge, timer } from 'rxjs';
import { switchMapTo } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class IdleService {
  private INACTIVE_MS = 5 * 60 * 1000;
  constructor() {
    const activity$ = merge(
      fromEvent(document, 'mousemove'),
      fromEvent(document, 'click'),
      fromEvent(document, 'keydown'),
      fromEvent(document, 'scroll'),
      fromEvent(document, 'touchstart'),
    );
    activity$.pipe(switchMapTo(timer(this.INACTIVE_MS))).subscribe(() => location.reload());
  }
}
