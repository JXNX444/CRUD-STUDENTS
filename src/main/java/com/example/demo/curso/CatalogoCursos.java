package com.example.demo.curso;

import com.example.demo.common.EstadoRegistro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Catalogo de cursos con cache. Guarda los cursos de cada carrera
 * (una entrada de cache por carreraId). Se lee mucho, cambia poco.
 */
@Service
public class CatalogoCursos {

    private static final Logger log = LoggerFactory.getLogger(CatalogoCursos.class);

    private final CursoRepository repository;

    public CatalogoCursos(CursoRepository repository) {
        this.repository = repository;
    }

    // La clave del cache es el carreraId: cada carrera guarda su propia lista.
    @Cacheable(value = "cursos", key = "#carreraId")
    public List<Curso> porCarrera(Integer carreraId) {
        log.info(">>> Consultando cursos de la carrera {} en la BASE DE DATOS...", carreraId);
        return repository.findByCarreraIdAndStateNotOrderByCodigoAsc(carreraId, EstadoRegistro.ELIMINADO);
    }

    // Vacia TODO el cache de cursos (util si cargas cursos nuevos).
    @CacheEvict(value = "cursos", allEntries = true)
    public void recargar() {
        log.info(">>> Cache de cursos vaciado.");
    }
}