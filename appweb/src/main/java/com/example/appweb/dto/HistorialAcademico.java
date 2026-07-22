package com.example.appweb.dto;

import java.util.List;

public class HistorialAcademico {

	private String estudiante;
	private List<CursoHistorialItem> cursos;
	private int creditosAprobados;
	private double promedioPonderado;
	private int cursosAprobados;
	private int cursosReprobados;
	private int cursosEnCurso;

	public HistorialAcademico(String estudiante, List<CursoHistorialItem> cursos, int creditosAprobados,
			double promedioPonderado, int cursosAprobados, int cursosReprobados, int cursosEnCurso) {
		this.estudiante = estudiante;
		this.cursos = cursos;
		this.creditosAprobados = creditosAprobados;
		this.promedioPonderado = promedioPonderado;
		this.cursosAprobados = cursosAprobados;
		this.cursosReprobados = cursosReprobados;
		this.cursosEnCurso = cursosEnCurso;
	}

	public String getEstudiante() { return estudiante; }
	public List<CursoHistorialItem> getCursos() { return cursos; }
	public int getCreditosAprobados() { return creditosAprobados; }
	public double getPromedioPonderado() { return promedioPonderado; }
	public int getCursosAprobados() { return cursosAprobados; }
	public int getCursosReprobados() { return cursosReprobados; }
	public int getCursosEnCurso() { return cursosEnCurso; }
}
