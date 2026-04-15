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

  getMe(): Observable<User> {
    return this.http.get<User>(`${this.base}/me`);
  }

  updateProfile(userId: number, name: string): Observable<User> {
    return this.http.put<User>(`${this.base}/${userId}/profile`, { name });
  }

  updateAvailability(userId: number, available: boolean): Observable<User> {
    return this.http.patch<User>(`${this.base}/${userId}/availability`, { available });
  }

  addSkill(userId: number, skillId: number, proficiencyLevel: number, yearsExperience: number): Observable<User> {
    return this.http.post<User>(`${this.base}/${userId}/skills`, { skillId, proficiencyLevel, yearsExperience });
  }

  updateSkill(userId: number, userSkillId: number, proficiencyLevel: number, yearsExperience: number): Observable<User> {
    return this.http.put<User>(`${this.base}/${userId}/skills/${userSkillId}`, { skillId: 0, proficiencyLevel, yearsExperience });
  }

  removeSkill(userId: number, userSkillId: number): Observable<User> {
    return this.http.delete<User>(`${this.base}/${userId}/skills/${userSkillId}`);
  }

  getNotifications(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/${userId}/notifications`);
  }

  markAllRead(userId: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/${userId}/notifications/read-all`, {});
  }

  getUnreadCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.base}/${userId}/notifications/unread-count`);
  }
}
