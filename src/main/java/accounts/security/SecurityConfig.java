package accounts.security;

import accounts.security.jwt.JwtFilterConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static accounts.controller.SecurityController.PATH_ACTIVATE_USER;
import static accounts.controller.SecurityController.PATH_LOGIN;
import static accounts.controller.SecurityController.PATH_REGISTER_USER;
import static accounts.controller.SecurityController.PATH_SECURITY;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilterConfigurer jwtFilterConfigurer;

    @Autowired
    public SecurityConfig(JwtFilterConfigurer jwtFilterConfigurer) {
        this.jwtFilterConfigurer = jwtFilterConfigurer;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF (Cross Site Request Forgery)
        http.csrf().disable();

        // No sessions will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Entry points
        http.authorizeRequests()
                // some of the security requests (login, register, activate) must skip authentication
                .antMatchers(PATH_SECURITY + PATH_LOGIN).permitAll()
                .antMatchers(PATH_SECURITY + PATH_REGISTER_USER).permitAll()
                .antMatchers(PATH_SECURITY + PATH_ACTIVATE_USER).permitAll()

                // actuator requests don't need authentication for now
                .antMatchers("/status/health").permitAll()

                // Everything else requires authentication
                .anyRequest().authenticated();

        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        // Apply JWT
        http.apply(jwtFilterConfigurer);
    }

    @Override
    public void configure(WebSecurity web) {
        // Allow swagger to be accessed without authentication
        web.ignoring()
                .antMatchers("/")
                .antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**")
                .antMatchers("/accounts/configuration/**")
                .antMatchers("/webjars/**")
                .antMatchers("/public")
                .antMatchers("/csrf")
                .antMatchers("/error");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
