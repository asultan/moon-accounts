package accounts.dto.request;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty
    private String email;

    @ApiModelProperty
    private String firstName;

    @ApiModelProperty
    private String lastName;

    @ApiModelProperty
    private String phoneNumber;
}
