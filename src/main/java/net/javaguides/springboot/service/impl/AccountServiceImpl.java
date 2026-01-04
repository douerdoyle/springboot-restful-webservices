package net.javaguides.springboot.service.impl;

import lombok.AllArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import net.javaguides.springboot.dto.AccountRequest;
import net.javaguides.springboot.dto.AccountResponse;
import net.javaguides.springboot.entity.Account;
import net.javaguides.springboot.repository.AccountRepository;
import net.javaguides.springboot.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {
        Account account = mapToEntity(accountRequest);
        Account savedAccount = accountRepository.save(account);
        return mapToResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + accountId));
        return mapToResponse(account);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AccountResponse updateAccount(Long accountId, AccountRequest accountRequest) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + accountId));

        existingAccount.setFirstName(accountRequest.getFirstName());
        existingAccount.setLastName(accountRequest.getLastName());
        existingAccount.setEmail(accountRequest.getEmail());

        Account updatedAccount = accountRepository.save(existingAccount);
        return mapToResponse(updatedAccount);
    }

    @Override
    public void deleteAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new EntityNotFoundException("Account not found with id: " + accountId);
        }
        accountRepository.deleteById(accountId);
    }

    private Account mapToEntity(AccountRequest request) {
        Account account = new Account();
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setEmail(request.getEmail());
        return account;
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.of(
                account.getId(),
                account.getFirstName(),
                account.getLastName(),
                account.getEmail());
    }
}
