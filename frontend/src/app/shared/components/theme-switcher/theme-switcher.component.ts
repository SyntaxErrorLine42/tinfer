import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DarkModeService } from '@shared/services/darkmode.service';
import { ZardButtonComponent } from '@shared/components/button/button.component';

@Component({
  selector: 'theme-switcher',
  templateUrl: './theme-switcher.component.html',
  standalone: true,
  imports: [RouterModule, ZardButtonComponent /* other imports */],
})
export class ThemeSwitcher {
  private readonly darkmodeService = inject(DarkModeService);

  toggleTheme(): void {
    this.darkmodeService.toggleTheme();
  }

  getCurrentTheme(): 'light' | 'dark' {
    return this.darkmodeService.getCurrentTheme();
  }
}
