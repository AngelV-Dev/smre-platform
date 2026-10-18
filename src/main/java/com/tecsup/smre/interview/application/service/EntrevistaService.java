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
import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
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
    private final TutorRepositoryPort tutorRepositoryPort;

    public EntrevistaService(EntrevistaRepositoryPort entrevistaRepositoryPort,
                             StudentRepositoryPort studentRepositoryPort,
                             UsuarioRepositoryPort usuarioRepositoryPort,
                             AssignmentRepositoryPort assignmentRepositoryPort,
                             TutorRepositoryPort tutorRepositoryPort) {
        this.entrevistaRepositoryPort = entrevistaRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.assignmentRepositoryPort = assignmentRepositoryPort;
        this.tutorRepositoryPort = tutorRepositoryPort;
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
        Usuario usuario = getTutorAutenticado();
        String email = usuario.getEmail();

        Long tutorEntityId = null;
        if (tutorRepositoryPort != null) {
            tutorEntityId = tutorRepositoryPort.findAll().stream()
                    .filter(t -> t.getEmail() != null && t.getEmail().equalsIgnoreCase(email))
                    .map(Tutor::getId)
                    .findFirst()
                    .orElse(null);
        }
        final Long entityId = tutorEntityId;
        final Long usuarioId = usuario.getId();

        List<Assignment> assignments = assignmentRepositoryPort.findAll().stream()
                .filter(asg -> asg.getTutorId() != null &&
                        (asg.getTutorId().equals(usuarioId) || (entityId != null && asg.getTutorId().equals(entityId))))
                .collect(Collectors.toList());

        List<Alumno> alumnos = studentRepositoryPort.findAll().stream()
                .filter(alumno ->
                        (alumno.getTutorId() != null &&
                                (alumno.getTutorId().equals(usuarioId) || (entityId != null && alumno.getTutorId().equals(entityId))))
                        || assignments.stream().anyMatch(asg ->
                                (alumno.getSemestre() != null && alumno.getSemestre().equalsIgnoreCase(asg.getPeriodo())) &&
                                (alumno.getCarrera() != null && alumno.getCarrera().equalsIgnoreCase(asg.getEspecialidad())) &&
                                (alumno.getGrupo() != null && alumno.getGrupo().equalsIgnoreCase(asg.getGrupo()))
                        )
                )
                .collect(Collectors.toList());

        return alumnos.stream().map(alumno -> {
            List<Entrevista> entrevistas = entrevistaRepositoryPort.findByAlumnoId(alumno.getId().toString());
            NivelRiesgo riesgo = entrevistas.isEmpty() ? null : entrevistas.get(entrevistas.size() - 1).getNivelRiesgo();
            return AlumnoRiesgoDto.builder()
                    .id(alumno.getId())
                    .codigo(alumno.getCodigo())
                    .nombre(alumno.getNombre())
                    .apellido(alumno.getApellido())
                    .email(alumno.getEmail())
                    .carrera(alumno.getCarrera())
                    .semestre(alumno.getSemestre())
                    .grupo(alumno.getGrupo())
                    .nivelRiesgo(riesgo)
                    .edad(alumno.getEdad())
                    .build();
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

        if (dto.getEdad() != null) {
            alumno.setEdad(dto.getEdad());
            studentRepositoryPort.save(alumno);
        }

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
        entrevista.setEdad(dto.getEdad());
        entrevista.setRespuestas(dto.getRespuestas().stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")));

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
                .edad(guardada.getEdad())
                .build();
    }

    @Override
    public Map<String, String> getCriterios() {
        return Map.of(
                "Pregunta 1", "Rendimiento académico: ¿Qué tan satisfecho(a) estás con tus calificaciones y asistencia a clases en este semestre?",
                "Pregunta 2", "Bienestar emocional: ¿Sientes que cuentas con apoyo emocional cuando enfrentas problemas?",
                "Pregunta 3", "Trabajo en equipo: ¿Cómo describirías tu experiencia al trabajar en equipo?",
                "Pregunta 4", "Comunicación efectiva: ¿Qué tan cómodo(a) te sientes al expresar tus ideas en público?",
                "Pregunta 5", "Trabajo / Economía: ¿Actualmente trabajas o enfrentas dificultades económicas?",
                "Pregunta 6", "Estrés - estado emocional: ¿Con qué frecuencia sientes que el estrés o la ansiedad afectan tu desempeño?"
        );
    }
}