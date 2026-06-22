package com.tecsup.smre.csv.domain.port.in;

import com.tecsup.smre.csv.application.dto.response.CsvUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface CargaCsvUseCase {
    CsvUploadResponseDto cargar(MultipartFile file);
}
