package accounts.controller;

import accounts.dto.request.ActivateUserRequestDTO;
import accounts.dto.request.LoginRequestDTO;
import accounts.dto.request.RegisterUserRequestDTO;
import accounts.dto.response.UserResponseDTO;
import accounts.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(summary = "Authenticate user and return corresponding JWT")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials or jwt token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not activated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping(PATH_LOGIN)
    public UserResponseDTO login(
            @Parameter(description = "Login User Credentials") @RequestBody LoginRequestDTO loginRequestDto) {
        log.debug("Handling POST request on path => {}{}. Request body => {}", PATH_SECURITY, PATH_LOGIN, loginRequestDto);
        final var userResponseDto = securityService.login(loginRequestDto);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }


    @Operation(summary = "Create user and returns its JWT")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "Username is already in use",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping(PATH_REGISTER_USER)
    public UserResponseDTO registerUser(
            @Parameter(description = "Register User Information") @RequestBody RegisterUserRequestDTO registerUserRequestDto) {
        log.debug("Handling POST request on path => {}{}. Request body => {}", PATH_SECURITY, PATH_REGISTER_USER, registerUserRequestDto);
        final var userResponseDto = securityService.registerUser(registerUserRequestDto);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }


    @Operation(summary = "Activate an user and set password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "Expired or invalid verification token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping(PATH_ACTIVATE_USER)
    public UserResponseDTO activateUser(@Parameter(description = "User Activation Information") @RequestBody ActivateUserRequestDTO activateUserRequestDto) {
        log.debug("Handling POST request on path => {}{}. Request body => {}", PATH_SECURITY, PATH_ACTIVATE_USER, activateUserRequestDto);
        final var activateUserResponseDTO = securityService.activateUser(activateUserRequestDto);
        final var loginUserResponseDto = securityService.login(LoginRequestDTO.builder()
                .email(activateUserResponseDTO.getEmail())
                .password(activateUserRequestDto.getPassword())
                .build());
        log.debug("Response => {}", loginUserResponseDto);
        return loginUserResponseDto;
    }


    @Operation(summary = "Retrieve current user's data based on request JWT")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials or jwt token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Expired or invalid JWT token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @GetMapping(PATH_WHOAMI)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public UserResponseDTO whoami(HttpServletRequest request) {
        log.debug("Handling GET request on path => {}{}", PATH_SECURITY, PATH_WHOAMI);
        final var userResponseDto = modelMapper.map(securityService.whoami(request), UserResponseDTO.class);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }


    @Operation(summary = "Refresh the current user's JWT")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials or jwt token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Expired or invalid JWT token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping(PATH_REFRESH)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public String refresh(HttpServletRequest request) {
        log.debug("Handling GET request on path => {}{}", PATH_SECURITY, PATH_REFRESH);
        final var jwt = securityService.refresh(request.getRemoteUser());
        log.debug("Response => {}", jwt);
        return jwt;
    }
}
