package accounts.repository;

import accounts.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByAuthority(String authority);

    Optional<Role> findByAuthority(String authority);
}
