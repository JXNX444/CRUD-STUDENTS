package com.example.demo.carrera;

import com.example.demo.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Carrera. Mapea la tabla academico.carrera.
 * Hereda de BaseEntity: state (baja logica), row_version y auditoria.
 */
@Entity
@Table(name = "carrera")
public class Carrera extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrera_id")
    private Integer id;

    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "facultad", length = 100)
    private String facultad;

    @Column(name = "anios_duracion", nullable = false)
    private Integer aniosDuracion;

    // ---- Getters / Setters ----

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public Integer getAniosDuracion() {
        return aniosDuracion;
    }

    public void setAniosDuracion(Integer aniosDuracion) {
        this.aniosDuracion = aniosDuracion;
    }
}