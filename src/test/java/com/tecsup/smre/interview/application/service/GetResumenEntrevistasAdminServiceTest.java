package com.tecsup.smre.interview.application.service;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.JpaUsuarioRepository;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioEntity;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.interview.application.dto.response.GrupoEntrevistasDto;
import com.tecsup.smre.interview.application.dto.response.ResumenEntrevistasResponseDto;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaEntity;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetResumenEntrevistasAdminServiceTest {

    private StudentRepositoryPort studentRepositoryPort;
    private EntrevistaJpaRepository entrevistaJpaRepository;
    private AssignmentRepositoryPort assignmentRepositoryPort;
    private TutorRepositoryPort tutorRepositoryPort;
    private JpaUsuarioRepository jpaUsuarioRepository;

    private GetResumenEntrevistasAdminService service;

    @BeforeEach
    void setUp() {
        studentRepositoryPort = mock(StudentRepositoryPort.class);
        entrevistaJpaRepository = mock(EntrevistaJpaRepository.class);
        assignmentRepositoryPort = mock(AssignmentRepositoryPort.class);
        tutorRepositoryPort = mock(TutorRepositoryPort.class);
        jpaUsuarioRepository = mock(JpaUsuarioRepository.class);

        service = new GetResumenEntrevistasAdminService(
                studentRepositoryPort,
                entrevistaJpaRepository,
                assignmentRepositoryPort,
                tutorRepositoryPort,
                jpaUsuarioRepository
        );
    }

    @Test
    void agruparPorTutorExitosoConContadoresYUltimoNivelRiesgo() {
        // Alumno 1: con tutor directo (Tutor 10: Carlos Ramirez), 2 entrevistas (la mas reciente es MEDIO)
        Alumno a1 = Alumno.builder()
                .id(1L).codigo("A001").nombre("Luis").apellido("Gonzales")
                .carrera("Software").semestre("2024-1").grupo("A").tutorId(10L).build();

        // Alumno 2: sin tutor directo, pero con asignación que apunta a Tutor 10, 0 entrevistas -> SIN EVALUAR
        Alumno a2 = Alumno.builder()
                .id(2L).codigo("A002").nombre("Ana").apellido("Torres")
                .carrera("Software").semestre("2024-1").grupo("A").tutorId(null).build();

        // Alumno 3: sin tutor y sin asignacion -> Sin tutor asignado
        Alumno a3 = Alumno.builder()
                .id(3L).codigo("A003").nombre("Pedro").apellido("Alvarez")
                .carrera("Redes").semestre("2025-1").grupo("C").tutorId(null).build();

        when(studentRepositoryPort.findAll()).thenReturn(List.of(a1, a2, a3));

        // Entrevistas ordenadas por fecha desc
        EntrevistaEntity e1 = EntrevistaEntity.builder()
                .id(101L).alumnoId("1").nivelRiesgo(NivelRiesgo.MEDIO).fecha(LocalDateTime.now()).build();
        EntrevistaEntity e2 = EntrevistaEntity.builder()
                .id(100L).alumnoId("1").nivelRiesgo(NivelRiesgo.ALTO).fecha(LocalDateTime.now().minusDays(10)).build();

        when(entrevistaJpaRepository.findAllByOrderByFechaDesc()).thenReturn(List.of(e1, e2));

        Tutor tutor10 = Tutor.builder().id(10L).nombre("Carlos").apellido("Ramirez").build();
        when(tutorRepositoryPort.findAll()).thenReturn(List.of(tutor10));

        Assignment asg = Assignment.builder()
                .id(1L).tutorId(10L).tutorNombre("Carlos Ramirez").periodo("2024-1").especialidad("Software").grupo("A").build();
        when(assignmentRepositoryPort.findAll()).thenReturn(List.of(asg));

        ResumenEntrevistasResponseDto response = service.execute("tutor");

        assertNotNull(response);
        assertEquals("tutor", response.getAgruparPor());
        assertEquals(2, response.getGrupos().size());

        // Primer grupo: Carlos Ramirez (orden alfabetico)
        GrupoEntrevistasDto grupoTutor = response.getGrupos().get(0);
        assertEquals("Carlos Ramirez", grupoTutor.getNombre());
        assertEquals(2, grupoTutor.getTotalAlumnos());
        assertEquals(1, grupoTutor.getEvaluados()); // Solo Luis tiene entrevista

        // Verificar alumno Luis: ultimo nivel MEDIO (no ALTO)
        var luis = grupoTutor.getAlumnos().stream().filter(a -> a.getId().equals(1L)).findFirst().orElseThrow();
        assertEquals("MEDIO", luis.getUltimoNivelRiesgo());
        assertEquals(2, luis.getTotalEntrevistas());

        // Verificar alumna Ana: SIN EVALUAR
        var ana = grupoTutor.getAlumnos().stream().filter(a -> a.getId().equals(2L)).findFirst().orElseThrow();
        assertEquals("SIN EVALUAR", ana.getUltimoNivelRiesgo());
        assertEquals(0, ana.getTotalEntrevistas());

        // Segundo grupo: Sin tutor asignado (siempre al final)
        GrupoEntrevistasDto grupoSinTutor = response.getGrupos().get(1);
        assertEquals("Sin tutor asignado", grupoSinTutor.getNombre());
        assertEquals(1, grupoSinTutor.getTotalAlumnos());
        assertEquals(0, grupoSinTutor.getEvaluados());
    }

    @Test
    void agruparPorCarreraOrdenAlfabeticoYContadores() {
        Alumno a1 = Alumno.builder().id(1L).codigo("A001").nombre("Luis").apellido("Gonzales").carrera("Software").build();
        Alumno a2 = Alumno.builder().id(2L).codigo("A002").nombre("Ana").apellido("Torres").carrera("Administracion").build();
        Alumno a3 = Alumno.builder().id(3L).codigo("A003").nombre("Pedro").apellido("Alvarez").carrera("Software").build();

        when(studentRepositoryPort.findAll()).thenReturn(List.of(a1, a2, a3));
        when(entrevistaJpaRepository.findAllByOrderByFechaDesc()).thenReturn(Collections.emptyList());
        when(tutorRepositoryPort.findAll()).thenReturn(Collections.emptyList());
        when(assignmentRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        ResumenEntrevistasResponseDto response = service.execute("carrera");

        assertEquals("carrera", response.getAgruparPor());
        assertEquals(2, response.getGrupos().size());

        // Administracion va antes de Software
        assertEquals("Administracion", response.getGrupos().get(0).getNombre());
        assertEquals(1, response.getGrupos().get(0).getTotalAlumnos());

        assertEquals("Software", response.getGrupos().get(1).getNombre());
        assertEquals(2, response.getGrupos().get(1).getTotalAlumnos());
    }

    @Test
    void tutorInexistenteUsaFallbackTutorNombreDeAsignacion() {
        Alumno alumno = Alumno.builder()
                .id(1L).codigo("A001").nombre("Valeria").apellido("Chambi")
                .carrera("Topografia").semestre("2026-1").grupo("B").tutorId(null).build();

        when(studentRepositoryPort.findAll()).thenReturn(List.of(alumno));
        when(entrevistaJpaRepository.findAllByOrderByFechaDesc()).thenReturn(Collections.emptyList());
        // Tutor con id 99 NO existe en tabla tutores ni usuarios
        when(tutorRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        Assignment asg = Assignment.builder()
                .id(5L).tutorId(99L).tutorNombre("Dominga Ross Chuquilllaki")
                .periodo("2026-1").especialidad("Topografia").grupo("B").build();
        when(assignmentRepositoryPort.findAll()).thenReturn(List.of(asg));
        when(jpaUsuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResumenEntrevistasResponseDto response = service.execute("tutor");

        assertEquals(1, response.getGrupos().size());
        assertEquals("Dominga Ross Chuquilllaki", response.getGrupos().get(0).getNombre());
    }

    @Test
    void tutorInexistenteSinNombreEnAsignacionUsaFallbackGenerico() {
        Alumno alumno = Alumno.builder()
                .id(1L).codigo("A001").nombre("Valeria").apellido("Chambi")
                .carrera("Topografia").semestre("2026-1").grupo("B").tutorId(null).build();

        when(studentRepositoryPort.findAll()).thenReturn(List.of(alumno));
        when(entrevistaJpaRepository.findAllByOrderByFechaDesc()).thenReturn(Collections.emptyList());
        when(tutorRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        // tutor_nombre es null o vacio
        Assignment asg = Assignment.builder()
                .id(5L).tutorId(99L).tutorNombre(null)
                .periodo("2026-1").especialidad("Topografia").grupo("B").build();
        when(assignmentRepositoryPort.findAll()).thenReturn(List.of(asg));
        when(jpaUsuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResumenEntrevistasResponseDto response = service.execute("tutor");

        assertEquals(1, response.getGrupos().size());
        assertEquals("Tutor no registrado (id 99)", response.getGrupos().get(0).getNombre());
    }

    @Test
    void agruparPorInvalidoLanzaBadRequestException() {
        assertThrows(BadRequestException.class, () -> service.execute("otro"));
        assertThrows(BadRequestException.class, () -> service.execute("grupo"));
    }
}
