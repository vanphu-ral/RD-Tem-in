import { Injectable } from "@angular/core";
import { HttpResponse } from "@angular/common/http";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { Observable, of, EMPTY } from "rxjs";
import { mergeMap } from "rxjs/operators";

import { GenerateTemInModule } from "../generate-tem-in.module";

@Injectable({ providedIn: "root" })
export class GenerateTemInResolveService {
  constructor(protected router: Router) {}
}
