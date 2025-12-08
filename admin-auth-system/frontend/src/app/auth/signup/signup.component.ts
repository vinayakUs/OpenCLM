import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-signup',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './signup.component.html',
    styleUrls: ['./signup.component.css']
})
export class SignupComponent {
    signupForm: FormGroup;
    isSuccessful = false;
    isSignUpFailed = false;
    errorMessage = '';

    constructor(private authService: AuthService, private router: Router, private fb: FormBuilder) {
        this.signupForm = this.fb.group({
            name: ['', [Validators.required, Validators.minLength(3)]],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    onSubmit(): void {
        if (this.signupForm.invalid) return;

        this.authService.signup(this.signupForm.value).subscribe({
            next: data => {
                this.isSuccessful = true;
                this.isSignUpFailed = false;
                setTimeout(() => this.router.navigate(['/login']), 2000);
            },
            error: err => {
                this.errorMessage = err.error.message || 'Signup failed';
                this.isSignUpFailed = true;
            }
        });
    }
}
