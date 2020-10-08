package accounts.dto.response;

import accounts.model.Role;
import accounts.model.UserPersonalInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String email;

    private Boolean activated;

    private String jwt;

    private Role role;

    private UserPersonalInfo personalInfo;

    private String verificationToken;
}
