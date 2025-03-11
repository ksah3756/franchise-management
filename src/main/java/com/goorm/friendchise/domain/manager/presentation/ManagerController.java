package com.goorm.friendchise.domain.manager.presentation;

import com.goorm.friendchise.domain.manager.application.ManagerService;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.dto.request.ManageCreateRequest;
import com.goorm.friendchise.domain.manager.dto.request.ManageLoginRequest;
import com.goorm.friendchise.domain.manager.dto.request.ManagerPasswordRequest;
import com.goorm.friendchise.domain.manager.dto.response.ManagerDetailResponse;
import com.goorm.friendchise.domain.manager.dto.response.ManagerPersistResponse;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.application.TokenService;
import com.goorm.friendchise.global.auth.dto.request.TokenReissueRequest;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import com.goorm.friendchise.global.auth.resolver.AuthManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/manager")
@RestController
@RequiredArgsConstructor
public class ManagerController {
	private final ManagerService managerService;
	private final TokenService tokenService;

	@PostMapping("/register")
	public ResponseEntity<ManagerPersistResponse> register(
		@RequestBody @Valid ManageCreateRequest request
	) {
		return ResponseEntity.status(CREATED).body(managerService.create(request));
	}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(
		@RequestBody @Valid ManageLoginRequest request
	) {
		return ResponseEntity.ok(managerService.login(request));
	}

	@GetMapping("/mypage")
	public ResponseEntity<ManagerDetailResponse> mypage(
			@AuthManager Manager manager
			) {
		return ResponseEntity.ok(managerService.mypage(manager));
	}

	@Secured({"ROLE_HEADQUARTER", "ROLE_STORE"})
	@PutMapping("/update/store-id")
	public ResponseEntity<Void> update(
			@AuthManager Manager manager,
			@RequestParam Long newStoreId
	) {
		managerService.updateManager(manager, newStoreId);
		return ResponseEntity.noContent().build();
	}

	@Secured({"ROLE_HEADQUARTER", "ROLE_STORE"})
	@PutMapping("/update/password")
	public ResponseEntity<Void> updatePassword(
			@AuthManager Manager manager,
			@RequestBody @Valid ManagerPasswordRequest request
	) {
		managerService.updatePassword(manager, request.password());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> delete(@AuthManager Manager manager) {
		managerService.delete(manager);
		return ResponseEntity.noContent().build();
	}

}
