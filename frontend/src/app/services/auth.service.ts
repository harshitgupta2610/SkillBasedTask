import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, Role } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly USER_KEY  = 'auth_user';

  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.loadUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { email, password })
      .pipe(tap(res => this.storeSession(res)));
  }

  register(name: string, email: string, password: string, role: Role): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/auth/register`, { name, email, password, role }
    ).pipe(tap(res => this.storeSession(res)));
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null { return localStorage.getItem(this.TOKEN_KEY); }
  get currentUser(): AuthResponse | null { return this.currentUserSubject.value; }
  get isLoggedIn(): boolean { return !!this.getToken(); }
  get isManager(): boolean { return this.currentUser?.role === 'MANAGER'; }

  private storeSession(res: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(res));
    this.currentUserSubject.next(res);
  }

  private loadUser(): AuthResponse | null {
    const stored = localStorage.getItem(this.USER_KEY);
    return stored ? JSON.parse(stored) : null;
  }
}
