package com.tecsup.smre.interview.application.service;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.interview.application.dto.EstadisticasTutorResponse;
import com.tecsup.smre.interview.application.dto.HistorialGrupoDto;
import com.tecsup.smre.interview.application.port.out.EntrevistaRepositoryPort;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import com.tecsup.smre.user.domain.model.Tutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
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

        // Mock autenticación usando Authentication mockeada para el tutor con ID 3L
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
    void listaAlumnosRespetaLasCarrerasConfiguradasDelTutor() {
        when(tutorRepo.findAll()).thenReturn(List.of(Tutor.builder()
                .id(4L)
                .email("tutor@tecsup.edu.pe")
                .carreras(List.of("Software"))
                .carrerasConfiguradas(true)
                .build()));
        when(assignmentRepo.findAll()).thenReturn(Collections.emptyList());
        when(studentRepo.findAll()).thenReturn(List.of(
                Alumno.builder().id(101L).carrera("Software").build(),
                Alumno.builder().id(102L).carrera("Diseño").build()
        ));
        when(entrevistaRepo.findByAlumnoId(anyString())).thenReturn(Collections.emptyList());

        List<Long> ids = service.getAlumnosTutor().stream()
                .map(alumno -> alumno.getId())
                .toList();

        assertEquals(List.of(101L), ids);
    }

    @Test
    void quitarTodasLasCarrerasOcultaAlumnosPeroConservaHistorialDeGrupo() {
        Assignment assignment = Assignment.builder()
                .id(5L)
                .tutorId(3L)
                .periodo("2025-2")
                .especialidad("Software")
                .ciclo("III")
                .grupo("A")
                .build();
        Alumno alumno = Alumno.builder()
                .id(101L)
                .semestre("2025-2")
                .carrera("Software")
                .grupo("A")
                .build();

        when(tutorRepo.findAll()).thenReturn(List.of(Tutor.builder()
                .id(4L)
                .email("tutor@tecsup.edu.pe")
                .carreras(Collections.emptyList())
                .carrerasConfiguradas(true)
                .build()));
        when(assignmentRepo.findAll()).thenReturn(List.of(assignment));
        when(studentRepo.findAll()).thenReturn(List.of(alumno));
        when(entrevistaRepo.findByAlumnoId("101")).thenReturn(Collections.emptyList());

        assertTrue(service.getAlumnosTutor().isEmpty());
        assertEquals(1, service.getHistorialGruposTutor(null, null).size());
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

    // ==========================================
    // TESTS TAREA C: ESTADÍSTICAS DEL TUTOR
    // ==========================================

    @Test
    void estadisticasCoincidenConHistorialDeGruposYSanitizanMultiplesEntrevistasConLaMasReciente() {
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

        // al1 tiene 2 entrevistas: primero BAJO, luego ALTO más reciente
        Entrevista eAntigua = Entrevista.builder().id(1L).nivelRiesgo(NivelRiesgo.BAJO).fecha(LocalDateTime.now().minusDays(10)).build();
        Entrevista eReciente = Entrevista.builder().id(2L).nivelRiesgo(NivelRiesgo.ALTO).fecha(LocalDateTime.now()).build();
        when(entrevistaRepo.findByAlumnoId("101")).thenReturn(List.of(eAntigua, eReciente));

        // al2 sin entrevistas
        when(entrevistaRepo.findByAlumnoId("102")).thenReturn(Collections.emptyList());

        // 1. Obtener Estadísticas
        EstadisticasTutorResponse stats = service.getEstadisticasTutor();

        // 2. Obtener Historial
        List<HistorialGrupoDto> historial = service.getHistorialGruposTutor(null, null);

        // Verificación 1: Consistencia de números entre estadísticas e historial
        assertEquals(historial.get(0).getTotalAlumnos(), stats.getTotalAlumnos());
        assertEquals(historial.get(0).getEntrevistasCompletadas(), stats.getEvaluadosCount());
        assertEquals(2, stats.getTotalAlumnos());
        assertEquals(1, stats.getEvaluadosCount());
        assertEquals(1, stats.getPendientesCount());

        // Verificación 2: Nivel de riesgo usa estrictamente la más reciente (ALTO, no BAJO)
        assertEquals(1, stats.getAltoRiesgoCount());
        assertEquals(0, stats.getBajoRiesgoCount());
        assertEquals(0, stats.getMedioRiesgoCount());

        // Verificación 3: Desglose por grupo
        assertEquals(1, stats.getGrupos().size());
        assertEquals(2, stats.getGrupos().get(0).getTotalAlumnos());
        assertEquals(1, stats.getGrupos().get(0).getEvaluadosCount());
        assertEquals(1, stats.getGrupos().get(0).getAltoRiesgoCount());
    }

    @Test
    void estadisticasAislanCompletamenteDatosDeTutorAjeno() {
        Assignment asgPropia = Assignment.builder()
                .id(1L)
                .tutorId(3L)
                .periodo("2026-1")
                .ciclo("IV")
                .especialidad("Diseño y Desarrollo de Software")
                .grupo("A")
                .build();

        Assignment asgAjena = Assignment.builder()
                .id(2L)
                .tutorId(99L) // Tutor ajeno
                .periodo("2026-1")
                .ciclo("IV")
                .especialidad("Diseño y Desarrollo de Software")
                .grupo("B")
                .build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asgPropia, asgAjena));

        Alumno alPropio = Alumno.builder().id(101L).semestre("2026-1").carrera("Diseño y Desarrollo de Software").grupo("A").build();
        Alumno alAjeno = Alumno.builder().id(999L).semestre("2026-1").carrera("Diseño y Desarrollo de Software").grupo("B").build();

        when(studentRepo.findAll()).thenReturn(List.of(alPropio, alAjeno));
        when(entrevistaRepo.findByAlumnoId("101")).thenReturn(Collections.emptyList());
        when(entrevistaRepo.findByAlumnoId("999")).thenReturn(List.of(new Entrevista()));

        EstadisticasTutorResponse stats = service.getEstadisticasTutor();

        // El tutor 3L solo debe ver a su alumno propio (101), no al alumno del grupo ajeno (999)
        assertEquals(1, stats.getTotalAlumnos());
        assertEquals(1, stats.getGrupos().size());
        assertEquals(1L, stats.getGrupos().get(0).getAsignacionId());
    }

    @Test
    void filtroCicloNumeroEncuentraAsignacionConRomanoYConNumero() {
        Assignment asgRomano = Assignment.builder()
                .id(1L).tutorId(3L).periodo("2026-1").ciclo("IV").especialidad("Software").grupo("A").build();
        Assignment asgNumero = Assignment.builder()
                .id(2L).tutorId(3L).periodo("2026-1").ciclo("4").especialidad("Redes").grupo("B").build();
        Assignment asgOtro = Assignment.builder()
                .id(3L).tutorId(3L).periodo("2026-1").ciclo("2").especialidad("Mecatrónica").grupo("C").build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asgRomano, asgNumero, asgOtro));
        when(studentRepo.findAll()).thenReturn(Collections.emptyList());

        // Filtrar con número "4" debe encontrar tanto "IV" como "4"
        List<HistorialGrupoDto> res = service.getHistorialGruposTutor("4", null);

        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(g -> g.getCiclo().equals("IV")));
        assertTrue(res.stream().anyMatch(g -> g.getCiclo().equals("4")));
    }

    @Test
    void filtroCicloRomanoEncuentraAsignacionConRomanoYConNumero() {
        Assignment asgRomano = Assignment.builder()
                .id(1L).tutorId(3L).periodo("2026-1").ciclo("IV").especialidad("Software").grupo("A").build();
        Assignment asgNumero = Assignment.builder()
                .id(2L).tutorId(3L).periodo("2026-1").ciclo("4").especialidad("Redes").grupo("B").build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asgRomano, asgNumero));
        when(studentRepo.findAll()).thenReturn(Collections.emptyList());

        // Filtrar con romano "IV" debe encontrar tanto "IV" como "4"
        List<HistorialGrupoDto> res = service.getHistorialGruposTutor("IV", null);

        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(g -> g.getCiclo().equals("IV")));
        assertTrue(res.stream().anyMatch(g -> g.getCiclo().equals("4")));
    }

    @Test
    void filtroCicloInvalidoNoRompeYDevuelveVacio() {
        Assignment asg1 = Assignment.builder()
                .id(1L).tutorId(3L).periodo("2026-1").ciclo("IV").especialidad("Software").grupo("A").build();

        when(assignmentRepo.findAll()).thenReturn(List.of(asg1));
        when(studentRepo.findAll()).thenReturn(Collections.emptyList());

        // Valor inválido "99" o texto raro no debe lanzar excepción y debe devolver lista vacía
        List<HistorialGrupoDto> res = service.getHistorialGruposTutor("99", null);

        assertNotNull(res);
        assertTrue(res.isEmpty());
    }
}
