import { Component, signal, OnInit, inject } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { InputComponent } from '../../shared/components/input/input.component';
import { ProfileService, ProfileDetailsResponse, UpdateProfileRequest } from '@shared/services/profile.service';

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
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  profile = signal<ProfileDetailsResponse | null>(null);
  isLoading = signal(true);
  isEditing = signal(false);
  error = signal<string | null>(null);

  // Edit form data (not using signal for form fields to work with ngModel)
  editForm = {
    firstName: '',
    lastName: '',
    displayName: '',
    bio: '',
    yearOfStudy: null as number | null,
    studentId: '',
  };

  ngOnInit() {
    this.loadProfile();
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

  editPhoto(index: number) {
    console.log('Edit photo', index);
    // TODO: Implement photo edit (skipped as requested)
  }

  addPhoto() {
    console.log('Add photo clicked');
    // TODO: Implement add photo (skipped as requested)
  }

  getInterestsByCategory(category: string) {
    const currentProfile = this.profile();
    if (!currentProfile || !currentProfile.interests) return [];
    return currentProfile.interests.filter((interest) => interest.category === category);
  }

  hasInterestsInCategory(category: string): boolean {
    return this.getInterestsByCategory(category).length > 0;
  }
}

