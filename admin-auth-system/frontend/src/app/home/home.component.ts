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
                this.content = JSON.parse(err.error).message || 'Error fetching data';
                this.error = 'Failed to load content';
            }
        });
    }

    logout(): void {
        this.authService.logout();
    }
}
