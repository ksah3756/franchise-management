package com.goorm.core.user.domain;

import com.goorm.core.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private UserRole userRole;

    public static User create(String username, String encodedPassword, UserRole userRole) {
        return User.builder()
            .username(username)
            .password(encodedPassword)
            .userRole(userRole)
            .build();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}

