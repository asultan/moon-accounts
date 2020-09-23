package accounts.dto.response;

import accounts.model.Role;
import accounts.model.UserPersonalInfo;
import accounts.model.VerificationToken;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    @ApiModelProperty
    private Long id;

    @ApiModelProperty
    private String email;

    @ApiModelProperty
    private Boolean activated;

    @ApiModelProperty
    private String jwt;

    @ApiModelProperty
    private Role role;

    @ApiModelProperty
    private UserPersonalInfo personalInfo;

    @ApiModelProperty
    private String verificationToken;
}
