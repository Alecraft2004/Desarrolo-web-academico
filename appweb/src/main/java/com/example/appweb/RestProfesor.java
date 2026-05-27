package com.example.appweb;

import java.util.List;
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
	public ResponseEntity<Profesor> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping
	public ResponseEntity<Profesor> registrar(@RequestBody Profesor nuevo) {
		return ResponseEntity.ok(repo.save(nuevo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Profesor> actualizar(@PathVariable int id, @RequestBody Profesor datos) {
		return repo.findById(id).map(p -> {
			p.setNombre(datos.getNombre());
			p.setEmail(datos.getEmail());
			p.setDepartamento(datos.getDepartamento());
			p.setEspecialidad(datos.getEspecialidad());
			return ResponseEntity.ok(repo.save(p));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Profesor> eliminar(@PathVariable int id) {
		return repo.findById(id).map(p -> {
			repo.deleteById(id);
			return ResponseEntity.ok(p);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
