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
@RequestMapping("/inscripciones")
public class RestInscripcion {

	static HashMap<Integer, Inscripcion> mapInscripciones = new HashMap<>();

	// Operación 12: Listar todas las inscripciones
	@GetMapping
	public List<Inscripcion> listar() {
		return new ArrayList<>(mapInscripciones.values());
	}

	// Operación 13: Consultar una inscripción por ID
	@GetMapping("/{id}")
	public ResponseEntity<Inscripcion> consultar(@PathVariable int id) {
		Inscripcion i = mapInscripciones.get(id);
		if (i == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(i);
	}

	// Operación 14: Registrar una nueva inscripción
	@PostMapping
	public ResponseEntity<Inscripcion> registrar(@RequestBody Inscripcion nueva) {
		mapInscripciones.put(nueva.getId(), nueva);
		return ResponseEntity.ok(nueva);
	}

	// Operación 15: Eliminar una inscripción por ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Inscripcion> eliminar(@PathVariable int id) {
		Inscripcion i = mapInscripciones.remove(id);
		if (i == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(i);
	}
}
