# Tinfer - Chat Implementation Notes

## Supabase Realtime za Chat

### Što je Supabase Realtime?

Supabase Realtime omogućava **real-time subscriptions** na promjene u PostgreSQL bazi podataka. Kada se doda nova poruka u tablicu `chat_message`, svi pretplaćeni korisnici automatski dobiju tu poruku bez potrebe za pollingom.

### Kako implementirati?

#### 1. Backend (Spring Boot) - **NE TREBA DODATNE KONFIGURACIJE**

Backend **već ima sve potrebno**:

- `ChatHistoryService` koji sprema poruke u `chat_message` tablicu
- `POST /api/conversations/{conversationId}/messages` endpoint (za slanje poruka)
- `GET /api/conversations/{conversationId}/messages` endpoint (za dohvaćanje povijesti)

**Backend NE KORISTI Supabase Realtime** - on samo piše u bazu i čita iz baze. Realtime se događa na frontendu.

#### 2. Frontend (Angular) - Implementacija Realtime Subscription

```typescript
import {
  createClient,
  SupabaseClient,
  RealtimeChannel,
} from "@supabase/supabase-js";
import { environment } from "../../../environments/environment";

export class ChatService {
  private supabase: SupabaseClient;
  private messageChannel?: RealtimeChannel;

  constructor() {
    this.supabase = createClient(
      environment.supabaseUrl,
      environment.supabaseAnonKey
    );
  }

  /**
   * Subscribe to new messages in a conversation
   */
  subscribeToMessages(
    conversationId: number,
    callback: (message: any) => void
  ) {
    // Create a channel for this conversation
    this.messageChannel = this.supabase
      .channel(`conversation:${conversationId}`)
      .on(
        "postgres_changes",
        {
          event: "INSERT",
          schema: "public",
          table: "chat_message",
          filter: `conversation_id=eq.${conversationId}`,
        },
        (payload) => {
          console.log("New message:", payload.new);
          callback(payload.new); // Pass the new message to the callback
        }
      )
      .subscribe();
  }

  /**
   * Unsubscribe when leaving the chat
   */
  unsubscribeFromMessages() {
    if (this.messageChannel) {
      this.supabase.removeChannel(this.messageChannel);
      this.messageChannel = undefined;
    }
  }
}
```

#### 3. Korištenje u komponenti

```typescript
export class ChatComponent implements OnInit, OnDestroy {
  messages = signal<MessageResponse[]>([]);

  ngOnInit() {
    const conversationId = 123; // From route params

    // Load initial messages
    this.conversationService.getMessages(conversationId).subscribe({
      next: (page) => this.messages.set(page.content),
    });

    // Subscribe to new messages
    this.chatService.subscribeToMessages(conversationId, (newMessage) => {
      this.messages.update((msgs) => [...msgs, newMessage]);
    });
  }

  ngOnDestroy() {
    this.chatService.unsubscribeFromMessages();
  }

  sendMessage(content: string) {
    // Send via backend API (NOT Supabase directly)
    this.http
      .post(`${apiUrl}/api/conversations/${conversationId}/messages`, {
        content,
      })
      .subscribe();

    // Message will appear via Realtime subscription when backend saves it
  }
}
```

### Važne napomene

1. **Backend piše, Frontend sluša**

   - Backend koristi standardne Spring Boot controllere i piše direktno u PostgreSQL
   - Frontend se pretplaćuje na Supabase Realtime da sluša promjene u bazi
   - **NE ŠALJEMO PORUKE DIREKTNO KROZ SUPABASE** - to se radi preko backend API-ja

2. **Supabase RLS (Row Level Security)**

   - Moguće je da trebaš omogućiti RLS politike na `chat_message` tablici
   - To omogućava da korisnici vide samo poruke iz konverzacija u kojima sudjeluju

3. **Instalacija paketa**

   ```bash
   npm install @supabase/supabase-js
   ```

4. **Trenutno stanje**
   - ✅ Home page s listom konverzacija i swipe karticama
   - ✅ Backend API endpointi za conversation, messages, swipes
   - ❌ Chat page s real-time porukama (treba kreirati)
   - ❌ Supabase Realtime subscription (treba dodati)

### Sljedeći koraci

1. **Kreiraj Chat komponentu** (`/chat/:conversationId`)

   - Prikaz poruka (lista bubbles)
   - Input za slanje poruka
   - Realtime subscription

2. **Dodaj ChatService** sa Supabase Realtime

   - `subscribeToMessages()`
   - `unsubscribeFromMessages()`

3. **Implementiraj slanje poruka**

   - POST preko backend API-ja
   - Poruke se automatski primaju preko Realtime subscriptiona

4. **Dodaj RLS politike** (u Supabase dashboardu)

   ```sql
   -- Enable RLS
   ALTER TABLE chat_message ENABLE ROW LEVEL SECURITY;

   -- Allow users to read messages from their conversations
   CREATE POLICY "Users can read own messages"
   ON chat_message FOR SELECT
   USING (
     EXISTS (
       SELECT 1 FROM conversation_participant
       WHERE conversation_id = chat_message.conversation_id
       AND user_id = auth.uid()
     )
   );
   ```

## Backend Endpointi

- `GET /api/conversations` - Dohvati sve konverzacije
- `GET /api/conversations/{id}/messages` - Dohvati poruke (pagination)
- `POST /api/conversations/{id}/messages` - Pošalji novu poruku
- `POST /api/conversations/{id}/read` - Označi poruke kao pročitane
- `GET /api/recommendations` - Dohvati preporuke
- `POST /api/swipes` - Pošalji swipe (LIKE/DISLIKE/SUPERLIKE)
- `GET /api/matches` - Dohvati match-eve
