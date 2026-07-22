export interface CursoHistorialItem {
  curso: string;
  creditos: number;
  fecha: string;
  calificacion: number;
  estado: 'APROBADO' | 'REPROBADO' | 'EN_CURSO';
  profesor: string | null;
}

export interface HistorialAcademico {
  estudiante: string;
  cursos: CursoHistorialItem[];
  creditosAprobados: number;
  promedioPonderado: number;
  cursosAprobados: number;
  cursosReprobados: number;
  cursosEnCurso: number;
}
