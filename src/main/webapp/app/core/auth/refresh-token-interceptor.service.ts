import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AuthServerProvider } from "./auth-session.service";
import { catchError, Observable, switchMap, throwError } from "rxjs";
import { AuthEventService } from "./auth_event.service";

@Injectable()
export class RefreshTokenInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(
    private authServerProvider: AuthServerProvider,
    private authEvents: AuthEventService,
  ) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error) => {
        if (
          error instanceof HttpErrorResponse &&
          error.status === 401 &&
          !this.isRefreshing
        ) {
          this.isRefreshing = true;
          return this.authServerProvider.refreshToken().pipe(
            switchMap((newToken: string) => {
              this.isRefreshing = false;
              const clonedReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`,
                },
              });
              return next.handle(clonedReq);
            }),
            catchError((err: unknown) => {
              this.isRefreshing = false;
              this.authEvents.triggerLogin();
              const typedError = err as HttpErrorResponse;
              console.error("Refresh token failed:", typedError.message);

              return throwError(() => typedError);
            }),
          );
        }

        return throwError(() => error as HttpErrorResponse);
      }),
    );
  }
}
