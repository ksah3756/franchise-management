package com.goorm.friendchise.domain.customer.domain;


import com.goorm.friendchise.domain.manager.exception.PasswordNotMatchException;
import com.goorm.friendchise.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;


import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Customer extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Builder.Default
    private Double movedDistance = 0.0;

    @ElementCollection
    @Builder.Default
    private Set<Achievement> achievements = new HashSet<>();

    public void updatePassword(String password) {this.password=password;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("USER"));
    }

    public void isPasswordMatch(String inputPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(inputPassword, this.password)) {
            throw new PasswordNotMatchException();
        }
    }

    public void plusMovedDistance(double movedDistance) {
        this.movedDistance+=movedDistance;
    }
}
