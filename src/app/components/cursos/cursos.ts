import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Curso } from '../../models/curso';

@Component({
  selector: 'app-cursos',
  imports: [FormsModule, CommonModule],
  templateUrl: './cursos.html'
})
export class CursosComponent implements OnInit {
  cursos: Curso[] = [];
  form: Curso = { nombre: '', descripcion: '', creditos: 0, cupo: 0 };
  editandoId: number | null = null;
  mensaje = '';
  esError = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.api.getAll<Curso>('cursos').subscribe({
      next: data => this.cursos = data,
      error: () => this.mostrarMsg('Error al cargar cursos', true)
    });
  }

  guardar() {
    if (!this.form.nombre) {
      return this.mostrarMsg('El nombre es requerido', true);
    }
    const op = this.editandoId !== null
      ? this.api.update<Curso>('cursos', this.editandoId, this.form)
      : this.api.create<Curso>('cursos', this.form);
    op.subscribe({
      next: () => {
        this.mostrarMsg(this.editandoId ? 'Actualizado correctamente' : 'Registrado correctamente', false);
        this.cancelar();
        this.cargar();
      },
      error: () => this.mostrarMsg('Error al guardar', true)
    });
  }

  editar(c: Curso) {
    this.editandoId = c.id!;
    this.form = { nombre: c.nombre, descripcion: c.descripcion, creditos: c.creditos, cupo: c.cupo };
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar este curso?')) return;
    this.api.delete('cursos', id).subscribe({ next: () => this.cargar() });
  }

  cancelar() {
    this.form = { nombre: '', descripcion: '', creditos: 0, cupo: 0 };
    this.editandoId = null;
  }

  private mostrarMsg(texto: string, esError: boolean) {
    this.mensaje = texto;
    this.esError = esError;
    setTimeout(() => this.mensaje = '', 3000);
  }
}
