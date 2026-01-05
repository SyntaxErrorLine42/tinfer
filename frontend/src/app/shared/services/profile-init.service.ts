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
   * Checks if the current user has a profile.
   * Returns true if profile exists, false if it needs to be created.
   */
  async checkProfileExists(): Promise<boolean> {
    try {
      // Try to get the current user's profile
      await firstValueFrom(
        this.http.get<ProfileResponse>('/api/profiles/me')
      );
      
      // Profile exists
      return true;
    } catch (error) {
      if (error instanceof HttpErrorResponse && error.status === 404) {
        // Profile doesn't exist
        return false;
      }
      
      // Some other error occurred
      console.error('Error checking profile:', error);
      throw error;
    }
  }
}

