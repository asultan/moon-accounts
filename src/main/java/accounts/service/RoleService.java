package accounts.service;

import accounts.dto.response.RoleResponseDTO;
import accounts.exception.CustomException;
import accounts.model.Role;
import accounts.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    public List<RoleResponseDTO> findAll() {
        log.debug("Finding all roles");
        final var roles = roleRepository.findAll().stream()
                .map(role -> modelMapper.map(role, RoleResponseDTO.class))
                .collect(Collectors.toList());
        log.debug("{} roles haven been found", roles.size());
        return roles;
    }

    public Role findByAuthority(String authority) {
        log.debug("Finding role for authority {}", authority);
        final var role = roleRepository.findByAuthority(authority);
        if (role.isEmpty()) {
            log.warn("Role for authority {} cannot be found", authority);
            throw new CustomException("The role " + authority + " doesn't exist", HttpStatus.NOT_FOUND);
        }
        log.debug("Successfully found role => {}", role);
        return role.get();
    }


    public Role save(Role role) {
        log.debug("Saving role {}", role.getAuthority());
        if (!roleRepository.existsByAuthority(role.getAuthority())) {
            final var savedRole = roleRepository.save(role);
            log.debug("Role saved successfully => {}", role);
            return savedRole;
        } else {
            log.error("Role already exists => {}", role);
            throw new CustomException("Role  already exists", HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
