import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ReportReason = 'INAPPROPRIATE_CONTENT' | 'HARASSMENT' | 'FAKE_PROFILE' | 'SPAM' | 'OTHER';

export interface ReportRequest {
    reportedId: string;
    reason: ReportReason;
    description?: string;
}

@Injectable({ providedIn: 'root' })
export class ReportService {
    private http = inject(HttpClient);

    reportUser(request: ReportRequest): Observable<void> {
        return this.http.post<void>('/api/reports', request);
    }
}
