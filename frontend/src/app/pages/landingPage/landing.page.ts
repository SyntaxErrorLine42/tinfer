import { Component } from '@angular/core';
import { LucideAngularModule, Heart, ArrowRight, Users, Shield, MessageCircle, Sparkles } from 'lucide-angular';
import { ThemeSwitcher } from '@shared/components/theme-switcher/theme-switcher.component';
import { Footer } from '@shared/components/footer/footer.component';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'landing-page',
  standalone: true,
  imports: [
    ThemeSwitcher,
    Footer,
    LucideAngularModule,
    RouterLink
  ],
  templateUrl: './landing.page.html',
  styleUrls: ['./landing.page.css']
})
export class LandingPage {
  // Assign icons to variables for template usage
  readonly Heart = Heart;
  readonly ArrowRight = ArrowRight;
  readonly Users = Users;
  readonly Shield = Shield;
  readonly MessageCircle = MessageCircle;
  readonly Sparkles = Sparkles;
}
