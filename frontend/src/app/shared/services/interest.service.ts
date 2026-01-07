import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Interest {
  id: number;
  name: string;
  category?: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class InterestService {
  private http = inject(HttpClient);

  /**
   * Get all available interests
   */
  getAllInterests(): Observable<Interest[]> {
    return this.http.get<Interest[]>('/api/interests');
  }

  /**
   * Search interests by name (for autocomplete)
   */
  searchInterests(query: string): Observable<Interest[]> {
    return this.http.get<Interest[]>('/api/interests/search', {
      params: { q: query }
    });
  }

  /**
   * Get interests by category
   */
  getInterestsByCategory(category: string): Observable<Interest[]> {
    return this.http.get<Interest[]>(`/api/interests/category/${category}`);
  }

  /**
   * Get current user's interests
   */
  getMyInterests(): Observable<Interest[]> {
    return this.http.get<Interest[]>('/api/interests/me');
  }

  /**
   * Add interests to current user's profile
   */
  addInterests(interestNames: string[]): Observable<void> {
    return this.http.post<void>('/api/interests/me', interestNames);
  }

  /**
   * Set user's interests (replace all existing)
   */
  setInterests(interestNames: string[]): Observable<void> {
    return this.http.put<void>('/api/interests/me', interestNames);
  }

  /**
   * Remove interest from current user's profile
   */
  removeInterest(interestName: string): Observable<void> {
    return this.http.delete<void>(`/api/interests/me/${encodeURIComponent(interestName)}`);
  }
}
