package accounts.controller;

import accounts.dto.request.UpdateUserPasswordRequestDTO;
import accounts.dto.request.UpdateUserPersonalInfoRequestDTO;
import accounts.dto.response.UserResponseDTO;
import accounts.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
@RequestMapping(UserController.PATH_USERS)
public class UserController {

    public static final String PATH_USERS = "/users";
    public static final String PATH_PERSONAL_INFO = "/personal-info";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Find all users")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful response",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}
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
            )
    })
    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CompletableFuture<List<UserResponseDTO>> findAll() {
        log.debug("Handling GET request on path => {}", PATH_USERS);
        return userService.findAll();
    }


    @Operation(summary = "Find user by id")
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
                    responseCode = "404",
                    description = "The user doesn't exist",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CompletableFuture<UserResponseDTO> findById(
            @Parameter(description = "User ID to find", required = true, example = "1") @PathVariable Long id) {
        log.debug("Handling GET request on path => {}/{}", PATH_USERS, id);
        final var userResponseDto = userService.findById(id);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }


    @Operation(summary = "Update user's password")
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
                    responseCode = "404",
                    description = "The user doesn't exist",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public CompletableFuture<UserResponseDTO> updatePasswordById(
            @Parameter(description = "User ID to update", required = true, example = "0") @PathVariable Long id,
            @Parameter(description = "User Details") @RequestBody UpdateUserPasswordRequestDTO updateUserPasswordRequestDTO) {
        log.debug("Handling PUT request on path => {}/{}. Request body => {}", PATH_USERS, id, updateUserPasswordRequestDTO);
        final var userResponseDto = userService.updatePasswordById(id, updateUserPasswordRequestDTO);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }


    @Operation(summary = "Update user's personal info")
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
                    responseCode = "404",
                    description = "The user doesn't exist",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @PatchMapping(value = "/{id}" + PATH_PERSONAL_INFO)
    @PreAuthorize("hasAuthority('USER')")
    public CompletableFuture<UserResponseDTO> updatePersonalInfo(
            @Parameter(description = "User ID to update", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "User Personal Information") @RequestBody UpdateUserPersonalInfoRequestDTO updateUserPersonalInfoRequestDto) {
        log.debug("Handling PUT request on path => {}/{}{}. Request body => {}", PATH_USERS, id, PATH_PERSONAL_INFO, updateUserPersonalInfoRequestDto);
        final var userResponseDto = userService.updatePersonalInfo(id, updateUserPersonalInfoRequestDto);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }


    @Operation(summary = "Delete user by id")
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
                    responseCode = "404",
                    description = "The user doesn't exist",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteById(
            @Parameter(description = "User ID to delete", required = true, example = "0") @PathVariable Long id) {
        log.debug("Handling DELETE request on path => {}/{}", PATH_USERS, id);
        userService.deleteById(id);
        log.debug("Response => {}", "OK");
    }
}