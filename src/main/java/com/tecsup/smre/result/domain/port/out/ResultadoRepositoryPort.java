package com.tecsup.smre.result.domain.port.out;

import com.tecsup.smre.interview.domain.model.Entrevista;

import java.util.List;
import java.util.Optional;

public interface ResultadoRepositoryPort {
    Optional<Entrevista> findById(Long entrevistaId);
    List<Entrevista> findHistorialByAlumnoId(Long alumnoId);
}
