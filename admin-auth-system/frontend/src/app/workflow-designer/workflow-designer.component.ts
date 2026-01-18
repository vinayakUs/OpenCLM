import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';
import { Workflow } from '../models/workflow.model';

import { DebounceInputDirective } from '../directives/debounce-input.directive';

@Component({
  selector: 'app-workflow-designer',
  standalone: true,
  imports: [CommonModule, FormsModule, DebounceInputDirective],
  templateUrl: './workflow-designer.component.html',
  styleUrls: ['./workflow-designer.component.css']
})
export class WorkflowDesignerComponent implements OnInit {
  private apiService = inject(ApiService);
  private router = inject(Router);

  workflows = signal<Workflow[]>([]);
  currentPage = signal(0);
  totalPages = signal(0);
  pageSize = signal(5); // Configurable page size
  showCreateMenu = signal(false);
  searchTerm = signal('');

  toggleCreateMenu() {
    this.showCreateMenu.update(v => !v);
  }

  ngOnInit() {
    this.loadWorkflows();
  }

  loadWorkflows() {
    this.apiService.getWorkflows(this.currentPage(), this.pageSize(), this.searchTerm()).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.workflows.set(response.data.content);
          this.totalPages.set(response.data.totalPages);
        }
      },
      error: (error) => console.error('Error fetching workflows:', error)
    });
  }

  onSearch(term: string) {
    this.searchTerm.set(term);
    this.currentPage.set(0); // Reset to first page
    this.loadWorkflows();
  }

  nextPage() {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(page => page + 1);
      this.loadWorkflows();
    }
  }

  prevPage() {
    if (this.currentPage() > 0) {
      this.currentPage.update(page => page - 1);
      this.loadWorkflows();
    }
  }

  createNew() {
    this.router.navigate(['/workflow-designer/editor']);
  }
}
