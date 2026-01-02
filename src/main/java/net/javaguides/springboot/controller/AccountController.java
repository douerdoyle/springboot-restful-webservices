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
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "CRUD APIs for managing accounts")
public class AccountController {

    private AccountService accountService;

    // build create Account REST API
    @PostMapping
    @Operation(summary = "Create account", description = "Creates a new account from the provided payload")
    @ApiResponse(responseCode = "201", description = "Account created successfully")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        AccountResponse savedAccount = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    // build get account by id REST API
    // http://localhost:8080/api/accounts/1
    @GetMapping("{id}")
    @Operation(summary = "Get account", description = "Returns account details by id")
    @ApiResponse(responseCode = "200", description = "Account retrieved successfully")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("id") Long accountId) {
        AccountResponse account = accountService.getAccountById(accountId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    // Build Get All Accounts REST API
    // http://localhost:8080/api/accounts
    @GetMapping
    @Operation(summary = "List accounts", description = "Fetches every account in the system")
    @ApiResponse(responseCode = "200", description = "Accounts fetched successfully")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Build Update Account REST API
    @PutMapping("{id}")
    // http://localhost:8080/api/accounts/1
    @Operation(summary = "Update account", description = "Updates an existing account with the provided payload")
    @ApiResponse(responseCode = "200", description = "Account updated successfully")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable("id") Long accountId,
                                                         @Valid @RequestBody AccountRequest accountRequest) {
        AccountResponse updatedAccount = accountService.updateAccount(accountId, accountRequest);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    // Build Delete Account REST API
    @DeleteMapping("{id}")
    @Operation(summary = "Delete account", description = "Deletes an account by id")
    @ApiResponse(responseCode = "204", description = "Account deleted successfully")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
