package com.example.appweb;

public class Inscripcion {

	private int id;
	private int estudianteId;
	private int cursoId;
	private String fecha;
	private double calificacion;

	public Inscripcion() {
	}

	public Inscripcion(int id, int estudianteId, int cursoId, String fecha, double calificacion) {
		this.id = id;
		this.estudianteId = estudianteId;
		this.cursoId = cursoId;
		this.fecha = fecha;
		this.calificacion = calificacion;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public int getEstudianteId() { return estudianteId; }
	public void setEstudianteId(int estudianteId) { this.estudianteId = estudianteId; }
	public int getCursoId() { return cursoId; }
	public void setCursoId(int cursoId) { this.cursoId = cursoId; }
	public String getFecha() { return fecha; }
	public void setFecha(String fecha) { this.fecha = fecha; }
	public double getCalificacion() { return calificacion; }
	public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
}
