import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

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

export interface DatingProfile {
  lookingFor: string;
  gender: string;
  showGender: boolean;
  prompts: Prompt[];
}

export interface Prompt {
  question: string;
  answer: string;
}

export interface Interest {
  name: string;
  category: string;
}

export interface Department {
  name: string;
  code: string;
}

export interface ProfileDetailsResponse extends ProfileResponse {
  updatedAt?: string;
  interests: Interest[];
  departments: Department[];
  photos: PhotoResponse[];
  datingProfile?: DatingProfile;
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

export interface UpdateProfileRequest {
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

  getMyProfileDetails(): Observable<ProfileDetailsResponse> {
    // First get basic profile to get the ID, then fetch detailed profile
    return this.getMyProfile().pipe(
      switchMap((profile) => this.getProfileDetails(profile.id))
    );
  }

  getProfileDetails(id: string): Observable<ProfileDetailsResponse> {
    return this.http.get<ProfileDetailsResponse>(`/api/profiles/${id}/details`);
  }

  updateProfile(id: string, payload: UpdateProfileRequest): Observable<ProfileResponse> {
    // Backend expects CreateProfileRequest format which requires email
    return this.http.put<ProfileResponse>(`/api/profiles/${id}`, payload);
  }

  createProfile(payload: CreateProfileRequest): Observable<ProfileResponse> {
    return this.http.post<ProfileResponse>('/api/profiles', payload);
  }

  getMyPhotos(): Observable<PhotoResponse[]> {
    return this.http.get<PhotoResponse[]>('/api/profiles/me/photos');
  }
}
