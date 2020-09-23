package accounts.controller;

import accounts.config.SwaggerConfig;
import accounts.dto.request.ActivateUserRequestDTO;
import accounts.dto.request.LoginRequestDTO;
import accounts.dto.request.RegisterUserRequestDTO;
import accounts.dto.response.UserResponseDTO;
import accounts.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping(SecurityController.PATH_SECURITY)
@Api(tags = SwaggerConfig.SECURITY_TAG)
public class SecurityController {

    public static final String PATH_SECURITY = "/security";
    public static final String PATH_LOGIN = "/login";
    public static final String PATH_REGISTER_USER = "/register-user";
    public static final String PATH_ACTIVATE_USER = "/activate-user";
    public static final String PATH_WHOAMI = "/whoami";
    public static final String PATH_REFRESH = "/refresh";

    private final SecurityService securityService;
    private final ModelMapper modelMapper;

    @Autowired
    public SecurityController(SecurityService securityService, ModelMapper modelMapper) {
        this.securityService = securityService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(PATH_LOGIN)
    @ApiOperation(value = "${SecurityController.login}", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 401, message = "User not activated"),
            @ApiResponse(code = 500, message = "Something went wrong")
    })
    public UserResponseDTO login(@ApiParam("Login User Credentials") @RequestBody LoginRequestDTO loginRequestDto) {
        log.debug("Handling POST request on path => {}{}. Request body => {}", PATH_SECURITY, PATH_LOGIN, loginRequestDto);
        final var userResponseDto = securityService.login(loginRequestDto);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }

    @PostMapping(PATH_REGISTER_USER)
    @ApiOperation(value = "${SecurityController.registerUser}", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Username is already in use"),
            @ApiResponse(code = 500, message = "Something went wrong")
    })
    public UserResponseDTO registerUser(@ApiParam("Register User Information") @RequestBody RegisterUserRequestDTO registerUserRequestDto) {
        log.debug("Handling POST request on path => {}{}. Request body => {}", PATH_SECURITY, PATH_REGISTER_USER, registerUserRequestDto);
        final var userResponseDto = securityService.registerUser(registerUserRequestDto);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }

    @PostMapping(PATH_ACTIVATE_USER)
    @ApiOperation(value = "${SecurityController.activateUser}", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Expired or invalid verification token"),
            @ApiResponse(code = 500, message = "Something went wrong")
    })
    public UserResponseDTO activateUser(@ApiParam("User Activation Information") @RequestBody ActivateUserRequestDTO activateUserRequestDto) {
        log.debug("Handling POST request on path => {}{}. Request body => {}", PATH_SECURITY, PATH_ACTIVATE_USER, activateUserRequestDto);
        final var activateUserResponseDTO = securityService.activateUser(activateUserRequestDto);
        final var loginUserResponseDto = securityService.login(LoginRequestDTO.builder()
                .email(activateUserResponseDTO.getEmail())
                .password(activateUserRequestDto.getPassword())
                .build());
        log.debug("Response => {}", loginUserResponseDto);
        return loginUserResponseDto;
    }

    @GetMapping(PATH_WHOAMI)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiOperation(value = "${SecurityController.whoami}", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Something went wrong"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")
    })
    public UserResponseDTO whoami(HttpServletRequest request) {
        log.debug("Handling GET request on path => {}{}", PATH_SECURITY, PATH_WHOAMI);
        final var userResponseDto = modelMapper.map(securityService.whoami(request), UserResponseDTO.class);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }

    @PostMapping(PATH_REFRESH)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiOperation(value = "${SecurityController.refresh}")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Something went wrong"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")
    })
    public String refresh(HttpServletRequest request) {
        log.debug("Handling GET request on path => {}{}", PATH_SECURITY, PATH_REFRESH);
        final var jwt = securityService.refresh(request.getRemoteUser());
        log.debug("Response => {}", jwt);
        return jwt;
    }
}
