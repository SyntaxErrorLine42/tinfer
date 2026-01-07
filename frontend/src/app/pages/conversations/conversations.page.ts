import { Component, signal, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { ConversationService, ConversationSummary } from '@shared/services/conversation.service';

@Component({
  selector: 'app-conversations',
  imports: [
    ButtonComponent,
    CardComponent,
    IconComponent,
    AvatarComponent,
    BadgeComponent,
  ],
  templateUrl: './conversations.page.html',
  styleUrl: './conversations.page.css',
})
export class ConversationsPage implements OnInit {
  private conversationService = inject(ConversationService);
  private router = inject(Router);

  conversations = signal<ConversationSummary[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.loadConversations();
  }

  loadConversations() {
    this.isLoading.set(true);
    this.error.set(null);

    this.conversationService.getConversations().subscribe({
      next: (conversations) => {
        // Sort by lastMessageAt descending (most recent first)
        const sorted = conversations.sort((a, b) => {
          if (!a.lastMessageAt && !b.lastMessageAt) return 0;
          if (!a.lastMessageAt) return 1;
          if (!b.lastMessageAt) return -1;
          return new Date(b.lastMessageAt).getTime() - new Date(a.lastMessageAt).getTime();
        });
        this.conversations.set(sorted);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load conversations:', err);
        this.error.set('Could not load conversations. Please try again.');
        this.isLoading.set(false);
      }
    });
  }

  goBack() {
    this.router.navigate(['/swipe']);
  }

  openConversation(conversationId: number) {
    this.router.navigate(['/chat', conversationId]);
  }

  getTimeAgo(dateString: string | null): string {
    if (!dateString) return '';

    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    if (diffInSeconds < 60) {
      return 'Just now';
    } else if (diffInSeconds < 3600) {
      const minutes = Math.floor(diffInSeconds / 60);
      return `${minutes}m ago`;
    } else if (diffInSeconds < 86400) {
      const hours = Math.floor(diffInSeconds / 3600);
      return `${hours}h ago`;
    } else if (diffInSeconds < 604800) {
      const days = Math.floor(diffInSeconds / 86400);
      return `${days}d ago`;
    } else {
      return date.toLocaleDateString('en-US');
    }
  }

  getAvatarSrc(photoBase64: string | null): string {
    if (!photoBase64) {
      return 'https://via.placeholder.com/100?text=?';
    }
    // Check if it's already a data URL or needs to be converted
    if (photoBase64.startsWith('data:')) {
      return photoBase64;
    }
    return `data:image/jpeg;base64,${photoBase64}`;
  }

  blockUser(conversationId: number, event: Event) {
    event.stopPropagation();
    const conversation = this.conversations().find((c) => c.conversationId === conversationId);
    if (conversation) {
      console.log('Block user:', conversation.partnerDisplayName);
      // TODO: Implement blocking user on backend
      const conversations = this.conversations().filter((c) => c.conversationId !== conversationId);
      this.conversations.set(conversations);
    }
  }

  deleteConversation(conversationId: number, event: Event) {
    event.stopPropagation();
    // TODO: Implement deleting conversation on backend
    const conversations = this.conversations().filter((c) => c.conversationId !== conversationId);
    this.conversations.set(conversations);
    console.log('Delete conversation', conversationId);
  }
}

