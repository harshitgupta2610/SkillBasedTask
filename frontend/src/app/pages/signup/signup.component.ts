import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Role } from '../../models/user.model';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html'
})
export class SignupComponent {
  name      = '';
  email     = '';
  password  = '';
  confirmPw = '';
  role: Role = 'EMPLOYEE';
  showPw    = false;
  showConfirmPw = false;
  loading   = false;
  error     = '';
  fieldErrors: Record<string, string> = {};

  constructor(private authService: AuthService, private router: Router) {}

  onRegister(): void {
    this.error       = '';
    this.fieldErrors = {};
    if (!this.validate()) return;

    this.loading = true;
    this.authService.register(this.name, this.email, this.password, this.role).subscribe({
      next: res => {
        this.loading = false;
        this.router.navigate([res.role === 'MANAGER' ? '/manager' : '/employee']);
      },
      error: err => {
        this.loading = false;
        this.error = err?.error?.message ?? err?.error?.error ?? 'Registration failed. Please try again.';
      }
    });
  }

  private validate(): boolean {
    let ok = true;
    if (!this.name.trim()) {
      this.fieldErrors['name'] = 'Full name is required'; ok = false;
    }
    if (!this.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
      this.fieldErrors['email'] = 'Valid email is required'; ok = false;
    }
    if (this.password.length < 6) {
      this.fieldErrors['password'] = 'Minimum 6 characters'; ok = false;
    }
    if (this.password !== this.confirmPw) {
      this.fieldErrors['confirmPw'] = 'Passwords do not match'; ok = false;
    }
    return ok;
  }
}
