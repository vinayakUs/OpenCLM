export interface WorkflowBase {
  createdAt: string;
  createdBy: string;
  currentStatus: 'DRAFT' | 'PUBLISHED';
  description: string;
  id: string;
  name: string;
  templateFileId: string;
  updatedAt: string;
  version: number;
}

export interface Workflow {
  id: string;
  name: string;
  description: string;
  templateFileId: string;
  currentStatus: 'DRAFT' | 'PUBLISHED';
  version: number;
  createdBy: string;
  createdAt: string; // ISO string for OffsetDateTime
  updatedAt: string;
  variableResponse?: any[]; // flexible for now
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
