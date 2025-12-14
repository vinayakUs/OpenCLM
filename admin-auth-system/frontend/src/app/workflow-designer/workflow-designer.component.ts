import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-workflow-designer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workflow-designer.component.html',
  styleUrls: ['./workflow-designer.component.css']
})
export class WorkflowDesignerComponent {

  workflows = [
    { title: 'Onboarding Flow', description: 'Employee onboarding process automation', status: 'Published', date: '2023-10-01' },
    { title: 'Invoice Approval', description: 'Multi-level approval for finance', status: 'Draft', date: '2023-11-15' },
    { title: 'Leave Request', description: 'Holiday and sick leave management', status: 'Published', date: '2023-09-20' },
    { title: 'Support Ticket', description: 'Customer support ticket routing', status: 'Draft', date: '2023-12-05' }
  ];

  constructor(private router: Router) { }

  createNew() {
    this.router.navigate(['/workflow-designer/editor']);
  }
}
