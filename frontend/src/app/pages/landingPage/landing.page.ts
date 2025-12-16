import { Component } from '@angular/core';
import { LucideAngularModule, Heart, ArrowRight, Users, Shield, MessageCircle, Sparkles } from 'lucide-angular';
import { Footer } from '@shared/components/footer/footer.component';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '@shared/components/navbar/navbar.component';

@Component({
  selector: 'landing-page',
  standalone: true,
  imports: [
    Footer,
    LucideAngularModule,
    RouterLink,
    NavbarComponent
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
