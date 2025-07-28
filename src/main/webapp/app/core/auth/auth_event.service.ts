import { Injectable } from "@angular/core";
import { Subject } from "rxjs";

@Injectable({ providedIn: "root" })
export class AuthEventService {
  loginSubject = new Subject<void>();
  login$ = this.loginSubject.asObservable();

  triggerLogin(): void {
    this.loginSubject.next();
  }
}
