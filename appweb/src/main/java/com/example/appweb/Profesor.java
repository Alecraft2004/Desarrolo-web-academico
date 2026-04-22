package com.example.appweb;

public class Profesor {

	private int id;
	private String nombre;
	private String email;
	private String departamento;
	private String especialidad;

	public Profesor() {
	}

	public Profesor(int id, String nombre, String email, String departamento, String especialidad) {
		this.id = id;
		this.nombre = nombre;
		this.email = email;
		this.departamento = departamento;
		this.especialidad = especialidad;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getDepartamento() { return departamento; }
	public void setDepartamento(String departamento) { this.departamento = departamento; }
	public String getEspecialidad() { return especialidad; }
	public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
