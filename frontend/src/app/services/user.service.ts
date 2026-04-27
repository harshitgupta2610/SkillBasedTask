import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private base = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getAllEmployees(): Observable<User[]> {
    return this.http.get<User[]>(`${this.base}/employees`);
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.base);
  }

  getById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.base}/${userId}`);
  }

  addSkill(userId: number, skillId: number, proficiencyLevel: number, yearsExperience: number): Observable<User> {
    return this.http.post<User>(`${this.base}/${userId}/skills`, { skillId, proficiencyLevel, yearsExperience });
  }
}
