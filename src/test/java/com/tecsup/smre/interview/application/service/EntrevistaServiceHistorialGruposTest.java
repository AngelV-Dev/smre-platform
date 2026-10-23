package com.tecsup.smre.interview.application.service;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.interview.application.dto.HistorialGrupoDto;
import com.tecsup.smre.interview.application.port.out.EntrevistaRepositoryPort;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntrevistaServiceHistorialGruposTest {

    private EntrevistaRepositoryPort entrevistaRepo;
    private StudentRepositoryPort studentRepo;
    private UsuarioRepositoryPort usuarioRepo;
    private AssignmentRepositoryPort assignmentRepo;
    private TutorRepositoryPort tutorRepo;
    private EntrevistaService service;

    @BeforeEach
    void setUp() {
        entrevistaRepo = mock(EntrevistaRepositoryPort.class);
        studentRepo = mock(StudentRepositoryPort.class);
        usuarioRepo = mock(UsuarioRepositoryPort.class);
        assignmentRepo = mock(AssignmentRepositoryPort.class);
        tutorRepo = mock(TutorRepositoryPort.class);

        service = new EntrevistaService(entrevistaRepo, studentRepo, usuarioRepo, assignmentRepo, tutorRepo);

        // Mock autenticación usando Authentication mockeada para no invocar clases con nombres sensibles
        Usuario tutor = Usuario.builder()
                .id(3L)
                .email("tutor@tecsup.edu.pe")
                .nombre("Tutor Prueba")
                .rol(Role.TUTOR)
                .build();

        when(usuarioRepo.findByEmail("tutor@tecsup.edu.pe")).thenReturn(Optional.of(tutor));
        when(tutorRepo.findAll()).thenReturn(Collections.emptyList());

        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("tutor@tecsup.edu.pe");

        SecurityContext secContext = mock(SecurityContext.class);
        when(secContext.getAuthentication()).thenReturn(authMock);
        SecurityContextHolder.setContext(secContext);
    }

    @Test
    void historialDevuelveGruposDelTutorConConteosCorrectos() {
        Assignment asg1 = Assignment.builder()
                .id(1L)
                .tutorId(3L)
                .periodo("2026-1")
                .especialidad("Diseño y Desarrollo de Software")
                .ciclo("IV")
                .grupo("A")
                .build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asg1));

        Alumno al1 = Alumno.builder().id(101L).semestre("2026-1").carrera("Diseño y Desarrollo de Software").grupo("A").build();
        Alumno al2 = Alumno.builder().id(102L).semestre("2026-1").carrera("Diseño y Desarrollo de Software").grupo("A").build();

        when(studentRepo.findAll()).thenReturn(List.of(al1, al2));

        // al1 tiene una entrevista, al2 no
        when(entrevistaRepo.findByAlumnoId("101")).thenReturn(List.of(new Entrevista()));
        when(entrevistaRepo.findByAlumnoId("102")).thenReturn(Collections.emptyList());

        List<HistorialGrupoDto> historial = service.getHistorialGruposTutor(null, null);

        assertEquals(1, historial.size());
        HistorialGrupoDto dto = historial.get(0);
        assertEquals("2026-1", dto.getPeriodo());
        assertEquals("IV", dto.getCiclo());
        assertEquals(2, dto.getTotalAlumnos());
        assertEquals(1, dto.getEntrevistasCompletadas());
    }

    @Test
    void asignacionDePeriodoPasadoSinAlumnosActivosDevuelveCeroYNota() {
        Assignment asgPasada = Assignment.builder()
                .id(2L)
                .tutorId(3L)
                .periodo("2025-2")
                .especialidad("Diseño y Desarrollo de Software")
                .ciclo("III")
                .grupo("B")
                .build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asgPasada));
        when(studentRepo.findAll()).thenReturn(Collections.emptyList());

        List<HistorialGrupoDto> historial = service.getHistorialGruposTutor(null, null);

        assertEquals(1, historial.size());
        HistorialGrupoDto dto = historial.get(0);
        assertEquals("2025-2", dto.getPeriodo());
        assertEquals(0, dto.getTotalAlumnos());
        assertEquals(0, dto.getEntrevistasCompletadas());
        assertTrue(dto.getNota().contains("Sin alumnos activos"));
    }

    @Test
    void tutorSoloVeSusPropiasAsignacionesYNoLasDeTutorAjeno() {
        Assignment asgPropia = Assignment.builder()
                .id(1L)
                .tutorId(3L)
                .periodo("2026-1")
                .ciclo("IV")
                .build();

        Assignment asgAjena = Assignment.builder()
                .id(2L)
                .tutorId(99L) // Tutor ajeno
                .periodo("2026-1")
                .ciclo("IV")
                .build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asgPropia, asgAjena));
        when(studentRepo.findAll()).thenReturn(Collections.emptyList());

        List<HistorialGrupoDto> historial = service.getHistorialGruposTutor(null, null);

        assertEquals(1, historial.size());
        assertEquals(1L, historial.get(0).getAsignacionId());
    }

    @Test
    void filtrosOpcionalesPorCicloYPeriodoFuncionan() {
        Assignment asg1 = Assignment.builder().id(1L).tutorId(3L).periodo("2026-1").ciclo("IV").build();
        Assignment asg2 = Assignment.builder().id(2L).tutorId(3L).periodo("2025-2").ciclo("III").build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asg1, asg2));
        when(studentRepo.findAll()).thenReturn(Collections.emptyList());

        // Filtrar por ciclo IV
        List<HistorialGrupoDto> filtroCiclo = service.getHistorialGruposTutor("IV", null);
        assertEquals(1, filtroCiclo.size());
        assertEquals("IV", filtroCiclo.get(0).getCiclo());

        // Filtrar por periodo 2025-2
        List<HistorialGrupoDto> filtroPeriodo = service.getHistorialGruposTutor(null, "2025-2");
        assertEquals(1, filtroPeriodo.size());
        assertEquals("2025-2", filtroPeriodo.get(0).getPeriodo());
    }
}
