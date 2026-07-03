package com.tecsup.smre.interview.application.service;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.interview.application.dto.AlumnoRiesgoDto;
import com.tecsup.smre.interview.application.dto.EntrevistaResponseDto;
import com.tecsup.smre.interview.application.dto.GuardarEntrevistaDto;
import com.tecsup.smre.interview.application.port.in.GetAlumnosTutorUseCase;
import com.tecsup.smre.interview.application.port.in.GetCriteriosUseCase;
import com.tecsup.smre.interview.application.port.in.GuardarEntrevistaUseCase;
import com.tecsup.smre.interview.application.port.in.IniciarEntrevistaUseCase;
import com.tecsup.smre.interview.application.port.out.EntrevistaRepositoryPort;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.interview.domain.model.RiesgoCalculator;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EntrevistaService implements GetAlumnosTutorUseCase, IniciarEntrevistaUseCase, GuardarEntrevistaUseCase, GetCriteriosUseCase {

    private final EntrevistaRepositoryPort entrevistaRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final AssignmentRepositoryPort assignmentRepositoryPort;

    public EntrevistaService(EntrevistaRepositoryPort entrevistaRepositoryPort,
                             StudentRepositoryPort studentRepositoryPort,
                             UsuarioRepositoryPort usuarioRepositoryPort,
                             AssignmentRepositoryPort assignmentRepositoryPort) {
        this.entrevistaRepositoryPort = entrevistaRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.assignmentRepositoryPort = assignmentRepositoryPort;
    }

    private String getTutorEmailAutenticado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Usuario getTutorAutenticado() {
        String email = getTutorEmailAutenticado();
        return usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado con email: " + email));
    }

    @Override
    public List<AlumnoRiesgoDto> getAlumnosTutor() {
        Usuario tutor = getTutorAutenticado();
        List<Assignment> assignments = assignmentRepositoryPort.findAll().stream()
                .filter(asg -> asg.getTutorId() != null && asg.getTutorId().equals(tutor.getId()))
                .collect(Collectors.toList());

        List<Alumno> alumnos = studentRepositoryPort.findAll().stream()
                .filter(alumno -> assignments.stream().anyMatch(asg ->
                        (alumno.getSemestre() != null && alumno.getSemestre().equalsIgnoreCase(asg.getPeriodo())) &&
                        (alumno.getCarrera() != null && alumno.getCarrera().equalsIgnoreCase(asg.getEspecialidad())) &&
                        (alumno.getGrupo() != null && alumno.getGrupo().equalsIgnoreCase(asg.getGrupo()))
                ))
                .collect(Collectors.toList());

        return alumnos.stream().map(alumno -> {
            List<Entrevista> entrevistas = entrevistaRepositoryPort.findByAlumnoId(alumno.getId().toString());
            NivelRiesgo riesgo = entrevistas.isEmpty() ? null : entrevistas.get(entrevistas.size() - 1).getNivelRiesgo();
            return new AlumnoRiesgoDto(alumno.getId(), alumno.getNombre() + " " + alumno.getApellido(), riesgo);
        }).collect(Collectors.toList());
    }

    @Override
    public Alumno iniciarEntrevista(Long alumnoId) {
        return studentRepositoryPort.findAll().stream()
                .filter(a -> a.getId().equals(alumnoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con el ID: " + alumnoId));
    }

    @Override
    public EntrevistaResponseDto guardarEntrevista(GuardarEntrevistaDto dto) {
        Alumno alumno = studentRepositoryPort.findAll().stream()
                .filter(a -> a.getId().equals(dto.getAlumnoId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con el ID: " + dto.getAlumnoId()));

        int puntajeTotal = RiesgoCalculator.calcularPuntajeTotal(dto.getRespuestas());
        NivelRiesgo nivelRiesgo = RiesgoCalculator.determinarNivelRiesgo(puntajeTotal);
        Usuario tutor = getTutorAutenticado();

        Entrevista entrevista = new Entrevista();
        entrevista.setAlumnoId(alumno.getId().toString());
        entrevista.setAlumnoNombre(alumno.getNombre());
        entrevista.setAlumnoApellido(alumno.getApellido());
        entrevista.setTutorId(tutor.getId().toString());
        entrevista.setTutorNombre(tutor.getNombre());
        entrevista.setPuntajeTotal(puntajeTotal);
        entrevista.setNivelRiesgo(nivelRiesgo);
        entrevista.setRecomendacion(nivelRiesgo.getRecomendacion());
        entrevista.setObservaciones(dto.getObservaciones());
        entrevista.setFecha(LocalDateTime.now());

        Entrevista guardada = entrevistaRepositoryPort.save(entrevista);

        return EntrevistaResponseDto.builder()
                .id(guardada.getId())
                .alumnoId(guardada.getAlumnoId())
                .alumnoNombre(guardada.getAlumnoNombre())
                .alumnoApellido(guardada.getAlumnoApellido())
                .tutorNombre(guardada.getTutorNombre())
                .puntaje(guardada.getPuntajeTotal())
                .nivelRiesgo(guardada.getNivelRiesgo())
                .recomendacion(guardada.getRecomendacion())
                .fecha(guardada.getFecha())
                .build();
    }

    @Override
    public Map<String, String> getCriterios() {
        return Map.of(
                "Pregunta 1", "¿Cómo calificaría su nivel de asistencia y puntualidad a las clases programadas?",
                "Pregunta 2", "¿Con qué frecuencia participa activamente y entrega a tiempo sus tareas/proyectos?",
                "Pregunta 3", "¿Cómo calificaría su rendimiento académico promedio en las evaluaciones?",
                "Pregunta 4", "¿Dispone del equipamiento tecnológico y conectividad necesarios para sus estudios?",
                "Pregunta 5", "¿Cómo calificaría su nivel de motivación y bienestar emocional respecto a la carrera?",
                "Pregunta 6", "¿Considera que su entorno familiar o personal actual apoya adecuadamente su proceso de estudio?"
        );
    }
}