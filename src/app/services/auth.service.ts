import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private BASE = 'http://localhost:8080';

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.BASE}/auth/login`, { username, password }).pipe(
      tap(data => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('username', data.username);
        localStorage.setItem('rol', data.rol);
      })
    );
  }

  register(username: string, password: string, rol: string): Observable<any> {
    return this.http.post<any>(`${this.BASE}/auth/register`, { username, password, rol });
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('rol');
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean { return !!localStorage.getItem('token'); }
  getUsername(): string { return localStorage.getItem('username') || ''; }
  getRol(): string { return localStorage.getItem('rol') || ''; }
}
