import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@shared/services/auth.service';

// Check if the user is actually logged in via Supabase session
export const authGuard: CanMatchFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // If someone tries to access a protected route without being logged in, redirect to login
  const user = await authService.getCurrentUser();

  if (!user) {
    return router.parseUrl('/login');
  }

  // Check if profile exists - if not, allow access (user might be going to /create-profile)
  // This guard only blocks unauthenticated users
  // Profile check is done in login/register flows

  return true;
};
