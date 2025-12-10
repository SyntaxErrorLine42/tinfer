import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
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
  email = signal('');
  password = signal('');
  emailError = signal('');
  passwordError = signal('');
  isLoading = signal(false);

  constructor(private router: Router) {}

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

    // Simulate API call
    this.isLoading.set(true);
    await new Promise((resolve) => setTimeout(resolve, 1000));
    this.isLoading.set(false);

    // Navigate to swipe interface
    this.router.navigate(['/tutorial']);
  }

  async socialLogin(provider: string) {
    // ... treba implementirati social login
  }
}

