package viniciusDias1001.com.github.gerenciamento_de_tickets.it;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.TicketPriority;
import viniciusDias1001.com.github.gerenciamento_de_tickets.domain.enums.UserRole;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.TicketHistoryRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.TicketRepository;
import viniciusDias1001.com.github.gerenciamento_de_tickets.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketApiIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("tickets_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");

        registry.add("security.jwt.secret", () -> "bdu1ijtyjetq5kimuqb8yon3khvwt661");
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TicketHistoryRepository ticketHistoryRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanDb() {

        ticketHistoryRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }



    @Test
    void should_register_and_login_returning_valid_jwt() throws Exception {
        var admin = register(UserRole.ADMIN);

        MvcResult loginRes = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"email":"%s","password":"%s"}
                                """, admin.email(), admin.password())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        JsonNode body = objectMapper.readTree(loginRes.getResponse().getContentAsString());
        assertThat(body.get("accessToken").asText()).isNotBlank();
    }

    @Test
    void client_can_create_ticket_and_read_it() throws Exception {
        var client = register(UserRole.CLIENT);

        UUID ticketId = createTicket(client.token(), "Meu ticket", "Descricao", TicketPriority.HIGH);

        MvcResult getRes = mockMvc.perform(get("/tickets/{id}", ticketId)
                        .header("Authorization", bearer(client.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();

        JsonNode ticket = objectMapper.readTree(getRes.getResponse().getContentAsString());
        assertThat(ticket.get("createdBy").get("email").asText()).isEqualTo(client.email());
    }

    @Test
    void tech_cannot_create_ticket_returns_403() throws Exception {
        var tech = register(UserRole.TECH);

        mockMvc.perform(post("/tickets")
                        .header("Authorization", bearer(tech.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"title":"X","description":"Y","priority":"LOW"}
                                """)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only CLIENT can create tickets"));
    }

    @Test
    void client_cannot_read_other_clients_ticket_returns_403() throws Exception {
        var owner = register(UserRole.CLIENT);
        var other = register(UserRole.CLIENT);

        UUID ticketId = createTicket(owner.token(), "T1", "D1", TicketPriority.MEDIUM);

        mockMvc.perform(get("/tickets/{id}", ticketId)
                        .header("Authorization", bearer(other.token())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to view this ticket"));
    }

    @Test
    void client_cannot_change_status_but_tech_can() throws Exception {
        var client = register(UserRole.CLIENT);
        var tech = register(UserRole.TECH);

        UUID ticketId = createTicket(client.token(), "T", "D", TicketPriority.MEDIUM);

        // client não pode
        mockMvc.perform(patch("/tickets/{id}/status", ticketId)
                        .header("Authorization", bearer(client.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"status":"IN_PROGRESS"}
                                """)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("CLIENT cannot change ticket status"));

        // tech pode
        mockMvc.perform(patch("/tickets/{id}/status", ticketId)
                        .header("Authorization", bearer(tech.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"status":"IN_PROGRESS"}
                                """)))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/tickets/{id}", ticketId)
                        .header("Authorization", bearer(client.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void ticket_done_cannot_be_updated_or_status_changed_returns_422() throws Exception {
        var client = register(UserRole.CLIENT);
        var tech = register(UserRole.TECH);

        UUID ticketId = createTicket(client.token(), "T", "D", TicketPriority.MEDIUM);

        // marca como DONE
        mockMvc.perform(patch("/tickets/{id}/status", ticketId)
                        .header("Authorization", bearer(tech.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"status":"DONE"}
                                """)))
                .andExpect(status().isNoContent());

        // update deve falhar (422)
        mockMvc.perform(put("/tickets/{id}", ticketId)
                        .header("Authorization", bearer(client.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"title":"Novo","description":"Nova desc","priority":"HIGH"}
                                """)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Ticket is DONE and cannot be changed"));

        // status change também falha (422)
        mockMvc.perform(patch("/tickets/{id}/status", ticketId)
                        .header("Authorization", bearer(tech.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"status":"OPEN"}
                                """)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Ticket is DONE and cannot be changed"));
    }

    @Test
    void only_admin_can_delete_ticket() throws Exception {
        var client = register(UserRole.CLIENT);
        var tech = register(UserRole.TECH);
        var admin = register(UserRole.ADMIN);

        UUID ticketId = createTicket(client.token(), "T", "D", TicketPriority.MEDIUM);

        // tech não pode
        mockMvc.perform(delete("/tickets/{id}", ticketId)
                        .header("Authorization", bearer(tech.token())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only ADMIN can delete tickets"));

        // admin pode
        mockMvc.perform(delete("/tickets/{id}", ticketId)
                        .header("Authorization", bearer(admin.token())))
                .andExpect(status().isNoContent());


        mockMvc.perform(get("/tickets/{id}", ticketId)
                        .header("Authorization", bearer(admin.token())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket not found"));
    }

    @Test
    void history_is_recorded_for_ticket_actions() throws Exception {
        var client = register(UserRole.CLIENT);
        var tech = register(UserRole.TECH);

        UUID ticketId = createTicket(client.token(), "T", "D", TicketPriority.MEDIUM);

        // muda status
        mockMvc.perform(patch("/tickets/{id}/status", ticketId)
                        .header("Authorization", bearer(tech.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"status":"IN_PROGRESS"}
                                """)))
                .andExpect(status().isNoContent());


        mockMvc.perform(patch("/tickets/{id}/assign/{techId}", ticketId, tech.userId())
                        .header("Authorization", bearer(tech.token())))
                .andExpect(status().isNoContent());

        // history
        MvcResult historyRes = mockMvc.perform(get("/tickets/{id}/history?page=0&size=20", ticketId)
                        .header("Authorization", bearer(client.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        JsonNode content = objectMapper.readTree(historyRes.getResponse().getContentAsString()).get("content");
        assertThat(content.size()).isGreaterThanOrEqualTo(3); // CREATED + STATUS_CHANGED + ASSIGNED

        Set<String> actions = new HashSet<>();
        for (JsonNode h : content) {
            actions.add(h.get("action").asText());
        }

        assertThat(actions).contains("CREATED", "STATUS_CHANGED", "ASSIGNED");
    }



    private record TestUser(String token, UUID userId, String email, String password, UserRole role) {}

    private TestUser register(UserRole role) throws Exception {
        String email = role.name().toLowerCase() + "+" + UUID.randomUUID() + "@test.com";
        String password = "Password@123";

        MvcResult res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"name":"%s","email":"%s","password":"%s","role":"%s"}
                                """, role.name(), email, password, role.name())))

                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(res.getResponse().getContentAsString());
        return new TestUser(
                body.get("accessToken").asText(),
                UUID.fromString(body.get("userId").asText()),
                email,
                password,
                role
        );
    }

    private UUID createTicket(String token, String title, String description, TicketPriority priority) throws Exception {
        MvcResult res = mockMvc.perform(post("/tickets")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"title":"%s","description":"%s","priority":"%s"}
                                """, title, description, priority.name())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        JsonNode ticket = objectMapper.readTree(res.getResponse().getContentAsString());
        return UUID.fromString(ticket.get("id").asText());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String json(String template, Object... args) {
        return template.formatted(args);
    }
}
