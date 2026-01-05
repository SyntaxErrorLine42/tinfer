import { Component, signal, inject, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonComponent } from '../../shared/components/button-wrapper/button-wrapper.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { IconComponent } from '../../shared/components/icon-wrapper/icon-wrapper.component';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import { ConversationService, ConversationSummary, Message } from '@shared/services/conversation.service';
import { AuthService } from '@shared/services/auth.service';

@Component({
  selector: 'app-chat',
  imports: [
    FormsModule,
    ButtonComponent,
    CardComponent,
    IconComponent,
    AvatarComponent,
  ],
  templateUrl: './chat.page.html',
  styleUrl: './chat.page.css',
})
export class ChatPage implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('messagesContainer') messagesContainer?: ElementRef<HTMLDivElement>;
  @ViewChild('messageInput') messageInput?: ElementRef<HTMLInputElement>;

  private conversationService = inject(ConversationService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  // State
  conversationId = signal<number | null>(null);
  partner = signal<ConversationSummary | null>(null);
  messages = signal<Message[]>([]);
  isLoading = signal(true);
  isSending = signal(false);
  error = signal<string | null>(null);
  newMessage = '';
  currentUserId = signal<string | null>(null);

  // For auto-scrolling
  private shouldScrollToBottom = false;
  
  // For polling new messages
  private pollingInterval: ReturnType<typeof setInterval> | null = null;
  private readonly POLL_INTERVAL = 3000; // 3 seconds

  ngOnInit() {
    // Get conversation ID from route
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.conversationId.set(parseInt(id, 10));
      this.loadConversationData();
      this.startPolling();
    } else {
      this.error.set('Razgovor nije pronađen');
      this.isLoading.set(false);
    }

    // Get current user ID
    this.authService.getCurrentUser().then(user => {
      if (user) {
        this.currentUserId.set(user.id);
      }
    });
  }

  ngOnDestroy() {
    this.stopPolling();
  }

  ngAfterViewChecked() {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  loadConversationData() {
    const convId = this.conversationId();
    if (!convId) return;

    this.isLoading.set(true);
    this.error.set(null);

    // Load conversation info and messages in parallel
    this.conversationService.getConversations().subscribe({
      next: (conversations) => {
        const conversation = conversations.find(c => c.conversationId === convId);
        if (conversation) {
          this.partner.set(conversation);
        }
        this.loadMessages();
      },
      error: (err) => {
        console.error('Failed to load conversation info:', err);
        this.loadMessages(); // Still try to load messages
      }
    });
  }

  loadMessages() {
    const convId = this.conversationId();
    if (!convId) return;

    this.conversationService.getMessages(convId, 0, 100).subscribe({
      next: (page) => {
        this.messages.set(page.content);
        this.isLoading.set(false);
        this.shouldScrollToBottom = true;
        
        // Mark messages as read
        this.markMessagesAsRead();
      },
      error: (err) => {
        console.error('Failed to load messages:', err);
        this.error.set('Nije moguće učitati poruke. Pokušajte ponovno.');
        this.isLoading.set(false);
      }
    });
  }

  markMessagesAsRead() {
    const convId = this.conversationId();
    const msgs = this.messages();
    const currentUser = this.currentUserId();
    
    if (!convId || msgs.length === 0 || !currentUser) return;

    // Find the last message not sent by current user
    const unreadMessages = msgs.filter(m => m.senderId !== currentUser && !m.read);
    if (unreadMessages.length === 0) return;

    const lastMessage = unreadMessages[unreadMessages.length - 1];
    
    this.conversationService.markAsRead(convId, lastMessage.id).subscribe({
      error: (err) => console.error('Failed to mark messages as read:', err)
    });
  }

  startPolling() {
    this.pollingInterval = setInterval(() => {
      this.pollNewMessages();
    }, this.POLL_INTERVAL);
  }

  stopPolling() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
    }
  }

  pollNewMessages() {
    const convId = this.conversationId();
    if (!convId || this.isSending()) return;

    this.conversationService.getMessages(convId, 0, 100).subscribe({
      next: (page) => {
        const currentMessages = this.messages();
        const newMessages = page.content;
        
        // Check if there are new messages
        if (newMessages.length > currentMessages.length) {
          this.messages.set(newMessages);
          this.shouldScrollToBottom = true;
          this.markMessagesAsRead();
        }
      },
      error: (err) => {
        console.error('Failed to poll messages:', err);
      }
    });
  }

  sendMessage() {
    const convId = this.conversationId();
    const content = this.newMessage.trim();
    
    if (!convId || !content || this.isSending()) return;

    this.isSending.set(true);

    this.conversationService.sendMessage(convId, content).subscribe({
      next: (message) => {
        // Add message to list
        this.messages.update(msgs => [...msgs, message]);
        this.newMessage = '';
        this.isSending.set(false);
        this.shouldScrollToBottom = true;
        
        // Focus back on input
        this.messageInput?.nativeElement.focus();
      },
      error: (err) => {
        console.error('Failed to send message:', err);
        this.isSending.set(false);
        // Show brief error - could improve with toast notification
        alert('Nije moguće poslati poruku. Pokušajte ponovno.');
      }
    });
  }

  onKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  goBack() {
    this.router.navigate(['/conversations']);
  }

  scrollToBottom() {
    if (this.messagesContainer) {
      const container = this.messagesContainer.nativeElement;
      container.scrollTop = container.scrollHeight;
    }
  }

  isMyMessage(message: Message): boolean {
    return message.senderId === this.currentUserId();
  }

  getAvatarSrc(photoBase64: string | null): string {
    if (!photoBase64) {
      return 'https://via.placeholder.com/100?text=?';
    }
    if (photoBase64.startsWith('data:')) {
      return photoBase64;
    }
    return `data:image/jpeg;base64,${photoBase64}`;
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('hr-HR', { hour: '2-digit', minute: '2-digit' });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Danas';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Jučer';
    } else {
      return date.toLocaleDateString('hr-HR', { day: 'numeric', month: 'long' });
    }
  }

  shouldShowDateSeparator(index: number): boolean {
    if (index === 0) return true;
    
    const msgs = this.messages();
    const currentDate = new Date(msgs[index].sentAt).toDateString();
    const previousDate = new Date(msgs[index - 1].sentAt).toDateString();
    
    return currentDate !== previousDate;
  }
}
