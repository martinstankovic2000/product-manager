package com.ingemark.productmanager.configuration;

import com.ingemark.productmanager.model.user.Role;
import com.ingemark.productmanager.model.user.User;
import com.ingemark.productmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * Application runner that initializes a default admin user at application startup based on configuration properties.
 * This component runs conditionally if the property "app.admin.create-on-startup" is true.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.admin.create-on-startup", havingValue = "true", matchIfMissing = true)
public class ConfigurableDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;


    /**
     * Called on application startup to create the admin user if it does not exist.
     *
     * @param args application arguments (ignored)
     */
    @Override
    public void run(ApplicationArguments args) {
        createAdminUser();
    }

    /**
     * Creates the admin user if not already present in the repository.
     */
    private void createAdminUser() {
        if (!userRepository.existsByUsername(adminConfig.getUsername())) {
            User admin = User.builder()
                    .username(adminConfig.getUsername())
                    .email(adminConfig.getEmail())
                    .password(passwordEncoder.encode(adminConfig.getPassword()))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
        }
    }
}
