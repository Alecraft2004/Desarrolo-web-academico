export interface Inscripcion {
  id?: number;
  estudianteId: number | null;
  cursoId: number | null;
  fecha: string;
  calificacion: number | null;
}
