import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule],
    template: `
        <!-- Toast Notification -->
        <!-- Toast Notification -->
        <div *ngIf="logoutMessage" style="position: fixed; top: 1.25rem; right: 1.25rem; z-index: 9999; display: flex; align-items: center; width: 100%; max-width: 20rem; padding: 1rem; background-color: white; border-left: 4px solid #22c55e; border-radius: 0.25rem; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05); transition: opacity 0.3s ease-in-out;">
            <div style="flex-shrink: 0; color: #22c55e;">
                <svg style="width: 1.5rem; height: 1.5rem;" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
            </div>
            <div style="margin-left: 0.75rem; flex: 1;">
                <p style="font-size: 0.875rem; font-weight: 500; color: #16a34a; margin: 0;">
                    {{ logoutMessage }}
                </p>
            </div>
        </div>

        <div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
            <div class="sm:mx-auto sm:w-full sm:max-w-sm">
                <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">Sign in to your account</h2>
            </div>

            <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
                <button (click)="login()" class="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600">
                    Sign in with SSO
                </button>
                 <p class="mt-10 text-center text-sm text-gray-500">
                    Not a member?
                    <a href="/signup" class="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">Sign up</a>
                </p>
            </div>
        </div>
    `
})
export class LoginComponent implements OnInit {
    logoutMessage: string | null = null;

    constructor(
        private authService: AuthService,
        private route: ActivatedRoute,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            if (params['logout'] !== undefined) {
                this.logoutMessage = 'You have been logged out successfully.';
                this.cdr.detectChanges(); // Force update

                // Delay clearing the URL to ensure the user (and the browser renderer) sees the state
                setTimeout(() => {
                    this.router.navigate([], {
                        relativeTo: this.route,
                        queryParams: { logout: null },
                        queryParamsHandling: 'merge',
                        replaceUrl: true
                    });
                }, 500);

                // Auto-hide the toast after 3 seconds
                setTimeout(() => {
                    this.logoutMessage = null;
                    this.cdr.detectChanges();
                }, 3000);
            }
        });
    }

    login() {
        this.authService.login({});
    }
}
