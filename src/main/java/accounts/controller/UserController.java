package accounts.controller;

import accounts.config.SwaggerConfig;
import accounts.dto.request.UpdateUserPasswordRequestDTO;
import accounts.dto.request.UpdateUserPersonalInfoRequestDTO;
import accounts.dto.response.UserResponseDTO;
import accounts.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.SneakyThrows;
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
@Api(tags = SwaggerConfig.USERS_TAG)
public class UserController {

    public static final String PATH_USERS = "/users";
    public static final String PATH_PERSONAL_INFO = "/personal-info";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "${UserController.findAll}", response = UserResponseDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public CompletableFuture<List<UserResponseDTO>> findAll() {
        log.debug("Handling GET request on path => {}", PATH_USERS);
        return userService.findAll();
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "${UserController.findById}", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public CompletableFuture<UserResponseDTO> findById(@ApiParam(value = "User ID to find", required = true, example = "0") @PathVariable Long id) {
        log.debug("Handling GET request on path => {}/{}", PATH_USERS, id);
        final var userResponseDto = userService.findById(id);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }

    @SneakyThrows
    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ApiOperation(value = "${UserController.updatePasswordById}", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public CompletableFuture<UserResponseDTO> updatePasswordById(@ApiParam(value = "User ID to update", required = true, example = "0") @PathVariable Long id,
                                                                 @ApiParam("User Details") @RequestBody UpdateUserPasswordRequestDTO updateUserPasswordRequestDTO) {
        log.debug("Handling PUT request on path => {}/{}. Request body => {}", PATH_USERS, id, updateUserPasswordRequestDTO);
        final var userResponseDto = userService.updatePasswordById(id, updateUserPasswordRequestDTO);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }

    @SneakyThrows
    @PatchMapping(value = "/{id}" + PATH_PERSONAL_INFO)
    @PreAuthorize("hasAuthority('USER')")
    @ApiOperation(value = "${UserController.updatePersonalInfo}", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public CompletableFuture<UserResponseDTO> updatePersonalInfo(@ApiParam(value = "User ID to update", required = true, example = "0") @PathVariable Long id,
                                                                 @ApiParam("User Personal Information") @RequestBody UpdateUserPersonalInfoRequestDTO updateUserPersonalInfoRequestDto) {
        log.debug("Handling PUT request on path => {}/{}{}. Request body => {}", PATH_USERS, id, PATH_PERSONAL_INFO, updateUserPersonalInfoRequestDto);
        final var userResponseDto = userService.updatePersonalInfo(id, updateUserPersonalInfoRequestDto);
        log.debug("Response => {}", userResponseDto);
        return userResponseDto;
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "${UserController.deleteById}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public void deleteById(@ApiParam(value = "User ID to delete", required = true, example = "0") @PathVariable Long id) {
        log.debug("Handling DELETE request on path => {}/{}", PATH_USERS, id);
        userService.deleteById(id);
        log.debug("Response => {}", "OK");
    }
}