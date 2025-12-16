import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';

interface ConversationItem {
  id: number;
  matchId: number;
  otherUser: {
    id: string;
    name: string;
    avatar: string;
  };
  lastMessage: {
    content: string;
    sentAt: string;
    isRead: boolean;
    isSentByMe: boolean;
  };
  unreadCount: number;
  matchedAt: string;
}

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
export class ConversationsPage {
  conversations = signal<ConversationItem[]>([
    {
      id: 1,
      matchId: 101,
      otherUser: {
        id: 'user-1',
        name: 'Emma',
        avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400',
      },
      lastMessage: {
        content: 'Hey! I saw you\'re into photography too! 📸',
        sentAt: '2024-12-11T10:30:00',
        isRead: false,
        isSentByMe: false,
      },
      unreadCount: 3,
      matchedAt: '2024-12-10T15:20:00',
    },
    {
      id: 2,
      matchId: 102,
      otherUser: {
        id: 'user-2',
        name: 'Sophie',
        avatar: 'https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?w=400',
      },
      lastMessage: {
        content: 'That sounds like a great plan! When are you free?',
        sentAt: '2024-12-11T09:15:00',
        isRead: true,
        isSentByMe: true,
      },
      unreadCount: 0,
      matchedAt: '2024-12-09T18:45:00',
    },
    {
      id: 3,
      matchId: 103,
      otherUser: {
        id: 'user-3',
        name: 'Maya',
        avatar: 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400',
      },
      lastMessage: {
        content: 'Have you tried that new coffee place near campus?',
        sentAt: '2024-12-10T20:00:00',
        isRead: false,
        isSentByMe: false,
      },
      unreadCount: 1,
      matchedAt: '2024-12-08T12:30:00',
    },
    {
      id: 4,
      matchId: 104,
      otherUser: {
        id: 'user-4',
        name: 'Jordan',
        avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400',
      },
      lastMessage: {
        content: 'Nice to match with you! What\'s your favorite movie?',
        sentAt: '2024-12-10T14:30:00',
        isRead: true,
        isSentByMe: false,
      },
      unreadCount: 0,
      matchedAt: '2024-12-10T14:20:00',
    },
    {
      id: 5,
      matchId: 105,
      otherUser: {
        id: 'user-5',
        name: 'Alex',
        avatar: 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400',
      },
      lastMessage: {
        content: 'Thanks! I\'ll see you there 😊',
        sentAt: '2024-12-09T16:45:00',
        isRead: true,
        isSentByMe: true,
      },
      unreadCount: 0,
      matchedAt: '2024-12-07T10:15:00',
    },
  ]);

  constructor(private router: Router) {}

  goBack() {
    this.router.navigate(['/swipe']);
  }

  openConversation(conversationId: number) {
    console.log('Open conversation', conversationId);
    // TODO: Navigate to conversation detail page
  }

  getTimeAgo(dateString: string): string {
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
      return date.toLocaleDateString();
    }
  }

  blockUser(conversationId: number, event: Event) {
    event.stopPropagation();
    const conversation = this.conversations().find((c) => c.id === conversationId);
    if (conversation) {
      console.log('Block user:', conversation.otherUser.name);
      // TODO: implementirati blokiranje korisnika, trentuno samo izbriše razgovor s popisa
      const conversations = this.conversations().filter((c) => c.id !== conversationId);
      this.conversations.set(conversations);
    }
  }

  deleteConversation(conversationId: number, event: Event) {
    event.stopPropagation();
    const conversations = this.conversations().filter((c) => c.id !== conversationId);
    this.conversations.set(conversations);
    console.log('Delete conversation', conversationId);
  }
}

