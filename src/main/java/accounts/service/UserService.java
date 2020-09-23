package accounts.service;

import accounts.dto.request.UpdateUserPersonalInfoRequestDTO;
import accounts.dto.request.UpdateUserPasswordRequestDTO;
import accounts.dto.response.UserResponseDTO;
import accounts.exception.CustomException;
import accounts.model.Role;
import accounts.model.User;
import accounts.model.UserPersonalInfo;
import accounts.repository.UserRepository;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public List<UserResponseDTO> findAll(Boolean fetchPersonalInfo) {
        final var fetchPersonalInfoNotNull = Boolean.TRUE.equals(fetchPersonalInfo); // trick to transform fetchPersonalInfo null to false

        log.debug("Finding all users, fetchPersonalInfo={}", fetchPersonalInfoNotNull);

        final var users = userRepository.findAll().stream()
                .map(user -> mapUserToUserResponseDTO(user, fetchPersonalInfoNotNull))
                .collect(Collectors.toList());

        log.debug("Found {} users", users.size());
        return users;
    }

    private UserResponseDTO mapUserToUserResponseDTO(User user, Boolean fetchPersonalInfo) {
        final var userResponseDto = UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .activated(user.getActivated())
                .role(user.getRole())
                .build();
        if (fetchPersonalInfo) {
            userResponseDto.setPersonalInfo(user.getPersonalInfo());
        }
        return userResponseDto;
    }

    public UserResponseDTO findById(Long id) {
        log.debug("Finding user by id {}", id);

        final var user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("No user found for id {}", id);
            throw new CustomException("The user with id " + id + " doesn't exist", HttpStatus.NOT_FOUND);
        }

        log.debug("Found user => {}", user.get().toString());
        return modelMapper.map(user.get(), UserResponseDTO.class);
    }

    public User findByEmail(String email) {
        log.debug("Finding user by email {}", email);

        final var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("No user found for email {}", email);
            throw new CustomException("The user " + email + " doesn't exist", HttpStatus.NOT_FOUND);
        }

        log.debug("Found user => {}", user.get().toString());
        return user.get();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User activateUser(@NotNull User user, @NotEmpty String password) {
        log.debug("Activating user => {}", user);

        user.setPassword(passwordEncoder.encode(password));
        user.setActivated(true);

        log.debug("Saving user => {}", user);
        return userRepository.save(user);
    }

    public User createInactiveUser(@NotEmpty String email, @NotEmpty UserPersonalInfo personalInfo, @NotEmpty Role role) {
        log.debug("Creating inactive user with email={}, personalInfo={}, role={}", email, personalInfo, role);

        final var user = User.builder()
                .email(email)
                .activated(false)
                .role(role)
                .personalInfo(personalInfo)
                .build();

        log.debug("Saving user => {}", user);
        return userRepository.save(user);
    }

    public User createActivatedUser(@NotEmpty String email, @NotEmpty String password, @NotNull UserPersonalInfo personalInfo, @NotEmpty Role role) {
        log.debug("Creating activated user with email={}, personalInfo={}, role={}", email, personalInfo, role);

        final var user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .activated(true)
                .role(role)
                .personalInfo(personalInfo)
                .build();

        log.debug("Saving user => {}", user);
        return userRepository.save(user);
    }

    public UserResponseDTO updatePasswordById(Long id, UpdateUserPasswordRequestDTO updateUserRequestDTO) {
        log.debug("Updating user's password by user id {}", id);

        final var optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            log.error("User having id {} wasn't found", id);
            throw new CustomException("The user with id" + id + " doesn't exist", HttpStatus.NOT_FOUND);
        }

        var user = optionalUser.get();
        if (!Strings.isNullOrEmpty(updateUserRequestDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateUserRequestDTO.getPassword()));
        }

        log.debug("Saving user => {}", user);
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO updatePersonalInfo(Long id, UpdateUserPersonalInfoRequestDTO updateUserPersonalInfoRequestDto) {
        log.debug("Updating personal info of user with id {}", id);

        final var user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("User having id {} wasn't found", id);
            throw new CustomException("The user id" + id + " doesn't exist", HttpStatus.NOT_FOUND);
        }

        final var personalInfo = user.get().getPersonalInfo();
        if (!StringUtils.isEmpty(updateUserPersonalInfoRequestDto.getFirstName())) {
            personalInfo.setFirstName(updateUserPersonalInfoRequestDto.getFirstName());
        }

        if (!StringUtils.isEmpty(updateUserPersonalInfoRequestDto.getLastName())) {
            personalInfo.setLastName(updateUserPersonalInfoRequestDto.getLastName());
        }

        if (!StringUtils.isEmpty(updateUserPersonalInfoRequestDto.getPhoneNumber())) {
            personalInfo.setPhoneNumber(updateUserPersonalInfoRequestDto.getPhoneNumber());
        }

        log.debug("Saving user => {}", user);
        userRepository.save(user.get());
        return modelMapper.map(user.get(), UserResponseDTO.class);
    }

    public void deleteById(Long id) {
        log.debug("Deleting user with id {}", id);

        userRepository.deleteById(id);
    }
}
