package com.example.appweb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cursos")
public class RestCurso {

	static HashMap<Integer, Curso> mapCursos = new HashMap<>();

	// Operación 5: Listar todos los cursos
	@GetMapping
	public List<Curso> listar() {
		return new ArrayList<>(mapCursos.values());
	}

	// Operación 6: Consultar un curso por ID
	@GetMapping("/{id}")
	public ResponseEntity<Curso> consultar(@PathVariable int id) {
		Curso c = mapCursos.get(id);
		if (c == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(c);
	}

	// Operación 7: Registrar un nuevo curso
	@PostMapping
	public ResponseEntity<Curso> registrar(@RequestBody Curso nuevo) {
		mapCursos.put(nuevo.getId(), nuevo);
		return ResponseEntity.ok(nuevo);
	}

	// Operación 8: Eliminar un curso por ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Curso> eliminar(@PathVariable int id) {
		Curso c = mapCursos.remove(id);
		if (c == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(c);
	}
}
