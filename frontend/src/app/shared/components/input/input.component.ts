import { Component, input, output } from '@angular/core';
import { cn } from '../../utils/cn';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  imports: [FormsModule],
  template: `
    <div class="w-full">
      @if (label()) {
        <label [for]="id()" class="block text-sm font-medium text-foreground mb-2">
          {{ label() }}
        </label>
      }
      <input
        [id]="id()"
        [type]="type()"
        [placeholder]="placeholder()"
        [disabled]="disabled()"
        [(ngModel)]="modelValue"
        (ngModelChange)="onValueChange($event)"
        [class]="inputClasses()"
      />
      @if (error()) {
        <p class="mt-1 text-sm text-destructive">{{ error() }}</p>
      }
    </div>
  `,
})
export class InputComponent {
  id = input<string>('input-' + Math.random().toString(36).substr(2, 9));
  type = input<string>('text');
  placeholder = input<string>('');
  label = input<string>('');
  error = input<string>('');
  disabled = input<boolean>(false);
  value = input<string>('');
  valueChange = output<string>();

  modelValue = '';

  ngOnInit() {
    this.modelValue = this.value();
  }

  onValueChange(value: string) {
    this.valueChange.emit(value);
  }

  inputClasses() {
    return cn(
      'flex h-12 w-full rounded-lg border border-input bg-background px-4 py-2 text-base',
      'ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium',
      'placeholder:text-muted-foreground',
      'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2',
      'disabled:cursor-not-allowed disabled:opacity-50',
      'transition-all duration-200',
      this.error() && 'border-destructive focus-visible:ring-destructive'
    );
  }
}

