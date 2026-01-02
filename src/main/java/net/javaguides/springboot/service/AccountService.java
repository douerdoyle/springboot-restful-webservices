package net.javaguides.springboot.service;

import net.javaguides.springboot.dto.AccountRequest;
import net.javaguides.springboot.dto.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest accountRequest);

    AccountResponse getAccountById(Long accountId);

    List<AccountResponse> getAllAccounts();

    AccountResponse updateAccount(Long accountId, AccountRequest accountRequest);

    void deleteAccount(Long accountId);
}
