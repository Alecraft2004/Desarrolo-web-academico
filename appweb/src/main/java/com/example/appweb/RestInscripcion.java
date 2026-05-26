package com.example.appweb;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inscripciones")
public class RestInscripcion {

	@Autowired
	private InscripcionRepository repo;

	@GetMapping
	public List<Inscripcion> listar() {
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Inscripcion> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping
	public ResponseEntity<Inscripcion> registrar(@RequestBody Inscripcion nueva) {
		return ResponseEntity.ok(repo.save(nueva));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Inscripcion> eliminar(@PathVariable int id) {
		return repo.findById(id).map(i -> {
			repo.deleteById(id);
			return ResponseEntity.ok(i);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
