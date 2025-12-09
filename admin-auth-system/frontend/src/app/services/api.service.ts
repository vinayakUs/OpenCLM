import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private API_URL = environment.apiBaseUrl + '/'; // Gateway URL

    constructor(private http: HttpClient) { }

    getHomeContent(): Observable<any> {
        return this.http.get(this.API_URL + 'home', { responseType: 'text', withCredentials: true });
    }
}
