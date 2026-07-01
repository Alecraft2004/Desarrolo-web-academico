import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  // Caso 1: login hace POST a /auth/login y guarda el token
  it('should POST to /auth/login and store token', () => {
    const mockResponse = { token: 'jwt-abc', username: 'admin1', rol: 'ADMIN' };

    service.login('admin1', '1234').subscribe(data => {
      expect(data.token).toBe('jwt-abc');
      expect(localStorage.getItem('token')).toBe('jwt-abc');
      expect(localStorage.getItem('username')).toBe('admin1');
      expect(localStorage.getItem('rol')).toBe('ADMIN');
    });

    const req = httpMock.expectOne('http://localhost:8080/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username: 'admin1', password: '1234' });
    req.flush(mockResponse);
  });

  // Caso 2: register hace POST a /auth/register
  it('should POST to /auth/register', () => {
    const mockResponse = { token: 'jwt-xyz', username: 'nuevo', rol: 'USER' };

    service.register('nuevo', 'pass123', 'USER').subscribe(data => {
      expect(data.token).toBe('jwt-xyz');
    });

    const req = httpMock.expectOne('http://localhost:8080/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username: 'nuevo', password: 'pass123', rol: 'USER' });
    req.flush(mockResponse);
  });

  // Caso 3: logout limpia el localStorage
  it('should clear localStorage on logout', () => {
    localStorage.setItem('token', 'jwt-test');
    localStorage.setItem('username', 'admin1');
    localStorage.setItem('rol', 'ADMIN');

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('username')).toBeNull();
    expect(localStorage.getItem('rol')).toBeNull();
  });

  // Caso 4: isLoggedIn devuelve true si hay token
  it('should return true from isLoggedIn when token exists', () => {
    localStorage.setItem('token', 'jwt-test');
    expect(service.isLoggedIn()).toBe(true);
  });

  // Caso 5: isLoggedIn devuelve false si no hay token
  it('should return false from isLoggedIn when no token', () => {
    expect(service.isLoggedIn()).toBe(false);
  });

  // Caso 6: getUsername y getRol devuelven los valores guardados
  it('should return stored username and role', () => {
    localStorage.setItem('username', 'admin1');
    localStorage.setItem('rol', 'ADMIN');

    expect(service.getUsername()).toBe('admin1');
    expect(service.getRol()).toBe('ADMIN');
  });
});
