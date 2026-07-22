import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Inscripcion } from '../../models/inscripcion';
import { Estudiante } from '../../models/estudiante';
import { Curso } from '../../models/curso';
import { GlassSelectComponent, GlassOption } from '../glass-select/glass-select';

@Component({
  selector: 'app-inscripciones',
  imports: [FormsModule, CommonModule, GlassSelectComponent],
  templateUrl: './inscripciones.html'
})
export class InscripcionesComponent implements OnInit {
  inscripciones: Inscripcion[] = [];
  estudiantes: Estudiante[] = [];
  cursos: Curso[] = [];
  form: Inscripcion = { estudianteId: null, cursoId: null, fecha: '', calificacion: null };
  editandoId: number | null = null;
  removiendoId: number | null = null;
  confirmandoId: number | null = null;
  cargando = false;
  formShake = false;
  btnEstado: 'idle' | 'cargando' | 'ok' | 'error' = 'idle';
  mensaje = '';
  esError = false;

  calificando: Inscripcion | null = null;
  nuevaCalificacion: number | null = null;
  guardandoCalificacion = false;
  errorCalificar = '';

  retirando: Inscripcion | null = null;
  retirandoId: number | null = null;
  errorRetirar = '';

  deshaciendoId: number | null = null;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.cargar();
    this.api.getAll<Estudiante>('estudiantes').subscribe({ next: data => this.estudiantes = data });
    this.api.getAll<Curso>('cursos').subscribe({ next: data => this.cursos = data });
  }

  cargar() {
    this.cargando = true;
    this.api.getAll<Inscripcion>('inscripciones').subscribe({
      next: data => { this.inscripciones = data; this.cargando = false; },
      error: () => { this.mostrarMsg('Error al cargar inscripciones', true); this.cargando = false; }
    });
  }

  nombreEstudiante(id: number | null): string {
    return this.estudiantes.find(e => e.id === id)?.nombre ?? `#${id}`;
  }

  nombreCurso(id: number | null): string {
    return this.cursos.find(c => c.id === id)?.nombre ?? `#${id}`;
  }

  cursoLleno(curso: Curso): boolean {
    if (curso.cupo == null) return false;
    const inscritos = this.inscripciones.filter(i => i.cursoId === curso.id && i.id !== this.editandoId).length;
    return inscritos >= curso.cupo;
  }

  get opcionesEstudiantes(): GlassOption[] {
    return this.estudiantes.map(e => ({ value: e.id!, label: e.nombre }));
  }

  get opcionesCursos(): GlassOption[] {
    return this.cursos.map(c => ({
      value: c.id!,
      label: c.nombre,
      disabled: this.cursoLleno(c),
      badge: this.cursoLleno(c) ? 'Lleno' : undefined
    }));
  }

  guardar() {
    if (!this.form.estudianteId || !this.form.cursoId) {
      this.sacudir();
      return this.mostrarMsg('ID de Estudiante y Curso son requeridos', true);
    }
    const eraEdicion = this.editandoId !== null;
    this.btnEstado = 'cargando';
    const op = eraEdicion
      ? this.api.update<Inscripcion>('inscripciones', this.editandoId!, this.form)
      : this.api.create<Inscripcion>('inscripciones/matricular', { ...this.form, calificacion: this.form.calificacion ?? 0 });
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

  editar(i: Inscripcion) {
    this.editandoId = i.id!;
    this.form = { estudianteId: i.estudianteId, cursoId: i.cursoId, fecha: i.fecha, calificacion: i.calificacion };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  abrirCalificar(i: Inscripcion) {
    this.calificando = i;
    this.nuevaCalificacion = i.calificacion;
    this.errorCalificar = '';
  }

  cerrarCalificar() {
    this.calificando = null;
    this.guardandoCalificacion = false;
  }

  guardarCalificacion() {
    if (this.nuevaCalificacion === null || this.nuevaCalificacion < 0 || this.nuevaCalificacion > 20) {
      this.errorCalificar = 'La calificación debe estar entre 0 y 20';
      return;
    }
    this.guardandoCalificacion = true;
    this.api.put<{ estado: string }>(`inscripciones/${this.calificando!.id}/calificar`, { calificacion: this.nuevaCalificacion })
      .subscribe({
        next: () => {
          this.cerrarCalificar();
          this.cargar();
          this.mostrarMsg('Calificación registrada correctamente', false);
        },
        error: (err) => {
          this.guardandoCalificacion = false;
          this.errorCalificar = err.error?.error || 'Error al registrar la calificación';
        }
      });
  }

  deshacerCalificacion(i: Inscripcion) {
    this.deshaciendoId = i.id!;
    this.api.put<{ estado: string }>(`inscripciones/${i.id}/calificar`, { calificacion: 0 }).subscribe({
      next: () => {
        this.deshaciendoId = null;
        this.cargar();
        this.mostrarMsg('Calificación deshecha, el curso vuelve a estar "En curso"', false);
      },
      error: (err) => {
        this.deshaciendoId = null;
        this.mostrarMsg(err.error?.error || 'Error al deshacer la calificación', true);
      }
    });
  }

  pedirRetiro(i: Inscripcion) {
    this.retirando = i;
    this.errorRetirar = '';
  }

  cancelarRetiro() {
    this.retirando = null;
    this.errorRetirar = '';
  }

  deshacerYReintentarRetiro() {
    const i = this.retirando!;
    this.retirandoId = i.id!;
    this.errorRetirar = '';
    this.api.put<{ estado: string }>(`inscripciones/${i.id}/calificar`, { calificacion: 0 }).subscribe({
      next: () => this.confirmarRetiro(),
      error: (err) => {
        this.retirandoId = null;
        this.errorRetirar = err.error?.error || 'Error al deshacer la calificación';
      }
    });
  }

  confirmarRetiro() {
    const id = this.retirando!.id!;
    this.retirandoId = id;
    this.errorRetirar = '';
    this.api.deletePath(`inscripciones/${id}/retirar`).subscribe({
      next: () => {
        this.retirando = null;
        this.retirandoId = null;
        this.cargar();
        this.mostrarMsg('Baja registrada correctamente', false);
      },
      error: (err) => {
        this.retirandoId = null;
        this.errorRetirar = err.error?.error || 'Error al dar de baja';
      }
    });
  }

  get hayNegativo(): boolean {
    const ei = this.form.estudianteId, ci = this.form.cursoId;
    return (ei !== null && ei < 0) || (ci !== null && ci < 0);
  }

  pedirConfirmacion(id: number) { this.confirmandoId = id; }
  cancelarConfirmacion() { this.confirmandoId = null; }

  eliminar(id: number) {
    this.confirmandoId = null;
    this.removiendoId = id;
    setTimeout(() => {
      this.api.delete('inscripciones', id).subscribe({
        next: () => { this.removiendoId = null; this.cargar(); },
        error: () => { this.removiendoId = null; this.mostrarMsg('Error al eliminar', true); }
      });
    }, 360);
  }

  cancelar() {
    this.form = { estudianteId: null, cursoId: null, fecha: '', calificacion: null };
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
