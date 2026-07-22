package com.example.appweb.controllers;

import com.example.appweb.models.Profesor;
import com.example.appweb.repositories.ProfesorRepository;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profesores")
public class RestProfesor {

	@Autowired
	private ProfesorRepository repo;

	@GetMapping
	public List<Profesor> listar() {
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping
	public ResponseEntity<?> registrar(@RequestBody Profesor nuevo) {
		if (nuevo.getEmail() != null && !nuevo.getEmail().isBlank()) {
			boolean duplicado = repo.findByEmail(nuevo.getEmail()).isPresent();
			if (duplicado) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "Ya existe un profesor con el email: " + nuevo.getEmail()));
			}
		}
		return ResponseEntity.ok(repo.save(nuevo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Profesor datos) {
		return repo.findById(id).map(p -> {
			if (datos.getEmail() != null && !datos.getEmail().isBlank()) {
				var existente = repo.findByEmail(datos.getEmail());
				if (existente.isPresent() && existente.get().getId() != id) {
					return ResponseEntity.status(HttpStatus.CONFLICT)
							.<Object>body(Map.of("error", "Ya existe otro profesor con el email: " + datos.getEmail()));
				}
			}
			p.setNombre(datos.getNombre());
			p.setEmail(datos.getEmail());
			p.setDepartamento(datos.getDepartamento());
			p.setEspecialidad(datos.getEspecialidad());
			return ResponseEntity.<Object>ok(repo.save(p));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		return repo.findById(id).map(p -> {
			repo.deleteById(id);
			return ResponseEntity.ok(p);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
