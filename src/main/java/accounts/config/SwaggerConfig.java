package accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Optional;
import java.util.function.Predicate;

@Configuration
//@EnableSwagger2
public class SwaggerConfig {

    public static final String SECURITY_TAG = "security";
    public static final String USERS_TAG = "users";
    public static final String ROLES_TAG = "roles";
}
