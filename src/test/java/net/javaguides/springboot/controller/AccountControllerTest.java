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
    @DisplayName("POST /api/accounts returns 201 with persisted account")
    void createAccount_ReturnsCreatedAccount() throws Exception {
        AccountRequest request = new AccountRequest("Jane", "Doe", "jane@example.com");
        AccountResponse response = AccountResponse.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .build();
        given(accountService.createAccount(any(AccountRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("jane@example.com")));

        verify(accountService).createAccount(any(AccountRequest.class));
    }

    @Test
    @DisplayName("POST /api/accounts returns 400 when validation fails")
    void createAccount_InvalidPayloadReturnsBadRequest() throws Exception {
        AccountRequest request = new AccountRequest("", "Doe", "invalid-email");

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid request")))
                .andExpect(jsonPath("$.details.firstName", containsString("required")))
                .andExpect(jsonPath("$.details.email", containsString("valid")));

        Mockito.verifyNoInteractions(accountService);
    }

    @Test
    @DisplayName("GET /api/accounts/{id} returns existing account")
    void getAccountById_ReturnsAccount() throws Exception {
        AccountResponse response = AccountResponse.builder()
                .id(42L)
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .build();
        given(accountService.getAccountById(42L)).willReturn(response);

        mockMvc.perform(get("/api/accounts/{id}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.smith@example.com")));
    }

    @Test
    @DisplayName("GET /api/accounts/{id} propagates 404 when account is missing")
    void getAccountById_NotFound() throws Exception {
        given(accountService.getAccountById(99L)).willThrow(new EntityNotFoundException("Account not found with id: 99"));

        mockMvc.perform(get("/api/accounts/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Account not found with id: 99")));
    }

    @Test
    @DisplayName("GET /api/accounts returns every account returned by the service")
    void getAllAccounts_ReturnsList() throws Exception {
        List<AccountResponse> responses = List.of(
                AccountResponse.builder().id(1L).firstName("A").lastName("User").email("a@example.com").build(),
                AccountResponse.builder().id(2L).firstName("B").lastName("User").email("b@example.com").build()
        );
        given(accountService.getAllAccounts()).willReturn(responses);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].email", is("b@example.com")));
    }

    @Test
    @DisplayName("PUT /api/accounts/{id} delegates to the service and returns updated data")
    void updateAccount_ReturnsUpdatedAccount() throws Exception {
        AccountRequest request = new AccountRequest("New", "Name", "new@example.com");
        AccountResponse response = AccountResponse.builder()
                .id(5L)
                .firstName("New")
                .lastName("Name")
                .email("new@example.com")
                .build();
        given(accountService.updateAccount(eq(5L), any(AccountRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/accounts/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.firstName", is("New")));

        verify(accountService).updateAccount(eq(5L), any(AccountRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id} returns 204 when deletion succeeds")
    void deleteAccount_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/accounts/{id}", 15L))
                .andExpect(status().isNoContent());

        verify(accountService).deleteAccount(15L);
    }
}
