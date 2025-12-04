package com.plenti.plentibackend.config;

import com.plenti.plentibackend.entity.Permission;
import com.plenti.plentibackend.entity.Role;
import com.plenti.plentibackend.repository.PermissionRepository;
import com.plenti.plentibackend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Initializer to create default roles and permissions on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing default roles and permissions...");

        // Create default permissions
        Permission readUsers = createPermissionIfNotExists("READ_USERS", "Read user information");
        Permission writeUsers = createPermissionIfNotExists("WRITE_USERS", "Create and update users");
        Permission readOrders = createPermissionIfNotExists("READ_ORDERS", "Read order information");
        Permission writeOrders = createPermissionIfNotExists("WRITE_ORDERS", "Create and update orders");
        Permission readProducts = createPermissionIfNotExists("READ_PRODUCTS", "Read product information");
        Permission writeProducts = createPermissionIfNotExists("WRITE_PRODUCTS", "Create and update products");
        Permission manageSystem = createPermissionIfNotExists("MANAGE_SYSTEM", "System administration");

        // Create default roles
        createRoleIfNotExists("USER", "Regular user", 
                Set.of(readProducts, readOrders));
        
        createRoleIfNotExists("RIDER", "Delivery rider", 
                Set.of(readProducts, readOrders, writeOrders));
        
        createRoleIfNotExists("ADMIN", "System administrator", 
                Set.of(readUsers, writeUsers, readOrders, writeOrders, 
                       readProducts, writeProducts, manageSystem));

        log.info("Default roles and permissions initialized successfully");
    }

    private Permission createPermissionIfNotExists(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    permission.setDescription(description);
                    Permission saved = permissionRepository.save(permission);
                    log.info("Created permission: {}", name);
                    return saved;
                });
    }

    private Role createRoleIfNotExists(String name, String description, Set<Permission> permissions) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setDescription(description);
                    role.setPermissions(new HashSet<>(permissions));
                    Role saved = roleRepository.save(role);
                    log.info("Created role: {}", name);
                    return saved;
                });
    }
}
