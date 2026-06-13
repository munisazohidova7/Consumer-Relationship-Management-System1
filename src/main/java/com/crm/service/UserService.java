package com.crm.service;

import com.crm.entity.User;
import com.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        if (!user.isActive()) throw new DisabledException("User is disabled");
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .build();
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user) { return userRepository.save(user); }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }

    public List<User> findAll() { return userRepository.findAll(); }

    public List<User> findByRole(User.Role role) { return userRepository.findByRole(role); }

    public long countUsers() { return userRepository.count(); }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    public void initAdminUser() {
        if (!userRepository.existsByEmail("admin@crm.com")) {
            userRepository.save(User.builder().email("admin@crm.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin").lastName("User")
                    .role(User.Role.ADMIN).active(true).build());
        }
        if (!userRepository.existsByEmail("manager@crm.com")) {
            userRepository.save(User.builder().email("manager@crm.com")
                    .password(passwordEncoder.encode("manager123"))
                    .firstName("Manager").lastName("User")
                    .role(User.Role.MANAGER).active(true).build());
        }
        if (!userRepository.existsByEmail("user@crm.com")) {
            userRepository.save(User.builder().email("user@crm.com")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("Regular").lastName("User")
                    .role(User.Role.USER).active(true).build());
        }
    }
}
