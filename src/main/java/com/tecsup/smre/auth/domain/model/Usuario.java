package com.tecsup.smre.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Role rol;

    public boolean hasTecsupDomain() {
        return email != null && email.endsWith("@tecsup.edu.pe");
    }
}
