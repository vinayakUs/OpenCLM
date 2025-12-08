import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private AUTH_API = 'http://localhost:8080/auth/'; // Gateway URL for Signup
    private BFF_LOGIN_URL = 'http://localhost:8082/oauth2/authorization/bff-client';

    constructor(private http: HttpClient, private router: Router) { }

    login(credentials: any): void {
        // Ignores credentials, redirects to OAuth2
        window.location.href = this.BFF_LOGIN_URL;
    }

    signup(user: any): Observable<any> {
        return this.http.post(this.AUTH_API + 'signup', user);
    }

    logout(): void {
        window.location.href = 'http://localhost:8082/logout';
    }

    isLoggedIn(): Observable<boolean> {
        // Check if we can access a protected resource
        return this.http.get('http://localhost:8080/api/home', { responseType: 'text' }).pipe(
            map(() => true),
            catchError(() => of(false))
        );
    }

    isAdmin(): Observable<boolean> {
        return this.isLoggedIn();
    }
}
