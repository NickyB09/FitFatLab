package com.fitfatlab.fitfatlab_backend.modules.user.repository;


import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(Role.RoleName name);

    boolean existsByName(Role.RoleName name);
}