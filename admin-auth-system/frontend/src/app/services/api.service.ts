import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Workflow, PageResponse } from '../models/workflow.model';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private API_URL = environment.apiBaseUrl + '/'; // Gateway URL

    constructor(private http: HttpClient) { }

    getHomeContent(): Observable<any> {
        return this.http.get(this.API_URL + 'home', { responseType: 'text', withCredentials: true });
    }

    getWorkflows(page: number, size: number): Observable<ApiResponse<PageResponse<Workflow>>> {
        return this.http.get<ApiResponse<PageResponse<Workflow>>>(this.API_URL + 'workflows/', {
            params: { page: page.toString(), size: size.toString() },
            withCredentials: true
        });
    }

    convertDocument(html: string): Observable<Blob> {
        return this.http.post(this.API_URL + 'documents/convert', { html }, {
            responseType: 'blob',
            withCredentials: true
        });
    }

    uploadDocument(formData: FormData): Observable<any> {
        return this.http.post<any>(this.API_URL + 'documents/upload', formData, {
            withCredentials: true
        });
    }

    createWorkflow(formData: FormData): Observable<ApiResponse<any>> {
        return this.http.post<ApiResponse<any>>(this.API_URL + 'workflows/', formData, {
            withCredentials: true
        });
    }
}
