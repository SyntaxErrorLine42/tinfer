import { Component, input, output } from '@angular/core';
import { ZardButtonComponent } from '../button/button.component';

type ButtonVariant = 'default' | 'outline' | 'destructive' | 'ghost' | 'link';

@Component({
  selector: 'app-button',
  imports: [ZardButtonComponent],
  template: `
    <button 
      z-button
      [zType]="getZardType()"
      [zLoading]="disabled()"
      [class]="class()"
      [disabled]="disabled()"
      [type]="type()"
      (click)="handleClick($event)"
    >
      <ng-content />
    </button>
  `,
})
export class ButtonComponent {
  variant = input<ButtonVariant>('default');
  type = input<'button' | 'submit' | 'reset'>('button');
  disabled = input<boolean>(false);
  class = input<string>('');
  click = output<MouseEvent>();

  handleClick(event: MouseEvent) {
    event.stopPropagation();
    if (!this.disabled()) {
      this.click.emit(event);
    }
  }

  getZardType(): 'default' | 'outline' | 'destructive' | 'ghost' | 'link' {
    return this.variant();
  }
}

