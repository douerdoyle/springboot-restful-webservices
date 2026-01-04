package net.javaguides.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javaguides.springboot.config.SecurityConfig;
import net.javaguides.springboot.dto.AccountRequest;
import net.javaguides.springboot.dto.AccountResponse;
import net.javaguides.springboot.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static net.javaguides.springboot.controller.ApiPaths.V2_ACCOUNTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountControllerV2.class)
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "app.security.admins[0].username=admin",
        "app.security.admins[0].password=admin-password",
        "app.security.viewers[0].username=viewer1",
        "app.security.viewers[0].password=viewer1-password",
        "app.security.viewers[1].username=viewer2",
        "app.security.viewers[1].password=viewer2-password"
})
class AccountControllerV2SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Viewer credentials can call GET /api/v2/accounts/{id}")
    void viewerCanFetchAccount() throws Exception {
        AccountResponse response = AccountResponse.of(15L, "Viewer", "User", "viewer@example.com");
        given(accountService.getAccountById(15L)).willReturn(response);

        mockMvc.perform(get(V2_ACCOUNTS + "/{id}", 15L)
                        .with(httpBasic("viewer1", "viewer1-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Viewer"));
    }

    @Test
    @DisplayName("Viewer credentials are forbidden from POST /api/v2/accounts")
    void viewerCannotCreateAccount() throws Exception {
        AccountRequest request = new AccountRequest("Blocked", "Viewer", "viewer@example.com");

        mockMvc.perform(post(V2_ACCOUNTS)
                        .with(httpBasic("viewer2", "viewer2-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin credentials can POST /api/v2/accounts")
    void adminCanCreateAccount() throws Exception {
        AccountRequest request = new AccountRequest("Admin", "Access", "admin@example.com");
        AccountResponse response = AccountResponse.of(1L, "Admin", "Access", "admin@example.com");
        given(accountService.createAccount(any(AccountRequest.class))).willReturn(response);

        mockMvc.perform(post(V2_ACCOUNTS)
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(accountService).createAccount(ArgumentMatchers.any(AccountRequest.class));
    }

    @Test
    @DisplayName("Missing credentials receive 401 on /api/v2/accounts")
    void missingCredentialsRejected() throws Exception {
        mockMvc.perform(get(V2_ACCOUNTS))
                .andExpect(status().isUnauthorized());
    }
}
