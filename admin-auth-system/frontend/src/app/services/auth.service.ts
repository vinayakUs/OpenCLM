import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, shareReplay } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private AUTH_API = environment.authBaseUrl + '/'; // Gateway URL for Signup
    private BFF_LOGIN_URL = environment.apiBaseUrl + '/oauth2/authorization/bff-client';
    private userCache$: Observable<any> | null = null;

    constructor(private http: HttpClient, private router: Router) { }

    login(credentials: any): void {
        // Ignores credentials, redirects to OAuth2
        window.location.href = this.BFF_LOGIN_URL;
    }

    signup(user: any): Observable<any> {
        return this.http.post(this.AUTH_API + 'signup', user);
    }

    logout(): void {
        this.userCache$ = null; // Clear internal cache
        window.location.href = environment.apiBaseUrl + '/logout';
    }

    getUser(): Observable<any> {
        if (!this.userCache$) {
            // Add timestamp to prevent browser caching
            const timestamp = new Date().getTime();
            this.userCache$ = this.http.get(`${environment.apiBaseUrl}/user?t=${timestamp}`, { withCredentials: true }).pipe(
                shareReplay(1),
                catchError(err => {
                    this.userCache$ = null; // Reset cache on error so we can retry later
                    return of(null); // Return null instead of erroring out
                })
            );
        }
        return this.userCache$;
    }

    isLoggedIn(): Observable<boolean> {
        return this.getUser().pipe(
            map(user => !!user && Object.keys(user).length > 0)
        );
    }

    isAdmin(): Observable<boolean> {
        return this.isLoggedIn(); // For now, just check if logged in. Add role check later if needed.
    }
}
