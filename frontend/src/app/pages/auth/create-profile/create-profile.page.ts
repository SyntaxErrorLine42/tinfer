import { Component, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '@shared/services/auth.service';
import { PhotoService } from '@shared/services/photo.service';
import { InterestService } from '@shared/services/interest.service';
import { CreateProfileRequest, ProfileResponse } from '@shared/services/profile.service';
import { InputComponent } from '../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../../shared/components/card/card.component';
import { IconComponent } from '../../../shared/components/icon-wrapper/icon-wrapper.component';
import { TagInputComponent } from '../../../shared/components/tag-input/tag-input.component';
import { firstValueFrom } from 'rxjs';

interface Photo {
  base64Data: string; // Base64 encoded image
  isPrimary: boolean;
  preview: string; // Object URL for preview
  file: File;
}

@Component({
  selector: 'app-create-profile',
  imports: [InputComponent, ButtonComponent, CardComponent, IconComponent, TagInputComponent],
  templateUrl: './create-profile.page.html',
  styleUrl: './create-profile.page.css',
})
export class CreateProfilePage {
  private router = inject(Router);
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private photoService = inject(PhotoService);
  private interestService = inject(InterestService);

  // Form fields
  firstName = signal('');
  lastName = signal('');
  displayName = signal('');
  bio = signal('');
  yearOfStudy = signal<number | null>(null);
  studentId = signal('');
  gender = signal('');
  interests = signal<string[]>([]);
  interestedInGender = signal('');
  photos = signal<Photo[]>([]);

  // Errors
  firstNameError = signal('');
  lastNameError = signal('');
  yearOfStudyError = signal('');
  genderError = signal('');
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
      this.yearOfStudyError.set('Year must be between 1 and 5');
    } else {
      this.yearOfStudy.set(num);
      this.yearOfStudyError.set('');
    }
  }

  onStudentIdChange(value: string) {
    this.studentId.set(value);
  }

  onGenderChange(value: string) {
    this.gender.set(value);
    this.genderError.set('');
  }

  onInterestedInGenderChange(value: string) {
    this.interestedInGender.set(value);
  }
  onInterestsChange(tags: string[]) {
    this.interests.set(tags);
  }


  async onPhotoSelect(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    // Validate image
    const validation = this.photoService.validateImage(file);
    if (!validation.valid) {
      this.generalError.set(validation.error || 'Invalid image');
      return;
    }

    // Check max 6 photos
    if (this.photos().length >= 6) {
      this.generalError.set('You can add a maximum of 6 photos');
      return;
    }

    try {
      // Convert to Base64
      const base64Data = await this.photoService.fileToBase64(file);

      // Create preview URL
      const preview = URL.createObjectURL(file);
      const newPhotos = [...this.photos()];

      newPhotos.push({
        base64Data,
        preview,
        isPrimary: newPhotos.length === 0, // First photo is primary
        file,
      });

      this.photos.set(newPhotos);
      this.generalError.set('');
    } catch (error) {
      this.generalError.set('Error loading image');
    }

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
      this.firstNameError.set('First name is required');
      hasError = true;
    }

    if (!this.lastName().trim()) {
      this.lastNameError.set('Last name is required');
      hasError = true;
    }

    if (!this.gender()) {
      this.genderError.set('Gender is required');
      hasError = true;
    }

    if (hasError) return;

    this.isLoading.set(true);

    try {
      // Get current user email
      const user = await this.authService.getCurrentUser();
      if (!user?.email) {
        throw new Error('User not logged in');
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
        gender: this.gender(),
        interestedInGender: this.interestedInGender() || undefined,
      };

      // Create profile
      const profile = await firstValueFrom(
        this.http.post<ProfileResponse>('/api/profiles', profileData)
      );

      // Set interests if any
      if (this.interests().length > 0) {
        try {
          await firstValueFrom(
            this.interestService.setInterests(this.interests())
          );
        } catch (error) {
          console.error('Failed to save interests:', error);
          // Continue even if interests fail
        }
      }

      // Upload photos if any
      if (this.photos().length > 0) {
        await this.uploadPhotos();
      }

      // Navigate to swipe page
      this.router.navigate(['/swipe']);
    } catch (error: any) {
      console.error('Profile creation error:', error);
      this.generalError.set(error?.error?.message || 'Error creating profile. Please try again.');
    } finally {
      this.isLoading.set(false);
    }
  }

  /**
   * Upload photos to backend
   */
  private async uploadPhotos() {
    const photos = this.photos();

    for (let i = 0; i < photos.length; i++) {
      const photo = photos[i];

      try {
        await this.photoService.addPhoto({
          base64Data: photo.base64Data,
          displayOrder: i,
          isPrimary: photo.isPrimary,
        });
      } catch (error) {
        console.error('Failed to upload photo:', error);
        // Continue with other photos even if one fails
      }
    }
  }
}
