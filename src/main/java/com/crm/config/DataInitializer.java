package com.crm.config;
import com.crm.entity.User;
import com.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@crm.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);

            User manager = new User();
            manager.setFirstName("Manager");
            manager.setLastName("User");
            manager.setEmail("manager@crm.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRole(User.Role.MANAGER);
            userRepository.save(manager);

            User user = new User();
            user.setFirstName("Regular");
            user.setLastName("User");
            user.setEmail("user@crm.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(User.Role.USER);
            userRepository.save(user);
        }
    }
}
