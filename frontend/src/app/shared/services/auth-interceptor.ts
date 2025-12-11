import { HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { from, Observable, switchMap } from 'rxjs';
import { AuthService } from '@shared/services/auth.service';

// This puts the bearer token on every HTTP request we make, using the current Supabase session
export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);

  // IMPORTANT: every time we call any HTTP request, or every time we call any supabase auth function, we call getSession() which updates the local access_token based on the renewal_token, meaning we are always using the newest token and we aren't using any extra space in local storage since we are reusing default Supabase local storage
  return from(authService.getSession()).pipe(
    switchMap((session) => {
      const token = session?.access_token;

      if (!token) {
        return next(req);
      }

      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });

      return next(authReq);
    }),
  );
};
