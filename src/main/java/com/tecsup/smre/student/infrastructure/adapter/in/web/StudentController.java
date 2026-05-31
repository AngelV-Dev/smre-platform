package com.tecsup.smre.student.infrastructure.adapter.in.web;

import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.student.application.dto.request.StudentRequest;
import com.tecsup.smre.student.application.dto.response.StudentResponse;
import com.tecsup.smre.student.domain.port.in.ManageStudentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final ManageStudentUseCase manageStudentUseCase;

    public StudentController(ManageStudentUseCase manageStudentUseCase) {
        this.manageStudentUseCase = manageStudentUseCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> findAll() {
        List<StudentResponse> alumnos = manageStudentUseCase.findAll();
        return ResponseEntity.ok(
                ApiResponse.success(alumnos, "Alumnos obtenidos correctamente"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = manageStudentUseCase.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Alumno registrado correctamente"));
    }

    @PostMapping("/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> uploadCsv(@RequestParam("file") MultipartFile file) {
        List<StudentResponse> importados = manageStudentUseCase.uploadCsv(file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(importados, importados.size() + " alumnos importados correctamente"));
    }
}