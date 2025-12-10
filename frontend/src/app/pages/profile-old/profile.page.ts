import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import { InputComponent } from '../../shared/components/input/input.component';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { ProfileDetailsResponse, ProfileService, ProfileResponse } from '../../shared/services/profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, CardComponent, AvatarComponent, InputComponent, ButtonComponent, IconComponent],
  templateUrl: './profile.page.html',
  styleUrl: './profile.page.css',
})
export class ProfilePage implements OnInit {
  profile = signal<ProfileDetailsResponse | null>(null);
  isEditing = signal(false);
  isLoading = signal(false);
  isSaving = signal(false);

  // form fields
  firstName = signal('');
  lastName = signal('');
  displayName = signal('');
  bio = signal('');
  yearOfStudy = signal<string>('');
  studentId = signal('');

  readonly initials = computed(() => {
    const p = this.profile();
    if (!p) return '';
    const first = p.firstName?.charAt(0) ?? '';
    const last = p.lastName?.charAt(0) ?? '';
    return `${first}${last}`.toUpperCase();
  });

  constructor(private profileService: ProfileService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile() {
    this.isLoading.set(true);

    this.profileService.getMyProfile().subscribe({
      next: (basic: ProfileResponse) => {
        this.profileService.getProfileDetails(basic.id).subscribe({
          next: (details) => {
            this.profile.set(details);
            this.syncFormWithProfile();
            this.isLoading.set(false);
          },
          error: () => {
            this.isLoading.set(false);
          },
        });
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  syncFormWithProfile() {
    const p = this.profile();
    if (!p) return;
    this.firstName.set(p.firstName ?? '');
    this.lastName.set(p.lastName ?? '');
    this.displayName.set(p.displayName ?? '');
    this.bio.set(p.bio ?? '');
    this.yearOfStudy.set(p.yearOfStudy ? String(p.yearOfStudy) : '');
    this.studentId.set(p.studentId ?? '');
  }

  enterEditMode() {
    this.syncFormWithProfile();
    this.isEditing.set(true);
  }

  cancelEdit() {
    this.isEditing.set(false);
    this.syncFormWithProfile();
  }

  saveProfile() {
    const p = this.profile();
    if (!p) return;

    const payload = {
      email: p.email,
      firstName: this.firstName(),
      lastName: this.lastName(),
      displayName: this.displayName() || undefined,
      bio: this.bio() || undefined,
      yearOfStudy: this.yearOfStudy() ? Number(this.yearOfStudy()) : null,
      studentId: this.studentId() || undefined,
    };

    this.isSaving.set(true);
    this.profileService.updateProfile(p.id, payload).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.isEditing.set(false);
        this.loadProfile();
      },
      error: () => {
        this.isSaving.set(false);
      },
    });
  }

  primaryPhotoUrl(): string {
    const p = this.profile();
    if (!p || !p.photos || p.photos.length === 0) {
      return '';
    }

    const primary = p.photos.find((photo) => photo.isPrimary);
    return primary?.url ?? p.photos[0].url;
  }
}
