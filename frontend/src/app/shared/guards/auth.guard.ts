import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@shared/services/auth.service';

// Provjera jesi li stvarno prijavljen preko Supabase sessiona
export const authGuard: CanMatchFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Ako netko proba ući na dio aplikacije samo za prijavljene, redirect na login
  const user = await authService.getCurrentUser();

  if (!user) {
    return router.parseUrl('/login');
  }

  // Check if profile exists - if not, allow access (user might be going to /create-profile)
  // This guard only blocks unauthenticated users
  // Profile check is done in login/register flows

  return true;
};
