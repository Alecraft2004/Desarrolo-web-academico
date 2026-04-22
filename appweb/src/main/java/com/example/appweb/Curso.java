package com.example.appweb;

public class Curso {

	private int id;
	private String nombre;
	private String descripcion;
	private int creditos;
	private int cupo;

	public Curso() {
	}

	public Curso(int id, String nombre, String descripcion, int creditos, int cupo) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.creditos = creditos;
		this.cupo = cupo;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public int getCreditos() { return creditos; }
	public void setCreditos(int creditos) { this.creditos = creditos; }
	public int getCupo() { return cupo; }
	public void setCupo(int cupo) { this.cupo = cupo; }
}
