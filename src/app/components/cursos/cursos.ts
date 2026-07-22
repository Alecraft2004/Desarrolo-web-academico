import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Curso } from '../../models/curso';
import { Profesor } from '../../models/profesor';
import { GlassSelectComponent, GlassOption } from '../glass-select/glass-select';

@Component({
  selector: 'app-cursos',
  imports: [FormsModule, CommonModule, GlassSelectComponent],
  templateUrl: './cursos.html'
})
export class CursosComponent implements OnInit {
  cursos: Curso[] = [];
  profesores: Profesor[] = [];
  form: Curso = { nombre: '', descripcion: '', creditos: null, cupo: null, profesorId: null };
  editandoId: number | null = null;
  removiendoId: number | null = null;
  confirmandoId: number | null = null;
  cargando = false;
  formShake = false;
  btnEstado: 'idle' | 'cargando' | 'ok' | 'error' = 'idle';
  mensaje = '';
  esError = false;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.cargar();
    this.api.getAll<Profesor>('profesores').subscribe({ next: data => this.profesores = data });
  }

  nombreProfesor(id: number | null | undefined): string {
    if (id == null) return '—';
    return this.profesores.find(p => p.id === id)?.nombre ?? `#${id}`;
  }

  profesorOcupado(profesorId: number | null | undefined): boolean {
    if (profesorId == null) return false;
    const asignados = this.cursos.filter(c => c.profesorId === profesorId && c.id !== this.editandoId).length;
    return asignados >= 3;
  }

  get opcionesProfesores(): GlassOption[] {
    return this.profesores.map(p => ({
      value: p.id!,
      label: p.nombre,
      disabled: this.profesorOcupado(p.id),
      badge: this.profesorOcupado(p.id) ? 'Ocupado' : undefined
    }));
  }

  cargar() {
    this.cargando = true;
    this.api.getAll<Curso>('cursos').subscribe({
      next: data => { this.cursos = data; this.cargando = false; },
      error: () => { this.mostrarMsg('Error al cargar cursos', true); this.cargando = false; }
    });
  }

  guardar() {
    if (!this.form.nombre) {
      this.sacudir();
      return this.mostrarMsg('El nombre es requerido', true);
    }
    const eraEdicion = this.editandoId !== null;
    this.btnEstado = 'cargando';
    const op = eraEdicion
      ? this.api.update<Curso>('cursos', this.editandoId!, this.form)
      : this.api.create<Curso>('cursos', this.form);
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

  editar(c: Curso) {
    this.editandoId = c.id!;
    this.form = { nombre: c.nombre, descripcion: c.descripcion, creditos: c.creditos, cupo: c.cupo, profesorId: c.profesorId ?? null };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  get hayNegativo(): boolean {
    const c = this.form.creditos, cu = this.form.cupo;
    return (c !== null && c < 0) || (cu !== null && cu < 0);
  }

  pedirConfirmacion(id: number) { this.confirmandoId = id; }
  cancelarConfirmacion() { this.confirmandoId = null; }

  eliminar(id: number) {
    this.confirmandoId = null;
    this.removiendoId = id;
    setTimeout(() => {
      this.api.delete('cursos', id).subscribe({
        next: () => { this.removiendoId = null; this.cargar(); },
        error: () => { this.removiendoId = null; this.mostrarMsg('Error al eliminar', true); }
      });
    }, 360);
  }

  cancelar() {
    this.form = { nombre: '', descripcion: '', creditos: null, cupo: null, profesorId: null };
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
