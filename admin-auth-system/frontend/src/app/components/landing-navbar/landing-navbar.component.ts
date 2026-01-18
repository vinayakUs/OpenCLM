import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-landing-navbar',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './landing-navbar.component.html'
})
export class LandingNavbarComponent {

    constructor(private authService: AuthService, private router: Router) { }

    setView(view: string) {
        if (view === 'login') {
            this.authService.login({});
        } else if (view === 'signup') {
            this.router.navigate(['/signup']);
        } else if (view === 'landing') {
            this.router.navigate(['/']);
        }
    }
}
