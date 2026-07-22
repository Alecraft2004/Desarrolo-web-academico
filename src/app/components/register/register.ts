import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthIllustrationComponent } from '../auth-illustration/auth-illustration';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink, AuthIllustrationComponent],
  templateUrl: './register.html'
})
export class RegisterComponent {
  username = '';
  password = '';
  rol = 'USER';
  mensaje = '';
  esError = false;
  mostrarPassword = false;
  btnEstado: 'idle' | 'cargando' | 'ok' | 'error' = 'idle';

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    if (!this.username.trim() || !this.password.trim()) {
      this.mensaje = 'Usuario y contraseña son requeridos';
      this.esError = true;
      return;
    }
    this.btnEstado = 'cargando';
    this.auth.register(this.username, this.password, this.rol).subscribe({
      next: () => {
        this.btnEstado = 'ok';
        this.mensaje = 'Cuenta creada exitosamente. Redirigiendo...';
        this.esError = false;
        setTimeout(() => this.router.navigate(['/login']), 1400);
      },
      error: (err) => {
        this.btnEstado = 'error';
        const serverMsg = err.error?.error || '';
        if (err.status === 409 || serverMsg.toLowerCase().includes('ya existe')) {
          this.mensaje = `El usuario "${this.username}" ya está registrado. Elige otro nombre.`;
        } else {
          this.mensaje = serverMsg || 'No se pudo crear la cuenta. Intenta de nuevo.';
        }
        this.esError = true;
        setTimeout(() => this.btnEstado = 'idle', 1500);
      }
    });
  }
}
