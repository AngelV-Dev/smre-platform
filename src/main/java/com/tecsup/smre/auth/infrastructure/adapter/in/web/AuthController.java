package com.tecsup.smre.auth.infrastructure.adapter.in.web;

import com.tecsup.smre.auth.application.dto.request.LoginRequest;
import com.tecsup.smre.auth.application.dto.response.LoginResponse;
import com.tecsup.smre.auth.application.dto.response.UsuarioActualResponse;
import com.tecsup.smre.auth.domain.port.in.GetUsuarioActualUseCase;
import com.tecsup.smre.auth.domain.port.in.LoginUseCase;
import com.tecsup.smre.auth.domain.port.in.LogoutUseCase;
import com.tecsup.smre.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetUsuarioActualUseCase getUsuarioActualUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          LogoutUseCase logoutUseCase,
                          GetUsuarioActualUseCase getUsuarioActualUseCase) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.getUsuarioActualUseCase = getUsuarioActualUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Sesión iniciada con éxito"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        logoutUseCase.logout(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Sesión cerrada correctamente"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioActualResponse>> me(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UsuarioActualResponse usuario = getUsuarioActualUseCase.getUsuario(token);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Usuario obtenido correctamente"));
    }
}