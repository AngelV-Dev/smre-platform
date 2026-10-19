package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.domain.model.Role;

public interface AsignarRolUseCase {
    /**
     * Asigna un rol a un usuario. Solo puede ser invocado por el superadmin (ksalas).
     * Si el usuario ya tiene el rol opuesto, se hace upgrade a ADMIN_TUTOR.
     *
     * @param emailSolicitante email del usuario que realiza la operación
     * @param emailDestino     email del usuario al que se le asigna el rol
     * @param rol              rol a asignar (ADMIN o TUTOR)
     */
    void asignarRol(String emailSolicitante, String emailDestino, Role rol);
}
