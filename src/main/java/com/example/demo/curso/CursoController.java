package com.example.demo.curso;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Devuelve los cursos de una carrera (desde el catalogo con cache).
 * GET /api/carreras/{carreraId}/cursos
 */
@RestController
public class CursoController {

    private final CatalogoCursos catalogo;

    public CursoController(CatalogoCursos catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping("/api/carreras/{carreraId}/cursos")
    public List<Curso> cursosDeCarrera(@PathVariable Integer carreraId) {
        return catalogo.porCarrera(carreraId);
    }
}