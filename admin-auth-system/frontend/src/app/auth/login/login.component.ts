import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
    loginForm: FormGroup;
    isLoggedIn = false;
    isLoginFailed = false;
    errorMessage = '';

    constructor(private authService: AuthService, private router: Router, private fb: FormBuilder) {
        this.loginForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required]
        });
    }

    ngOnInit(): void {
        if (this.authService.isLoggedIn()) {
            this.isLoggedIn = true;
            this.router.navigate(['/home']);
        }
    }

    onSubmit(): void {
        if (this.loginForm.invalid) return;

        const { email, password } = this.loginForm.value;

        this.authService.login({ email, password }).subscribe({
            next: data => {
                this.isLoginFailed = false;
                this.isLoggedIn = true;
                this.router.navigate(['/home']);
            },
            error: err => {
                this.errorMessage = err.error.message || 'Login failed';
                this.isLoginFailed = true;
            }
        });
    }
}
