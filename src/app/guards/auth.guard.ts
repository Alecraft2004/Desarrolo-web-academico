import { inject } from '@angular/core'; //importa el inject paara pedir prestado a los servicios de angular
import { CanActivateFn, Router } from '@angular/router'; //Reconoce el Guard
import { AuthService } from '../services/auth.service'; //Obtener el AuthService y Router

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) return true;
  router.navigate(['/login']);
  return false;
};
