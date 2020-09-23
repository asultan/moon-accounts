package accounts.security.jwt;

import accounts.exception.CustomException;
import accounts.model.Role;
import accounts.security.MyUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    @Value("${accounts.security.jwt.JwtTokenProvider.secretKey:Aqe4Y83g4InDmfJ4QEUbhERduWHTM9vX}")
    private String secretKey;
    @Value("${accounts.security.jwt.JwtTokenProvider.validityMillis:3600000}")
    private long validityMillis;

    private final MyUserDetails myUserDetails;

    @Lazy
    @Autowired
    public JwtProvider(MyUserDetails myUserDetails) {
        this.myUserDetails = myUserDetails;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createJwt(String email, Role role) {
        log.debug("Creating JWT for email {} and role {}", email, role.getAuthority());
        final var claims = Jwts.claims().setSubject(email);
        claims.put("auth", new SimpleGrantedAuthority(role.getAuthority()));

        final var now = new Date();
        final var validUntil = new Date(now.getTime() + validityMillis);
        final var jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validUntil)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        log.debug("JWT has been created successfully => {}", jwt);
        return jwt;
    }

    public Authentication getAuthentication(String jwt) {
        final var userDetails = myUserDetails.loadUserByUsername(getEmail(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String jwt) {
        final var email = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody().getSubject();
        log.debug("Extracted email = {} from JWT = {}", email, jwt);
        return email;
    }

    public String resolveJwt(HttpServletRequest req) {
        final var bearerToken = req.getHeader("Authorization");
        if (StringUtils.isEmpty(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            log.error("Invalid Authorization header / bearer token => {}", bearerToken);
            return null;
        }
        return bearerToken.substring(7);
    }

    public boolean validateJwt(String token) {
        log.debug("Validating JWT => {}", token);
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT {} is invalid or expired", token);
            throw new CustomException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }
}
