import { Component, input } from '@angular/core';
import { ZardIconComponent } from '../icon/icon.component';
import { ZardIcon } from '../icon/icons';

@Component({
  selector: 'app-icon',
  imports: [ZardIconComponent],
  template: `
    <z-icon 
      [zType]="name()" 
      [zSize]="getZardSize()"
      [class]="class()"
    />
  `,
})
export class IconComponent {
  name = input.required<ZardIcon>();
  size = input<number>(24);
  class = input<string>('');

  getZardSize(): 'sm' | 'default' | 'lg' | 'xl' {
    const sizeNum = this.size();
    if (sizeNum <= 16) return 'sm';
    if (sizeNum <= 24) return 'default';
    if (sizeNum <= 32) return 'lg';
    return 'xl';
  }
}

