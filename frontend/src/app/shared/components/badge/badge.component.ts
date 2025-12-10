import { Component, input } from '@angular/core';
import { cn } from '../../utils/cn';
import { NgClass } from '@angular/common';

type BadgeVariant = 'default' | 'secondary' | 'destructive' | 'outline' | 'success';

@Component({
  selector: 'app-badge',
  imports: [NgClass],
  template: `
    <div [ngClass]="badgeClasses()">
      <ng-content />
    </div>
  `,
})
export class BadgeComponent {
  variant = input<BadgeVariant>('default');
  class = input<string>('');

  badgeClasses() {
    const variants = {
      default: 'bg-primary text-primary-foreground hover:bg-primary/80',
      secondary: 'bg-secondary text-secondary-foreground hover:bg-secondary/80',
      destructive: 'bg-destructive text-destructive-foreground hover:bg-destructive/80',
      outline: 'border border-input bg-background hover:bg-accent hover:text-accent-foreground',
      success: 'bg-green-500 text-white hover:bg-green-600',
    };

    return cn(
      'inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold',
      'transition-colors duration-200',
      variants[this.variant()],
      this.class()
    );
  }
}

