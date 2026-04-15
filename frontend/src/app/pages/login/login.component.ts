import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  email    = '';
  password = '';
  loading  = false;
  error    = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(): void {
    this.error   = '';
    this.loading = true;
    this.authService.login(this.email, this.password).subscribe({
      next: (res) => {
        this.loading = false;
        this.router.navigate([res.role === 'MANAGER' ? '/manager' : '/employee']);
      },
      error: () => {
        this.loading = false;
        this.error   = 'Invalid email or password.';
      }
    });
  }

  fillDemo(role: 'manager' | 'alice' | 'bob' | 'charlie'): void {
    const map: Record<string, string> = {
      manager: 'manager@demo.com',
      alice:   'alice@demo.com',
      bob:     'bob@demo.com',
      charlie: 'charlie@demo.com'
    };
    this.email    = map[role];
    this.password = 'password123';
  }
}
