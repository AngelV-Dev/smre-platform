package com.tecsup.smre.interview.application.port.in;

import com.tecsup.smre.interview.application.dto.AlumnoRiesgoDto;
import java.util.List;

public interface GetAlumnosTutorUseCase {
    List<AlumnoRiesgoDto> getAlumnosTutor();
}
