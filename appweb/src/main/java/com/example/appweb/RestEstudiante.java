package com.example.appweb;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiantes")
public class RestEstudiante {

	@Autowired
	private EstudianteRepository repo;

	@GetMapping
	public List<Estudiante> listar() {
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Estudiante> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping
	public ResponseEntity<Estudiante> registrar(@RequestBody Estudiante nuevo) {
		return ResponseEntity.ok(repo.save(nuevo));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Estudiante> eliminar(@PathVariable int id) {
		return repo.findById(id).map(e -> {
			repo.deleteById(id);
			return ResponseEntity.ok(e);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
