import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

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
  private apiUrl = environment.apiUrl;

  getMyProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(`${this.apiUrl}/api/profiles/me`);
  }

  getProfileDetails(id: string): Observable<ProfileDetailsResponse> {
    return this.http.get<ProfileDetailsResponse>(`${this.apiUrl}/api/profiles/${id}/details`);
  }

  createProfile(payload: CreateProfileRequest): Observable<ProfileResponse> {
    return this.http.post<ProfileResponse>(`${this.apiUrl}/api/profiles`, payload);
  }

  updateProfile(id: string, payload: CreateProfileRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(`${this.apiUrl}/api/profiles/${id}`, payload);
  }

  getMyPhotos(): Observable<PhotoResponse[]> {
    return this.http.get<PhotoResponse[]>(`${this.apiUrl}/api/profiles/me/photos`);
  }

  addPhoto(payload: { url: string; displayOrder?: number; isPrimary?: boolean }): Observable<PhotoResponse> {
    return this.http.post<PhotoResponse>(`${this.apiUrl}/api/profiles/me/photos`, payload);
  }

  updatePhoto(photoId: number, payload: { displayOrder?: number; isPrimary?: boolean }): Observable<PhotoResponse> {
    return this.http.put<PhotoResponse>(`${this.apiUrl}/api/profiles/me/photos/${photoId}`, payload);
  }

  deletePhoto(photoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/profiles/me/photos/${photoId}`);
  }
}
