import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { TaskService } from '../../services/task.service';
import { UserService } from '../../services/user.service';
import { SkillService } from '../../services/skill.service';
import { Task, TaskStatus } from '../../models/task.model';
import { User } from '../../models/user.model';
import { Skill } from '../../models/skill.model';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html'
})
export class EmployeeComponent implements OnInit {
  tasks: Task[] = [];
  user: User | null = null;
  allSkills: Skill[] = [];

  // Add skill form
  newSkillId = 0;
  newProficiency = 3;
  newYears = 0;
  skillError = '';
  skillMessage = '';

  proficiencies = [
    { value: 1, label: 'Beginner' },
    { value: 2, label: 'Elementary' },
    { value: 3, label: 'Intermediate' },
    { value: 4, label: 'Advanced' },
    { value: 5, label: 'Expert' }
  ];

  constructor(
    public authService: AuthService,
    private taskService: TaskService,
    private userService: UserService,
    private skillService: SkillService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
    this.loadUser();
    this.skillService.getAll().subscribe(s => this.allSkills = s);
  }

  loadTasks(): void {
    const uid = this.authService.currentUser?.userId;
    if (!uid) return;
    this.taskService.getMyTasks(uid).subscribe(t => this.tasks = t);
  }

  loadUser(): void {
    const uid = this.authService.currentUser?.userId;
    if (!uid) return;
    this.userService.getById(uid).subscribe(u => this.user = u);
  }

  addSkill(): void {
    this.skillError = '';
    this.skillMessage = '';
    if (!this.newSkillId) {
      this.skillError = 'Please select a skill.';
      return;
    }
    if (!this.user) return;

    this.userService.addSkill(this.user.id, this.newSkillId, this.newProficiency, this.newYears).subscribe({
      next: u => {
        this.user = u;
        this.skillMessage = 'Skill added!';
        this.newSkillId = 0;
        this.newProficiency = 3;
        this.newYears = 0;
      },
      error: err => {
        this.skillError = err?.error?.message ?? 'Could not add skill (already added?)';
      }
    });
  }

  // Skills the user has NOT added yet (so they don't add duplicates)
  get availableSkills(): Skill[] {
    if (!this.user) return this.allSkills;
    const have = new Set(this.user.skills.map(s => s.skillId));
    return this.allSkills.filter(s => !have.has(s.id));
  }

  updateStatus(task: Task, status: TaskStatus): void {
    this.taskService.updateStatus(task.id, status).subscribe(updated => {
      const idx = this.tasks.findIndex(t => t.id === updated.id);
      if (idx !== -1) this.tasks[idx] = updated;
    });
  }

  nextStatus(current: TaskStatus): TaskStatus | null {
    if (current === 'ASSIGNED') return 'IN_PROGRESS';
    if (current === 'IN_PROGRESS') return 'DONE';
    return null;
  }

  nextStatusLabel(current: TaskStatus): string {
    const next = this.nextStatus(current);
    if (next === 'IN_PROGRESS') return 'Start';
    if (next === 'DONE') return 'Mark Done';
    return '';
  }

  // Bootstrap badge color for task status
  statusColor(s: TaskStatus): string {
    if (s === 'ASSIGNED') return 'bg-primary';
    if (s === 'IN_PROGRESS') return 'bg-warning text-dark';
    if (s === 'DONE') return 'bg-success';
    if (s === 'CANCELLED') return 'bg-secondary';
    return 'bg-light text-dark';
  }

  // Bootstrap badge color for priority
  priorityColor(p: string): string {
    if (p === 'CRITICAL') return 'bg-danger';
    if (p === 'HIGH') return 'bg-warning text-dark';
    if (p === 'MEDIUM') return 'bg-info text-dark';
    return 'bg-secondary';
  }

  // Color for proficiency level badge
  profColor(level: number): string {
    if (level >= 5) return 'bg-success';
    if (level === 4) return 'bg-primary';
    if (level === 3) return 'bg-info text-dark';
    if (level === 2) return 'bg-warning text-dark';
    return 'bg-secondary';
  }

  profLabel(level: number): string {
    return ['', 'Beginner', 'Elementary', 'Intermediate', 'Advanced', 'Expert'][level] ?? String(level);
  }

  logout(): void {
    this.authService.logout();
  }
}
