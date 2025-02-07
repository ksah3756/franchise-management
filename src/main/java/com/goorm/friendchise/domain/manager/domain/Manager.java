package com.goorm.friendchise.domain.manager.domain;

import com.goorm.friendchise.domain.manager.exception.PasswordNotMatchException;
import com.goorm.friendchise.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Manager extends BaseEntity implements UserDetails {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false)
	@Enumerated(value = STRING)
	private Role role;

	@Column(name = "manage_id")
	private Long manageId;

	public static Manager create(String username, String encodedPassword, Role role) {
		return Manager.builder()
			.username(username)
			.password(encodedPassword)
			.role(role)
			.build();
	}

	public void updateManageId(Long manageId) {
		this.manageId = manageId;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
	}

	public void isPasswordMatch(String inputPassword, PasswordEncoder passwordEncoder) {
		if (!passwordEncoder.matches(inputPassword, this.password)) {
			throw new PasswordNotMatchException();
		}
	}
}
