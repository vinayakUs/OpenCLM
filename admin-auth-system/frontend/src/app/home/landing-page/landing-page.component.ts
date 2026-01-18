import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LandingNavbarComponent } from '../../components/landing-navbar/landing-navbar.component';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-landing-page',
    standalone: true,
    imports: [CommonModule, RouterModule, LandingNavbarComponent],
    templateUrl: './landing-page.component.html',
    styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent {
    constructor(private router: Router, private authService: AuthService) { }

    setView(view: string) {
        if (view === 'signup') {
            this.router.navigate(['/signup']);
        } else if (view === 'sso') {
            this.authService.login({});
        } else if (view === 'login') {
            this.authService.login({});
        }
    }
}
