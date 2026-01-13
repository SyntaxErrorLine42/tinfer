import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';

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

  // Simple in-memory cache
  private cachedRecommendations: ProfileRecommendation[] = [];
  private cacheTimestamp: number = 0;
  private readonly CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutes

  /**
   * Get recommended profiles for swiping (with caching)
   * @param limit Number of profiles to fetch (default 25, max 100)
   * @param forceRefresh Force fetch from server, ignoring cache
   */
  getRecommendations(limit: number = 25, forceRefresh: boolean = false): Observable<ProfileRecommendation[]> {
    const now = Date.now();
    const cacheValid = this.cachedRecommendations.length > 0 && 
                       (now - this.cacheTimestamp) < this.CACHE_TTL_MS;

    // Return cached data if valid and not forcing refresh
    if (cacheValid && !forceRefresh) {
      return of(this.cachedRecommendations);
    }

    // Fetch from server and update cache
    return this.http.get<ProfileRecommendation[]>('/api/recommendations', {
      params: { limit: limit.toString() }
    }).pipe(
      tap(recommendations => {
        this.cachedRecommendations = recommendations;
        this.cacheTimestamp = Date.now();
      })
    );
  }

  /**
   * Remove a profile from cache (called after swipe)
   */
  removeFromCache(profileId: string): void {
    this.cachedRecommendations = this.cachedRecommendations.filter(
      p => p.profileId !== profileId
    );
  }

  /**
   * Clear the cache (useful when user wants fresh recommendations)
   */
  clearCache(): void {
    this.cachedRecommendations = [];
    this.cacheTimestamp = 0;
  }

  /**
   * Send a swipe action (LIKE, PASS, or SUPER_LIKE)
   */
  swipe(swipedUserId: string, action: SwipeAction): Observable<SwipeResponse> {
    const request: SwipeRequest = { swipedUserId, action };
    return this.http.post<SwipeResponse>('/api/swipes', request).pipe(
      tap(() => {
        // Remove swiped profile from cache
        this.removeFromCache(swipedUserId);
      })
    );
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
