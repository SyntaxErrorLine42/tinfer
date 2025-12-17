import { Component, signal, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@shared/services/auth.service';
import { InputComponent } from '../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../../shared/components/card/card.component';
import { IconComponent } from '../../../shared/components/icon-wrapper/icon-wrapper.component';

@Component({
  selector: 'app-register',
  imports: [InputComponent, ButtonComponent, CardComponent, IconComponent, RouterLink],
  templateUrl: './register.page.html',
  styleUrl: './register.page.css',
})
export class RegisterPage {
  private router = inject(Router);
  private authService = inject(AuthService);

  fullName = signal('');
  email = signal('');
  password = signal('');
  confirmPassword = signal('');
  fullNameError = signal('');
  emailError = signal('');
  passwordError = signal('');
  confirmPasswordError = signal('');
  isLoading = signal(false);
  agreeToTerms = signal(false);

  onFullNameChange(value: string) {
    this.fullName.set(value);
    this.fullNameError.set('');
  }

  onEmailChange(value: string) {
    this.email.set(value);
    this.emailError.set('');
  }

  onPasswordChange(value: string) {
    this.password.set(value);
    this.passwordError.set('');
  }

  onConfirmPasswordChange(value: string) {
    this.confirmPassword.set(value);
    this.confirmPasswordError.set('');
  }

  async onSubmit() {
    // Validation
    let hasError = false;

    if (!this.fullName()) {
      this.fullNameError.set('Full name is required');
      hasError = true;
    } else if (this.fullName().length < 2) {
      this.fullNameError.set('Name must be at least 2 characters');
      hasError = true;
    }

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
    } else if (this.password().length < 8) {
      this.passwordError.set('Password must be at least 8 characters');
      hasError = true;
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(this.password())) {
      this.passwordError.set('Password must contain uppercase, lowercase, and number');
      hasError = true;
    }

    if (!this.confirmPassword()) {
      this.confirmPasswordError.set('Please confirm your password');
      hasError = true;
    } else if (this.password() !== this.confirmPassword()) {
      this.confirmPasswordError.set('Passwords do not match');
      hasError = true;
    }

    if (!this.agreeToTerms()) {
      hasError = true;
    }

    if (hasError) return;

    this.isLoading.set(true);
    this.emailError.set('');
    this.passwordError.set('');

    try {
      await this.authService.register({
        fullName: this.fullName(),
        email: this.email(),
        password: this.password(),
      });

      // After registration, redirect to create profile
      this.router.navigate(['/create-profile']);
    } catch (error: any) {
      const message = error?.message ?? 'Registration failed. Please try again.';

      if (message.toLowerCase().includes('email')) {
        this.emailError.set(message);
      } else {
        this.passwordError.set(message);
      }
    } finally {
      this.isLoading.set(false);
    }
  }

  async socialSignup(provider: string) {
    // treba implementirati social signup
  }
}

