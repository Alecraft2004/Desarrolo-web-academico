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
  mensaje = '';
  esError = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.getAll<Profesor>('profesores').subscribe({
      next: data => this.profesores = data,
      error: () => this.mostrarMsg('Error al cargar profesores', true)
    });
  }

  guardar() {
    if (!this.form.nombre || !this.form.email) {
      return this.mostrarMsg('Nombre y Email son requeridos', true);
    }
    const op = this.editandoId !== null
      ? this.api.update<Profesor>('profesores', this.editandoId, this.form)
      : this.api.create<Profesor>('profesores', this.form);
    op.subscribe({
      next: () => {
        this.mostrarMsg(this.editandoId ? 'Actualizado correctamente' : 'Registrado correctamente', false);
        this.cancelar();
        this.cargar();
      },
      error: () => this.mostrarMsg('Error al guardar', true)
    });
  }

  editar(p: Profesor) {
    this.editandoId = p.id!;
    this.form = { nombre: p.nombre, email: p.email, departamento: p.departamento, especialidad: p.especialidad };
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar este profesor?')) return;
    this.api.delete('profesores', id).subscribe({ next: () => this.cargar() });
  }

  cancelar() {
    this.form = { nombre: '', email: '', departamento: '', especialidad: '' };
    this.editandoId = null;
  }

  private mostrarMsg(texto: string, esError: boolean) {
    this.mensaje = texto;
    this.esError = esError;
    setTimeout(() => this.mensaje = '', 3000);
  }
}
