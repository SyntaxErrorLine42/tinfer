import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@shared/services/auth.service';
import { ProfileService } from '@shared/services/profile.service';
import { catchError, map, of } from 'rxjs';

// Auth check based on actual Supabase session validity
export const authGuard: CanMatchFn = async (route) => {
  const authService = inject(AuthService);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  // If user tries to access the "authenticated only" endpoint, check if logged in
  const user = await authService.getCurrentUser();

  if (!user) {
    return router.parseUrl('/login');
  }

  // Za create-profile rutu, samo provjeri autentifikaciju
  if (route.path === 'create-profile') {
    return true;
  }

  // Za ostale zaštićene rute (npr. swipe), provjeri i postojanje profila
  try {
    const profile = await profileService.getMyProfile()
      .pipe(
        map(() => true),
        catchError(() => of(false))
      )
      .toPromise();

    if (!profile) {
      // Korisnik nema profil, redirect na create-profile
      return router.parseUrl('/create-profile');
    }

    return true;
  } catch (error) {
    return router.parseUrl('/create-profile');
  }
};
