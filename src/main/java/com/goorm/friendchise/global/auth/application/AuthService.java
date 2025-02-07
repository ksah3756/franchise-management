package com.goorm.friendchise.global.auth.application;

import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.exception.ManagerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final ManagerRepository managerRepository;

	public Manager findManagerByAuth() {
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String username = ((UserDetails) principal).getUsername();
			return managerRepository.findByUsername(username)
				.orElseThrow(ManagerNotFoundException::new);
		} catch (Exception e) {
			throw new ManagerNotFoundException();
		}
	}
}
