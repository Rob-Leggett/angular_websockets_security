import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginCredentials } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-container bg-white">
      <h2 class="text-center mb-4">Login</h2>
      
      @if (error) {
        <div class="alert alert-danger" role="alert">
          {{ error }}
        </div>
      }
      
      <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input 
            type="email" 
            class="form-control" 
            id="email" 
            name="email"
            [(ngModel)]="credentials.email"
            required
            email
            placeholder="user@example.com"
          >
        </div>
        
        <div class="mb-3">
          <label for="password" class="form-label">Password</label>
          <input 
            type="password" 
            class="form-control" 
            id="password"
            name="password"
            [(ngModel)]="credentials.password"
            required
            placeholder="password"
          >
        </div>
        
        <button 
          type="submit" 
          class="btn btn-primary w-100"
          [disabled]="!loginForm.form.valid || isLoading"
        >
          @if (isLoading) {
            <span class="spinner-border spinner-border-sm me-2"></span>
          }
          Login
        </button>
      </form>
      
      <div class="mt-3 text-muted small text-center">
        <p class="mb-1">Default credentials:</p>
        <p class="mb-0"><strong>Email:</strong> user&#64;example.com</p>
        <p class="mb-0"><strong>Password:</strong> password</p>
      </div>
    </div>
  `
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  credentials: LoginCredentials = {
    email: '',
    password: ''
  };
  
  error: string | null = null;
  isLoading = false;

  onSubmit(): void {
    this.error = null;
    this.isLoading = true;

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.error = 'Authentication failed. Please check your credentials.';
        this.isLoading = false;
      }
    });
  }
}
