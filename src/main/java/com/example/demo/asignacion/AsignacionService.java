package com.example.demo.asignacion;

import com.example.demo.carrera.Carrera;
import com.example.demo.carrera.CarreraRepository;
import com.example.demo.common.EstadoRegistro;
import com.example.demo.correo.CorreoService;
import com.example.demo.curso.Curso;
import com.example.demo.curso.CursoRepository;
import com.example.demo.estudiante.Estudiante;
import com.example.demo.estudiante.EstudianteService;
import com.example.demo.inscripcion.Inscripcion;
import com.example.demo.inscripcion.InscripcionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Asigna al estudiante los cursos elegidos (hasta 3 carreras) y le envia
 * el correo con el listado AGRUPADO por carrera.
 */
@Service
public class AsignacionService {

    private static final int MAX_CARRERAS = 3;

    private final EstudianteService estudianteService;
    private final CarreraRepository carreraRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CorreoService correoService;

    public AsignacionService(EstudianteService estudianteService,
                             CarreraRepository carreraRepository,
                             CursoRepository cursoRepository,
                             InscripcionRepository inscripcionRepository,
                             CorreoService correoService) {
        this.estudianteService = estudianteService;
        this.carreraRepository = carreraRepository;
        this.cursoRepository = cursoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.correoService = correoService;
    }

    @Transactional
    public List<Curso> asignar(Integer estudianteId, List<Integer> cursoIds) {
        // 1) Validar estudiante
        Estudiante estudiante = estudianteService.buscarPorId(estudianteId);

        // 2) Debe haber cursos
        if (cursoIds == null || cursoIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No seleccionaste ningun curso.");
        }

        // 3) Traer cursos vigentes, respetando el orden en que se eligieron
        Map<Integer, Curso> porId = new LinkedHashMap<>();
        cursoRepository.findAllById(cursoIds).stream()
                .filter(c -> c.getState() != EstadoRegistro.ELIMINADO)
                .forEach(c -> porId.put(c.getId(), c));
        List<Curso> cursos = new ArrayList<>();
        for (Integer id : cursoIds) {
            Curso c = porId.get(id);
            if (c != null && !cursos.contains(c)) cursos.add(c);
        }
        if (cursos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Los cursos seleccionados no existen.");
        }

        // 4) Agrupar por carrera y validar el limite de 3 carreras
        Map<Integer, List<Curso>> porCarrera = new LinkedHashMap<>();
        for (Curso c : cursos) {
            porCarrera.computeIfAbsent(c.getCarreraId(), k -> new ArrayList<>()).add(c);
        }
        if (porCarrera.size() > MAX_CARRERAS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo podes asignar cursos de hasta " + MAX_CARRERAS + " carreras.");
        }

        // 5) Inscribir cada curso (si aun no lo estaba)
        for (Curso curso : cursos) {
            boolean yaInscrito = inscripcionRepository
                    .existsByEstudianteIdAndCursoIdAndStateNot(estudianteId, curso.getId(), EstadoRegistro.ELIMINADO);
            if (!yaInscrito) {
                Inscripcion ins = new Inscripcion();
                ins.setEstudianteId(estudianteId);
                ins.setCursoId(curso.getId());
                ins.setFechaInscripcion(OffsetDateTime.now());
                ins.setState(EstadoRegistro.ACTIVO);
                inscripcionRepository.save(ins);
            }
        }

        // 6) Armar el mapa "Nombre de carrera -> cursos" para el correo
        Map<String, List<Curso>> grupos = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<Curso>> e : porCarrera.entrySet()) {
            String nombreCarrera = carreraRepository.findById(e.getKey())
                    .map(Carrera::getNombre)
                    .orElse("Carrera");
            grupos.put(nombreCarrera, e.getValue());
        }

        // 7) Enviar el correo agrupado por carrera
        correoService.enviarAsignacion(estudiante, grupos);

        return cursos;
    }
}