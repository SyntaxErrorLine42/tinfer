import { Component, signal, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ConversationService, ConversationSummary } from '../../shared/services/conversation.service';
import { RecommendationService, ProfileRecommendation } from '../../shared/services/recommendation.service';
import { SwipeService } from '../../shared/services/swipe.service';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    CardComponent,
    IconComponent,
    AvatarComponent,
  ],
  templateUrl: './home.page.html',
  styleUrl: './home.page.css',
})
export class HomePage implements OnInit {
  private router = inject(Router);
  private conversationService = inject(ConversationService);
  private recommendationService = inject(RecommendationService);
  private swipeService = inject(SwipeService);

  conversations = signal<ConversationSummary[]>([]);
  recommendations = signal<ProfileRecommendation[]>([]);
  currentRecommendation = signal<ProfileRecommendation | null>(null);
  currentPhotoIndex = signal(0);

  // Swipe animation states
  dragX = signal(0);
  dragY = signal(0);
  rotation = signal(0);
  isDragging = signal(false);

  private startX = 0;
  private startY = 0;

  ngOnInit() {
    this.loadConversations();
    this.loadRecommendations();
  }

  loadConversations() {
    this.conversationService.getMyConversations().subscribe({
      next: (conversations) => {
        this.conversations.set(conversations);
      },
      error: (err) => console.error('Error loading conversations:', err),
    });
  }

  loadRecommendations() {
    this.recommendationService.getRecommendations(25).subscribe({
      next: (recommendations) => {
        this.recommendations.set(recommendations);
        if (recommendations.length > 0) {
          this.currentRecommendation.set(recommendations[0]);
        }
      },
      error: (err) => console.error('Error loading recommendations:', err),
    });
  }

  openConversation(conversationId: number) {
    this.router.navigate(['/chat', conversationId]);
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'sada';
    if (diffMins < 60) return `${diffMins}m`;
    if (diffHours < 24) return `${diffHours}h`;
    if (diffDays < 7) return `${diffDays}d`;
    return date.toLocaleDateString('hr-HR', { day: 'numeric', month: 'short' });
  }

  // Swipe functionality
  onMouseDown(event: MouseEvent) {
    this.isDragging.set(true);
    this.startX = event.clientX;
    this.startY = event.clientY;
  }

  onMouseMove(event: MouseEvent) {
    if (!this.isDragging()) return;

    const deltaX = event.clientX - this.startX;
    const deltaY = event.clientY - this.startY;
    const rotation = deltaX * 0.1;

    this.dragX.set(deltaX);
    this.dragY.set(deltaY);
    this.rotation.set(rotation);
  }

  onMouseUp(event: MouseEvent) {
    if (!this.isDragging()) return;
    this.isDragging.set(false);

    const dragDistance = this.dragX();

    if (dragDistance > 100) {
      this.handleSwipe('LIKE');
    } else if (dragDistance < -100) {
      this.handleSwipe('DISLIKE');
    } else {
      this.resetCard();
    }
  }

  handleSwipe(action: 'LIKE' | 'DISLIKE' | 'SUPERLIKE') {
    const current = this.currentRecommendation();
    if (!current) return;

    this.swipeService.swipe({ swipedUserId: current.profileId, action }).subscribe({
      next: (response) => {
        if (response.isMatch) {
          // Show match animation/modal
          console.log('It\'s a match!', response);
          // Refresh conversations to show new match
          this.loadConversations();
        }
        this.nextRecommendation();
      },
      error: (err) => {
        console.error('Error swiping:', err);
        this.resetCard();
      },
    });
  }

  nextRecommendation() {
    const recs = this.recommendations();
    const currentIndex = recs.findIndex((r) => r.profileId === this.currentRecommendation()?.profileId);
    
    if (currentIndex < recs.length - 1) {
      this.currentRecommendation.set(recs[currentIndex + 1]);
      this.currentPhotoIndex.set(0);
    } else {
      this.currentRecommendation.set(null);
      // Load more recommendations
      this.loadRecommendations();
    }
    
    this.resetCard();
  }

  resetCard() {
    this.dragX.set(0);
    this.dragY.set(0);
    this.rotation.set(0);
  }

  onLike() {
    this.handleSwipe('LIKE');
  }

  onDislike() {
    this.handleSwipe('DISLIKE');
  }

  onSuperLike() {
    this.handleSwipe('SUPERLIKE');
  }

  prevPhoto() {
    const current = this.currentRecommendation();
    if (!current) return;
    
    if (this.currentPhotoIndex() > 0) {
      this.currentPhotoIndex.update((i) => i - 1);
    }
  }

  nextPhoto() {
    const current = this.currentRecommendation();
    if (!current) return;
    
    if (this.currentPhotoIndex() < current.photoGallery.length - 1) {
      this.currentPhotoIndex.update((i) => i + 1);
    }
  }
}
