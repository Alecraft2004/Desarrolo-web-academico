export interface Curso {
  id?: number;
  nombre: string;
  descripcion: string;
  creditos: number | null;
  cupo: number | null;
  profesorId?: number | null;
}
