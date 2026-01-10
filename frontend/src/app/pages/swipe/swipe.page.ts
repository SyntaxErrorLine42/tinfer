import { Component, signal, ViewChild, ElementRef, AfterViewInit, AfterViewChecked, OnInit, OnDestroy, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import { NgStyle, DecimalPipe } from '@angular/common';
import { SwipeService, ProfileRecommendation, SwipeResponse } from '@shared/services/swipe.service';
import { ProfileService } from '@shared/services/profile.service';
import { ConversationService } from '@shared/services/conversation.service';
import { ReportDialogComponent } from '../../shared/components/report-dialog/report-dialog.component';
import { ReportService, ReportReason } from '@shared/services/report.service';

// Extended profile for UI state (adds currentPhotoIndex for photo navigation)
interface SwipeProfile extends ProfileRecommendation {
  currentPhotoIndex: number;
}

@Component({
  selector: 'app-swipe',
  imports: [
    ButtonComponent,
    CardComponent,
    IconComponent,
    AvatarComponent,
    NgStyle,
    DecimalPipe,
    ReportDialogComponent
  ],
  templateUrl: './swipe.page.html',
  styleUrl: './swipe.page.css',
})
export class SwipePage implements OnInit, AfterViewInit, AfterViewChecked, OnDestroy {
  @ViewChild('swipeCard') swipeCard?: ElementRef<HTMLDivElement>;

  private swipeService = inject(SwipeService);
  private profileService = inject(ProfileService);
  private conversationService = inject(ConversationService);
  private reportService = inject(ReportService);
  private router = inject(Router);

  // State
  profiles = signal<SwipeProfile[]>([]);
  totalUnreadCount = signal(0);
  currentIndex = signal(0);
  isLoading = signal(true);
  error = signal<string | null>(null);
  currentUserPhoto = signal<string | null>(null);

  // Drag state
  isDragging = signal(false);
  dragX = signal(0);
  dragY = signal(0);
  rotation = signal(0);

  // Match modal state
  showMatchModal = signal(false);
  lastMatchedProfile = signal<SwipeProfile | null>(null);
  lastMatchConversationId = signal<number | null>(null);

  // Report state
  showReportDialog = signal(false);

  private startX = 0;
  private startY = 0;
  private isProcessingSwipe = false;
  private gestureListenersSetup = false;
  private boundMouseMove: ((e: MouseEvent) => void) | null = null;
  private boundMouseUp: (() => void) | null = null;

  ngOnInit() {
    this.loadRecommendations();
    this.loadCurrentUserPhoto();
    this.loadUnreadCount();
  }

  ngAfterViewInit() {
    this.setupGestureListeners();
  }

  ngAfterViewChecked() {
    // Re-setup gesture listeners if card exists but listeners not set up yet
    if (!this.gestureListenersSetup && this.swipeCard) {
      this.setupGestureListeners();
    }
  }

  ngOnDestroy() {
    // Clean up document event listeners
    if (this.boundMouseMove) {
      document.removeEventListener('mousemove', this.boundMouseMove);
    }
    if (this.boundMouseUp) {
      document.removeEventListener('mouseup', this.boundMouseUp);
    }
  }

  get currentProfile(): SwipeProfile | null {
    return this.profiles()[this.currentIndex()] || null;
  }

  /**
   * Load total unread message count from all conversations
   */
  loadUnreadCount() {
    this.conversationService.getConversations().subscribe({
      next: (conversations) => {
        const total = conversations.reduce((sum, conv) => sum + conv.unreadCount, 0);
        this.totalUnreadCount.set(total);
      },
      error: (err) => {
        console.error('Failed to load unread count:', err);
      }
    });
  }

  /**
   * Load profile recommendations from backend
   */
  loadRecommendations() {
    this.isLoading.set(true);
    this.error.set(null);

    this.swipeService.getRecommendations(25).subscribe({
      next: (recommendations) => {
        // Convert to SwipeProfile by adding currentPhotoIndex
        const swipeProfiles: SwipeProfile[] = recommendations.map(rec => ({
          ...rec,
          currentPhotoIndex: 0
        }));
        this.profiles.set(swipeProfiles);
        this.currentIndex.set(0);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load recommendations:', err);
        this.error.set('Failed to load profiles. Please try again.');
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Load current user's photo for match modal
   */
  loadCurrentUserPhoto() {
    this.profileService.getMyProfile().subscribe({
      next: () => {
        // Try to get photos
        this.profileService.getMyPhotos().subscribe({
          next: (photos) => {
            const primaryPhoto = photos.find(p => p.isPrimary) || photos[0];
            if (primaryPhoto) {
              // PhotoResponse now has imageUrl field
              this.currentUserPhoto.set(primaryPhoto.imageUrl);
            }
          },
          error: () => {
            // Ignore photo loading errors
          }
        });
      },
      error: () => {
        // Ignore profile loading errors
      }
    });
  }

  setupGestureListeners() {
    if (!this.swipeCard || this.gestureListenersSetup) return;

    const card = this.swipeCard.nativeElement;
    let isTouching = false;

    // Create bound functions for cleanup
    this.boundMouseMove = (e: MouseEvent) => {
      if (!isTouching) return;
      const deltaX = e.clientX - this.startX;
      const deltaY = e.clientY - this.startY;
      this.dragX.set(deltaX);
      this.dragY.set(deltaY);
      this.rotation.set(deltaX / 20);
    };

    this.boundMouseUp = () => {
      if (!isTouching) return;
      isTouching = false;
      this.handleDragEnd();
    };

    // Mouse events
    card.addEventListener('mousedown', (e: MouseEvent) => {
      isTouching = true;
      this.startX = e.clientX;
      this.startY = e.clientY;
      this.isDragging.set(true);
    });

    document.addEventListener('mousemove', this.boundMouseMove);
    document.addEventListener('mouseup', this.boundMouseUp);

    // Touch events
    card.addEventListener('touchstart', (e: TouchEvent) => {
      this.startX = e.touches[0].clientX;
      this.startY = e.touches[0].clientY;
      this.isDragging.set(true);
    });

    card.addEventListener('touchmove', (e: TouchEvent) => {
      const deltaX = e.touches[0].clientX - this.startX;
      const deltaY = e.touches[0].clientY - this.startY;
      this.dragX.set(deltaX);
      this.dragY.set(deltaY);
      this.rotation.set(deltaX / 20);
    });

    card.addEventListener('touchend', () => {
      this.handleDragEnd();
    });

    this.gestureListenersSetup = true;
  }

  handleDragEnd() {
    const threshold = 100;
    const dragXValue = this.dragX();

    if (Math.abs(dragXValue) > threshold) {
      if (dragXValue > 0) {
        this.animateSwipe('right');
        this.like();
      } else {
        this.animateSwipe('left');
        this.pass();
      }
    } else {
      this.resetCard();
    }
  }

  animateSwipe(direction: 'left' | 'right') {
    const distance = direction === 'right' ? 1000 : -1000;
    this.dragX.set(distance);
    this.rotation.set(distance / 20);

    setTimeout(() => {
      this.nextProfile();
      this.resetCard();
    }, 300);
  }

  resetCard() {
    this.isDragging.set(false);
    this.dragX.set(0);
    this.dragY.set(0);
    this.rotation.set(0);
  }

  nextProfile() {
    if (this.currentIndex() < this.profiles().length - 1) {
      this.currentIndex.update((i) => i + 1);
    } else {
      // All profiles viewed - try to load more
      this.loadRecommendations();
    }
  }

  /**
   * Handle swipe response from backend
   */
  private handleSwipeResponse(response: SwipeResponse, profile: SwipeProfile) {
    if (response.matchCreated) {
      this.lastMatchedProfile.set(profile);
      this.lastMatchConversationId.set(response.conversationId);
      setTimeout(() => this.showMatchModal.set(true), 400);
    }
  }

  like() {
    const profile = this.currentProfile;
    if (!profile || this.isProcessingSwipe) return;

    this.isProcessingSwipe = true;
    console.log('Liking:', profile.displayName || profile.firstName);

    this.swipeService.like(profile.profileId).subscribe({
      next: (response) => {
        this.handleSwipeResponse(response, profile);
        this.isProcessingSwipe = false;
      },
      error: (err) => {
        console.error('Swipe failed:', err);
        this.isProcessingSwipe = false;
      }
    });
  }

  pass() {
    const profile = this.currentProfile;
    if (!profile || this.isProcessingSwipe) return;

    this.isProcessingSwipe = true;
    console.log('Passing:', profile.displayName || profile.firstName);

    this.swipeService.pass(profile.profileId).subscribe({
      next: () => {
        this.isProcessingSwipe = false;
      },
      error: (err) => {
        console.error('Swipe failed:', err);
        this.isProcessingSwipe = false;
      }
    });
  }

  superLike() {
    const profile = this.currentProfile;
    if (!profile || this.isProcessingSwipe) return;

    this.isProcessingSwipe = true;
    console.log('Super liking:', profile.displayName || profile.firstName);

    this.swipeService.superLike(profile.profileId).subscribe({
      next: (response) => {
        this.handleSwipeResponse(response, profile);
        this.animateSwipe('right');
        this.isProcessingSwipe = false;
      },
      error: (err) => {
        console.error('Swipe failed:', err);
        this.isProcessingSwipe = false;
      }
    });
  }

  rewind() {
    if (this.currentIndex() > 0) {
      this.currentIndex.update((i) => i - 1);
    }
  }

  nextPhoto() {
    const profile = this.currentProfile;
    if (profile && profile.photoGalleryUrls && profile.currentPhotoIndex < profile.photoGalleryUrls.length - 1) {
      profile.currentPhotoIndex++;
    }
  }

  prevPhoto() {
    const profile = this.currentProfile;
    if (profile && profile.currentPhotoIndex > 0) {
      profile.currentPhotoIndex--;
    }
  }

  /**
   * Get current photo URL for display
   */
  getCurrentPhoto(profile: SwipeProfile): string | null {
    if (profile.photoGalleryUrls && profile.photoGalleryUrls.length > 0) {
      return profile.photoGalleryUrls[profile.currentPhotoIndex] || profile.primaryPhotoUrl;
    }
    return profile.primaryPhotoUrl;
  }

  /**
   * Get all photos for a profile
   */
  getPhotos(profile: SwipeProfile): string[] {
    if (profile.photoGalleryUrls && profile.photoGalleryUrls.length > 0) {
      return profile.photoGalleryUrls;
    }
    if (profile.primaryPhotoUrl) {
      return [profile.primaryPhotoUrl];
    }
    return [];
  }

  closeMatchModal() {
    this.showMatchModal.set(false);
  }

  sendMessage() {
    this.showMatchModal.set(false);
    const conversationId = this.lastMatchConversationId();
    if (conversationId) {
      // Navigate to conversation (will implement conversations page later)
      this.router.navigate(['/conversations'], { queryParams: { id: conversationId } });
    }
  }

  keepSwiping() {
    this.showMatchModal.set(false);
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }

  goToConversations() {
    this.router.navigate(['/conversations']);
  }

  openReportDialog() {
    this.showReportDialog.set(true);
  }

  closeReportDialog() {
    this.showReportDialog.set(false);
  }

  handleReportSubmit(data: { reason: ReportReason; description: string }) {
    const profile = this.currentProfile;
    if (!profile) return;

    this.reportService.reportUser({
      reportedId: profile.profileId,
      reason: data.reason,
      description: data.description
    }).subscribe({
      next: () => {
        console.log('User reported successfully');
        this.closeReportDialog();
        // Since backend marks it as passed, we should just move to next profile locally
        this.nextProfile();
      },
      error: (err) => {
        console.error('Failed to report user:', err);
        this.closeReportDialog();
      }
    });
  }
}

