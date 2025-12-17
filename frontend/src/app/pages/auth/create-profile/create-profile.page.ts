import { Component, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '@shared/services/auth.service';
import { CreateProfileRequest, ProfileResponse } from '@shared/services/profile.service';
import { InputComponent } from '../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../../shared/components/card/card.component';
import { IconComponent } from '../../../shared/components/icon-wrapper/icon-wrapper.component';
import { firstValueFrom } from 'rxjs';

interface Photo {
  url: string;
  isPrimary: boolean;
  file?: File;
}

@Component({
  selector: 'app-create-profile',
  imports: [InputComponent, ButtonComponent, CardComponent, IconComponent],
  templateUrl: './create-profile.page.html',
  styleUrl: './create-profile.page.css',
})
export class CreateProfilePage {
  private router = inject(Router);
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  // Form fields
  firstName = signal('');
  lastName = signal('');
  displayName = signal('');
  bio = signal('');
  yearOfStudy = signal<number | null>(null);
  studentId = signal('');
  photos = signal<Photo[]>([]);

  // Errors
  firstNameError = signal('');
  lastNameError = signal('');
  yearOfStudyError = signal('');
  generalError = signal('');
  isLoading = signal(false);

  onFirstNameChange(value: string) {
    this.firstName.set(value);
    this.firstNameError.set('');
  }

  onLastNameChange(value: string) {
    this.lastName.set(value);
    this.lastNameError.set('');
  }

  onDisplayNameChange(value: string) {
    this.displayName.set(value);
  }

  onBioChange(value: string) {
    if (value.length <= 500) {
      this.bio.set(value);
    }
  }

  onYearOfStudyChange(value: string) {
    const num = parseInt(value, 10);
    if (value === '') {
      this.yearOfStudy.set(null);
      this.yearOfStudyError.set('');
    } else if (isNaN(num) || num < 1 || num > 5) {
      this.yearOfStudyError.set('Godina mora biti između 1 i 5');
    } else {
      this.yearOfStudy.set(num);
      this.yearOfStudyError.set('');
    }
  }

  onStudentIdChange(value: string) {
    this.studentId.set(value);
  }

  async onPhotoSelect(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      this.generalError.set('Molimo odaberi sliku');
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      this.generalError.set('Slika je prevelika (max 5MB)');
      return;
    }

    // Create preview URL
    const url = URL.createObjectURL(file);
    const newPhotos = [...this.photos()];
    
    newPhotos.push({
      url,
      isPrimary: newPhotos.length === 0, // First photo is primary
      file,
    });

    this.photos.set(newPhotos);
    input.value = ''; // Reset input
  }

  removePhoto(index: number) {
    const newPhotos = this.photos().filter((_, i) => i !== index);
    
    // If we removed the primary photo, make the first one primary
    if (newPhotos.length > 0 && !newPhotos.some(p => p.isPrimary)) {
      newPhotos[0].isPrimary = true;
    }
    
    this.photos.set(newPhotos);
  }

  setPrimaryPhoto(index: number) {
    const newPhotos = this.photos().map((photo, i) => ({
      ...photo,
      isPrimary: i === index,
    }));
    
    this.photos.set(newPhotos);
  }

  async onSubmit() {
    // Validation
    let hasError = false;
    this.generalError.set('');

    if (!this.firstName().trim()) {
      this.firstNameError.set('Ime je obavezno');
      hasError = true;
    }

    if (!this.lastName().trim()) {
      this.lastNameError.set('Prezime je obavezno');
      hasError = true;
    }

    if (hasError) return;

    this.isLoading.set(true);

    try {
      // Get current user email
      const user = await this.authService.getCurrentUser();
      if (!user?.email) {
        throw new Error('Korisnik nije prijavljen');
      }

      // Create profile data
      const profileData: CreateProfileRequest = {
        email: user.email,
        firstName: this.firstName().trim(),
        lastName: this.lastName().trim(),
        displayName: this.displayName().trim() || this.firstName().trim(),
        bio: this.bio().trim(),
        yearOfStudy: this.yearOfStudy(),
        studentId: this.studentId().trim(),
      };

      // Create profile
      const profile = await firstValueFrom(
        this.http.post<ProfileResponse>('/api/profiles', profileData)
      );

      // TODO: Upload photos if any
      // This would require photo upload endpoint from backend
      if (this.photos().length > 0) {
        console.log('Photo upload not yet implemented');
        // await this.uploadPhotos(profile.id);
      }

      // Navigate to swipe page
      this.router.navigate(['/swipe']);
    } catch (error: any) {
      console.error('Profile creation error:', error);
      this.generalError.set(error?.error?.message || 'Greška pri kreiranju profila. Pokušaj ponovno.');
    } finally {
      this.isLoading.set(false);
    }
  }

  // TODO: Implement photo upload
  // private async uploadPhotos(profileId: string) {
  //   const formData = new FormData();
  //   this.photos().forEach((photo, index) => {
  //     if (photo.file) {
  //       formData.append('files', photo.file);
  //       formData.append('isPrimary', photo.isPrimary ? 'true' : 'false');
  //       formData.append('displayOrder', index.toString());
  //     }
  //   });
  //   await firstValueFrom(
  //     this.http.post(`/api/profiles/${profileId}/photos`, formData)
  //   );
  // }
}
