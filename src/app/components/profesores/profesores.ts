import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Profesor } from '../../models/profesor';

@Component({
  selector: 'app-profesores',
  imports: [FormsModule, CommonModule],
  templateUrl: './profesores.html'
})
export class ProfesoresComponent implements OnInit {
  profesores: Profesor[] = [];
  form: Profesor = { nombre: '', email: '', departamento: '', especialidad: '' };
  editandoId: number | null = null;
  removiendoId: number | null = null;
  confirmandoId: number | null = null;
  cargando = false;
  formShake = false;
  btnEstado: 'idle' | 'cargando' | 'ok' | 'error' = 'idle';
  mensaje = '';
  esError = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.cargando = true;
    this.api.getAll<Profesor>('profesores').subscribe({
      next: data => { this.profesores = data; this.cargando = false; },
      error: () => { this.mostrarMsg('Error al cargar profesores', true); this.cargando = false; }
    });
  }

  guardar() {
    if (!this.form.nombre || !this.form.email) {
      this.sacudir();
      return this.mostrarMsg('Nombre y Email son requeridos', true);
    }
    const eraEdicion = this.editandoId !== null;
    this.btnEstado = 'cargando';
    const op = eraEdicion
      ? this.api.update<Profesor>('profesores', this.editandoId!, this.form)
      : this.api.create<Profesor>('profesores', this.form);
    op.subscribe({
      next: () => {
        this.btnEstado = 'ok';
        this.mostrarMsg(eraEdicion ? 'Actualizado correctamente' : 'Registrado correctamente', false);
        this.cancelar();
        this.cargar();
        setTimeout(() => { this.btnEstado = 'idle'; }, 900);
      },
      error: (err) => {
        this.btnEstado = 'error';
        this.sacudir();
        const msg = err.name === 'TimeoutError'
          ? 'Sin respuesta del servidor. Verifica que el backend y la base de datos estén activos.'
          : (err.error?.error || 'Error al guardar');
        this.mostrarMsg(msg, true);
        setTimeout(() => this.btnEstado = 'idle', 1500);
      }
    });
  }

  editar(p: Profesor) {
    this.editandoId = p.id!;
    this.form = { nombre: p.nombre, email: p.email, departamento: p.departamento, especialidad: p.especialidad };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  pedirConfirmacion(id: number) { this.confirmandoId = id; }
  cancelarConfirmacion() { this.confirmandoId = null; }

  eliminar(id: number) {
    this.confirmandoId = null;
    this.removiendoId = id;
    setTimeout(() => {
      this.api.delete('profesores', id).subscribe({
        next: () => { this.removiendoId = null; this.cargar(); },
        error: () => { this.removiendoId = null; this.mostrarMsg('Error al eliminar', true); }
      });
    }, 360);
  }

  cancelar() {
    this.form = { nombre: '', email: '', departamento: '', especialidad: '' };
    this.editandoId = null;
  }

  private sacudir() {
    this.formShake = false;
    setTimeout(() => { this.formShake = true; setTimeout(() => this.formShake = false, 450); }, 10);
  }

  private mostrarMsg(texto: string, esError: boolean) {
    this.mensaje = texto;
    this.esError = esError;
    setTimeout(() => this.mensaje = '', 3000);
  }
}
