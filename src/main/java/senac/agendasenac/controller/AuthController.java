package senac.agendasenac.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import senac.agendasenac.dto.LoginDTO;
import senac.agendasenac.dto.LoginResponseDTO;
import senac.agendasenac.entity.Usuario;
import senac.agendasenac.security.TokenService;
import senac.agendasenac.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    // O app guarda o token e manda nas próximas chamadas
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        Usuario usuario = authService.login(dto.email(), dto.senha());

        String token = tokenService.gerar(usuario);

        return ResponseEntity.ok(LoginResponseDTO.of(usuario, token));
    }
}
