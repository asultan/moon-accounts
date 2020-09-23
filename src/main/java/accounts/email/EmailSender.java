package accounts.email;

import accounts.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSender {

    public void sendUserActivationEmail(User toUser, String userActivationUrl) {
        log.debug("Sending activation email to the user => {}. Activation URL is {}" , toUser, userActivationUrl);
        sendEmail(toUser, "Welcome to Moon", getUserActivationEmailContent(userActivationUrl));
    }

    private void sendEmail(User toUser, String subject, String content) {
        // send the actual email here
    }

    private String getUserActivationEmailContent(String url) {
        return new StringBuilder()
                .append("Your account is ready. Please click the following link to activate it and get started: ")
                .append(url)
                .toString();
    }
}
