import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthIllustrationComponent } from '../auth-illustration/auth-illustration';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, AuthIllustrationComponent],
  templateUrl: './login.html'
})
export class LoginComponent {
  username = '';
  password = '';
  mensaje = '';
  esError = false;
  mostrarPassword = false;
  btnEstado: 'idle' | 'cargando' | 'ok' | 'error' = 'idle';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.btnEstado = 'cargando';
    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        this.btnEstado = 'ok';
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.btnEstado = 'error';
        this.mensaje = err.error?.error || 'Credenciales invalidas';
        this.esError = true;
        setTimeout(() => this.btnEstado = 'idle', 1500);
      }
    });
  }
}
