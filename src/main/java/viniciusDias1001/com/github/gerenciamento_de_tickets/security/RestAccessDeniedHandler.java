package viniciusDias1001.com.github.gerenciamento_de_tickets.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.ApiError;

import java.io.IOException;
import java.time.OffsetDateTime;

public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {

        ApiError body = new ApiError(
                OffsetDateTime.now(),
                403,
                "Forbidden",
                "Access denied",
                request.getRequestURI(),
                null
        );

        response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
