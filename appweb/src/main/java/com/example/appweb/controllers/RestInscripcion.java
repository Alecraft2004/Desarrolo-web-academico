package com.example.appweb.controllers;

import com.example.appweb.dto.CalificarRequest;
import com.example.appweb.models.Curso;
import com.example.appweb.models.Estudiante;
import com.example.appweb.models.Inscripcion;
import com.example.appweb.repositories.CursoRepository;
import com.example.appweb.repositories.EstudianteRepository;
import com.example.appweb.repositories.InscripcionRepository;
import com.example.appweb.util.CalificacionUtil;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inscripciones")
public class RestInscripcion {

	@Autowired
	private InscripcionRepository repo;

	@Autowired
	private CursoRepository cursoRepo;

	@Autowired
	private EstudianteRepository estudianteRepo;

	@GetMapping
	public List<Inscripcion> listar() {
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping
	public ResponseEntity<?> registrar(@RequestBody Inscripcion nueva) {
		var duplicado = repo.findByEstudianteIdAndCursoId(nueva.getEstudianteId(), nueva.getCursoId());
		if (duplicado.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("error",
							"El estudiante " + nombreEstudiante(nueva.getEstudianteId()) +
							" ya está inscrito en el curso " + nombreCurso(nueva.getCursoId())));
		}
		return ResponseEntity.ok(repo.save(nueva));
	}

	private String nombreCurso(int cursoId) {
		return cursoRepo.findById(cursoId).map(Curso::getNombre).orElse(String.valueOf(cursoId));
	}

	private String nombreEstudiante(int estudianteId) {
		return estudianteRepo.findById(estudianteId).map(Estudiante::getNombre).orElse(String.valueOf(estudianteId));
	}

	// Proceso de negocio: matricular a un estudiante en un curso validando
	// que el curso exista, que no esté ya inscrito, y que haya cupo disponible.
	@PostMapping("/matricular")
	public ResponseEntity<?> matricular(@RequestBody Inscripcion nueva) {
		Curso curso = cursoRepo.findById(nueva.getCursoId()).orElse(null);
		if (curso == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", "El curso " + nueva.getCursoId() + " no existe"));
		}

		var duplicado = repo.findByEstudianteIdAndCursoId(nueva.getEstudianteId(), nueva.getCursoId());
		if (duplicado.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("error",
							"El estudiante " + nombreEstudiante(nueva.getEstudianteId()) +
							" ya está inscrito en el curso " + curso.getNombre()));
		}

		int matriculados = repo.countByCursoId(nueva.getCursoId());
		if (matriculados >= curso.getCupo()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Map.of("error", "El curso " + curso.getNombre() + " no tiene cupo disponible"));
		}

		return ResponseEntity.ok(repo.save(nueva));
	}

	// Proceso de negocio: registrar la calificación final de una inscripción
	// y determinar automáticamente si el estudiante aprobó, reprobó o sigue
	// en curso (misma regla que usa el historial académico).
	@PutMapping("/{id}/calificar")
	public ResponseEntity<?> calificar(@PathVariable int id, @RequestBody CalificarRequest body) {
		if (body.getCalificacion() < 0 || body.getCalificacion() > 20) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "La calificación debe estar entre 0 y 20"));
		}
		return repo.findById(id).map(i -> {
			i.setCalificacion(body.getCalificacion());
			Inscripcion guardada = repo.save(i);
			return ResponseEntity.<Object>ok(Map.of(
					"inscripcion", guardada,
					"estado", CalificacionUtil.estadoDe(guardada.getCalificacion())));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Inscripcion datos) {
		return repo.findById(id).map(i -> {
			// Verificar duplicado solo si cambia la combinación estudiante+curso
			var existente = repo.findByEstudianteIdAndCursoId(datos.getEstudianteId(), datos.getCursoId());
			if (existente.isPresent() && existente.get().getId() != id) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.<Object>body(Map.of("error",
								"El estudiante " + nombreEstudiante(datos.getEstudianteId()) +
								" ya está inscrito en el curso " + nombreCurso(datos.getCursoId())));
			}
			i.setEstudianteId(datos.getEstudianteId());
			i.setCursoId(datos.getCursoId());
			i.setFecha(datos.getFecha());
			i.setCalificacion(datos.getCalificacion());
			return ResponseEntity.<Object>ok(repo.save(i));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		return repo.findById(id).map(i -> {
			repo.deleteById(id);
			return ResponseEntity.ok(i);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	// Proceso de negocio: retirar (dar de baja) a un estudiante de un curso.
	// El cupo se libera automáticamente porque se calcula contando las
	// inscripciones vigentes (countByCursoId), no hay contador que actualizar.
	// No se permite la baja si la inscripción ya tiene calificación registrada.
	@DeleteMapping("/{id}/retirar")
	public ResponseEntity<?> retirar(@PathVariable int id) {
		return repo.findById(id).map(i -> {
			if (!"EN_CURSO".equals(CalificacionUtil.estadoDe(i.getCalificacion()))) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.<Object>body(Map.of("error", "No se puede dar de baja: el curso ya tiene una calificación registrada"));
			}
			repo.deleteById(id);
			return ResponseEntity.<Object>ok(Map.of("mensaje", "Baja registrada correctamente"));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
