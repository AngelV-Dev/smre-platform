package com.tecsup.smre.interview.application.port.in;

import com.tecsup.smre.student.domain.model.Alumno;

public interface IniciarEntrevistaUseCase {
    Alumno iniciarEntrevista(Long alumnoId);
}
