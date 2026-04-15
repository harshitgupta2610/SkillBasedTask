import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Skill } from '../models/skill.model';

@Injectable({ providedIn: 'root' })
export class SkillService {
  private base = `${environment.apiUrl}/skills`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Skill[]> {
    return this.http.get<Skill[]>(this.base);
  }

  create(name: string, category: string): Observable<Skill> {
    return this.http.post<Skill>(this.base, { name, category });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
