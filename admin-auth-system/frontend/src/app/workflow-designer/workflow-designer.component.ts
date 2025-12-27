import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';
import { Workflow } from '../models/workflow.model';

@Component({
  selector: 'app-workflow-designer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workflow-designer.component.html',
  styleUrls: ['./workflow-designer.component.css']
})
export class WorkflowDesignerComponent implements OnInit {
  private apiService = inject(ApiService);
  private router = inject(Router);

  workflows = signal<Workflow[]>([]);

  ngOnInit() {
    this.loadWorkflows();
  }

  loadWorkflows() {
    this.apiService.getWorkflows(0, 100).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.workflows.set(response.data.content);
        }
      },
      error: (error) => console.error('Error fetching workflows:', error)
    });
  }

  createNew() {
    this.router.navigate(['/workflow-designer/editor']);
  }
}
