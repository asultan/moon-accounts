package accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 256, message = "Email length: 1-256 characters")
    private String firstName;

    @Size(min = 1, max = 256, message = "Email length: 1-256 characters")
    private String lastName;

    @Size(min = 8, max = 15, message = "Email length: 8-15 characters")
    private String phoneNumber;
}
