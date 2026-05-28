package com.tecsup.smre.auth.domain.port.out;

import com.tecsup.smre.auth.domain.model.Usuario;

public interface TokenServicePort {
    String generateToken(Usuario usuario);
}
