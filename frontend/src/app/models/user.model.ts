export type Role = 'MANAGER' | 'EMPLOYEE';

export interface SkillSummary {
  userSkillId: number;
  skillId: number;
  skillName: string;
  category: string;
  proficiencyLevel: number;
  yearsExperience: number;
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  available: boolean;
  activeTaskCount: number;
  skills: SkillSummary[];
}

export interface AuthResponse {
  token: string;
  userId: number;
  name: string;
  email: string;
  role: Role;
}
