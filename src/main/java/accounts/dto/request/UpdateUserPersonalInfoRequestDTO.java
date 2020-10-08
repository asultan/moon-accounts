package accounts.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPersonalInfoRequestDTO {

    private String firstName;

    private String lastName;

    private String phoneNumber;
}
