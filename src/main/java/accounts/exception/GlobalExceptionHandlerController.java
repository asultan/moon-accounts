package accounts.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {

    @Bean
    public ErrorAttributes errorAttributes() {
        // Hide accounts.exception field in the return object
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
                errorAttributes.remove("accounts/exception");
                return errorAttributes;
            }
        };
    }


    /***********************
     * AUTHENTICATION ERRORS
     ***********************/
    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(HttpServletResponse res, AuthenticationException ex) throws IOException {
        log.error("Invalid credentials", ex);
        res.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials");
    }

    @ExceptionHandler(DisabledException.class)
    public void handleAccountDisabledException(HttpServletResponse res, AuthenticationException ex) throws IOException {
        log.error("User not activated", ex);
        res.sendError(HttpStatus.UNAUTHORIZED.value(), "User not activated");
    }


    /**********************
     * AUTHORIZATION ERRORS
     **********************/
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(HttpServletResponse res, AccessDeniedException ex) throws IOException {
        log.error("Access denied: ", ex);
        res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
    }


    /****************************
     * CUSTOM & UNEXPECTED ERRORS
     ***************************/
    @ExceptionHandler(CustomException.class)
    public void handleCustomException(HttpServletResponse res, CustomException ex) throws IOException {
        log.error("Something went wrong: ", ex);
        res.sendError(ex.getHttpStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletResponse res, Exception ex) throws IOException {
        log.error("Something went wrong: ", ex);
        res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
    }

}
