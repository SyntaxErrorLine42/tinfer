import { Component, input, output, signal, effect, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IconComponent } from '../icon-wrapper/icon-wrapper.component';
import { InterestService, Interest } from '../../services/interest.service';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-tag-input',
  standalone: true,
  imports: [IconComponent, FormsModule],
  template: `
    <div class="space-y-3">
      <!-- Selected Tags -->
      @if (selectedTags().length > 0) {
        <div class="flex flex-wrap gap-2">
          @for (tag of selectedTags(); track tag) {
            <div class="inline-flex items-center gap-2 px-3 py-1.5 bg-pink-100 dark:bg-pink-900/30 text-pink-700 dark:text-pink-300 rounded-full text-sm font-medium border border-pink-200 dark:border-pink-800">
              <span>{{ tag }}</span>
              <button
                type="button"
                (click)="removeTag(tag)"
                [disabled]="disabled()"
                class="hover:bg-pink-200 dark:hover:bg-pink-800 rounded-full p-0.5 transition-colors disabled:opacity-50"
              >
                <app-icon name="x" [size]="14" />
              </button>
            </div>
          }
        </div>
      }

      <!-- Input Field -->
      <div class="relative">
        <input
          #inputEl
          type="text"
          [placeholder]="placeholder()"
          [disabled]="disabled()"
          [(ngModel)]="searchQuery"
          (input)="onSearchChange($event)"
          (keydown.enter)="onEnter($event)"
          (keydown.escape)="clearSuggestions()"
          (focus)="onFocus()"
          (blur)="onBlur()"
          class="w-full px-4 py-3 border-2 border-input bg-background/50 text-foreground rounded-xl focus:outline-none focus:ring-2 focus:ring-pink-500 focus:border-transparent disabled:opacity-50 transition-all"
        />

        <!-- Suggestions Dropdown -->
        @if (showSuggestions() && suggestions().length > 0) {
          <div class="absolute z-10 w-full mt-2 bg-white dark:bg-gray-800 border border-border rounded-xl shadow-lg max-h-60 overflow-y-auto">
            @for (suggestion of suggestions(); track suggestion.id) {
              <button
                type="button"
                (mousedown)="selectSuggestion(suggestion.name)"
                class="w-full px-4 py-2.5 text-left hover:bg-pink-50 dark:hover:bg-pink-900/20 transition-colors flex items-center justify-between group"
              >
                <span class="text-foreground">{{ suggestion.name }}</span>
                @if (suggestion.category) {
                  <span class="text-xs text-muted-foreground px-2 py-0.5 bg-muted rounded-full">
                    {{ suggestion.category }}
                  </span>
                }
              </button>
            }
          </div>
        }
      </div>

      <!-- Helper Text -->
      @if (helperText()) {
        <p class="text-xs text-muted-foreground">{{ helperText() }}</p>
      }

      @if (maxTags() && selectedTags().length >= maxTags()) {
        <p class="text-xs text-amber-600 dark:text-amber-400">
          Maximum {{ maxTags() }} interests
        </p>
      }
    </div>
  `,
  styles: [`
    /* ngModel directive */
    :host ::ng-deep input {
      appearance: none;
    }
  `]
})
export class TagInputComponent {
  private interestService = inject(InterestService);

  // Inputs
  placeholder = input('Add interest...');
  disabled = input(false);
  maxTags = input(10);
  helperText = input('Enter interests and press Enter or select from suggestions');
  selectedTags = input<string[]>([]);

  // Outputs
  selectedTagsChange = output<string[]>();
  tagsChange = output<string[]>();

  // State
  searchQuery = '';
  suggestions = signal<Interest[]>([]);
  showSuggestions = signal(false);

  private searchSubject = new Subject<string>();

  constructor() {
    // Setup debounced search
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      if (query.trim().length >= 2) {
        this.interestService.searchInterests(query).subscribe({
          next: (results) => {
            // Filter out already selected tags
            this.suggestions.set(
              results.filter(interest =>
                !this.selectedTags().includes(interest.name)
              )
            );
          },
          error: (err) => console.error('Failed to search interests:', err)
        });
      } else {
        this.suggestions.set([]);
      }
    });

    // Load all interests on init
    effect(() => {
      if (!this.disabled()) {
        this.interestService.getAllInterests().subscribe({
          next: (interests) => {
            // Store for potential future use
          },
          error: (err) => console.error('Failed to load interests:', err)
        });
      }
    });
  }

  onSearchChange(event: Event) {
    const query = (event.target as HTMLInputElement).value;
    this.searchSubject.next(query);
    this.showSuggestions.set(true);
  }

  onEnter(event: Event) {
    event.preventDefault();
    const trimmed = this.searchQuery.trim();

    if (trimmed && this.canAddTag()) {
      this.addTag(trimmed);
      this.searchQuery = '';
      this.suggestions.set([]);
    }
  }

  onFocus() {
    if (this.searchQuery.trim().length >= 2) {
      this.showSuggestions.set(true);
    }
  }

  onBlur() {
    // Delay to allow click on suggestion
    setTimeout(() => {
      this.showSuggestions.set(false);
    }, 200);
  }

  selectSuggestion(name: string) {
    if (this.canAddTag()) {
      this.addTag(name);
      this.searchQuery = '';
      this.suggestions.set([]);
      this.showSuggestions.set(false);
    }
  }

  addTag(tag: string) {
    const newTags = [...this.selectedTags(), tag];
    this.selectedTagsChange.emit(newTags);
    this.tagsChange.emit(newTags);
  }

  removeTag(tag: string) {
    const newTags = this.selectedTags().filter(t => t !== tag);
    this.selectedTagsChange.emit(newTags);
    this.tagsChange.emit(newTags);
  }

  clearSuggestions() {
    this.suggestions.set([]);
    this.showSuggestions.set(false);
  }

  canAddTag(): boolean {
    return !this.disabled() &&
      (!this.maxTags() || this.selectedTags().length < this.maxTags());
  }
}
