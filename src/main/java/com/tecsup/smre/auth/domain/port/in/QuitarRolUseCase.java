package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.domain.model.Role;

public interface QuitarRolUseCase {
    /**
     * Quita un rol a un usuario. Solo puede ser invocado por el superadmin (ksalas).
     * Si el usuario tiene ADMIN_TUTOR y se le quita ADMIN, queda como TUTOR (y viceversa).
     *
     * @param emailSolicitante email del usuario que realiza la operación
     * @param emailDestino     email del usuario al que se le quita el rol
     * @param rol              rol a quitar (ADMIN o TUTOR)
     */
    void quitarRol(String emailSolicitante, String emailDestino, Role rol);
}
