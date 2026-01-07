import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Conversation summary from backend
export interface ConversationSummary {
  conversationId: number;
  partnerId: string;
  partnerDisplayName: string;
  partnerPrimaryPhotoBase64: string | null;
  lastMessageSnippet: string | null;
  lastMessageAt: string | null;
  unreadCount: number;
}

// Message from backend
export interface Message {
  id: number;
  conversationId: number;
  senderId: string;
  content: string;
  attachmentUrl: string | null;
  read: boolean;
  sentAt: string;
  readAt: string | null;
}

// Paginated response
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Request to send a message
export interface SendMessageRequest {
  content: string;
  attachmentUrl?: string;
}

// Request to mark messages as read
export interface MarkMessagesReadRequest {
  lastReadMessageId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ConversationService {
  private http = inject(HttpClient);

  /**
   * Get all conversations for the current user
   */
  getConversations(): Observable<ConversationSummary[]> {
    return this.http.get<ConversationSummary[]>('/api/conversations');
  }

  /**
   * Get messages for a conversation with pagination
   */
  getMessages(conversationId: number, page: number = 0, size: number = 50): Observable<Page<Message>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<Page<Message>>(`/api/conversations/${conversationId}/messages`, { params });
  }

  /**
   * Send a message to a conversation
   */
  sendMessage(conversationId: number, content: string, attachmentUrl?: string): Observable<Message> {
    const request: SendMessageRequest = { content };
    if (attachmentUrl) {
      request.attachmentUrl = attachmentUrl;
    }
    return this.http.post<Message>(`/api/conversations/${conversationId}/messages`, request);
  }

  /**
   * Mark messages as read up to a specific message ID
   */
  markAsRead(conversationId: number, lastReadMessageId: number): Observable<void> {
    const request: MarkMessagesReadRequest = { lastReadMessageId };
    return this.http.post<void>(`/api/conversations/${conversationId}/read`, request);
  }
}
