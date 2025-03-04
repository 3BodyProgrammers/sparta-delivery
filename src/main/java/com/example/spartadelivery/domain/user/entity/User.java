package com.example.spartadelivery.domain.user.entity;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.entity.BaseEntity;
import com.example.spartadelivery.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    public User(String email, String password, String name, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userRole = userRole;
    }

    private User(Long id, String email, String name, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId(), authUser.getEmail(), authUser.getName(), authUser.getUserRole());
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
