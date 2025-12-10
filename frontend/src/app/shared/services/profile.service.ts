import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProfileResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  displayName?: string;
  bio?: string;
  yearOfStudy?: number;
  studentId?: string;
  isVerified: boolean;
  isActive: boolean;
  createdAt: string;
}

export interface PhotoResponse {
  id: number;
  url: string;
  displayOrder: number;
  isPrimary: boolean;
  uploadedAt: string;
}

export interface ProfileDetailsResponse extends ProfileResponse {
  updatedAt?: string;
  interests: string[];
  departments: string[];
  photos: PhotoResponse[];
}

export interface CreateProfileRequest {
  email: string;
  firstName: string;
  lastName: string;
  displayName?: string;
  bio?: string;
  yearOfStudy?: number | null;
  studentId?: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);

  getMyProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>('/api/profiles/me');
  }

  getProfileDetails(id: string): Observable<ProfileDetailsResponse> {
    return this.http.get<ProfileDetailsResponse>(`/api/profiles/${id}/details`);
  }

  updateProfile(id: string, payload: CreateProfileRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(`/api/profiles/${id}`, payload);
  }

  getMyPhotos(): Observable<PhotoResponse[]> {
    return this.http.get<PhotoResponse[]>('/api/profiles/me/photos');
  }
}
