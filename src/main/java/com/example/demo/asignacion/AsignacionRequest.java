package com.example.demo.asignacion;

import java.util.List;

/** Datos del front: que estudiante y que cursos (de cualquier carrera) asignarle. */
public class AsignacionRequest {
    private Integer estudianteId;
    private List<Integer> cursoIds;

    public Integer getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Integer estudianteId) { this.estudianteId = estudianteId; }
    public List<Integer> getCursoIds() { return cursoIds; }
    public void setCursoIds(List<Integer> cursoIds) { this.cursoIds = cursoIds; }
}