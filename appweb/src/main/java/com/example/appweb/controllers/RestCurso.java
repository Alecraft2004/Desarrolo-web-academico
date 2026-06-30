package com.example.appweb.controllers;

import com.example.appweb.models.Curso;
import com.example.appweb.repositories.CursoRepository;

import java.util.List;
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
	public ResponseEntity<Curso> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping
	public ResponseEntity<Curso> registrar(@RequestBody Curso nuevo) {
		return ResponseEntity.ok(repo.save(nuevo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Curso> actualizar(@PathVariable int id, @RequestBody Curso datos) {
		return repo.findById(id).map(c -> {
			c.setNombre(datos.getNombre());
			c.setDescripcion(datos.getDescripcion());
			c.setCreditos(datos.getCreditos());
			c.setCupo(datos.getCupo());
			return ResponseEntity.ok(repo.save(c));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Curso> eliminar(@PathVariable int id) {
		return repo.findById(id).map(c -> {
			repo.deleteById(id);
			return ResponseEntity.ok(c);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
