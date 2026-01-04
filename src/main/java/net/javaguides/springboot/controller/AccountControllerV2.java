package net.javaguides.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.javaguides.springboot.dto.AccountRequest;
import net.javaguides.springboot.dto.AccountResponse;
import net.javaguides.springboot.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(ApiPaths.V2_ACCOUNTS)
@Tag(name = "Accounts v2", description = "CRUD APIs for managing accounts with Basic Auth protection")
public class AccountControllerV2 {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create account (v2)", description = "Creates a new account from the provided payload")
    @ApiResponse(responseCode = "201", description = "Account created successfully")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        AccountResponse savedAccount = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get account (v2)", description = "Returns account details by id")
    @ApiResponse(responseCode = "200", description = "Account retrieved successfully")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("id") Long accountId) {
        AccountResponse account = accountService.getAccountById(accountId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "List accounts (v2)", description = "Fetches every account in the system")
    @ApiResponse(responseCode = "200", description = "Accounts fetched successfully")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update account (v2)", description = "Updates an existing account with the provided payload")
    @ApiResponse(responseCode = "200", description = "Account updated successfully")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable("id") Long accountId,
                                                         @Valid @RequestBody AccountRequest accountRequest) {
        AccountResponse updatedAccount = accountService.updateAccount(accountId, accountRequest);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete account (v2)", description = "Deletes an account by id")
    @ApiResponse(responseCode = "204", description = "Account deleted successfully")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
