package com.example.demo.curso;

import com.example.demo.common.EstadoRegistro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Integer> {

    /** Cursos vigentes de una carrera, ordenados por codigo. */
    List<Curso> findByCarreraIdAndStateNotOrderByCodigoAsc(Integer carreraId, EstadoRegistro state);
}