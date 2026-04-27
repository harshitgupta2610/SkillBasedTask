import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, Role } from '../models/user.model';

/**
 * Very basic auth service - NO JWT.
 * Stores the logged-in user in localStorage so that page refresh keeps the session.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly USER_KEY = 'auth_user';

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { email, password })
      .pipe(tap(res => this.saveUser(res)));
  }

  register(name: string, email: string, password: string, role: Role): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/auth/register`, { name, email, password, role }
    ).pipe(tap(res => this.saveUser(res)));
  }

  logout(): void {
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }

  get currentUser(): AuthResponse | null {
    const stored = localStorage.getItem(this.USER_KEY);
    return stored ? JSON.parse(stored) : null;
  }

  get isLoggedIn(): boolean {
    return this.currentUser !== null;
  }

  private saveUser(res: AuthResponse): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(res));
  }
}
