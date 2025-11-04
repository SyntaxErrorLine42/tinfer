import { Component } from '@angular/core';
import { ThemeSwitcher } from '../theme-switcher/theme-switcher.component';
import { LucideAngularModule, LogOut } from 'lucide-angular';

@Component({
  selector: 'app-navbar',
  imports: [ThemeSwitcher, LucideAngularModule],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  readonly LogOut = LogOut
}
