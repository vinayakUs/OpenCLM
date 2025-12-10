import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-insights',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="page-container">
      <h1>Insights</h1>
      <p>View analytics and insights.</p>
    </div>
  `,
    styles: [`
    .page-container {
      padding: 20px;
      padding-top: 80px; /* Account for fixed navbar */
    }
  `]
})
export class InsightsComponent { }
