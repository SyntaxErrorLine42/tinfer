import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SwipeRequest {
  swipedUserId: string;
  action: 'LIKE' | 'DISLIKE' | 'SUPERLIKE';
}

export interface SwipeResponse {
  isMatch: boolean;
  matchId?: number;
  conversationId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class SwipeService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  swipe(request: SwipeRequest): Observable<SwipeResponse> {
    return this.http.post<SwipeResponse>(`${this.apiUrl}/api/swipes`, request);
  }
}
