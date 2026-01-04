package net.javaguides.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AccountResponse", description = "Representation returned to clients for account resources")
public class AccountResponse {

    @Schema(description = "Unique identifier of the account", example = "1")
    private Long id;
    @Schema(description = "First name of the account holder", example = "Jane")
    private String firstName;
    @Schema(description = "Last name of the account holder", example = "Doe")
    private String lastName;
    @Schema(description = "Email linked to the account", example = "jane.doe@example.com")
    private String email;

    public static AccountResponse of(Long id, String firstName, String lastName, String email) {
        return new AccountResponse(id, firstName, lastName, email);
    }
}
