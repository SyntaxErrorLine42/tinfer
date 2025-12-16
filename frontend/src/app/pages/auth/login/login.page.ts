import { Component, signal, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@shared/services/auth.service';
import { ProfileInitService } from '@shared/services/profile-init.service';
import { InputComponent } from '../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../../shared/components/card/card.component';
import { IconComponent } from '../../../shared/components/icon-wrapper/icon-wrapper.component';

@Component({
  selector: 'app-login',
  imports: [InputComponent, ButtonComponent, CardComponent, IconComponent, RouterLink],
  templateUrl: './login.page.html',
  styleUrl: './login.page.css',
})
export class LoginPage {
  private router = inject(Router);
  private authService = inject(AuthService);
  private profileInitService = inject(ProfileInitService);

  email = signal('');
  password = signal('');
  emailError = signal('');
  passwordError = signal('');
  isLoading = signal(false);

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

      // Ensure profile exists (creates one if needed)
      await this.profileInitService.ensureProfileExists();

      // Navigate to swipe interface after successful login
      this.router.navigate(['/swipe']);
    } catch (error: any) {
      const message = error?.message ?? 'Login failed. Please check your credentials.';

      if (message.toLowerCase().includes('email')) {
        this.emailError.set(message);
      } else {
        this.passwordError.set(message);
      }
    } finally {
      this.isLoading.set(false);
    }
  }

  async socialLogin(provider: string) {
    // ... treba implementirati social login
  }
}

