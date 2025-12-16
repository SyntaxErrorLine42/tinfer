import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface MatchSummary {
  matchId: number;
  matchedUserId: string;
  displayName: string;
  primaryPhotoUrl: string;
  matchedAt: string;
  conversationId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class MatchService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getMatches(): Observable<MatchSummary[]> {
    return this.http.get<MatchSummary[]>(`${this.apiUrl}/api/matches`);
  }
}
