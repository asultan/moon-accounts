package accounts.service;

import accounts.dto.request.ActivateUserRequestDTO;
import accounts.dto.request.LoginRequestDTO;
import accounts.dto.request.RegisterUserRequestDTO;
import accounts.dto.response.UserResponseDTO;
import accounts.event.RegisterUserEvent;
import accounts.exception.CustomException;
import accounts.model.RoleType;
import accounts.model.User;
import accounts.model.UserPersonalInfo;
import accounts.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@Transactional
@Slf4j
public class SecurityService {

    private final UserService userService;
    private final RoleService roleService;
    private final VerificationTokenService verificationTokenService;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SecurityService(UserService userService, RoleService roleService, VerificationTokenService verificationTokenService, ModelMapper modelMapper,
                           JwtProvider jwtProvider, AuthenticationManager authenticationManager, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.roleService = roleService;
        this.verificationTokenService = verificationTokenService;
        this.modelMapper = modelMapper;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.eventPublisher = eventPublisher;
    }

    public UserResponseDTO login(LoginRequestDTO loginRequestDto) {
        final var email = loginRequestDto.getEmail();
        final var password = loginRequestDto.getPassword();

        log.debug("Authenticating user with email {}", email);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        final var user = userService.findByEmail(email);

        if (!user.getActivated()) {
            log.error("User {} is not activated", user.getEmail());
            throw new DisabledException("User not activated");
        }

        final var userResponseDto = modelMapper.map(user, UserResponseDTO.class);
        userResponseDto.setJwt(jwtProvider.createJwt(email, user.getRole()));
        return userResponseDto;
    }

    public UserResponseDTO registerUser(RegisterUserRequestDTO registerUserRequestDto) {
        final var email = registerUserRequestDto.getEmail();
        if (userService.existsByEmail(email)) {
            log.error("An user with email {} doesn't exist", email);
            throw new CustomException("Email is already in use", HttpStatus.NOT_ACCEPTABLE);
        }

        final var userRole = roleService.findByAuthority(RoleType.USER.name());
        final var personalInfo = UserPersonalInfo.builder()
                .firstName(registerUserRequestDto.getFirstName())
                .lastName(registerUserRequestDto.getLastName())
                .phoneNumber(registerUserRequestDto.getPhoneNumber())
                .build();

        final var user = userService.createInactiveUser(email, personalInfo, userRole);
        final var verificationToken = verificationTokenService.createActivationAccountVerificationToken(user);

        final var event = RegisterUserEvent.builder()
                .user(user)
                .verificationToken(verificationToken)
                .appUrl("appBaseUrl/activate-user") // TODO: build full url
                .build();
        log.debug("Publishing event => {}", event);
        eventPublisher.publishEvent(event);

        final var userResponseDto = modelMapper.map(user, UserResponseDTO.class);
        userResponseDto.setVerificationToken(verificationToken.getToken());
        userResponseDto.setJwt(jwtProvider.createJwt(email, userRole));
        return userResponseDto;
    }

    public UserResponseDTO activateUser(ActivateUserRequestDTO activateUserRequestDto) {
        final var token = activateUserRequestDto.getToken();
        var user = verifyToken(token);
        user = userService.activateUser(user, activateUserRequestDto.getPassword());
        verificationTokenService.deleteByToken(activateUserRequestDto.getToken());
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public void setPassword(ActivateUserRequestDTO activateUserRequestDto) {
        final var token = activateUserRequestDto.getToken();
        final var verificationToken = verificationTokenService.findByToken(token);
        if (verificationTokenService.isAccountActivationVerificationTokenValid(verificationToken)) {
            final var user = verificationToken.getUser();
            userService.activateUser(user, activateUserRequestDto.getPassword());
        }
    }

    private User verifyToken(String token) {
        log.debug("Validating verification token {}", token);
        final var verificationToken = verificationTokenService.findByToken(token);
        final var user = verificationToken.getUser();
        if (!verificationTokenService.isAccountActivationVerificationTokenValid(verificationToken)) {
            log.error("Token {} is invalid or expired", token);
            throw new CustomException("Invalid or expired token", HttpStatus.NOT_ACCEPTABLE);
        }
        return user;
    }

    public UserResponseDTO whoami(HttpServletRequest req) {
        return whoami(jwtProvider.resolveJwt(req));
    }

    public UserResponseDTO whoami(String token) {
        final var email = jwtProvider.getEmail(token);
        final var user = userService.findByEmail(email);
        final var userResponseDto = modelMapper.map(user, UserResponseDTO.class);
        userResponseDto.setJwt(token);
        return userResponseDto;
    }

    public String refresh(String email) {
        final var userRole = userService.findByEmail(email).getRole();
        return jwtProvider.createJwt(email, userRole);
    }
}