package com.tecsup.smre.student.domain.model;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {
    private Long id;
    private String codigo;
    private String nombre;
    private String apellido;
    private String email;
    private String carrera;
    private String semestre;  
    private String grupo;     
}
 