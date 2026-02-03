import { Injectable } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Notification {
  id: number;
  userId: number;
  message: string;
  createdAt: string;
  read: boolean;
}

/**
 * WebSocket Service - Modern replacement for socketService.js
 * 
 * CRITICAL SECURITY COMPONENT
 * 
 * Handles WebSocket connections with STOMP protocol.
 * Passes the X-AUTH-TOKEN in the STOMP CONNECT frame headers,
 * which is then validated by the backend's WebSocketTokenInterceptor.
 * 
 * This preserves the exact security model from the original application:
 * 1. Token is passed in native headers on CONNECT
 * 2. Server validates token and sets SecurityContext
 * 3. Only authenticated users can subscribe to notifications
 */
@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private rxStomp: RxStomp | null = null;
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  
  notifications$ = this.notificationsSubject.asObservable();

  /**
   * Connect to WebSocket with the auth token.
   * Token is passed in STOMP headers for server-side validation.
   */
  connect(token: string): void {
    if (this.rxStomp?.connected()) {
      return;
    }

    this.rxStomp = new RxStomp();

    const config: RxStompConfig = {
      brokerURL: this.getWebSocketUrl(),
      
      // CRITICAL: Pass the auth token in STOMP connect headers
      // This is validated by WebSocketTokenInterceptor on the server
      connectHeaders: {
        'X-AUTH-TOKEN': token
      },
      
      // Heartbeat settings
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      // Reconnection settings
      reconnectDelay: 5000,
      
      // Debug (disable in production)
      debug: (msg: string) => {
        if (!environment.production) {
          console.log('STOMP:', msg);
        }
      }
    };

    this.rxStomp.configure(config);
    this.rxStomp.activate();

    // Subscribe to user notifications after connection
    this.rxStomp.connected$.subscribe(() => {
      console.log('WebSocket connected');
      this.subscribeToNotifications();
    });

    this.rxStomp.stompErrors$.subscribe(error => {
      console.error('STOMP error:', error);
    });
  }

  /**
   * Subscribe to user-specific notifications.
   * The server requires authentication for SUBSCRIBE messages.
   */
  private subscribeToNotifications(): void {
    if (!this.rxStomp) return;

    // Subscribe to the user notifications endpoint
    // This maps to NotificationController.subscribeToNotifications()
    this.rxStomp.watch('/user/queue/notifications').subscribe(message => {
      try {
        const notifications = JSON.parse(message.body);
        this.notificationsSubject.next(notifications);
      } catch (e) {
        console.error('Failed to parse notification:', e);
      }
    });

    // Also subscribe via the direct endpoint for initial data
    this.rxStomp.watch('/app/user/notifications').subscribe(message => {
      try {
        const notifications = JSON.parse(message.body);
        this.notificationsSubject.next(notifications);
      } catch (e) {
        console.error('Failed to parse notification:', e);
      }
    });
  }

  /**
   * Disconnect from WebSocket.
   */
  disconnect(): void {
    if (this.rxStomp) {
      this.rxStomp.deactivate();
      this.rxStomp = null;
    }
    this.notificationsSubject.next([]);
  }

  /**
   * Get the WebSocket URL based on current location.
   */
  private getWebSocketUrl(): string {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    return `${protocol}//${host}${environment.wsUrl}`;
  }
}
