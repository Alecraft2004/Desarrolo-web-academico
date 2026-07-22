import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Estudiante } from '../../models/estudiante';
import { HistorialAcademico } from '../../models/historial';

@Component({
  selector: 'app-estudiantes',
  imports: [FormsModule, CommonModule],
  templateUrl: './estudiantes.html'
})
export class EstudiantesComponent implements OnInit {
  estudiantes: Estudiante[] = [];
  form: Estudiante = { nombre: '', email: '', carrera: '', semestre: null };
  editandoId: number | null = null;
  removiendoId: number | null = null;
  confirmandoId: number | null = null;
  cargando = false;
  formShake = false;
  btnEstado: 'idle' | 'cargando' | 'ok' | 'error' = 'idle';
  mensaje = '';
  esError = false;

  historial: HistorialAcademico | null = null;
  cargandoHistorial = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.cargando = true;
    this.api.getAll<Estudiante>('estudiantes').subscribe({
      next: data => { this.estudiantes = data; this.cargando = false; },
      error: () => { this.mostrarMsg('Error al cargar estudiantes', true); this.cargando = false; }
    });
  }

  guardar() {
    if (!this.form.nombre || !this.form.email) {
      this.sacudir();
      return this.mostrarMsg('Nombre y Email son requeridos', true);
    }
    const eraEdicion = this.editandoId !== null;
    this.btnEstado = 'cargando';
    const op = this.editandoId !== null
      ? this.api.update<Estudiante>('estudiantes', this.editandoId, this.form)
      : this.api.create<Estudiante>('estudiantes', this.form);
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
          : (err.error?.error || 'Error al guardar. Verifica los datos.');
        this.mostrarMsg(msg, true);
        setTimeout(() => this.btnEstado = 'idle', 1500);
      }
    });
  }

  editar(e: Estudiante) {
    this.editandoId = e.id!;
    this.form = { nombre: e.nombre, email: e.email, carrera: e.carrera, semestre: e.semestre };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  get hayNegativo(): boolean { const s = this.form.semestre; return s !== null && s < 0; }

  pedirConfirmacion(id: number) { this.confirmandoId = id; }
  cancelarConfirmacion() { this.confirmandoId = null; }

  eliminar(id: number) {
    this.confirmandoId = null;
    this.removiendoId = id;
    setTimeout(() => {
      this.api.delete('estudiantes', id).subscribe({
        next: () => { this.removiendoId = null; this.cargar(); },
        error: () => { this.removiendoId = null; this.mostrarMsg('Error al eliminar', true); }
      });
    }, 360);
  }

  cancelar() {
    this.form = { nombre: '', email: '', carrera: '', semestre: null };
    this.editandoId = null;
  }

  verHistorial(id: number) {
    this.cargandoHistorial = true;
    this.historial = null;
    this.api.getAll<HistorialAcademico>(`estudiantes/${id}/historial`).subscribe({
      next: data => {
        this.historial = data as unknown as HistorialAcademico;
        this.cargandoHistorial = false;
      },
      error: () => {
        this.cargandoHistorial = false;
        this.mostrarMsg('Error al cargar el historial académico', true);
      }
    });
  }

  cerrarHistorial() {
    this.historial = null;
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
