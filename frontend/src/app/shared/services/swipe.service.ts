import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Matches backend ProfileRecommendation DTO
export interface ProfileRecommendation {
  profileId: string;
  firstName: string;
  lastName: string;
  displayName: string;
  bio: string | null;
  yearOfStudy: number | null;
  gender: string | null;
  verified: boolean;
  sharedInterests: string[];  // Backend sends Set<String> as JSON array
  candidateInterests: string[];  // Backend sends Set<String> as JSON array
  departments: string[];  // Backend sends Set<String> as JSON array
  primaryPhotoUrl: string | null;  // URL to photo in Supabase Storage
  photoGalleryUrls: string[];  // URLs to photos in Supabase Storage
  compatibilityScore: number;
  highlight: string;
}

// Matches backend SwipeAction enum
export type SwipeAction = 'LIKE' | 'PASS' | 'SUPER_LIKE';

// Matches backend SwipeRequest DTO
export interface SwipeRequest {
  swipedUserId: string;
  action: SwipeAction;
}

// Matches backend SwipeResponse DTO
export interface SwipeResponse {
  swipedUserId: string;
  action: SwipeAction;
  matchCreated: boolean;
  matchId: number | null;
  conversationId: number | null;
}

@Injectable({ providedIn: 'root' })
export class SwipeService {
  private http = inject(HttpClient);

  /**
   * Get recommended profiles for swiping
   * @param limit Number of profiles to fetch (default 25, max 100)
   */
  getRecommendations(limit: number = 25): Observable<ProfileRecommendation[]> {
    return this.http.get<ProfileRecommendation[]>('/api/recommendations', {
      params: { limit: limit.toString() }
    });
  }

  /**
   * Send a swipe action (LIKE, PASS, or SUPER_LIKE)
   */
  swipe(swipedUserId: string, action: SwipeAction): Observable<SwipeResponse> {
    const request: SwipeRequest = { swipedUserId, action };
    return this.http.post<SwipeResponse>('/api/swipes', request);
  }

  /**
   * Like a profile
   */
  like(profileId: string): Observable<SwipeResponse> {
    return this.swipe(profileId, 'LIKE');
  }

  /**
   * Pass on a profile
   */
  pass(profileId: string): Observable<SwipeResponse> {
    return this.swipe(profileId, 'PASS');
  }

  /**
   * Super like a profile
   */
  superLike(profileId: string): Observable<SwipeResponse> {
    return this.swipe(profileId, 'SUPER_LIKE');
  }
}
