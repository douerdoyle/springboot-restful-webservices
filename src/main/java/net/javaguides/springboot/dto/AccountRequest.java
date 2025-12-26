package net.javaguides.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AccountRequest", description = "Payload required to create or update an account")
public class AccountRequest {

    @Schema(description = "First name of the account holder", example = "Jane")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Last name of the account holder", example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Unique email for the account", example = "jane.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
