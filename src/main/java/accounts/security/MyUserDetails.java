package accounts.security;

import accounts.exception.CustomException;
import accounts.model.User;
import accounts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public MyUserDetails(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByEmail(username);
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        final User user;

        try {
            user = userService.findByEmail(email);
        } catch (CustomException e) {
            throw new UsernameNotFoundException("User " + email + " not found");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(user.getPassword())
                .authorities(user.getRole())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
