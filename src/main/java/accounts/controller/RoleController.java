package accounts.controller;

import accounts.dto.response.RoleResponseDTO;
import accounts.dto.response.UserResponseDTO;
import accounts.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(RoleController.PATH_ROLES)
public class RoleController {

    public static final String PATH_ROLES = "/roles";

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Find all roles")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RoleResponseDTO.class)))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}

            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}
            )
    })
    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<RoleResponseDTO> findAll() {
        log.debug("Handling GET request on path: {}", PATH_ROLES);
        return roleService.findAll();
    }
}
