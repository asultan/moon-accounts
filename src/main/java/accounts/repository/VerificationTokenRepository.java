package accounts.repository;

import accounts.model.User;
import accounts.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

    @Modifying
    @Query(nativeQuery = true, value = "delete from verification_token where token = :token")
    void deleteByToken(@Param(value = "token") String token);
}
