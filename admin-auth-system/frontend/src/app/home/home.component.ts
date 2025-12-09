import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
    content?: string;
    error?: string;

    constructor(public authService: AuthService, private apiService: ApiService) { }

    ngOnInit(): void {
        this.apiService.getHomeContent().subscribe({
            next: data => {
                this.content = data;
            },
            error: err => {
                console.error('Error fetching home content:', err);
                let message = 'Error fetching data';

                if (err.error) {
                    if (typeof err.error === 'string') {
                        try {
                            const parsed = JSON.parse(err.error);
                            message = parsed.message || err.error;
                        } catch (e) {
                            // If parsing fails, it's likely HTML or plain text
                            message = err.error;
                        }
                    } else if (err.error.message) {
                        message = err.error.message;
                    }
                }

                this.content = message;
                this.error = 'Failed to load content';
            }
        });
    }

    logout(): void {
        this.authService.logout();
    }
}
