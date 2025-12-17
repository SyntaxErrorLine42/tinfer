import { Component, signal, OnInit, inject } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { InputComponent } from '../../shared/components/input/input.component';
import { ProfileService, ProfileDetailsResponse, UpdateProfileRequest } from '@shared/services/profile.service';
import { PhotoService, PhotoResponse } from '@shared/services/photo.service';
import { AuthService } from '@shared/services/auth.service';

@Component({
  selector: 'app-profile',
  imports: [
    ButtonComponent,
    CardComponent,
    IconComponent,
    BadgeComponent,
    InputComponent,
    FormsModule,
  ],
  templateUrl: './profile.page.html',
  styleUrl: './profile.page.css',
})
export class ProfilePage implements OnInit {
  private profileService = inject(ProfileService);
  private photoService = inject(PhotoService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  profile = signal<ProfileDetailsResponse | null>(null);
  photos = signal<PhotoResponse[]>([]);
  isLoading = signal(true);
  isEditing = signal(false);
  error = signal<string | null>(null);
  photoError = signal<string | null>(null);
  isUploadingPhoto = signal(false);

  // Edit form data (not using signal for form fields to work with ngModel)
  editForm = {
    firstName: '',
    lastName: '',
    displayName: '',
    bio: '',
    yearOfStudy: null as number | null,
    studentId: '',
  };

  // Get primary photo
  getPrimaryPhoto(): string | null {
    const primaryPhoto = this.photos().find(p => p.isPrimary);
    return primaryPhoto?.base64Data || null;
  }

  // Open file picker for changing primary photo
  changePrimaryPhoto() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.onchange = (e: Event) => {
      const target = e.target as HTMLInputElement;
      const file = target.files?.[0];
      if (file) {
        this.uploadAndSetPrimary(file);
      }
    };
    input.click();
  }

  // Upload new photo and set as primary
  private async uploadAndSetPrimary(file: File) {
    this.isUploadingPhoto.set(true);
    this.photoError.set(null);

    try {
      // Validate image
      const validation = await this.photoService.validateImage(file);
      if (!validation.valid) {
        this.photoError.set(validation.error || 'Invalid image');
        this.isUploadingPhoto.set(false);
        return;
      }

      // Convert to base64
      const base64 = await this.photoService.fileToBase64(file);

      // Add photo
      this.photoService.addPhoto({ base64Data: base64, isPrimary: true }).subscribe({
        next: (newPhoto) => {
          console.log('Primary photo uploaded:', newPhoto);
          this.loadPhotos();
          this.isUploadingPhoto.set(false);
        },
        error: (err) => {
          console.error('Error uploading primary photo:', err);
          this.photoError.set(err.error?.message || 'Failed to upload photo');
          this.isUploadingPhoto.set(false);
        },
      });
    } catch (err: any) {
      this.photoError.set(err.message || 'Failed to process image');
      this.isUploadingPhoto.set(false);
    }
  }

  ngOnInit() {
    this.loadProfile();
    this.loadPhotos();
  }

