package accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 256, message = "Email length: 3-256 characters")
    private String email;

    @Size(min = 8, message = "Minimum password length: 8 characters")
    private String password;

    @NotNull
    private Boolean activated;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    private Role role;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserPersonalInfo personalInfo;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private VerificationToken verificationToken;
}
