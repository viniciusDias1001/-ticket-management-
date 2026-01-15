package viniciusDias1001.com.github.gerenciamento_de_tickets.controller;


import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.UserSummaryResponse;
import viniciusDias1001.com.github.gerenciamento_de_tickets.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private UUID requesterId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @GetMapping("/myId")
    @ResponseStatus(HttpStatus.OK)
    public UserSummaryResponse me(@AuthenticationPrincipal Jwt jwt) {
        UUID id = requesterId(jwt);
        return userService.getById(id, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserSummaryResponse> list(@AuthenticationPrincipal Jwt jwt,
                                          @RequestParam(required = false) UserRole role,
                                          @ParameterObject Pageable pageable) {
        return userService.list(requesterId(jwt), role, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserSummaryResponse getById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return userService.getById(requesterId(jwt), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        userService.delete(requesterId(jwt), id);
    }
}
