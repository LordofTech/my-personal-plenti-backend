package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing a permission for granular access control
 */
@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends Domain {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;
}
