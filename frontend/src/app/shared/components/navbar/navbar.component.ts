import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ThemeSwitcher } from '../theme-switcher/theme-switcher.component';
import { LucideAngularModule, LogOut } from 'lucide-angular';
import { AuthService } from '@shared/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [ThemeSwitcher, LucideAngularModule, RouterLink],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  readonly LogOut = LogOut;

  constructor(private router: Router, private authService: AuthService) {}

  async logout() {
    await this.authService.signOut();
    this.router.navigate(['/login']);
  }
}
