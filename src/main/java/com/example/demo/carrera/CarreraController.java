package com.example.demo.carrera;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST del catalogo de carreras.
 * Ruta base: /api/carreras
 */
@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CatalogoCarreras catalogo;

    public CarreraController(CatalogoCarreras catalogo) {
        this.catalogo = catalogo;
    }

    // GET /api/carreras -> lista las carreras (1a vez de la BD, luego del cache)
    @GetMapping
    public List<Carrera> listar() {
        return catalogo.listar();
    }

    // POST /api/carreras/recargar -> vacia el cache
    @PostMapping("/recargar")
    public String recargar() {
        catalogo.recargar();
        return "Cache de carreras vaciado. La proxima consulta reconsulta la BD.";
    }
}