import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@shared/services/auth.service';

// Auth check based on actual Supabase session validity
export const authGuard: CanMatchFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // If user tries to access the "authenticated only" endpoint, he gets redirected to login
  const user = await authService.getCurrentUser();

  if (user) {
    return true;
  }

  return router.parseUrl('/login');
};
