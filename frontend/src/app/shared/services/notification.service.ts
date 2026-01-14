import { Injectable, inject, signal, NgZone } from '@angular/core';
import { AuthService } from './auth.service';

export interface MatchNotification {
  matchId: number;
  conversationId: number;
  matchedUserId: string;
  matchedUserName: string;
  matchedUserPhotoUrl: string | null;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private authService = inject(AuthService);
  private ngZone = inject(NgZone);
  
  private eventSource: EventSource | null = null;
  
  // Signal for latest match notification (consumed by swipe page)
  latestMatch = signal<MatchNotification | null>(null);
  isConnected = signal(false);

  /**
   * Connect to SSE stream for real-time notifications.
   * Call this when user logs in or app initializes.
   */
  async connect(): Promise<void> {
    // Don't connect if already connected
    if (this.eventSource) {
      return;
    }

    const session = await this.authService.getSession();
    if (!session?.access_token) {
      console.warn('Cannot connect to notifications: no auth token');
      return;
    }

    // Build URL with auth token as query param (EventSource doesn't support headers)
    // Use relative path so Angular proxy works in development
    const url = `/api/notifications/stream?token=${encodeURIComponent(session.access_token)}`;

    // Create EventSource
    this.eventSource = new EventSource(url);

    this.eventSource.onopen = () => {
      this.ngZone.run(() => {
        this.isConnected.set(true);
        console.log('Connected to notification stream');
      });
    };

    // Listen for match events
    this.eventSource.addEventListener('match', (event) => {
      this.ngZone.run(() => {
        try {
          const notification: MatchNotification = JSON.parse(event.data);
          console.log('Received match notification:', notification);
          this.latestMatch.set(notification);
        } catch (e) {
          console.error('Failed to parse match notification:', e);
        }
      });
    });

    // Listen for connection confirmation
    this.eventSource.addEventListener('connected', (event) => {
      console.log('SSE connection confirmed:', event.data);
    });

    this.eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      this.ngZone.run(() => {
        this.isConnected.set(false);
      });
      // Attempt to reconnect after 5 seconds
      this.disconnect();
      setTimeout(() => this.connect(), 5000);
    };
  }

  /**
   * Disconnect from SSE stream.
   * Call this when user logs out.
   */
  disconnect(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
      this.isConnected.set(false);
      console.log('Disconnected from notification stream');
    }
  }

  /**
   * Clear the latest match notification (after it's been shown).
   */
  clearLatestMatch(): void {
    this.latestMatch.set(null);
  }
}
