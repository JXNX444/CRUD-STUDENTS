package com.example.demo.estudiante;

import com.example.demo.common.EstadoRegistro;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Logica del CRUD de Estudiante.
 * El Controller llama a estos metodos; ellos usan el Repository.
 */
@Service
public class EstudianteService {

    private final EstudianteRepository repository;

    // Spring "inyecta" el repositorio por el constructor.
    public EstudianteService(EstudianteRepository repository) {
        this.repository = repository;
    }

    // ---- READ: listar todos los vigentes (sin eliminados) ----
    public List<Estudiante> listar() {
        return repository.findByStateNot(EstadoRegistro.ELIMINADO);
    }

    // ---- READ: buscar uno por id (si no existe o esta eliminado -> 404) ----
    public Estudiante buscarPorId(Integer id) {
        return repository.findByIdAndStateNot(id, EstadoRegistro.ELIMINADO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado: " + id));
    }

    // ---- CREATE ----
    @Transactional
    public Estudiante crear(Estudiante estudiante) {
        estudiante.setId(null);
        estudiante.setState(EstadoRegistro.ACTIVO);
        return repository.save(estudiante);
    }

    // ---- UPDATE ----
    @Transactional
    public Estudiante actualizar(Integer id, Estudiante datos) {
        Estudiante actual = buscarPorId(id);          // valida que exista y no este eliminado
        actual.setCarnet(datos.getCarnet());
        actual.setNombreCompleto(datos.getNombreCompleto());
        actual.setCorreo(datos.getCorreo());
        actual.setTelefono(datos.getTelefono());
        actual.setCarrera(datos.getCarrera());
        actual.setFacultad(datos.getFacultad());
        actual.setFechaIngreso(datos.getFechaIngreso());
        return repository.save(actual);
    }

    // ---- DELETE: no borra, solo marca ELIMINADO ----
    @Transactional
    public void eliminar(Integer id) {
        Estudiante actual = buscarPorId(id);
        actual.setState(EstadoRegistro.ELIMINADO);
        repository.save(actual);
    }

    // ---- REPORTE PAGINADO: filtros obligatorios (facultad + rango de fechas) ----
    public Page<Estudiante> reportar(Facultad facultad, LocalDate inicio, LocalDate fin,
                                     int pagina, int tamano) {
        // 1) los tres filtros son obligatorios
        if (facultad == null || inicio == null || fin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe indicar facultad, fecha inicio y fecha fin.");
        }
        // 2) inicio no puede ser mayor que fin
        if (inicio.isAfter(fin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha inicio no puede ser mayor que la fecha fin.");
        }
        // 3) el rango no puede pasar de 6 meses
        if (fin.isAfter(inicio.plusMonths(6))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El rango de fechas no puede ser mayor a 6 meses.");
        }
        // pagina desde 0, ordenado por fecha de ingreso
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("fechaIngreso").ascending());
        return repository.findByFacultadAndFechaIngresoBetweenAndStateNot(
                facultad, inicio, fin, EstadoRegistro.ELIMINADO, pageable);
    }
}