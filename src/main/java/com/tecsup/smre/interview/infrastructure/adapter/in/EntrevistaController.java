package com.tecsup.smre.interview.infrastructure.adapter.in;

import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.interview.application.dto.AlumnoRiesgoDto;
import com.tecsup.smre.interview.application.dto.EntrevistaResponseDto;
import com.tecsup.smre.interview.application.dto.GuardarEntrevistaDto;
import com.tecsup.smre.interview.application.port.in.GetAlumnosTutorUseCase;
import com.tecsup.smre.interview.application.port.in.GetCriteriosUseCase;
import com.tecsup.smre.interview.application.port.in.GuardarEntrevistaUseCase;
import com.tecsup.smre.interview.application.port.in.IniciarEntrevistaUseCase;
import com.tecsup.smre.student.domain.model.Alumno;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('TUTOR', 'ADMIN')")
public class EntrevistaController {

    private final GetAlumnosTutorUseCase getAlumnosTutorUseCase;
    private final IniciarEntrevistaUseCase iniciarEntrevistaUseCase;
    private final GuardarEntrevistaUseCase guardarEntrevistaUseCase;
    private final GetCriteriosUseCase getCriteriosUseCase;

    public EntrevistaController(GetAlumnosTutorUseCase getAlumnosTutorUseCase,
                                IniciarEntrevistaUseCase iniciarEntrevistaUseCase,
                                GuardarEntrevistaUseCase guardarEntrevistaUseCase,
                                GetCriteriosUseCase getCriteriosUseCase) {
        this.getAlumnosTutorUseCase = getAlumnosTutorUseCase;
        this.iniciarEntrevistaUseCase = iniciarEntrevistaUseCase;
        this.guardarEntrevistaUseCase = guardarEntrevistaUseCase;
        this.getCriteriosUseCase = getCriteriosUseCase;
    }

    @GetMapping("/tutor/alumnos")
    public ResponseEntity<ApiResponse<List<AlumnoRiesgoDto>>> getAlumnosTutor() {
        List<AlumnoRiesgoDto> alumnos = getAlumnosTutorUseCase.getAlumnosTutor();
        return ResponseEntity.ok(ApiResponse.success(alumnos, "Alumnos obtenidos exitosamente"));
    }

    @GetMapping("/entrevistas/nueva/{alumnoId}")
    public ResponseEntity<ApiResponse<Alumno>> iniciarEntrevista(@PathVariable Long alumnoId) {
        Alumno alumno = iniciarEntrevistaUseCase.iniciarEntrevista(alumnoId);
        return ResponseEntity.ok(ApiResponse.success(alumno, "Alumno encontrado"));
    }

    @PostMapping("/entrevistas")
    public ResponseEntity<ApiResponse<EntrevistaResponseDto>> guardarEntrevista(@RequestBody GuardarEntrevistaDto dto) {
        EntrevistaResponseDto response = guardarEntrevistaUseCase.guardarEntrevista(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Entrevista guardada exitosamente"));
    }

    @GetMapping("/entrevistas/criterios")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCriterios() {
        Map<String, String> criterios = getCriteriosUseCase.getCriterios();
        return ResponseEntity.ok(ApiResponse.success(criterios, "Criterios obtenidos"));
    }
}
