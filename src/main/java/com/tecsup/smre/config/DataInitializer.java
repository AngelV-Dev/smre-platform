package com.tecsup.smre.config;

import com.tecsup.smre.auth.domain.model.EmailMatriculado;
import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TutorRepositoryPort tutorRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort;

    public DataInitializer(UsuarioRepositoryPort usuarioRepositoryPort,
                           TutorRepositoryPort tutorRepositoryPort,
                           StudentRepositoryPort studentRepositoryPort,
                           PasswordEncoderPort passwordEncoderPort,
                           EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.tutorRepositoryPort = tutorRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.emailMatriculadoRepositoryPort = emailMatriculadoRepositoryPort;
    }

    @Override
    public void run(String... args) throws Exception {
        String passAdmin = passwordEncoderPort.encode("admin123");
        String passTutor = passwordEncoderPort.encode("tutor123");

        // 0. Inicializar matrícula de correos permitidos
        matricularEmailSiNoExiste("admin@tecsup.edu.pe");
        matricularEmailSiNoExiste("tutor@tecsup.edu.pe");
        matricularEmailSiNoExiste("ksalas@tecsup.edu.pe");

        // 1. Crear Administrador por defecto si no existe
        if (usuarioRepositoryPort.findByEmail("admin@tecsup.edu.pe").isEmpty()) {
            usuarioRepositoryPort.save(Usuario.builder()
                    .nombre("Administrador Principal")
                    .email("admin@tecsup.edu.pe")
                    .password(passAdmin)
                    .rol(Role.ADMIN)
                    .activo(true)
                    .build());
        }

        // 2. Crear Tutor por defecto si no existe
        if (usuarioRepositoryPort.findByEmail("tutor@tecsup.edu.pe").isEmpty()) {
            usuarioRepositoryPort.save(Usuario.builder()
                    .nombre("Juan Perez")
                    .email("tutor@tecsup.edu.pe")
                    .password(passTutor)
                    .rol(Role.TUTOR)
                    .activo(true)
                    .build());
        }

        if (!tutorRepositoryPort.existsByEmail("tutor@tecsup.edu.pe")) {
            tutorRepositoryPort.save(Tutor.builder()
                    .nombre("Juan")
                    .apellido("Perez")
                    .email("tutor@tecsup.edu.pe")
                    .password(passTutor)
                    .telefono("987654321")
                    .rol(Role.TUTOR)
                    .activo(true)
                    .build());
        }

        // 3. Crear usuario ksalas@tecsup.edu.pe con roles ADMIN y TUTOR
        if (usuarioRepositoryPort.findByEmail("ksalas@tecsup.edu.pe").isEmpty()) {
            usuarioRepositoryPort.save(Usuario.builder()
                    .nombre("Katherine Salas")
                    .email("ksalas@tecsup.edu.pe")
                    .password(passAdmin)
                    .rol(Role.ADMIN_TUTOR)
                    .activo(true)
                    .build());
        }

        if (!tutorRepositoryPort.existsByEmail("ksalas@tecsup.edu.pe")) {
            tutorRepositoryPort.save(Tutor.builder()
                    .nombre("Katherine")
                    .apellido("Salas")
                    .email("ksalas@tecsup.edu.pe")
                    .password(passAdmin)
                    .telefono("999888777")
                    .rol(Role.ADMIN_TUTOR)
                    .activo(true)
                    .build());
        }

        // 4. Crear Alumnos de prueba si la lista está vacía
        if (studentRepositoryPort.findAll().isEmpty()) {
            studentRepositoryPort.save(Alumno.builder()
                    .codigo("ALU001")
                    .nombre("Carlos")
                    .apellido("Mendoza")
                    .email("carlos.mendoza@tecsup.edu.pe")
                    .carrera("Diseño y Desarrollo de Software")
                    .semestre("IV")
                    .grupo("A")
                    .edad(20)
                    .build());

            studentRepositoryPort.save(Alumno.builder()
                    .codigo("ALU002")
                    .nombre("Maria")
                    .apellido("Lopez")
                    .email("maria.lopez@tecsup.edu.pe")
                    .carrera("Redes y Comunicaciones")
                    .semestre("IV")
                    .grupo("B")
                    .edad(21)
                    .build());
        }
    }

    private void matricularEmailSiNoExiste(String email) {
        if (!emailMatriculadoRepositoryPort.existsByEmail(email)) {
            emailMatriculadoRepositoryPort.save(EmailMatriculado.builder()
                    .email(email.toLowerCase())
                    .fechaRegistro(LocalDateTime.now())
                    .build());
        }
    }
}
