import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap, BehaviorSubject } from 'rxjs';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'X-AUTH-TOKEN';
const USER_KEY = 'currentUser';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface LoginCredentials {
  email: string;
  password: string;
}

/**
 * Authentication Service - Modern replacement for authenticationService.js
 * 
 * Handles:
 * - Login with Basic Auth
 * - Token storage in sessionStorage
 * - Current user management
 * 
 * Maintains compatibility with the original token-based security model.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  
  private currentUserSubject = new BehaviorSubject<User | null>(this.loadUserFromStorage());
  
  currentUser$ = this.currentUserSubject.asObservable();

  constructor() {
    // Restore user from storage on init
    const storedUser = this.loadUserFromStorage();
    if (storedUser) {
      this.currentUserSubject.next(storedUser);
    }
  }

  /**
   * Login with email and password.
   * Sends Basic Auth header, receives X-AUTH-TOKEN in response.
   */
  login(credentials: LoginCredentials): Observable<any> {
    const base64Credentials = btoa(`${credentials.email}:${credentials.password}`);
    
    const headers = new HttpHeaders({
      'Authorization': `Basic ${base64Credentials}`
    });

    return this.http.post(`${environment.apiUrl}/authentication/login`, null, { 
      headers,
      observe: 'response'
    }).pipe(
      tap(response => {
        // Extract token from response header
        const token = response.headers.get(TOKEN_KEY);
        if (token) {
          this.setToken(token);
          // Load user details
          this.loadCurrentUser();
        }
      })
    );
  }

  /**
   * Logout - clears local storage and notifies server.
   */
  logout(): Observable<any> {
    return this.http.post(`${environment.apiUrl}/authentication/logout`, null).pipe(
      tap(() => {
        this.clearAuth();
      })
    );
  }

  /**
   * Load current user details from API.
   */
  loadCurrentUser(): void {
    this.http.get<User>(`${environment.apiUrl}/user`).subscribe({
      next: (user) => {
        this.currentUserSubject.next(user);
        sessionStorage.setItem(USER_KEY, JSON.stringify(user));
      },
      error: () => {
        this.clearAuth();
      }
    });
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.getValue();
  }

  /**
   * Check if user is authenticated.
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Get the current auth token.
   */
  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  /**
   * Store the auth token.
   */
  setToken(token: string): void {
    sessionStorage.setItem(TOKEN_KEY, token);
  }

  /**
   * Clear all auth data.
   */
  private clearAuth(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    this.currentUserSubject.next(null);
  }

  /**
   * Load user from storage on app init.
   */
  private loadUserFromStorage(): User | null {
    const userJson = sessionStorage.getItem(USER_KEY);
    if (userJson) {
      try {
        return JSON.parse(userJson);
      } catch {
        return null;
      }
    }
    return null;
  }
}
