package accounts.service;

import accounts.exception.CustomException;
import accounts.model.User;
import accounts.model.VerificationToken;
import accounts.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class VerificationTokenService {

    @Value("${accounts.service.VerificationTokenService.activationAccountVerificationTokenValidityHours:72}")
    private Integer ACTIVATION_ACCOUNT_VERIFICATION_TOKEN_VALIDITY_HOURS;
    @Value("${accounts.service.VerificationTokenService.resetPasswordVerificationTokenValidityHours:3}")
    private Integer RESET_PASSWORD_VERIFICATION_TOKEN_VALIDITY_HOURS;

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken findById(@NotNull Long id) {
        log.debug("Finding verification token by id {}", id);

        final var optionalVerificationToken = verificationTokenRepository.findById(id);
        if (!optionalVerificationToken.isPresent()) {
            log.debug("Verification token with id {} was not found", id);
            throw new CustomException("Verification token with id " + id + " doesn't exist", HttpStatus.NOT_FOUND);
        }

        final var verificationToken = optionalVerificationToken.get();
        log.debug("Found verification token => {}", verificationToken);
        return verificationToken;
    }

    public VerificationToken findByToken(@NotEmpty String token) {
        log.debug("Finding verification token by token {}", token);

        final var optionalVerificationToken = verificationTokenRepository.findByToken(token);
        if (!optionalVerificationToken.isPresent()) {
            log.debug("Verification token was not found");
            throw new CustomException("Verification token " + token + " doesn't exist", HttpStatus.NOT_FOUND);
        }

        final var verificationToken = optionalVerificationToken.get();
        log.debug("Found verification token => {}", verificationToken);
        return verificationToken;
    }

    public VerificationToken save(@NotEmpty String token, @NotEmpty Date expiryDate, @NotEmpty User user) {
        log.debug("Saving verification token={}, expiryDate={}, userEmail={}", token, expiryDate.toString(), user.getEmail());

        final var verificationToken = VerificationToken.builder()
                .token(token)
                .expiryDate(expiryDate)
                .user(user)
                .build();

        final var result = verificationTokenRepository.save(verificationToken);
        log.debug("VerificationToken saved successfully => {} ", result.toString());
        return result;
    }

    public void delete(VerificationToken verificationToken) {
        log.debug("Deleting verification token => {}", verificationToken);
        verificationTokenRepository.delete(verificationToken);
    }

    public void deleteById(Long id) {
        log.debug("Deleting verification token with id {}", id);
        verificationTokenRepository.deleteById(id);
    }

    public void deleteByToken(String token) {
        log.debug("Deleting verification token {}", token);
        verificationTokenRepository.deleteByToken(token);
    }

    public VerificationToken createActivationAccountVerificationToken(@NotNull User user) {
        log.debug("Creating activation token for activating user => {}", user);
        return save(generateVerificationToken(), getExpiryDate(ACTIVATION_ACCOUNT_VERIFICATION_TOKEN_VALIDITY_HOURS), user);
    }

    private String generateVerificationToken() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }

    private Date getExpiryDate(Integer validityHours) {
        final var c = Calendar.getInstance();
        c.add(Calendar.HOUR, validityHours);
        return c.getTime();
    }

    public boolean isAccountActivationVerificationTokenValid(@NotNull VerificationToken verificationToken) {
        final var result = Calendar.getInstance().getTime().before(verificationToken.getExpiryDate());
        log.debug("Token is " + (result ? "not expired" : "expired") + " => {}", verificationToken);
        return result;
    }
}
