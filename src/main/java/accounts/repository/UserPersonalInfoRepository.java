package accounts.repository;

import accounts.model.UserPersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPersonalInfoRepository extends JpaRepository<UserPersonalInfo, Long> {

}
