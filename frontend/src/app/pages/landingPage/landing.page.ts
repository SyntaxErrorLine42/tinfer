import { Component } from '@angular/core';
import { ThemeSwitcher } from '@shared/components/theme-switcher/theme-switcher.component';

@Component({
  selector: 'landing-page',
  standalone: true,
  imports: [ThemeSwitcher],
  templateUrl: './landing.page.html',
  styleUrl: './landing.page.css'
})
export class LandingPage {

}
