import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DarkModeService } from '@shared/services/darkmode.service';
import { LucideAngularModule, Sun, Moon } from 'lucide-angular';

@Component({
  selector: 'theme-switcher',
  templateUrl: './theme-switcher.component.html',
  standalone: true,
  imports: [LucideAngularModule, RouterModule ],
})
export class ThemeSwitcher {
  private readonly darkmodeService = inject(DarkModeService);

  readonly Sun = Sun;
  readonly Moon = Moon;

  toggleTheme(): void {
    this.darkmodeService.toggleTheme();
  }

  getCurrentTheme(): 'light' | 'dark' {
    return this.darkmodeService.getCurrentTheme();
  }
}
