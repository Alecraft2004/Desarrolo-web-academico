package com.example.appweb.repositories;

import com.example.appweb.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
    Optional<Curso> findByNombre(String nombre);
    long countByProfesorId(Integer profesorId);
}
