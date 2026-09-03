package com.example.demo.carrera;

import com.example.demo.common.EstadoRegistro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Catalogo de carreras. Lee de la BD (academico.carrera) pero
 * CACHEA el resultado: la 1a vez consulta la base, las siguientes
 * devuelve la lista guardada en el cache "carreras" SIN tocar la BD.
 */
@Service
public class CatalogoCarreras {

    private static final Logger log = LoggerFactory.getLogger(CatalogoCarreras.class);

    private final CarreraRepository repository;

    // Spring inyecta el repositorio por el constructor.
    public CatalogoCarreras(CarreraRepository repository) {
        this.repository = repository;
    }

    /**
     * Devuelve las carreras vigentes, ordenadas por id.
     * @Cacheable: la 1a vez ejecuta el metodo (consulta la BD) y guarda
     * el resultado en el cache "carreras". Las siguientes veces NO entra
     * al metodo: devuelve directo lo que hay en cache.
     */
    @Cacheable(value = "carreras", key = "'todas'")
    public List<Carrera> listar() {
        log.info(">>> Consultando carreras en la BASE DE DATOS (esto NO deberia repetirse)...");
        return repository.findByStateNotOrderByIdAsc(EstadoRegistro.ELIMINADO);
    }

    /**
     * Vacia el cache "carreras". La proxima llamada a listar()
     * volvera a consultar la BD.
     */
    @CacheEvict(value = "carreras", allEntries = true)
    public void recargar() {
        log.info(">>> Cache de carreras vaciado. Se reconsultara la BD en la proxima peticion.");
    }
}