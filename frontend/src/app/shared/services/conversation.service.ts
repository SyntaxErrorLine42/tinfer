import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ConversationSummary {
  conversationId: number;
  partnerId: string;
  partnerDisplayName: string;
  partnerPrimaryPhotoUrl: string;
  lastMessageSnippet: string;
  lastMessageAt: string;
  unreadCount: number;
}

export interface MessageResponse {
  id: number;
  senderId: string;
  content: string;
  sentAt: string;
  readAt?: string;
}

export interface MessagePage {
  content: MessageResponse[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

@Injectable({
  providedIn: 'root',
})
export class ConversationService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getMyConversations(): Observable<ConversationSummary[]> {
    return this.http.get<ConversationSummary[]>(`${this.apiUrl}/api/conversations`);
  }

  getMessages(conversationId: number, page: number = 0, size: number = 20): Observable<MessagePage> {
    return this.http.get<MessagePage>(
      `${this.apiUrl}/api/conversations/${conversationId}/messages`,
      { params: { page: page.toString(), size: size.toString() } }
    );
  }

  markAsRead(conversationId: number, messageIds: number[]): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/api/conversations/${conversationId}/read`,
      { messageIds }
    );
  }
}
