package accounts.database;

import accounts.model.Role;
import accounts.model.RoleType;
import accounts.model.UserPersonalInfo;
import accounts.service.RoleService;
import accounts.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class DefaultDataInitializer {

    @Value("${accounts.database.DefaultDataInitializer.initData:false}")
    private boolean initData;
    @Value("${spring.jpa.hibernate.ddl-auto:create}")
    private String hibernateDdlAuto;
    @Value("${accounts.database.DefaultDataInitializer.appAdminUserEmail:admin@moon.io}")
    private String appAdminUserEmail;
    @Value("${accounts.database.DefaultDataInitializer.appAdminUserPassword:god!}")
    private String appAdminUserPassword;
    @Value("${accounts.database.DefaultDataInitializer.appAdminUserFirstName:God}")
    private String appAdminUserFirstName;
    @Value("${accounts.database.DefaultDataInitializer.appAdminUserLastName:}")
    private String appAdminUserLastName;

    final private UserService userService;
    final private RoleService roleService;

    @Autowired
    public DefaultDataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void init() {
        log.debug("Initializing the database, hibernateDdlAuto={}", hibernateDdlAuto);
        if ("create".equals(hibernateDdlAuto) || "create-drop".equals(hibernateDdlAuto)) {
            initData = true;
        }

        if (!initData) {
            log.debug("Skipping initializing default data");
            return;
        }

        log.debug("Adding the {} and {} roles", RoleType.ADMIN.name(), RoleType.USER.name());
        final var adminRole = addRole(RoleType.ADMIN.name());
        addRole(RoleType.USER.name());

        log.debug("Adding the {} user", appAdminUserEmail);
        final UserPersonalInfo godPersonalInfo = buildUserPersonalInfo(appAdminUserFirstName, appAdminUserLastName);
        addUser(appAdminUserEmail, appAdminUserPassword, godPersonalInfo, adminRole);
    }

    private Role addRole(String authority) {
        final var role = Role.builder()
                .authority(authority)
                .build();
        return roleService.save(role);
    }

    private UserPersonalInfo buildUserPersonalInfo(String firstName, String lastName) {
        return UserPersonalInfo.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    private void addUser(String email, String password, UserPersonalInfo personalInfo, Role role) {
        userService.createActivatedUser(email, password, personalInfo, role);
    }
}
