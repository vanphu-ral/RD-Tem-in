import { HTTP_INTERCEPTORS } from "@angular/common/http";

import { AuthExpiredInterceptor } from "app/core/interceptor/auth-expired.interceptor";
import { ErrorHandlerInterceptor } from "app/core/interceptor/error-handler.interceptor";
import { NotificationInterceptor } from "app/core/interceptor/notification.interceptor";
import { RefreshTokenInterceptor } from "../auth/refresh-token-interceptor.service";

export const httpInterceptorProviders = [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthExpiredInterceptor,
    multi: true,
  },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: RefreshTokenInterceptor,
    multi: true,
  },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: ErrorHandlerInterceptor,
    multi: true,
  },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: NotificationInterceptor,
    multi: true,
  },
];
