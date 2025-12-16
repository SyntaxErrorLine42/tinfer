import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ProfileService, CreateProfileRequest } from '@shared/services/profile.service';
import { AuthService } from '@shared/services/auth.service';
import { InputComponent } from '../../shared/components/input/input.component';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';

@Component({
  selector: 'app-create-profile',
  imports: [InputComponent, ButtonComponent, CardComponent, IconComponent],
  templateUrl: './create-profile.page.html',
  styleUrl: './create-profile.page.css',
})
export class CreateProfilePage {
  firstName = signal('');
  lastName = signal('');
  displayName = signal('');
  bio = signal('');
  yearOfStudy = signal<number | null>(null);
  studentId = signal('');
  photos = signal<{ url: string; isPrimary: boolean; file?: File }[]>([]);

  firstNameError = signal('');
  lastNameError = signal('');
  yearOfStudyError = signal('');
  
  isLoading = signal(false);
  generalError = signal('');
  isUploadingPhoto = signal(false);

  constructor(
    private router: Router,
    private profileService: ProfileService,
    private authService: AuthService
  ) {}

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
    // Limit bio to 500 characters
    if (value.length <= 500) {
      this.bio.set(value);
    }
  }

  onYearOfStudyChange(value: string) {
    const numValue = value ? parseInt(value, 10) : null;
    this.yearOfStudy.set(numValue);
    this.yearOfStudyError.set('');
  }

  onStudentIdChange(value: string) {
    this.studentId.set(value);
  }

  onPhotoSelect(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    
    // Validate file type
    if (!file.type.startsWith('image/')) {
      this.generalError.set('Molimo odaberite sliku (PNG, JPG, itd.)');
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      this.generalError.set('Slika je prevelika. Maksimalna veličina je 5MB.');
      return;
    }

    // Create preview URL
    const reader = new FileReader();
    reader.onload = (e) => {
      const url = e.target?.result as string;
      const currentPhotos = this.photos();
      const isPrimary = currentPhotos.length === 0; // First photo is primary
      
      this.photos.set([...currentPhotos, { url, isPrimary, file }]);
      this.generalError.set('');
    };
    reader.readAsDataURL(file);
    
    // Reset input
    input.value = '';
  }

  removePhoto(index: number) {
    const currentPhotos = this.photos();
    const newPhotos = currentPhotos.filter((_, i) => i !== index);
    
    // If we removed the primary photo, make the first one primary
    if (newPhotos.length > 0 && !newPhotos.some(p => p.isPrimary)) {
      newPhotos[0].isPrimary = true;
    }
    
    this.photos.set(newPhotos);
  }

  setPrimaryPhoto(index: number) {
    const currentPhotos = this.photos();
    const newPhotos = currentPhotos.map((photo, i) => ({
      ...photo,
      isPrimary: i === index
    }));
    this.photos.set(newPhotos);
  }

  async onSubmit() {
    // Validation
    let hasError = false;

    if (!this.firstName()) {
      this.firstNameError.set('Ime je obavezno');
      hasError = true;
    }

    if (!this.lastName()) {
      this.lastNameError.set('Prezime je obavezno');
      hasError = true;
    }

    if (this.yearOfStudy() !== null && (this.yearOfStudy()! < 1 || this.yearOfStudy()! > 5)) {
      this.yearOfStudyError.set('Godina studija mora biti između 1 i 5');
      hasError = true;
    }

    if (hasError) return;

    this.isLoading.set(true);
    this.generalError.set('');

    try {
      const user = await this.authService.getCurrentUser();
      if (!user || !user.email) {
        throw new Error('Korisnik nije prijavljen');
      }

      const payload: CreateProfileRequest = {
        email: user.email,
        firstName: this.firstName(),
        lastName: this.lastName(),
        displayName: this.displayName() || undefined,
        bio: this.bio() || undefined,
        yearOfStudy: this.yearOfStudy() || undefined,
        studentId: this.studentId() || undefined,
      };

      await this.profileService.createProfile(payload).toPromise();

      // Upload photos if any
      if (this.photos().length > 0) {
        this.isUploadingPhoto.set(true);
        
        try {
          // Upload each photo to backend
          for (let i = 0; i < this.photos().length; i++) {
            const photo = this.photos()[i];
            
            // TODO: Ovdje bi trebao biti upload file na cloud storage (npr. Supabase Storage)
            // i dobiti URL. Za sada koristimo Base64 URL iz previewa
            await this.profileService.addPhoto({
              url: photo.url,
              displayOrder: i,
              isPrimary: photo.isPrimary
            }).toPromise();
          }
        } catch (photoError) {
          console.error('Error uploading photos:', photoError);
          // Continue anyway, profile is created
        } finally {
          this.isUploadingPhoto.set(false);
        }
      }

      // Uspješno kreiran profil, redirect na home
      this.router.navigate(['/home']);
    } catch (error: any) {
      console.error('Error creating profile:', error);
      this.generalError.set(
        error?.error?.message || error?.message || 'Greška pri kreiranju profila. Pokušajte ponovno.'
      );
    } finally {
      this.isLoading.set(false);
    }
  }
}
