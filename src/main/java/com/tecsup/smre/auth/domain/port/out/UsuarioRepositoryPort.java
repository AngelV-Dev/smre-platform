package com.tecsup.smre.auth.domain.port.out;

import com.tecsup.smre.auth.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
}