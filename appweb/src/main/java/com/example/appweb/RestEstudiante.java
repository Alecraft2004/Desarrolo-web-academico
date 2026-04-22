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
@RequestMapping("/estudiantes")
public class RestEstudiante {

	static HashMap<Integer, Estudiante> mapEstudiantes = new HashMap<>();

	// Operación 1: Listar todos los estudiantes
	@GetMapping
	public List<Estudiante> listar() {
		return new ArrayList<>(mapEstudiantes.values());
	}

	// Operación 2: Consultar un estudiante por ID
	@GetMapping("/{id}")
	public ResponseEntity<Estudiante> consultar(@PathVariable int id) {
		Estudiante e = mapEstudiantes.get(id);
		if (e == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(e);
	}

	// Operación 3: Registrar un nuevo estudiante
	@PostMapping
	public ResponseEntity<Estudiante> registrar(@RequestBody Estudiante nuevo) {
		mapEstudiantes.put(nuevo.getId(), nuevo);
		return ResponseEntity.ok(nuevo);
	}

	// Operación 4: Eliminar un estudiante por ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Estudiante> eliminar(@PathVariable int id) {
		Estudiante e = mapEstudiantes.remove(id);
		if (e == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(e);
	}
}

