package com.example.appweb;

public class Estudiante {

	private int id;
	private String nombre;
	private String email;
	private String carrera;
	private int semestre;

	public Estudiante() {
	}

	public Estudiante(int id, String nombre, String email, String carrera, int semestre) {
		this.id = id;
		this.nombre = nombre;
		this.email = email;
		this.carrera = carrera;
		this.semestre = semestre;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getCarrera() { return carrera; }
	public void setCarrera(String carrera) { this.carrera = carrera; }
	public int getSemestre() { return semestre; }
	public void setSemestre(int semestre) { this.semestre = semestre; }
}
