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
@RequestMapping("/profesores")
public class RestProfesor {

	static HashMap<Integer, Profesor> mapProfesores = new HashMap<>();

	// Operación 9: Listar todos los profesores
	@GetMapping
	public List<Profesor> listar() {
		return new ArrayList<>(mapProfesores.values());
	}

	// Operación 10: Consultar un profesor por ID
	@GetMapping("/{id}")
	public ResponseEntity<Profesor> consultar(@PathVariable int id) {
		Profesor p = mapProfesores.get(id);
		if (p == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(p);
	}

	// Operación 11: Registrar un nuevo profesor
	@PostMapping
	public ResponseEntity<Profesor> registrar(@RequestBody Profesor nuevo) {
		mapProfesores.put(nuevo.getId(), nuevo);
		return ResponseEntity.ok(nuevo);
	}

	// Operación 12: Eliminar un profesor por ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Profesor> eliminar(@PathVariable int id) {
		Profesor p = mapProfesores.remove(id);
		if (p == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(p);
	}
}
