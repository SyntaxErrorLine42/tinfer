import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { CreateProfileRequest, ProfileResponse } from './profile.service';

@Injectable({ providedIn: 'root' })
export class ProfileInitService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  /**
   * Checks if the current user has a profile, and creates one if they don't.
   * This should be called after successful login/registration.
   */
  async ensureProfileExists(): Promise<boolean> {
    try {
      // Try to get the current user's profile
      const profile = await firstValueFrom(
        this.http.get<ProfileResponse>('/api/profiles/me')
      );
      
      // Profile exists
      return true;
    } catch (error) {
      if (error instanceof HttpErrorResponse && error.status === 404) {
        // Profile doesn't exist, create it
        return await this.createProfileFromAuth();
      }
      
      // Some other error occurred
      console.error('Error checking profile:', error);
      return false;
    }
  }

  /**
   * Creates a profile using the authenticated user's Supabase data
   */
  private async createProfileFromAuth(): Promise<boolean> {
    try {
      const user = await this.authService.getCurrentUser();
      
      if (!user) {
        console.error('No authenticated user found');
        return false;
      }

      // Extract name from user metadata or email
      const fullName = user.user_metadata?.['full_name'] || '';
      const nameParts = fullName.split(' ');
      const firstName = nameParts[0] || user.email?.split('@')[0] || 'User';
      const lastName = nameParts.slice(1).join(' ') || '';

      const profileData: CreateProfileRequest = {
        email: user.email!,
        firstName: firstName,
        lastName: lastName,
        displayName: firstName,
        bio: '',
        yearOfStudy: null,
        studentId: '',
      };

      // Create the profile
      await firstValueFrom(
        this.http.post<ProfileResponse>('/api/profiles', profileData)
      );

      console.log('✅ Profile created automatically');
      return true;
    } catch (error) {
      console.error('Failed to create profile:', error);
      return false;
    }
  }
}

