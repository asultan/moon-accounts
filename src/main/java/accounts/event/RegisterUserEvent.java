package accounts.event;

import accounts.model.User;
import accounts.model.VerificationToken;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Data
@Getter
@Builder
public class RegisterUserEvent extends ApplicationEvent {

    private final String appUrl;
    private final User user;
    private final VerificationToken verificationToken;

    public RegisterUserEvent(String appUrl, User user, VerificationToken verificationToken) {
        super(user);

        this.appUrl = appUrl;
        this.user = user;
        this.verificationToken = verificationToken;
    }
}
