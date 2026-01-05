import { Component, input, output, forwardRef } from '@angular/core';
import { cn } from '../../utils/cn';
import { FormsModule, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-input',
  imports: [FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputComponent),
      multi: true
    }
  ],
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
        [value]="internalValue"
        (input)="onInput($event)"
        (blur)="onTouched()"
        [class]="inputClasses()"
      />
      @if (error()) {
        <p class="mt-1 text-sm text-destructive">{{ error() }}</p>
      }
    </div>
  `,
})
export class InputComponent implements ControlValueAccessor {
  // Input signals for traditional binding
  id = input<string>('input-' + Math.random().toString(36).substr(2, 9));
  type = input<string>('text');
  placeholder = input<string>('');
  label = input<string>('');
  error = input<string>('');
  disabled = input<boolean>(false);
  value = input<string>('');
  
  // Output for traditional binding
  valueChange = output<string>();

  // Internal value for both ControlValueAccessor and traditional binding
  internalValue: any = '';
  
  // ControlValueAccessor callbacks
  onChange: any = () => {};
  onTouched: any = () => {};

  ngOnInit() {
    // Initialize from value input if provided
    if (this.value()) {
      this.internalValue = this.value();
    }
  }

  onInput(event: Event) {
    const inputValue = (event.target as HTMLInputElement).value;
    this.internalValue = this.type() === 'number' ? (inputValue ? Number(inputValue) : null) : inputValue;
    
    // Notify ControlValueAccessor (for ngModel)
    this.onChange(this.internalValue);
    
    // Notify traditional output binding
    this.valueChange.emit(this.internalValue);
  }

  // ControlValueAccessor implementation
  writeValue(value: any): void {
    this.internalValue = value ?? '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    // Handle disabled state if needed
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

