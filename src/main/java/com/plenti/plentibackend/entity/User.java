package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entity representing a user in the Plenti system
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles"})
@NoArgsConstructor
@AllArgsConstructor
public class User extends Domain implements UserDetails {

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(unique = true)
    private String referralCode;

    @Column(nullable = false)
    private Double metaCoins = 0.0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean suspended = false;

    @Column(nullable = false)
    private Double trustScore = 100.0;

    @Column(nullable = false)
    private Boolean isGuest = false;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(nullable = false)
    private Boolean accountLocked = false;

    private LocalDateTime lockTime;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_payment_methods", 
                    joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "payment_type")
    @Column(name = "payment_details")
    private Map<String, String> paymentMethods = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        if (metaCoins == null) {
            metaCoins = 0.0;
        }
        if (suspended == null) {
            suspended = false;
        }
        if (trustScore == null) {
            trustScore = 100.0;
        }
        if (isGuest == null) {
            isGuest = false;
        }
        if (enabled == null) {
            enabled = false;
        }
        if (failedLoginAttempts == null) {
            failedLoginAttempts = 0;
        }
        if (accountLocked == null) {
            accountLocked = false;
        }
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled && !suspended;
    }
}
