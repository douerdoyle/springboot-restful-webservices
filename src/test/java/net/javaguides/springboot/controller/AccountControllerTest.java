package net.javaguides.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import net.javaguides.springboot.dto.AccountRequest;
import net.javaguides.springboot.dto.AccountResponse;
import net.javaguides.springboot.exception.RestExceptionHandler;
import net.javaguides.springboot.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static net.javaguides.springboot.controller.ApiPaths.V1_ACCOUNTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LocalValidatorFactoryBean validatorFactory;

    @BeforeEach
    void setUp() {
        validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();

        AccountController controller = new AccountController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validatorFactory)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/accounts returns 201 with persisted account")
    void createAccount_ReturnsCreatedAccount() throws Exception {
        AccountRequest request = new AccountRequest("Jane", "Doe", "jane@example.com");
        AccountResponse response = AccountResponse.of(1L, "Jane", "Doe", "jane@example.com");
        given(accountService.createAccount(any(AccountRequest.class))).willReturn(response);

        mockMvc.perform(post(V1_ACCOUNTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("jane@example.com")));

        verify(accountService).createAccount(any(AccountRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/accounts returns 400 when validation fails")
    void createAccount_InvalidPayloadReturnsBadRequest() throws Exception {
        AccountRequest request = new AccountRequest("", "Doe", "invalid-email");

        mockMvc.perform(post(V1_ACCOUNTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid request")))
                .andExpect(jsonPath("$.details.firstName", containsString("required")))
                .andExpect(jsonPath("$.details.email", containsString("valid")));

        Mockito.verifyNoInteractions(accountService);
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} returns existing account")
    void getAccountById_ReturnsAccount() throws Exception {
        AccountResponse response = AccountResponse.of(42L, "John", "Smith", "john.smith@example.com");
        given(accountService.getAccountById(42L)).willReturn(response);

        mockMvc.perform(get(V1_ACCOUNTS + "/{id}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.smith@example.com")));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} propagates 404 when account is missing")
    void getAccountById_NotFound() throws Exception {
        given(accountService.getAccountById(99L)).willThrow(new EntityNotFoundException("Account not found with id: 99"));

        mockMvc.perform(get(V1_ACCOUNTS + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Account not found with id: 99")));
    }

    @Test
    @DisplayName("GET /api/v1/accounts returns every account returned by the service")
    void getAllAccounts_ReturnsList() throws Exception {
        List<AccountResponse> responses = List.of(
                AccountResponse.of(1L, "A", "User", "a@example.com"),
                AccountResponse.of(2L, "B", "User", "b@example.com")
        );
        given(accountService.getAllAccounts()).willReturn(responses);

        mockMvc.perform(get(V1_ACCOUNTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].email", is("b@example.com")));
    }

    @Test
    @DisplayName("PUT /api/v1/accounts/{id} delegates to the service and returns updated data")
    void updateAccount_ReturnsUpdatedAccount() throws Exception {
        AccountRequest request = new AccountRequest("New", "Name", "new@example.com");
        AccountResponse response = AccountResponse.of(5L, "New", "Name", "new@example.com");
        given(accountService.updateAccount(eq(5L), any(AccountRequest.class))).willReturn(response);

        mockMvc.perform(put(V1_ACCOUNTS + "/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.firstName", is("New")));

        verify(accountService).updateAccount(eq(5L), any(AccountRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} returns 204 when deletion succeeds")
    void deleteAccount_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(V1_ACCOUNTS + "/{id}", 15L))
                .andExpect(status().isNoContent());

        verify(accountService).deleteAccount(15L);
    }
}
