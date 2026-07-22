package com.example.appweb.dto;

public class CursoHistorialItem {

	private String curso;
	private int creditos;
	private String fecha;
	private double calificacion;
	private String estado;
	private String profesor;

	public CursoHistorialItem(String curso, int creditos, String fecha, double calificacion, String estado, String profesor) {
		this.curso = curso;
		this.creditos = creditos;
		this.fecha = fecha;
		this.calificacion = calificacion;
		this.estado = estado;
		this.profesor = profesor;
	}

	public String getCurso() { return curso; }
	public int getCreditos() { return creditos; }
	public String getFecha() { return fecha; }
	public double getCalificacion() { return calificacion; }
	public String getEstado() { return estado; }
	public String getProfesor() { return profesor; }
}
