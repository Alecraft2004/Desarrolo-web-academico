package com.example.appweb.controllers;

import com.example.appweb.models.Curso;
import com.example.appweb.repositories.CursoRepository;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cursos")
public class RestCurso {

	@Autowired
	private CursoRepository repo;

	@GetMapping
	public List<Curso> listar() {
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	private static final int MAX_CURSOS_POR_PROFESOR = 3;

	@PostMapping
	public ResponseEntity<?> registrar(@RequestBody Curso nuevo) {
		if (nuevo.getNombre() != null && !nuevo.getNombre().isBlank()) {
			boolean duplicado = repo.findByNombre(nuevo.getNombre()).isPresent();
			if (duplicado) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "Ya existe un curso con el nombre: " + nuevo.getNombre()));
			}
		}
		if (nuevo.getProfesorId() != null && repo.countByProfesorId(nuevo.getProfesorId()) >= MAX_CURSOS_POR_PROFESOR) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("error", "El profesor ya tiene el máximo de " + MAX_CURSOS_POR_PROFESOR + " cursos asignados"));
		}
		return ResponseEntity.ok(repo.save(nuevo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Curso datos) {
		return repo.findById(id).map(c -> {
			if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
				var existente = repo.findByNombre(datos.getNombre());
				if (existente.isPresent() && existente.get().getId() != id) {
					return ResponseEntity.status(HttpStatus.CONFLICT)
							.<Object>body(Map.of("error", "Ya existe otro curso con el nombre: " + datos.getNombre()));
				}
			}
			boolean cambiaProfesor = datos.getProfesorId() != null && !datos.getProfesorId().equals(c.getProfesorId());
			if (cambiaProfesor && repo.countByProfesorId(datos.getProfesorId()) >= MAX_CURSOS_POR_PROFESOR) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.<Object>body(Map.of("error", "El profesor ya tiene el máximo de " + MAX_CURSOS_POR_PROFESOR + " cursos asignados"));
			}
			c.setNombre(datos.getNombre());
			c.setDescripcion(datos.getDescripcion());
			c.setCreditos(datos.getCreditos());
			c.setCupo(datos.getCupo());
			c.setProfesorId(datos.getProfesorId());
			return ResponseEntity.<Object>ok(repo.save(c));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		return repo.findById(id).map(c -> {
			repo.deleteById(id);
			return ResponseEntity.ok(c);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
