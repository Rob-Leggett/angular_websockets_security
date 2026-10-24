import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('authGuard', () => {
  let isAuthenticated: boolean;
  let navigate: ReturnType<typeof vi.fn>;

  const runGuard = () =>
    TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

  beforeEach(() => {
    navigate = vi.fn();
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: { isAuthenticated: () => isAuthenticated } },
        { provide: Router, useValue: { navigate } }
      ]
    });
  });

  it('allows navigation when authenticated', () => {
    isAuthenticated = true;
    expect(runGuard()).toBe(true);
    expect(navigate).not.toHaveBeenCalled();
  });

  it('redirects to /login when not authenticated', () => {
    isAuthenticated = false;
    expect(runGuard()).toBe(false);
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });
});
