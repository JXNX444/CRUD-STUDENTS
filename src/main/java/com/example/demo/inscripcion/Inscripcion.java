package com.example.demo.inscripcion;

import com.example.demo.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Une un estudiante con un curso (la asignacion).
 * Mapea la tabla academico.inscripcion.
 */
@Entity
@Table(name = "inscripcion")
public class Inscripcion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inscripcion_id")
    private Integer id;

    @Column(name = "estudiante_id", nullable = false)
    private Integer estudianteId;

    @Column(name = "curso_id", nullable = false)
    private Integer cursoId;

    @Column(name = "fecha_inscripcion", nullable = false)
    private OffsetDateTime fechaInscripcion;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Integer estudianteId) { this.estudianteId = estudianteId; }
    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }
    public OffsetDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(OffsetDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
}