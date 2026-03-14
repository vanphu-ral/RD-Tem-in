import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of, EMPTY } from "rxjs";
import { mergeMap } from "rxjs/operators";

import { InfoTemNccModule } from "../info-tem-ncc.module";

@Injectable({ providedIn: "root" })
export class InfoTemNccResolveService {
  constructor(protected router: Router) {}
}
