package accounts.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Optional;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String SECURITY_TAG = "security";
    public static final String USERS_TAG = "users";
    public static final String ROLES_TAG = "roles";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/")))
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build()
                .apiInfo(metadata())
                .useDefaultResponseMessages(false)
                .tags(new Tag(SECURITY_TAG, "Security operations (register user, activate user, login, etc)"))
                .tags(new Tag(USERS_TAG, "Users related operations"))
                .tags(new Tag(ROLES_TAG, "Roles related operations"))
                .genericModelSubstitutes(Optional.class);

    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Accounts Service API")
                .description("This is the Accounts Service REST API. The service context is <b>/accounts</b>, so make sure that all REST requests start with that.")
                .version("1.0.0")
                .build();
    }

}
