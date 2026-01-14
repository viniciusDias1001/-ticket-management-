package viniciusDias1001.com.github.gerenciamento_de_tickets.service.impl;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.entity.User;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.AuthResponse;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.LoginRequest;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.RegisterRequest;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.ConflictException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.exception.UnauthorizedException;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.UserRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.security.JwtService;


@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email already registered");
        }

        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setRole(req.role());
        u.setPasswordHash(passwordEncoder.encode(req.password()));

        User saved = userRepository.save(u);

        String token = jwtService.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
