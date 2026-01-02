package net.javaguides.springboot.service;

import jakarta.persistence.EntityNotFoundException;
import net.javaguides.springboot.dto.AccountRequest;
import net.javaguides.springboot.dto.AccountResponse;
import net.javaguides.springboot.entity.Account;
import net.javaguides.springboot.repository.AccountRepository;
import net.javaguides.springboot.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("createAccount persists a new entity and maps the response")
    void createAccount_PersistsEntity() {
        AccountRequest request = new AccountRequest("Jane", "Doe", "jane@example.com");
        Account persisted = new Account(1L, "Jane", "Doe", "jane@example.com");
        given(accountRepository.save(any(Account.class))).willReturn(persisted);

        AccountResponse response = accountService.createAccount(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("jane@example.com");

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("getAccountById returns mapped response when repository contains the entity")
    void getAccountById_ReturnsResponse() {
        Account account = new Account(10L, "John", "Smith", "john@example.com");
        given(accountRepository.findById(10L)).willReturn(Optional.of(account));

        AccountResponse response = accountService.getAccountById(10L);

        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("getAccountById throws when entity is missing")
    void getAccountById_NotFound() {
        given(accountRepository.findById(55L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(55L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("55");
    }

    @Test
    @DisplayName("updateAccount overwrites fields and saves the entity")
    void updateAccount_ReplacesExistingValues() {
        Account existing = new Account(3L, "Old", "Name", "old@example.com");
        given(accountRepository.findById(3L)).willReturn(Optional.of(existing));
        Account saved = new Account(3L, "New", "Name", "new@example.com");
        given(accountRepository.save(any(Account.class))).willReturn(saved);
        AccountRequest request = new AccountRequest("New", "Name", "new@example.com");

        AccountResponse response = accountService.updateAccount(3L, request);

        assertThat(response.getEmail()).isEqualTo("new@example.com");
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo("New");
    }

    @Test
    @DisplayName("deleteAccount removes existing entities and throws when missing")
    void deleteAccount_HandlesExistingAndMissingCases() {
        given(accountRepository.existsById(7L)).willReturn(true);

        accountService.deleteAccount(7L);
        verify(accountRepository).deleteById(7L);

        given(accountRepository.existsById(8L)).willReturn(false);
        assertThatThrownBy(() -> accountService.deleteAccount(8L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("8");
    }
}
