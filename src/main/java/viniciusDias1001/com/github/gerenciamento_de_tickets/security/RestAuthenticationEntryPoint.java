package viniciusDias1001.com.github.gerenciamento_de_tickets.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import viniciusDias1001.com.github.gerenciamento_de_tickets.dto.ApiError;

import java.io.IOException;
import java.time.OffsetDateTime;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {

        ApiError body = new ApiError(
                OffsetDateTime.now(),
                401,
                "Unauthorized",
                "Authentication required",
                request.getRequestURI(),
                null
        );

        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
