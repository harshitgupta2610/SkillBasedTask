export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type TaskStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'DONE' | 'CANCELLED';

export interface SkillRequirement {
  skillId: number;
  skillName: string;
  skillCategory: string;
  minProficiencyLevel: number;
}

export interface UserSummary {
  id: number;
  name: string;
  email: string;
}

export interface Task {
  id: number;
  title: string;
  description: string;
  priority: Priority;
  status: TaskStatus;
  deadline: string;
  createdAt: string;
  assignedAt: string;
  allocationScore: number | null;
  createdBy: UserSummary;
  assignedTo: UserSummary | null;
  requiredSkills: SkillRequirement[];
}

export interface TaskCreateRequest {
  title: string;
  description: string;
  priority: Priority;
  deadline?: string;
  requiredSkills: { skillId: number; minProficiencyLevel: number }[];
}
