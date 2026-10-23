package com.tecsup.smre.user.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.service.RoleAssignmentService;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.user.application.dto.request.EditarTutorRequest;
import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TutorServiceTest {

    @Test
    void actualizarNormalizaCarrerasYPermiteQuitarTodas() {
        TutorRepositoryPort tutorRepository = mock(TutorRepositoryPort.class);
        Tutor tutor = Tutor.builder()
                .id(1L)
                .nombre("Tutor")
                .apellido("Prueba")
                .email("tutor@tecsup.edu.pe")
                .telefono("999999999")
                .rol(Role.TUTOR)
                .carreras(List.of("Anterior"))
                .build();
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TutorService service = new TutorService(
                tutorRepository,
                mock(PasswordEncoderPort.class),
                mock(UsuarioRepositoryPort.class),
                mock(RoleAssignmentService.class));
        EditarTutorRequest request = new EditarTutorRequest(
                "Tutor", "Prueba", "999999999", null, List.of(" Software ", "software"));

        var response = service.actualizar(1L, request);

        assertEquals(List.of("Software"), response.getCarreras());
        assertTrue(response.isCarrerasConfiguradas());

        request.setCarreras(List.of());
        response = service.actualizar(1L, request);

        assertEquals(List.of(), response.getCarreras());
        assertTrue(response.isCarrerasConfiguradas());
    }
}
