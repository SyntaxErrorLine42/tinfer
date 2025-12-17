import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ThemeSwitcher } from '../theme-switcher/theme-switcher.component';
import { LucideAngularModule, LogIn } from 'lucide-angular';

@Component({
  selector: 'app-navbar',
  imports: [ThemeSwitcher, LucideAngularModule, RouterLink],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  readonly LogIn = LogIn;
}
