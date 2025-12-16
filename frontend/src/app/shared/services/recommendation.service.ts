import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ProfileRecommendation {
  profileId: string;
  firstName: string;
  lastName: string;
  displayName: string;
  bio: string;
  yearOfStudy: number;
  verified: boolean;
  sharedInterests: string[];
  candidateInterests: string[];
  departments: string[];
  primaryPhotoUrl: string;
  photoGallery: string[];
  compatibilityScore: number;
  highlight: string;
}

@Injectable({
  providedIn: 'root',
})
export class RecommendationService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getRecommendations(limit: number = 25): Observable<ProfileRecommendation[]> {
    return this.http.get<ProfileRecommendation[]>(
      `${this.apiUrl}/api/recommendations`,
      { params: { limit: limit.toString() } }
    );
  }
}
