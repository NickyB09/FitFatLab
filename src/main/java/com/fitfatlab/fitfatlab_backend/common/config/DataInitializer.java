package com.fitfatlab.fitfatlab_backend.common.config;

import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.RoleRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${fitfatlab.bootstrap.seed-admin.enabled:false}")
    private boolean seedAdminEnabled;

    @Value("${fitfatlab.bootstrap.seed-admin.email:admin@fitfatlab.com}")
    private String adminEmail;

    @Value("${fitfatlab.bootstrap.seed-admin.password:ChangeMe123!}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensureRolesExist();
        createDefaultAdminIfMissing();
    }

    private void ensureRolesExist() {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                log.info("Role created: {}", roleName);
            }
        }
    }

    private void createDefaultAdminIfMissing() {
        if (!seedAdminEnabled) {
            log.info("Default admin seed disabled — skipping");
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Default admin already exists — skipping seed");
            return;
        }

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found even after creation attempt"));

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found even after creation attempt"));

        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setFullName("FitFatLab Administrator");
        admin.setEnabled(true);
        admin.setRoles(Set.of(adminRole, userRole));

        userRepository.save(admin);
        log.info("Default admin created: {}", adminEmail);
    }
}
