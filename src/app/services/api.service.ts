import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { timeout } from 'rxjs/operators';

const TIMEOUT_MS = 12000;

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private BASE = 'http://localhost:8080';

  getAll<T>(endpoint: string): Observable<T[]> {
    return this.http.get<T[]>(`${this.BASE}/${endpoint}`).pipe(timeout(TIMEOUT_MS));
  }

  getById<T>(endpoint: string, id: number): Observable<T> {
    return this.http.get<T>(`${this.BASE}/${endpoint}/${id}`).pipe(timeout(TIMEOUT_MS));
  }

  create<T>(endpoint: string, data: T): Observable<T> {
    return this.http.post<T>(`${this.BASE}/${endpoint}`, data).pipe(timeout(TIMEOUT_MS));
  }

  update<T>(endpoint: string, id: number, data: T): Observable<T> {
    return this.http.put<T>(`${this.BASE}/${endpoint}/${id}`, data).pipe(timeout(TIMEOUT_MS));
  }

  put<T>(path: string, data: unknown): Observable<T> {
    return this.http.put<T>(`${this.BASE}/${path}`, data).pipe(timeout(TIMEOUT_MS));
  }

  delete(endpoint: string, id: number): Observable<any> {
    return this.http.delete(`${this.BASE}/${endpoint}/${id}`).pipe(timeout(TIMEOUT_MS));
  }

  deletePath(path: string): Observable<any> {
    return this.http.delete(`${this.BASE}/${path}`).pipe(timeout(TIMEOUT_MS));
  }
}