  loadProfile() {
    this.isLoading.set(true);
    this.error.set(null);

    // Check if viewing a specific user's profile or own profile
    const userId = this.route.snapshot.paramMap.get('id');

    const profileObservable = userId
      ? this.profileService.getProfileDetails(userId)
      : this.profileService.getMyProfileDetails();

    profileObservable.subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.isLoading.set(false);
        // Initialize edit form with current values
        this.editForm = {
          firstName: profile.firstName,
          lastName: profile.lastName,
          displayName: profile.displayName || '',
          bio: profile.bio || '',
          yearOfStudy: profile.yearOfStudy || null,
          studentId: profile.studentId || '',
        };
      },
      error: (err) => {
        console.error('Failed to load profile:', err);
        this.error.set('Failed to load profile. Please try again.');
        this.isLoading.set(false);
      },
    });
  }

  loadPhotos() {
    this.photoError.set(null);
    
    this.photoService.getMyPhotos().subscribe({
      next: (photos) => {
        this.photos.set(photos);
      },
      error: (err) => {
        console.error('Failed to load photos:', err);
        this.photoError.set('Failed to load photos.');
      },
    });
  }

  goBack() {
    this.router.navigate(['/swipe']);
  }

  editProfile() {
    this.isEditing.set(true);
  }

  cancelEdit() {
    this.isEditing.set(false);
    // Reset form to current profile values
    const currentProfile = this.profile();
    if (currentProfile) {
      this.editForm = {
        firstName: currentProfile.firstName,
        lastName: currentProfile.lastName,
        displayName: currentProfile.displayName || '',
        bio: currentProfile.bio || '',
        yearOfStudy: currentProfile.yearOfStudy || null,
        studentId: currentProfile.studentId || '',
      };
    }
  }

  saveProfile() {
    const currentProfile = this.profile();
    if (!currentProfile) return;

    this.isLoading.set(true);
    this.error.set(null);

    // Backend expects CreateProfileRequest format (includes email)
    const updateData: UpdateProfileRequest = {
      email: currentProfile.email, // Required by backend
      firstName: this.editForm.firstName,
      lastName: this.editForm.lastName,
      displayName: this.editForm.displayName || undefined,
      bio: this.editForm.bio || undefined,
      yearOfStudy: this.editForm.yearOfStudy || null,
      studentId: this.editForm.studentId || undefined,
    };

    this.profileService.updateProfile(currentProfile.id, updateData).subscribe({
      next: (updatedProfile) => {
        console.log('Profile updated successfully:', updatedProfile);
        // Reload the full profile details
        this.loadProfile();
        this.isEditing.set(false);
      },
      error: (err) => {
        console.error('Failed to update profile:', err);
        this.error.set('Failed to update profile. Please try again.');
        this.isLoading.set(false);
      },
    });
  }

  async addPhoto(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (!file) return;

    // Validate image
    const validation = this.photoService.validateImage(file);
    if (!validation.valid) {
      this.photoError.set(validation.error || 'Nevažeća slika');
      input.value = '';
      return;
    }

    // Check max 6 photos
    if (this.photos().length >= 6) {
      this.photoError.set('Možeš dodati maksimalno 6 slika');
      input.value = '';
      return;
    }

    this.isUploadingPhoto.set(true);
    this.photoError.set(null);

    try {
      // Convert to Base64
      const base64Data = await this.photoService.fileToBase64(file);
      
      // Determine if this should be primary (first photo)
      const isPrimary = this.photos().length === 0;
      
      // Upload to backend
      await this.photoService.addPhoto({
        base64Data,
        displayOrder: this.photos().length,
        isPrimary,
      });

      // Reload photos
      this.loadPhotos();
    } catch (error) {
      console.error('Failed to upload photo:', error);
      this.photoError.set('Greška pri dodavanju slike');
    } finally {
      this.isUploadingPhoto.set(false);
      input.value = '';
    }
  }

  async deletePhoto(photoId: number) {
    if (!confirm('Sigurno želiš obrisati ovu sliku?')) {
      return;
    }

    this.photoError.set(null);

    try {
      await this.photoService.deletePhoto(photoId);
      this.loadPhotos();
    } catch (error) {
      console.error('Failed to delete photo:', error);
      this.photoError.set('Greška pri brisanju slike');
    }
  }

  async setPrimaryPhoto(photoId: number) {
    this.photoError.set(null);

    try {
      // Set this photo as primary
      await this.photoService.updatePhoto(photoId, {
        isPrimary: true,
      });
      
      this.loadPhotos();
    } catch (error) {
      console.error('Failed to set primary photo:', error);
      this.photoError.set('Greška pri postavljanju profilne slike');
    }
  }

  getInterestsByCategory(category: string) {
    const currentProfile = this.profile();
    if (!currentProfile || !currentProfile.interests) return [];
    return currentProfile.interests.filter((interest) => interest.category === category);
  }

  hasInterestsInCategory(category: string): boolean {
    return this.getInterestsByCategory(category).length > 0;
  }

  async logout() {
    await this.authService.signOut();
    this.router.navigate(['/login']);
  }
}

