package com.example.appweb;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	private String rol;

	public Usuario() {}

	public Usuario(int id, String username, String password, String rol) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.rol = rol;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getRol() { return rol; }
	public void setRol(String rol) { this.rol = rol; }
}
