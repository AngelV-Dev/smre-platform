package com.tecsup.smre.interview.application.port.out;

import com.tecsup.smre.interview.domain.model.Entrevista;
import java.util.List;
import java.util.Optional;

public interface EntrevistaRepositoryPort {
    Entrevista save(Entrevista entrevista);
    Optional<Entrevista> findById(Long id);
    List<Entrevista> findByAlumnoId(Long alumnoId);
}
