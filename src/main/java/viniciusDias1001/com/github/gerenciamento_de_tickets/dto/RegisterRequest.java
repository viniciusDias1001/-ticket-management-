package viniciusDias1001.com.github.gerenciamento_de_tickets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;

public record RegisterRequest(@NotBlank String name,
                              @NotBlank @Email String email,
                              @NotBlank @Size(min = 6, max = 72) String password,
                              @NotNull UserRole role) {
}
