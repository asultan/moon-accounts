package accounts.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequestDTO {

    @NotBlank
    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;
}
