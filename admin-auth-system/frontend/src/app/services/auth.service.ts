import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private AUTH_API = 'http://localhost:8080/auth/'; // Gateway URL

    constructor(private http: HttpClient, private router: Router) { }

    login(credentials: any): Observable<any> {
        return this.http.post(this.AUTH_API + 'login', credentials).pipe(
            tap((response: any) => {
                if (response.token) {
                    localStorage.setItem('accessToken', response.token);
                    localStorage.setItem('refreshToken', response.refreshToken);
                    localStorage.setItem('roles', JSON.stringify(response.roles));
                }
            })
        );
    }

    signup(user: any): Observable<any> {
        return this.http.post(this.AUTH_API + 'signup', user);
    }

    logout(): void {
        localStorage.clear();
        this.router.navigate(['/login']);
    }

    getToken(): string | null {
        return localStorage.getItem('accessToken');
    }

    isLoggedIn(): boolean {
        return !!this.getToken();
    }

    isAdmin(): boolean {
        const roles = localStorage.getItem('roles');
        if (roles) {
            return roles.includes('ROLE_ADMIN');
        }
        return false;
    }
}
