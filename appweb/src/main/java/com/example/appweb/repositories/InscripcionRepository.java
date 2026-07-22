package com.example.appweb.repositories;

import com.example.appweb.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    Optional<Inscripcion> findByEstudianteIdAndCursoId(int estudianteId, int cursoId);
    int countByCursoId(int cursoId);
    List<Inscripcion> findByEstudianteId(int estudianteId);
}
