import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-repository',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="page-container">
      <h1>Repository</h1>
      <p>Manage your data repository here.</p>
    </div>
  `,
    styles: [`
    .page-container {
      padding: 20px;
      padding-top: 80px; /* Account for fixed navbar */
    }
  `]
})
export class RepositoryComponent { }
