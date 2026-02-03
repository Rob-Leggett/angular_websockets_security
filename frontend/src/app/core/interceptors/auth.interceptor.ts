import { HttpInterceptorFn } from '@angular/common/http';

const TOKEN_KEY = 'X-AUTH-TOKEN';

/**
 * Auth Interceptor - Modern replacement for the HTTP interceptor in config.js
 * 
 * Automatically adds the X-AUTH-TOKEN header to all outgoing requests.
 * This maintains compatibility with the backend's stateless token authentication.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = sessionStorage.getItem(TOKEN_KEY);

  if (token) {
    const clonedReq = req.clone({
      setHeaders: {
        [TOKEN_KEY]: token
      }
    });
    return next(clonedReq);
  }

  return next(req);
};
