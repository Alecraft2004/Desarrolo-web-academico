import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html'
})
export class RegisterComponent {
  username = '';
  password = '';
  rol = 'USER';
  mensaje = '';
  esError = false;

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    this.auth.register(this.username, this.password, this.rol).subscribe({
      next: () => {
        this.mensaje = 'Cuenta creada. Redirigiendo...';
        this.esError = false;
        setTimeout(() => this.router.navigate(['/login']), 900);
      },
      error: (err) => {
        this.mensaje = err.error?.error || 'No se pudo registrar';
        this.esError = true;
      }
    });
  }
}
