import { Component } from '@angular/core';
import { LucideAngularModule, Heart, ArrowRight, Users, Shield, MessageCircle, Sparkles } from 'lucide-angular';

@Component({
  selector: 'test',
  standalone: true,
  imports: [
    LucideAngularModule
  ],
  templateUrl: './test.page.html',
  styleUrls: ['./test.page.css']
})
export class TestPage {
  // Assign icons to variables for template usage
  readonly Heart = Heart;
  readonly ArrowRight = ArrowRight;
  readonly Users = Users;
  readonly Shield = Shield;
  readonly MessageCircle = MessageCircle;
  readonly Sparkles = Sparkles;
}

