package com.example.appweb.controllers;

import com.example.appweb.models.Usuario;
import com.example.appweb.repositories.UsuarioRepository;
import com.example.appweb.security.JwtService;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UsuarioRepository repo;
	private final PasswordEncoder encoder;
	private final JwtService jwtService;

	public AuthController(UsuarioRepository repo, PasswordEncoder encoder, JwtService jwtService) {
		this.repo = repo;
		this.encoder = encoder;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		if (repo.findByUsername(usuario.getUsername()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("error", "El usuario ya existe"));
		}

		String rol = usuario.getRol() == null ? "" : usuario.getRol().trim().toUpperCase();
		if (rol.isBlank()) {
			rol = "USER";
		} else if (!rol.equals("ADMIN") && !rol.equals("USER")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "El rol debe ser ADMIN o USER"));
		}
		usuario.setRol(rol);
		usuario.setPassword(encoder.encode(usuario.getPassword()));
		repo.save(usuario);

		String token = jwtService.generarToken(usuario.getUsername(), usuario.getRol());
		return ResponseEntity.ok(Map.of(
				"mensaje", "Usuario registrado correctamente",
				"username", usuario.getUsername(),
				"rol", usuario.getRol(),
				"token", token,
				"tipo", "Bearer"));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario credenciales) {
		Optional<Usuario> opt = repo.findByUsername(credenciales.getUsername());
		if (opt.isEmpty() || !encoder.matches(credenciales.getPassword(), opt.get().getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Credenciales invalidas"));
		}
		Usuario u = opt.get();
		String token = jwtService.generarToken(u.getUsername(), u.getRol());
		return ResponseEntity.ok(Map.of(
				"token", token,
				"tipo", "Bearer",
				"username", u.getUsername(),
				"rol", u.getRol()));
	}
}
