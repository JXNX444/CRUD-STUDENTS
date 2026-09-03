package com.example.demo.carrera;

import com.example.demo.common.EstadoRegistro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de Carrera.
 * JpaRepository ya trae save(), findById(), findAll(), etc.
 * Agregamos una consulta que EXCLUYE las eliminadas (baja logica),
 * ordenada por id.
 */
public interface CarreraRepository extends JpaRepository<Carrera, Integer> {

    /** Lista las carreras cuyo state NO sea el que pasemos
     *  (le pasaremos ELIMINADO para traer solo las vigentes),
     *  ordenadas por id (carrera_id). */
    List<Carrera> findByStateNotOrderByIdAsc(EstadoRegistro state);
}