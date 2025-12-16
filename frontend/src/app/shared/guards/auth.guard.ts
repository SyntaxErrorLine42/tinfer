import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@shared/services/auth.service';
import { ProfileInitService } from '@shared/services/profile-init.service';

// Provjera jesi li stvarno prijavljen preko Supabase sessiona
export const authGuard: CanMatchFn = async () => {
  const authService = inject(AuthService);
  const profileInitService = inject(ProfileInitService);
  const router = inject(Router);

  // Ako netko proba ući na dio aplikacije samo za prijavljene, redirect na login
  const user = await authService.getCurrentUser();

  if (!user) {
    return router.parseUrl('/login');
  }

  // provjera ako ima profil
  const profileExists = await profileInitService.ensureProfileExists();
  
  if (!profileExists) {
    console.error('Failed to initialize profile, redirecting to login');
    return router.parseUrl('/login');
  }

  return true;
};
