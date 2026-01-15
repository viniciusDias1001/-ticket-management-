package viniciusDias1001.com.github.gerenciamento_de_tickets.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.AuthResponse;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.LoginRequest;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.RegisterRequest;
import viniciusDias1001.com.github.gerenciamento_de_tickets.service.impl.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
