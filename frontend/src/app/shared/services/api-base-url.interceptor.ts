import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

/**
 * Interceptor that adds the base API URL to all requests that start with /api
 * This ensures requests go to the backend server instead of the Angular dev server
 */
export const apiBaseUrlInterceptor: HttpInterceptorFn = (req, next) => {
  // Only modify requests that start with /api
  if (req.url.startsWith('/api') || req.url.startsWith('/ws')) {
    const apiReq = req.clone({
      url: `${environment.apiUrl}${req.url}`,
    });
    return next(apiReq);
  }

  return next(req);
};
