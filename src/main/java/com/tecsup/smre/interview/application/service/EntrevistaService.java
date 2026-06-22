package com.tecsup.smre.interview.application.service;

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

    public EntrevistaService(EntrevistaRepositoryPort entrevistaRepositoryPort, StudentRepositoryPort studentRepositoryPort, UsuarioRepositoryPort usuarioRepositoryPort) {
        this.entrevistaRepositoryPort = entrevistaRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
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
        // Filtrar según tutor o según tu repositorio si es necesario. Por ahora listamos todos y calculamos su nivel de riesgo.
        List<Alumno> alumnos = studentRepositoryPort.findAll(); 
        
        return alumnos.stream().map(alumno -> {
            List<Entrevista> entrevistas = entrevistaRepositoryPort.findByAlumnoId(alumno.getId());
            NivelRiesgo riesgo = entrevistas.isEmpty() ? null : entrevistas.get(entrevistas.size() - 1).getNivelRiesgo();
            return new AlumnoRiesgoDto(alumno.getId(), alumno.getNombre(), riesgo);
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

        Entrevista entrevista = new Entrevista();
        entrevista.setAlumno(alumno);
        entrevista.setTutor(getTutorAutenticado());
        entrevista.setPuntajeTotal(puntajeTotal);
        entrevista.setNivelRiesgo(nivelRiesgo);
        entrevista.setObservaciones(dto.getObservaciones());
        entrevista.setFecha(LocalDateTime.now());

        Entrevista guardada = entrevistaRepositoryPort.save(entrevista);

        EntrevistaResponseDto response = new EntrevistaResponseDto();
        response.setId(guardada.getId());
        response.setAlumno(guardada.getAlumno());
        response.setPuntaje(guardada.getPuntajeTotal());
        response.setNivelRiesgo(guardada.getNivelRiesgo());
        response.setRecomendacion(guardada.getNivelRiesgo().getRecomendacion());
        response.setFecha(guardada.getFecha());

        return response;
    }

    @Override
    public Map<String, String> getCriterios() {
        return Map.of(
            "Criterio 1", "Asistencia a clases",
            "Criterio 2", "Participación",
            "Criterio 3", "Entrega de trabajos",
            "Criterio 4", "Calificaciones",
            "Criterio 5", "Comportamiento",
            "Criterio 6", "Salud emocional"
        );
    }
}