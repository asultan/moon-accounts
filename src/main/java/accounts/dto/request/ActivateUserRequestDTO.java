package accounts.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserRequestDTO {

    @NotBlank
    @ApiModelProperty
    private String token;

    @NotBlank
    @Size(min = 8, max = 255, message = "Minimum password length: 8 characters")
    @ApiModelProperty
    private String password;
}
