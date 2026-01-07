import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, firstValueFrom } from 'rxjs';

export interface PhotoResponse {
  id: number;
  base64Data: string;
  displayOrder: number | null;
  isPrimary: boolean;
  uploadedAt: string;
}

export interface PhotoCreateRequest {
  base64Data: string;
  displayOrder?: number;
  isPrimary?: boolean;
}

export interface PhotoUpdateRequest {
  base64Data?: string;
  displayOrder?: number;
  isPrimary?: boolean;
}

@Injectable({ providedIn: 'root' })
export class PhotoService {
  private http = inject(HttpClient);

  /**
   * Get all photos for the current user
   */
  getMyPhotos(): Observable<PhotoResponse[]> {
    return this.http.get<PhotoResponse[]>('/api/profiles/me/photos');
  }

  /**
   * Add a new photo
   */
  async addPhoto(request: PhotoCreateRequest): Promise<PhotoResponse> {
    return firstValueFrom(
      this.http.post<PhotoResponse>('/api/profiles/me/photos', request)
    );
  }

  /**
   * Update an existing photo
   */
  async updatePhoto(photoId: number, request: PhotoUpdateRequest): Promise<PhotoResponse> {
    return firstValueFrom(
      this.http.put<PhotoResponse>(`/api/profiles/me/photos/${photoId}`, request)
    );
  }

  /**
   * Delete a photo
   */
  async deletePhoto(photoId: number): Promise<void> {
    return firstValueFrom(
      this.http.delete<void>(`/api/profiles/me/photos/${photoId}`)
    );
  }

  /**
   * Convert File to Base64 string
   */
  async fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = (error) => reject(error);
    });
  }

  /**
   * Validate image
   */
  validateImage(file: File): { valid: boolean; error?: string } {
    const maxSize = 5 * 1024 * 1024; // 5MB
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];

    if (!allowedTypes.includes(file.type)) {
      return {
        valid: false,
        error: 'Image type must be JPEG, PNG or WebP',
      };
    }

    if (file.size > maxSize) {
      return {
        valid: false,
        error: 'Image size must not exceed 5MB',
      };
    }

    return { valid: true };
  }
}
