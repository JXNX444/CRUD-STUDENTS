package com.example.demo.inscripcion;

import com.example.demo.common.EstadoRegistro;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    /** true si el estudiante YA esta inscrito y vigente en ese curso. */
    boolean existsByEstudianteIdAndCursoIdAndStateNot(Integer estudianteId, Integer cursoId, EstadoRegistro state);
}