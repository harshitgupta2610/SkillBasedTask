import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Task, TaskCreateRequest, TaskStatus } from '../models/task.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private base = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.base);
  }

  getMyTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.base}/my-tasks`);
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.base}/${id}`);
  }

  createTask(req: TaskCreateRequest): Observable<Task> {
    return this.http.post<Task>(this.base, req);
  }

  updateStatus(id: number, status: TaskStatus): Observable<Task> {
    return this.http.patch<Task>(`${this.base}/${id}/status`, { status });
  }

  manualAssign(taskId: number, employeeId: number): Observable<Task> {
    return this.http.patch<Task>(`${this.base}/${taskId}/assign/${employeeId}`, {});
  }
}
