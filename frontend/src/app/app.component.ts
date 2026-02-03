import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService } from './core/auth/auth.service';
import { WebSocketService } from './core/websocket/websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary fixed-top">
      <div class="container">
        <a class="navbar-brand" routerLink="/home">
          <i class="bi bi-shield-lock"></i> WebSocket Security
        </a>
        
        @if (authService.isAuthenticated()) {
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
          </button>
          
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
              <li class="nav-item">
                <a class="nav-link" routerLink="/home">Home</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" routerLink="/customers">Customers</a>
              </li>
            </ul>
            
            <ul class="navbar-nav">
              <li class="nav-item">
                <span class="nav-link notification-badge">
                  <i class="bi bi-bell"></i>
                  @if (notificationCount > 0) {
                    <span class="badge bg-danger rounded-pill">{{ notificationCount }}</span>
                  }
                </span>
              </li>
              <li class="nav-item">
                <span class="nav-link">{{ currentUser?.email }}</span>
              </li>
              <li class="nav-item">
                <button class="btn btn-outline-light btn-sm ms-2" (click)="logout()">
                  Logout
                </button>
              </li>
            </ul>
          </div>
        }
      </div>
    </nav>

    <div class="container-fluid">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent implements OnInit, OnDestroy {
  authService = inject(AuthService);
  private wsService = inject(WebSocketService);
  private router = inject(Router);
  
  currentUser: any = null;
  notificationCount = 0;
  private subscriptions = new Subscription();

  ngOnInit(): void {
    // Subscribe to user changes
    this.subscriptions.add(
      this.authService.currentUser$.subscribe((user: any) => {
        this.currentUser = user;
        if (user) {
          this.connectWebSocket();
        }
      })
    );

    // Subscribe to notifications
    this.subscriptions.add(
      this.wsService.notifications$.subscribe(notifications => {
        this.notificationCount = notifications.length;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.wsService.disconnect();
  }

  private connectWebSocket(): void {
    const token = this.authService.getToken();
    if (token) {
      this.wsService.connect(token);
    }
  }

  logout(): void {
    this.wsService.disconnect();
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
