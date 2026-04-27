import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { TaskService } from '../../services/task.service';
import { UserService } from '../../services/user.service';
import { SkillService } from '../../services/skill.service';
import { Task, TaskCreateRequest, Priority } from '../../models/task.model';
import { User } from '../../models/user.model';
import { Skill } from '../../models/skill.model';

@Component({
  selector: 'app-manager',
  templateUrl: './manager.component.html'
})
export class ManagerComponent implements OnInit {
  tasks: Task[] = [];
  employees: User[] = [];
  skills: Skill[] = [];

  error = '';
  loading = false;

  newTask: TaskCreateRequest = {
    title: '',
    description: '',
    priority: 'MEDIUM',
    deadline: '',
    createdById: 0,
    requiredSkills: []
  };

  priorities: Priority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  proficiencies = [1, 2, 3, 4, 5];

  constructor(
    public authService: AuthService,
    private taskService: TaskService,
    private userService: UserService,
    private skillService: SkillService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
    this.loadEmployees();
    this.skillService.getAll().subscribe(s => this.skills = s);
  }

  loadTasks(): void {
    this.taskService.getAllTasks().subscribe(t => this.tasks = t);
  }

  loadEmployees(): void {
    this.userService.getAllEmployees().subscribe(e => this.employees = e);
  }

  addSkillReq(): void {
    this.newTask.requiredSkills.push({ skillId: 0, minProficiencyLevel: 1 });
  }

  removeSkillReq(i: number): void {
    this.newTask.requiredSkills.splice(i, 1);
  }

  submitTask(): void {
    const uid = this.authService.currentUser?.userId;
    if (!uid) {
      this.error = 'You must be logged in.';
      return;
    }
    if (!this.newTask.title.trim()) {
      this.error = 'Title is required.';
      return;
    }
    this.error = '';
    this.loading = true;

    const payload: TaskCreateRequest = {
      ...this.newTask,
      createdById: uid,
      requiredSkills: this.newTask.requiredSkills.filter(s => s.skillId > 0)
    };

    this.taskService.createTask(payload).subscribe({
      next: () => {
        this.loading = false;
        this.newTask = { title: '', description: '', priority: 'MEDIUM', deadline: '', createdById: 0, requiredSkills: [] };
        this.loadTasks();
        this.loadEmployees();
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to create task.';
      }
    });
  }

  proficiencyLabel(level: number): string {
    return ['', 'Beginner', 'Elementary', 'Intermediate', 'Advanced', 'Expert'][level] ?? String(level);
  }

  statusColor(s: string): string {
    if (s === 'OPEN') return 'bg-secondary';
    if (s === 'ASSIGNED') return 'bg-primary';
    if (s === 'IN_PROGRESS') return 'bg-warning text-dark';
    if (s === 'DONE') return 'bg-success';
    if (s === 'CANCELLED') return 'bg-dark';
    return 'bg-light text-dark';
  }

  priorityColor(p: string): string {
    if (p === 'CRITICAL') return 'bg-danger';
    if (p === 'HIGH') return 'bg-warning text-dark';
    if (p === 'MEDIUM') return 'bg-info text-dark';
    return 'bg-secondary';
  }

  logout(): void {
    this.authService.logout();
  }
}
