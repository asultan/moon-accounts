package accounts.controller;

import accounts.config.SwaggerConfig;
import accounts.dto.response.RoleResponseDTO;
import accounts.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = SwaggerConfig.ROLES_TAG)
public class RoleController {

    public static final String PATH_ROLES = "/roles";

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "${RoleController.findAll}", response = RoleResponseDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public List<RoleResponseDTO> findAll() {
        log.debug("Handling GET request on path: {}", PATH_ROLES);
        return roleService.findAll();
    }
}
