import { Component, input } from '@angular/core';
import { cn } from '../../utils/cn';
import { NgClass } from '@angular/common';

type AvatarSize = 'sm' | 'md' | 'lg' | 'xl';

@Component({
  selector: 'app-avatar',
  imports: [NgClass],
  template: `
    <div [ngClass]="avatarClasses()">
      @if (src()) {
        <img [src]="src()" [alt]="alt()" class="h-full w-full object-cover" />
      } @else {
        <div class="flex h-full w-full items-center justify-center bg-muted text-muted-foreground font-semibold">
          {{ initials() }}
        </div>
      }
    </div>
  `,
})
export class AvatarComponent {
  src = input<string>('');
  alt = input<string>('Avatar');
  initials = input<string>('');
  size = input<AvatarSize>('md');
  class = input<string>('');

  avatarClasses() {
    const sizes = {
      sm: 'h-8 w-8 text-xs',
      md: 'h-12 w-12 text-sm',
      lg: 'h-16 w-16 text-base',
      xl: 'h-24 w-24 text-xl',
    };

    return cn(
      'relative flex shrink-0 overflow-hidden rounded-full',
      'ring-2 ring-background',
      sizes[this.size()],
      this.class()
    );
  }
}

