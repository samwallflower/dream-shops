package com.andromeda.dreamshops.data;

import com.andromeda.dreamshops.model.Role;
import com.andromeda.dreamshops.model.User;
import com.andromeda.dreamshops.repository.RoleRepository;
import com.andromeda.dreamshops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    /**
     * This class is responsible for initializing data when the application is ready.
     * It implements ApplicationListener to listen for ApplicationReadyEvent.
     */

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        createDefaultUserIfNotExists();
        createDefaultRoleIfNotExists(defaultRoles);
        createDefaultAdminIfNotExists();
    }

    private void createDefaultUserIfNotExists() {
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        for(int i=1; i<=5; i++){
            String defaultEmail = "user" + i + "@email.com";
            if (userRepository.existsByEmail(defaultEmail))
                continue;

            User user = new User();
            user.setFirstName("The User");
            user.setLastName("User " + i);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            System.out.println("Default user created: " + i + " created successfully with email: " + defaultEmail);

        }
    }

    private void createDefaultAdminIfNotExists() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();

        for(int i=1; i<=2; i++){
            String defaultEmail = "admin" + i + "@email.com";
            if (userRepository.existsByEmail(defaultEmail))
                continue;

            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin " + i);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
            System.out.println("Default admin created: " + i + " created successfully with email: " + defaultEmail);

        }
    }

    private void createDefaultRoleIfNotExists(Set<String> roles){
        roles.stream()
                .filter(role-> roleRepository.findByName(role).isEmpty())
                .map(Role :: new).forEach(roleRepository::save);
    }
}
