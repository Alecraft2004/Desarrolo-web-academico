package com.example.appweb.controllers;

import com.example.appweb.dto.CursoHistorialItem;
import com.example.appweb.dto.HistorialAcademico;
import com.example.appweb.models.Curso;
import com.example.appweb.models.Estudiante;
import com.example.appweb.models.Inscripcion;
import com.example.appweb.repositories.CursoRepository;
import com.example.appweb.repositories.EstudianteRepository;
import com.example.appweb.repositories.InscripcionRepository;
import com.example.appweb.repositories.ProfesorRepository;
import com.example.appweb.util.CalificacionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiantes")
public class RestEstudiante {

	@Autowired
	private EstudianteRepository repo;

	@Autowired
	private InscripcionRepository inscripcionRepo;

	@Autowired
	private CursoRepository cursoRepo;

	@Autowired
	private ProfesorRepository profesorRepo;

	@GetMapping
	public List<Estudiante> listar() {
		return repo.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> consultar(@PathVariable int id) {
		return repo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	// Proceso de negocio: construir la constancia/historial académico de un
	// estudiante, calculando créditos aprobados, promedio ponderado y el
	// estado (en curso / aprobado / reprobado) de cada inscripción.
	@GetMapping("/{id}/historial")
	public ResponseEntity<?> historial(@PathVariable int id) {
		var estudianteOpt = repo.findById(id);
		if (estudianteOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", "El estudiante " + id + " no existe"));
		}
		Estudiante estudiante = estudianteOpt.get();

		List<Inscripcion> inscripciones = inscripcionRepo.findByEstudianteId(id);
		List<CursoHistorialItem> items = new ArrayList<>();

		int creditosAprobados = 0;
		int cursosAprobados = 0;
		int cursosReprobados = 0;
		int cursosEnCurso = 0;
		double sumaPonderada = 0;
		int creditosCalificados = 0;

		for (Inscripcion i : inscripciones) {
			Curso curso = cursoRepo.findById(i.getCursoId()).orElse(null);
			String nombreCurso = curso != null ? curso.getNombre() : "Curso " + i.getCursoId();
			int creditos = curso != null ? curso.getCreditos() : 0;
			String estado = CalificacionUtil.estadoDe(i.getCalificacion());
			String nombreProfesor = (curso != null && curso.getProfesorId() != null)
					? profesorRepo.findById(curso.getProfesorId()).map(p -> p.getNombre()).orElse(null)
					: null;

			switch (estado) {
				case "APROBADO" -> {
					cursosAprobados++;
					creditosAprobados += creditos;
					sumaPonderada += i.getCalificacion() * creditos;
					creditosCalificados += creditos;
				}
				case "REPROBADO" -> {
					cursosReprobados++;
					sumaPonderada += i.getCalificacion() * creditos;
					creditosCalificados += creditos;
				}
				default -> cursosEnCurso++;
			}

			items.add(new CursoHistorialItem(nombreCurso, creditos, i.getFecha(), i.getCalificacion(), estado, nombreProfesor));
		}

		double promedioPonderado = creditosCalificados > 0
				? Math.round((sumaPonderada / creditosCalificados) * 100) / 100.0
				: 0.0;

		return ResponseEntity.ok(new HistorialAcademico(
				estudiante.getNombre(), items, creditosAprobados, promedioPonderado,
				cursosAprobados, cursosReprobados, cursosEnCurso));
	}

	@PostMapping
	public ResponseEntity<?> registrar(@RequestBody Estudiante nuevo) {
		if (nuevo.getEmail() != null && !nuevo.getEmail().isBlank()) {
			boolean duplicado = repo.findByEmail(nuevo.getEmail()).isPresent();
			if (duplicado) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "Ya existe un estudiante con el email: " + nuevo.getEmail()));
			}
		}
		return ResponseEntity.ok(repo.save(nuevo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Estudiante datos) {
		return repo.findById(id).map(e -> {
			// Verificar email duplicado solo si cambió y pertenece a otro estudiante
			if (datos.getEmail() != null && !datos.getEmail().isBlank()) {
				var existente = repo.findByEmail(datos.getEmail());
				if (existente.isPresent() && existente.get().getId() != id) {
					return ResponseEntity.status(HttpStatus.CONFLICT)
							.<Object>body(Map.of("error", "Ya existe otro estudiante con el email: " + datos.getEmail()));
				}
			}
			e.setNombre(datos.getNombre());
			e.setEmail(datos.getEmail());
			e.setCarrera(datos.getCarrera());
			e.setSemestre(datos.getSemestre());
			return ResponseEntity.<Object>ok(repo.save(e));
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		return repo.findById(id).map(e -> {
			repo.deleteById(id);
			return ResponseEntity.ok(e);
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
}
