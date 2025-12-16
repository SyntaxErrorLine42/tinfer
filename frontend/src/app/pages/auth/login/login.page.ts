import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@shared/services/auth.service';
import { ProfileService } from '@shared/services/profile.service';
import { InputComponent } from '../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../../shared/components/card/card.component';
import { IconComponent } from '../../../shared/components/icon-wrapper/icon-wrapper.component';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [InputComponent, ButtonComponent, CardComponent, IconComponent, RouterLink],
  templateUrl: './login.page.html',
  styleUrl: './login.page.css',
})
export class LoginPage {
  email = signal('');
  password = signal('');
  emailError = signal('');
  passwordError = signal('');
  isLoading = signal(false);

  constructor(
    private router: Router,
    private authService: AuthService,
    private profileService: ProfileService
  ) {}

  onEmailChange(value: string) {
    this.email.set(value);
    this.emailError.set('');
  }

  onPasswordChange(value: string) {
    this.password.set(value);
    this.passwordError.set('');
  }

  async onSubmit() {
    // Validation
    let hasError = false;

    if (!this.email()) {
      this.emailError.set('Email is required');
      hasError = true;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email())) {
      this.emailError.set('Please enter a valid email');
      hasError = true;
    }

    if (!this.password()) {
      this.passwordError.set('Password is required');
      hasError = true;
    } else if (this.password().length < 6) {
      this.passwordError.set('Password must be at least 6 characters');
      hasError = true;
    }

    if (hasError) return;

    this.isLoading.set(true);
    this.emailError.set('');
    this.passwordError.set('');

    try {
      await this.authService.login({
        email: this.email(),
        password: this.password(),
      });

      // Provjeri postoji li profil
      this.profileService
        .getMyProfile()
        .pipe(
          catchError((error) => {
            // Ako dobijemo 404 ili bilo koji error, korisnik nema profil
            console.log('Profile not found, redirecting to create profile');
            return of(null);
          })
        )
        .subscribe((profile) => {
          if (profile) {
            // Profil postoji, idi na home
            this.router.navigate(['/home']);
          } else {
            // Profil ne postoji, idi na kreiranje profila
            this.router.navigate(['/create-profile']);
          }
        });
    } catch (error: any) {
      const message = error?.message ?? 'Login failed. Please check your credentials.';

      if (message.toLowerCase().includes('email')) {
        this.emailError.set(message);
      } else {
        this.passwordError.set(message);
      }
      this.isLoading.set(false);
    }
  }

  async socialLogin(provider: string) {
    // ... treba implementirati social login
  }
}

