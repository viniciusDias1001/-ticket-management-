package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String email,
                           @NotBlank String password) {
}
