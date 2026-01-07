import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@shared/services/auth.service';
import { ProfileInitService } from '@shared/services/profile-init.service';

@Component({
  selector: 'app-callback',
  standalone: true,
  template: `
    <div class="min-h-screen flex items-center justify-center">
      <div class="text-center">
        <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-pink-500 mx-auto mb-4"></div>
        <p class="text-lg">Completing login...</p>
      </div>
    </div>
  `,
})
export class CallbackPage implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);
  private profileInitService = inject(ProfileInitService);

  async ngOnInit() {
    try {
      // Supabase automatically handles the OAuth callback and stores the session
      // We just need to check if the user is authenticated
      const session = await this.authService.getSession();
      
      if (session) {
        // User is authenticated, check if profile exists
        const profileExists = await this.profileInitService.checkProfileExists();

        if (profileExists) {
          // Profile exists, go to swipe
          this.router.navigate(['/swipe']);
        } else {
          // Profile doesn't exist, redirect to create profile
          this.router.navigate(['/create-profile']);
        }
      } else {
        // No session, something went wrong
        console.error('No session after OAuth callback');
        this.router.navigate(['/login']);
      }
    } catch (error) {
      console.error('OAuth callback error:', error);
      this.router.navigate(['/login']);
    }
  }
}
