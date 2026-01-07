import { Component, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonComponent } from '../button-wrapper/button-wrapper.component';
import { CardComponent } from '../card/card.component';
import { IconComponent } from '../icon-wrapper/icon-wrapper.component';
import { ReportReason } from '../../services/report.service';

@Component({
  selector: 'app-report-dialog',
  imports: [
    FormsModule,
    ButtonComponent,
    CardComponent,
    IconComponent
  ],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-300">
      <div class="w-full max-w-md animate-in zoom-in-95 duration-300">
        <app-card class="p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-xl font-bold flex items-center gap-2">
              <app-icon name="flag" [size]="20" class="text-red-500" />
              REPORT USER
            </h2>
            <button (click)="cancel.emit()" class="text-muted-foreground hover:text-foreground">
              <app-icon name="x" [size]="20" />
            </button>
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium mb-1">Report reason</label>
              <select
                [(ngModel)]="reason"
                class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              >
                <option value="HARASSMENT">Harassment</option>
                <option value="INAPPROPRIATE_CONTENT">Inappropriate content</option>
                <option value="FAKE_PROFILE">Fake profile</option>
                <option value="SPAM">Spam</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium mb-1">Description (optional)</label>
              <textarea
                [(ngModel)]="description"
                rows="4"
                class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 resize-none"
                placeholder="Please describe the issue..."
              ></textarea>
            </div>

            <div class="flex gap-3 pt-2">
              <app-button variant="outline" class="flex-1" (click)="cancel.emit()">
                Cancel
              </app-button>
              <app-button
                class="flex-1"
                variant="destructive"
                [disabled]="isSubmitting()"
                (click)="onSubmit()"
              >
                @if (isSubmitting()) {
                  Sending...
                } @else {
                  Report
                }
              </app-button>
            </div>
          </div>
        </app-card>
      </div>
    </div>
  `
})
export class ReportDialogComponent {
  reason = signal<ReportReason>('HARASSMENT');
  description = signal('');
  isSubmitting = signal(false);

  cancel = output<void>();
  submit = output<{ reason: ReportReason; description: string }>();

  onSubmit() {
    if (this.isSubmitting()) return;
    this.isSubmitting.set(true);
    this.submit.emit({
      reason: this.reason(),
      description: this.description()
    });
  }
}
