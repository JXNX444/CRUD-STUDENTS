package com.example.demo.asignacion;

import com.example.demo.curso.Curso;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * POST /api/asignaciones
 * body: { "estudianteId": 1, "cursoIds": [3, 8, 21] }
 * Asigna esos cursos al estudiante y le manda el correo.
 */
@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService service;

    public AsignacionController(AsignacionService service) {
        this.service = service;
    }

    @PostMapping
    public List<Curso> asignar(@RequestBody AsignacionRequest req) {
        return service.asignar(req.getEstudianteId(), req.getCursoIds());
    }
}