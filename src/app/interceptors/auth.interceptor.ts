import { HttpInterceptorFn } from '@angular/common/http'; // importa la funcion que lo hace reconocer como insterceptor

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
};
