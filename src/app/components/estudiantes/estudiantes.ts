import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Estudiante } from '../../models/estudiante';

@Component({
  selector: 'app-estudiantes',
  imports: [FormsModule, CommonModule],
  templateUrl: './estudiantes.html'
})
export class EstudiantesComponent implements OnInit {
  estudiantes: Estudiante[] = [];
  form: Estudiante = { nombre: '', email: '', carrera: '', semestre: 0 };
  editandoId: number | null = null;
  mensaje = '';
  esError = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.getAll<Estudiante>('estudiantes').subscribe({
      next: data => this.estudiantes = data,
      error: () => this.mostrarMsg('Error al cargar estudiantes', true)
    });
  }

  guardar() {
    if (!this.form.nombre || !this.form.email) {
      return this.mostrarMsg('Nombre y Email son requeridos', true);
    }
    const op = this.editandoId !== null
      ? this.api.update<Estudiante>('estudiantes', this.editandoId, this.form)
      : this.api.create<Estudiante>('estudiantes', this.form);
    op.subscribe({
      next: () => {
        this.mostrarMsg(this.editandoId ? 'Actualizado correctamente' : 'Registrado correctamente', false);
        this.cancelar();
        this.cargar();
      },
      error: () => this.mostrarMsg('Error al guardar. Verifica los datos.', true)
    });
  }

  editar(e: Estudiante) {
    this.editandoId = e.id!;
    this.form = { nombre: e.nombre, email: e.email, carrera: e.carrera, semestre: e.semestre };
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar este estudiante?')) return;
    this.api.delete('estudiantes', id).subscribe({ next: () => this.cargar() });
  }

  cancelar() {
    this.form = { nombre: '', email: '', carrera: '', semestre: 0 };
    this.editandoId = null;
  }

  private mostrarMsg(texto: string, esError: boolean) {
    this.mensaje = texto;
    this.esError = esError;
    setTimeout(() => this.mensaje = '', 3000);
  }
}
