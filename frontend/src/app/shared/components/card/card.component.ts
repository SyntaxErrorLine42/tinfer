import { Component, input } from '@angular/core';
import { cn } from '../../utils/cn';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-card',
  imports: [NgClass],
  template: `
    <div [ngClass]="cardClasses()">
      <ng-content />
    </div>
  `,
})
export class CardComponent {
  class = input<string>('');

  cardClasses() {
    return cn(
      'rounded-xl border border-border bg-card text-card-foreground shadow-sm',
      'transition-all duration-200',
      this.class()
    );
  }
}

