package com.tecsup.smre.student.domain.port.in;

import com.tecsup.smre.student.application.dto.request.StudentRequest;
import com.tecsup.smre.student.application.dto.response.StudentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ManageStudentUseCase {
    List<StudentResponse> findAll();
    StudentResponse create(StudentRequest request);
    List<StudentResponse> uploadCsv(MultipartFile file);
}
