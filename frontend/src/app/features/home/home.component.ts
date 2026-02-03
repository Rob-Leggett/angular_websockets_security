import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService, User } from '../../core/auth/auth.service';
import { WebSocketService, Notification } from '../../core/websocket/websocket.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      <div class="row">
        <div class="col-md-8">
          <div class="card">
            <div class="card-header">
              <h4 class="mb-0">Welcome, {{ user?.firstName }}!</h4>
            </div>
            <div class="card-body">
              <p class="lead">
                This application demonstrates secure WebSocket communication with token-based authentication.
              </p>
              
              <h5 class="mt-4">Security Features:</h5>
              <ul class="list-group list-group-flush">
                <li class="list-group-item">
                  <i class="bi bi-shield-check text-success me-2"></i>
                  JWT token authentication for REST API
                </li>
                <li class="list-group-item">
                  <i class="bi bi-shield-check text-success me-2"></i>
                  Token-secured WebSocket connections
                </li>
                <li class="list-group-item">
                  <i class="bi bi-shield-check text-success me-2"></i>
                  STOMP message-level security
                </li>
                <li class="list-group-item">
                  <i class="bi bi-shield-check text-success me-2"></i>
                  Stateless session management
                </li>
              </ul>
            </div>
          </div>
        </div>
        
        <div class="col-md-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-bell me-2"></i>
                Notifications
                @if (notifications.length > 0) {
                  <span class="badge bg-primary ms-2">{{ notifications.length }}</span>
                }
              </h5>
            </div>
            <div class="card-body">
              @if (notifications.length === 0) {
                <p class="text-muted mb-0">No new notifications</p>
              } @else {
                <ul class="list-group list-group-flush">
                  @for (notification of notifications; track notification.id) {
                    <li class="list-group-item px-0">
                      <small class="text-muted">{{ notification.createdAt | date:'short' }}</small>
                      <p class="mb-0">{{ notification.message }}</p>
                    </li>
                  }
                </ul>
              }
            </div>
          </div>
          
          <div class="card mt-3">
            <div class="card-header">
              <h5 class="mb-0">WebSocket Status</h5>
            </div>
            <div class="card-body">
              <p class="mb-0">
                <span class="badge" [class.bg-success]="wsConnected" [class.bg-danger]="!wsConnected">
                  {{ wsConnected ? 'Connected' : 'Disconnected' }}
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class HomeComponent implements OnInit {
  private authService = inject(AuthService);
  private wsService = inject(WebSocketService);

  user: User | null = null;
  notifications: Notification[] = [];
  wsConnected = false;

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
    this.wsService.notifications$.subscribe(notifications => {
      this.notifications = notifications;
      this.wsConnected = true;
    });
  }
}
