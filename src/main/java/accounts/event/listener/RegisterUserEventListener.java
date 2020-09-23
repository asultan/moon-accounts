package accounts.event.listener;

import accounts.email.EmailSender;
import accounts.event.RegisterUserEvent;
import accounts.model.User;
import accounts.model.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@Slf4j
public class RegisterUserEventListener implements ApplicationListener<RegisterUserEvent> {

    private final EmailSender emailSender;

    @Autowired
    public RegisterUserEventListener(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void onApplicationEvent(RegisterUserEvent event) {
        log.debug("Handling event => {}", event);
        final User user = event.getUser();
        final VerificationToken verificationToken = event.getVerificationToken();
        final String appUrl = event.getAppUrl();
        sendEmail(user, verificationToken, appUrl);
    }

    private void sendEmail(User user, VerificationToken verificationToken, String appUrl) {
        emailSender.sendUserActivationEmail(user, appUrl + "?data=" + getDataString(user, verificationToken));
    }

    private String getDataString(User user, VerificationToken verificationToken) {
        final String data = verificationToken.getToken() +
                "," +
                user.getEmail() +
                "," +
                user.getPersonalInfo().getFirstName();
        return Base64.getUrlEncoder().encodeToString(data.getBytes());
    }
}
