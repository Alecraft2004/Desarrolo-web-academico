import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Inscripcion } from '../../models/inscripcion';

@Component({
  selector: 'app-inscripciones',
  imports: [FormsModule, CommonModule],
  templateUrl: './inscripciones.html'
})
export class InscripcionesComponent implements OnInit {
  inscripciones: Inscripcion[] = [];
  form: Inscripcion = { estudianteId: 0, cursoId: 0, fecha: '', calificacion: 0 };
  editandoId: number | null = null;
  mensaje = '';
  esError = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.getAll<Inscripcion>('inscripciones').subscribe({
      next: data => this.inscripciones = data,
      error: () => this.mostrarMsg('Error al cargar inscripciones', true)
    });
  }

  guardar() {
    if (!this.form.estudianteId || !this.form.cursoId) {
      return this.mostrarMsg('ID de Estudiante y Curso son requeridos', true);
    }
    const op = this.editandoId !== null
      ? this.api.update<Inscripcion>('inscripciones', this.editandoId, this.form)
      : this.api.create<Inscripcion>('inscripciones', this.form);
    op.subscribe({
      next: () => {
        this.mostrarMsg(this.editandoId ? 'Actualizado correctamente' : 'Registrado correctamente', false);
        this.cancelar();
        this.cargar();
      },
      error: () => this.mostrarMsg('Error al guardar', true)
    });
  }

  editar(i: Inscripcion) {
    this.editandoId = i.id!;
    this.form = { estudianteId: i.estudianteId, cursoId: i.cursoId, fecha: i.fecha, calificacion: i.calificacion };
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar esta inscripcion?')) return;
    this.api.delete('inscripciones', id).subscribe({ next: () => this.cargar() });
  }

  cancelar() {
    this.form = { estudianteId: 0, cursoId: 0, fecha: '', calificacion: 0 };
    this.editandoId = null;
  }

  private mostrarMsg(texto: string, esError: boolean) {
    this.mensaje = texto;
    this.esError = esError;
    setTimeout(() => this.mensaje = '', 3000);
  }
}
